package ppol.jdonref.geocodeur;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Génération de scripts SQL
 * @author jmoquet
 */
public class Scripts
{
    public Scripts()
    {
    }
    

    
    /**
     * Crée un script insert.
     */
    public void insert(Config config,String rep,String tablename) throws UnsupportedEncodingException, FileNotFoundException, IOException, Exception
    {
        Out out = new Out();
        out.read(rep+"/out.csv",config);
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rep+"/insert.csv"),"UTF-8"));
        
        StringBuilder sbInsertHead = new StringBuilder();
        sbInsertHead.append("INSERT INTO \"");
        sbInsertHead.append(tablename);
        sbInsertHead.append("\" (id,ligne_1,ligne_2,ligne_3,ligne_4,ligne_5,ligne_6,");
        sbInsertHead.append("note,ligne_1_valide,ligne_2_valide,ligne_3_valide,ligne_4_valide,ligne_5_valide,ligne_6_valide");
        if (config.geocodage != Geocodage.Aucun)
        {
            sbInsertHead.append(",x,y,naturegeocodage");
        }
        sbInsertHead.append(") VALUES (");
        String insertHead = sbInsertHead.toString();
        
        
        
        writer.close();
    }
}