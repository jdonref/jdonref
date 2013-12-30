--
-- Julien Moquet. Licence Cecill v2. Février 2011.
--
-- Permet de créer les tables de point adresse pour JDONREF.
--
-- L'extension postgis doit être présente, ainsi que les tables geometrycolumns et spatial_ref_sys
-- sur la base cible.

CREATE TABLE adr_adresses_01 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_01','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_02 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_02','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_03 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_03','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_04 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_04','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_05 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_05','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_06 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_06','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_07 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_07','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_08 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_08','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_09 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_09','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_10 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_10','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_11 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_11','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_12 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_12','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_13 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_13','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_14 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_14','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_15 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_15','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_16 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_16','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_17 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_17','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_18 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_18','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_19 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_19','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_21 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_21','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_22 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_22','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_23 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_23','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_24 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_24','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_25 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_25','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_26 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_26','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_27 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_27','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_28 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_28','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_29 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_29','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_30 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_30','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_31 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_31','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_32 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_32','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_33 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_33','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_34 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_34','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_35 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_35','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_36 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_36','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_37 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_37','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_38 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_38','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_39 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_39','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_40 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_40','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_41 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_41','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_42 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_42','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_43 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_43','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_44 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_44','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_45 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_45','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_46 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_46','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_47 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_47','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_48 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_48','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_49 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_49','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_50 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_50','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_51 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_51','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_52 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_52','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_53 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_53','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_54 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_54','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_55 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_55','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_56 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_56','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_57 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_57','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_58 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_58','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_59 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_59','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_60 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_60','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_61 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_61','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_62 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_62','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_63 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_63','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_64 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_64','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_65 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_65','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_66 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_66','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_67 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_67','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_68 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_68','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_69 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_69','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_70 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_70','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_71 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_71','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_72 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_72','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_73 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_73','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_74 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_74','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_75 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_75','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_76 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_76','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_77 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_77','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_78 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_78','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_79 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_79','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_80 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_80','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_81 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_81','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_82 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_82','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_83 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_83','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_84 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_84','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_85 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_85','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_86 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_86','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_87 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_87','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_88 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_88','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_89 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_89','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_90 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_90','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_91 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_91','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_92 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_92','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_93 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_93','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_94 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_94','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_95 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_95','geometrie',2154,'GEOMETRY',2);
CREATE TABLE adr_adresses_96 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_96','geometrie',2154,'GEOMETRY',2);



-----------------------------------------------------------------------------------------------------------------
-- Section correspondant a la Corse et aux DOM et TOM
-----------------------------------------------------------------------------------------------------------------
CREATE TABLE adr_adresses_20_a (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_20_a','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_20_b (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_20_b','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_971 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_971','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_972 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_972','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_973 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_973','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_974 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_974','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_975 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_975','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_976 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_976','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_984 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_984','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_986 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_986','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_987 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_987','geometrie',2154,'GEOMETRY',2);

CREATE TABLE adr_adresses_988 (
    adr_id character varying(30),
    adr_rep character(1),
    voi_id character varying(30),
    adr_numero integer,
    t0 timestamp without time zone,
    t1 timestamp without time zone
);
Select addgeometrycolumn('public','adr_adresses_988','geometrie',2154,'GEOMETRY',2);
