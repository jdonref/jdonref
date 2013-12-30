--
-- PostgreSQL database dump
--

-- Started on 2012-03-16 10:10:30

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;


CREATE TABLE pay_pays (
    pay_sov_a3 character varying(3),
    pay_nom_origine character varying(256),
    pay_nom_int character varying(64),
    pay_nom_int_pq character varying(64),
    pay_nom_fr character varying(64),
    pay_nom_fr_desab character varying(64),
    pay_nom_fr_pq character varying(64),
    pay_type character varying(32),
    pay_nom_capitale character varying(64),
    pay_nom_capitale_pq character varying(64),
    t0 timestamp without time zone,
    t1 timestamp without time zone,
    pay_position_capitale geometry,
    pay_point_central geometry,
    pay_geometrie geometry,
    pay_projection character varying(255),
    pay_referentiel character varying(255),
	
	CONSTRAINT enforce_dims_geometrie_capitale CHECK (st_ndims(pay_position_capitale) = 2),
	CONSTRAINT enforce_dims_geometrie_pt_central CHECK (st_ndims(pay_point_central) = 2),
	CONSTRAINT enforce_dims_geometrie CHECK (st_ndims(pay_geometrie) = 2)
);


ALTER TABLE pay_pays OWNER TO jdonrefadmin;
GRANT ALL ON TABLE pay_pays TO jdonrefadmin;
GRANT SELECT ON TABLE pay_pays TO jdonref;



CREATE INDEX pay_nom_fr ON pay_pays USING btree (pay_nom_fr);
CREATE INDEX pay_sov_a3 ON pay_pays USING btree (pay_sov_a3);
CREATE INDEX pay_pays_geometrie ON pay_pays USING gist (pay_geometrie);
CREATE INDEX pay_pays_point_central ON pay_pays USING gist (pay_point_central);
