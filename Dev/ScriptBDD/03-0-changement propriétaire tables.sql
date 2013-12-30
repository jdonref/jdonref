--
-- J. Moquet 29/10/2008
-- Permet de changer les propriétaires des tables de JDONREF (ainsi que la database).
-- compatible JDONREF v2.1
--

-- la database
ALTER DATABASE db_navteq_2005 OWNER TO jdonrefadmin;

-- le schéma
ALTER SCHEMA public OWNER TO jdonrefadmin;

-- les tables postgis
ALTER TABLE geometry_columns OWNER TO jdonrefadmin;
ALTER TABLE spatial_ref_sys OWNER TO jdonrefadmin;

-- les tables jdonref
ALTER TABLE cdp_codes_postaux OWNER TO jdonrefadmin;
ALTER TABLE com_communes OWNER TO jdonrefadmin;
ALTER TABLE dpt_departements OWNER TO jdonrefadmin;
ALTER TABLE idv_id_voies OWNER TO jdonrefadmin;
ALTER TABLE ttr_tables_troncons OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_01_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_02_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_03_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_04_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_05_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_06_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_07_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_08_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_09_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_10_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_11_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_12_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_13_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_14_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_15_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_16_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_17_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_18_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_19_0 OWNER TO jdonrefadmin;

--ALTER TABLE tro_troncons_20_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_21_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_22_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_23_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_24_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_25_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_26_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_27_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_28_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_29_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_30_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_31_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_32_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_33_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_34_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_35_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_36_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_37_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_38_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_39_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_40_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_41_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_42_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_43_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_44_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_45_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_46_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_47_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_48_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_49_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_50_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_51_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_52_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_53_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_54_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_55_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_56_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_57_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_58_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_59_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_60_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_61_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_62_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_63_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_64_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_65_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_66_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_67_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_68_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_69_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_70_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_71_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_72_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_73_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_74_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_75_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_76_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_77_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_78_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_79_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_80_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_81_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_82_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_83_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_84_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_85_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_86_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_87_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_88_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_89_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_90_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_91_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_92_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_93_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_94_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_95_0 OWNER TO jdonrefadmin;
--ALTER TABLE tro_troncons_96_0 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_01 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_02 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_03 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_04 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_05 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_06 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_07 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_08 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_09 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_10 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_11 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_12 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_13 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_14 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_15 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_16 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_17 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_18 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_19 OWNER TO jdonrefadmin;

--ALTER TABLE vhi_voies_historisee_20 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_21 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_22 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_23 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_24 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_25 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_26 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_27 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_28 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_29 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_30 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_31 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_32 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_33 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_34 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_35 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_36 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_37 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_38 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_39 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_40 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_41 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_42 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_43 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_44 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_45 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_46 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_47 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_48 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_49 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_50 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_51 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_52 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_53 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_54 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_55 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_56 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_57 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_58 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_59 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_60 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_61 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_62 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_63 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_64 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_65 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_66 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_67 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_68 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_69 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_70 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_71 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_72 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_73 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_74 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_75 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_76 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_77 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_78 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_79 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_80 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_81 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_82 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_83 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_84 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_85 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_86 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_87 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_88 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_89 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_90 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_91 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_92 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_93 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_94 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_95 OWNER TO jdonrefadmin;
--ALTER TABLE vhi_voies_historisee_96 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_01 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_02 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_03 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_04 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_05 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_06 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_07 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_08 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_09 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_10 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_11 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_12 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_13 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_14 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_15 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_16 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_17 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_18 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_19 OWNER TO jdonrefadmin;

--ALTER TABLE voa_voies_ambigues_20 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_21 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_22 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_23 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_24 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_25 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_26 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_27 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_28 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_29 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_30 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_31 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_32 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_33 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_34 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_35 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_36 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_37 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_38 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_39 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_40 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_41 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_42 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_43 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_44 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_45 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_46 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_47 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_48 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_49 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_50 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_51 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_52 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_53 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_54 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_55 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_56 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_57 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_58 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_59 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_60 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_61 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_62 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_63 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_64 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_65 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_66 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_67 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_68 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_69 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_70 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_71 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_72 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_73 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_74 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_75 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_76 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_77 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_78 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_79 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_80 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_81 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_82 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_83 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_84 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_85 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_86 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_87 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_88 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_89 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_90 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_91 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_92 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_93 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_94 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_95 OWNER TO jdonrefadmin;
--ALTER TABLE voa_voies_ambigues_96 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_971 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_972 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_973 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_974 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_975 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_976 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_984 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_986 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_987 OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_988 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_01 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_02 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_03 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_04 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_05 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_06 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_07 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_08 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_09 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_10 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_11 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_12 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_13 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_14 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_15 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_16 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_17 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_18 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_19 OWNER TO jdonrefadmin;

--ALTER TABLE voi_voies_20 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_21 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_22 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_23 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_24 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_25 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_26 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_27 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_28 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_29 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_30 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_31 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_32 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_33 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_34 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_35 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_36 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_37 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_38 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_39 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_40 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_41 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_42 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_43 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_44 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_45 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_46 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_47 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_48 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_49 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_50 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_51 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_52 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_53 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_54 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_55 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_56 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_57 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_58 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_59 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_60 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_61 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_62 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_63 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_64 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_65 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_66 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_67 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_68 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_69 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_70 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_71 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_72 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_73 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_74 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_75 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_76 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_77 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_78 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_79 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_80 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_81 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_82 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_83 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_84 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_85 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_86 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_87 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_88 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_89 OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_90 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_91 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_92 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_93 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_94 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_95 OWNER TO jdonrefadmin;
--ALTER TABLE voi_voies_96 OWNER TO jdonrefadmin;


-----------------------------------------------------------------------------------------------------------------
-- Section correspondant a la Corse et aux DOM et TOM
-----------------------------------------------------------------------------------------------------------------
ALTER TABLE tro_troncons_20_a_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_20_b_0 OWNER TO jdonrefadmin;

ALTER TABLE tro_troncons_971_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_972_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_973_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_974_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_975_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_976_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_984_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_986_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_987_0 OWNER TO jdonrefadmin;
ALTER TABLE tro_troncons_988_0 OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_20_a OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_20_b OWNER TO jdonrefadmin;

ALTER TABLE vhi_voies_historisee_971 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_972 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_973 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_974 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_975 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_976 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_984 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_986 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_987 OWNER TO jdonrefadmin;
ALTER TABLE vhi_voies_historisee_988 OWNER TO jdonrefadmin;

ALTER TABLE voa_voies_ambigues_20_a OWNER TO jdonrefadmin;
ALTER TABLE voa_voies_ambigues_20_b OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_20_a OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_20_b OWNER TO jdonrefadmin;

ALTER TABLE voi_voies_971 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_972 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_973 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_974 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_975 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_976 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_984 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_986 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_987 OWNER TO jdonrefadmin;
ALTER TABLE voi_voies_988 OWNER TO jdonrefadmin;

