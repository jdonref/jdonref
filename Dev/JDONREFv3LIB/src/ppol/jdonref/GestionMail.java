/**
 * Version 2.1.5 – Juin 2009
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
package ppol.jdonref;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Permet d'envoyer un mail à l'administrateur de JDONREF.<br>
 * La méthode loadConfig permet de charger les paramètres nécessaires.
 * @author jmoquet
 */
public class GestionMail
{
    private String smtpServer = null;
    private String jdonrefAdminAdress = null;
    private int port = 25;
    private boolean debug = false;
    
    public String obtientServerSmtp()
    {
        return smtpServer;
    }
    
    public String obtientAdresseAdmin()
    {
        return jdonrefAdminAdress;
    }
    
    /**
     * Charge le fichier de configuration indiquant le moyen de contacter l'administrateur de JDONREF.<br>
     * Le fichier de configuration est ainsi constitué:<br>
     * adresse
     * <ul>
     * <li>smtpserver contient l'url du serveur smtp.</li>
     * <li>smtpport</li>
     * <li>jdonrefadminadress contient l'adresse mail de l'administrateur jdonref. Plusieurs adresses peuvent être
     * séparées par des ;.</li>
     * <li>debug optionel : s'il est présent, les informations de débugage de l'api javamail sont activées.</li>
     * </ul>
     * Si le fichier n'est pas trouvé, ou que ses paramètres sont erronés, la fonctionnalité contacte ne pourra 
     * etre utilisé.
     */
    public void loadConfig(String file) throws JDOMException, IOException, JDONREFException
    {
        SAXBuilder builder=new SAXBuilder();
        Document d=builder.build(file);
        
        Element root = d.getRootElement();
        
        if (root.getName().compareTo("adresse")!=0)
        {
            throw(new JDONREFException("Le fichier "+file+" contient une racine incorrecte : "+root.getName()));
        }
        
        Element e_smtpserver = root.getChild("smtpserver");
        Element e_jdonrefadminadress = root.getChild("jdonrefadminadress");
        Element e_debug = root.getChild("debug");
        Element e_port = root.getChild("smtpport");
        
        if (e_smtpserver==null)
        {
            throw(new JDONREFException("Le fichier "+file+" ne contient pas de balise smtpserver."));
        }
        if (e_jdonrefadminadress==null)
        {
            throw(new JDONREFException("Le fichier "+file+" ne contient pas de balise jdonrefadminadress."));
        }
        if (e_debug!=null)
        {
            debug = Boolean.parseBoolean(e_debug.getValue());
        }
        if (e_port!=null)
        {
            try
            {
                port = Integer.parseInt(e_port.getValue());
            }
            catch(NumberFormatException nfe)
            {
                throw(new JDONREFException("La balise port du fichier "+file+" a une valeur incorrecte."));
            }
        }
        
        smtpServer = e_smtpserver.getValue();
        jdonrefAdminAdress = e_jdonrefadminadress.getValue();
    }
    
    /**
     * Envoie un mail à l'administrateur enregistré de JDONREF.<br>
     * L'encodage utilisé est l'UTF-8.<br>
     * Les paramètres forment le message envoyé:<br>
     * Application :application<br>
     * Un problème a été rencontré avec l'adresse<br>
     * ligne1...<br>
     * @param from le mail de la personne qui envoi le mail
     * @param title le sujet du mail.
     */
    public void envoieMail(String from,String title,String application,String[] lignes) throws MessagingException
    {
        Properties props=System.getProperties();
        
        // Définit les propriétés de connection.
        props.put("mail.smtp.host",smtpServer);
        props.put("mail.smtp.port",Integer.toString(port));
        if (debug)
            props.put("mail.debug","true");
        
        // Obtient la session en cour.
        Session session=Session.getDefaultInstance(props,null);
        
        MimeMessage msg=new MimeMessage(session);
        
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(jdonrefAdminAdress,false));
        
        msg.setSubject(title,"UTF-8");
        
        StringBuilder sb = new StringBuilder();
        sb.append("Application :");
        sb.append(application);
        sb.append("\r\nUn problème a été rencontré un problème avec l'adresse  \r\n");
        for(int i=0;i<lignes.length;i++)
        {
            sb.append(lignes[i]);
            sb.append("\r\n");
        }
        
        msg.setText(sb.toString(),"UTF-8");
        msg.setHeader("Content-type","text/plain; charset=UTF-8");
        msg.setHeader("X-Mailer","JDONREFv2");
        msg.setSentDate(Calendar.getInstance().getTime());
        
        Transport.send(msg);
    }
    
    public static void main(String[] args)
    {
        try
        {
            System.out.println("Teste la classe GestionMail.");
            GestionMail gm=new GestionMail();
            System.out.println("Lecture des paramètres.");
            gm.loadConfig("mail.xml");
            System.out.println("Serveur:"+gm.obtientServerSmtp());
            System.out.println("Mail:"+gm.obtientAdresseAdmin());
            System.out.println("Envoi d'un mail.");
            gm.envoieMail("julien.moquet@interieur.gouv.fr","Test éé d'envoi de mail","application test",new String[]{"ligne2","ligne3","ligne4","ligne5","ligne6"});
        }
        catch(MessagingException ex)
        {
            Logger.getLogger(GestionMail.class.getName()).log(Level.SEVERE,null,ex);
            System.out.println(ex.getMessage());
        }
        catch(JDOMException ex)
        {
            Logger.getLogger(GestionMail.class.getName()).log(Level.SEVERE,null,ex);
            System.out.println(ex.getMessage());
        }
        catch(IOException ex)
        {
            Logger.getLogger(GestionMail.class.getName()).log(Level.SEVERE,null,ex);
            System.out.println(ex.getMessage());
        }
        catch(JDONREFException ex)
        {
            Logger.getLogger(GestionMail.class.getName()).log(Level.SEVERE,null,ex);
            System.out.println(ex.getMessage());
        }
    }
}