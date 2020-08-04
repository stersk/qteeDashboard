
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;
--
-- Name: qteeAdmin; Type: DATABASE; Schema: -; Owner: postgres
--

ALTER DATABASE ${flyway:database} OWNER TO postgres;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

--
-- Name: accounts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.accounts (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.accounts OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: invoices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.invoices (
    id uuid NOT NULL,
    commission_rate bigint,
    date timestamp without time zone,
    description character varying(2048),
    notified boolean,
    number character varying(255),
    sum bigint,
    account_id bigint,
    text character varying(1024)
);


ALTER TABLE public.invoices OWNER TO postgres;

--
-- Name: metrics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.metrics (
    metric_type integer NOT NULL,
    date timestamp without time zone,
    notify boolean,
    value double precision,
    account_id bigint NOT NULL,
    notify_text character varying(255)
);


ALTER TABLE public.metrics OWNER TO postgres;

--
-- Name: shipments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipments (
    id uuid NOT NULL,
    address character varying(255),
    customer character varying(255),
    date timestamp without time zone,
    delivery_service integer,
    phone character varying(255),
    sum bigint,
    account_id bigint,
    declaration_number character varying(255)
);


ALTER TABLE public.shipments OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    fullname character varying(255),
    password character varying(255),
    username character varying(255),
    role integer,
    enabled boolean,
    phone character varying(255),
    account_id bigint
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: accounts accounts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.accounts
    ADD CONSTRAINT accounts_pkey PRIMARY KEY (id);


--
-- Name: invoices invoices_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoices
    ADD CONSTRAINT invoices_pkey PRIMARY KEY (id);


--
-- Name: metrics metrics_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.metrics
    ADD CONSTRAINT metrics_pkey PRIMARY KEY (account_id, metric_type);


--
-- Name: shipments shipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT shipments_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: shipments fk823ns1hn6otdypg46566gr01x; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipments
    ADD CONSTRAINT fk823ns1hn6otdypg46566gr01x FOREIGN KEY (account_id) REFERENCES public.accounts(id);


--
-- Name: invoices fke8hrbi6a0viu29om1c38p4ymy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.invoices
    ADD CONSTRAINT fke8hrbi6a0viu29om1c38p4ymy FOREIGN KEY (account_id) REFERENCES public.accounts(id);


--
-- Name: users fkfm8rm8ks0kgj4fhlmmljkj17x; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fkfm8rm8ks0kgj4fhlmmljkj17x FOREIGN KEY (account_id) REFERENCES public.accounts(id);


--
-- Name: metrics fkq1ld7ii320owf4bi4smx48vpn; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.metrics
    ADD CONSTRAINT fkq1ld7ii320owf4bi4smx48vpn FOREIGN KEY (account_id) REFERENCES public.accounts(id);

--
-- Name: initial data
--

INSERT INTO public.accounts VALUES (1, 'Главный администратор');

INSERT INTO public.users VALUES (5, 'Головний адміністратор', '$2a$05$3nxAZqLKhJ9jsUj8V2oHk.pq6ZS5d6KKW.vyE76gW..FpQ5wGmdyG', 'admin', 1, true, '', 1);

