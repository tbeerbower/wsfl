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


--
-- Name: league_id_seq; Type: SEQUENCE; Schema: public; Owner: root
--

CREATE SEQUENCE public.league_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.league_id_seq OWNER TO root;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: league; Type: TABLE; Schema: public; Owner: root
--

CREATE TABLE public.league (
    id integer DEFAULT nextval('public.league_id_seq'::regclass) NOT NULL,
    name character varying(45) NOT NULL,
    owner_id integer
);


ALTER TABLE public.league OWNER TO root;

--
-- Name: matchup_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.matchup_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.matchup_id_seq OWNER TO tbeerbower;

--
-- Name: matchup; Type: TABLE; Schema: public; Owner: tbeerbower
--


CREATE TABLE public.matchup (
    id integer DEFAULT nextval('public.matchup_id_seq'::regclass) NOT NULL,
    season_id integer NOT NULL,
    race_id integer NOT NULL,
    team_a_id integer NOT NULL,
    team_b_id integer NOT NULL
);


ALTER TABLE public.matchup OWNER TO tbeerbower;

--
-- Name: race_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.race_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.race_id_seq OWNER TO postgres;

--
-- Name: race; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.race (
    id integer DEFAULT nextval('public.race_id_seq'::regclass) NOT NULL,
    name character varying(255) NOT NULL,
    season_id integer NOT NULL,
    week integer NOT NULL,
    cancelled boolean NOT NULL,
    race_definition_id integer NOT NULL
);


ALTER TABLE public.race OWNER TO postgres;

--
-- Name: race_definition_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.race_definition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.race_definition_id_seq OWNER TO tbeerbower;

--
-- Name: race_definition; Type: TABLE; Schema: public; Owner: tbeerbower
--

CREATE TABLE public.race_definition (
    id integer DEFAULT nextval('public.race_definition_id_seq'::regclass) NOT NULL,
    name character varying(45) NOT NULL,
    short_name character varying(45) NOT NULL,
    small_icon character varying(45) NOT NULL
);


ALTER TABLE public.race_definition OWNER TO tbeerbower;

--
-- Name: result_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.result_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.result_id_seq OWNER TO postgres;

--
-- Name: result; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.result (
    id integer DEFAULT nextval('public.result_id_seq'::regclass) NOT NULL,
    runner_id integer NOT NULL,
    race_id integer NOT NULL,
    place_gender integer NOT NULL,
    place_overall integer NOT NULL,
    "time" character varying(255) NOT NULL
);


ALTER TABLE public.result OWNER TO postgres;

--
-- Name: runner_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.runner_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.runner_id_seq OWNER TO postgres;

--
-- Name: runner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.runner (
    id integer DEFAULT nextval('public.runner_id_seq'::regclass) NOT NULL,
    name character varying(45) NOT NULL,
    gender character varying(1) NOT NULL
);


ALTER TABLE public.runner OWNER TO postgres;

--
-- Name: runner_alias_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.runner_alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.runner_alias_id_seq OWNER TO postgres;

--
-- Name: runner_alias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.runner_alias (
    id integer DEFAULT nextval('public.runner_alias_id_seq'::regclass) NOT NULL,
    name character varying(45) NOT NULL,
    runner_id integer NOT NULL
);


ALTER TABLE public.runner_alias OWNER TO postgres;

--
-- Name: season_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.season_id_seq OWNER TO postgres;

--
-- Name: season; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.season (
    id integer DEFAULT nextval('public.season_id_seq'::regclass) NOT NULL,
    name character varying(45) NOT NULL,
    num_scores integer NOT NULL,
    draft_rounds integer NOT NULL,
    supplemental_rounds integer NOT NULL
);


ALTER TABLE public.season OWNER TO postgres;

--
-- Name: team_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.team_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.team_id_seq OWNER TO postgres;

--
-- Name: team; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.team (
    id integer DEFAULT nextval('public.team_id_seq'::regclass) NOT NULL,
    owner_id integer,
    name character varying(45) NOT NULL
);


ALTER TABLE public.team OWNER TO postgres;

--
-- Name: team_runner_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.team_runner_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.team_runner_id_seq OWNER TO postgres;

--
-- Name: team_runner; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.team_runner (
    id integer DEFAULT nextval('public.team_runner_id_seq'::regclass) NOT NULL,
    runner_id integer NOT NULL,
    draft_order integer DEFAULT 1 NOT NULL,
    active boolean NOT NULL,
    team_season_id integer NOT NULL
);


ALTER TABLE public.team_runner OWNER TO postgres;

--
-- Name: team_season_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.team_season_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.team_season_id_seq OWNER TO tbeerbower;

--
-- Name: team_season; Type: TABLE; Schema: public; Owner: tbeerbower
--

CREATE TABLE public.team_season (
    team_id integer NOT NULL,
    season_id integer NOT NULL,
    draft_order integer NOT NULL,
    id integer DEFAULT nextval('public.team_season_id_seq'::regclass) NOT NULL,
    league_id integer NOT NULL
);


ALTER TABLE public.team_season OWNER TO tbeerbower;

--
-- Name: usr_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.usr_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usr_id_seq OWNER TO tbeerbower;

--
-- Name: usr; Type: TABLE; Schema: public; Owner: tbeerbower
--

CREATE TABLE public.usr (
    id integer DEFAULT nextval('public.usr_id_seq'::regclass) NOT NULL,
    name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(100) NOT NULL,
    active boolean NOT NULL,
    roles character varying(100) NOT NULL,
    picture character varying(255)
);


ALTER TABLE public.usr OWNER TO tbeerbower;

--
-- Name: volunteer_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.volunteer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.volunteer_id_seq OWNER TO tbeerbower;

--
-- Name: volunteer; Type: TABLE; Schema: public; Owner: tbeerbower
--

CREATE TABLE public.volunteer (
    id integer DEFAULT nextval('public.volunteer_id_seq'::regclass) NOT NULL,
    shift_id integer NOT NULL,
    runner_id integer NOT NULL
);


ALTER TABLE public.volunteer OWNER TO tbeerbower;

--
-- Name: volunteer_shift_id_seq; Type: SEQUENCE; Schema: public; Owner: tbeerbower
--

CREATE SEQUENCE public.volunteer_shift_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.volunteer_shift_id_seq OWNER TO tbeerbower;

--
-- Name: volunteer_shift; Type: TABLE; Schema: public; Owner: tbeerbower
--

CREATE TABLE public.volunteer_shift (
    id integer DEFAULT nextval('public.volunteer_shift_id_seq'::regclass) NOT NULL,
    season_id integer NOT NULL,
    race_id integer,
    name character varying(45) NOT NULL,
    tag character varying(100) NOT NULL,
    race_definition_id integer
);


ALTER TABLE public.volunteer_shift OWNER TO tbeerbower;

--
-- Name: league league_pkey; Type: CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.league
    ADD CONSTRAINT league_pkey PRIMARY KEY (id);


--
-- Name: race_definition race_definition_name_key; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.race_definition
    ADD CONSTRAINT race_definition_name_key UNIQUE (name);


--
-- Name: race_definition race_definition_pkey; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.race_definition
    ADD CONSTRAINT race_definition_pkey PRIMARY KEY (id);


--
-- Name: race race_name_season_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_name_season_id_key UNIQUE (name, season_id);


--
-- Name: race race_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_pkey PRIMARY KEY (id);


--
-- Name: result result_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.result
    ADD CONSTRAINT result_pkey PRIMARY KEY (id);


--
-- Name: result result_runner_id_race_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.result
    ADD CONSTRAINT result_runner_id_race_id_key UNIQUE (runner_id, race_id);


--
-- Name: runner_alias runner_alias_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.runner_alias
    ADD CONSTRAINT runner_alias_name_key UNIQUE (name);


--
-- Name: runner_alias runner_alias_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.runner_alias
    ADD CONSTRAINT runner_alias_pkey PRIMARY KEY (id);


--
-- Name: runner runner_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.runner
    ADD CONSTRAINT runner_name_key UNIQUE (name);


--
-- Name: runner runner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.runner
    ADD CONSTRAINT runner_pkey PRIMARY KEY (id);


--
-- Name: season season_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_name_key UNIQUE (name);


--
-- Name: season season_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.season
    ADD CONSTRAINT season_pkey PRIMARY KEY (id);


--
-- Name: team team_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team
    ADD CONSTRAINT team_name_key UNIQUE (name);


--
-- Name: team team_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team
    ADD CONSTRAINT team_pkey PRIMARY KEY (id);


--
-- Name: team_runner team_runner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team_runner
    ADD CONSTRAINT team_runner_pkey PRIMARY KEY (id);


--
-- Name: team_season team_season_pkey; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.team_season
    ADD CONSTRAINT team_season_pkey PRIMARY KEY (id);


--
-- Name: team_season team_season_season_id_draft_order_key; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.team_season
    ADD CONSTRAINT team_season_season_id_draft_order_key UNIQUE (season_id, draft_order);


--
-- Name: usr usr_pkey; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.usr
    ADD CONSTRAINT usr_pkey PRIMARY KEY (id);


--
-- Name: volunteer volunteer_pkey; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer
    ADD CONSTRAINT volunteer_pkey PRIMARY KEY (id);


--
-- Name: volunteer volunteer_shift_id_runner_id_key; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer
    ADD CONSTRAINT volunteer_shift_id_runner_id_key UNIQUE (shift_id, runner_id);


--
-- Name: volunteer_shift volunteer_shift_pkey; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer_shift
    ADD CONSTRAINT volunteer_shift_pkey PRIMARY KEY (id);


--
-- Name: volunteer_shift volunteer_shift_tag_season_id_key; Type: CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer_shift
    ADD CONSTRAINT volunteer_shift_tag_season_id_key UNIQUE (tag, season_id);


--
-- Name: fki_team_season_league_id_fkey; Type: INDEX; Schema: public; Owner: tbeerbower
--

CREATE INDEX fki_team_season_league_id_fkey ON public.team_season USING btree (league_id);


--
-- Name: race_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX race_id ON public.race USING btree (id);


--
-- Name: race_season_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX race_season_id ON public.race USING btree (season_id);


--
-- Name: result_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX result_id ON public.result USING btree (id);


--
-- Name: result_race_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX result_race_id ON public.result USING btree (race_id);


--
-- Name: result_runner_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX result_runner_id ON public.result USING btree (runner_id);


--
-- Name: runner_alias_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX runner_alias_id ON public.runner_alias USING btree (id);


--
-- Name: runner_alias_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX runner_alias_name ON public.runner_alias USING btree (name);


--
-- Name: runner_alias_runner_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX runner_alias_runner_id ON public.runner_alias USING btree (runner_id);


--
-- Name: runner_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX runner_id ON public.runner USING btree (id);


--
-- Name: runner_name; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX runner_name ON public.runner USING btree (name);


--
-- Name: season_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX season_id ON public.season USING btree (id);


--
-- Name: team_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX team_id ON public.team USING btree (id);


--
-- Name: team_runner_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX team_runner_id ON public.team_runner USING btree (id);


--
-- Name: league fknjnle5op9xoyb2dyq8cwxui11; Type: FK CONSTRAINT; Schema: public; Owner: root
--

ALTER TABLE ONLY public.league
    ADD CONSTRAINT fknjnle5op9xoyb2dyq8cwxui11 FOREIGN KEY (owner_id) REFERENCES public.usr(id);


--
-- Name: matchup matchup_race_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.matchup
    ADD CONSTRAINT matchup_race_id_fkey FOREIGN KEY (race_id) REFERENCES public.race(id) ON DELETE CASCADE;


--
-- Name: matchup matchup_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.matchup
    ADD CONSTRAINT matchup_season_id_fkey FOREIGN KEY (season_id) REFERENCES public.season(id) ON DELETE CASCADE;


--
-- Name: matchup matchup_team_a_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.matchup
    ADD CONSTRAINT matchup_team_a_id_fkey FOREIGN KEY (team_a_id) REFERENCES public.team(id) ON DELETE CASCADE;


--
-- Name: matchup matchup_team_b_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.matchup
    ADD CONSTRAINT matchup_team_b_id_fkey FOREIGN KEY (team_b_id) REFERENCES public.team(id) ON DELETE CASCADE;


--
-- Name: race race_race_definition_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_race_definition_id_fkey FOREIGN KEY (race_definition_id) REFERENCES public.race_definition(id);


--
-- Name: race race_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_season_id_fkey FOREIGN KEY (season_id) REFERENCES public.season(id) ON DELETE CASCADE;


--
-- Name: result result_race_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.result
    ADD CONSTRAINT result_race_id_fkey FOREIGN KEY (race_id) REFERENCES public.race(id) ON DELETE CASCADE;


--
-- Name: result result_runner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.result
    ADD CONSTRAINT result_runner_id_fkey FOREIGN KEY (runner_id) REFERENCES public.runner(id) ON DELETE CASCADE;


--
-- Name: runner_alias runner_alias_runner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.runner_alias
    ADD CONSTRAINT runner_alias_runner_id_fkey FOREIGN KEY (runner_id) REFERENCES public.runner(id) ON DELETE CASCADE;


--
-- Name: team_runner team_runner_runner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team_runner
    ADD CONSTRAINT team_runner_runner_id_fkey FOREIGN KEY (runner_id) REFERENCES public.runner(id) ON DELETE CASCADE;


--
-- Name: team_runner team_runner_team_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team_runner
    ADD CONSTRAINT team_runner_team_season_id_fkey FOREIGN KEY (team_season_id) REFERENCES public.team_season(id);


--
-- Name: team_season team_season_league_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.team_season
    ADD CONSTRAINT team_season_league_id_fkey FOREIGN KEY (league_id) REFERENCES public.league(id);


--
-- Name: team_season team_season_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.team_season
    ADD CONSTRAINT team_season_season_id_fkey FOREIGN KEY (season_id) REFERENCES public.season(id) ON DELETE CASCADE;


--
-- Name: team_season team_season_team_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.team_season
    ADD CONSTRAINT team_season_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.team(id) ON DELETE CASCADE;


--
-- Name: team team_usr_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.team
    ADD CONSTRAINT team_usr_id_fkey FOREIGN KEY (owner_id) REFERENCES public.usr(id);


--
-- Name: volunteer volunteer_runner_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer
    ADD CONSTRAINT volunteer_runner_id_fkey FOREIGN KEY (runner_id) REFERENCES public.runner(id) ON DELETE CASCADE;


--
-- Name: volunteer volunteer_shift_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer
    ADD CONSTRAINT volunteer_shift_id_fkey FOREIGN KEY (shift_id) REFERENCES public.volunteer_shift(id) ON DELETE CASCADE;


--
-- Name: volunteer_shift volunteer_shift_race_definition_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer_shift
    ADD CONSTRAINT volunteer_shift_race_definition_id_fkey FOREIGN KEY (race_definition_id) REFERENCES public.race_definition(id);


--
-- Name: volunteer_shift volunteer_shift_race_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer_shift
    ADD CONSTRAINT volunteer_shift_race_id_fkey FOREIGN KEY (race_id) REFERENCES public.race(id) ON DELETE CASCADE;


--
-- Name: volunteer_shift volunteer_shift_season_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: tbeerbower
--

ALTER TABLE ONLY public.volunteer_shift
    ADD CONSTRAINT volunteer_shift_season_id_fkey FOREIGN KEY (season_id) REFERENCES public.season(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

