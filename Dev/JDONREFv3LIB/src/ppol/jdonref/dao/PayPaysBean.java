/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ppol.jdonref.dao;

import java.util.Date;

/**
 *
 * @author arochewi
 */
public class PayPaysBean extends AbstractBeanWithNote
{

    private String sovAc3;
    private String nomOrigine;
    private String nomInternationnal;
    private String nomFr;
    private String nomFrDesab;
    private String typePays;
    private String nomCapitale;
    private Date t0;
    private Date t1;

    public PayPaysBean(String sovAc3, String nomOrigine, String nomInternationnal, String nomFr,
            String nomFrDesab, String typePays, String nomCapitale, Date t0, Date t1)
    {
        super();
        this.nomOrigine = nomOrigine;
        this.nomInternationnal = nomInternationnal;
        this.nomFr = nomFr;
        this.nomFrDesab = nomFrDesab;
        this.typePays = typePays;
        this.nomCapitale = nomCapitale;
        this.t0 = t0;
        this.t1 = t1;
    }

    public PayPaysBean(String sovAc3, String nomOrigine, String nomInternationnal, String nomFr,
            String nomFrDesab, String typePays, String nomCapitale, Date t0, Date t1, int note)
    {
        super();
        this.sovAc3 = sovAc3;
        this.nomOrigine = nomOrigine;
        this.nomInternationnal = nomInternationnal;
        this.nomFr = nomFr;
        this.nomFrDesab = nomFrDesab;
        this.typePays = typePays;
        this.nomCapitale = nomCapitale;
        this.t0 = t0;
        this.t1 = t1;
        setNote(note);
    }

    /**
     * @return the sovAc3
     */
    public String getSovAc3()
    {
        return sovAc3;
    }

    /**
     * @param sovAc3 the sovAc3 to set
     */
    public void setSovAc3(String sovAc3)
    {
        this.sovAc3 = sovAc3;
    }

    /**
     * @return the nomOrigine
     */
    public String getNomOrigine()
    {
        return nomOrigine;
    }

    /**
     * @param nomOrigine the nomOrigine to set
     */
    public void setNomOrigine(String nomOrigine)
    {
        this.nomOrigine = nomOrigine;
    }

    /**
     * @return the nomInternationnal
     */
    public String getNomInternationnal()
    {
        return nomInternationnal;
    }

    /**
     * @param nomInternationnal the nomInternationnal to set
     */
    public void setNomInternationnal(String nomInternationnal)
    {
        this.nomInternationnal = nomInternationnal;
    }

    /**
     * @return the nomFr
     */
    public String getNomFr()
    {
        return nomFr;
    }

    /**
     * @param nomFr the nomFr to set
     */
    public void setNomFr(String nomFr)
    {
        this.nomFr = nomFr;
    }

    /**
     * @return the nomFrDesab
     */
    public String getNomFrDesab()
    {
        return nomFrDesab;
    }

    /**
     * @param nomFrDesab the nomFrDesab to set
     */
    public void setNomFrDesab(String nomFrDesab)
    {
        this.nomFrDesab = nomFrDesab;
    }

    /**
     * @return the typePays
     */
    public String getTypePays()
    {
        return typePays;
    }

    /**
     * @param typePays the typePays to set
     */
    public void setTypePays(String typePays)
    {
        this.typePays = typePays;
    }

    /**
     * @return the nomCapitale
     */
    public String getNomCapitale()
    {
        return nomCapitale;
    }

    /**
     * @param nomCapitale the nomCapitale to set
     */
    public void setNomCapitale(String nomCapitale)
    {
        this.nomCapitale = nomCapitale;
    }

    /**
     * @return the t0
     */
    public Date getT0()
    {
        return t0;
    }

    /**
     * @param t0 the t0 to set
     */
    public void setT0(Date t0)
    {
        this.t0 = t0;
    }

    /**
     * @return the t1
     */
    public Date getT1()
    {
        return t1;
    }

    /**
     * @param t1 the t1 to set
     */
    public void setT1(Date t1)
    {
        this.t1 = t1;
    }

    @Override
    public String toString()
    {
        final String TAB = "    ";
        String retValue = "";
        retValue = "PayPaysBean ( " + super.toString() + TAB + "sovAc3=" + this.sovAc3 + TAB + "nomOrigine=" + this.nomOrigine + TAB + "nomInternationnal=" + this.nomInternationnal + TAB + "nomFr=" + this.nomFr + TAB + "nomFrDesab=" + this.nomFrDesab + TAB + "typePays=" + this.typePays + TAB + "nomCapitale=" + this.nomCapitale + TAB + "t0=" + this.t0 + TAB + "t1=" + this.t1 + " )";
        return retValue;
    }


}
