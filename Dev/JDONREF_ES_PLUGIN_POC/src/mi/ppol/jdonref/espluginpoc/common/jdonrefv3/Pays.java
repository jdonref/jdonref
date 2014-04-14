package mi.ppol.jdonref.espluginpoc.common.jdonrefv3;

/**
 *
 * @author Julien
 */
public final class Pays
{
    private String ligne7;
    private String codepays;
    private long t0;
    private long t1;
    private String geometrie;
    
    public Pays()
    {
        
    }
    
    public Pays(String codepays,String ligne7,long t0,long t1,String geometrie)
    {
        this.codepays = codepays;
        this.ligne7 = ligne7;
        this.t0 = t0;
        this.t1 = t1;
        this.geometrie = geometrie;
    }

    public String codepays() {
        return codepays;
    }

    public String geometrie() {
        return geometrie;
    }

    public long t0() {
        return t0;
    }

    public long t1() {
        return t1;
    }
    
    public String getCodepays() {
        return codepays;
    }

    public String getGeometrie() {
        return geometrie;
    }

    public long getT0() {
        return t0;
    }

    public long getT1() {
        return t1;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        
        Pays pays = (Pays)o;
        
        if (this.codepays==null && pays.codepays!=null) return false;
        if (this.ligne7==null && pays.ligne7!=null) return false;
        if (this.geometrie==null && pays.geometrie!=null) return false;

        if (this.codepays.compareTo(pays.codepays)!=0) return false;
        if (this.ligne7.compareTo(pays.ligne7)!=0) return false;
        if (this.geometrie.compareTo(pays.geometrie)!=0) return false;
        if (this.t0!=pays.t0) return false;
        if (this.t1!=pays.t1) return false;
        
        return true;
    }
}