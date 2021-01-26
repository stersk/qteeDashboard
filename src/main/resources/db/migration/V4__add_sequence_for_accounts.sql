BEGIN TRANSACTION;

ALTER DATABASE ${flyway:database} OWNER TO postgres;

CREATE SEQUENCE public.account_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

SELECT pg_catalog.setval('public.account_sequence', 1, true);

COMMIT;