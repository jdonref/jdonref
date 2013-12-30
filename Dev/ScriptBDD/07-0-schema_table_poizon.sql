--
-- PostgreSQL database dump
--

-- Started on 2012-12-21 10:29:02

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 3834 (class 1259 OID 1330597)
-- Dependencies: 4133 1099 6
-- Name: poizon; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE poizon (
    poizon_service integer,
    poizon_cle character varying,
    poizon_cle_pq character varying,
    poizon_lbl character varying,
    poizon_lbl_pq character varying,
    poizon_lbl_sans_articles character varying,
    poizon_lbl_sans_articles_pq character varying,
    poizon_id1 character varying,
    poizon_id2 character varying,
    poizon_id3 character varying,
    poizon_id4 character varying,
    poizon_id5 character varying,
    poizon_id6 character varying,
    poizon_id7 character varying,
    poizon_donnee1 character varying,
    poizon_donnee2 character varying,
    poizon_donnee3 character varying,
    poizon_donnee4 character varying,
    poizon_donnee5 character varying,
    poizon_donnee6 character varying,
    poizon_donnee7 character varying,
    poizon_donnee_origine1 character varying,
    poizon_donnee_origine2 character varying,
    poizon_donnee_origine3 character varying,
    poizon_donnee_origine4 character varying,
    poizon_donnee_origine5 character varying,
    poizon_donnee_origine6 character varying,
    poizon_donnee_origine7 character varying,
    t0 timestamp without time zone,
    t1 timestamp without time zone,
    poizon_referentiel character varying,
    geometrie geometry,
    CONSTRAINT enforce_dims_geometrie CHECK ((st_ndims(geometrie) = 2))
);


--
-- TOC entry 4134 (class 1259 OID 1332780)
-- Dependencies: 3834
-- Name: poizon_cle_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_cle_idx ON poizon USING btree (poizon_cle);


--
-- TOC entry 4135 (class 1259 OID 1330816)
-- Dependencies: 3076 3834
-- Name: poizon_geometrie; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_geometrie ON poizon USING gist (geometrie);


--
-- TOC entry 4136 (class 1259 OID 1332773)
-- Dependencies: 3834
-- Name: poizon_id1_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id1_idx ON poizon USING btree (poizon_id1);


--
-- TOC entry 4137 (class 1259 OID 1332774)
-- Dependencies: 3834
-- Name: poizon_id2_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id2_idx ON poizon USING btree (poizon_donnee2);


--
-- TOC entry 4138 (class 1259 OID 1332775)
-- Dependencies: 3834
-- Name: poizon_id3_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id3_idx ON poizon USING btree (poizon_id3);


--
-- TOC entry 4139 (class 1259 OID 1332776)
-- Dependencies: 3834
-- Name: poizon_id4_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id4_idx ON poizon USING btree (poizon_id4);


--
-- TOC entry 4140 (class 1259 OID 1332777)
-- Dependencies: 3834
-- Name: poizon_id5_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id5_idx ON poizon USING btree (poizon_id5);


--
-- TOC entry 4141 (class 1259 OID 1332778)
-- Dependencies: 3834
-- Name: poizon_id6_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id6_idx ON poizon USING btree (poizon_id6);


--
-- TOC entry 4142 (class 1259 OID 1332779)
-- Dependencies: 3834
-- Name: poizon_id7_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_id7_idx ON poizon USING btree (poizon_id7);


--
-- TOC entry 4143 (class 1259 OID 1332781)
-- Dependencies: 3834
-- Name: poizon_lbl_sans_articles_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_lbl_sans_articles_idx ON poizon USING btree (poizon_lbl_sans_articles);


--
-- TOC entry 4144 (class 1259 OID 1332772)
-- Dependencies: 3834
-- Name: poizon_service_idx; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX poizon_service_idx ON poizon USING btree (poizon_service);




--
-- TOC entry 4147 (class 0 OID 0)
-- Dependencies: 3834
-- Name: poizon; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE poizon FROM PUBLIC;
REVOKE ALL ON TABLE poizon FROM postgres;
GRANT ALL ON TABLE poizon TO postgres;
GRANT ALL ON TABLE poizon TO jdonrefadmin;
GRANT SELECT ON TABLE poizon TO jdonref;


-- Completed on 2012-12-21 10:29:03

--
-- PostgreSQL database dump complete
--

