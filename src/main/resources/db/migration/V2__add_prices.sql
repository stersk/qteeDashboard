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

ALTER DATABASE ${flyway:database} OWNER TO postgres;

ALTER TABLE public.accounts
ADD COLUMN price bigint;

UPDATE public.accounts
SET price = 500;

ALTER TABLE public.shipments
ADD COLUMN price bigint;

UPDATE public.shipments
SET price = 500;