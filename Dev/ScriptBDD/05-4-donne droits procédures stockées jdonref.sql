--
-- J.Moquet 29/10/08
-- Permet de donner les droits aux procédures stockées de JDONREF
-- utilise levenstein.so
-- Compatible JDONREF v2.1
--

-- procédures stockées de JDONREF
GRANT EXECUTE ON FUNCTION index_derniermot(arg1 character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION definitNotes(codepostal int,commune int,determinant int,libelle int,typedevoie int,numero int,arrondissement int) TO jdonref;
GRANT EXECUTE ON FUNCTION definitNotesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int) TO jdonref;
GRANT EXECUTE ON FUNCTION definitPourcentages(codepostal int,commune int,determinant int,libelle int,typedevoie int) TO jdonref;
GRANT EXECUTE ON FUNCTION definitPourcentagesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int) TO jdonref;
GRANT EXECUTE ON FUNCTION definitMalus(mot int,motdirecteur int) to jdonref;
GRANT EXECUTE ON FUNCTION definitDivers(taille_abbr int) TO jdonref;
GRANT EXECUTE ON FUNCTION definitDiversPOIZON(taille_abbr int) TO jdonrefadmin;
GRANT EXECUTE ON FUNCTION position_levenstein_joker(character varying,character varying,int,int) TO jdonref;
GRANT EXECUTE ON FUNCTION note_pourcentage_seuil_total(character varying,character varying,int,int) TO jdonref;
GRANT EXECUTE ON FUNCTION note_type_de_voie(character varying,character varying,int,int,int) TO jdonref;
GRANT EXECUTE ON FUNCTION note_pourcentage_seuil(character varying,character varying,int,int) TO jdonref;
GRANT EXECUTE ON FUNCTION note_pourcentage_seuil_n(character varying,character varying,int,int) TO jdonref;
GRANT EXECUTE ON FUNCTION distance_levenstein(arg1 character varying, arg2 character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION distance_libelle(arg1 character varying, arg2 character varying, correspondanceparmot integer, maluspasdemot integer, maluspasdemotdirecteur integer) TO jdonref;
GRANT EXECUTE ON FUNCTION distance_type_de_voie(arg1 character varying, arg2 character varying, taille_abbreviation_minimale integer, distance_par_defaut integer) TO jdonref;
GRANT EXECUTE ON FUNCTION note_voie_codepostal_commune(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
                                  commune character varying,communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying,
                                  arrondissement character varying,refestarrondissement boolean) TO jdonref;
GRANT EXECUTE ON FUNCTION note_voie_codepostal(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
				  communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION note_codepostal(codepostal character varying,codepostalreference character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION distance_levenstein(arg1 character varying, arg2 character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION note_codepostal_commune(commune character varying,communereferentiel character varying,
                                                   codepostal character varying,codepostalreferentiel character varying,
                                                   arrondissement character varying,refestarrondissement boolean) TO jdonref;
GRANT EXECUTE ON FUNCTION note_commune(commune character varying,communereferentiel character varying,
                                        arrondissement character varying, codepostalreference character varying,refestarrondissement boolean) TO jdonref;
GRANT EXECUTE ON FUNCTION note_commune_seul(commune character varying,communereferentiel character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION note_codepostal(codepostal character varying,codepostalreference character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION note_arrondissement(arrondissement character varying,codepostalreference character varying,refestarrondissement boolean) TO jdonref;
GRANT EXECUTE ON FUNCTION note_cle_poizon(cle character varying,clereference character varying,
                                            poizon character varying,poizonreference character varying,
                                            ligne2 character varying,ligne2reference character varying,											
                                            ligne3 character varying,ligne3reference character varying,
                                            ligne4 character varying,ligne4reference character varying,
                                            ligne5 character varying,ligne5reference character varying,
                                            ligne6 character varying,ligne6reference character varying,
                                            ligne7 character varying,ligne7reference character varying) TO jdonref;
GRANT EXECUTE ON FUNCTION contexte(arg1 character varying, arg2 character varying, arg3 integer) TO jdonref;