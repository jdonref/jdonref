--
-- J. Moquet 29/10/2008
-- Permet de créer les procédures stockées de JDONREF
-- utilise la librairie levenstein.so
-- Compatible JDONREF v2.1
--

CREATE OR REPLACE FUNCTION index_derniermot(arg1 character varying)
  RETURNS integer AS
'$libdir/levenstein.so', 'index_derniermot'
  LANGUAGE 'c' VOLATILE;

CREATE OR REPLACE FUNCTION definitNotes(codepostal int,commune int,determinant int,libelle int,typedevoie int,numero int,arrondissement int)
 RETURNS integer AS
'$libdir/levenstein.so','definitNotes'
 LANGUAGE 'c' VOLATILE STRICT;

 CREATE OR REPLACE FUNCTION definitNotesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int)
 RETURNS integer AS
'$libdir/levenstein.so','definitNotesPOIZON'
 LANGUAGE 'c' VOLATILE STRICT;

CREATE OR REPLACE FUNCTION definitPourcentages(codepostal int,commune int,determinant int,libelle int,typedevoie int)
 RETURNS integer AS
'$libdir/levenstein.so','definitPourcentages'
 LANGUAGE 'c' VOLATILE STRICT;
 
CREATE OR REPLACE FUNCTION definitPourcentagesPOIZON(cle int,poizon int,ligne2 int,ligne3 int,ligne4 int,ligne5 int,ligne6 int,ligne7 int)
 RETURNS integer AS
'$libdir/levenstein.so','definitPourcentagesPOIZON'
 LANGUAGE 'c' VOLATILE STRICT;

CREATE OR REPLACE FUNCTION definitMalus(mot int,motdirecteur int)
 RETURNS integer AS
'$libdir/levenstein.so','definitMalus'
 LANGUAGE 'c' VOLATILE STRICT;

CREATE OR REPLACE FUNCTION definitDivers(taille_abbr int)
 RETURNS integer AS
'$libdir/levenstein.so','definitDivers'
 LANGUAGE 'c' VOLATILE STRICT;
 
CREATE OR REPLACE FUNCTION definitDiversPOIZON(taille_abbr int)
 RETURNS integer AS
'$libdir/levenstein.so','definitDiversPOIZON'
 LANGUAGE 'c' VOLATILE STRICT;

CREATE OR REPLACE FUNCTION position_levenstein_joker(character varying,character varying,int,int)
  RETURNS integer AS
'$libdir/levenstein.so', 'position_levenstein_joker'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_pourcentage_seuil_total(character varying,character varying,int,int)
  RETURNS integer AS
'$libdir/levenstein.so', 'note_pourcentage_seuil_total'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_type_de_voie(character varying,character varying,int,int,int)
  RETURNS integer AS
'$libdir/levenstein.so', 'note_type_de_voie'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_pourcentage_seuil(character varying,character varying,int,int)
  RETURNS integer AS
'$libdir/levenstein.so', 'note_pourcentage_seuil'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_pourcentage_seuil_n(character varying,character varying,int,int)
  RETURNS integer AS
'$libdir/levenstein.so', 'note_pourcentage_seuil_n'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION distance_levenstein(arg1 character varying, arg2 character varying)
  RETURNS integer AS
'$libdir/levenstein.so', 'distance_levenstein'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION distance_libelle(arg1 character varying, arg2 character varying, correspondanceparmot integer, maluspasdemot integer, maluspasdemotdirecteur integer)
  RETURNS integer AS
'$libdir/levenstein.so', 'distance_libelle'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION distance_type_de_voie(arg1 character varying, arg2 character varying, taille_abbreviation_minimale integer, distance_par_defaut integer)
  RETURNS integer AS
'$libdir/levenstein.so', 'distance_type_de_voie'
  LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_voie_codepostal_commune(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
                                  commune character varying,communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying,
                                  arrondissement character varying,refestarrondissement boolean)
 RETURNS integer AS '$libdir/levenstein.so', 'note_voie_codepostal_commune'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_voie_codepostal(motdeterminant character varying,motdeterminantreferentiel character varying,
                                  libelle character varying,libellesansarticlereferentiel character varying,
                                  typedevoie character varying,typedevoiereferentiel character varying,
				  communereferentiel character varying,
                                  codepostal character varying,codepostalreferentiel character varying)
 RETURNS integer AS '$libdir/levenstein.so', 'note_voie_codepostal'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_codepostal(codepostal character varying,codepostalreference character varying)
 RETURNS integer AS '$libdir/levenstein.so', 'note_codepostal'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_codepostal_commune(commune character varying,communereferentiel character varying,
                                                   codepostal character varying,codepostalreferentiel character varying,
                                                   arrondissement character varying,refestarrondissement boolean)
 RETURNS integer AS '$libdir/levenstein.so', 'note_codepostal_commune'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_commune(commune character varying,communereferentiel character varying,
                                        arrondissement character varying, codepostalreference character varying,refestarrondissement boolean)
 RETURNS integer AS '$libdir/levenstein.so', 'note_commune'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_commune_seul(commune character varying,communereferentiel character varying)
 RETURNS integer AS '$libdir/levenstein.so', 'note_commune_seul'
 LANGUAGE 'c' IMMUTABLE STRICT;

CREATE OR REPLACE FUNCTION note_arrondissement(arrondissement character varying,codepostalreference character varying,refestarrondissement boolean)
 RETURNS integer AS '$libdir/levenstein.so', 'note_arrondissement'
 LANGUAGE 'c' IMMUTABLE CALLED ON NULL INPUT;

 CREATE OR REPLACE FUNCTION note_cle_poizon(cle character varying,clereference character varying,
                                            poizon character varying,poizonreference character varying,
                                            ligne2 character varying,ligne2reference character varying,											
                                            ligne3 character varying,ligne3reference character varying,
                                            ligne4 character varying,ligne4reference character varying,
                                            ligne5 character varying,ligne5reference character varying,
                                            ligne6 character varying,ligne6reference character varying,
                                            ligne7 character varying,ligne7reference character varying)
 RETURNS integer AS '$libdir/levenstein.so', 'note_cle_poizon'
 LANGUAGE 'c' IMMUTABLE STRICT;
 
CREATE OR REPLACE FUNCTION contexte(arg1 character varying, arg2 character varying, arg3 integer)
RETURNS integer AS
'$libdir/levenstein.so','contexte' LANGUAGE 'c' IMMUTABLE STRICT;