-- 
-- J. Moquet 29/10/2008
-- Permet de donner les droits en lecture sur les tables de JDONREF
-- Compatible JDONREF v2.1
--

-- la database
-- cette commande devrait marcher en théorie, mais ne marche pas.
--GRANT CONNECT ON DATABASE db_navteq_2005 TO jdonrefadmin;

-- le schéma
GRANT USAGE ON SCHEMA public TO jdonrefadmin;

-- Les tables postgis
GRANT SELECT ON geometry_columns TO jdonrefadmin;
GRANT SELECT ON spatial_ref_sys TO jdonrefadmin;

-- Les tables jdonref niveau france
GRANT SELECT ON cdp_codes_postaux TO jdonrefadmin;
GRANT INSERT ON cdp_codes_postaux TO jdonrefadmin;
GRANT UPDATE ON cdp_codes_postaux TO jdonrefadmin;
GRANT DELETE ON cdp_codes_postaux TO jdonrefadmin;

GRANT SELECT ON com_communes TO jdonrefadmin;
GRANT INSERT ON com_communes TO jdonrefadmin;
GRANT UPDATE ON com_communes TO jdonrefadmin;
GRANT DELETE ON com_communes TO jdonrefadmin;

GRANT SELECT ON dpt_departements TO jdonrefadmin;
GRANT INSERT ON dpt_departements TO jdonrefadmin;
GRANT UPDATE ON dpt_departements TO jdonrefadmin;
GRANT DELETE ON dpt_departements TO jdonrefadmin;

GRANT SELECT ON idv_id_voies TO jdonrefadmin;
GRANT INSERT ON idv_id_voies TO jdonrefadmin;
GRANT UPDATE ON idv_id_voies TO jdonrefadmin;
GRANT DELETE ON idv_id_voies TO jdonrefadmin;

GRANT SELECT ON ttr_tables_troncons TO jdonrefadmin;
GRANT INSERT ON ttr_tables_troncons TO jdonrefadmin;
GRANT UPDATE ON ttr_tables_troncons TO jdonrefadmin;
GRANT DELETE ON ttr_tables_troncons TO jdonrefadmin;

-- Les tables JDONREF départementales
GRANT SELECT ON tro_troncons_01_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_02_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_03_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_04_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_05_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_06_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_07_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_08_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_09_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_10_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_11_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_12_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_13_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_14_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_15_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_16_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_17_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_18_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_19_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_21_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_22_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_23_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_24_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_25_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_26_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_27_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_28_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_29_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_30_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_31_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_32_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_33_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_34_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_35_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_36_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_37_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_38_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_39_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_40_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_41_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_42_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_43_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_44_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_45_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_46_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_47_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_48_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_49_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_50_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_51_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_52_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_53_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_54_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_55_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_56_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_57_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_58_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_59_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_60_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_61_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_62_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_63_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_64_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_65_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_66_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_67_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_68_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_69_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_70_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_71_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_72_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_73_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_74_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_75_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_76_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_77_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_78_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_79_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_80_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_81_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_82_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_83_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_84_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_85_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_86_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_87_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_88_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_89_0  TO jdonrefadmin;

GRANT SELECT ON tro_troncons_90_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_91_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_92_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_93_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_94_0  TO jdonrefadmin;
GRANT SELECT ON tro_troncons_95_0  TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_01 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_02 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_03 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_04 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_05 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_06 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_07 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_08 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_09 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_10 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_11 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_12 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_13 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_14 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_15 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_16 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_17 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_18 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_19 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_21 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_22 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_23 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_24 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_25 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_26 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_27 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_28 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_29 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_30 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_31 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_32 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_33 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_34 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_35 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_36 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_37 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_38 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_39 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_40 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_41 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_42 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_43 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_44 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_45 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_46 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_47 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_48 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_49 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_50 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_51 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_52 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_53 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_54 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_55 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_56 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_57 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_58 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_59 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_60 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_61 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_62 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_63 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_64 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_65 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_66 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_67 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_68 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_69 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_70 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_71 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_72 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_73 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_74 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_75 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_76 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_77 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_78 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_79 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_80 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_81 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_82 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_83 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_84 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_85 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_86 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_87 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_88 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_89 TO jdonrefadmin;

GRANT SELECT ON vhi_voies_historisee_90 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_91 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_92 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_93 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_94 TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_95 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_01 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_02 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_03 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_04 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_05 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_06 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_07 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_08 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_09 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_10 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_11 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_12 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_13 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_14 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_15 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_16 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_17 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_18 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_19 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_21 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_22 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_23 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_24 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_25 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_26 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_27 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_28 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_29 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_30 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_31 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_32 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_33 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_34 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_35 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_36 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_37 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_38 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_39 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_40 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_41 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_42 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_43 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_44 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_45 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_46 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_47 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_48 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_49 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_50 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_51 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_52 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_53 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_54 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_55 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_56 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_57 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_58 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_59 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_60 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_61 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_62 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_63 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_64 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_65 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_66 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_67 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_68 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_69 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_70 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_71 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_72 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_73 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_74 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_75 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_76 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_77 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_78 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_79 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_80 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_81 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_82 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_83 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_84 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_85 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_86 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_87 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_88 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_89 TO jdonrefadmin;

GRANT SELECT ON voa_voies_ambigues_90 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_91 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_92 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_93 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_94 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_95 TO jdonrefadmin;

GRANT SELECT ON voi_voies_01 TO jdonrefadmin;
GRANT SELECT ON voi_voies_02 TO jdonrefadmin;
GRANT SELECT ON voi_voies_03 TO jdonrefadmin;
GRANT SELECT ON voi_voies_04 TO jdonrefadmin;
GRANT SELECT ON voi_voies_05 TO jdonrefadmin;
GRANT SELECT ON voi_voies_06 TO jdonrefadmin;
GRANT SELECT ON voi_voies_07 TO jdonrefadmin;
GRANT SELECT ON voi_voies_08 TO jdonrefadmin;
GRANT SELECT ON voi_voies_09 TO jdonrefadmin;

GRANT SELECT ON voi_voies_10 TO jdonrefadmin;
GRANT SELECT ON voi_voies_11 TO jdonrefadmin;
GRANT SELECT ON voi_voies_12 TO jdonrefadmin;
GRANT SELECT ON voi_voies_13 TO jdonrefadmin;
GRANT SELECT ON voi_voies_14 TO jdonrefadmin;
GRANT SELECT ON voi_voies_15 TO jdonrefadmin;
GRANT SELECT ON voi_voies_16 TO jdonrefadmin;
GRANT SELECT ON voi_voies_17 TO jdonrefadmin;
GRANT SELECT ON voi_voies_18 TO jdonrefadmin;
GRANT SELECT ON voi_voies_19 TO jdonrefadmin;

GRANT SELECT ON voi_voies_21 TO jdonrefadmin;
GRANT SELECT ON voi_voies_22 TO jdonrefadmin;
GRANT SELECT ON voi_voies_23 TO jdonrefadmin;
GRANT SELECT ON voi_voies_24 TO jdonrefadmin;
GRANT SELECT ON voi_voies_25 TO jdonrefadmin;
GRANT SELECT ON voi_voies_26 TO jdonrefadmin;
GRANT SELECT ON voi_voies_27 TO jdonrefadmin;
GRANT SELECT ON voi_voies_28 TO jdonrefadmin;
GRANT SELECT ON voi_voies_29 TO jdonrefadmin;

GRANT SELECT ON voi_voies_30 TO jdonrefadmin;
GRANT SELECT ON voi_voies_31 TO jdonrefadmin;
GRANT SELECT ON voi_voies_32 TO jdonrefadmin;
GRANT SELECT ON voi_voies_33 TO jdonrefadmin;
GRANT SELECT ON voi_voies_34 TO jdonrefadmin;
GRANT SELECT ON voi_voies_35 TO jdonrefadmin;
GRANT SELECT ON voi_voies_36 TO jdonrefadmin;
GRANT SELECT ON voi_voies_37 TO jdonrefadmin;
GRANT SELECT ON voi_voies_38 TO jdonrefadmin;
GRANT SELECT ON voi_voies_39 TO jdonrefadmin;

GRANT SELECT ON voi_voies_40 TO jdonrefadmin;
GRANT SELECT ON voi_voies_41 TO jdonrefadmin;
GRANT SELECT ON voi_voies_42 TO jdonrefadmin;
GRANT SELECT ON voi_voies_43 TO jdonrefadmin;
GRANT SELECT ON voi_voies_44 TO jdonrefadmin;
GRANT SELECT ON voi_voies_45 TO jdonrefadmin;
GRANT SELECT ON voi_voies_46 TO jdonrefadmin;
GRANT SELECT ON voi_voies_47 TO jdonrefadmin;
GRANT SELECT ON voi_voies_48 TO jdonrefadmin;
GRANT SELECT ON voi_voies_49 TO jdonrefadmin;

GRANT SELECT ON voi_voies_50 TO jdonrefadmin;
GRANT SELECT ON voi_voies_51 TO jdonrefadmin;
GRANT SELECT ON voi_voies_52 TO jdonrefadmin;
GRANT SELECT ON voi_voies_53 TO jdonrefadmin;
GRANT SELECT ON voi_voies_54 TO jdonrefadmin;
GRANT SELECT ON voi_voies_55 TO jdonrefadmin;
GRANT SELECT ON voi_voies_56 TO jdonrefadmin;
GRANT SELECT ON voi_voies_57 TO jdonrefadmin;
GRANT SELECT ON voi_voies_58 TO jdonrefadmin;
GRANT SELECT ON voi_voies_59 TO jdonrefadmin;

GRANT SELECT ON voi_voies_60 TO jdonrefadmin;
GRANT SELECT ON voi_voies_61 TO jdonrefadmin;
GRANT SELECT ON voi_voies_62 TO jdonrefadmin;
GRANT SELECT ON voi_voies_63 TO jdonrefadmin;
GRANT SELECT ON voi_voies_64 TO jdonrefadmin;
GRANT SELECT ON voi_voies_65 TO jdonrefadmin;
GRANT SELECT ON voi_voies_66 TO jdonrefadmin;
GRANT SELECT ON voi_voies_67 TO jdonrefadmin;
GRANT SELECT ON voi_voies_68 TO jdonrefadmin;
GRANT SELECT ON voi_voies_69 TO jdonrefadmin;

GRANT SELECT ON voi_voies_70 TO jdonrefadmin;
GRANT SELECT ON voi_voies_71 TO jdonrefadmin;
GRANT SELECT ON voi_voies_72 TO jdonrefadmin;
GRANT SELECT ON voi_voies_73 TO jdonrefadmin;
GRANT SELECT ON voi_voies_74 TO jdonrefadmin;
GRANT SELECT ON voi_voies_75 TO jdonrefadmin;
GRANT SELECT ON voi_voies_76 TO jdonrefadmin;
GRANT SELECT ON voi_voies_77 TO jdonrefadmin;
GRANT SELECT ON voi_voies_78 TO jdonrefadmin;
GRANT SELECT ON voi_voies_79 TO jdonrefadmin;

GRANT SELECT ON voi_voies_80 TO jdonrefadmin;
GRANT SELECT ON voi_voies_81 TO jdonrefadmin;
GRANT SELECT ON voi_voies_82 TO jdonrefadmin;
GRANT SELECT ON voi_voies_83 TO jdonrefadmin;
GRANT SELECT ON voi_voies_84 TO jdonrefadmin;
GRANT SELECT ON voi_voies_85 TO jdonrefadmin;
GRANT SELECT ON voi_voies_86 TO jdonrefadmin;
GRANT SELECT ON voi_voies_87 TO jdonrefadmin;
GRANT SELECT ON voi_voies_88 TO jdonrefadmin;
GRANT SELECT ON voi_voies_89 TO jdonrefadmin;

GRANT SELECT ON voi_voies_90 TO jdonrefadmin;
GRANT SELECT ON voi_voies_91 TO jdonrefadmin;
GRANT SELECT ON voi_voies_92 TO jdonrefadmin;
GRANT SELECT ON voi_voies_93 TO jdonrefadmin;
GRANT SELECT ON voi_voies_94 TO jdonrefadmin;
GRANT SELECT ON voi_voies_95 TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_01_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_02_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_03_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_04_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_05_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_06_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_07_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_08_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_09_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_10_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_11_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_12_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_13_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_14_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_15_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_16_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_17_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_18_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_19_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_21_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_22_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_23_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_24_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_25_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_26_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_27_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_28_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_29_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_30_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_31_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_32_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_33_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_34_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_35_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_36_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_37_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_38_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_39_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_40_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_41_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_42_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_43_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_44_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_45_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_46_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_47_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_48_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_49_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_50_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_51_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_52_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_53_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_54_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_55_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_56_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_57_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_58_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_59_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_60_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_61_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_62_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_63_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_64_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_65_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_66_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_67_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_68_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_69_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_70_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_71_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_72_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_73_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_74_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_75_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_76_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_77_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_78_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_79_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_80_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_81_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_82_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_83_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_84_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_85_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_86_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_87_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_88_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_89_0  TO jdonrefadmin;

GRANT UPDATE ON tro_troncons_90_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_91_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_92_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_93_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_94_0  TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_95_0  TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_01 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_02 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_03 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_04 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_05 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_06 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_07 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_08 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_09 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_10 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_11 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_12 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_13 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_14 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_15 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_16 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_17 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_18 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_19 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_21 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_22 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_23 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_24 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_25 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_26 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_27 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_28 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_29 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_30 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_31 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_32 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_33 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_34 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_35 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_36 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_37 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_38 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_39 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_40 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_41 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_42 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_43 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_44 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_45 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_46 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_47 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_48 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_49 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_50 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_51 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_52 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_53 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_54 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_55 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_56 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_57 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_58 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_59 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_60 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_61 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_62 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_63 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_64 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_65 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_66 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_67 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_68 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_69 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_70 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_71 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_72 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_73 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_74 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_75 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_76 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_77 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_78 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_79 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_80 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_81 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_82 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_83 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_84 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_85 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_86 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_87 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_88 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_89 TO jdonrefadmin;

GRANT UPDATE ON vhi_voies_historisee_90 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_91 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_92 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_93 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_94 TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_95 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_01 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_02 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_03 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_04 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_05 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_06 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_07 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_08 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_09 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_10 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_11 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_12 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_13 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_14 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_15 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_16 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_17 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_18 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_19 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_21 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_22 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_23 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_24 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_25 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_26 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_27 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_28 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_29 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_30 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_31 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_32 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_33 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_34 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_35 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_36 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_37 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_38 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_39 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_40 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_41 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_42 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_43 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_44 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_45 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_46 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_47 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_48 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_49 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_50 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_51 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_52 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_53 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_54 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_55 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_56 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_57 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_58 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_59 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_60 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_61 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_62 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_63 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_64 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_65 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_66 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_67 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_68 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_69 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_70 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_71 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_72 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_73 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_74 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_75 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_76 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_77 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_78 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_79 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_80 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_81 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_82 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_83 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_84 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_85 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_86 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_87 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_88 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_89 TO jdonrefadmin;

GRANT UPDATE ON voa_voies_ambigues_90 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_91 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_92 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_93 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_94 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_95 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_01 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_02 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_03 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_04 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_05 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_06 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_07 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_08 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_09 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_10 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_11 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_12 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_13 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_14 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_15 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_16 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_17 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_18 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_19 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_21 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_22 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_23 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_24 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_25 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_26 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_27 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_28 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_29 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_30 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_31 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_32 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_33 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_34 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_35 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_36 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_37 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_38 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_39 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_40 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_41 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_42 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_43 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_44 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_45 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_46 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_47 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_48 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_49 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_50 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_51 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_52 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_53 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_54 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_55 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_56 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_57 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_58 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_59 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_60 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_61 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_62 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_63 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_64 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_65 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_66 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_67 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_68 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_69 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_70 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_71 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_72 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_73 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_74 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_75 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_76 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_77 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_78 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_79 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_80 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_81 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_82 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_83 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_84 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_85 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_86 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_87 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_88 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_89 TO jdonrefadmin;

GRANT UPDATE ON voi_voies_90 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_91 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_92 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_93 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_94 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_95 TO jdonrefadmin;

GRANT DELETE ON tro_troncons_01_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_02_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_03_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_04_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_05_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_06_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_07_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_08_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_09_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_10_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_11_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_12_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_13_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_14_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_15_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_16_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_17_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_18_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_19_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_21_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_22_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_23_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_24_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_25_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_26_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_27_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_28_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_29_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_30_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_31_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_32_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_33_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_34_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_35_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_36_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_37_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_38_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_39_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_40_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_41_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_42_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_43_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_44_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_45_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_46_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_47_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_48_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_49_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_50_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_51_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_52_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_53_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_54_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_55_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_56_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_57_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_58_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_59_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_60_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_61_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_62_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_63_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_64_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_65_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_66_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_67_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_68_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_69_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_70_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_71_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_72_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_73_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_74_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_75_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_76_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_77_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_78_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_79_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_80_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_81_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_82_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_83_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_84_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_85_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_86_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_87_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_88_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_89_0  TO jdonrefadmin;

GRANT DELETE ON tro_troncons_90_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_91_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_92_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_93_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_94_0  TO jdonrefadmin;
GRANT DELETE ON tro_troncons_95_0  TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_01 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_02 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_03 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_04 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_05 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_06 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_07 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_08 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_09 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_10 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_11 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_12 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_13 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_14 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_15 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_16 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_17 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_18 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_19 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_21 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_22 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_23 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_24 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_25 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_26 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_27 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_28 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_29 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_30 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_31 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_32 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_33 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_34 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_35 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_36 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_37 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_38 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_39 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_40 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_41 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_42 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_43 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_44 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_45 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_46 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_47 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_48 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_49 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_50 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_51 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_52 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_53 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_54 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_55 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_56 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_57 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_58 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_59 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_60 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_61 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_62 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_63 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_64 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_65 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_66 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_67 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_68 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_69 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_70 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_71 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_72 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_73 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_74 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_75 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_76 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_77 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_78 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_79 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_80 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_81 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_82 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_83 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_84 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_85 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_86 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_87 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_88 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_89 TO jdonrefadmin;

GRANT DELETE ON vhi_voies_historisee_90 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_91 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_92 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_93 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_94 TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_95 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_01 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_02 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_03 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_04 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_05 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_06 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_07 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_08 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_09 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_10 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_11 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_12 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_13 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_14 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_15 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_16 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_17 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_18 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_19 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_21 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_22 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_23 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_24 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_25 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_26 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_27 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_28 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_29 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_30 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_31 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_32 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_33 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_34 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_35 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_36 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_37 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_38 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_39 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_40 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_41 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_42 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_43 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_44 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_45 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_46 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_47 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_48 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_49 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_50 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_51 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_52 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_53 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_54 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_55 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_56 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_57 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_58 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_59 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_60 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_61 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_62 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_63 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_64 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_65 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_66 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_67 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_68 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_69 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_70 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_71 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_72 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_73 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_74 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_75 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_76 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_77 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_78 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_79 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_80 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_81 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_82 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_83 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_84 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_85 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_86 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_87 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_88 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_89 TO jdonrefadmin;

GRANT DELETE ON voa_voies_ambigues_90 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_91 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_92 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_93 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_94 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_95 TO jdonrefadmin;

GRANT DELETE ON voi_voies_01 TO jdonrefadmin;
GRANT DELETE ON voi_voies_02 TO jdonrefadmin;
GRANT DELETE ON voi_voies_03 TO jdonrefadmin;
GRANT DELETE ON voi_voies_04 TO jdonrefadmin;
GRANT DELETE ON voi_voies_05 TO jdonrefadmin;
GRANT DELETE ON voi_voies_06 TO jdonrefadmin;
GRANT DELETE ON voi_voies_07 TO jdonrefadmin;
GRANT DELETE ON voi_voies_08 TO jdonrefadmin;
GRANT DELETE ON voi_voies_09 TO jdonrefadmin;

GRANT DELETE ON voi_voies_10 TO jdonrefadmin;
GRANT DELETE ON voi_voies_11 TO jdonrefadmin;
GRANT DELETE ON voi_voies_12 TO jdonrefadmin;
GRANT DELETE ON voi_voies_13 TO jdonrefadmin;
GRANT DELETE ON voi_voies_14 TO jdonrefadmin;
GRANT DELETE ON voi_voies_15 TO jdonrefadmin;
GRANT DELETE ON voi_voies_16 TO jdonrefadmin;
GRANT DELETE ON voi_voies_17 TO jdonrefadmin;
GRANT DELETE ON voi_voies_18 TO jdonrefadmin;
GRANT DELETE ON voi_voies_19 TO jdonrefadmin;

GRANT DELETE ON voi_voies_21 TO jdonrefadmin;
GRANT DELETE ON voi_voies_22 TO jdonrefadmin;
GRANT DELETE ON voi_voies_23 TO jdonrefadmin;
GRANT DELETE ON voi_voies_24 TO jdonrefadmin;
GRANT DELETE ON voi_voies_25 TO jdonrefadmin;
GRANT DELETE ON voi_voies_26 TO jdonrefadmin;
GRANT DELETE ON voi_voies_27 TO jdonrefadmin;
GRANT DELETE ON voi_voies_28 TO jdonrefadmin;
GRANT DELETE ON voi_voies_29 TO jdonrefadmin;

GRANT DELETE ON voi_voies_30 TO jdonrefadmin;
GRANT DELETE ON voi_voies_31 TO jdonrefadmin;
GRANT DELETE ON voi_voies_32 TO jdonrefadmin;
GRANT DELETE ON voi_voies_33 TO jdonrefadmin;
GRANT DELETE ON voi_voies_34 TO jdonrefadmin;
GRANT DELETE ON voi_voies_35 TO jdonrefadmin;
GRANT DELETE ON voi_voies_36 TO jdonrefadmin;
GRANT DELETE ON voi_voies_37 TO jdonrefadmin;
GRANT DELETE ON voi_voies_38 TO jdonrefadmin;
GRANT DELETE ON voi_voies_39 TO jdonrefadmin;

GRANT DELETE ON voi_voies_40 TO jdonrefadmin;
GRANT DELETE ON voi_voies_41 TO jdonrefadmin;
GRANT DELETE ON voi_voies_42 TO jdonrefadmin;
GRANT DELETE ON voi_voies_43 TO jdonrefadmin;
GRANT DELETE ON voi_voies_44 TO jdonrefadmin;
GRANT DELETE ON voi_voies_45 TO jdonrefadmin;
GRANT DELETE ON voi_voies_46 TO jdonrefadmin;
GRANT DELETE ON voi_voies_47 TO jdonrefadmin;
GRANT DELETE ON voi_voies_48 TO jdonrefadmin;
GRANT DELETE ON voi_voies_49 TO jdonrefadmin;

GRANT DELETE ON voi_voies_50 TO jdonrefadmin;
GRANT DELETE ON voi_voies_51 TO jdonrefadmin;
GRANT DELETE ON voi_voies_52 TO jdonrefadmin;
GRANT DELETE ON voi_voies_53 TO jdonrefadmin;
GRANT DELETE ON voi_voies_54 TO jdonrefadmin;
GRANT DELETE ON voi_voies_55 TO jdonrefadmin;
GRANT DELETE ON voi_voies_56 TO jdonrefadmin;
GRANT DELETE ON voi_voies_57 TO jdonrefadmin;
GRANT DELETE ON voi_voies_58 TO jdonrefadmin;
GRANT DELETE ON voi_voies_59 TO jdonrefadmin;

GRANT DELETE ON voi_voies_60 TO jdonrefadmin;
GRANT DELETE ON voi_voies_61 TO jdonrefadmin;
GRANT DELETE ON voi_voies_62 TO jdonrefadmin;
GRANT DELETE ON voi_voies_63 TO jdonrefadmin;
GRANT DELETE ON voi_voies_64 TO jdonrefadmin;
GRANT DELETE ON voi_voies_65 TO jdonrefadmin;
GRANT DELETE ON voi_voies_66 TO jdonrefadmin;
GRANT DELETE ON voi_voies_67 TO jdonrefadmin;
GRANT DELETE ON voi_voies_68 TO jdonrefadmin;
GRANT DELETE ON voi_voies_69 TO jdonrefadmin;

GRANT DELETE ON voi_voies_70 TO jdonrefadmin;
GRANT DELETE ON voi_voies_71 TO jdonrefadmin;
GRANT DELETE ON voi_voies_72 TO jdonrefadmin;
GRANT DELETE ON voi_voies_73 TO jdonrefadmin;
GRANT DELETE ON voi_voies_74 TO jdonrefadmin;
GRANT DELETE ON voi_voies_75 TO jdonrefadmin;
GRANT DELETE ON voi_voies_76 TO jdonrefadmin;
GRANT DELETE ON voi_voies_77 TO jdonrefadmin;
GRANT DELETE ON voi_voies_78 TO jdonrefadmin;
GRANT DELETE ON voi_voies_79 TO jdonrefadmin;

GRANT DELETE ON voi_voies_80 TO jdonrefadmin;
GRANT DELETE ON voi_voies_81 TO jdonrefadmin;
GRANT DELETE ON voi_voies_82 TO jdonrefadmin;
GRANT DELETE ON voi_voies_83 TO jdonrefadmin;
GRANT DELETE ON voi_voies_84 TO jdonrefadmin;
GRANT DELETE ON voi_voies_85 TO jdonrefadmin;
GRANT DELETE ON voi_voies_86 TO jdonrefadmin;
GRANT DELETE ON voi_voies_87 TO jdonrefadmin;
GRANT DELETE ON voi_voies_88 TO jdonrefadmin;
GRANT DELETE ON voi_voies_89 TO jdonrefadmin;

GRANT DELETE ON voi_voies_90 TO jdonrefadmin;
GRANT DELETE ON voi_voies_91 TO jdonrefadmin;
GRANT DELETE ON voi_voies_92 TO jdonrefadmin;
GRANT DELETE ON voi_voies_93 TO jdonrefadmin;
GRANT DELETE ON voi_voies_94 TO jdonrefadmin;
GRANT DELETE ON voi_voies_95 TO jdonrefadmin;

GRANT INSERT ON tro_troncons_01_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_02_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_03_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_04_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_05_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_06_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_07_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_08_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_09_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_10_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_11_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_12_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_13_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_14_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_15_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_16_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_17_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_18_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_19_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_21_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_22_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_23_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_24_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_25_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_26_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_27_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_28_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_29_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_30_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_31_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_32_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_33_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_34_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_35_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_36_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_37_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_38_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_39_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_40_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_41_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_42_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_43_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_44_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_45_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_46_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_47_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_48_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_49_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_50_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_51_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_52_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_53_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_54_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_55_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_56_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_57_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_58_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_59_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_60_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_61_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_62_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_63_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_64_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_65_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_66_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_67_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_68_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_69_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_70_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_71_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_72_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_73_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_74_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_75_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_76_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_77_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_78_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_79_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_80_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_81_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_82_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_83_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_84_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_85_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_86_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_87_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_88_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_89_0  TO jdonrefadmin;

GRANT INSERT ON tro_troncons_90_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_91_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_92_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_93_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_94_0  TO jdonrefadmin;
GRANT INSERT ON tro_troncons_95_0  TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_01 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_02 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_03 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_04 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_05 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_06 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_07 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_08 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_09 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_10 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_11 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_12 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_13 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_14 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_15 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_16 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_17 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_18 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_19 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_21 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_22 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_23 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_24 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_25 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_26 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_27 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_28 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_29 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_30 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_31 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_32 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_33 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_34 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_35 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_36 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_37 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_38 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_39 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_40 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_41 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_42 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_43 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_44 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_45 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_46 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_47 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_48 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_49 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_50 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_51 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_52 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_53 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_54 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_55 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_56 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_57 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_58 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_59 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_60 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_61 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_62 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_63 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_64 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_65 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_66 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_67 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_68 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_69 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_70 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_71 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_72 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_73 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_74 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_75 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_76 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_77 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_78 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_79 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_80 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_81 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_82 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_83 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_84 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_85 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_86 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_87 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_88 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_89 TO jdonrefadmin;

GRANT INSERT ON vhi_voies_historisee_90 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_91 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_92 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_93 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_94 TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_95 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_01 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_02 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_03 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_04 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_05 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_06 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_07 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_08 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_09 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_10 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_11 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_12 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_13 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_14 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_15 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_16 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_17 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_18 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_19 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_21 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_22 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_23 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_24 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_25 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_26 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_27 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_28 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_29 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_30 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_31 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_32 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_33 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_34 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_35 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_36 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_37 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_38 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_39 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_40 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_41 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_42 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_43 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_44 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_45 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_46 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_47 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_48 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_49 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_50 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_51 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_52 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_53 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_54 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_55 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_56 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_57 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_58 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_59 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_60 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_61 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_62 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_63 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_64 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_65 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_66 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_67 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_68 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_69 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_70 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_71 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_72 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_73 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_74 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_75 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_76 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_77 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_78 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_79 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_80 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_81 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_82 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_83 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_84 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_85 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_86 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_87 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_88 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_89 TO jdonrefadmin;

GRANT INSERT ON voa_voies_ambigues_90 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_91 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_92 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_93 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_94 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_95 TO jdonrefadmin;

GRANT INSERT ON voi_voies_01 TO jdonrefadmin;
GRANT INSERT ON voi_voies_02 TO jdonrefadmin;
GRANT INSERT ON voi_voies_03 TO jdonrefadmin;
GRANT INSERT ON voi_voies_04 TO jdonrefadmin;
GRANT INSERT ON voi_voies_05 TO jdonrefadmin;
GRANT INSERT ON voi_voies_06 TO jdonrefadmin;
GRANT INSERT ON voi_voies_07 TO jdonrefadmin;
GRANT INSERT ON voi_voies_08 TO jdonrefadmin;
GRANT INSERT ON voi_voies_09 TO jdonrefadmin;

GRANT INSERT ON voi_voies_10 TO jdonrefadmin;
GRANT INSERT ON voi_voies_11 TO jdonrefadmin;
GRANT INSERT ON voi_voies_12 TO jdonrefadmin;
GRANT INSERT ON voi_voies_13 TO jdonrefadmin;
GRANT INSERT ON voi_voies_14 TO jdonrefadmin;
GRANT INSERT ON voi_voies_15 TO jdonrefadmin;
GRANT INSERT ON voi_voies_16 TO jdonrefadmin;
GRANT INSERT ON voi_voies_17 TO jdonrefadmin;
GRANT INSERT ON voi_voies_18 TO jdonrefadmin;
GRANT INSERT ON voi_voies_19 TO jdonrefadmin;

GRANT INSERT ON voi_voies_21 TO jdonrefadmin;
GRANT INSERT ON voi_voies_22 TO jdonrefadmin;
GRANT INSERT ON voi_voies_23 TO jdonrefadmin;
GRANT INSERT ON voi_voies_24 TO jdonrefadmin;
GRANT INSERT ON voi_voies_25 TO jdonrefadmin;
GRANT INSERT ON voi_voies_26 TO jdonrefadmin;
GRANT INSERT ON voi_voies_27 TO jdonrefadmin;
GRANT INSERT ON voi_voies_28 TO jdonrefadmin;
GRANT INSERT ON voi_voies_29 TO jdonrefadmin;

GRANT INSERT ON voi_voies_30 TO jdonrefadmin;
GRANT INSERT ON voi_voies_31 TO jdonrefadmin;
GRANT INSERT ON voi_voies_32 TO jdonrefadmin;
GRANT INSERT ON voi_voies_33 TO jdonrefadmin;
GRANT INSERT ON voi_voies_34 TO jdonrefadmin;
GRANT INSERT ON voi_voies_35 TO jdonrefadmin;
GRANT INSERT ON voi_voies_36 TO jdonrefadmin;
GRANT INSERT ON voi_voies_37 TO jdonrefadmin;
GRANT INSERT ON voi_voies_38 TO jdonrefadmin;
GRANT INSERT ON voi_voies_39 TO jdonrefadmin;

GRANT INSERT ON voi_voies_40 TO jdonrefadmin;
GRANT INSERT ON voi_voies_41 TO jdonrefadmin;
GRANT INSERT ON voi_voies_42 TO jdonrefadmin;
GRANT INSERT ON voi_voies_43 TO jdonrefadmin;
GRANT INSERT ON voi_voies_44 TO jdonrefadmin;
GRANT INSERT ON voi_voies_45 TO jdonrefadmin;
GRANT INSERT ON voi_voies_46 TO jdonrefadmin;
GRANT INSERT ON voi_voies_47 TO jdonrefadmin;
GRANT INSERT ON voi_voies_48 TO jdonrefadmin;
GRANT INSERT ON voi_voies_49 TO jdonrefadmin;

GRANT INSERT ON voi_voies_50 TO jdonrefadmin;
GRANT INSERT ON voi_voies_51 TO jdonrefadmin;
GRANT INSERT ON voi_voies_52 TO jdonrefadmin;
GRANT INSERT ON voi_voies_53 TO jdonrefadmin;
GRANT INSERT ON voi_voies_54 TO jdonrefadmin;
GRANT INSERT ON voi_voies_55 TO jdonrefadmin;
GRANT INSERT ON voi_voies_56 TO jdonrefadmin;
GRANT INSERT ON voi_voies_57 TO jdonrefadmin;
GRANT INSERT ON voi_voies_58 TO jdonrefadmin;
GRANT INSERT ON voi_voies_59 TO jdonrefadmin;

GRANT INSERT ON voi_voies_60 TO jdonrefadmin;
GRANT INSERT ON voi_voies_61 TO jdonrefadmin;
GRANT INSERT ON voi_voies_62 TO jdonrefadmin;
GRANT INSERT ON voi_voies_63 TO jdonrefadmin;
GRANT INSERT ON voi_voies_64 TO jdonrefadmin;
GRANT INSERT ON voi_voies_65 TO jdonrefadmin;
GRANT INSERT ON voi_voies_66 TO jdonrefadmin;
GRANT INSERT ON voi_voies_67 TO jdonrefadmin;
GRANT INSERT ON voi_voies_68 TO jdonrefadmin;
GRANT INSERT ON voi_voies_69 TO jdonrefadmin;

GRANT INSERT ON voi_voies_70 TO jdonrefadmin;
GRANT INSERT ON voi_voies_71 TO jdonrefadmin;
GRANT INSERT ON voi_voies_72 TO jdonrefadmin;
GRANT INSERT ON voi_voies_73 TO jdonrefadmin;
GRANT INSERT ON voi_voies_74 TO jdonrefadmin;
GRANT INSERT ON voi_voies_75 TO jdonrefadmin;
GRANT INSERT ON voi_voies_76 TO jdonrefadmin;
GRANT INSERT ON voi_voies_77 TO jdonrefadmin;
GRANT INSERT ON voi_voies_78 TO jdonrefadmin;
GRANT INSERT ON voi_voies_79 TO jdonrefadmin;

GRANT INSERT ON voi_voies_80 TO jdonrefadmin;
GRANT INSERT ON voi_voies_81 TO jdonrefadmin;
GRANT INSERT ON voi_voies_82 TO jdonrefadmin;
GRANT INSERT ON voi_voies_83 TO jdonrefadmin;
GRANT INSERT ON voi_voies_84 TO jdonrefadmin;
GRANT INSERT ON voi_voies_85 TO jdonrefadmin;
GRANT INSERT ON voi_voies_86 TO jdonrefadmin;
GRANT INSERT ON voi_voies_87 TO jdonrefadmin;
GRANT INSERT ON voi_voies_88 TO jdonrefadmin;
GRANT INSERT ON voi_voies_89 TO jdonrefadmin;

GRANT INSERT ON voi_voies_90 TO jdonrefadmin;
GRANT INSERT ON voi_voies_91 TO jdonrefadmin;
GRANT INSERT ON voi_voies_92 TO jdonrefadmin;
GRANT INSERT ON voi_voies_93 TO jdonrefadmin;
GRANT INSERT ON voi_voies_94 TO jdonrefadmin;
GRANT INSERT ON voi_voies_95 TO jdonrefadmin;


-----------------------------------------------------------------------------------------------------------------
-- Section correspondant a la Corse et aux DOM et TOM
-----------------------------------------------------------------------------------------------------------------
GRANT SELECT ON tro_troncons_20_a_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_20_a TO jdonrefadmin;
GRANT SELECT ON voi_voies_20_a TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_20_a TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_20_a_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_20_a TO jdonrefadmin;
GRANT UPDATE ON voi_voies_20_a TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_20_a TO jdonrefadmin;
GRANT INSERT ON tro_troncons_20_a_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_20_a TO jdonrefadmin;
GRANT INSERT ON voi_voies_20_a TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_20_a TO jdonrefadmin;
GRANT DELETE ON tro_troncons_20_a_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_20_a TO jdonrefadmin;
GRANT DELETE ON voi_voies_20_a TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_20_a TO jdonrefadmin;

GRANT SELECT ON tro_troncons_20_b_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_20_b TO jdonrefadmin;
GRANT SELECT ON voi_voies_20_b TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_20_b TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_20_b_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_20_b TO jdonrefadmin;
GRANT UPDATE ON voi_voies_20_b TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_20_b TO jdonrefadmin;
GRANT INSERT ON tro_troncons_20_b_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_20_b TO jdonrefadmin;
GRANT INSERT ON voi_voies_20_b TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_20_b TO jdonrefadmin;
GRANT DELETE ON tro_troncons_20_b_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_20_b TO jdonrefadmin;
GRANT DELETE ON voi_voies_20_b TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_20_b TO jdonrefadmin;

GRANT SELECT ON tro_troncons_971_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_971 TO jdonrefadmin;
GRANT SELECT ON voi_voies_971 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_971 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_971_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_971 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_971 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_971 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_971_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_971 TO jdonrefadmin;
GRANT INSERT ON voi_voies_971 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_971 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_971_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_971 TO jdonrefadmin;
GRANT DELETE ON voi_voies_971 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_971 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_972_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_972 TO jdonrefadmin;
GRANT SELECT ON voi_voies_972 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_972 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_972_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_972 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_972 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_972 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_972_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_972 TO jdonrefadmin;
GRANT INSERT ON voi_voies_972 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_972 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_972_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_972 TO jdonrefadmin;
GRANT DELETE ON voi_voies_972 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_972 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_973_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_973 TO jdonrefadmin;
GRANT SELECT ON voi_voies_973 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_973 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_973_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_973 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_973 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_973 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_973_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_973 TO jdonrefadmin;
GRANT INSERT ON voi_voies_973 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_973 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_973_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_973 TO jdonrefadmin;
GRANT DELETE ON voi_voies_973 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_973 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_974_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_974 TO jdonrefadmin;
GRANT SELECT ON voi_voies_974 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_974 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_974_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_974 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_974 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_974 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_974_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_974 TO jdonrefadmin;
GRANT INSERT ON voi_voies_974 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_974 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_974_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_974 TO jdonrefadmin;
GRANT DELETE ON voi_voies_974 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_974 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_975_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_975 TO jdonrefadmin;
GRANT SELECT ON voi_voies_975 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_975 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_975_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_975 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_975 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_975 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_975_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_975 TO jdonrefadmin;
GRANT INSERT ON voi_voies_975 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_975 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_975_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_975 TO jdonrefadmin;
GRANT DELETE ON voi_voies_975 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_975 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_976_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_976 TO jdonrefadmin;
GRANT SELECT ON voi_voies_976 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_976 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_976_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_976 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_976 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_976 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_976_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_976 TO jdonrefadmin;
GRANT INSERT ON voi_voies_976 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_976 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_976_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_976 TO jdonrefadmin;
GRANT DELETE ON voi_voies_976 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_976 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_984_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_984 TO jdonrefadmin;
GRANT SELECT ON voi_voies_984 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_984 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_984_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_984 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_984 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_984 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_984_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_984 TO jdonrefadmin;
GRANT INSERT ON voi_voies_984 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_984 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_984_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_984 TO jdonrefadmin;
GRANT DELETE ON voi_voies_984 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_984 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_986_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_986 TO jdonrefadmin;
GRANT SELECT ON voi_voies_986 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_986 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_986_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_986 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_986 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_986 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_986_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_986 TO jdonrefadmin;
GRANT INSERT ON voi_voies_986 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_986 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_986_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_986 TO jdonrefadmin;
GRANT DELETE ON voi_voies_986 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_986 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_987_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_987 TO jdonrefadmin;
GRANT SELECT ON voi_voies_987 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_987 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_987_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_987 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_987 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_987 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_987_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_987 TO jdonrefadmin;
GRANT INSERT ON voi_voies_987 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_987 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_987_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_987 TO jdonrefadmin;
GRANT DELETE ON voi_voies_987 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_987 TO jdonrefadmin;

GRANT SELECT ON tro_troncons_988_0  TO jdonrefadmin;
GRANT SELECT ON vhi_voies_historisee_988 TO jdonrefadmin;
GRANT SELECT ON voi_voies_988 TO jdonrefadmin;
GRANT SELECT ON voa_voies_ambigues_988 TO jdonrefadmin;
GRANT UPDATE ON tro_troncons_988_0  TO jdonrefadmin;
GRANT UPDATE ON vhi_voies_historisee_988 TO jdonrefadmin;
GRANT UPDATE ON voi_voies_988 TO jdonrefadmin;
GRANT UPDATE ON voa_voies_ambigues_988 TO jdonrefadmin;
GRANT INSERT ON tro_troncons_988_0  TO jdonrefadmin;
GRANT INSERT ON vhi_voies_historisee_988 TO jdonrefadmin;
GRANT INSERT ON voi_voies_988 TO jdonrefadmin;
GRANT INSERT ON voa_voies_ambigues_988 TO jdonrefadmin;
GRANT DELETE ON tro_troncons_988_0  TO jdonrefadmin;
GRANT DELETE ON vhi_voies_historisee_988 TO jdonrefadmin;
GRANT DELETE ON voi_voies_988 TO jdonrefadmin;
GRANT DELETE ON voa_voies_ambigues_988 TO jdonrefadmin;
