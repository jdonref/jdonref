package ppol.jdonref.geocodeur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Permet de gérer un fichier d'erreurs
 * @author jmoquet
 */
public class Erreurs
{
    ArrayList<String> erreurs = new ArrayList<String>();
    
    /**
     * Constructeur par défaut.
     */
    public Erreurs()
    {
    }
    
    /**
     * Ecrit le fichier d'erreur dans le répertoire spécifié.<br>
     * Des erreurs sont affichées sur la sortie standard si le message ne peut pas être écrit dans le fichier.
     * @return false en cas d'erreur.
     */
    public boolean write(String repertoire) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repertoire+"/erreurs.txt"),"UTF-8"));
        boolean error = false;
        for(int i=0;i<erreurs.size();i++)
        {
            try
            {
                writer.write(erreurs.get(i));
                writer.write("\r\n");
            }
            catch(Exception e)
            {
                System.out.println("Impossible d'écrire "+erreurs.get(i)+" dans out.csv : "+e.getMessage());
                error = true;
            }
        }
        
        writer.close();
        
        return error;
    }
    
    /**
     * Lit le fichier d'erreur à partir du répertoire spécifié.
     */
    public void read(String repertoire) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(repertoire+"/erreurs.txt"),"UTF-8"));
        
        String line = reader.readLine();
        
        while(line!=null)
        {
            erreurs.add(line);
            line = reader.readLine();
        }
        
        reader.close();
    }
}