package ppol.jdonref.geocodeur;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Représente les adresses en entrée du géocodeur.
 * @author jmoquet
 */
public class In
{
    public ArrayList<Adresse> adresses = new ArrayList<Adresse>();
    
    /**
     * Lit le fichier d'adresses in.csv du répertoire spécifié.<br>
     * Sa structure doit être la suivante, pour chaque ligne, séparé par des points-virgule: 
     * <ul>
     *     <li>id</li>
     *     <li>ligne1</li>
     *     <li>ligne4</li>
     *     <li>ligne6</li>
     * </ul>
     * @param repertoire le répertoire dans lequel chercher in.csv
     */
    public void read(String repertoire,Config config) throws UnsupportedEncodingException, FileNotFoundException, IOException, Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(repertoire+"/in.csv"),"UTF8"));
        
        String line = br.readLine();
        
        if (config.entete && line!=null) // passe l'entête.
            line = br.readLine();
        
        try
        {
            while(line!=null)
            {
                String[] data=line.split(";");

                Adresse a=new Adresse();

                a.id=Util.noGuillemets(data[0]);
                a.ligne1=Util.noGuillemets(data[1]);
                a.ligne4=Util.noGuillemets(data[2]);
                a.ligne6=Util.noGuillemets(data[3]);
                if(data.length > 4)
                    a.ligne7 = Util.noGuillemets(data[4]);
                a.config = config;

                adresses.add(a);

                line=br.readLine();
            }
        }
        catch(ArrayIndexOutOfBoundsException aoobe)
        {
            br.close();
            throw(new Exception("Erreur ligne "+line+". Un caractère retour chariot ou fin de ligne a peut être été trouvé.",aoobe));
        }
        
        br.close();
    }
}