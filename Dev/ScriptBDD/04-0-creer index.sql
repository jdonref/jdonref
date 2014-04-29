--
-- Julien Moquet. Licence Cecill v2. Février 2011.
--
-- Permet de créer les index utiles à JDONREFv2.
-- A noter que d'autres index peuvent être mis en place pour optimiser la mise à jour du référentiel.
--
-- L'extension postgis doit être présente (index GIST)

-- tables communes à tous les départements.
CREATE INDEX cdp_code_departement ON cdp_codes_postaux USING btree (dpt_code_departement);
CREATE INDEX cdp_code_insee ON cdp_codes_postaux USING btree (com_code_insee);
CREATE INDEX cdp_code_postal ON cdp_codes_postaux USING btree (cdp_code_postal);

CREATE INDEX com_noms_code_dpt ON com_communes USING btree (dpt_code_departement);
CREATE INDEX com_noms_code_insee ON com_communes USING btree (com_code_insee);
CREATE INDEX com_noms_nom ON com_communes USING btree (com_nom);
CREATE INDEX com_noms_geometrie ON com_communes USING gist (geometrie);

CREATE INDEX dpt_code_dpt ON dpt_departements USING btree (dpt_code_departement);
CREATE INDEX dpt_geometrie ON dpt_departements USING gist (geometrie);

CREATE INDEX idv_voi_id ON idv_id_voies USING btree (voi_id);

-- troncons
CREATE INDEX tro_troncons_01_0_tro_id ON tro_troncons_01_0 USING btree (tro_id);
CREATE INDEX tro_troncons_01_0_voi_id_droit ON tro_troncons_01_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_01_0_voi_id_gauche ON tro_troncons_01_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_01_0_geometrie ON tro_troncons_01_0 USING gist (geometrie);

CREATE INDEX tro_troncons_02_0_tro_id ON tro_troncons_02_0 USING btree (tro_id);
CREATE INDEX tro_troncons_02_0_voi_id_droit ON tro_troncons_02_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_02_0_voi_id_gauche ON tro_troncons_02_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_02_0_geometrie ON tro_troncons_02_0 USING gist (geometrie);

CREATE INDEX tro_troncons_03_0_tro_id ON tro_troncons_03_0 USING btree (tro_id);
CREATE INDEX tro_troncons_03_0_voi_id_droit ON tro_troncons_03_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_03_0_voi_id_gauche ON tro_troncons_03_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_03_0_geometrie ON tro_troncons_03_0 USING gist (geometrie);

CREATE INDEX tro_troncons_04_0_tro_id ON tro_troncons_04_0 USING btree (tro_id);
CREATE INDEX tro_troncons_04_0_voi_id_droit ON tro_troncons_04_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_04_0_voi_id_gauche ON tro_troncons_04_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_04_0_geometrie ON tro_troncons_04_0 USING gist (geometrie);

CREATE INDEX tro_troncons_05_0_tro_id ON tro_troncons_05_0 USING btree (tro_id);
CREATE INDEX tro_troncons_05_0_voi_id_droit ON tro_troncons_05_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_05_0_voi_id_gauche ON tro_troncons_05_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_05_0_geometrie ON tro_troncons_05_0 USING gist (geometrie);

CREATE INDEX tro_troncons_06_0_tro_id ON tro_troncons_06_0 USING btree (tro_id);
CREATE INDEX tro_troncons_06_0_voi_id_droit ON tro_troncons_06_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_06_0_voi_id_gauche ON tro_troncons_06_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_06_0_geometrie ON tro_troncons_06_0 USING gist (geometrie);

CREATE INDEX tro_troncons_07_0_tro_id ON tro_troncons_07_0 USING btree (tro_id);
CREATE INDEX tro_troncons_07_0_voi_id_droit ON tro_troncons_07_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_07_0_voi_id_gauche ON tro_troncons_07_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_07_0_geometrie ON tro_troncons_07_0 USING gist (geometrie);

CREATE INDEX tro_troncons_08_0_tro_id ON tro_troncons_08_0 USING btree (tro_id);
CREATE INDEX tro_troncons_08_0_voi_id_droit ON tro_troncons_08_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_08_0_voi_id_gauche ON tro_troncons_08_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_08_0_geometrie ON tro_troncons_08_0 USING gist (geometrie);

CREATE INDEX tro_troncons_09_0_tro_id ON tro_troncons_09_0 USING btree (tro_id);
CREATE INDEX tro_troncons_09_0_voi_id_droit ON tro_troncons_09_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_09_0_voi_id_gauche ON tro_troncons_09_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_09_0_geometrie ON tro_troncons_09_0 USING gist (geometrie);

CREATE INDEX tro_troncons_10_0_tro_id ON tro_troncons_10_0 USING btree (tro_id);
CREATE INDEX tro_troncons_10_0_voi_id_droit ON tro_troncons_10_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_10_0_voi_id_gauche ON tro_troncons_10_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_10_0_geometrie ON tro_troncons_10_0 USING gist (geometrie);

CREATE INDEX tro_troncons_11_0_tro_id ON tro_troncons_11_0 USING btree (tro_id);
CREATE INDEX tro_troncons_11_0_voi_id_droit ON tro_troncons_11_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_11_0_voi_id_gauche ON tro_troncons_11_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_11_0_geometrie ON tro_troncons_11_0 USING gist (geometrie);

CREATE INDEX tro_troncons_12_0_tro_id ON tro_troncons_12_0 USING btree (tro_id);
CREATE INDEX tro_troncons_12_0_voi_id_droit ON tro_troncons_12_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_12_0_voi_id_gauche ON tro_troncons_12_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_12_0_geometrie ON tro_troncons_12_0 USING gist (geometrie);

CREATE INDEX tro_troncons_13_0_tro_id ON tro_troncons_13_0 USING btree (tro_id);
CREATE INDEX tro_troncons_13_0_voi_id_droit ON tro_troncons_13_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_13_0_voi_id_gauche ON tro_troncons_13_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_13_0_geometrie ON tro_troncons_13_0 USING gist (geometrie);

CREATE INDEX tro_troncons_14_0_tro_id ON tro_troncons_14_0 USING btree (tro_id);
CREATE INDEX tro_troncons_14_0_voi_id_droit ON tro_troncons_14_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_14_0_voi_id_gauche ON tro_troncons_14_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_14_0_geometrie ON tro_troncons_14_0 USING gist (geometrie);

CREATE INDEX tro_troncons_15_0_tro_id ON tro_troncons_15_0 USING btree (tro_id);
CREATE INDEX tro_troncons_15_0_voi_id_droit ON tro_troncons_15_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_15_0_voi_id_gauche ON tro_troncons_15_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_15_0_geometrie ON tro_troncons_15_0 USING gist (geometrie);

CREATE INDEX tro_troncons_16_0_tro_id ON tro_troncons_16_0 USING btree (tro_id);
CREATE INDEX tro_troncons_16_0_voi_id_droit ON tro_troncons_16_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_16_0_voi_id_gauche ON tro_troncons_16_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_16_0_geometrie ON tro_troncons_16_0 USING gist (geometrie);

CREATE INDEX tro_troncons_17_0_tro_id ON tro_troncons_17_0 USING btree (tro_id);
CREATE INDEX tro_troncons_17_0_voi_id_droit ON tro_troncons_17_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_17_0_voi_id_gauche ON tro_troncons_17_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_17_0_geometrie ON tro_troncons_17_0 USING gist (geometrie);

CREATE INDEX tro_troncons_18_0_tro_id ON tro_troncons_18_0 USING btree (tro_id);
CREATE INDEX tro_troncons_18_0_voi_id_droit ON tro_troncons_18_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_18_0_voi_id_gauche ON tro_troncons_18_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_18_0_geometrie ON tro_troncons_18_0 USING gist (geometrie);

CREATE INDEX tro_troncons_19_0_tro_id ON tro_troncons_19_0 USING btree (tro_id);
CREATE INDEX tro_troncons_19_0_voi_id_droit ON tro_troncons_19_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_19_0_voi_id_gauche ON tro_troncons_19_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_19_0_geometrie ON tro_troncons_19_0 USING gist (geometrie);

CREATE INDEX tro_troncons_21_0_tro_id ON tro_troncons_21_0 USING btree (tro_id);
CREATE INDEX tro_troncons_21_0_voi_id_droit ON tro_troncons_21_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_21_0_voi_id_gauche ON tro_troncons_21_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_21_0_geometrie ON tro_troncons_21_0 USING gist (geometrie);

CREATE INDEX tro_troncons_22_0_tro_id ON tro_troncons_22_0 USING btree (tro_id);
CREATE INDEX tro_troncons_22_0_voi_id_droit ON tro_troncons_22_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_22_0_voi_id_gauche ON tro_troncons_22_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_22_0_geometrie ON tro_troncons_22_0 USING gist (geometrie);

CREATE INDEX tro_troncons_23_0_tro_id ON tro_troncons_23_0 USING btree (tro_id);
CREATE INDEX tro_troncons_23_0_voi_id_droit ON tro_troncons_23_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_23_0_voi_id_gauche ON tro_troncons_23_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_23_0_geometrie ON tro_troncons_23_0 USING gist (geometrie);

CREATE INDEX tro_troncons_24_0_tro_id ON tro_troncons_24_0 USING btree (tro_id);
CREATE INDEX tro_troncons_24_0_voi_id_droit ON tro_troncons_24_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_24_0_voi_id_gauche ON tro_troncons_24_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_24_0_geometrie ON tro_troncons_24_0 USING gist (geometrie);

CREATE INDEX tro_troncons_25_0_tro_id ON tro_troncons_25_0 USING btree (tro_id);
CREATE INDEX tro_troncons_25_0_voi_id_droit ON tro_troncons_25_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_25_0_voi_id_gauche ON tro_troncons_25_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_25_0_geometrie ON tro_troncons_25_0 USING gist (geometrie);

CREATE INDEX tro_troncons_26_0_tro_id ON tro_troncons_26_0 USING btree (tro_id);
CREATE INDEX tro_troncons_26_0_voi_id_droit ON tro_troncons_26_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_26_0_voi_id_gauche ON tro_troncons_26_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_26_0_geometrie ON tro_troncons_26_0 USING gist (geometrie);

CREATE INDEX tro_troncons_27_0_tro_id ON tro_troncons_27_0 USING btree (tro_id);
CREATE INDEX tro_troncons_27_0_voi_id_droit ON tro_troncons_27_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_27_0_voi_id_gauche ON tro_troncons_27_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_27_0_geometrie ON tro_troncons_27_0 USING gist (geometrie);

CREATE INDEX tro_troncons_28_0_tro_id ON tro_troncons_28_0 USING btree (tro_id);
CREATE INDEX tro_troncons_28_0_voi_id_droit ON tro_troncons_28_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_28_0_voi_id_gauche ON tro_troncons_28_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_28_0_geometrie ON tro_troncons_28_0 USING gist (geometrie);

CREATE INDEX tro_troncons_29_0_tro_id ON tro_troncons_29_0 USING btree (tro_id);
CREATE INDEX tro_troncons_29_0_voi_id_droit ON tro_troncons_29_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_29_0_voi_id_gauche ON tro_troncons_29_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_29_0_geometrie ON tro_troncons_29_0 USING gist (geometrie);

CREATE INDEX tro_troncons_30_0_tro_id ON tro_troncons_30_0 USING btree (tro_id);
CREATE INDEX tro_troncons_30_0_voi_id_droit ON tro_troncons_30_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_30_0_voi_id_gauche ON tro_troncons_30_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_30_0_geometrie ON tro_troncons_30_0 USING gist (geometrie);

CREATE INDEX tro_troncons_31_0_tro_id ON tro_troncons_31_0 USING btree (tro_id);
CREATE INDEX tro_troncons_31_0_voi_id_droit ON tro_troncons_31_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_31_0_voi_id_gauche ON tro_troncons_31_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_31_0_geometrie ON tro_troncons_31_0 USING gist (geometrie);

CREATE INDEX tro_troncons_32_0_tro_id ON tro_troncons_32_0 USING btree (tro_id);
CREATE INDEX tro_troncons_32_0_voi_id_droit ON tro_troncons_32_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_32_0_voi_id_gauche ON tro_troncons_32_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_32_0_geometrie ON tro_troncons_32_0 USING gist (geometrie);

CREATE INDEX tro_troncons_33_0_tro_id ON tro_troncons_33_0 USING btree (tro_id);
CREATE INDEX tro_troncons_33_0_voi_id_droit ON tro_troncons_33_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_33_0_voi_id_gauche ON tro_troncons_33_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_33_0_geometrie ON tro_troncons_33_0 USING gist (geometrie);

CREATE INDEX tro_troncons_34_0_tro_id ON tro_troncons_34_0 USING btree (tro_id);
CREATE INDEX tro_troncons_34_0_voi_id_droit ON tro_troncons_34_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_34_0_voi_id_gauche ON tro_troncons_34_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_34_0_geometrie ON tro_troncons_34_0 USING gist (geometrie);

CREATE INDEX tro_troncons_35_0_tro_id ON tro_troncons_35_0 USING btree (tro_id);
CREATE INDEX tro_troncons_35_0_voi_id_droit ON tro_troncons_35_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_35_0_voi_id_gauche ON tro_troncons_35_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_35_0_geometrie ON tro_troncons_35_0 USING gist (geometrie);

CREATE INDEX tro_troncons_36_0_tro_id ON tro_troncons_36_0 USING btree (tro_id);
CREATE INDEX tro_troncons_36_0_voi_id_droit ON tro_troncons_36_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_36_0_voi_id_gauche ON tro_troncons_36_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_36_0_geometrie ON tro_troncons_36_0 USING gist (geometrie);

CREATE INDEX tro_troncons_37_0_tro_id ON tro_troncons_37_0 USING btree (tro_id);
CREATE INDEX tro_troncons_37_0_voi_id_droit ON tro_troncons_37_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_37_0_voi_id_gauche ON tro_troncons_37_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_37_0_geometrie ON tro_troncons_37_0 USING gist (geometrie);

CREATE INDEX tro_troncons_38_0_tro_id ON tro_troncons_38_0 USING btree (tro_id);
CREATE INDEX tro_troncons_38_0_voi_id_droit ON tro_troncons_38_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_38_0_voi_id_gauche ON tro_troncons_38_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_38_0_geometrie ON tro_troncons_38_0 USING gist (geometrie);

CREATE INDEX tro_troncons_39_0_tro_id ON tro_troncons_39_0 USING btree (tro_id);
CREATE INDEX tro_troncons_39_0_voi_id_droit ON tro_troncons_39_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_39_0_voi_id_gauche ON tro_troncons_39_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_39_0_geometrie ON tro_troncons_39_0 USING gist (geometrie);

CREATE INDEX tro_troncons_40_0_tro_id ON tro_troncons_40_0 USING btree (tro_id);
CREATE INDEX tro_troncons_40_0_voi_id_droit ON tro_troncons_40_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_40_0_voi_id_gauche ON tro_troncons_40_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_40_0_geometrie ON tro_troncons_40_0 USING gist (geometrie);

CREATE INDEX tro_troncons_41_0_tro_id ON tro_troncons_41_0 USING btree (tro_id);
CREATE INDEX tro_troncons_41_0_voi_id_droit ON tro_troncons_41_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_41_0_voi_id_gauche ON tro_troncons_41_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_41_0_geometrie ON tro_troncons_41_0 USING gist (geometrie);

CREATE INDEX tro_troncons_42_0_tro_id ON tro_troncons_42_0 USING btree (tro_id);
CREATE INDEX tro_troncons_42_0_voi_id_droit ON tro_troncons_42_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_42_0_voi_id_gauche ON tro_troncons_42_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_42_0_geometrie ON tro_troncons_42_0 USING gist (geometrie);

CREATE INDEX tro_troncons_43_0_tro_id ON tro_troncons_43_0 USING btree (tro_id);
CREATE INDEX tro_troncons_43_0_voi_id_droit ON tro_troncons_43_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_43_0_voi_id_gauche ON tro_troncons_43_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_43_0_geometrie ON tro_troncons_43_0 USING gist (geometrie);

CREATE INDEX tro_troncons_44_0_tro_id ON tro_troncons_44_0 USING btree (tro_id);
CREATE INDEX tro_troncons_44_0_voi_id_droit ON tro_troncons_44_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_44_0_voi_id_gauche ON tro_troncons_44_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_44_0_geometrie ON tro_troncons_44_0 USING gist (geometrie);

CREATE INDEX tro_troncons_45_0_tro_id ON tro_troncons_45_0 USING btree (tro_id);
CREATE INDEX tro_troncons_45_0_voi_id_droit ON tro_troncons_45_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_45_0_voi_id_gauche ON tro_troncons_45_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_45_0_geometrie ON tro_troncons_45_0 USING gist (geometrie);

CREATE INDEX tro_troncons_46_0_tro_id ON tro_troncons_46_0 USING btree (tro_id);
CREATE INDEX tro_troncons_46_0_voi_id_droit ON tro_troncons_46_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_46_0_voi_id_gauche ON tro_troncons_46_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_46_0_geometrie ON tro_troncons_46_0 USING gist (geometrie);

CREATE INDEX tro_troncons_47_0_tro_id ON tro_troncons_47_0 USING btree (tro_id);
CREATE INDEX tro_troncons_47_0_voi_id_droit ON tro_troncons_47_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_47_0_voi_id_gauche ON tro_troncons_47_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_47_0_geometrie ON tro_troncons_47_0 USING gist (geometrie);

CREATE INDEX tro_troncons_48_0_tro_id ON tro_troncons_48_0 USING btree (tro_id);
CREATE INDEX tro_troncons_48_0_voi_id_droit ON tro_troncons_48_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_48_0_voi_id_gauche ON tro_troncons_48_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_48_0_geometrie ON tro_troncons_48_0 USING gist (geometrie);

CREATE INDEX tro_troncons_49_0_tro_id ON tro_troncons_49_0 USING btree (tro_id);
CREATE INDEX tro_troncons_49_0_voi_id_droit ON tro_troncons_49_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_49_0_voi_id_gauche ON tro_troncons_49_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_49_0_geometrie ON tro_troncons_49_0 USING gist (geometrie);

CREATE INDEX tro_troncons_50_0_tro_id ON tro_troncons_50_0 USING btree (tro_id);
CREATE INDEX tro_troncons_50_0_voi_id_droit ON tro_troncons_50_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_50_0_voi_id_gauche ON tro_troncons_50_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_50_0_geometrie ON tro_troncons_50_0 USING gist (geometrie);

CREATE INDEX tro_troncons_51_0_tro_id ON tro_troncons_51_0 USING btree (tro_id);
CREATE INDEX tro_troncons_51_0_voi_id_droit ON tro_troncons_51_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_51_0_voi_id_gauche ON tro_troncons_51_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_51_0_geometrie ON tro_troncons_51_0 USING gist (geometrie);

CREATE INDEX tro_troncons_52_0_tro_id ON tro_troncons_52_0 USING btree (tro_id);
CREATE INDEX tro_troncons_52_0_voi_id_droit ON tro_troncons_52_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_52_0_voi_id_gauche ON tro_troncons_52_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_52_0_geometrie ON tro_troncons_52_0 USING gist (geometrie);

CREATE INDEX tro_troncons_53_0_tro_id ON tro_troncons_53_0 USING btree (tro_id);
CREATE INDEX tro_troncons_53_0_voi_id_droit ON tro_troncons_53_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_53_0_voi_id_gauche ON tro_troncons_53_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_53_0_geometrie ON tro_troncons_53_0 USING gist (geometrie);

CREATE INDEX tro_troncons_54_0_tro_id ON tro_troncons_54_0 USING btree (tro_id);
CREATE INDEX tro_troncons_54_0_voi_id_droit ON tro_troncons_54_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_54_0_voi_id_gauche ON tro_troncons_54_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_54_0_geometrie ON tro_troncons_54_0 USING gist (geometrie);

CREATE INDEX tro_troncons_55_0_tro_id ON tro_troncons_55_0 USING btree (tro_id);
CREATE INDEX tro_troncons_55_0_voi_id_droit ON tro_troncons_55_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_55_0_voi_id_gauche ON tro_troncons_55_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_55_0_geometrie ON tro_troncons_55_0 USING gist (geometrie);

CREATE INDEX tro_troncons_56_0_tro_id ON tro_troncons_56_0 USING btree (tro_id);
CREATE INDEX tro_troncons_56_0_voi_id_droit ON tro_troncons_56_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_56_0_voi_id_gauche ON tro_troncons_56_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_56_0_geometrie ON tro_troncons_56_0 USING gist (geometrie);

CREATE INDEX tro_troncons_57_0_tro_id ON tro_troncons_57_0 USING btree (tro_id);
CREATE INDEX tro_troncons_57_0_voi_id_droit ON tro_troncons_57_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_57_0_voi_id_gauche ON tro_troncons_57_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_57_0_geometrie ON tro_troncons_57_0 USING gist (geometrie);

CREATE INDEX tro_troncons_58_0_tro_id ON tro_troncons_58_0 USING btree (tro_id);
CREATE INDEX tro_troncons_58_0_voi_id_droit ON tro_troncons_58_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_58_0_voi_id_gauche ON tro_troncons_58_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_58_0_geometrie ON tro_troncons_58_0 USING gist (geometrie);

CREATE INDEX tro_troncons_59_0_tro_id ON tro_troncons_59_0 USING btree (tro_id);
CREATE INDEX tro_troncons_59_0_voi_id_droit ON tro_troncons_59_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_59_0_voi_id_gauche ON tro_troncons_59_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_59_0_geometrie ON tro_troncons_59_0 USING gist (geometrie);

CREATE INDEX tro_troncons_60_0_tro_id ON tro_troncons_60_0 USING btree (tro_id);
CREATE INDEX tro_troncons_60_0_voi_id_droit ON tro_troncons_60_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_60_0_voi_id_gauche ON tro_troncons_60_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_60_0_geometrie ON tro_troncons_60_0 USING gist (geometrie);

CREATE INDEX tro_troncons_61_0_tro_id ON tro_troncons_61_0 USING btree (tro_id);
CREATE INDEX tro_troncons_61_0_voi_id_droit ON tro_troncons_61_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_61_0_voi_id_gauche ON tro_troncons_61_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_61_0_geometrie ON tro_troncons_61_0 USING gist (geometrie);

CREATE INDEX tro_troncons_62_0_tro_id ON tro_troncons_62_0 USING btree (tro_id);
CREATE INDEX tro_troncons_62_0_voi_id_droit ON tro_troncons_62_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_62_0_voi_id_gauche ON tro_troncons_62_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_62_0_geometrie ON tro_troncons_62_0 USING gist (geometrie);

CREATE INDEX tro_troncons_63_0_tro_id ON tro_troncons_63_0 USING btree (tro_id);
CREATE INDEX tro_troncons_63_0_voi_id_droit ON tro_troncons_63_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_63_0_voi_id_gauche ON tro_troncons_63_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_63_0_geometrie ON tro_troncons_63_0 USING gist (geometrie);

CREATE INDEX tro_troncons_64_0_tro_id ON tro_troncons_64_0 USING btree (tro_id);
CREATE INDEX tro_troncons_64_0_voi_id_droit ON tro_troncons_64_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_64_0_voi_id_gauche ON tro_troncons_64_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_64_0_geometrie ON tro_troncons_64_0 USING gist (geometrie);

CREATE INDEX tro_troncons_65_0_tro_id ON tro_troncons_65_0 USING btree (tro_id);
CREATE INDEX tro_troncons_65_0_voi_id_droit ON tro_troncons_65_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_65_0_voi_id_gauche ON tro_troncons_65_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_65_0_geometrie ON tro_troncons_65_0 USING gist (geometrie);

CREATE INDEX tro_troncons_66_0_tro_id ON tro_troncons_66_0 USING btree (tro_id);
CREATE INDEX tro_troncons_66_0_voi_id_droit ON tro_troncons_66_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_66_0_voi_id_gauche ON tro_troncons_66_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_66_0_geometrie ON tro_troncons_66_0 USING gist (geometrie);

CREATE INDEX tro_troncons_67_0_tro_id ON tro_troncons_67_0 USING btree (tro_id);
CREATE INDEX tro_troncons_67_0_voi_id_droit ON tro_troncons_67_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_67_0_voi_id_gauche ON tro_troncons_67_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_67_0_geometrie ON tro_troncons_67_0 USING gist (geometrie);

CREATE INDEX tro_troncons_68_0_tro_id ON tro_troncons_68_0 USING btree (tro_id);
CREATE INDEX tro_troncons_68_0_voi_id_droit ON tro_troncons_68_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_68_0_voi_id_gauche ON tro_troncons_68_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_68_0_geometrie ON tro_troncons_68_0 USING gist (geometrie);

CREATE INDEX tro_troncons_69_0_tro_id ON tro_troncons_69_0 USING btree (tro_id);
CREATE INDEX tro_troncons_69_0_voi_id_droit ON tro_troncons_69_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_69_0_voi_id_gauche ON tro_troncons_69_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_69_0_geometrie ON tro_troncons_69_0 USING gist (geometrie);

CREATE INDEX tro_troncons_70_0_tro_id ON tro_troncons_70_0 USING btree (tro_id);
CREATE INDEX tro_troncons_70_0_voi_id_droit ON tro_troncons_70_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_70_0_voi_id_gauche ON tro_troncons_70_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_70_0_geometrie ON tro_troncons_70_0 USING gist (geometrie);

CREATE INDEX tro_troncons_71_0_tro_id ON tro_troncons_71_0 USING btree (tro_id);
CREATE INDEX tro_troncons_71_0_voi_id_droit ON tro_troncons_71_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_71_0_voi_id_gauche ON tro_troncons_71_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_71_0_geometrie ON tro_troncons_71_0 USING gist (geometrie);

CREATE INDEX tro_troncons_72_0_tro_id ON tro_troncons_72_0 USING btree (tro_id);
CREATE INDEX tro_troncons_72_0_voi_id_droit ON tro_troncons_72_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_72_0_voi_id_gauche ON tro_troncons_72_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_72_0_geometrie ON tro_troncons_72_0 USING gist (geometrie);

CREATE INDEX tro_troncons_73_0_tro_id ON tro_troncons_73_0 USING btree (tro_id);
CREATE INDEX tro_troncons_73_0_voi_id_droit ON tro_troncons_73_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_73_0_voi_id_gauche ON tro_troncons_73_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_73_0_geometrie ON tro_troncons_73_0 USING gist (geometrie);

CREATE INDEX tro_troncons_74_0_tro_id ON tro_troncons_74_0 USING btree (tro_id);
CREATE INDEX tro_troncons_74_0_voi_id_droit ON tro_troncons_74_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_74_0_voi_id_gauche ON tro_troncons_74_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_74_0_geometrie ON tro_troncons_74_0 USING gist (geometrie);

CREATE INDEX tro_troncons_75_0_tro_id ON tro_troncons_75_0 USING btree (tro_id);
CREATE INDEX tro_troncons_75_0_voi_id_droit ON tro_troncons_75_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_75_0_voi_id_gauche ON tro_troncons_75_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_75_0_geometrie ON tro_troncons_75_0 USING gist (geometrie);

CREATE INDEX tro_troncons_76_0_tro_id ON tro_troncons_76_0 USING btree (tro_id);
CREATE INDEX tro_troncons_76_0_voi_id_droit ON tro_troncons_76_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_76_0_voi_id_gauche ON tro_troncons_76_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_76_0_geometrie ON tro_troncons_76_0 USING gist (geometrie);

CREATE INDEX tro_troncons_77_0_tro_id ON tro_troncons_77_0 USING btree (tro_id);
CREATE INDEX tro_troncons_77_0_voi_id_droit ON tro_troncons_77_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_77_0_voi_id_gauche ON tro_troncons_77_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_77_0_geometrie ON tro_troncons_77_0 USING gist (geometrie);

CREATE INDEX tro_troncons_78_0_tro_id ON tro_troncons_78_0 USING btree (tro_id);
CREATE INDEX tro_troncons_78_0_voi_id_droit ON tro_troncons_78_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_78_0_voi_id_gauche ON tro_troncons_78_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_78_0_geometrie ON tro_troncons_78_0 USING gist (geometrie);

CREATE INDEX tro_troncons_79_0_tro_id ON tro_troncons_79_0 USING btree (tro_id);
CREATE INDEX tro_troncons_79_0_voi_id_droit ON tro_troncons_79_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_79_0_voi_id_gauche ON tro_troncons_79_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_79_0_geometrie ON tro_troncons_79_0 USING gist (geometrie);

CREATE INDEX tro_troncons_80_0_tro_id ON tro_troncons_80_0 USING btree (tro_id);
CREATE INDEX tro_troncons_80_0_voi_id_droit ON tro_troncons_80_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_80_0_voi_id_gauche ON tro_troncons_80_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_80_0_geometrie ON tro_troncons_80_0 USING gist (geometrie);

CREATE INDEX tro_troncons_81_0_tro_id ON tro_troncons_81_0 USING btree (tro_id);
CREATE INDEX tro_troncons_81_0_voi_id_droit ON tro_troncons_81_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_81_0_voi_id_gauche ON tro_troncons_81_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_81_0_geometrie ON tro_troncons_81_0 USING gist (geometrie);

CREATE INDEX tro_troncons_82_0_tro_id ON tro_troncons_82_0 USING btree (tro_id);
CREATE INDEX tro_troncons_82_0_voi_id_droit ON tro_troncons_82_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_82_0_voi_id_gauche ON tro_troncons_82_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_82_0_geometrie ON tro_troncons_82_0 USING gist (geometrie);

CREATE INDEX tro_troncons_83_0_tro_id ON tro_troncons_83_0 USING btree (tro_id);
CREATE INDEX tro_troncons_83_0_voi_id_droit ON tro_troncons_83_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_83_0_voi_id_gauche ON tro_troncons_83_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_83_0_geometrie ON tro_troncons_83_0 USING gist (geometrie);

CREATE INDEX tro_troncons_84_0_tro_id ON tro_troncons_84_0 USING btree (tro_id);
CREATE INDEX tro_troncons_84_0_voi_id_droit ON tro_troncons_84_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_84_0_voi_id_gauche ON tro_troncons_84_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_84_0_geometrie ON tro_troncons_84_0 USING gist (geometrie);

CREATE INDEX tro_troncons_85_0_tro_id ON tro_troncons_85_0 USING btree (tro_id);
CREATE INDEX tro_troncons_85_0_voi_id_droit ON tro_troncons_85_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_85_0_voi_id_gauche ON tro_troncons_85_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_85_0_geometrie ON tro_troncons_85_0 USING gist (geometrie);

CREATE INDEX tro_troncons_86_0_tro_id ON tro_troncons_86_0 USING btree (tro_id);
CREATE INDEX tro_troncons_86_0_voi_id_droit ON tro_troncons_86_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_86_0_voi_id_gauche ON tro_troncons_86_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_86_0_geometrie ON tro_troncons_86_0 USING gist (geometrie);

CREATE INDEX tro_troncons_87_0_tro_id ON tro_troncons_87_0 USING btree (tro_id);
CREATE INDEX tro_troncons_87_0_voi_id_droit ON tro_troncons_87_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_87_0_voi_id_gauche ON tro_troncons_87_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_87_0_geometrie ON tro_troncons_87_0 USING gist (geometrie);

CREATE INDEX tro_troncons_88_0_tro_id ON tro_troncons_88_0 USING btree (tro_id);
CREATE INDEX tro_troncons_88_0_voi_id_droit ON tro_troncons_88_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_88_0_voi_id_gauche ON tro_troncons_88_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_88_0_geometrie ON tro_troncons_88_0 USING gist (geometrie);

CREATE INDEX tro_troncons_89_0_tro_id ON tro_troncons_89_0 USING btree (tro_id);
CREATE INDEX tro_troncons_89_0_voi_id_droit ON tro_troncons_89_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_89_0_voi_id_gauche ON tro_troncons_89_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_89_0_geometrie ON tro_troncons_89_0 USING gist (geometrie);

CREATE INDEX tro_troncons_90_0_tro_id ON tro_troncons_90_0 USING btree (tro_id);
CREATE INDEX tro_troncons_90_0_voi_id_droit ON tro_troncons_90_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_90_0_voi_id_gauche ON tro_troncons_90_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_90_0_geometrie ON tro_troncons_90_0 USING gist (geometrie);

CREATE INDEX tro_troncons_91_0_tro_id ON tro_troncons_91_0 USING btree (tro_id);
CREATE INDEX tro_troncons_91_0_voi_id_droit ON tro_troncons_91_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_91_0_voi_id_gauche ON tro_troncons_91_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_91_0_geometrie ON tro_troncons_91_0 USING gist (geometrie);

CREATE INDEX tro_troncons_92_0_tro_id ON tro_troncons_92_0 USING btree (tro_id);
CREATE INDEX tro_troncons_92_0_voi_id_droit ON tro_troncons_92_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_92_0_voi_id_gauche ON tro_troncons_92_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_92_0_geometrie ON tro_troncons_92_0 USING gist (geometrie);

CREATE INDEX tro_troncons_93_0_tro_id ON tro_troncons_93_0 USING btree (tro_id);
CREATE INDEX tro_troncons_93_0_voi_id_droit ON tro_troncons_93_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_93_0_voi_id_gauche ON tro_troncons_93_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_93_0_geometrie ON tro_troncons_93_0 USING gist (geometrie);

CREATE INDEX tro_troncons_94_0_tro_id ON tro_troncons_94_0 USING btree (tro_id);
CREATE INDEX tro_troncons_94_0_voi_id_droit ON tro_troncons_94_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_94_0_voi_id_gauche ON tro_troncons_94_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_94_0_geometrie ON tro_troncons_94_0 USING gist (geometrie);

CREATE INDEX tro_troncons_95_0_tro_id ON tro_troncons_95_0 USING btree (tro_id);
CREATE INDEX tro_troncons_95_0_voi_id_droit ON tro_troncons_95_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_95_0_voi_id_gauche ON tro_troncons_95_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_95_0_geometrie ON tro_troncons_95_0 USING gist (geometrie);

CREATE INDEX tro_troncons_96_0_tro_id ON tro_troncons_96_0 USING btree (tro_id);
CREATE INDEX tro_troncons_96_0_voi_id_droit ON tro_troncons_96_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_96_0_voi_id_gauche ON tro_troncons_96_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_96_0_geometrie ON tro_troncons_96_0 USING gist (geometrie);


-- historisation des voies
CREATE INDEX vhi_01_voi_id_precedent ON vhi_voies_historisee_01 USING btree (voi_id_precedent);
CREATE INDEX vhi_02_voi_id_precedent ON vhi_voies_historisee_02 USING btree (voi_id_precedent);
CREATE INDEX vhi_03_voi_id_precedent ON vhi_voies_historisee_03 USING btree (voi_id_precedent);
CREATE INDEX vhi_04_voi_id_precedent ON vhi_voies_historisee_04 USING btree (voi_id_precedent);
CREATE INDEX vhi_05_voi_id_precedent ON vhi_voies_historisee_05 USING btree (voi_id_precedent);
CREATE INDEX vhi_06_voi_id_precedent ON vhi_voies_historisee_06 USING btree (voi_id_precedent);
CREATE INDEX vhi_07_voi_id_precedent ON vhi_voies_historisee_07 USING btree (voi_id_precedent);
CREATE INDEX vhi_08_voi_id_precedent ON vhi_voies_historisee_08 USING btree (voi_id_precedent);
CREATE INDEX vhi_09_voi_id_precedent ON vhi_voies_historisee_09 USING btree (voi_id_precedent);
CREATE INDEX vhi_10_voi_id_precedent ON vhi_voies_historisee_10 USING btree (voi_id_precedent);
CREATE INDEX vhi_11_voi_id_precedent ON vhi_voies_historisee_11 USING btree (voi_id_precedent);
CREATE INDEX vhi_12_voi_id_precedent ON vhi_voies_historisee_12 USING btree (voi_id_precedent);
CREATE INDEX vhi_13_voi_id_precedent ON vhi_voies_historisee_13 USING btree (voi_id_precedent);
CREATE INDEX vhi_14_voi_id_precedent ON vhi_voies_historisee_14 USING btree (voi_id_precedent);
CREATE INDEX vhi_15_voi_id_precedent ON vhi_voies_historisee_15 USING btree (voi_id_precedent);
CREATE INDEX vhi_16_voi_id_precedent ON vhi_voies_historisee_16 USING btree (voi_id_precedent);
CREATE INDEX vhi_17_voi_id_precedent ON vhi_voies_historisee_17 USING btree (voi_id_precedent);
CREATE INDEX vhi_18_voi_id_precedent ON vhi_voies_historisee_18 USING btree (voi_id_precedent);
CREATE INDEX vhi_19_voi_id_precedent ON vhi_voies_historisee_19 USING btree (voi_id_precedent);
CREATE INDEX vhi_21_voi_id_precedent ON vhi_voies_historisee_21 USING btree (voi_id_precedent);
CREATE INDEX vhi_22_voi_id_precedent ON vhi_voies_historisee_22 USING btree (voi_id_precedent);
CREATE INDEX vhi_23_voi_id_precedent ON vhi_voies_historisee_23 USING btree (voi_id_precedent);
CREATE INDEX vhi_24_voi_id_precedent ON vhi_voies_historisee_24 USING btree (voi_id_precedent);
CREATE INDEX vhi_25_voi_id_precedent ON vhi_voies_historisee_25 USING btree (voi_id_precedent);
CREATE INDEX vhi_26_voi_id_precedent ON vhi_voies_historisee_26 USING btree (voi_id_precedent);
CREATE INDEX vhi_27_voi_id_precedent ON vhi_voies_historisee_27 USING btree (voi_id_precedent);
CREATE INDEX vhi_28_voi_id_precedent ON vhi_voies_historisee_28 USING btree (voi_id_precedent);
CREATE INDEX vhi_29_voi_id_precedent ON vhi_voies_historisee_29 USING btree (voi_id_precedent);
CREATE INDEX vhi_30_voi_id_precedent ON vhi_voies_historisee_30 USING btree (voi_id_precedent);
CREATE INDEX vhi_31_voi_id_precedent ON vhi_voies_historisee_31 USING btree (voi_id_precedent);
CREATE INDEX vhi_32_voi_id_precedent ON vhi_voies_historisee_32 USING btree (voi_id_precedent);
CREATE INDEX vhi_33_voi_id_precedent ON vhi_voies_historisee_33 USING btree (voi_id_precedent);
CREATE INDEX vhi_34_voi_id_precedent ON vhi_voies_historisee_34 USING btree (voi_id_precedent);
CREATE INDEX vhi_35_voi_id_precedent ON vhi_voies_historisee_35 USING btree (voi_id_precedent);
CREATE INDEX vhi_36_voi_id_precedent ON vhi_voies_historisee_36 USING btree (voi_id_precedent);
CREATE INDEX vhi_37_voi_id_precedent ON vhi_voies_historisee_37 USING btree (voi_id_precedent);
CREATE INDEX vhi_38_voi_id_precedent ON vhi_voies_historisee_38 USING btree (voi_id_precedent);
CREATE INDEX vhi_39_voi_id_precedent ON vhi_voies_historisee_39 USING btree (voi_id_precedent);
CREATE INDEX vhi_40_voi_id_precedent ON vhi_voies_historisee_40 USING btree (voi_id_precedent);
CREATE INDEX vhi_41_voi_id_precedent ON vhi_voies_historisee_41 USING btree (voi_id_precedent);
CREATE INDEX vhi_42_voi_id_precedent ON vhi_voies_historisee_42 USING btree (voi_id_precedent);
CREATE INDEX vhi_43_voi_id_precedent ON vhi_voies_historisee_43 USING btree (voi_id_precedent);
CREATE INDEX vhi_44_voi_id_precedent ON vhi_voies_historisee_44 USING btree (voi_id_precedent);
CREATE INDEX vhi_45_voi_id_precedent ON vhi_voies_historisee_45 USING btree (voi_id_precedent);
CREATE INDEX vhi_46_voi_id_precedent ON vhi_voies_historisee_46 USING btree (voi_id_precedent);
CREATE INDEX vhi_47_voi_id_precedent ON vhi_voies_historisee_47 USING btree (voi_id_precedent);
CREATE INDEX vhi_48_voi_id_precedent ON vhi_voies_historisee_48 USING btree (voi_id_precedent);
CREATE INDEX vhi_49_voi_id_precedent ON vhi_voies_historisee_49 USING btree (voi_id_precedent);
CREATE INDEX vhi_50_voi_id_precedent ON vhi_voies_historisee_50 USING btree (voi_id_precedent);
CREATE INDEX vhi_51_voi_id_precedent ON vhi_voies_historisee_51 USING btree (voi_id_precedent);
CREATE INDEX vhi_52_voi_id_precedent ON vhi_voies_historisee_52 USING btree (voi_id_precedent);
CREATE INDEX vhi_53_voi_id_precedent ON vhi_voies_historisee_53 USING btree (voi_id_precedent);
CREATE INDEX vhi_54_voi_id_precedent ON vhi_voies_historisee_54 USING btree (voi_id_precedent);
CREATE INDEX vhi_55_voi_id_precedent ON vhi_voies_historisee_55 USING btree (voi_id_precedent);
CREATE INDEX vhi_56_voi_id_precedent ON vhi_voies_historisee_56 USING btree (voi_id_precedent);
CREATE INDEX vhi_57_voi_id_precedent ON vhi_voies_historisee_57 USING btree (voi_id_precedent);
CREATE INDEX vhi_58_voi_id_precedent ON vhi_voies_historisee_58 USING btree (voi_id_precedent);
CREATE INDEX vhi_59_voi_id_precedent ON vhi_voies_historisee_59 USING btree (voi_id_precedent);
CREATE INDEX vhi_60_voi_id_precedent ON vhi_voies_historisee_60 USING btree (voi_id_precedent);
CREATE INDEX vhi_61_voi_id_precedent ON vhi_voies_historisee_61 USING btree (voi_id_precedent);
CREATE INDEX vhi_62_voi_id_precedent ON vhi_voies_historisee_62 USING btree (voi_id_precedent);
CREATE INDEX vhi_63_voi_id_precedent ON vhi_voies_historisee_63 USING btree (voi_id_precedent);
CREATE INDEX vhi_64_voi_id_precedent ON vhi_voies_historisee_64 USING btree (voi_id_precedent);
CREATE INDEX vhi_65_voi_id_precedent ON vhi_voies_historisee_65 USING btree (voi_id_precedent);
CREATE INDEX vhi_66_voi_id_precedent ON vhi_voies_historisee_66 USING btree (voi_id_precedent);
CREATE INDEX vhi_67_voi_id_precedent ON vhi_voies_historisee_67 USING btree (voi_id_precedent);
CREATE INDEX vhi_68_voi_id_precedent ON vhi_voies_historisee_68 USING btree (voi_id_precedent);
CREATE INDEX vhi_69_voi_id_precedent ON vhi_voies_historisee_69 USING btree (voi_id_precedent);
CREATE INDEX vhi_70_voi_id_precedent ON vhi_voies_historisee_70 USING btree (voi_id_precedent);
CREATE INDEX vhi_71_voi_id_precedent ON vhi_voies_historisee_71 USING btree (voi_id_precedent);
CREATE INDEX vhi_72_voi_id_precedent ON vhi_voies_historisee_72 USING btree (voi_id_precedent);
CREATE INDEX vhi_73_voi_id_precedent ON vhi_voies_historisee_73 USING btree (voi_id_precedent);
CREATE INDEX vhi_74_voi_id_precedent ON vhi_voies_historisee_74 USING btree (voi_id_precedent);
CREATE INDEX vhi_75_voi_id_precedent ON vhi_voies_historisee_75 USING btree (voi_id_precedent);
CREATE INDEX vhi_76_voi_id_precedent ON vhi_voies_historisee_76 USING btree (voi_id_precedent);
CREATE INDEX vhi_77_voi_id_precedent ON vhi_voies_historisee_77 USING btree (voi_id_precedent);
CREATE INDEX vhi_78_voi_id_precedent ON vhi_voies_historisee_78 USING btree (voi_id_precedent);
CREATE INDEX vhi_79_voi_id_precedent ON vhi_voies_historisee_79 USING btree (voi_id_precedent);
CREATE INDEX vhi_80_voi_id_precedent ON vhi_voies_historisee_80 USING btree (voi_id_precedent);
CREATE INDEX vhi_81_voi_id_precedent ON vhi_voies_historisee_81 USING btree (voi_id_precedent);
CREATE INDEX vhi_82_voi_id_precedent ON vhi_voies_historisee_82 USING btree (voi_id_precedent);
CREATE INDEX vhi_83_voi_id_precedent ON vhi_voies_historisee_83 USING btree (voi_id_precedent);
CREATE INDEX vhi_84_voi_id_precedent ON vhi_voies_historisee_84 USING btree (voi_id_precedent);
CREATE INDEX vhi_85_voi_id_precedent ON vhi_voies_historisee_85 USING btree (voi_id_precedent);
CREATE INDEX vhi_86_voi_id_precedent ON vhi_voies_historisee_86 USING btree (voi_id_precedent);
CREATE INDEX vhi_87_voi_id_precedent ON vhi_voies_historisee_87 USING btree (voi_id_precedent);
CREATE INDEX vhi_88_voi_id_precedent ON vhi_voies_historisee_88 USING btree (voi_id_precedent);
CREATE INDEX vhi_89_voi_id_precedent ON vhi_voies_historisee_89 USING btree (voi_id_precedent);
CREATE INDEX vhi_90_voi_id_precedent ON vhi_voies_historisee_90 USING btree (voi_id_precedent);
CREATE INDEX vhi_91_voi_id_precedent ON vhi_voies_historisee_91 USING btree (voi_id_precedent);
CREATE INDEX vhi_92_voi_id_precedent ON vhi_voies_historisee_92 USING btree (voi_id_precedent);
CREATE INDEX vhi_93_voi_id_precedent ON vhi_voies_historisee_93 USING btree (voi_id_precedent);
CREATE INDEX vhi_94_voi_id_precedent ON vhi_voies_historisee_94 USING btree (voi_id_precedent);
CREATE INDEX vhi_95_voi_id_precedent ON vhi_voies_historisee_95 USING btree (voi_id_precedent);
CREATE INDEX vhi_96_voi_id_precedent ON vhi_voies_historisee_96 USING btree (voi_id_precedent);

-- voies
CREATE INDEX voi_01_code_insee ON voi_voies_01 USING btree (com_code_insee);
CREATE INDEX voi_01_lbl ON voi_voies_01 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_01_type_lbl ON voi_voies_01 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_01_voi_id ON voi_voies_01 USING btree (voi_id);
CREATE INDEX voi_02_code_insee ON voi_voies_02 USING btree (com_code_insee);
CREATE INDEX voi_02_lbl ON voi_voies_02 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_02_type_lbl ON voi_voies_02 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_02_voi_id ON voi_voies_02 USING btree (voi_id);
CREATE INDEX voi_03_code_insee ON voi_voies_03 USING btree (com_code_insee);
CREATE INDEX voi_03_lbl ON voi_voies_03 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_03_type_lbl ON voi_voies_03 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_03_voi_id ON voi_voies_03 USING btree (voi_id);
CREATE INDEX voi_04_code_insee ON voi_voies_04 USING btree (com_code_insee);
CREATE INDEX voi_04_lbl ON voi_voies_04 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_04_type_lbl ON voi_voies_04 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_04_voi_id ON voi_voies_04 USING btree (voi_id);
CREATE INDEX voi_05_code_insee ON voi_voies_05 USING btree (com_code_insee);
CREATE INDEX voi_05_lbl ON voi_voies_05 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_05_type_lbl ON voi_voies_05 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_05_voi_id ON voi_voies_05 USING btree (voi_id);
CREATE INDEX voi_06_code_insee ON voi_voies_06 USING btree (com_code_insee);
CREATE INDEX voi_06_lbl ON voi_voies_06 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_06_type_lbl ON voi_voies_06 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_06_voi_id ON voi_voies_06 USING btree (voi_id);
CREATE INDEX voi_07_code_insee ON voi_voies_07 USING btree (com_code_insee);
CREATE INDEX voi_07_lbl ON voi_voies_07 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_07_type_lbl ON voi_voies_07 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_07_voi_id ON voi_voies_07 USING btree (voi_id);
CREATE INDEX voi_08_code_insee ON voi_voies_08 USING btree (com_code_insee);
CREATE INDEX voi_08_lbl ON voi_voies_08 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_08_type_lbl ON voi_voies_08 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_08_voi_id ON voi_voies_08 USING btree (voi_id);
CREATE INDEX voi_09_code_insee ON voi_voies_09 USING btree (com_code_insee);
CREATE INDEX voi_09_lbl ON voi_voies_09 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_09_type_lbl ON voi_voies_09 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_09_voi_id ON voi_voies_09 USING btree (voi_id);
CREATE INDEX voi_10_code_insee ON voi_voies_10 USING btree (com_code_insee);
CREATE INDEX voi_10_lbl ON voi_voies_10 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_10_type_lbl ON voi_voies_10 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_10_voi_id ON voi_voies_10 USING btree (voi_id);
CREATE INDEX voi_11_code_insee ON voi_voies_11 USING btree (com_code_insee);
CREATE INDEX voi_11_lbl ON voi_voies_11 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_11_type_lbl ON voi_voies_11 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_11_voi_id ON voi_voies_11 USING btree (voi_id);
CREATE INDEX voi_12_code_insee ON voi_voies_12 USING btree (com_code_insee);
CREATE INDEX voi_12_lbl ON voi_voies_12 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_12_type_lbl ON voi_voies_12 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_12_voi_id ON voi_voies_12 USING btree (voi_id);
CREATE INDEX voi_13_code_insee ON voi_voies_13 USING btree (com_code_insee);
CREATE INDEX voi_13_lbl ON voi_voies_13 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_13_type_lbl ON voi_voies_13 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_13_voi_id ON voi_voies_13 USING btree (voi_id);
CREATE INDEX voi_14_code_insee ON voi_voies_14 USING btree (com_code_insee);
CREATE INDEX voi_14_lbl ON voi_voies_14 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_14_type_lbl ON voi_voies_14 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_14_voi_id ON voi_voies_14 USING btree (voi_id);
CREATE INDEX voi_15_code_insee ON voi_voies_15 USING btree (com_code_insee);
CREATE INDEX voi_15_lbl ON voi_voies_15 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_15_type_lbl ON voi_voies_15 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_15_voi_id ON voi_voies_15 USING btree (voi_id);
CREATE INDEX voi_16_code_insee ON voi_voies_16 USING btree (com_code_insee);
CREATE INDEX voi_16_lbl ON voi_voies_16 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_16_type_lbl ON voi_voies_16 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_16_voi_id ON voi_voies_16 USING btree (voi_id);
CREATE INDEX voi_17_code_insee ON voi_voies_17 USING btree (com_code_insee);
CREATE INDEX voi_17_lbl ON voi_voies_17 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_17_type_lbl ON voi_voies_17 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_17_voi_id ON voi_voies_17 USING btree (voi_id);
CREATE INDEX voi_18_code_insee ON voi_voies_18 USING btree (com_code_insee);
CREATE INDEX voi_18_lbl ON voi_voies_18 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_18_type_lbl ON voi_voies_18 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_18_voi_id ON voi_voies_18 USING btree (voi_id);
CREATE INDEX voi_19_code_insee ON voi_voies_19 USING btree (com_code_insee);
CREATE INDEX voi_19_lbl ON voi_voies_19 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_19_type_lbl ON voi_voies_19 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_19_voi_id ON voi_voies_19 USING btree (voi_id);
CREATE INDEX voi_21_code_insee ON voi_voies_21 USING btree (com_code_insee);
CREATE INDEX voi_21_lbl ON voi_voies_21 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_21_type_lbl ON voi_voies_21 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_21_voi_id ON voi_voies_21 USING btree (voi_id);
CREATE INDEX voi_22_code_insee ON voi_voies_22 USING btree (com_code_insee);
CREATE INDEX voi_22_lbl ON voi_voies_22 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_22_type_lbl ON voi_voies_22 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_22_voi_id ON voi_voies_22 USING btree (voi_id);
CREATE INDEX voi_23_code_insee ON voi_voies_23 USING btree (com_code_insee);
CREATE INDEX voi_23_lbl ON voi_voies_23 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_23_type_lbl ON voi_voies_23 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_23_voi_id ON voi_voies_23 USING btree (voi_id);
CREATE INDEX voi_24_code_insee ON voi_voies_24 USING btree (com_code_insee);
CREATE INDEX voi_24_lbl ON voi_voies_24 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_24_type_lbl ON voi_voies_24 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_24_voi_id ON voi_voies_24 USING btree (voi_id);
CREATE INDEX voi_25_code_insee ON voi_voies_25 USING btree (com_code_insee);
CREATE INDEX voi_25_lbl ON voi_voies_25 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_25_type_lbl ON voi_voies_25 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_25_voi_id ON voi_voies_25 USING btree (voi_id);
CREATE INDEX voi_26_code_insee ON voi_voies_26 USING btree (com_code_insee);
CREATE INDEX voi_26_lbl ON voi_voies_26 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_26_type_lbl ON voi_voies_26 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_26_voi_id ON voi_voies_26 USING btree (voi_id);
CREATE INDEX voi_27_code_insee ON voi_voies_27 USING btree (com_code_insee);
CREATE INDEX voi_27_lbl ON voi_voies_27 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_27_type_lbl ON voi_voies_27 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_27_voi_id ON voi_voies_27 USING btree (voi_id);
CREATE INDEX voi_28_code_insee ON voi_voies_28 USING btree (com_code_insee);
CREATE INDEX voi_28_lbl ON voi_voies_28 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_28_type_lbl ON voi_voies_28 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_28_voi_id ON voi_voies_28 USING btree (voi_id);
CREATE INDEX voi_29_code_insee ON voi_voies_29 USING btree (com_code_insee);
CREATE INDEX voi_29_lbl ON voi_voies_29 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_29_type_lbl ON voi_voies_29 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_29_voi_id ON voi_voies_29 USING btree (voi_id);
CREATE INDEX voi_30_code_insee ON voi_voies_30 USING btree (com_code_insee);
CREATE INDEX voi_30_lbl ON voi_voies_30 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_30_type_lbl ON voi_voies_30 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_30_voi_id ON voi_voies_30 USING btree (voi_id);
CREATE INDEX voi_31_code_insee ON voi_voies_31 USING btree (com_code_insee);
CREATE INDEX voi_31_lbl ON voi_voies_31 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_31_type_lbl ON voi_voies_31 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_31_voi_id ON voi_voies_31 USING btree (voi_id);
CREATE INDEX voi_32_code_insee ON voi_voies_32 USING btree (com_code_insee);
CREATE INDEX voi_32_lbl ON voi_voies_32 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_32_type_lbl ON voi_voies_32 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_32_voi_id ON voi_voies_32 USING btree (voi_id);
CREATE INDEX voi_33_code_insee ON voi_voies_33 USING btree (com_code_insee);
CREATE INDEX voi_33_lbl ON voi_voies_33 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_33_type_lbl ON voi_voies_33 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_33_voi_id ON voi_voies_33 USING btree (voi_id);
CREATE INDEX voi_34_code_insee ON voi_voies_34 USING btree (com_code_insee);
CREATE INDEX voi_34_lbl ON voi_voies_34 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_34_type_lbl ON voi_voies_34 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_34_voi_id ON voi_voies_34 USING btree (voi_id);
CREATE INDEX voi_35_code_insee ON voi_voies_35 USING btree (com_code_insee);
CREATE INDEX voi_35_lbl ON voi_voies_35 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_35_type_lbl ON voi_voies_35 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_35_voi_id ON voi_voies_35 USING btree (voi_id);
CREATE INDEX voi_36_code_insee ON voi_voies_36 USING btree (com_code_insee);
CREATE INDEX voi_36_lbl ON voi_voies_36 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_36_type_lbl ON voi_voies_36 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_36_voi_id ON voi_voies_36 USING btree (voi_id);
CREATE INDEX voi_37_code_insee ON voi_voies_37 USING btree (com_code_insee);
CREATE INDEX voi_37_lbl ON voi_voies_37 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_37_type_lbl ON voi_voies_37 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_37_voi_id ON voi_voies_37 USING btree (voi_id);
CREATE INDEX voi_38_code_insee ON voi_voies_38 USING btree (com_code_insee);
CREATE INDEX voi_38_lbl ON voi_voies_38 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_38_type_lbl ON voi_voies_38 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_38_voi_id ON voi_voies_38 USING btree (voi_id);
CREATE INDEX voi_39_code_insee ON voi_voies_39 USING btree (com_code_insee);
CREATE INDEX voi_39_lbl ON voi_voies_39 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_39_type_lbl ON voi_voies_39 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_39_voi_id ON voi_voies_39 USING btree (voi_id);
CREATE INDEX voi_40_code_insee ON voi_voies_40 USING btree (com_code_insee);
CREATE INDEX voi_40_lbl ON voi_voies_40 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_40_type_lbl ON voi_voies_40 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_40_voi_id ON voi_voies_40 USING btree (voi_id);
CREATE INDEX voi_41_code_insee ON voi_voies_41 USING btree (com_code_insee);
CREATE INDEX voi_41_lbl ON voi_voies_41 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_41_type_lbl ON voi_voies_41 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_41_voi_id ON voi_voies_41 USING btree (voi_id);
CREATE INDEX voi_42_code_insee ON voi_voies_42 USING btree (com_code_insee);
CREATE INDEX voi_42_lbl ON voi_voies_42 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_42_type_lbl ON voi_voies_42 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_42_voi_id ON voi_voies_42 USING btree (voi_id);
CREATE INDEX voi_43_code_insee ON voi_voies_43 USING btree (com_code_insee);
CREATE INDEX voi_43_lbl ON voi_voies_43 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_43_type_lbl ON voi_voies_43 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_43_voi_id ON voi_voies_43 USING btree (voi_id);
CREATE INDEX voi_44_code_insee ON voi_voies_44 USING btree (com_code_insee);
CREATE INDEX voi_44_lbl ON voi_voies_44 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_44_type_lbl ON voi_voies_44 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_44_voi_id ON voi_voies_44 USING btree (voi_id);
CREATE INDEX voi_45_code_insee ON voi_voies_45 USING btree (com_code_insee);
CREATE INDEX voi_45_lbl ON voi_voies_45 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_45_type_lbl ON voi_voies_45 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_45_voi_id ON voi_voies_45 USING btree (voi_id);
CREATE INDEX voi_46_code_insee ON voi_voies_46 USING btree (com_code_insee);
CREATE INDEX voi_46_lbl ON voi_voies_46 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_46_type_lbl ON voi_voies_46 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_46_voi_id ON voi_voies_46 USING btree (voi_id);
CREATE INDEX voi_47_code_insee ON voi_voies_47 USING btree (com_code_insee);
CREATE INDEX voi_47_lbl ON voi_voies_47 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_47_type_lbl ON voi_voies_47 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_47_voi_id ON voi_voies_47 USING btree (voi_id);
CREATE INDEX voi_48_code_insee ON voi_voies_48 USING btree (com_code_insee);
CREATE INDEX voi_48_lbl ON voi_voies_48 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_48_type_lbl ON voi_voies_48 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_48_voi_id ON voi_voies_48 USING btree (voi_id);
CREATE INDEX voi_49_code_insee ON voi_voies_49 USING btree (com_code_insee);
CREATE INDEX voi_49_lbl ON voi_voies_49 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_49_type_lbl ON voi_voies_49 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_49_voi_id ON voi_voies_49 USING btree (voi_id);
CREATE INDEX voi_50_code_insee ON voi_voies_50 USING btree (com_code_insee);
CREATE INDEX voi_50_lbl ON voi_voies_50 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_50_type_lbl ON voi_voies_50 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_50_voi_id ON voi_voies_50 USING btree (voi_id);
CREATE INDEX voi_51_code_insee ON voi_voies_51 USING btree (com_code_insee);
CREATE INDEX voi_51_lbl ON voi_voies_51 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_51_type_lbl ON voi_voies_51 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_51_voi_id ON voi_voies_51 USING btree (voi_id);
CREATE INDEX voi_52_code_insee ON voi_voies_52 USING btree (com_code_insee);
CREATE INDEX voi_52_lbl ON voi_voies_52 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_52_type_lbl ON voi_voies_52 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_52_voi_id ON voi_voies_52 USING btree (voi_id);
CREATE INDEX voi_53_code_insee ON voi_voies_53 USING btree (com_code_insee);
CREATE INDEX voi_53_lbl ON voi_voies_53 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_53_type_lbl ON voi_voies_53 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_53_voi_id ON voi_voies_53 USING btree (voi_id);
CREATE INDEX voi_54_code_insee ON voi_voies_54 USING btree (com_code_insee);
CREATE INDEX voi_54_lbl ON voi_voies_54 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_54_type_lbl ON voi_voies_54 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_54_voi_id ON voi_voies_54 USING btree (voi_id);
CREATE INDEX voi_55_code_insee ON voi_voies_55 USING btree (com_code_insee);
CREATE INDEX voi_55_lbl ON voi_voies_55 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_55_type_lbl ON voi_voies_55 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_55_voi_id ON voi_voies_55 USING btree (voi_id);
CREATE INDEX voi_56_code_insee ON voi_voies_56 USING btree (com_code_insee);
CREATE INDEX voi_56_lbl ON voi_voies_56 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_56_type_lbl ON voi_voies_56 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_56_voi_id ON voi_voies_56 USING btree (voi_id);
CREATE INDEX voi_57_code_insee ON voi_voies_57 USING btree (com_code_insee);
CREATE INDEX voi_57_lbl ON voi_voies_57 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_57_type_lbl ON voi_voies_57 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_57_voi_id ON voi_voies_57 USING btree (voi_id);
CREATE INDEX voi_58_code_insee ON voi_voies_58 USING btree (com_code_insee);
CREATE INDEX voi_58_lbl ON voi_voies_58 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_58_type_lbl ON voi_voies_58 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_58_voi_id ON voi_voies_58 USING btree (voi_id);
CREATE INDEX voi_59_code_insee ON voi_voies_59 USING btree (com_code_insee);
CREATE INDEX voi_59_lbl ON voi_voies_59 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_59_type_lbl ON voi_voies_59 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_59_voi_id ON voi_voies_59 USING btree (voi_id);
CREATE INDEX voi_60_code_insee ON voi_voies_60 USING btree (com_code_insee);
CREATE INDEX voi_60_lbl ON voi_voies_60 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_60_type_lbl ON voi_voies_60 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_60_voi_id ON voi_voies_60 USING btree (voi_id);
CREATE INDEX voi_61_code_insee ON voi_voies_61 USING btree (com_code_insee);
CREATE INDEX voi_61_lbl ON voi_voies_61 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_61_type_lbl ON voi_voies_61 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_61_voi_id ON voi_voies_61 USING btree (voi_id);
CREATE INDEX voi_62_code_insee ON voi_voies_62 USING btree (com_code_insee);
CREATE INDEX voi_62_lbl ON voi_voies_62 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_62_type_lbl ON voi_voies_62 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_62_voi_id ON voi_voies_62 USING btree (voi_id);
CREATE INDEX voi_63_code_insee ON voi_voies_63 USING btree (com_code_insee);
CREATE INDEX voi_63_lbl ON voi_voies_63 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_63_type_lbl ON voi_voies_63 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_63_voi_id ON voi_voies_63 USING btree (voi_id);
CREATE INDEX voi_64_code_insee ON voi_voies_64 USING btree (com_code_insee);
CREATE INDEX voi_64_lbl ON voi_voies_64 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_64_type_lbl ON voi_voies_64 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_64_voi_id ON voi_voies_64 USING btree (voi_id);
CREATE INDEX voi_65_code_insee ON voi_voies_65 USING btree (com_code_insee);
CREATE INDEX voi_65_lbl ON voi_voies_65 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_65_type_lbl ON voi_voies_65 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_65_voi_id ON voi_voies_65 USING btree (voi_id);
CREATE INDEX voi_66_code_insee ON voi_voies_66 USING btree (com_code_insee);
CREATE INDEX voi_66_lbl ON voi_voies_66 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_66_type_lbl ON voi_voies_66 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_66_voi_id ON voi_voies_66 USING btree (voi_id);
CREATE INDEX voi_67_code_insee ON voi_voies_67 USING btree (com_code_insee);
CREATE INDEX voi_67_lbl ON voi_voies_67 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_67_type_lbl ON voi_voies_67 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_67_voi_id ON voi_voies_67 USING btree (voi_id);
CREATE INDEX voi_68_code_insee ON voi_voies_68 USING btree (com_code_insee);
CREATE INDEX voi_68_lbl ON voi_voies_68 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_68_type_lbl ON voi_voies_68 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_68_voi_id ON voi_voies_68 USING btree (voi_id);
CREATE INDEX voi_69_code_insee ON voi_voies_69 USING btree (com_code_insee);
CREATE INDEX voi_69_lbl ON voi_voies_69 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_69_type_lbl ON voi_voies_69 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_69_voi_id ON voi_voies_69 USING btree (voi_id);
CREATE INDEX voi_70_code_insee ON voi_voies_70 USING btree (com_code_insee);
CREATE INDEX voi_70_lbl ON voi_voies_70 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_70_type_lbl ON voi_voies_70 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_70_voi_id ON voi_voies_70 USING btree (voi_id);
CREATE INDEX voi_71_code_insee ON voi_voies_71 USING btree (com_code_insee);
CREATE INDEX voi_71_lbl ON voi_voies_71 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_71_type_lbl ON voi_voies_71 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_71_voi_id ON voi_voies_71 USING btree (voi_id);
CREATE INDEX voi_72_code_insee ON voi_voies_72 USING btree (com_code_insee);
CREATE INDEX voi_72_lbl ON voi_voies_72 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_72_type_lbl ON voi_voies_72 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_72_voi_id ON voi_voies_72 USING btree (voi_id);
CREATE INDEX voi_73_code_insee ON voi_voies_73 USING btree (com_code_insee);
CREATE INDEX voi_73_lbl ON voi_voies_73 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_73_type_lbl ON voi_voies_73 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_73_voi_id ON voi_voies_73 USING btree (voi_id);
CREATE INDEX voi_74_code_insee ON voi_voies_74 USING btree (com_code_insee);
CREATE INDEX voi_74_lbl ON voi_voies_74 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_74_type_lbl ON voi_voies_74 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_74_voi_id ON voi_voies_74 USING btree (voi_id);
CREATE INDEX voi_75_code_insee ON voi_voies_75 USING btree (com_code_insee);
CREATE INDEX voi_75_lbl ON voi_voies_75 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_75_type_lbl ON voi_voies_75 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_75_voi_id ON voi_voies_75 USING btree (voi_id);
CREATE INDEX voi_76_code_insee ON voi_voies_76 USING btree (com_code_insee);
CREATE INDEX voi_76_lbl ON voi_voies_76 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_76_type_lbl ON voi_voies_76 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_76_voi_id ON voi_voies_76 USING btree (voi_id);
CREATE INDEX voi_77_code_insee ON voi_voies_77 USING btree (com_code_insee);
CREATE INDEX voi_77_lbl ON voi_voies_77 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_77_type_lbl ON voi_voies_77 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_77_voi_id ON voi_voies_77 USING btree (voi_id);
CREATE INDEX voi_78_code_insee ON voi_voies_78 USING btree (com_code_insee);
CREATE INDEX voi_78_lbl ON voi_voies_78 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_78_type_lbl ON voi_voies_78 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_78_voi_id ON voi_voies_78 USING btree (voi_id);
CREATE INDEX voi_79_code_insee ON voi_voies_79 USING btree (com_code_insee);
CREATE INDEX voi_79_lbl ON voi_voies_79 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_79_type_lbl ON voi_voies_79 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_79_voi_id ON voi_voies_79 USING btree (voi_id);
CREATE INDEX voi_80_code_insee ON voi_voies_80 USING btree (com_code_insee);
CREATE INDEX voi_80_lbl ON voi_voies_80 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_80_type_lbl ON voi_voies_80 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_80_voi_id ON voi_voies_80 USING btree (voi_id);
CREATE INDEX voi_81_code_insee ON voi_voies_81 USING btree (com_code_insee);
CREATE INDEX voi_81_lbl ON voi_voies_81 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_81_type_lbl ON voi_voies_81 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_81_voi_id ON voi_voies_81 USING btree (voi_id);
CREATE INDEX voi_82_code_insee ON voi_voies_82 USING btree (com_code_insee);
CREATE INDEX voi_82_lbl ON voi_voies_82 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_82_type_lbl ON voi_voies_82 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_82_voi_id ON voi_voies_82 USING btree (voi_id);
CREATE INDEX voi_83_code_insee ON voi_voies_83 USING btree (com_code_insee);
CREATE INDEX voi_83_lbl ON voi_voies_83 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_83_type_lbl ON voi_voies_83 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_83_voi_id ON voi_voies_83 USING btree (voi_id);
CREATE INDEX voi_84_code_insee ON voi_voies_84 USING btree (com_code_insee);
CREATE INDEX voi_84_lbl ON voi_voies_84 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_84_type_lbl ON voi_voies_84 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_84_voi_id ON voi_voies_84 USING btree (voi_id);
CREATE INDEX voi_85_code_insee ON voi_voies_85 USING btree (com_code_insee);
CREATE INDEX voi_85_lbl ON voi_voies_85 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_85_type_lbl ON voi_voies_85 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_85_voi_id ON voi_voies_85 USING btree (voi_id);
CREATE INDEX voi_86_code_insee ON voi_voies_86 USING btree (com_code_insee);
CREATE INDEX voi_86_lbl ON voi_voies_86 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_86_type_lbl ON voi_voies_86 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_86_voi_id ON voi_voies_86 USING btree (voi_id);
CREATE INDEX voi_87_code_insee ON voi_voies_87 USING btree (com_code_insee);
CREATE INDEX voi_87_lbl ON voi_voies_87 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_87_type_lbl ON voi_voies_87 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_87_voi_id ON voi_voies_87 USING btree (voi_id);
CREATE INDEX voi_88_code_insee ON voi_voies_88 USING btree (com_code_insee);
CREATE INDEX voi_88_lbl ON voi_voies_88 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_88_type_lbl ON voi_voies_88 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_88_voi_id ON voi_voies_88 USING btree (voi_id);
CREATE INDEX voi_89_code_insee ON voi_voies_89 USING btree (com_code_insee);
CREATE INDEX voi_89_lbl ON voi_voies_89 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_89_type_lbl ON voi_voies_89 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_89_voi_id ON voi_voies_89 USING btree (voi_id);
CREATE INDEX voi_90_code_insee ON voi_voies_90 USING btree (com_code_insee);
CREATE INDEX voi_90_lbl ON voi_voies_90 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_90_type_lbl ON voi_voies_90 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_90_voi_id ON voi_voies_90 USING btree (voi_id);
CREATE INDEX voi_91_code_insee ON voi_voies_91 USING btree (com_code_insee);
CREATE INDEX voi_91_lbl ON voi_voies_91 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_91_type_lbl ON voi_voies_91 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_91_voi_id ON voi_voies_91 USING btree (voi_id);
CREATE INDEX voi_92_code_insee ON voi_voies_92 USING btree (com_code_insee);
CREATE INDEX voi_92_lbl ON voi_voies_92 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_92_type_lbl ON voi_voies_92 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_92_voi_id ON voi_voies_92 USING btree (voi_id);
CREATE INDEX voi_93_code_insee ON voi_voies_93 USING btree (com_code_insee);
CREATE INDEX voi_93_lbl ON voi_voies_93 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_93_type_lbl ON voi_voies_93 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_93_voi_id ON voi_voies_93 USING btree (voi_id);
CREATE INDEX voi_94_code_insee ON voi_voies_94 USING btree (com_code_insee);
CREATE INDEX voi_94_lbl ON voi_voies_94 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_94_type_lbl ON voi_voies_94 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_94_voi_id ON voi_voies_94 USING btree (voi_id);
CREATE INDEX voi_95_code_insee ON voi_voies_95 USING btree (com_code_insee);
CREATE INDEX voi_95_lbl ON voi_voies_95 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_95_type_lbl ON voi_voies_95 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_95_voi_id ON voi_voies_95 USING btree (voi_id);
CREATE INDEX voi_96_code_insee ON voi_voies_96 USING btree (com_code_insee);
CREATE INDEX voi_96_lbl ON voi_voies_96 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_96_type_lbl ON voi_voies_96 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_96_voi_id ON voi_voies_96 USING btree (voi_id);

-----------------------------------------------------------------------------------------------------------------
-- Section correspondant a la Corse et aux DOM et TOM
-----------------------------------------------------------------------------------------------------------------
CREATE INDEX tro_troncons_20_a_0_tro_id ON tro_troncons_20_a_0 USING btree (tro_id);
CREATE INDEX tro_troncons_20_a_0_voi_id_droit ON tro_troncons_20_a_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_20_a_0_voi_id_gauche ON tro_troncons_20_a_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_20_a_0_geometrie ON tro_troncons_20_a_0 USING gist (geometrie);

CREATE INDEX tro_troncons_20_b_0_tro_id ON tro_troncons_20_b_0 USING btree (tro_id);
CREATE INDEX tro_troncons_20_b_0_voi_id_droit ON tro_troncons_20_b_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_20_b_0_voi_id_gauche ON tro_troncons_20_b_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_20_b_0_geometrie ON tro_troncons_20_b_0 USING gist (geometrie);

CREATE INDEX tro_troncons_971_0_tro_id ON tro_troncons_971_0 USING btree (tro_id);
CREATE INDEX tro_troncons_971_0_voi_id_droit ON tro_troncons_971_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_971_0_voi_id_gauche ON tro_troncons_971_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_971_0_geometrie ON tro_troncons_971_0 USING gist (geometrie);

CREATE INDEX tro_troncons_972_0_tro_id ON tro_troncons_972_0 USING btree (tro_id);
CREATE INDEX tro_troncons_972_0_voi_id_droit ON tro_troncons_972_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_972_0_voi_id_gauche ON tro_troncons_972_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_972_0_geometrie ON tro_troncons_972_0 USING gist (geometrie);

CREATE INDEX tro_troncons_973_0_tro_id ON tro_troncons_973_0 USING btree (tro_id);
CREATE INDEX tro_troncons_973_0_voi_id_droit ON tro_troncons_973_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_973_0_voi_id_gauche ON tro_troncons_973_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_973_0_geometrie ON tro_troncons_973_0 USING gist (geometrie);

CREATE INDEX tro_troncons_974_0_tro_id ON tro_troncons_974_0 USING btree (tro_id);
CREATE INDEX tro_troncons_974_0_voi_id_droit ON tro_troncons_974_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_974_0_voi_id_gauche ON tro_troncons_974_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_974_0_geometrie ON tro_troncons_974_0 USING gist (geometrie);

CREATE INDEX tro_troncons_975_0_tro_id ON tro_troncons_975_0 USING btree (tro_id);
CREATE INDEX tro_troncons_975_0_voi_id_droit ON tro_troncons_975_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_975_0_voi_id_gauche ON tro_troncons_975_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_975_0_geometrie ON tro_troncons_975_0 USING gist (geometrie);

CREATE INDEX tro_troncons_976_0_tro_id ON tro_troncons_976_0 USING btree (tro_id);
CREATE INDEX tro_troncons_976_0_voi_id_droit ON tro_troncons_976_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_976_0_voi_id_gauche ON tro_troncons_976_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_976_0_geometrie ON tro_troncons_976_0 USING gist (geometrie);

CREATE INDEX tro_troncons_984_0_tro_id ON tro_troncons_984_0 USING btree (tro_id);
CREATE INDEX tro_troncons_984_0_voi_id_droit ON tro_troncons_984_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_984_0_voi_id_gauche ON tro_troncons_984_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_984_0_geometrie ON tro_troncons_984_0 USING gist (geometrie);

CREATE INDEX tro_troncons_986_0_tro_id ON tro_troncons_984_0 USING btree (tro_id);
CREATE INDEX tro_troncons_986_0_voi_id_droit ON tro_troncons_984_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_986_0_voi_id_gauche ON tro_troncons_984_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_986_0_geometrie ON tro_troncons_984_0 USING gist (geometrie);

CREATE INDEX tro_troncons_987_0_tro_id ON tro_troncons_987_0 USING btree (tro_id);
CREATE INDEX tro_troncons_987_0_voi_id_droit ON tro_troncons_987_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_987_0_voi_id_gauche ON tro_troncons_987_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_987_0_geometrie ON tro_troncons_987_0 USING gist (geometrie);

CREATE INDEX tro_troncons_988_0_tro_id ON tro_troncons_988_0 USING btree (tro_id);
CREATE INDEX tro_troncons_988_0_voi_id_droit ON tro_troncons_988_0 USING btree (voi_id_droit);
CREATE INDEX tro_troncons_988_0_voi_id_gauche ON tro_troncons_988_0 USING btree (voi_id_gauche);
CREATE INDEX tro_troncons_988_0_geometrie ON tro_troncons_988_0 USING gist (geometrie);

CREATE INDEX vhi_20_a_voi_id_precedent ON vhi_voies_historisee_20_a USING btree (voi_id_precedent);
CREATE INDEX vhi_20_b_voi_id_precedent ON vhi_voies_historisee_20_b USING btree (voi_id_precedent);
CREATE INDEX vhi_971_voi_id_precedent ON vhi_voies_historisee_971 USING btree (voi_id_precedent);
CREATE INDEX vhi_972_voi_id_precedent ON vhi_voies_historisee_972 USING btree (voi_id_precedent);
CREATE INDEX vhi_973_voi_id_precedent ON vhi_voies_historisee_973 USING btree (voi_id_precedent);
CREATE INDEX vhi_974_voi_id_precedent ON vhi_voies_historisee_974 USING btree (voi_id_precedent);
CREATE INDEX vhi_975_voi_id_precedent ON vhi_voies_historisee_975 USING btree (voi_id_precedent);
CREATE INDEX vhi_976_voi_id_precedent ON vhi_voies_historisee_976 USING btree (voi_id_precedent);
CREATE INDEX vhi_984_voi_id_precedent ON vhi_voies_historisee_984 USING btree (voi_id_precedent);
CREATE INDEX vhi_986_voi_id_precedent ON vhi_voies_historisee_986 USING btree (voi_id_precedent);
CREATE INDEX vhi_987_voi_id_precedent ON vhi_voies_historisee_987 USING btree (voi_id_precedent);
CREATE INDEX vhi_988_voi_id_precedent ON vhi_voies_historisee_988 USING btree (voi_id_precedent);

CREATE INDEX voi_20_a_code_insee ON voi_voies_20_a USING btree (com_code_insee);
CREATE INDEX voi_20_a_lbl ON voi_voies_20_a USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_20_a_type_lbl ON voi_voies_20_a USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_20_a_voi_id ON voi_voies_20_a USING btree (voi_id);

CREATE INDEX voi_20_b_code_insee ON voi_voies_20_b USING btree (com_code_insee);
CREATE INDEX voi_20_b_lbl ON voi_voies_20_b USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_20_b_type_lbl ON voi_voies_20_b USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_20_b_voi_id ON voi_voies_20_b USING btree (voi_id);

CREATE INDEX voi_971_code_insee ON voi_voies_971 USING btree (com_code_insee);
CREATE INDEX voi_971_lbl ON voi_voies_971 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_971_type_lbl ON voi_voies_971 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_971_voi_id ON voi_voies_971 USING btree (voi_id);

CREATE INDEX voi_972_code_insee ON voi_voies_972 USING btree (com_code_insee);
CREATE INDEX voi_972_lbl ON voi_voies_972 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_972_type_lbl ON voi_voies_972 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_972_voi_id ON voi_voies_972 USING btree (voi_id);

CREATE INDEX voi_973_code_insee ON voi_voies_973 USING btree (com_code_insee);
CREATE INDEX voi_973_lbl ON voi_voies_973 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_973_type_lbl ON voi_voies_973 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_973_voi_id ON voi_voies_973 USING btree (voi_id);

CREATE INDEX voi_974_code_insee ON voi_voies_974 USING btree (com_code_insee);
CREATE INDEX voi_974_lbl ON voi_voies_974 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_974_type_lbl ON voi_voies_974 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_974_voi_id ON voi_voies_974 USING btree (voi_id);

CREATE INDEX voi_975_code_insee ON voi_voies_975 USING btree (com_code_insee);
CREATE INDEX voi_975_lbl ON voi_voies_975 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_975_type_lbl ON voi_voies_975 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_975_voi_id ON voi_voies_975 USING btree (voi_id);

CREATE INDEX voi_976_code_insee ON voi_voies_976 USING btree (com_code_insee);
CREATE INDEX voi_976_lbl ON voi_voies_976 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_976_type_lbl ON voi_voies_976 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_976_voi_id ON voi_voies_976 USING btree (voi_id);

CREATE INDEX voi_984_code_insee ON voi_voies_984 USING btree (com_code_insee);
CREATE INDEX voi_984_lbl ON voi_voies_984 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_984_type_lbl ON voi_voies_984 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_984_voi_id ON voi_voies_984 USING btree (voi_id);

CREATE INDEX voi_986_code_insee ON voi_voies_986 USING btree (com_code_insee);
CREATE INDEX voi_986_lbl ON voi_voies_986 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_986_type_lbl ON voi_voies_986 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_986_voi_id ON voi_voies_986 USING btree (voi_id);

CREATE INDEX voi_987_code_insee ON voi_voies_987 USING btree (com_code_insee);
CREATE INDEX voi_987_lbl ON voi_voies_987 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_987_type_lbl ON voi_voies_987 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_987_voi_id ON voi_voies_987 USING btree (voi_id);

CREATE INDEX voi_988_code_insee ON voi_voies_988 USING btree (com_code_insee);
CREATE INDEX voi_988_lbl ON voi_voies_988 USING btree (voi_lbl_sans_articles);
CREATE INDEX voi_988_type_lbl ON voi_voies_988 USING btree (voi_lbl_sans_articles, voi_type_de_voie);
CREATE INDEX voi_988_voi_id ON voi_voies_988 USING btree (voi_id);
