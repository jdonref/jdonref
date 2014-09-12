package ppol.jdonref.wservice;

import java.util.List;
import ppol.jdonref.JDONREFv3Exception;
import ppol.jdonref.referentiel.JDONREFv3Lib;

/**
 *
 * @author marcanhe, moquetju
 */
public interface IJDONREFv3Router {

    void init(JDONREFv3Lib jdonrefv3lib)
            throws JDONREFv3Exception;
    
    List<ServiceParameters> normalise(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> valide(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] ids,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> geocode(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> revalide(
            int application,
            int[] services,
            String[] donnees,
            String[] ids,
            String date,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> inverse(
            int application,
            int[] services,
            String[] donnees,
            double distance,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> decoupe(
            int application,
            int[] services,
            int[] operations,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> contacte(
            int application,
            int[] services,
            int operation,
            String[] donnees,
            String[] options)
            throws JDONREFv3Exception;

    List<ServiceParameters> getVersion(
            int application,
            int[] services)
            throws JDONREFv3Exception;
}
