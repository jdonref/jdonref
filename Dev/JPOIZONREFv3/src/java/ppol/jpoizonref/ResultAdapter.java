/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jpoizonref;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import ppol.jdonref.dao.PoizonBean;
import ppol.jdonref.utils.DateUtils;
import ppol.jdonref.utils.MiscUtils;
import ppol.jdonref.wservice.PropositionDecoupage;
import ppol.jdonref.wservice.PropositionGeocodage;
import ppol.jdonref.wservice.PropositionGeocodageInverse;
import ppol.jdonref.wservice.PropositionGeocodageInverseBuilder;
import ppol.jdonref.wservice.PropositionGeocodageInverseComparator;
import ppol.jdonref.wservice.PropositionNormalisation;
import ppol.jdonref.wservice.PropositionNormalisationBuilder;
import ppol.jdonref.wservice.PropositionRevalidation;
import ppol.jdonref.wservice.PropositionRevalidationBuilder;
import ppol.jdonref.wservice.PropositionRevalidationComparator;
import ppol.jdonref.wservice.PropositionValidation;
import ppol.jdonref.wservice.PropositionValidationBuilder;
import ppol.jdonref.wservice.PropositionValidationComparator;
import ppol.jdonref.wservice.ResultatDecoupage;
import ppol.jdonref.wservice.ResultatGeocodage;
import ppol.jdonref.wservice.ResultatGeocodageInverse;
import ppol.jdonref.wservice.ResultatNormalisation;
import ppol.jdonref.wservice.ResultatRevalidation;
import ppol.jdonref.wservice.ResultatValidation;

/**
 *
 * @author marcanhe
 */
public class ResultAdapter {

    private final static DateUtils.DateFormatType sdformat = DateUtils.DateFormatType.SimpleSlashed;

    public static ResultatValidation adapteValide(List<PoizonBean> beans) {
        final ResultatValidation resultatRet = new ResultatValidation();
        resultatRet.setCodeRetour(1);
        final List<PropositionValidation> propositionList = new ArrayList<PropositionValidation>();
        for (PoizonBean bean : beans) {
            final PropositionValidationBuilder builder = new PropositionValidationBuilder();
            builder.setCode(100);
            builder.setNote(String.valueOf(bean.getNote()));
            builder.setService(bean.getService());
            builder.setT0(DateUtils.formatDateToString(bean.getT0(), sdformat));
            builder.setT1(DateUtils.formatDateToString(bean.getT1(), sdformat));
            builder.setDonnee(0, bean.getDonnee1());
            builder.setDonnee(1, bean.getDonnee2());
            builder.setDonnee(2, bean.getDonnee3());
            builder.setDonnee(3, bean.getDonnee4());
            builder.setDonnee(4, bean.getDonnee5());
            builder.setDonnee(5, bean.getDonnee6());
            builder.addDonnee(bean.getDonnee7());
            builder.setId(0, bean.getId1());
            builder.setId(1, bean.getId2());
            builder.setId(2, bean.getId3());
            builder.setId(3, bean.getId4());
            builder.setId(4, bean.getId5());
            builder.setId(5, bean.getId6());
            builder.addId(bean.getId7());
            builder.setDonneeOrigine(0, bean.getDonneeOrigine1());
            builder.setDonneeOrigine(1, bean.getDonneeOrigine2());
            builder.setDonneeOrigine(2, bean.getDonneeOrigine3());
            builder.setDonneeOrigine(3, bean.getDonneeOrigine4());
            builder.setDonneeOrigine(4, bean.getDonneeOrigine5());
            builder.setDonneeOrigine(5, bean.getDonneeOrigine6());
            builder.addDonneeOrigine(bean.getDonneeOrigine7());

            propositionList.add(builder.build());
        }

        Collections.sort(propositionList, PropositionValidationComparator.getInstance());
        resultatRet.setPropositions(propositionList.toArray(new PropositionValidation[propositionList.size()]));

        return resultatRet;
    }

    public static ResultatRevalidation adapteRevalide(List<PoizonBean> beans) {
        final ResultatRevalidation resultatRet = new ResultatRevalidation();
        resultatRet.setCodeRetour(1);
        final List<PropositionRevalidation> propositionList = new ArrayList<PropositionRevalidation>();
        for (PoizonBean bean : beans) {
            final PropositionRevalidationBuilder builder = new PropositionRevalidationBuilder();
            builder.setService(bean.getService());
            builder.setT0(DateUtils.formatDateToString(bean.getT0(), sdformat));
            builder.setT1(DateUtils.formatDateToString(bean.getT1(), sdformat));
            builder.setDonnee(0, bean.getDonnee1());
            builder.setDonnee(1, bean.getDonnee2());
            builder.setDonnee(2, bean.getDonnee3());
            builder.setDonnee(3, bean.getDonnee4());
            builder.setDonnee(4, bean.getDonnee5());
            builder.setDonnee(5, bean.getDonnee6());
            builder.addDonnee(bean.getDonnee7());
            builder.setId(0, bean.getId1());
            builder.setId(1, bean.getId2());
            builder.setId(2, bean.getId3());
            builder.setId(3, bean.getId4());
            builder.setId(4, bean.getId5());
            builder.setId(5, bean.getId6());
            builder.addId(bean.getId7());
            builder.setDonneeOrigine(0, bean.getDonneeOrigine1());
            builder.setDonneeOrigine(1, bean.getDonneeOrigine2());
            builder.setDonneeOrigine(2, bean.getDonneeOrigine3());
            builder.setDonneeOrigine(3, bean.getDonneeOrigine4());
            builder.setDonneeOrigine(4, bean.getDonneeOrigine5());
            builder.setDonneeOrigine(5, bean.getDonneeOrigine6());
            builder.addDonneeOrigine(bean.getDonneeOrigine7());

            propositionList.add(builder.build());
        }

        Collections.sort(propositionList, PropositionRevalidationComparator.getInstance());
        resultatRet.setPropositions(propositionList.toArray(new PropositionRevalidation[propositionList.size()]));

        return resultatRet;
    }

    public static ResultatGeocodage adapteGeocode(List<PoizonBean> beans) {
        final ResultatGeocodage resultatRet = new ResultatGeocodage();
        resultatRet.setCodeRetour(1);
        final List<PropositionGeocodage> propositions = new ArrayList<PropositionGeocodage>();
        for (PoizonBean bean : beans) {
            final PropositionGeocodage proposition = new PropositionGeocodage();
            proposition.setDate(DateUtils.formatDateToString(bean.getDate(), sdformat));
            proposition.setReferentiel(bean.getReferentiel());
            final Geometry geometrie = bean.getGeometrie();
            final Point point = geometrie.getCentroid();
            proposition.setX(String.valueOf(MiscUtils.truncate(point.getX(), 2)));
            proposition.setY(String.valueOf(MiscUtils.truncate(point.getY(), 2)));
            proposition.setProjection(bean.getProjection());
            final int service = bean.getService();
            proposition.setService(service);
            if (geometrie instanceof Point) {
                proposition.setType("1"); // POI
            } else {
                proposition.setType("6"); // ZON
            }

            propositions.add(proposition);
        }
        resultatRet.setPropositions(propositions.toArray(new PropositionGeocodage[propositions.size()]));

        return resultatRet;
    }

    public static ResultatGeocodageInverse adapteGeocodeInverse(List<PoizonBean> beans) {
        final ResultatGeocodageInverse resultatRet = new ResultatGeocodageInverse();
        resultatRet.setCodeRetour(1);
        final List<PropositionGeocodageInverse> propositionList = new ArrayList<PropositionGeocodageInverse>();
        for (PoizonBean bean : beans) {
            final PropositionGeocodageInverseBuilder builder = new PropositionGeocodageInverseBuilder();
            builder.setService(bean.getService());
            builder.setDistance(String.valueOf(bean.getDistance()));
            builder.setT0(DateUtils.formatDateToString(bean.getT0(), sdformat));
            builder.setT1(DateUtils.formatDateToString(bean.getT1(), sdformat));
            builder.setDonnee(0, bean.getDonnee1());
            builder.setDonnee(1, bean.getDonnee2());
            builder.setDonnee(2, bean.getDonnee3());
            builder.setDonnee(3, bean.getDonnee4());
            builder.setDonnee(4, bean.getDonnee5());
            builder.setDonnee(5, bean.getDonnee6());
            builder.addDonnee(bean.getDonnee7());
            builder.setId(0, bean.getId1());
            builder.setId(1, bean.getId2());
            builder.setId(2, bean.getId3());
            builder.setId(3, bean.getId4());
            builder.setId(4, bean.getId5());
            builder.setId(5, bean.getId6());
            builder.addId(bean.getId7());
            builder.setDonneeOrigine(0, bean.getDonneeOrigine1());
            builder.setDonneeOrigine(1, bean.getDonneeOrigine2());
            builder.setDonneeOrigine(2, bean.getDonneeOrigine3());
            builder.setDonneeOrigine(3, bean.getDonneeOrigine4());
            builder.setDonneeOrigine(4, bean.getDonneeOrigine5());
            builder.setDonneeOrigine(5, bean.getDonneeOrigine6());
            builder.addDonneeOrigine(bean.getDonneeOrigine7());
            builder.setReferentiel(bean.getReferentiel());
            final Geometry geometrie = bean.getGeometrie();
            final Point point = geometrie.getCentroid();
            builder.setX(String.valueOf(MiscUtils.truncate(point.getX(), 2)));
            builder.setY(String.valueOf(MiscUtils.truncate(point.getY(), 2)));

            propositionList.add(builder.build());
        }

        Collections.sort(propositionList, PropositionGeocodageInverseComparator.getInstance());
        resultatRet.setPropositions(propositionList.toArray(new PropositionGeocodageInverse[propositionList.size()]));

        return resultatRet;
    }

    public static ResultatNormalisation adapteNormalise(List<PoizonBean> beans) {
        final ResultatNormalisation resultatRet = new ResultatNormalisation();
        resultatRet.setCodeRetour(1);
        final List<PropositionNormalisation> propositions = new ArrayList<PropositionNormalisation>();
        for (PoizonBean poizon : beans) {
            final PropositionNormalisationBuilder builder = new PropositionNormalisationBuilder();
            builder.setDonnee(0, poizon.getDonnee1());
            builder.setDonnee(1, poizon.getDonnee2());
            builder.setDonnee(2, poizon.getDonnee3());
            builder.setDonnee(3, poizon.getDonnee4());
            builder.setDonnee(4, poizon.getDonnee5());
            builder.setDonnee(5, poizon.getDonnee6());
            if (poizon.getDonnee7() != null) {
                builder.addDonnee(poizon.getDonnee7());
            }
            builder.setService(poizon.getService());
            propositions.add(builder.build());
        }
        resultatRet.setPropositions(propositions.toArray(new PropositionNormalisation[propositions.size()]));

        return resultatRet;
    }

    public static ResultatDecoupage adapteDecoupe(List<PoizonBean> beans) {
        final ResultatDecoupage resultatRet = new ResultatDecoupage();
        resultatRet.setCodeRetour(1);
        final List<PropositionDecoupage> propositions = new ArrayList<PropositionDecoupage>();
        for (PoizonBean poizon : beans) {
            final PropositionDecoupage proposition = new PropositionDecoupage();
            proposition.setDonnees(poizon.getDonnees());
            proposition.setService(poizon.getService());
            propositions.add(proposition);
        }
        resultatRet.setPropositions(propositions.toArray(new PropositionDecoupage[propositions.size()]));

        return resultatRet;
    }
}
