package ppol.jdonref.geocodeur;

/**
 * Représente les différents niveaux de géocodage.
 * @author jmoquet
 */
public enum TypeGeocodage
{
    Plaque,
    InterpolationPlaque,
    InterpolationTroncon,
    CentroideTroncon,
    CentroideVoie,
    Commune,
    Departement,
    Pays;
            
    /**
     * Obtient la valeur d'un type de géocodage.
     */
    public static int getValue(TypeGeocodage type)
    {
        if (type==TypeGeocodage.Plaque)
            return 0;
        else if (type==TypeGeocodage.InterpolationPlaque)
            return 1;
        else if (type==TypeGeocodage.InterpolationTroncon)
            return 2;
        else if (type==TypeGeocodage.CentroideTroncon)
            return 3;
        else if (type==TypeGeocodage.CentroideVoie)
            return 4;
        else if (type==TypeGeocodage.Commune)
            return 5;
        else if(type==TypeGeocodage.Departement)
            return 6;
        else if(type==TypeGeocodage.Pays)
            return 7;
        else
            return 7;
    }
    
    /**
     * Obtient le niveau de géocodage correspondant à la valeur.
     */
    public static TypeGeocodage getValue(int value)
    {
        switch(value)
        {
            default:
            case 0:
                return TypeGeocodage.Plaque;
            case 1:
                return TypeGeocodage.InterpolationPlaque;
            case 2:
                return TypeGeocodage.InterpolationTroncon;
            case 3:
                return TypeGeocodage.CentroideTroncon;
            case 4:
                return TypeGeocodage.CentroideVoie;
            case 5:
                return TypeGeocodage.Commune;
            case 6:
                return TypeGeocodage.Departement;
            case 7:
                return TypeGeocodage.Pays;
        }
    }
}