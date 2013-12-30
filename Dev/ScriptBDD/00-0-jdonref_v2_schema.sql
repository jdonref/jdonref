-- J. Moquet 29/10/2008
-- Généré avec PgAdmin III
-- Permet de créer le schéma initial de la base de JDONREF v2
-- Compatible JDONREF v2.1 --> v2.3.0
--
-- Attention aux contraintes SRID sur les colonnes géométrie.
--
-- L'extension Postgis doit être activée et les tables spatial_ref_sys et geometry_columns

CREATE TABLE cdp_codes_postaux (
    com_code_insee character(5),
    cdp_code_postal character(5),
    dpt_code_departement character varying(5),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

CREATE TABLE com_communes (
    com_code_insee character(5) NOT NULL,
    dpt_code_departement character varying(5),
    com_nom character varying(32),
    com_nom_desab character varying(255),
    com_nom_origine character varying(255),
    com_nom_pq character varying(255),
    com_code_insee_commune character(5),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','com_communes','geometrie',2154,'GEOMETRY',2);

CREATE TABLE dpt_departements (
    dpt_code_departement character varying(5),
    dpt_projection character varying(255),
    dpt_referentiel character varying(255),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','dpt_departements','geometrie',2154,'GEOMETRY',2);

CREATE TABLE idv_id_voies (
    voi_id character varying(30),
    dpt_code_departement character varying(5),
    idv_code_fantoir character varying(10)
);

--
-- TOC entry 2052 (class 1259 OID 15555738)
-- Dependencies: 2761 526 4
-- Name: tro_troncons_01_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_01_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_01_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2057 (class 1259 OID 15730662)
-- Dependencies: 2762 526 4
-- Name: tro_troncons_02_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_02_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_02_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2060 (class 1259 OID 15778096)
-- Dependencies: 2763 4 526
-- Name: tro_troncons_03_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_03_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_03_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2063 (class 1259 OID 15811478)
-- Dependencies: 2764 4 526
-- Name: tro_troncons_04_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_04_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_04_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2066 (class 1259 OID 15826973)
-- Dependencies: 2765 4 526
-- Name: tro_troncons_05_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_05_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_05_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2069 (class 1259 OID 15839451)
-- Dependencies: 2766 526 4
-- Name: tro_troncons_06_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_06_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_06_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2072 (class 1259 OID 15891588)
-- Dependencies: 2767 4 526
-- Name: tro_troncons_07_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_07_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_07_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2075 (class 1259 OID 15919464)
-- Dependencies: 2768 4 526
-- Name: tro_troncons_08_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_08_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_08_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2078 (class 1259 OID 15943492)
-- Dependencies: 2769 4 526
-- Name: tro_troncons_09_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_09_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_09_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2315 (class 1259 OID 19696237)
-- Dependencies: 2848 4 526
-- Name: tro_troncons_10_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_10_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_10_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2081 (class 1259 OID 15961464)
-- Dependencies: 2770 4 526
-- Name: tro_troncons_11_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_11_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_11_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2084 (class 1259 OID 16005358)
-- Dependencies: 2771 526 4
-- Name: tro_troncons_12_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_12_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_12_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2087 (class 1259 OID 16045088)
-- Dependencies: 2772 4 526
-- Name: tro_troncons_13_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_13_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_13_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2090 (class 1259 OID 16140590)
-- Dependencies: 2773 4 526
-- Name: tro_troncons_14_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_14_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_14_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2093 (class 1259 OID 16198279)
-- Dependencies: 2774 526 4
-- Name: tro_troncons_15_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_15_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_15_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2096 (class 1259 OID 16217452)
-- Dependencies: 2775 526 4
-- Name: tro_troncons_16_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_16_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_16_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2099 (class 1259 OID 16260340)
-- Dependencies: 2776 4 526
-- Name: tro_troncons_17_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_17_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_17_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2102 (class 1259 OID 16340287)
-- Dependencies: 2777 526 4
-- Name: tro_troncons_18_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_18_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_18_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2105 (class 1259 OID 16369725)
-- Dependencies: 2778 526 4
-- Name: tro_troncons_19_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_19_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_19_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2108 (class 1259 OID 16397806)
-- Dependencies: 2779 4 526
-- Name: tro_troncons_21_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_21_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_21_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2111 (class 1259 OID 16441549)
-- Dependencies: 2780 4 526
-- Name: tro_troncons_22_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_22_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_22_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2114 (class 1259 OID 16501192)
-- Dependencies: 2781 526 4
-- Name: tro_troncons_23_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_23_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_23_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2117 (class 1259 OID 16521643)
-- Dependencies: 2782 4 526
-- Name: tro_troncons_24_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_24_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_24_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2120 (class 1259 OID 16561955)
-- Dependencies: 2783 4 526
-- Name: tro_troncons_25_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_25_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_25_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2123 (class 1259 OID 16602350)
-- Dependencies: 2784 4 526
-- Name: tro_troncons_26_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_26_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_26_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2126 (class 1259 OID 16639192)
-- Dependencies: 2785 4 526
-- Name: tro_troncons_27_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_27_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_27_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2129 (class 1259 OID 16690258)
-- Dependencies: 2786 526 4
-- Name: tro_troncons_28_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_28_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_28_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2132 (class 1259 OID 16731392)
-- Dependencies: 2787 4 526
-- Name: tro_troncons_29_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_29_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_29_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2321 (class 1259 OID 19732212)
-- Dependencies: 2850 526 4
-- Name: tro_troncons_30_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_30_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_30_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2135 (class 1259 OID 16801849)
-- Dependencies: 2788 526 4
-- Name: tro_troncons_31_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_31_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_31_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2138 (class 1259 OID 16878598)
-- Dependencies: 2789 526 4
-- Name: tro_troncons_32_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_32_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_32_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2141 (class 1259 OID 16897509)
-- Dependencies: 2790 526 4
-- Name: tro_troncons_33_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_33_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_33_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2144 (class 1259 OID 16988068)
-- Dependencies: 2791 526 4
-- Name: tro_troncons_34_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_34_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_34_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2147 (class 1259 OID 17066474)
-- Dependencies: 2792 526 4
-- Name: tro_troncons_35_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_35_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_35_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2150 (class 1259 OID 17149355)
-- Dependencies: 2793 526 4
-- Name: tro_troncons_36_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_36_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_36_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2153 (class 1259 OID 17176435)
-- Dependencies: 2794 4 526
-- Name: tro_troncons_37_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_37_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_37_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2156 (class 1259 OID 17223223)
-- Dependencies: 2795 4 526
-- Name: tro_troncons_38_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_38_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_38_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2159 (class 1259 OID 17299402)
-- Dependencies: 2796 4 526
-- Name: tro_troncons_39_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_39_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_39_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2324 (class 1259 OID 19788330)
-- Dependencies: 2851 526 4
-- Name: tro_troncons_40_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_40_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_40_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2162 (class 1259 OID 17330048)
-- Dependencies: 2797 526 4
-- Name: tro_troncons_41_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_41_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_41_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2165 (class 1259 OID 17368261)
-- Dependencies: 2798 4 526
-- Name: tro_troncons_42_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_42_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_42_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2168 (class 1259 OID 17415684)
-- Dependencies: 2799 4 526
-- Name: tro_troncons_43_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_43_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_43_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2171 (class 1259 OID 17440392)
-- Dependencies: 2800 526 4
-- Name: tro_troncons_44_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_44_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_44_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2174 (class 1259 OID 17540144)
-- Dependencies: 2801 4 526
-- Name: tro_troncons_45_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_45_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_45_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2177 (class 1259 OID 17585778)
-- Dependencies: 2802 4 526
-- Name: tro_troncons_46_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_46_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_46_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2180 (class 1259 OID 17605959)
-- Dependencies: 2803 526 4
-- Name: tro_troncons_47_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_47_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_47_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2183 (class 1259 OID 17632455)
-- Dependencies: 2804 526 4
-- Name: tro_troncons_48_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_48_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_48_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2186 (class 1259 OID 17642820)
-- Dependencies: 2805 4 526
-- Name: tro_troncons_49_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_49_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_49_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2327 (class 1259 OID 19827991)
-- Dependencies: 2852 526 4
-- Name: tro_troncons_50_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_50_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_50_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2189 (class 1259 OID 17713346)
-- Dependencies: 2806 4 526
-- Name: tro_troncons_51_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_51_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_51_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2192 (class 1259 OID 17758977)
-- Dependencies: 2807 4 526
-- Name: tro_troncons_52_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_52_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_52_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2195 (class 1259 OID 17781647)
-- Dependencies: 2808 526 4
-- Name: tro_troncons_53_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_53_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_53_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2198 (class 1259 OID 17819071)
-- Dependencies: 2809 4 526
-- Name: tro_troncons_54_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_54_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_54_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2201 (class 1259 OID 17862729)
-- Dependencies: 2810 526 4
-- Name: tro_troncons_55_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_55_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_55_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2204 (class 1259 OID 17884419)
-- Dependencies: 2811 526 4
-- Name: tro_troncons_56_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_56_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_56_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2207 (class 1259 OID 17958792)
-- Dependencies: 2812 4 526
-- Name: tro_troncons_57_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_57_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_57_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2210 (class 1259 OID 18020217)
-- Dependencies: 2813 4 526
-- Name: tro_troncons_58_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_58_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_58_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2213 (class 1259 OID 18044442)
-- Dependencies: 2814 526 4
-- Name: tro_troncons_59_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_59_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_59_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2330 (class 1259 OID 19883548)
-- Dependencies: 2853 4 526
-- Name: tro_troncons_60_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_60_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_60_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2216 (class 1259 OID 18165889)
-- Dependencies: 2815 4 526
-- Name: tro_troncons_61_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_61_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_61_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2219 (class 1259 OID 18201337)
-- Dependencies: 2816 4 526
-- Name: tro_troncons_62_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_62_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_62_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2222 (class 1259 OID 18290900)
-- Dependencies: 2817 526 4
-- Name: tro_troncons_63_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_63_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_63_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2225 (class 1259 OID 18352082)
-- Dependencies: 2818 4 526
-- Name: tro_troncons_64_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_64_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_64_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2228 (class 1259 OID 18398469)
-- Dependencies: 2819 4 526
-- Name: tro_troncons_65_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_65_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_65_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2231 (class 1259 OID 18422494)
-- Dependencies: 2820 4 526
-- Name: tro_troncons_66_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_66_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_66_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2234 (class 1259 OID 18464174)
-- Dependencies: 2821 526 4
-- Name: tro_troncons_67_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_67_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_67_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2237 (class 1259 OID 18525775)
-- Dependencies: 2822 526 4
-- Name: tro_troncons_68_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_68_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_68_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2240 (class 1259 OID 18574030)
-- Dependencies: 2823 526 4
-- Name: tro_troncons_69_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_69_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_69_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2333 (class 1259 OID 19935832)
-- Dependencies: 2854 4 526
-- Name: tro_troncons_70_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_70_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_70_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2243 (class 1259 OID 18646218)
-- Dependencies: 2824 4 526
-- Name: tro_troncons_71_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_71_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_71_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2246 (class 1259 OID 18695073)
-- Dependencies: 2825 526 4
-- Name: tro_troncons_72_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_72_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_72_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2249 (class 1259 OID 18745707)
-- Dependencies: 2826 4 526
-- Name: tro_troncons_73_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_73_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_73_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2252 (class 1259 OID 18775999)
-- Dependencies: 2827 526 4
-- Name: tro_troncons_74_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_74_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_74_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2255 (class 1259 OID 18823296)
-- Dependencies: 2828 526 4
-- Name: tro_troncons_75_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_75_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_75_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2258 (class 1259 OID 18846209)
-- Dependencies: 2829 526 4
-- Name: tro_troncons_76_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_76_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_76_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2261 (class 1259 OID 18925634)
-- Dependencies: 2830 4 526
-- Name: tro_troncons_77_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_77_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_77_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2264 (class 1259 OID 19001298)
-- Dependencies: 2831 4 526
-- Name: tro_troncons_78_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_78_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_78_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2267 (class 1259 OID 19064572)
-- Dependencies: 2832 526 4
-- Name: tro_troncons_79_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_79_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_79_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2336 (class 1259 OID 19960923)
-- Dependencies: 2855 526 4
-- Name: tro_troncons_80_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_80_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_80_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2270 (class 1259 OID 19108752)
-- Dependencies: 2833 4 526
-- Name: tro_troncons_81_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_81_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_81_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2273 (class 1259 OID 19143545)
-- Dependencies: 2834 4 526
-- Name: tro_troncons_82_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_82_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_82_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2276 (class 1259 OID 19162638)
-- Dependencies: 2835 526 4
-- Name: tro_troncons_83_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_83_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_83_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2279 (class 1259 OID 19230880)
-- Dependencies: 2836 4 526
-- Name: tro_troncons_84_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_84_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_84_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2282 (class 1259 OID 19270903)
-- Dependencies: 2837 526 4
-- Name: tro_troncons_85_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_85_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_85_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2285 (class 1259 OID 19339930)
-- Dependencies: 2838 526 4
-- Name: tro_troncons_86_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_86_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_86_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2288 (class 1259 OID 19382714)
-- Dependencies: 2839 526 4
-- Name: tro_troncons_87_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_87_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_87_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2291 (class 1259 OID 19418878)
-- Dependencies: 2840 526 4
-- Name: tro_troncons_88_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_88_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_88_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2294 (class 1259 OID 19458549)
-- Dependencies: 2841 526 4
-- Name: tro_troncons_89_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_89_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_89_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2339 (class 1259 OID 20004481)
-- Dependencies: 2856 4 526
-- Name: tro_troncons_90_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_90_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_90_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2297 (class 1259 OID 19497139)
-- Dependencies: 2842 4 526
-- Name: tro_troncons_91_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_91_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_91_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2300 (class 1259 OID 19547696)
-- Dependencies: 2843 526 4
-- Name: tro_troncons_92_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_92_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_92_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2303 (class 1259 OID 19575775)
-- Dependencies: 2844 526 4
-- Name: tro_troncons_93_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_93_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_93_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2306 (class 1259 OID 19609479)
-- Dependencies: 2845 526 4
-- Name: tro_troncons_94_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_94_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_94_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2309 (class 1259 OID 19642356)
-- Dependencies: 2846 4 526
-- Name: tro_troncons_95_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_95_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_95_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Dependencies: 2847 4 526
-- Name: tro_troncons_96_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_96_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_96_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2051 (class 1259 OID 15555735)
-- Dependencies: 4
-- Name: ttr_tables_troncons; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE ttr_tables_troncons (
    dpt_code_departement character varying(5),
    ttr_nom character varying(50),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2055 (class 1259 OID 15555752)
-- Dependencies: 4
-- Name: vhi_voies_historisee_01; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_01 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2059 (class 1259 OID 15730674)
-- Dependencies: 4
-- Name: vhi_voies_historisee_02; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_02 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2062 (class 1259 OID 15778108)
-- Dependencies: 4
-- Name: vhi_voies_historisee_03; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_03 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2065 (class 1259 OID 15811490)
-- Dependencies: 4
-- Name: vhi_voies_historisee_04; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_04 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2068 (class 1259 OID 15826985)
-- Dependencies: 4
-- Name: vhi_voies_historisee_05; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_05 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2071 (class 1259 OID 15839463)
-- Dependencies: 4
-- Name: vhi_voies_historisee_06; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_06 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2074 (class 1259 OID 15891600)
-- Dependencies: 4
-- Name: vhi_voies_historisee_07; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_07 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2077 (class 1259 OID 15919476)
-- Dependencies: 4
-- Name: vhi_voies_historisee_08; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_08 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2080 (class 1259 OID 15943504)
-- Dependencies: 4
-- Name: vhi_voies_historisee_09; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_09 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2317 (class 1259 OID 19696249)
-- Dependencies: 4
-- Name: vhi_voies_historisee_10; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_10 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2083 (class 1259 OID 15961476)
-- Dependencies: 4
-- Name: vhi_voies_historisee_11; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_11 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2086 (class 1259 OID 16005370)
-- Dependencies: 4
-- Name: vhi_voies_historisee_12; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_12 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2089 (class 1259 OID 16045100)
-- Dependencies: 4
-- Name: vhi_voies_historisee_13; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_13 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2092 (class 1259 OID 16140602)
-- Dependencies: 4
-- Name: vhi_voies_historisee_14; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_14 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2095 (class 1259 OID 16198291)
-- Dependencies: 4
-- Name: vhi_voies_historisee_15; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_15 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2098 (class 1259 OID 16217464)
-- Dependencies: 4
-- Name: vhi_voies_historisee_16; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_16 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2101 (class 1259 OID 16260352)
-- Dependencies: 4
-- Name: vhi_voies_historisee_17; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_17 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2104 (class 1259 OID 16340299)
-- Dependencies: 4
-- Name: vhi_voies_historisee_18; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_18 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2107 (class 1259 OID 16369737)
-- Dependencies: 4
-- Name: vhi_voies_historisee_19; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_19 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- TOC entry 2110 (class 1259 OID 16397818)
-- Dependencies: 4
-- Name: vhi_voies_historisee_21; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_21 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2113 (class 1259 OID 16441561)
-- Dependencies: 4
-- Name: vhi_voies_historisee_22; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_22 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2116 (class 1259 OID 16501204)
-- Dependencies: 4
-- Name: vhi_voies_historisee_23; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_23 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2119 (class 1259 OID 16521655)
-- Dependencies: 4
-- Name: vhi_voies_historisee_24; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_24 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2122 (class 1259 OID 16561967)
-- Dependencies: 4
-- Name: vhi_voies_historisee_25; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_25 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2125 (class 1259 OID 16602362)
-- Dependencies: 4
-- Name: vhi_voies_historisee_26; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_26 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2128 (class 1259 OID 16639204)
-- Dependencies: 4
-- Name: vhi_voies_historisee_27; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_27 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2131 (class 1259 OID 16690270)
-- Dependencies: 4
-- Name: vhi_voies_historisee_28; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_28 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2134 (class 1259 OID 16731404)
-- Dependencies: 4
-- Name: vhi_voies_historisee_29; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_29 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2323 (class 1259 OID 19732224)
-- Dependencies: 4
-- Name: vhi_voies_historisee_30; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_30 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2137 (class 1259 OID 16801861)
-- Dependencies: 4
-- Name: vhi_voies_historisee_31; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_31 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2140 (class 1259 OID 16878610)
-- Dependencies: 4
-- Name: vhi_voies_historisee_32; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_32 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2143 (class 1259 OID 16897521)
-- Dependencies: 4
-- Name: vhi_voies_historisee_33; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_33 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2146 (class 1259 OID 16988080)
-- Dependencies: 4
-- Name: vhi_voies_historisee_34; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_34 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2149 (class 1259 OID 17066486)
-- Dependencies: 4
-- Name: vhi_voies_historisee_35; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_35 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2152 (class 1259 OID 17149367)
-- Dependencies: 4
-- Name: vhi_voies_historisee_36; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_36 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2155 (class 1259 OID 17176447)
-- Dependencies: 4
-- Name: vhi_voies_historisee_37; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_37 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2158 (class 1259 OID 17223235)
-- Dependencies: 4
-- Name: vhi_voies_historisee_38; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_38 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2161 (class 1259 OID 17299414)
-- Dependencies: 4
-- Name: vhi_voies_historisee_39; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_39 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2326 (class 1259 OID 19788342)
-- Dependencies: 4
-- Name: vhi_voies_historisee_40; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_40 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2164 (class 1259 OID 17330060)
-- Dependencies: 4
-- Name: vhi_voies_historisee_41; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_41 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2167 (class 1259 OID 17368273)
-- Dependencies: 4
-- Name: vhi_voies_historisee_42; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_42 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2170 (class 1259 OID 17415696)
-- Dependencies: 4
-- Name: vhi_voies_historisee_43; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_43 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2173 (class 1259 OID 17440404)
-- Dependencies: 4
-- Name: vhi_voies_historisee_44; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_44 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2176 (class 1259 OID 17540156)
-- Dependencies: 4
-- Name: vhi_voies_historisee_45; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_45 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2179 (class 1259 OID 17585790)
-- Dependencies: 4
-- Name: vhi_voies_historisee_46; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_46 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2182 (class 1259 OID 17605971)
-- Dependencies: 4
-- Name: vhi_voies_historisee_47; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_47 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2185 (class 1259 OID 17632467)
-- Dependencies: 4
-- Name: vhi_voies_historisee_48; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_48 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2188 (class 1259 OID 17642832)
-- Dependencies: 4
-- Name: vhi_voies_historisee_49; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_49 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2329 (class 1259 OID 19828003)
-- Dependencies: 4
-- Name: vhi_voies_historisee_50; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_50 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2191 (class 1259 OID 17713358)
-- Dependencies: 4
-- Name: vhi_voies_historisee_51; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_51 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2194 (class 1259 OID 17758989)
-- Dependencies: 4
-- Name: vhi_voies_historisee_52; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_52 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2197 (class 1259 OID 17781659)
-- Dependencies: 4
-- Name: vhi_voies_historisee_53; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_53 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2200 (class 1259 OID 17819083)
-- Dependencies: 4
-- Name: vhi_voies_historisee_54; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_54 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2203 (class 1259 OID 17862741)
-- Dependencies: 4
-- Name: vhi_voies_historisee_55; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_55 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2206 (class 1259 OID 17884431)
-- Dependencies: 4
-- Name: vhi_voies_historisee_56; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_56 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2209 (class 1259 OID 17958804)
-- Dependencies: 4
-- Name: vhi_voies_historisee_57; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_57 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2212 (class 1259 OID 18020229)
-- Dependencies: 4
-- Name: vhi_voies_historisee_58; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_58 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2215 (class 1259 OID 18044454)
-- Dependencies: 4
-- Name: vhi_voies_historisee_59; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_59 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2332 (class 1259 OID 19883560)
-- Dependencies: 4
-- Name: vhi_voies_historisee_60; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_60 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2218 (class 1259 OID 18165901)
-- Dependencies: 4
-- Name: vhi_voies_historisee_61; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_61 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2221 (class 1259 OID 18201349)
-- Dependencies: 4
-- Name: vhi_voies_historisee_62; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_62 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2224 (class 1259 OID 18290912)
-- Dependencies: 4
-- Name: vhi_voies_historisee_63; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_63 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2227 (class 1259 OID 18352094)
-- Dependencies: 4
-- Name: vhi_voies_historisee_64; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_64 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2230 (class 1259 OID 18398481)
-- Dependencies: 4
-- Name: vhi_voies_historisee_65; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_65 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2233 (class 1259 OID 18422506)
-- Dependencies: 4
-- Name: vhi_voies_historisee_66; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_66 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2236 (class 1259 OID 18464186)
-- Dependencies: 4
-- Name: vhi_voies_historisee_67; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_67 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2239 (class 1259 OID 18525787)
-- Dependencies: 4
-- Name: vhi_voies_historisee_68; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_68 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2242 (class 1259 OID 18574042)
-- Dependencies: 4
-- Name: vhi_voies_historisee_69; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_69 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2335 (class 1259 OID 19935844)
-- Dependencies: 4
-- Name: vhi_voies_historisee_70; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_70 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2245 (class 1259 OID 18646230)
-- Dependencies: 4
-- Name: vhi_voies_historisee_71; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_71 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2248 (class 1259 OID 18695085)
-- Dependencies: 4
-- Name: vhi_voies_historisee_72; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_72 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2251 (class 1259 OID 18745719)
-- Dependencies: 4
-- Name: vhi_voies_historisee_73; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_73 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2254 (class 1259 OID 18776011)
-- Dependencies: 4
-- Name: vhi_voies_historisee_74; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_74 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2257 (class 1259 OID 18823308)
-- Dependencies: 4
-- Name: vhi_voies_historisee_75; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_75 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2260 (class 1259 OID 18846221)
-- Dependencies: 4
-- Name: vhi_voies_historisee_76; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_76 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2263 (class 1259 OID 18925646)
-- Dependencies: 4
-- Name: vhi_voies_historisee_77; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_77 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2266 (class 1259 OID 19001310)
-- Dependencies: 4
-- Name: vhi_voies_historisee_78; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_78 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2269 (class 1259 OID 19064584)
-- Dependencies: 4
-- Name: vhi_voies_historisee_79; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_79 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2338 (class 1259 OID 19960935)
-- Dependencies: 4
-- Name: vhi_voies_historisee_80; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_80 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2272 (class 1259 OID 19108764)
-- Dependencies: 4
-- Name: vhi_voies_historisee_81; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_81 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2275 (class 1259 OID 19143557)
-- Dependencies: 4
-- Name: vhi_voies_historisee_82; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_82 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2278 (class 1259 OID 19162650)
-- Dependencies: 4
-- Name: vhi_voies_historisee_83; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_83 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2281 (class 1259 OID 19230892)
-- Dependencies: 4
-- Name: vhi_voies_historisee_84; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_84 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2284 (class 1259 OID 19270915)
-- Dependencies: 4
-- Name: vhi_voies_historisee_85; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_85 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2287 (class 1259 OID 19339942)
-- Dependencies: 4
-- Name: vhi_voies_historisee_86; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_86 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2290 (class 1259 OID 19382726)
-- Dependencies: 4
-- Name: vhi_voies_historisee_87; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_87 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2293 (class 1259 OID 19418890)
-- Dependencies: 4
-- Name: vhi_voies_historisee_88; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_88 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2296 (class 1259 OID 19458561)
-- Dependencies: 4
-- Name: vhi_voies_historisee_89; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_89 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2341 (class 1259 OID 20004493)
-- Dependencies: 4
-- Name: vhi_voies_historisee_90; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_90 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2299 (class 1259 OID 19497151)
-- Dependencies: 4
-- Name: vhi_voies_historisee_91; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_91 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2302 (class 1259 OID 19547708)
-- Dependencies: 4
-- Name: vhi_voies_historisee_92; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_92 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2305 (class 1259 OID 19575787)
-- Dependencies: 4
-- Name: vhi_voies_historisee_93; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_93 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2308 (class 1259 OID 19609491)
-- Dependencies: 4
-- Name: vhi_voies_historisee_94; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_94 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2311 (class 1259 OID 19642368)
-- Dependencies: 4
-- Name: vhi_voies_historisee_95; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_95 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2314 (class 1259 OID 19688030)
-- Dependencies: 4
-- Name: vhi_voies_historisee_96; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_96 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);


--
-- TOC entry 2343 (class 1259 OID 23022169)
-- Dependencies: 4
-- Name: voa_voies_ambigues_01; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_01 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2344 (class 1259 OID 23057039)
-- Dependencies: 4
-- Name: voa_voies_ambigues_02; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_02 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2345 (class 1259 OID 23061267)
-- Dependencies: 4
-- Name: voa_voies_ambigues_03; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_03 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2346 (class 1259 OID 23063607)
-- Dependencies: 4
-- Name: voa_voies_ambigues_04; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_04 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2347 (class 1259 OID 23064636)
-- Dependencies: 4
-- Name: voa_voies_ambigues_05; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_05 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2348 (class 1259 OID 23065675)
-- Dependencies: 4
-- Name: voa_voies_ambigues_06; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_06 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2349 (class 1259 OID 23070290)
-- Dependencies: 4
-- Name: voa_voies_ambigues_07; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_07 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2350 (class 1259 OID 23072375)
-- Dependencies: 4
-- Name: voa_voies_ambigues_08; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_08 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2351 (class 1259 OID 23074461)
-- Dependencies: 4
-- Name: voa_voies_ambigues_09; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_09 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2352 (class 1259 OID 23075774)
-- Dependencies: 4
-- Name: voa_voies_ambigues_10; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_10 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2353 (class 1259 OID 23110357)
-- Dependencies: 4
-- Name: voa_voies_ambigues_11; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_11 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2354 (class 1259 OID 23280660)
-- Dependencies: 4
-- Name: voa_voies_ambigues_12; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_12 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2355 (class 1259 OID 23282802)
-- Dependencies: 4
-- Name: voa_voies_ambigues_13; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_13 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2356 (class 1259 OID 23286693)
-- Dependencies: 4
-- Name: voa_voies_ambigues_14; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_14 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2357 (class 1259 OID 23292363)
-- Dependencies: 4
-- Name: voa_voies_ambigues_15; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_15 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2358 (class 1259 OID 23293368)
-- Dependencies: 4
-- Name: voa_voies_ambigues_16; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_16 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2359 (class 1259 OID 23296387)
-- Dependencies: 4
-- Name: voa_voies_ambigues_17; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_17 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2360 (class 1259 OID 23301672)
-- Dependencies: 4
-- Name: voa_voies_ambigues_18; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_18 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2361 (class 1259 OID 23303664)
-- Dependencies: 4
-- Name: voa_voies_ambigues_19; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_19 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2362 (class 1259 OID 23305462)
-- Dependencies: 4
-- Name: voa_voies_ambigues_20; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_20 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2363 (class 1259 OID 23305945)
-- Dependencies: 4
-- Name: voa_voies_ambigues_21; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_21 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2364 (class 1259 OID 23308581)
-- Dependencies: 4
-- Name: voa_voies_ambigues_22; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_22 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2365 (class 1259 OID 23311827)
-- Dependencies: 4
-- Name: voa_voies_ambigues_23; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_23 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2366 (class 1259 OID 23312959)
-- Dependencies: 4
-- Name: voa_voies_ambigues_24; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_24 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2367 (class 1259 OID 23315156)
-- Dependencies: 4
-- Name: voa_voies_ambigues_25; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_25 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2368 (class 1259 OID 23318061)
-- Dependencies: 4
-- Name: voa_voies_ambigues_26; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_26 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2369 (class 1259 OID 23320721)
-- Dependencies: 4
-- Name: voa_voies_ambigues_27; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_27 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2370 (class 1259 OID 23324686)
-- Dependencies: 4
-- Name: voa_voies_ambigues_28; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_28 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2371 (class 1259 OID 23328923)
-- Dependencies: 4
-- Name: voa_voies_ambigues_29; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_29 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2372 (class 1259 OID 23331474)
-- Dependencies: 4
-- Name: voa_voies_ambigues_30; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_30 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2373 (class 1259 OID 23334484)
-- Dependencies: 4
-- Name: voa_voies_ambigues_31; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_31 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2374 (class 1259 OID 23338112)
-- Dependencies: 4
-- Name: voa_voies_ambigues_32; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_32 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2375 (class 1259 OID 23339176)
-- Dependencies: 4
-- Name: voa_voies_ambigues_33; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_33 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2376 (class 1259 OID 23344207)
-- Dependencies: 4
-- Name: voa_voies_ambigues_34; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_34 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2377 (class 1259 OID 23349661)
-- Dependencies: 4
-- Name: voa_voies_ambigues_35; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_35 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2378 (class 1259 OID 23356354)
-- Dependencies: 4
-- Name: voa_voies_ambigues_36; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_36 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2379 (class 1259 OID 23358079)
-- Dependencies: 4
-- Name: voa_voies_ambigues_37; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_37 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2380 (class 1259 OID 23361008)
-- Dependencies: 4
-- Name: voa_voies_ambigues_38; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_38 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2381 (class 1259 OID 23364971)
-- Dependencies: 4
-- Name: voa_voies_ambigues_39; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_39 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2382 (class 1259 OID 23367872)
-- Dependencies: 4
-- Name: voa_voies_ambigues_40; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_40 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2383 (class 1259 OID 23369598)
-- Dependencies: 4
-- Name: voa_voies_ambigues_41; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_41 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2384 (class 1259 OID 23373015)
-- Dependencies: 4
-- Name: voa_voies_ambigues_42; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_42 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2385 (class 1259 OID 23375194)
-- Dependencies: 4
-- Name: voa_voies_ambigues_43; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_43 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2386 (class 1259 OID 23376320)
-- Dependencies: 4
-- Name: voa_voies_ambigues_44; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_44 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2387 (class 1259 OID 23382603)
-- Dependencies: 4
-- Name: voa_voies_ambigues_45; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_45 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2388 (class 1259 OID 23385644)
-- Dependencies: 4
-- Name: voa_voies_ambigues_46; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_46 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2389 (class 1259 OID 23386593)
-- Dependencies: 4
-- Name: voa_voies_ambigues_47; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_47 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2390 (class 1259 OID 23388011)
-- Dependencies: 4
-- Name: voa_voies_ambigues_48; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_48 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2391 (class 1259 OID 23388535)
-- Dependencies: 4
-- Name: voa_voies_ambigues_49; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_49 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2392 (class 1259 OID 23393422)
-- Dependencies: 4
-- Name: voa_voies_ambigues_50; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_50 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2393 (class 1259 OID 23398658)
-- Dependencies: 4
-- Name: voa_voies_ambigues_51; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_51 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2394 (class 1259 OID 23401710)
-- Dependencies: 4
-- Name: voa_voies_ambigues_52; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_52 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2395 (class 1259 OID 23403175)
-- Dependencies: 4
-- Name: voa_voies_ambigues_53; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_53 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2396 (class 1259 OID 23406307)
-- Dependencies: 4
-- Name: voa_voies_ambigues_54; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_54 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2397 (class 1259 OID 23409177)
-- Dependencies: 4
-- Name: voa_voies_ambigues_55; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_55 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2398 (class 1259 OID 23410746)
-- Dependencies: 4
-- Name: voa_voies_ambigues_56; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_56 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2399 (class 1259 OID 23415080)
-- Dependencies: 4
-- Name: voa_voies_ambigues_57; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_57 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2400 (class 1259 OID 23418442)
-- Dependencies: 4
-- Name: voa_voies_ambigues_58; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_58 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2401 (class 1259 OID 23419973)
-- Dependencies: 4
-- Name: voa_voies_ambigues_59; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_59 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2402 (class 1259 OID 23427479)
-- Dependencies: 4
-- Name: voa_voies_ambigues_60; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_60 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2403 (class 1259 OID 23430954)
-- Dependencies: 4
-- Name: voa_voies_ambigues_61; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_61 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2404 (class 1259 OID 23434234)
-- Dependencies: 4
-- Name: voa_voies_ambigues_62; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_62 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2405 (class 1259 OID 23440194)
-- Dependencies: 4
-- Name: voa_voies_ambigues_63; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_63 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2406 (class 1259 OID 23443486)
-- Dependencies: 4
-- Name: voa_voies_ambigues_64; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_64 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2407 (class 1259 OID 23445674)
-- Dependencies: 4
-- Name: voa_voies_ambigues_65; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_65 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2408 (class 1259 OID 23447397)
-- Dependencies: 4
-- Name: voa_voies_ambigues_66; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_66 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2409 (class 1259 OID 23449248)
-- Dependencies: 4
-- Name: voa_voies_ambigues_67; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_67 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2410 (class 1259 OID 23451819)
-- Dependencies: 4
-- Name: voa_voies_ambigues_68; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_68 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2411 (class 1259 OID 23453707)
-- Dependencies: 4
-- Name: voa_voies_ambigues_69; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_69 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2412 (class 1259 OID 23457313)
-- Dependencies: 4
-- Name: voa_voies_ambigues_70; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_70 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2413 (class 1259 OID 23459076)
-- Dependencies: 4
-- Name: voa_voies_ambigues_71; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_71 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2414 (class 1259 OID 23461984)
-- Dependencies: 4
-- Name: voa_voies_ambigues_72; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_72 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2415 (class 1259 OID 23466465)
-- Dependencies: 4
-- Name: voa_voies_ambigues_73; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_73 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2416 (class 1259 OID 23468302)
-- Dependencies: 4
-- Name: voa_voies_ambigues_74; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_74 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2417 (class 1259 OID 23470727)
-- Dependencies: 4
-- Name: voa_voies_ambigues_75; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_75 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2418 (class 1259 OID 23471406)
-- Dependencies: 4
-- Name: voa_voies_ambigues_76; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_76 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2419 (class 1259 OID 23477855)
-- Dependencies: 4
-- Name: voa_voies_ambigues_77; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_77 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2420 (class 1259 OID 23482402)
-- Dependencies: 4
-- Name: voa_voies_ambigues_78; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_78 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2421 (class 1259 OID 23485542)
-- Dependencies: 4
-- Name: voa_voies_ambigues_79; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_79 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2422 (class 1259 OID 23488187)
-- Dependencies: 4
-- Name: voa_voies_ambigues_80; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_80 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2423 (class 1259 OID 23496352)
-- Dependencies: 4
-- Name: voa_voies_ambigues_81; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_81 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2424 (class 1259 OID 23498000)
-- Dependencies: 4
-- Name: voa_voies_ambigues_82; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_82 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2425 (class 1259 OID 23498999)
-- Dependencies: 4
-- Name: voa_voies_ambigues_83; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_83 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2426 (class 1259 OID 23502255)
-- Dependencies: 4
-- Name: voa_voies_ambigues_84; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_84 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2427 (class 1259 OID 23504249)
-- Dependencies: 4
-- Name: voa_voies_ambigues_85; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_85 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2428 (class 1259 OID 23508265)
-- Dependencies: 4
-- Name: voa_voies_ambigues_86; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_86 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2429 (class 1259 OID 23510834)
-- Dependencies: 4
-- Name: voa_voies_ambigues_87; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_87 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2430 (class 1259 OID 23512838)
-- Dependencies: 4
-- Name: voa_voies_ambigues_88; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_88 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2431 (class 1259 OID 23515530)
-- Dependencies: 4
-- Name: voa_voies_ambigues_89; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_89 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2432 (class 1259 OID 23518837)
-- Dependencies: 4
-- Name: voa_voies_ambigues_90; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_90 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2433 (class 1259 OID 23519401)
-- Dependencies: 4
-- Name: voa_voies_ambigues_91; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_91 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2434 (class 1259 OID 23521725)
-- Dependencies: 4
-- Name: voa_voies_ambigues_92; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_92 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2435 (class 1259 OID 23522685)
-- Dependencies: 4
-- Name: voa_voies_ambigues_93; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_93 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2436 (class 1259 OID 23523561)
-- Dependencies: 4
-- Name: voa_voies_ambigues_94; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_94 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2437 (class 1259 OID 23524533)
-- Dependencies: 4
-- Name: voa_voies_ambigues_95; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_95 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2438 (class 1259 OID 23526571)
-- Dependencies: 4
-- Name: voa_voies_ambigues_96; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_96 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);


--
-- TOC entry 2054 (class 1259 OID 15555747)
-- Dependencies: 4
-- Name: voi_voies_01; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_01 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2058 (class 1259 OID 15730669)
-- Dependencies: 4
-- Name: voi_voies_02; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_02 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2061 (class 1259 OID 15778103)
-- Dependencies: 4
-- Name: voi_voies_03; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_03 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2064 (class 1259 OID 15811485)
-- Dependencies: 4
-- Name: voi_voies_04; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_04 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2067 (class 1259 OID 15826980)
-- Dependencies: 4
-- Name: voi_voies_05; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_05 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2070 (class 1259 OID 15839458)
-- Dependencies: 4
-- Name: voi_voies_06; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_06 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2073 (class 1259 OID 15891595)
-- Dependencies: 4
-- Name: voi_voies_07; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_07 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2076 (class 1259 OID 15919471)
-- Dependencies: 4
-- Name: voi_voies_08; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_08 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2079 (class 1259 OID 15943499)
-- Dependencies: 4
-- Name: voi_voies_09; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_09 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2316 (class 1259 OID 19696244)
-- Dependencies: 4
-- Name: voi_voies_10; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_10 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2082 (class 1259 OID 15961471)
-- Dependencies: 4
-- Name: voi_voies_11; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_11 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2085 (class 1259 OID 16005365)
-- Dependencies: 4
-- Name: voi_voies_12; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_12 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2088 (class 1259 OID 16045095)
-- Dependencies: 4
-- Name: voi_voies_13; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_13 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2091 (class 1259 OID 16140597)
-- Dependencies: 4
-- Name: voi_voies_14; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_14 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2094 (class 1259 OID 16198286)
-- Dependencies: 4
-- Name: voi_voies_15; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_15 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2097 (class 1259 OID 16217459)
-- Dependencies: 4
-- Name: voi_voies_16; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_16 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2100 (class 1259 OID 16260347)
-- Dependencies: 4
-- Name: voi_voies_17; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_17 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2103 (class 1259 OID 16340294)
-- Dependencies: 4
-- Name: voi_voies_18; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_18 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2106 (class 1259 OID 16369732)
-- Dependencies: 4
-- Name: voi_voies_19; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_19 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2319 (class 1259 OID 19725300)
-- Dependencies: 4
-- Name: voi_voies_20; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_20 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2109 (class 1259 OID 16397813)
-- Dependencies: 4
-- Name: voi_voies_21; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_21 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2112 (class 1259 OID 16441556)
-- Dependencies: 4
-- Name: voi_voies_22; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_22 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2115 (class 1259 OID 16501199)
-- Dependencies: 4
-- Name: voi_voies_23; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_23 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2118 (class 1259 OID 16521650)
-- Dependencies: 4
-- Name: voi_voies_24; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_24 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2121 (class 1259 OID 16561962)
-- Dependencies: 4
-- Name: voi_voies_25; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_25 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2124 (class 1259 OID 16602357)
-- Dependencies: 4
-- Name: voi_voies_26; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_26 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2127 (class 1259 OID 16639199)
-- Dependencies: 4
-- Name: voi_voies_27; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_27 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2130 (class 1259 OID 16690265)
-- Dependencies: 4
-- Name: voi_voies_28; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_28 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2133 (class 1259 OID 16731399)
-- Dependencies: 4
-- Name: voi_voies_29; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_29 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2322 (class 1259 OID 19732219)
-- Dependencies: 4
-- Name: voi_voies_30; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_30 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2136 (class 1259 OID 16801856)
-- Dependencies: 4
-- Name: voi_voies_31; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_31 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2139 (class 1259 OID 16878605)
-- Dependencies: 4
-- Name: voi_voies_32; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_32 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2142 (class 1259 OID 16897516)
-- Dependencies: 4
-- Name: voi_voies_33; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_33 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2145 (class 1259 OID 16988075)
-- Dependencies: 4
-- Name: voi_voies_34; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_34 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2148 (class 1259 OID 17066481)
-- Dependencies: 4
-- Name: voi_voies_35; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_35 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2151 (class 1259 OID 17149362)
-- Dependencies: 4
-- Name: voi_voies_36; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_36 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2154 (class 1259 OID 17176442)
-- Dependencies: 4
-- Name: voi_voies_37; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_37 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2157 (class 1259 OID 17223230)
-- Dependencies: 4
-- Name: voi_voies_38; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_38 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2160 (class 1259 OID 17299409)
-- Dependencies: 4
-- Name: voi_voies_39; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_39 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2325 (class 1259 OID 19788337)
-- Dependencies: 4
-- Name: voi_voies_40; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_40 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2163 (class 1259 OID 17330055)
-- Dependencies: 4
-- Name: voi_voies_41; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_41 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2166 (class 1259 OID 17368268)
-- Dependencies: 4
-- Name: voi_voies_42; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_42 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2169 (class 1259 OID 17415691)
-- Dependencies: 4
-- Name: voi_voies_43; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_43 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2172 (class 1259 OID 17440399)
-- Dependencies: 4
-- Name: voi_voies_44; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_44 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2175 (class 1259 OID 17540151)
-- Dependencies: 4
-- Name: voi_voies_45; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_45 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2178 (class 1259 OID 17585785)
-- Dependencies: 4
-- Name: voi_voies_46; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_46 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2181 (class 1259 OID 17605966)
-- Dependencies: 4
-- Name: voi_voies_47; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_47 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2184 (class 1259 OID 17632462)
-- Dependencies: 4
-- Name: voi_voies_48; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_48 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2187 (class 1259 OID 17642827)
-- Dependencies: 4
-- Name: voi_voies_49; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_49 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2328 (class 1259 OID 19827998)
-- Dependencies: 4
-- Name: voi_voies_50; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_50 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2190 (class 1259 OID 17713353)
-- Dependencies: 4
-- Name: voi_voies_51; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_51 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2193 (class 1259 OID 17758984)
-- Dependencies: 4
-- Name: voi_voies_52; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_52 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2196 (class 1259 OID 17781654)
-- Dependencies: 4
-- Name: voi_voies_53; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_53 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2199 (class 1259 OID 17819078)
-- Dependencies: 4
-- Name: voi_voies_54; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_54 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2202 (class 1259 OID 17862736)
-- Dependencies: 4
-- Name: voi_voies_55; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_55 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2205 (class 1259 OID 17884426)
-- Dependencies: 4
-- Name: voi_voies_56; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_56 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2208 (class 1259 OID 17958799)
-- Dependencies: 4
-- Name: voi_voies_57; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_57 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2211 (class 1259 OID 18020224)
-- Dependencies: 4
-- Name: voi_voies_58; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_58 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2214 (class 1259 OID 18044449)
-- Dependencies: 4
-- Name: voi_voies_59; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_59 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2331 (class 1259 OID 19883555)
-- Dependencies: 4
-- Name: voi_voies_60; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_60 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2217 (class 1259 OID 18165896)
-- Dependencies: 4
-- Name: voi_voies_61; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_61 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2220 (class 1259 OID 18201344)
-- Dependencies: 4
-- Name: voi_voies_62; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_62 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2223 (class 1259 OID 18290907)
-- Dependencies: 4
-- Name: voi_voies_63; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_63 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2226 (class 1259 OID 18352089)
-- Dependencies: 4
-- Name: voi_voies_64; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_64 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2229 (class 1259 OID 18398476)
-- Dependencies: 4
-- Name: voi_voies_65; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_65 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2232 (class 1259 OID 18422501)
-- Dependencies: 4
-- Name: voi_voies_66; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_66 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2235 (class 1259 OID 18464181)
-- Dependencies: 4
-- Name: voi_voies_67; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_67 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2238 (class 1259 OID 18525782)
-- Dependencies: 4
-- Name: voi_voies_68; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_68 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2241 (class 1259 OID 18574037)
-- Dependencies: 4
-- Name: voi_voies_69; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_69 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2334 (class 1259 OID 19935839)
-- Dependencies: 4
-- Name: voi_voies_70; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_70 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2244 (class 1259 OID 18646225)
-- Dependencies: 4
-- Name: voi_voies_71; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_71 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2247 (class 1259 OID 18695080)
-- Dependencies: 4
-- Name: voi_voies_72; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_72 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2250 (class 1259 OID 18745714)
-- Dependencies: 4
-- Name: voi_voies_73; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_73 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2253 (class 1259 OID 18776006)
-- Dependencies: 4
-- Name: voi_voies_74; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_74 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2256 (class 1259 OID 18823303)
-- Dependencies: 4
-- Name: voi_voies_75; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_75 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2259 (class 1259 OID 18846216)
-- Dependencies: 4
-- Name: voi_voies_76; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_76 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2262 (class 1259 OID 18925641)
-- Dependencies: 4
-- Name: voi_voies_77; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_77 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2265 (class 1259 OID 19001305)
-- Dependencies: 4
-- Name: voi_voies_78; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_78 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2268 (class 1259 OID 19064579)
-- Dependencies: 4
-- Name: voi_voies_79; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_79 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2337 (class 1259 OID 19960930)
-- Dependencies: 4
-- Name: voi_voies_80; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_80 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2271 (class 1259 OID 19108759)
-- Dependencies: 4
-- Name: voi_voies_81; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_81 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2274 (class 1259 OID 19143552)
-- Dependencies: 4
-- Name: voi_voies_82; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_82 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2277 (class 1259 OID 19162645)
-- Dependencies: 4
-- Name: voi_voies_83; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_83 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2280 (class 1259 OID 19230887)
-- Dependencies: 4
-- Name: voi_voies_84; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_84 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2283 (class 1259 OID 19270910)
-- Dependencies: 4
-- Name: voi_voies_85; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_85 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2286 (class 1259 OID 19339937)
-- Dependencies: 4
-- Name: voi_voies_86; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_86 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2289 (class 1259 OID 19382721)
-- Dependencies: 4
-- Name: voi_voies_87; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_87 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2292 (class 1259 OID 19418885)
-- Dependencies: 4
-- Name: voi_voies_88; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_88 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2295 (class 1259 OID 19458556)
-- Dependencies: 4
-- Name: voi_voies_89; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_89 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2340 (class 1259 OID 20004488)
-- Dependencies: 4
-- Name: voi_voies_90; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_90 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2298 (class 1259 OID 19497146)
-- Dependencies: 4
-- Name: voi_voies_91; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_91 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2301 (class 1259 OID 19547703)
-- Dependencies: 4
-- Name: voi_voies_92; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_92 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2304 (class 1259 OID 19575782)
-- Dependencies: 4
-- Name: voi_voies_93; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_93 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2307 (class 1259 OID 19609486)
-- Dependencies: 4
-- Name: voi_voies_94; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_94 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2310 (class 1259 OID 19642363)
-- Dependencies: 4
-- Name: voi_voies_95; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_95 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);


--
-- TOC entry 2313 (class 1259 OID 19688025)
-- Dependencies: 4
-- Name: voi_voies_96; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_96 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);




-----------------------------------------------------------------------------------------------------------------
-- Section correspondant a la Corse et aux DOM et TOM
-----------------------------------------------------------------------------------------------------------------

--
-- TOC entry 2105 (class 1259 OID 16369725)
-- Name: tro_troncons_20_a_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_20_a_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_20_a_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2105 (class 1259 OID 16369725)
-- Name: tro_troncons_20_a_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_20_b_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_20_b_0','geometrie',2154,'GEOMETRY',2);


--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_971_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_971_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_971_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_972_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_972_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_972_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_973_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_973_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_973_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_974_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_974_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_974_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_975_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_975_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_975_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_976_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_976_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_976_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_984_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_984_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_984_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_986_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_986_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_986_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_987_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_987_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_987_0','geometrie',2154,'GEOMETRY',2);

--
-- TOC entry 2312 (class 1259 OID 19688018)
-- Name: tro_troncons_988_0; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE tro_troncons_988_0 (
    tro_id character varying(30),
    voi_id_droit character varying(30),
    voi_id_gauche character varying(30),
    tro_numero_debut_droit integer,
    tro_numero_debut_gauche integer,
    tro_numero_fin_droit integer,
    tro_numero_fin_gauche integer,
    tro_rep_debut_droit character(1),
    tro_rep_debut_gauche character(1),
    tro_rep_fin_droit character(1),
    tro_rep_fin_gauche character(1),
    tro_typ_adr character varying(30),
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
select addgeometrycolumn('public','tro_troncons_988_0','geometrie',2154,'GEOMETRY',2);

--
-- Name: vhi_voies_historisee_20_a; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_20_a (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_20_b; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_20_b (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_971; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_971 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_972; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_972 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_973; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_973 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_974; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_974 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_975; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_975 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_976; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_976 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_984; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_984 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_986; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_986 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_987; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_987 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: vhi_voies_historisee_988; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vhi_voies_historisee_988 (
    voi_id_precedent character varying(30),
    voi_id_suivant character varying(30),
    t0 timestamp without time zone
);

--
-- Name: voa_voies_ambigues_20_a; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_20_a (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_20_b; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_20_b (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_971; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_971 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_972; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_972 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_973; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_973 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_974; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_974 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_975; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_975 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_976; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_976 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_984; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_984 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_986; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_986 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_987; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_987 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voa_voies_ambigues_988; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voa_voies_ambigues_988 (
    voa_mot character varying(32),
    voa_lbl_pq character varying(255),
    voa_categorie_ambiguite character varying(255)
);

--
-- Name: voi_voies_20_a; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_20_a (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_20_b; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_20_b (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_971; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_971 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_972; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_972 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_973; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_973 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_974; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_974 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_975; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_975 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_976; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_976 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_984; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_984 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_986; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_986 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_987; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_987 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);

--
-- Name: voi_voies_988; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE voi_voies_988 (
    voi_id character varying(30),
    voi_code_fantoir character(10),
    voi_nom character varying(32),
    voi_nom_desab character varying(255),
    voi_nom_origine character varying(255),
    com_code_insee character(5),
    cdp_code_postal character(5),
    voi_type_de_voie character varying(255),
    voi_type_de_voie_pq character varying(255),
    voi_lbl character varying(255),
    voi_lbl_pq character varying(255),
    voi_lbl_sans_articles character varying(255),
    voi_lbl_sans_articles_pq character varying(255),
    voi_mot_determinant character varying(255),
    voi_mot_determinant_pq character varying(255),
    voi_min_numero integer,
    voi_max_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
