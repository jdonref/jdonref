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
 * Représente les adresses validées ou géocodées.
 * @author jmoquet
 */
public class Out
{
    public ArrayList<Adresse> adresses = new ArrayList<Adresse>();
    
    /**
     * Ecrit dans le fichier out.csv du répertoire les adresses validées et éventuellement géocodées.<br>
     * Des messages d'erreurs sont transmis sur la sortie standard pour chaque adresse qui n'a pas été inscrite
     * dans le fichier.
     * @return false en cas d'erreur.
     */
    public boolean write(String repertoire,Config config) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        boolean error = false;
        
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repertoire+"/out.csv"),"UTF-8"));
        
        if (config.entete)
        {
            writer.write("\"id\";\"ligne1\";\"ligne2\";\"ligne3\";\"ligne4\";\"ligne5\";\"ligne6\"");
            if(config.gererPays)
                writer.write(";\"ligne7\"");
            
            if (config.decoupe)
            {
                writer.write(";\"firstNumber\";\"firstRep\";\"otherNumbers\";\"typedevoie\";\"article\";\"libelle\";\"motdeterminant\";\"codedepartement\";\"codepostal\";\"commune\";\"arrondissement\";\"cedex\";\"codecedex\"");
                if(config.gererPays)
                    writer.write(";\"pays\"");
            }
            
            writer.write(";\"note\"");
            
            if (config.idvoie)
                writer.write(";\"idvoie\"");
            if (config.codeinsee)
                writer.write(";\"codeinsee\"");
            if (config.t0)
                writer.write(";\"t0\"");
            if (config.t1)
                writer.write(";\"t1\"");
            
            writer.write(";\"ligne1valide\";\"ligne2valide\";\"ligne3valide\";\"ligne4valide\";\"ligne5valide\";\"ligne6valide\"");
            if(config.gererPays)
                writer.write(";\"ligne7\"");
            
            if (config.geocodage!=Geocodage.Aucun)
                writer.write(";\"x\";\"y\";\"naturegeocodage\"");
            
            writer.write("\r\n");
        }
        
        for(int i=0;i<adresses.size();i++)
        {
            try
            {
                adresses.get(i).config = config;
                writer.write(adresses.get(i).toString()+"\r\n");
            }
            catch(Exception e)
            {
                System.out.println("Impossible d'écrire "+adresses.get(i).toString()+" dans out.csv : "+e.getMessage());
                error = true;
            }
        }
        
        writer.close();
        
        return error;
    }
    
    /**
     * Compte le nombre de paramètres séparé par des virgules.<br>
     * Les virgules entre quote ne sont pas comptés.<br>
     * Les quotes peuvent être ignorés avec des \.<br>
     * @param sql
     * @return
     */
    private int compteparams(String sql)
    {
        int count = 0;
        int state = 0;
        for(int i=0;i<sql.length();i++)
        {
            char c = sql.charAt(i);
            switch(state)
            {
                case 0: // après une virgule ou début de chaine
                    switch(c)
                    {
                        case '\'': count++;state = 1;break;
                        case ' ': break;
                        case ',': count++; break;
                        default: count++; state = 4; break;
                    }
                    break;
                case 1: // après une simple quote
                    switch(c)
                    {
                        case '\'': state=2; break;
                        case '\\': state=3; break;
                        default:break;
                    }
                    break;
                case 2: // après la fermeture d'une quote
                    switch(c)
                    {
                        case ',': state = 0;break;
                        default: break;
                    }
                    break;
                case 3: // après un \ dans une quote
                    state = 1; break;
                case 4: // après un debut de parametre sans quote
                    switch(c)
                    {
                        case ',':state = 0;break;
                        default : break;
                    }
                    break;
            }            
        }
        return count;
    }
    
    /**
     * Complète la portion de chaine sql spécifiée pour avoir le nombre de paramètre spécifié.<br>
     * ex: complete("'','',''" , 4) retourne "'','','',''"
     * @param sql
     * @param nb_params nombre de paramètres au total
     * @return
     */
    private String complete(String sql,int nb_params)
    {
        int i = compteparams(sql);
        if (i==0 && nb_params>0)
        {
            sql = "''";
            i++;
        }
        for(;i<nb_params;i++)
        {
            sql += ",''";
        }
        return sql;
    }
    
    public void writeSQL(String repertoire,String tablename,Config config) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repertoire+"/insert.sql"),"UTF-8"));
        int nbparams = 0;
        StringBuilder sbInsertHead = new StringBuilder();
        sbInsertHead.append("INSERT INTO \"");
        sbInsertHead.append(tablename);
        sbInsertHead.append("\" (id,ligne_1,ligne_2,ligne_3,ligne_4,ligne_5,ligne_6,");
        nbparams+=7;
        if(config.gererPays)
        {
            sbInsertHead.append("ligne_7,");
            nbparams++;
        }
        if (config.decoupe)
        {
            sbInsertHead.append("firstNumber,firstRep,otherNumbers,typedevoie,article,libelle,motdeterminant,codedepartement,codepostal,commune,arrondissement,cedex,codecedex,");
            nbparams+=13;
            if(config.gererPays)
            {
                sbInsertHead.append("pays,");
                nbparams++;
            }
        }
        sbInsertHead.append("note,ligne_1_valide,ligne_2_valide,ligne_3_valide,ligne_4_valide,ligne_5_valide,ligne_6_valide");
        nbparams+=7;
        if(config.gererPays)
        {
            sbInsertHead.append(", pays ");
            nbparams++;
        }
//        int nbparams = 14;
        if (config.geocodage != Geocodage.Aucun)
        {
            sbInsertHead.append(",x,y,naturegeocodage");
            nbparams += 3;
        }
        sbInsertHead.append(") VALUES (");
        
        String insertHead = sbInsertHead.toString();
        String insertTail = ");\r\n";
        
        for(int i=0;i<adresses.size();i++)
        {
            Adresse a = adresses.get(i);
            writer.write(insertHead);
            writer.write(complete(a.toStringSQL(),nbparams)); // ajoute des '' pour avoir le bon nb de colonnes
            writer.write(insertTail);
        }
        writer.close();
    }
    
    /**
     * Crée le script create de la table.
     * @param config
     * @param rep
     * @param tablename
     */
    public void create(Config config,String rep,String tablename) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rep+"/create.sql"),"UTF-8"));

        writer.write("CREATE TABLE \"");
        writer.write(tablename);
        writer.write("\" (\r\n");
        writer.write("id character varying,\r\n");
        writer.write("ligne_1 character varying,\r\n");
        writer.write("ligne_2 character varying,\r\n");
        writer.write("ligne_3 character varying,\r\n");
        writer.write("ligne_4 character varying,\r\n");
        writer.write("ligne_5 character varying,\r\n");
        writer.write("ligne_6 character varying,\r\n");
        if(config.gererPays)
            writer.write("ligne_7 character varying,\r\n");
        writer.write("note character varying,\r\n");
        writer.write("ligne_1_valide character varying,\r\n");
        writer.write("ligne_2_valide character varying,\r\n");
        writer.write("ligne_3_valide character varying,\r\n");
        writer.write("ligne_4_valide character varying,\r\n");
        writer.write("ligne_5_valide character varying,\r\n");
        writer.write("ligne_6_valide character varying\r\n");
        if(config.gererPays)
            writer.write("ligne_7_valide character varying\r\n");

        if (config.decoupe)
        {
            writer.write(",firstNumber character varying,\r\n");
            writer.write("firstRep character varying,\r\n");
            writer.write("otherNumbers character varying,\r\n");
            writer.write("typedevoie character varying,\r\n");
            writer.write("article character varying,\r\n");
            writer.write("libelle character varying,\r\n");
            writer.write("motdeterminant character varying,\r\n");
            writer.write("codedepartement character varying,\r\n");
            writer.write("codepostal character varying,\r\n");
            writer.write("commune character varying,\r\n");
            writer.write("arrondissement character varying,\r\n");
            writer.write("cedex character varying,\r\n");
            writer.write("codecedex character varying\r\n");
            if(config.gererPays)
                writer.write("pays character varying\r\n");
        }
        
        if (config.geocodage!=Geocodage.Aucun)
        {
            writer.write(",x character varying,\r\n");
            writer.write("y character varying,\r\n");
            writer.write("naturegeocodage character varying\r\n");
        }

        writer.write(")\r\n");

        writer.close();
    }
    
    /**
     * Permet de lire le fichier out.csv du répertoire des adresses validées et éventuellement géocodées.
     */
    public void read(String repertoire, Config config) throws UnsupportedEncodingException, FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(repertoire+"/out.csv"),"UTF-8"));
        
        if (config.entete)
        {
            reader.readLine();
        }
        String line = reader.readLine();
        while(line!=null)
        {
            String[] data = line.split(";");

            Adresse a = new Adresse();
            a.config = config;

            a.id = Util.noGuillemets(data[0]);
            a.ligne1 = Util.noGuillemets(data[1]);
            a.ligne2 = Util.noGuillemets(data[2]);
            a.ligne3 = Util.noGuillemets(data[3]);
            a.ligne4 = Util.noGuillemets(data[4]);
            a.ligne5 = Util.noGuillemets(data[5]);
            a.ligne6 = Util.noGuillemets(data[6]);
            int index = 7;
            if(config.gererPays)
            {
                a.ligne7 = Util.noGuillemets(data[7]);
                index++;
            }

            if (config.decoupe)
            {
                a.firstNumber = Util.noGuillemets(data[index++]);
                a.firstRep = Util.noGuillemets(data[index++]);
                a.otherNumbers = Util.noGuillemets(data[index++]).split(";");
                a.typedevoie = Util.noGuillemets(data[index++]);
                a.article = Util.noGuillemets(data[index++]);
                a.libelle = Util.noGuillemets(data[index++]);
                a.motdeterminant = Util.noGuillemets(data[index++]);
                a.codedepartement = Util.noGuillemets(data[index++]);
                a.codepostal = Util.noGuillemets(data[index++]);
                a.commune = Util.noGuillemets(data[index++]);
                a.arrondissement = Util.noGuillemets(data[index++]);
                a.cedex = Util.noGuillemets(data[index++]);
                a.codecedex = Util.noGuillemets(data[index++]);
                if(config.gererPays)
                    a.pays = Util.noGuillemets(data[index++]);
                
                if (a.libelle.length()>0 || a.commune.length()>0 || a.commune.length()>0)
                    a.decoupe = true;
            }
            
//            if (data.length>(7 + (config.decoupe?13:0)))
            if (data.length> index)
            {
                AdresseValide av=new AdresseValide(a);
                av.note=Integer.parseInt(data[index++]);
                av.ligne1valide=Util.noGuillemets(data[index++]);
                av.ligne2valide=Util.noGuillemets(data[index++]);
                av.ligne3valide=Util.noGuillemets(data[index++]);
                av.ligne4valide=Util.noGuillemets(data[index++]);
                av.ligne5valide=Util.noGuillemets(data[index++]);
                av.ligne6valide=Util.noGuillemets(data[index++]);
                if(config.gererPays)
                    av.ligne7valide = Util.noGuillemets(data[index++]);

//                if (data.length>(14+ (config.decoupe?13:0)))
                if(data.length > index)
                {
                    AdresseGeocodee ag=new AdresseGeocodee(av);
                    ag.x=Util.noGuillemets(data[index++]);
                    ag.y=Util.noGuillemets(data[index++]);
                    ag.type=TypeGeocodage.valueOf(Util.noGuillemets(data[index++]));

                    adresses.add(ag);
                }
                else
                    adresses.add(av);
            }
            else
                adresses.add(a);
            
            line = reader.readLine();
        }
        
        reader.close();
    }
}
