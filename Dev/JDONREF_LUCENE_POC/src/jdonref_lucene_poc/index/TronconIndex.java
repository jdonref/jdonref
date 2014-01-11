package jdonref_lucene_poc.index;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdonref_lucene_poc.dao.TronconDAO;
import jdonref_lucene_poc.entity.Adresse;
import jdonref_lucene_poc.entity.Voie;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;

/**
 * 
 * @author Julien
 */
public class TronconIndex
{
     boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    private static void addAdresse(IndexWriter w,Adresse a)
            throws IOException
    {
        Document doc = a.toDocument();
        
        w.addDocument(doc);
    }
    
    public void indexJDONREFTronconsDepartement(IndexWriter writer,Voie[] voies,String dpt,String side) throws IOException
    {
        int c =0;
        
        for(int i=0;i<voies.length;i++)
        {
            Voie v = voies[i];
            
            int voi_min_numero = v.min_numero;
            int voi_max_numero = v.max_numero;
            
            if (voi_min_numero!=0 || voi_max_numero!=0)
            {
                if (voi_min_numero > voi_max_numero)
                {
                    int temp = voi_max_numero;
                    voi_max_numero = voi_min_numero;
                    voi_min_numero = temp;
                }
                
                if ( voi_min_numero%2 != voi_max_numero%2)
                {
                    if (side.equals("gauches"))
                    {
                        for(int j=voi_min_numero;j<=voi_max_numero;j++,c++)
                        {
                            Adresse adresse = new Adresse(v,Integer.toString(j),"");
                    
                            addAdresse(writer, adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
                else
                {
                    if (side.equals("gauches") && voi_min_numero%2==0 ||
                        side.equals("droits")  && voi_min_numero%2==1)
                    {
                        for(int j=voi_min_numero;j<=voi_max_numero;j+=2,c++)
                        {
                            Adresse adresse = new Adresse(v,Integer.toString(j),"");
                        
                            addAdresse(writer, adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
            }
        }
    }
    
    public void indexJDONREFTronconsDepartement(IndexWriter writer,ResultSet rs, String side, String dpt) throws IOException, SQLException
    {
        int c =0;
        
        while(rs.next())
        {
            String voi_id = rs.getString(1);
            String voi_nom = rs.getString(2);
            String com_code_insee = rs.getString(3);
            String com_code_postal = rs.getString(4);
            String voi_type_voie = rs.getString(5);
            String voi_lbl = rs.getString(6);
            int voi_min_numero = rs.getInt(7);
            int voi_max_numero = rs.getInt(8);
            String com_nom = rs.getString(9);
            
            if (voi_min_numero!=0 || voi_max_numero!=0)
            {
                if (voi_min_numero > voi_max_numero)
                {
                    int temp = voi_max_numero;
                    voi_max_numero = voi_min_numero;
                    voi_min_numero = temp;
                }
                
                if (voi_min_numero%2 != voi_max_numero%2)
                {
                    if (side.equals("gauches"))
                    {
                        for(int i=voi_min_numero;i<=voi_max_numero;i++,c++)
                        {
                            Adresse adresse = new Adresse(rs,Integer.toString(i),"");
                    
                            addAdresse(writer, adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
                else
                {
                    if (side.equals("gauches") && voi_min_numero%2==0 ||
                        side.equals("droits")  && voi_min_numero%2==1)
                    {
                        for(int i=voi_min_numero;i<=voi_max_numero;i+=2,c++)
                        {
                            Adresse adresse = new Adresse(rs,Integer.toString(i),"");
                            
                            addAdresse(writer, adresse);
                            if (isVerbose() && c%1000==0)
                                System.out.println(c+" troncons "+side+" traitées");
                        }
                    }
                }
            }
        }
    }
    
    public void indexJDONREFTronconsGaucheDepartement(IndexWriter writer,Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons gauche");
        
        indexJDONREFTronconsDepartement(writer, voies, dpt,"gauches");
    }
    
    public void indexJDONREFTronconsGaucheDepartement(IndexWriter writer,Connection connection, String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons gauche");
        
        TronconDAO dao = new TronconDAO();
        
        ResultSet rs = dao.getAllTronconsGaucheOfDepartment(connection,dpt);
        indexJDONREFTronconsDepartement(writer, rs, "gauches", dpt);
    }
    
    public void indexJDONREFTronconsDroitDepartement(IndexWriter writer,Voie[] voies, String dpt) throws IOException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons droits");
        
        indexJDONREFTronconsDepartement(writer, voies, dpt, "droits");
    }
    
    public void indexJDONREFTronconsDroitDepartement(IndexWriter writer,Connection connection, String dpt) throws IOException, SQLException
    {
        if (isVerbose())
            System.out.println("dpt "+dpt+" : troncons droits");
        
        TronconDAO dao = new TronconDAO();
        
        ResultSet rs = dao.getAllTronconsDroitOfDepartment(connection,dpt);
        indexJDONREFTronconsDepartement(writer, rs, "droits", dpt);
    }
}