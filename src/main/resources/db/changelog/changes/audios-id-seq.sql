-- liquibase formatted sql
-- changeset <postgres>:<audios-id-seq>

CREATE SEQUENCE IF NOT EXISTS public.audios_id_seq INCREMENT 1 START 1 MINVALUE 1;

--rollback DROP SEQUENCE public.audios_id_seq;