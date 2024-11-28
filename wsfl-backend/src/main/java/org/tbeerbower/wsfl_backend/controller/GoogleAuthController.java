package org.tbeerbower.wsfl_backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tbeerbower.wsfl_backend.model.User;
import org.tbeerbower.wsfl_backend.repository.UserRepository;
import org.tbeerbower.wsfl_backend.security.JwtUtil;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Set;


@Tag(name = "Google Authentication", description = "Google OAuth management APIs")
@RestController
@PreAuthorize("permitAll()")
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api/auth/google")
public class GoogleAuthController {
    private static final String GOOGLE_ISSUER = "https://accounts.google.com";
    public static final String GOOGLE_APIS_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_APIS_OAUTH_CERTS_URL = "https://www.googleapis.com/oauth2/v3/certs";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public GoogleAuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Login Google user",
            description = "Authenticates a Google user and returns a JWT token"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully authenticated"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Invalid ID token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Invalid ID token"))
            )
    })
    @PostMapping
    public ResponseEntity<?> handleGoogleAuth(@RequestBody GoogleCode body, HttpServletResponse response) {
        try {

            // Exchange the authorization code for tokens
            GoogleTokenRequest tokenRequest = new GoogleTokenRequest(
                    clientId, clientSecret, body.code(),
                    redirectUri, "authorization_code");

            GoogleTokenResponse tokenResponse = restTemplate.exchange(
                    GOOGLE_APIS_TOKEN_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(tokenRequest),
                    GoogleTokenResponse.class
            ).getBody();

            try {
                String idToken = tokenResponse.id_token;
                // Step 2: Verify ID token
                User user = verifyIdToken(idToken);
                // Step 3: Create JWT
                String jwt = jwtUtil.generateToken(user);
                // Step 4: Return JWT to frontend
                GoogleAuthResponse authResponse = new GoogleAuthResponse(jwt, user);
                return ResponseEntity.ok().body(authResponse);

            } catch (JWTVerificationException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID token: " + e.getMessage());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    public User verifyIdToken(String idToken) throws JWTVerificationException {
        // Decode the token without verification first to get the key ID
        DecodedJWT unverifiedJwt = JWT.decode(idToken);
        String keyId = unverifiedJwt.getKeyId();

        // Fetch Google's public keys
        RSAPublicKey publicKey = retrieveGooglePublicKey(keyId);

        if (publicKey == null) {
            throw new JWTVerificationException("Matching public key not found");
        }

        // Verify the token
        Algorithm algorithm = Algorithm.RSA256(publicKey, null);
        Verification verification = JWT.require(algorithm)
                .withIssuer(GOOGLE_ISSUER)
                .withAudience(clientId)
                .acceptLeeway(5); // 5 seconds leeway for clock skew

        DecodedJWT jwt = verification.build().verify(idToken);

        String email = jwt.getClaim("email").asString();
        String name = jwt.getClaim("name").asString();
        String picture = jwt.getClaim("picture").asString();

        // Check if user exists
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Create user
            user = new User(email, name, picture, "N/A", Set.of("ROLE_USER"));
            userRepository.save(user);
        }
        return user;
    }

    private RSAPublicKey retrieveGooglePublicKey(String keyId) throws JWTVerificationException {
        JwksResponse jwksResponse = restTemplate.getForObject(
                GOOGLE_APIS_OAUTH_CERTS_URL,
                JwksResponse.class
        );

        return jwksResponse.findPublicKeyById(keyId);
    }

    public record GoogleCode(String code) {
    }

    private record GoogleTokenRequest(String client_id, String client_secret,
                                      String code, String redirect_uri, String grant_type) {
    }

    private record GoogleTokenResponse(String access_token, String id_token) {
    }

    private record GoogleAuthResponse(String jwt, User user) {
    }

    public record JwksResponse(List<JwksKey> keys) {
        public record JwksKey(String kid, String n, String e) {
        }

        public RSAPublicKey findPublicKeyById(String keyId) throws JWTVerificationException {
            return keys.stream()
                    .filter(key -> key.kid().equals(keyId))
                    .findFirst()
                    .map(key -> {
                        try {
                            return parsePublicKey(key.n(), key.e());
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            throw new JWTVerificationException("Error parsing public key: " + e.getMessage(), e);
                        }
                    })
                    .orElseThrow(() -> new JWTVerificationException("Key not found"));
        }

        private RSAPublicKey parsePublicKey(String modulus, String exponent)
                throws NoSuchAlgorithmException, InvalidKeySpecException {
            byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
            byte[] eBytes = Base64.getUrlDecoder().decode(exponent);

            java.math.BigInteger n = new java.math.BigInteger(1, nBytes);
            java.math.BigInteger e = new java.math.BigInteger(1, eBytes);

            java.security.spec.RSAPublicKeySpec spec = new java.security.spec.RSAPublicKeySpec(n, e);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) factory.generatePublic(spec);
        }
    }
}
