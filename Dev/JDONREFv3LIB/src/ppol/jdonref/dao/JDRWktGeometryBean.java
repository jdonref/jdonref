/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ppol.jdonref.dao;

/**
 *
 * @author arochewi
 */
public class JDRWktGeometryBean
{
    private String wkt;
    private String projectionText;
    private String projectionSRID;
    private String referentielText;

    /**
     * @return the wkt
     */
    public String getWkt()
    {
        return wkt;
    }

    /**
     * @param wkt the wkt to set
     */
    public void setWkt(String wkt)
    {
        this.wkt = wkt;
    }

    /**
     * @return the projectionText
     */
    public String getProjectionText()
    {
        return projectionText;
    }

    /**
     * @param projectionText the projectionText to set
     */
    public void setProjectionText(String projectionText)
    {
        this.projectionText = projectionText;
    }

    /**
     * @return the projectionSRID
     */
    public String getProjectionSRID()
    {
        return projectionSRID;
    }

    /**
     * @param projectionSRID the projectionSRID to set
     */
    public void setProjectionSRID(String projectionSRID)
    {
        this.projectionSRID = projectionSRID;
    }

    /**
     * @return the referentielText
     */
    public String getReferentielText()
    {
        return referentielText;
    }

    /**
     * @param referentielText the referentielText to set
     */
    public void setReferentielText(String referentielText)
    {
        this.referentielText = referentielText;
    }

    
}
