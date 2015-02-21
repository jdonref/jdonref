/**
 * Version 3 – 2014
 * CeCILL Copyright © Préfecture de Police
 * Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ
 * julien.moquet@interieur.gouv.fr
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
package ppol.jdonref.referentiel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.ws.rest.api.client.Client;
import com.sun.ws.rest.api.client.ClientHandlerException;
import com.sun.ws.rest.api.client.ClientResponse;
import com.sun.ws.rest.api.client.WebResource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ppol.jdonref.GestionConnectionES;
import ppol.jdonref.JDONREFParams;
import ppol.jdonref.mots.GestionMots;

/**
 *
 * @author moquetju
 */
public class GestionReferentielES extends GestionReferentiel
{
    public static final int SERVICE_POIZON = 8;
    
    protected GestionConnectionES connexionES = null;

    public GestionConnectionES getConnexionES() {
        return connexionES;
    }

    public void setConnexionES(GestionConnectionES connexionES) {
        this.connexionES = connexionES;
    }
    
    /** Creates a new instance of GestionReferentiel */
    public GestionReferentielES(GestionMots gestionMots, GestionMiseAJour gestionMiseAJour, JDONREFParams jdonrefParams) {
        super(gestionMots,gestionMiseAJour,jdonrefParams);
    }
    
    public String aggregeLignes(String[] lignes)
    {
        String res = "";
        for(int i=0;i<lignes.length;i++)
        {
            if (res.length()==0)
                res = lignes[i];
            else if (lignes[i].length()>0)
            {
                res += " "+lignes[i];
            }
        }
        return res;
    }
    
    public String getQueryPOST(int[] services, String[] lignes)
    {
        String data = aggregeLignes(lignes);
        
        JsonObject jdonrefv4 = new JsonObject();
        jdonrefv4.addProperty("value",data);
        jdonrefv4.addProperty("maxSizePerType",5000);
        
        JsonObject query = new JsonObject();
        query.add("jdonrefv4",jdonrefv4);
        
        JsonObject post = new JsonObject();
        post.add("query",query);
        
        return post.toString();
    }
    
    public String getFieldString(JsonObject object,String field)
    {
        JsonElement element = object.get(field);
        if (element==null) return null;
        
        return element.getAsString();
    }
    
    public String getFieldDate(JsonObject object,String field) throws ParseException
    {
        JsonElement element = object.get(field);
        if (element==null) return null;
        
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = formater.parse(element.getAsString());
        SimpleDateFormat formaterJDRF = new SimpleDateFormat("dd/MM/yyyy");
        
        return formaterJDRF.format(date);
    }
    
    public void ajouteRetourPOIZON(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();

        //String adr_id = getFieldString(source,"adr_id");
        String poizon_id  = getFieldString(source,"poizon_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        String ligne1     = getFieldString(source,"ligne1");
        String ligne2     = getFieldString(source,"ligne2");
        String ligne3     = getFieldString(source,"ligne3");
        String ligne4     = getFieldString(source,"ligne4");
        String ligne5     = getFieldString(source,"ligne5");
        String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "8";
        res[index++] = "1";
        res[index++] = ligne1;
        res[index++] = ligne2;
        res[index++] = ligne3;
        res[index++] = ligne5;
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = poizon_id==null?"":poizon_id;
        res[index++] = ligne4;
        res[index++] = ligne4;
        res[index++] = code_insee==null?"":code_insee;
        res[index++] = ligne6;
        res[index++] = ligne6;
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "8";
        
        listRet.add(res);
    }
    
    public void ajouteRetourAdresse(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();
        
        //String adr_id = getFieldString(source,"adr_id");
        String voi_id     = getFieldString(source,"voi_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        String ligne4     = getFieldString(source,"ligne4");
        String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "2";
        res[index++] = "1";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = voi_id;
        res[index++] = ligne4;
        res[index++] = ligne4;
        res[index++] = code_insee;
        res[index++] = ligne6;
        res[index++] = ligne6;
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "1";
        
        listRet.add(res);
    }
    
    public void ajouteRetourVoie(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();
        
        //String adr_id = getFieldString(source,"adr_id");
        String voi_id     = getFieldString(source,"voi_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        String ligne4     = getFieldString(source,"ligne4");
        String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "2";
        res[index++] = "1";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = voi_id;
        res[index++] = ligne4;
        res[index++] = ligne4;
        res[index++] = code_insee;
        res[index++] = ligne6;
        res[index++] = ligne6;
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "4";
        
        listRet.add(res);
    }
    
    public void ajouteRetourCommune(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();
        
        //String adr_id = getFieldString(source,"adr_id");
        //String voi_id     = getFieldString(source,"voi_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        String ligne4     = getFieldString(source,"ligne4");
        String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "2";
        res[index++] = "1";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = "";
        res[index++] = ligne4;
        res[index++] = ligne4;
        res[index++] = code_insee;
        res[index++] = ligne6;
        res[index++] = ligne6;
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "5";
        
        listRet.add(res);
    }
    
    public void ajouteRetourDepartement(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();
        
        //String adr_id = getFieldString(source,"adr_id");
        //String voi_id     = getFieldString(source,"voi_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        //String ligne4     = getFieldString(source,"ligne4");
        String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "2";
        res[index++] = "1";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = code_insee;
        res[index++] = ligne6;
        res[index++] = ligne6;
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "6";
        
        listRet.add(res);
    }
    
    public void ajouteRetourPays(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        float score = object.get("_score").getAsFloat();   
        JsonObject source = object.get("_source").getAsJsonObject();
        
        //String adr_id = getFieldString(source,"adr_id");
        //String voi_id     = getFieldString(source,"voi_id");
        String code_insee = getFieldString(source,"code_insee");
        String t0         = getFieldDate(source,"t0");
        String t1         = getFieldDate(source,"t1");
        //String ligne4     = getFieldString(source,"ligne4");
        //String ligne6     = getFieldString(source,"ligne6");
        String ligne7     = getFieldString(source,"ligne7");
        
        String[] res = new String[17+(gererPays?1:0)];
        int index = 0;
        res[index++] = "2";
        res[index++] = "1";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        if (gererPays)
            res[index++] = ligne7;
        
        res[index++] = "";
        res[index++] = "";
        res[index++] = "";
        res[index++] = code_insee;
        res[index++] = "";
        res[index++] = "";
        res[index++] = t0;
        res[index++] = t1;
        res[index++] = Float.toString(score);
        res[index++] = "";
        res[index++] = "7";
        
        listRet.add(res);
    }
    
    public void ajouteRetour(List<String[]> listRet,JsonObject object, boolean gererPays) throws ParseException
    {
        String type = object.get("_type").getAsString();
        
        if (type.equals("poizon"))
        {
             ajouteRetourPOIZON(listRet,object,gererPays);
        }
        else
        if (type.equals("adresse"))
        {
             ajouteRetourAdresse(listRet,object,gererPays);
        }
        else if (type.equals("voie"))
        {
            ajouteRetourVoie(listRet,object,gererPays);
        }
        else if (type.equals("commune"))
        {
            ajouteRetourCommune(listRet,object,gererPays);
        }
        else if (type.equals("departement"))
        {
            ajouteRetourDepartement(listRet,object,gererPays);
        }
        else if (type.equals("pays"))
        {
            ajouteRetourPays(listRet,object,gererPays);
        }
    }
    
    public List<String[]> parseResponse(String output, boolean gererPays) throws ParseException
    {
        final List<String[]> listRet = new ArrayList<String[]>();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(output);
        JsonObject object = element.getAsJsonObject();
        JsonObject hits = object.getAsJsonObject("hits");
        int size = hits.get("total").getAsInt();
        if (size>0)
        {
            JsonArray allHits = hits.getAsJsonArray("hits");
            for (int i = 0; i < allHits.size(); i++)
            {
                JsonElement element_i = allHits.get(i);
                JsonObject object_i = element_i.getAsJsonObject();

                ajouteRetour(listRet, object_i, gererPays);
            }
        }

        return listRet;
    }
    
    public String addUrl(String url,String add)
    {
        if (url.length()==0)
        {
            return "/"+add;
        }
        else
        {
            return url+","+add;
        }
    }
    
    public String getUrl(int[] services)
    {
        String url = "";
        
        for (Integer serviceCle : services)
        {
            Integer id = JDONREFv3Lib.getInstance().getServices().getServiceFromCle(serviceCle).getId();
        
            if (id == SERVICE_POINT_ADRESSE)
            {
                url = addUrl(url,"adresse");
            }
            else if (id == SERVICE_ADRESSE)
            {
                url = addUrl(url,"adresse,voie,commune,departement,pays");
            }
            else if (id==SERVICE_PAYS)
            {
                url = addUrl(url,"pays");
            }
            else if (id==SERVICE_DEPARTEMENT)
            {
                url = addUrl(url,"departement");
            }
            else if (id==SERVICE_COMMUNE)
            {
                url = addUrl(url,"commune");
            }
            else if (id==SERVICE_VOIE || id==SERVICE_TRONCON)
            {
                url = addUrl(url,"voie");
            }
            else if (id==SERVICE_POIZON)
            {
                url = addUrl(url,"poizon");
            }
            // TODO : prendre en compte POI et ZON.
        }
        url +=  "/_search";
        
        return url;
    }
    
    @Override
    public List<String[]> valide(int application, int[] services, String[] lignes, String strdate, boolean force, boolean gestionAdresse,boolean gererPays, Connection connection) throws
            SQLException
    {
        String resource = "vide";
        String output = "";
        
        try
        {
            Client client = Client.create();
            resource = connexionES.getURL() + getUrl(services);
            String queryPOST = getQueryPOST(services, lignes);
            WebResource webResource = client.resource(resource);
            ClientResponse response = webResource.accept("application/json").post(ClientResponse.class, queryPOST);
            output = response.getEntity(String.class);
            return parseResponse(output,gererPays);
        }
        catch(ParseException pe)
        {
            throw(new SQLException("Une date est mal formée.",pe));
        }
        catch(ClientHandlerException che)
        {
            throw(new SQLException("L'url "+resource+" est malformée.",che));
        }
        catch(Exception e)
        {
            throw(new SQLException("Erreur durant le traitement de "+output));
        }
    }
}
