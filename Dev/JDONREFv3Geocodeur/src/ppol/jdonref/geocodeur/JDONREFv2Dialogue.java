package ppol.jdonref.geocodeur;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Boîte de dialogue permettant de valider une adresse.<br>
 * MVC respecté sauf pour l'index.
 * @author jmoquet
 */
public class JDONREFv2Dialogue extends JFrame
{
    int index;
    int max;
    
    JPanel jpadresse = new JPanel();
    JPanel jpchoix = new JPanel();
    
    JLabel jlblank1 = new JLabel();
    JLabel jlblank2 = new JLabel();
    JLabel jlblank3 = new JLabel();
    JLabel jlblank4 = new JLabel();
    JLabel jlblank5 = new JLabel();
    JLabel jlblank6 = new JLabel();
    JLabel jlblank7 = new JLabel();
    JLabel jlblank8 = new JLabel();
    
    JLabel jlid = new JLabel();
    JLabel jlligne1 = new JLabel();
    JLabel jlligne2 = new JLabel();
    JLabel jlligne3 = new JLabel();
    JLabel jlligne4 = new JLabel();
    JLabel jlligne5 = new JLabel();
    JLabel jlligne6 = new JLabel();
    JLabel jlligne7 = new JLabel();
    
    JButton jbvalide = new JButton();
    JButton jbechec = new JButton();
    JButton jbchoisir = new JButton();
    JButton jbreset = new JButton();
    JButton jbrestructure = new JButton();
    
    JLabel jlcompte = new JLabel();
    JButton jbsuivant = new JButton();
    
    JTextField jtfid     = new JTextField();
    JTextField jtfligne1 = new JTextField();
    JTextField jtfligne2 = new JTextField();
    JTextField jtfligne3 = new JTextField();
    JTextField jtfligne4 = new JTextField();
    JTextField jtfligne5 = new JTextField();
    JTextField jtfligne6 = new JTextField();
    JTextField jtfligne7 = new JTextField();
    
    JScrollPane jsp = null;
    JTable jttable = null; // construit dynamiquement
    JLabel jlerreur = new JLabel();
    
    String version = "?";
    
    /**
     * Définit la version qui apparaît dans le titre.
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    /**
     * Les états du formulaire:
     * <ul>
     * <li>0 propositions d'adresse.</li>
     * <li>1 propositions de communes.</li>
     * <li>2 propositions de départements.</li>
     * <li>3 propositions de communes après échec de validation d'adresse.</li>
     * <li>4 le choix effectué</li>
     * <li>5 erreur</li>
     * </ul>
     */
    int state = 0;
    
    /**
     * Met à jour l'affichage de l'index des adresses gérées.
     */
    private void updateIndex()
    {
        jlcompte.setText(index+"/"+max);
    }
    
    /**
     * Définit l'index parmi les adresses gérées
     */
    public void setIndex(int index)
    {
        this.index = index;
        updateIndex();
    }
    
    public int getIndex()
    {
        return index;
    }
    
    /**
     * Définit le maximum d'adresses gérées
     * @param max
     */
    public void setMax(int max)
    {
        this.max = max;
        updateIndex();
    }
    
    /**
     * Constructeur par défaut.
     */
    public JDONREFv2Dialogue()
    {
        initComponents();
        dimensionne();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        setTitle("Validation manuelles des adresses");
    }
    
    private void dimensionne()
    {
        int ecart = 5;
        
        Dimension d = getSize();
        Dimension d2 = getContentPane().getSize();
        
        int width  = 250;
        int height = 150;
        
        if (this.getExtendedState()==Frame.NORMAL)
            setSize(5*ecart+4*width+d.width-d2.width,5*ecart+4*height+d.height-d2.height);
    }
    
    private void initComponents()
    {
        setLayout(new GridLayout(2,1));
        jpadresse.setLayout(new GridLayout(8,4));
        jpchoix.setLayout(new GridLayout(1,1));
        
        add(jpadresse);
        add(jpchoix);
        
        updateIndex();
        jbsuivant.setText("Suivant");
        
        jlid.setText("id");
        jlligne1.setText("ligne1");
        jlligne2.setText("ligne2");
        jlligne3.setText("ligne3");
        jlligne4.setText("ligne4");
        jlligne5.setText("ligne5");
        jlligne6.setText("ligne6");
        jlligne7.setText("ligne7");
        
        jtfid.setEnabled(false);
        jtfligne1.setEnabled(false);
        
        setButtonState(EtatBoutonValide.Valider); // le bouton valider
        jbechec.setText("Echec");
        jbchoisir.setText("Choisir");
        jbreset.setText("Reset");
        jbrestructure.setText("Restructure");
        
        jbvalide.setActionCommand("valide");
        jbsuivant.setActionCommand("suivant");
        jbechec.setActionCommand("echec");
        jbchoisir.setActionCommand("choisir");
        jbreset.setActionCommand("reset");
        jbrestructure.setActionCommand("restructure");

        jpadresse.add(jlid);
        jpadresse.add(jtfid);
        jpadresse.add(jlcompte);
        jpadresse.add(jbsuivant);
        
        jpadresse.add(jlligne1);
        jpadresse.add(jtfligne1);
        jpadresse.add(jlblank1);
        jpadresse.add(jlblank2);
        
        jpadresse.add(jlligne2);
        jpadresse.add(jtfligne2);
        jpadresse.add(jlblank3);
        jpadresse.add(jlblank4);
        
        jpadresse.add(jlligne3);
        jpadresse.add(jtfligne3);
        jpadresse.add(jlblank5);
        jpadresse.add(jlblank6);
        
        jpadresse.add(jlligne4);
        jpadresse.add(jtfligne4);
        jpadresse.add(jbrestructure);
        jpadresse.add(jlblank8);
        
        jpadresse.add(jlligne5);
        jpadresse.add(jtfligne5);
        jpadresse.add(jbvalide);
        jpadresse.add(jbechec);
        
        jpadresse.add(jlligne6);
        jpadresse.add(jtfligne6);        
        jpadresse.add(jbchoisir);
        jpadresse.add(jbreset);

        jpadresse.add(jlligne7);
        jpadresse.add(jtfligne7);
    }
    
    IJDONREFDialogueController controller = null;
    
    /**
     * Connecte le controlleur spécifié.
     * @param controller
     */
    public void connect(IJDONREFDialogueController controller)
    {
        if (controller!=null) deconnect();
        
        jbchoisir.addActionListener(controller);
        jbechec.addActionListener(controller);
        jbreset.addActionListener(controller);
        jbsuivant.addActionListener(controller);
        jbvalide.addActionListener(controller);
        jbrestructure.addActionListener(controller);
        
        jtfligne2.addCaretListener(controller);
        jtfligne3.addCaretListener(controller);
        jtfligne4.addCaretListener(controller);
        jtfligne5.addCaretListener(controller);

        jtfligne7.addCaretListener(controller);

        addWindowListener(controller);
    }
    
    /**
     * Déconnecte le controlleur actuel.
     */
    public void deconnect()
    {
        if (controller==null) return;
        jbchoisir.removeActionListener(controller);
        jbechec.removeActionListener(controller);
        jbreset.removeActionListener(controller);
        jbsuivant.removeActionListener(controller);
        jbvalide.removeActionListener(controller);
        
        jtfligne4.removeCaretListener(controller);
    }
    
    /**
     * Obtient l'adresse saisie par l'utilisateur.
     * @return
     */
    public String[] getSaisie()
    {
        String[] res = new String[]
        {
            jtfligne2.getText(),
            jtfligne3.getText(),
            jtfligne4.getText(),
            jtfligne5.getText(),
            jtfligne6.getText(),
            jtfligne7.getText()
        };
        return res;
    }
    
    /**
     * Définit l'adresse saisie par l'utilisateur.
     */
    public void setSaisie(String[] lignes)
    {
        jtfligne2.setText(lignes[0]);
        jtfligne3.setText(lignes[1]);
        jtfligne4.setText(lignes[2]);
        jtfligne5.setText(lignes[3]);
        jtfligne6.setText(lignes[4]);
        jtfligne7.setText(lignes[5]);
    }
    
    /**
     * Définit l'adresse à éditer.
     */
    public void setAdresse(Adresse a)
    {
        jpchoix.removeAll();
        
        jtfid.setText(a.id);
        jtfligne1.setText(a.ligne1);
        jtfligne2.setText(a.ligne2);
        jtfligne3.setText(a.ligne3);
        jtfligne4.setText(a.ligne4);
        jtfligne5.setText(a.ligne5);
        jtfligne6.setText(a.ligne6);
        jtfligne7.setText(a.ligne7);
        
        // S'il s'agit d'une erreur
        if (a.getClass().getName().compareTo(Erreur.class.getName())==0)
        {
            jttable = null;
            Erreur e = (Erreur)a;
            jlerreur.setText(e.message);
            jpchoix.add(jlerreur);
        }
        // S'il s'agit d'un choix d'adresses
        else if (a.getClass().getName().compareTo(Propositions.class.getName())==0)
        {
            Propositions p = (Propositions)a;
            
            if (p.propositions.size()==0)
            {
                jttable = null;
                jlerreur.setText("Aucune proposition trouvée.");
                jpchoix.add(jlerreur);
                
                jbchoisir.setEnabled(false);
            }
            else
            {
                Object[] columnNames=new Object[]
                {
                    //"ligne1","ligne2","ligne3",
                    "ligne4",
                    //"ligne5",
                    "ligne6","note","ligne7"
                };

                Object[][] rowDate=new Object[p.propositions.size()][];

                for(int i=0; i<p.propositions.size(); i++)
                {
                    rowDate[i]=new Object[7];
                    //rowDate[i][0]=p.propositions.get(i).ligne1valide;
                    //rowDate[i][1]=p.propositions.get(i).ligne2valide;
                    //rowDate[i][2]=p.propositions.get(i).ligne3valide;
                    rowDate[i][0]=p.propositions.get(i).ligne4valide;
                    //rowDate[i][4]=p.propositions.get(i).ligne5valide;
                    rowDate[i][1]=p.propositions.get(i).ligne6valide;
                    rowDate[i][2]=p.propositions.get(i).note;
                    rowDate[i][3]=p.propositions.get(i).ligne7valide;
                }
                
                jbchoisir.setEnabled(true);
                
                jttable=new JTable(rowDate,columnNames);
                //jttable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                jttable.getTableHeader().setResizingAllowed(true);
                //jttable.getColumnModel().getColumn(3).setWidth(200);
                jttable.validate();
                
                jsp = new JScrollPane(jttable);
                jpchoix.add(jsp);
                
                jpchoix.validate();
            }
        }
    }
    
    /**
     * Ferme la fenêtre.
     */
    public void close()
    {
        this.dispose();
    }
    
    /**
     * Obtient l'adresse choisie.
     * @return -1 si aucune adresse n'est choisie.
     */
    public int getChoix()
    {
        if (jttable!=null)
        {
            return jttable.getSelectedRow();
        }
        return -1;
    }
    
    /**
     * Définit l'état du bouton choisir.
     * @param etat
     */
    public void setChoisir(boolean etat)
    {
        jbchoisir.setEnabled(etat);
    }
    
    /**
     * Obtient l'état actuel du formulaire.
     */
    public int getEtat()
    {
        return state;
    }
    
    /**
     * Définit l'état du formulaire.<br>
     * Cette méthode modifie éventuellement le formulaire.
     * @param value  Les différents états du formulaire
     * <ul>
     * <li>0 propositions d'adresse.</li>
     * <li>1 propositions de communes.</li>
     * <li>2 propositions de départements.</li>
     * <li>3 propositions de communes après échec de validation d'adresse.</li>
     * <li>4 erreur de validation.</li>
     * <li>5 propositions de pays.</li>
     * <li>6 propositions de pays après echec de validation commune.</li>
     * </ul>
     * @return l'état précédent
     */
    public int setEtat(int value)
    {
        int laststate = state;
        state = value;
        switch(state)
        {
            case 0:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - validation à la voie.");
                break;
            case 1:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - validation à la commune.");
                break;
            case 2:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - validation du département.");
                break;
            case 3:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - échec de validation : tentative de validation à la commune");
                break;
            case 4:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - Erreur de validation.");
                break;
            case 5:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - validation au pays.");
                break;
            case 6:
                setTitle("JDONREFv2Geocodeur v"+version+" - Validation manuelle des adresses - échec de validation : tentative de validation au pays.");
                break;
        }
        return laststate;
    }
    
    /**
     * Obtient l'état du bouton de validation.
     * @return
     */
    public EtatBoutonValide getButtonState()
    {
        if (jbvalide.getText().compareTo("Valider")==0)
        {
            return EtatBoutonValide.Valider;
        }
        else if (jbvalide.getText().compareTo("Forcer")==0)
        {
            return EtatBoutonValide.Forcer;
        }
        return null;
    }
    
    /**
     * Définit l'état du bouton valide.
     * @param ebv
     */
    public void setButtonState(EtatBoutonValide ebv)
    {
        if (ebv==EtatBoutonValide.Forcer)
        {
            jbvalide.setText("Forcer");
        }
        else
        {
            jbvalide.setText("Valider");
        }
    }
    
    /**
     * Crée et affiche la fenêtre d'édition.<br>
     * Utilisé par main pour créer la fenêtre dans un thread dédié.
     */
    public static JDONREFv2Dialogue createAndShowGUI()
    {
        JDONREFv2Dialogue e = new JDONREFv2Dialogue();
        
        e.setVisible(true);
        
        return e;
    }
    
    /**
     * Permet de tester la classe.
     * @param args
     */
    public static void main(String[] args)
    {
        Runnable r = new Runnable()
                               {
                                   public void run()
                                   {
                                       createAndShowGUI();
                                   }
                               };
        
        SwingUtilities.invokeLater(r);
        
        Propositions a = new Propositions();
        a.id = "111";
        a.ligne1 = "JULIEN MOQUET";
        a.ligne4 = "24 BD HOPITAL";
        a.ligne6 = "75 PARIS";
        
        AdresseValide av = new AdresseValide(a);
        av.ligne4valide = "24 BOULEVARD DE L HOPITAL";
        av.ligne6valide = "75005 PARIS";
        av.note = 200;
        a.propositions.add(av);
        
        av = new AdresseValide(a);
        av.ligne4valide = "BOULEVARD DE L HOPITAL";
        av.ligne6valide = "75013 PARIS";
        av.note = 197;
        a.propositions.add(av);
    }
}