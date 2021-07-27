ALTER DATABASE ${flyway:database} OWNER TO postgres;

CREATE TABLE public.address_classificator_items (
    id bigint NOT NULL,
    parent_id bigint,
    delivery_service integer NOT NULL,
    name_ua character varying(100) NOT NULL,
    name_ru character varying(100) NOT NULL,
    region_ua character varying(100) NOT NULL,
    region_ru character varying(100) NOT NULL,
    archive boolean NOT NULL
);

ALTER TABLE public.address_classificator_items OWNER TO postgres;

ALTER TABLE ONLY public.address_classificator_items
    ADD CONSTRAINT address_classificator_items_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.address_classificator_items
    ADD CONSTRAINT parentConstrain FOREIGN KEY (parent_id) REFERENCES public.address_classificator_items(id);

CREATE SEQUENCE public.address_classificator_item_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;