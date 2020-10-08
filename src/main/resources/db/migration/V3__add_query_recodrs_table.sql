ALTER DATABASE ${flyway:database} OWNER TO postgres;

CREATE TABLE public.query_records (
    id bigint NOT NULL,
    filtered boolean,
    request_body text,
    request_headers text,
    response_body text,
    response_headers text,
    response_status_code integer,
    uri character varying(255),
    account_id bigint,
    query_date timestamp without time zone,
    response_date timestamp without time zone
);

ALTER TABLE public.query_records OWNER TO postgres;

ALTER TABLE ONLY public.query_records
    ADD CONSTRAINT query_records_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.query_records
    ADD CONSTRAINT fkdq10csagxfbur4cxhtligbvi8 FOREIGN KEY (account_id) REFERENCES public.accounts(id);