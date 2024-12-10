--
-- PostgreSQL database dump
--

-- Dumped from database version 10.6
-- Dumped by pg_dump version 10.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: draft_order; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.draft_order (
    "position" integer NOT NULL,
    draft_id bigint NOT NULL,
    team_id bigint NOT NULL
);


ALTER TABLE public.draft_order OWNER TO root;

--
-- Name: draft_picks; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.draft_picks (
    pick_number integer,
    round integer,
    draft_id bigint,
    id bigint NOT NULL,
    pick_time timestamp(6) without time zone,
    runner_id bigint,
    team_id bigint
);


ALTER TABLE public.draft_picks OWNER TO root;

--
-- Name: draft_picks_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.draft_picks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.draft_picks_id_seq OWNER TO root;

--
-- Name: draft_picks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.draft_picks_id_seq OWNED BY public.draft_picks.id;


--
-- Name: drafts; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.drafts (
    number_of_rounds integer,
    snake_order boolean,
    id bigint NOT NULL,
    league_id bigint,
    season_id bigint,
    start_time timestamp(6) without time zone,
    name character varying(255)
);


ALTER TABLE public.drafts OWNER TO root;

--
-- Name: drafts_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.drafts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.drafts_id_seq OWNER TO root;

--
-- Name: drafts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.drafts_id_seq OWNED BY public.drafts.id;


--
-- Name: leagues; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.leagues (
    max_teams integer,
    admin_id bigint,
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.leagues OWNER TO root;

--
-- Name: leagues_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.leagues_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.leagues_id_seq OWNER TO root;

--
-- Name: leagues_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.leagues_id_seq OWNED BY public.leagues.id;


--
-- Name: matchups; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.matchups (
    is_championship boolean NOT NULL,
    is_playoff boolean NOT NULL,
    draft_id bigint,
    id bigint NOT NULL,
    race_id bigint,
    team1_id bigint,
    team2_id bigint
);


ALTER TABLE public.matchups OWNER TO root;

--
-- Name: matchups_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.matchups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.matchups_id_seq OWNER TO root;

--
-- Name: matchups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.matchups_id_seq OWNED BY public.matchups.id;


--
-- Name: race_results; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.race_results (
    gender_place integer,
    overall_place integer,
    id bigint NOT NULL,
    race_id bigint,
    runner_id bigint,
    "time" character varying(255)
);


ALTER TABLE public.race_results OWNER TO root;

--
-- Name: race_results_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.race_results_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.race_results_id_seq OWNER TO root;

--
-- Name: race_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.race_results_id_seq OWNED BY public.race_results.id;


--
-- Name: races; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.races (
    date date,
    is_canceled boolean,
    id bigint NOT NULL,
    season_id bigint,
    name character varying(255)
);


ALTER TABLE public.races OWNER TO root;

--
-- Name: races_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.races_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.races_id_seq OWNER TO root;

--
-- Name: races_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.races_id_seq OWNED BY public.races.id;


--
-- Name: runners; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.runners (
    id bigint NOT NULL,
    gender character varying(255),
    name character varying(255)
);


ALTER TABLE public.runners OWNER TO root;

--
-- Name: runners_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.runners_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.runners_id_seq OWNER TO root;

--
-- Name: runners_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.runners_id_seq OWNED BY public.runners.id;


--
-- Name: seasons; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.seasons (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.seasons OWNER TO root;

--
-- Name: seasons_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.seasons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.seasons_id_seq OWNER TO root;

--
-- Name: seasons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.seasons_id_seq OWNED BY public.seasons.id;


--
-- Name: teams; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.teams (
    id bigint NOT NULL,
    league_id bigint,
    owner_id bigint,
    name character varying(255)
);


ALTER TABLE public.teams OWNER TO root;

--
-- Name: teams_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.teams_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.teams_id_seq OWNER TO root;

--
-- Name: teams_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.teams_id_seq OWNED BY public.teams.id;


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role character varying(255)
);


ALTER TABLE public.user_roles OWNER TO root;

--
-- Name: users; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.users (
    active boolean NOT NULL,
    id bigint NOT NULL,
    email character varying(255),
    name character varying(255),
    password character varying(255),
    picture character varying(255)
);


ALTER TABLE public.users OWNER TO root;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO root;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: root
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: draft_picks id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_picks ALTER COLUMN id SET DEFAULT nextval('public.draft_picks_id_seq'::regclass);


--
-- Name: drafts id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.drafts ALTER COLUMN id SET DEFAULT nextval('public.drafts_id_seq'::regclass);


--
-- Name: leagues id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.leagues ALTER COLUMN id SET DEFAULT nextval('public.leagues_id_seq'::regclass);


--
-- Name: matchups id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups ALTER COLUMN id SET DEFAULT nextval('public.matchups_id_seq'::regclass);


--
-- Name: race_results id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.race_results ALTER COLUMN id SET DEFAULT nextval('public.race_results_id_seq'::regclass);


--
-- Name: races id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.races ALTER COLUMN id SET DEFAULT nextval('public.races_id_seq'::regclass);


--
-- Name: runners id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.runners ALTER COLUMN id SET DEFAULT nextval('public.runners_id_seq'::regclass);


--
-- Name: seasons id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.seasons ALTER COLUMN id SET DEFAULT nextval('public.seasons_id_seq'::regclass);


--
-- Name: teams id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.teams ALTER COLUMN id SET DEFAULT nextval('public.teams_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: draft_order draft_order_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_order
    ADD CONSTRAINT draft_order_pkey PRIMARY KEY ("position", draft_id);


--
-- Name: draft_picks draft_picks_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_picks
    ADD CONSTRAINT draft_picks_pkey PRIMARY KEY (id);


--
-- Name: drafts drafts_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.drafts
    ADD CONSTRAINT drafts_pkey PRIMARY KEY (id);


--
-- Name: leagues leagues_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.leagues
    ADD CONSTRAINT leagues_pkey PRIMARY KEY (id);


--
-- Name: matchups matchups_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups
    ADD CONSTRAINT matchups_pkey PRIMARY KEY (id);


--
-- Name: race_results race_results_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.race_results
    ADD CONSTRAINT race_results_pkey PRIMARY KEY (id);


--
-- Name: races races_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.races
    ADD CONSTRAINT races_pkey PRIMARY KEY (id);


--
-- Name: runners runners_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.runners
    ADD CONSTRAINT runners_pkey PRIMARY KEY (id);


--
-- Name: seasons seasons_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.seasons
    ADD CONSTRAINT seasons_pkey PRIMARY KEY (id);


--
-- Name: teams teams_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: race_results fk20dxqseovc32mc7m552mhrnku; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.race_results
    ADD CONSTRAINT fk20dxqseovc32mc7m552mhrnku FOREIGN KEY (runner_id) REFERENCES public.runners(id);


--
-- Name: race_results fk2elke3gjhe8xwitsftgj573cn; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.race_results
    ADD CONSTRAINT fk2elke3gjhe8xwitsftgj573cn FOREIGN KEY (race_id) REFERENCES public.races(id);


--
-- Name: draft_picks fk4505bstx8p0c4c3h0mh2v84pk; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_picks
    ADD CONSTRAINT fk4505bstx8p0c4c3h0mh2v84pk FOREIGN KEY (runner_id) REFERENCES public.runners(id);


--
-- Name: matchups fk78cwma72hqxoejhqei2tp3qmy; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups
    ADD CONSTRAINT fk78cwma72hqxoejhqei2tp3qmy FOREIGN KEY (draft_id) REFERENCES public.drafts(id);


--
-- Name: leagues fk7oiejbdywqy1u23vbjdua5ht; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.leagues
    ADD CONSTRAINT fk7oiejbdywqy1u23vbjdua5ht FOREIGN KEY (admin_id) REFERENCES public.users(id);


--
-- Name: draft_order fk7ymtsrxafuohq835ev916xe0u; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_order
    ADD CONSTRAINT fk7ymtsrxafuohq835ev916xe0u FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- Name: matchups fk8halk4d520930lci42svt5y9k; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups
    ADD CONSTRAINT fk8halk4d520930lci42svt5y9k FOREIGN KEY (team1_id) REFERENCES public.teams(id);


--
-- Name: races fkam78v1w4lr5hqu0msm7qa0v97; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.races
    ADD CONSTRAINT fkam78v1w4lr5hqu0msm7qa0v97 FOREIGN KEY (season_id) REFERENCES public.seasons(id);


--
-- Name: teams fkcmnrlwu7alyse9s3x5tgvxyqj; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT fkcmnrlwu7alyse9s3x5tgvxyqj FOREIGN KEY (league_id) REFERENCES public.leagues(id);


--
-- Name: teams fkde03in0noals71lom04bmfgit; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT fkde03in0noals71lom04bmfgit FOREIGN KEY (owner_id) REFERENCES public.users(id);


--
-- Name: drafts fkfnbyk41jhv6ubqjlibgqsmmc; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.drafts
    ADD CONSTRAINT fkfnbyk41jhv6ubqjlibgqsmmc FOREIGN KEY (league_id) REFERENCES public.leagues(id);


--
-- Name: draft_picks fkg3j0yi9ssr6sae9n2g658hly7; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_picks
    ADD CONSTRAINT fkg3j0yi9ssr6sae9n2g658hly7 FOREIGN KEY (draft_id) REFERENCES public.drafts(id);


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: matchups fknb6cmr9ncs5clcjyagq76ni0t; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups
    ADD CONSTRAINT fknb6cmr9ncs5clcjyagq76ni0t FOREIGN KEY (team2_id) REFERENCES public.teams(id);


--
-- Name: draft_order fknhq73hk3cqxn8eqkcvyshnyy1; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_order
    ADD CONSTRAINT fknhq73hk3cqxn8eqkcvyshnyy1 FOREIGN KEY (draft_id) REFERENCES public.drafts(id);


--
-- Name: draft_picks fkqqb2ro8kusada6segyxbxh813; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.draft_picks
    ADD CONSTRAINT fkqqb2ro8kusada6segyxbxh813 FOREIGN KEY (team_id) REFERENCES public.teams(id);


--
-- Name: matchups fksiuk7laxyuseegfvcdidbd742; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.matchups
    ADD CONSTRAINT fksiuk7laxyuseegfvcdidbd742 FOREIGN KEY (race_id) REFERENCES public.races(id);


--
-- Name: drafts fktd2u4e8hxg8mkc37y5qen6ubf; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.drafts
    ADD CONSTRAINT fktd2u4e8hxg8mkc37y5qen6ubf FOREIGN KEY (season_id) REFERENCES public.seasons(id);


--
-- PostgreSQL database dump complete
--

