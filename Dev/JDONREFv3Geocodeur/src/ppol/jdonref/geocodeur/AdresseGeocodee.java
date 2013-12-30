package ppol.jdonref.geocodeur;

/**
 * Représente une adresse géocodée
 * @author jmoquet
 */
public class AdresseGeocodee extends AdresseValide
{
    public String x;
    public String y;
    public TypeGeocodage type;
    
     /**
     * Construit une adresse valide à partir d'une adresse.
     * @param a
     */
    public AdresseGeocodee(AdresseValide a)
    {
        super(a);
        this.note = a.note;
        this.idvoie = a.idvoie;
        this.t0 = a.t0;
        this.t1 = a.t1;
        this.ligne1valide = a.ligne1valide;
        this.ligne2valide = a.ligne2valide;
        this.ligne3valide = a.ligne3valide;
        this.ligne4valide = a.ligne4valide;
        this.ligne5valide = a.ligne5valide;
        this.ligne6valide = a.ligne6valide;
        ligne7valide = a.ligne7valide;
    }
    
    @Override
    public String toStringSQL()
    {
        String str = super.toStringSQL();
        
        str += ",'"+x+"','"+y+"','"+traite(type.toString())+"'";
        
        return str;
    }
    
    /**
     * Obtient la réprésentation sous forme de chaine.
     * @return
     */
    @Override
    public String toString()
    {
        String str = super.toString();
        
        str += ";\""+x+"\";\""+y+"\";\""+type.toString()+"\"";
        
        return str;
    }
}
