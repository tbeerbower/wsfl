package org.tbeerbower.wsfl_backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.tbeerbower.wsfl_backend.api.WsflResponse;

import java.util.List;

public abstract class BaseController {

    protected <T> ResponseEntity<WsflResponse<T>> ok(T data) {
        return ResponseEntity.ok(new WsflResponse<>(data));
    }

    protected <T> ResponseEntity<WsflResponse<T>> ok(T data, List<Link> links) {
        WsflResponse<T> response = new WsflResponse<>(data);
        response.setLinks(links);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity<WsflResponse<Page<T>>> ok(Page<T> page) {
        WsflResponse<Page<T>> response = new WsflResponse<>(page);
        response.setMeta(new WsflResponse.Meta(
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize()
        ));
        return ResponseEntity.ok(response);
    }

    protected ResponseEntity<WsflResponse<Void>> noContent() {
        return ResponseEntity.noContent().build();
    }

    protected ResponseEntity<WsflResponse<Void>> badRequest(String message) {
        return ResponseEntity.badRequest().body(new WsflResponse<>(null, List.of(message)));
    }

    protected ResponseEntity<WsflResponse<Void>> notFound(String message) {
        return ResponseEntity.status(404).body(new WsflResponse<>(null, List.of(message)));
    }

    protected <T> ResponseEntity<WsflResponse<T>> created(T data) {
        return ResponseEntity.status(201).body(new WsflResponse<>(data));
    }
}
