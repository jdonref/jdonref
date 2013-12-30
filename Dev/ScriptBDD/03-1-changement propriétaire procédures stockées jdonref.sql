--
-- J.Moquet 29 octobre 2008
-- Permet de changer le propriétaire des procédures stockées de jdonref
--
-- version compatible JDONREF 2.1
--
ALTER FUNCTION index_derniermot(arg1 character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION definitNotes(codepostal int,commune int,determinant int,libelle int,typedevoie int,numero int,arrondissement int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitNotesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitPourcentages(codepostal int,commune int,determinant int,libelle int,typedevoie int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitPourcentagesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitMalus(mot int,motdirecteur int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitDivers(taille_abbr int) OWNER TO jdonrefadmin;
ALTER FUNCTION definitDiversPOIZON(taille_abbr int) OWNER TO jdonrefadmin;
ALTER FUNCTION position_levenstein_joker(character varying,character varying,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION note_pourcentage_seuil_total(character varying,character varying,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION note_type_de_voie(character varying,character varying,int,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION note_pourcentage_seuil(character varying,character varying,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION note_pourcentage_seuil_n(character varying,character varying,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION note_pourcentage_seuil_total(character varying,character varying,int,int) OWNER TO jdonrefadmin;
ALTER FUNCTION distance_levenstein(arg1 character varying, arg2 character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION distance_libelle(arg1 character varying, arg2 character varying, correspondanceparmot integer, maluspasdemot integer, maluspasdemotdirecteur integer) OWNER TO jdonrefadmin;
ALTER FUNCTION distance_type_de_voie(arg1 character varying, arg2 character varying, taille_abbreviation_minimale integer, distance_par_defaut integer) OWNER TO jdonrefadmin;
ALTER FUNCTION note_voie_codepostal_commune(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
                                  commune character varying,communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying,
                                  arrondissement character varying,refestarrondissement boolean) OWNER TO jdonrefadmin;
ALTER FUNCTION note_voie_codepostal(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
				  communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION note_codepostal(codepostal character varying,codepostalreference character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION distance_levenstein(arg1 character varying, arg2 character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION note_codepostal_commune(commune character varying,communereferentiel character varying,
                                                   codepostal character varying,codepostalreferentiel character varying,
                                                   arrondissement character varying,refestarrondissement boolean) OWNER TO jdonrefadmin;
ALTER FUNCTION note_commune(commune character varying,communereferentiel character varying,
                                        arrondissement character varying, codepostalreference character varying,refestarrondissement boolean) OWNER TO jdonrefadmin;
ALTER FUNCTION note_commune_seul(commune character varying,communereferentiel character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION note_codepostal(codepostal character varying,codepostalreference character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION note_arrondissement(arrondissement character varying,codepostalreference character varying,refestarrondissement boolean) OWNER TO jdonrefadmin;
ALTER FUNCTION note_cle_poizon(cle character varying,clereference character varying,
                                            poizon character varying,poizonreference character varying,
                                            ligne2 character varying,ligne2reference character varying,											
                                            ligne3 character varying,ligne3reference character varying,
                                            ligne4 character varying,ligne4reference character varying,
                                            ligne5 character varying,ligne5reference character varying,
                                            ligne6 character varying,ligne6reference character varying,
                                            ligne7 character varying,ligne7reference character varying) OWNER TO jdonrefadmin;
ALTER FUNCTION contexte(arg1 character varying, arg2 character varying, arg3 integer) OWNER TO jdonrefadmin;