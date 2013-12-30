/*
 * Version 2.3.0 – Juin 2009
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * willy.aroche@interieur.gouv.fr
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

package ppol.jdonref.dao;

import java.util.Date;

/**
 *
 * @author arochewi
 */
public class CdpCodesPostauxBean implements Comparable
{
    private String codeInsee;
    private String codePostal;
    private String codeDepartement;
    private Date t0;
    private Date t1;

    public CdpCodesPostauxBean(String myCodeInsee, String myCodePostal, String myCodeDpt, Date myT0, Date myT1)
    {
        codeInsee = myCodeInsee;
        codePostal = myCodePostal;
        codeDepartement = myCodeDpt;
        t0 = myT0;
        t1 = myT1;
    }

    /**
     * @return the codeInsee
     */
    public String getCodeInsee()
    {
        return codeInsee;
    }

    /**
     * @param codeInsee the codeInsee to set
     */
    public void setCodeInsee(String codeInsee)
    {
        this.codeInsee = codeInsee;
    }

    /**
     * @return the codePostal
     */
    public String getCodePostal()
    {
        return codePostal;
    }

    /**
     * @param codePostal the codePostal to set
     */
    public void setCodePostal(String codePostal)
    {
        this.codePostal = codePostal;
    }

    /**
     * @return the codeDepartement
     */
    public String getCodeDepartement()
    {
        return codeDepartement;
    }

    /**
     * @param codeDepartement the codeDepartement to set
     */
    public void setCodeDepartement(String codeDepartement)
    {
        this.codeDepartement = codeDepartement;
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
    public int hashCode()
    {
        int hash = 7;
        hash = 31 * hash + (this.codeInsee != null ? this.codeInsee.hashCode() : 0);
        hash = 31 * hash + (this.codePostal != null ? this.codePostal.hashCode() : 0);
        hash = 31 * hash + (this.codeDepartement != null ? this.codeDepartement.hashCode() : 0);
        hash = 31 * hash + (this.t0 != null ? this.t0.hashCode() : 0);
        hash = 31 * hash + (this.t1 != null ? this.t1.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
        {
            return false;
        }
        if(getClass() != obj.getClass())
        {
            return false;
        }
        final CdpCodesPostauxBean other = (CdpCodesPostauxBean) obj;
        if((this.codeInsee == null) ? (other.codeInsee != null) : !this.codeInsee.equals(other.codeInsee))
        {
            return false;
        }
        if((this.codePostal == null) ? (other.codePostal != null) : !this.codePostal.equals(other.codePostal))
        {
            return false;
        }
        if((this.codeDepartement == null) ? (other.codeDepartement != null) : !this.codeDepartement.equals(other.codeDepartement))
        {
            return false;
        }
        if(this.t0 != other.t0 && (this.t0 == null || !this.t0.equals(other.t0)))
        {
            return false;
        }
        if(this.t1 != other.t1 && (this.t1 == null || !this.t1.equals(other.t1)))
        {
            return false;
        }
        return true;
    }

    public int compareTo(Object o)
    {
        CdpCodesPostauxBean other = (CdpCodesPostauxBean)o;
        // Code dpt puis code postal
        if(! codeDepartement.equals(other.codeDepartement))
        {
            return codeDepartement.compareTo(other.codeDepartement);
        }
        return codePostal.compareTo(other.codePostal);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("< CdpCodesPostauxBean - ");
        builder.append("codeInsee : ").append(codeInsee).append(" - ");
        builder.append("codePostal : ").append(codePostal).append(" - ");
        builder.append("codeDepartement : ").append(codeDepartement).append(" - ");
        builder.append("t0 : ").append(t0).append(" - ");
        builder.append("t1 : ").append(t1).append(" >");
        return builder.toString();
    }
}
