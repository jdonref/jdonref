/*
 * Version 2.1.5 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
 * julien.moquet@interieur.gouv.fr
 * 
 * Ce logiciel est un service web servant à valider et géocoder des adresses postales.
 * Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant 
 * les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier 
 * et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que 
 * diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
 * En contrepartie de l'accessibilité au code source et des droits de copie, de 
 * modification et de redistribution accordés par cette licence, il n'est offert aux 
 * utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une 
 * responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits 
 * patrimoniaux et les concédants successifs.
 * A cet égard l'attention de l'utilisateur est attirée sur les risques associés au 
 * chargement,  à l'utilisation,  à la modification et/ou au développement et à la 
 * reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel 
 * libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des 
 * développeurs et des professionnels avertis possédant  des  connaissances 
 * informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
 * l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
 * sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
 * et l'exploiter dans les mêmes conditions de sécurité. 
 * Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris 
 * connaissance de la licence CeCILL, et que vous en avez accepté les termes.
 */
package jdonrefv3charge;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 * Permet de générer des échantillons d'adresse pour chaque profil.
 * Un répertoire de travail est utilisé, qui doit contenir un sous répertoire "échantillons".
 * Les fichiers de configuration doivent faire parti du répertoire de travail.
 * @author jmoquet
 */
public class Echantillonneur {

    Parametres parametres = new Parametres();

    /**
     * Constructeur par défaut.
     */
    public Echantillonneur() throws ClassNotFoundException {
    }

    /**
     * Permet de charger les fichiers de configuration:
     * <ul>
     *     <li>profils.xml</li>
     *     <li>modeles.xml</li>
     *     <li>connections.xml</li>
     * </ul>
     */
    public void load(String rep) throws JDOMException, IOException, Exception {
        parametres.loadConnection(rep + "/connections.xml");
        parametres.loadModeles(rep + "/modeles.xml");
        parametres.loadProfils(rep + "/profils.xml");
    }

    /**
     * Obtient des adresses aléatoires parmi le département spécifié.
     * @param count le nombre d'adresses à obtenir.
     */
    public ArrayList<Adresse> getAdresses(String departement, Connection connection, int countDpt, Payss payss)
            throws SQLException {
        final String nomPays = payss.obtientPays();
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_type_de_voie,voi_lbl,cdp_code_postal,com_nom,voi_nom_desab,voi_min_numero,voi_max_numero,voies.com_code_insee,pay_sov_a3,");
        sb.append("st_x(st_closestpoint(troncons.geometrie,st_centroid(troncons.geometrie))),");
        sb.append("st_y(st_closestpoint(troncons.geometrie,st_centroid(troncons.geometrie)))");
        sb.append("FROM pay_pays,voi_voies_");
        sb.append(departement);
        sb.append(" AS voies, tro_troncons_");
        sb.append(departement);
        sb.append("_0 AS troncons,com_communes");
        sb.append(" WHERE pay_nom_fr = ? AND (voi_id_droit=voies.voi_id OR voi_id_gauche=voies.voi_id) ");
        sb.append("AND com_communes.com_code_insee=voies.com_code_insee ");
        sb.append("AND NOT (voies.t1<com_communes.t0 OR com_communes.t1<voies.t0) ");
        sb.append("ORDER BY random() LIMIT ?");
        PreparedStatement st = connection.prepareStatement(sb.toString());
        st.setString(1, nomPays);
        st.setInt(2, countDpt);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) {
            rs.close();
            st.close();
            return new ArrayList<Adresse>();
        }

        ArrayList<Adresse> adresses = new ArrayList<Adresse>();
        do {
            Adresse res = new Adresse();
            res.typedevoie = rs.getString(1);
            res.libelle = rs.getString(2);
            res.codepostal = rs.getString(3);
            res.ville = rs.getString(4);
            res.numeromin = rs.getInt(6);
            res.numeromax = rs.getInt(7);
            res.codeinsee = rs.getString(8);
            res.codeSovAc3 = rs.getString(9);
            res.x = String.valueOf(rs.getDouble(10));
            res.y = String.valueOf(rs.getDouble(11));
            res.departement = departement;

            // Pour extraire l'article, il faut travailler le nom.
            String nom = rs.getString(5);
            nom = nom.substring(res.typedevoie.length());
            nom = nom.substring(0, nom.length() - res.libelle.length());
            res.article = nom.trim();

            res.pays = nomPays;
            res.calculeLignes();

            adresses.add(res);
        } while (rs.next());

        rs.close();
        st.close();

        return adresses;
    }

    public ArrayList<Adresse> getData(String departement, Connection connection, int countPoizon, Payss payss)
            throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT poizon_cle, poizon_lbl, poizon_donnee4, poizon_donnee6, poizon_id1,poizon_id6, poizon_id7," +
                " voi_type_de_voie,voi_lbl,cdp_code_postal,com_nom,voi_nom_desab,voi_min_numero,voi_max_numero," +
                " st_x(st_centroid(poizon.geometrie)),st_y(st_centroid(poizon.geometrie))" +
                " FROM poizon, voi_voies_");
        sb.append(departement);
        sb.append(" AS voies,com_communes");
        sb.append(" WHERE com_communes.com_code_insee=poizon_id6");
        sb.append(" AND com_communes.com_code_insee=voies.com_code_insee AND NOT (voies.t1<com_communes.t0 OR com_communes.t1<voies.t0)");
        sb.append(" ORDER BY random() LIMIT ?");

        PreparedStatement st = connection.prepareStatement(sb.toString());
        st.setInt(1, countPoizon);
        ResultSet rs = st.executeQuery();

        if (!rs.next()) {
            rs.close();
            st.close();
            return getAdresses(departement, connection, countPoizon, payss);
        }

        ArrayList<Adresse> adresses = new ArrayList<Adresse>();
        do {
            Adresse res = new Adresse();
            res.clePoizon = rs.getString(1);
            res.libellePoizon = rs.getString(2);
            res.ligne4Poizon = rs.getString(3);
            res.ligne6Poizon = rs.getString(4);
            res.idPoizon = rs.getString(5);
            res.codeinsee = rs.getString(6);
            res.codeSovAc3 = rs.getString(7);
            res.typedevoie = rs.getString(8);
            res.libelle = rs.getString(9);
            res.codepostal = rs.getString(10);
            res.ville = rs.getString(11);
            res.numeromin = rs.getInt(13);
            res.numeromax = rs.getInt(14);
            res.x = String.valueOf(rs.getDouble(15));
            res.y = String.valueOf(rs.getDouble(16));
            res.departement = departement;

            // Pour extraire l'article, il faut travailler le nom.
            String nom = rs.getString(12);
            nom = nom.substring(res.typedevoie.length());
            nom = nom.substring(0, nom.length() - res.libelle.length());
            res.article = nom.trim();
            res.pays = payss.obtientPays();
            res.calculeLignes();
            adresses.add(res);
        } while (rs.next());

        rs.close();
        st.close();

        return adresses;
    }

    /**
     * Obtient une adresse aléatoire du département spécifié.
     * @param departement département sur deux chiffres
     */
    public Adresse getAdresse(String departement, Connection connection) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT voi_type_de_voie,voi_lbl,cdp_code_postal,com_nom,voi_nom_desab,voi_min_numero,voi_max_numero FROM voi_voies_");
        sb.append(departement);
        sb.append(" AS voies,com_communes WHERE com_communes.com_code_insee=voies.com_code_insee AND NOT (voies.t1<com_communes.t0 OR com_communes.t1<voies.t0) ORDER BY random() LIMIT 1");

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sb.toString());

        if (!rs.next()) {
            return null;
        }

        Adresse res = new Adresse();
        res.typedevoie = rs.getString(1);
        res.libelle = rs.getString(2);
        res.codepostal = rs.getString(3);
        res.ville = rs.getString(4);
        res.numeromin = rs.getInt(6);
        res.numeromax = rs.getInt(7);
        res.departement = departement;

        // Pour extraire l'article, il faut travailler le nom.
        String nom = rs.getString(5);
        nom = nom.substring(res.typedevoie.length());
        nom = nom.substring(0, nom.length() - res.libelle.length());
        res.article = nom.trim();

        rs.close();
        st.close();

        //res.calculeLignes();

        return res;
    }

    /**
     * Génère un échantillon pour le profil spécifié.<br>
     * La génération suit les deux étapes suivantes:
     * <ul>
     * <li>Les adresses sont obtenues aléatoirement dans le référentiel de JDONREF, dans les départements aléatoirement choisi.</li>
     * <li>Elles sont ensuite transformées pour adopter un modèle aléatoire, une structure aléatoire, et des fautes aléatoires, avant d'être écrite dans l'échantillon.</li>
     * </ul>
     * @param echantillon le nombre d'adresse par échantillon
     */
    public void doone2(String rep, Profil profil, int echantillon) throws UnsupportedEncodingException, FileNotFoundException, IOException, SQLException {
        Connection connection = parametres.connectionStruct.connecte();

        int[] repartitionDpt = profil.departements.obtientRepartition(echantillon);
        ArrayList<Adresse> adresses = new ArrayList<Adresse>();

        // Obtient les lignes de la base
        for (int i = 0; i < profil.departements.items.size(); i++) {
            adresses.addAll(getData(profil.departements.getDepartement(i).departement, connection, repartitionDpt[i], profil.pays));
        }
        connection.close();

        String filePath = rep + "/echantillons/" + profil.nom + ".txt";
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));

        // Puis les transforme et les écrits dans le fichier échantillon
        for (int i = 0; i < adresses.size(); i++) {
            // obtient une adresse dans ce département
            Adresse a = adresses.get(i);

            // choisi un modèle aléatoire.
            Modele m = parametres.modeles.getModele(profil.getModele());

            // modifie l'adresse selon ce modèle
            m.genereAdresse(a);

            // Calcule les lignes d'adresse
            a.calculeLignes();

            // adopte la structure nécessaire avec déformation éventuelle.
            a = profil.structure.altereStructure(a);
            // ajoute quelques fautes
            int fautes = profil.getFautes();
            a = a.introduitFautes(fautes, m.pays ? 7 : 6);
            writer.write(a.ecrit());
            writer.write("\r\n");
        }

        writer.close();
    }

    /**
     * Génère des échantillons pour chaque profil.
     * @param rep
     */
    public void doall(String rep, int echantillon) throws UnsupportedEncodingException, FileNotFoundException, IOException, SQLException {
        for (int i = 0; i < parametres.profils.profils.size(); i++) {
            doone2(rep, parametres.profils.profils.get(i), echantillon);
        }
    }

    /**
     * Teste la classe.
     * @param args
     */
    public static void main(String[] args) {
        boolean echantillon = true;
        boolean getadresse = false;
        boolean doone = false;

        if (echantillon) {
            try {
                System.out.println("Teste doall() avec des fichiers xml.");

                Echantillonneur e = new Echantillonneur();
                e.load("test");
                e.doall("test", 2000);
            } catch (JDOMException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (doone) {
            try {
                // Teste l'échantilloneur sur 4 départements avec 2 modèles d'écriture.
                System.out.println("Teste doone()");

                Echantillonneur e = new Echantillonneur();

                e.parametres.connectionStruct.server = "srv-sigpp";
                e.parametres.connectionStruct.port = "5430";
                e.parametres.connectionStruct.database = "db_navteq_2005";
                e.parametres.connectionStruct.password = "jdonref";
                e.parametres.connectionStruct.user = "jdonref";

                Departement d = new Departement();
                d.departement = "75";
                d.probabilite = 70;
                Departement d1 = new Departement();
                d1.departement = "92";
                d1.probabilite = 10;
                Departement d2 = new Departement();
                d2.departement = "93";
                d2.probabilite = 10;
                Departement d3 = new Departement();
                d3.departement = "94";
                d3.probabilite = 10;

                Profil p = new Profil();
                p.nom = "test";
                p.probazero = 80;
                p.probaun = 7;
                p.qualitecontenu.put("test", new Integer(40));
                p.qualitecontenu.put("test2", new Integer(60));
                e.parametres.profils.profils.add(p);
                p.structure = new StructureFormat2();
                p.departements.addDepartement(d);
                p.departements.addDepartement(d1);
                p.departements.addDepartement(d2);
                p.departements.addDepartement(d3);
                ((StructureFormat2) p.structure).proba1 = 80;

                Modele m = new Modele();
                m.nom = "test";
                m.typedevoie = true;
                m.libelle = true;
                m.ville = true;
                e.parametres.modeles.modeles.put("test", m);

                Modele m2 = new Modele();
                m2.nom = "test2";
                m2.numero = true;
                m2.typedevoie = true;
                m2.libelle = true;
                m2.ville = true;
                e.parametres.modeles.modeles.put("test2", m2);

                e.doone2("test", p, 100);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (getadresse) {
            try {
                System.out.println("Teste getAdresse()");

                ConnectionStruct cs = new ConnectionStruct();
                cs.server = "srv-sigpp";
                cs.port = "5430";
                cs.database = "db_navteq_2005";
                cs.password = "jdonref";
                cs.user = "jdonref";
                Connection connection = cs.connecte();

                Echantillonneur e = new Echantillonneur();

                Random r = new Random(Calendar.getInstance().getTimeInMillis());

                for (int i = 0; i < 5; i++) {
                    String dpt = Integer.toString(r.nextInt(60) + 21);
                    Adresse a = e.getAdresse(dpt, connection);
                    a.calculeLignes();
                    System.out.println(a.ligne4 + " " + a.ligne6);
                }
                Adresse a = e.getAdresse("75", connection);
                a.calculeLignes();
                System.out.println(a.ligne4 + " " + a.ligne6);
                e.getAdresse("75", connection);
                a.calculeLignes();
                System.out.println(a.ligne4 + " " + a.ligne6);
                e.getAdresse("75", connection);
                a.calculeLignes();
                System.out.println(a.ligne4 + " " + a.ligne6);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Echantillonneur.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
