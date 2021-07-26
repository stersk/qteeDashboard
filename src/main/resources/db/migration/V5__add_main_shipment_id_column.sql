
ALTER DATABASE ${flyway:database} OWNER TO postgres;

ALTER TABLE public.shipments
ADD COLUMN main_shipment_id uuid NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';