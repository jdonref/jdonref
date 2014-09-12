package ppol.jdonref.poizon;

import ppol.jdonref.poizon.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import ppol.jdonref.Algos;
import ppol.jdonref.GestionConnection;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.dao.PoizonDao;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.mots.GestionMots;
import ppol.jdonref.dao.PoizonBean;

/**
 *
 * @author marcanhe
 */
public final class GestionPoizon {

    private final JDONREFParams params;
    private final GestionMots gestionMots;
    private final GestionConnection gestionConnection;
    protected Services servicesTree = null;

    public GestionPoizon(JDONREFParams params, GestionMots gestionMots, GestionConnection gestionConnection) {
        this.params = params;
        this.gestionMots = gestionMots;
        this.gestionConnection = gestionConnection;
        
        try
        {
            this.servicesTree = Services.getInstance(params.obtientConfigPath());
        }
        catch(JDONREFv3Exception ex)
        {
            Logger.getLogger("GestionPoizon").fatal("La lecture du fichier services.xml a posé problème.",ex);
        }
    }

    public List<PoizonBean> normalise(
            int[] services,
            int operation,
            String[] donnees)
    {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        final String donnee1 = normalise(operation, donnees[0]);
        for (int service : services)
        {
            final PoizonBean poizon = new PoizonBean();
            
            poizon.setDonnee1(donnee1);
            poizon.setDonnee2((donnees.length > 1) ? donnees[1] : "");
            poizon.setDonnee3((donnees.length > 2) ? donnees[2] : "");
            poizon.setDonnee4((donnees.length > 3) ? donnees[3] : "");
            poizon.setDonnee5((donnees.length > 4) ? donnees[4] : "");
            poizon.setDonnee6((donnees.length > 5) ? donnees[5] : "");
            poizon.setDonnee7((donnees.length > 6) ? donnees[6] : "");
            poizon.setService(service);
            listRet.add(poizon);
        }

        return listRet;
    }

    public List<PoizonBean> geocodeInverse(
            int[] services,
            String[] position,
            double distance,
            Date date,
            int projection)
            throws JDONREFv3Exception {
        services = servicesTree.getClesByService(services);
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        int[] servicesLeaves = servicesTree.getLeaves(services);
        HashMap<Integer,Integer> serviceByCle = servicesTree.getHashMapServiceByCle(servicesLeaves);
        final PoizonDao dao = new PoizonDao(params, gestionConnection,servicesTree);
        dao.setServiceByCle(serviceByCle);
        listRet.addAll(dao.findGeocodageInverse(servicesLeaves, position, distance, date, projection));
        
        return listRet;
    }

    public List<PoizonBean> geocode(
            int[] services,
            String[] donnees,
            String[] ids,
            Date date,
            int projection)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        if (date == null) {
            final Calendar calendar = GregorianCalendar.getInstance();
            date = calendar.getTime();
        }
        services = servicesTree.getClesByService(services);
        int[] servicesLeaves = servicesTree.getLeaves(services);
        HashMap<Integer,Integer> serviceByCle = servicesTree.getHashMapServiceByCle(servicesLeaves);
        final PoizonDao dao = new PoizonDao(params, gestionConnection,servicesTree);
        dao.setServiceByCle(serviceByCle);
        listRet.addAll(dao.findGeocodage(servicesLeaves, donnees, ids, date, projection));

        return listRet;
    }

    public List<PoizonBean> valide(
            int[] services,
            int operation,
            String[] donnees,
            String[] ids,
            boolean force,
            Date date)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        if (date == null) {
            final Calendar calendar = GregorianCalendar.getInstance();
            date = calendar.getTime();
        }
        services = servicesTree.getClesByService(services);
        int[] servicesLeaves = servicesTree.getLeaves(services);
        HashMap<Integer,Integer> serviceByCle = servicesTree.getHashMapServiceByCle(servicesLeaves);
        final String donnee1 = normalise(operation, donnees[0]);
        final Ligne1 ligne1 = Ligne1.getNewInstance(donnee1, servicesTree, gestionMots);
        final PoizonDao dao = new PoizonDao(params, gestionConnection,servicesTree);
        dao.setServiceByCle(serviceByCle);
        listRet.addAll(dao.findValidation(servicesLeaves, donnees, ids, force, date, ligne1));

        return listRet;
    }

    public List<PoizonBean> revalide(
            int[] services,
            String[] ids,
            Date dateParam,
            Date dateOption)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        services = servicesTree.getClesByService(services);
        int[] servicesLeaves = servicesTree.getLeaves(services);
        HashMap<Integer,Integer> serviceByCle = servicesTree.getHashMapServiceByCle(servicesLeaves);
        final PoizonDao dao = new PoizonDao(params, gestionConnection,servicesTree);
        dao.setServiceByCle(serviceByCle);
        listRet.addAll(dao.findRevalidation(servicesLeaves, ids, dateParam, dateOption));

        return listRet;
    }

    public List<PoizonBean> decoupe(
            int[] services,
            int[] operations,
            String[] donnees)
            throws JDONREFv3Exception {
        final List<PoizonBean> listRet = new ArrayList<PoizonBean>();
        Ligne1 ligne1 = null;
        ligne1 = Ligne1.getNewInstance(donnees[0], servicesTree, gestionMots);
        for (int service : services) {
            final PoizonBean poizon = new PoizonBean();
            final List<String> donneesRet = new ArrayList<String>();
            for (int operation : operations) {
                if ((operation & 524288) != 0) {
                    donneesRet.add(ligne1.getCle());
                }
                if ((operation & 2097152) != 0) {
                    donneesRet.add(ligne1.getArticle());
                }
                if ((operation & 1048576) != 0) {
                    donneesRet.add(ligne1.getLibelle());
                }
                if ((operation & 8192) != 0) {
                    if (donnees.length < 2) {
                        throw new JDONREFv3Exception(5, "La ligne 2 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[1]);
                    }
                }
                if ((operation & 16384) != 0) {
                    if (donnees.length < 3) {
                        throw new JDONREFv3Exception(5, "La ligne 3 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[2]);
                    }
                }
                if ((operation & 262144) != 0) {
                    if (donnees.length < 4) {
                        throw new JDONREFv3Exception(5, "La ligne 4 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[3]);
                    }
                }
                if ((operation & 32768) != 0) {
                    if (donnees.length < 5) {
                        throw new JDONREFv3Exception(5, "La ligne 5 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[4]);
                    }
                }
                if ((operation & 131072) != 0) {
                    if (donnees.length < 6) {
                        throw new JDONREFv3Exception(5, "La ligne 6 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[5]);
                    }
                }
                if ((operation & 65536) != 0) {
                    if (donnees.length < 7) {
                        throw new JDONREFv3Exception(5, "La ligne 7 n'est pas spécifiée.");
                    } else {
                        donneesRet.add(donnees[6]);
                    }
                }
            }
            poizon.setService(service);
            poizon.setDonnees(donneesRet.toArray(new String[donneesRet.size()]));
            listRet.add(poizon);
        }

        return listRet;
    }

    private String normalise(int operation, String donnee) {
        String donnee1 = donnee;
        if ((operation & 1) != 0) {
            donnee1 = Algos.normalise_1(donnee); 
        }
        if ((operation & 16) != 0) {
            donnee1 = Algos.phonexNonVide(donnee1);
        }
        // Désabréviation de la ligne 1
        if ((operation & 32) != 0) {
            donnee1 = gestionMots.corrige_abbreviations_ligneX(donnee1, 1);
        }
        if ((operation & 64) != 0) {
            donnee1 = Algos.sansarticles(donnee1);
        }

        return donnee1;
    }
}
