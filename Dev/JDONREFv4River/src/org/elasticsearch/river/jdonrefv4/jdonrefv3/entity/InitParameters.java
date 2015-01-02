/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.river.jdonrefv4.jdonrefv3.dao.CommuneDAO;


/**
 *
 * @author akchana
 */
public class InitParameters {
    
    

     String[] listeDepartement;
     String[] listeDepartementN;
    
    ArrayList<String> allDept = new ArrayList<>();
    protected static InitParameters instance = null;
    
    // TODO commenter le code !!!!
    
    /**
     * singleton
     */
    public static InitParameters getInstance()
    {
        if (instance==null){
            instance = new InitParameters();
        }
        return instance;
    }

    
// liste des departements  01..19,20_a,20_b,21..95,971..974 ==> 100
//        public ArrayList<String> allDept()
//    {
//        ArrayList<String> str= new ArrayList<>();
//        for(int i=1;i<96;i++){
//            if(i == 20){
//                str.add("20_a");
//                str.add("20_b");
//            }
//            else{
//                if(i<10) str.add("0"+i);
//                else str.add(""+i);
//            }
//        }
//        for(int j=1;j<=4;j++)
//            str.add(97+""+j);
//        return str;
//    }
    
        public void allDeptInit(Connection connection) {
            
            CommuneDAO dao = new CommuneDAO();
            ResultSet rs = null;
        try {
            rs = dao.getAllDep(connection);
            while(rs.next())
            {
                Commune c = new Commune(rs);
                allDept.add(c.dpt_code_departement);
            }
        } catch (SQLException ex) {
            Logger.getLogger(InitParameters.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
     public String[] getListeDepartement() {
        return listeDepartement;
    }

    public void setListeDepartement(String[] listeDepartement) {
        this.listeDepartement = listeDepartement;
    }

     public String[] getListeDepartementN() {
        return listeDepartementN;
    }

    public void setListeDepartementN(String[] listeDepartementN) {
        this.listeDepartementN = listeDepartementN;
    }

    public ArrayList<String> getAllDept() {
        return allDept;
    }
    
    public void setAllDept(ArrayList<String> allDept) {
        this.allDept = allDept;
    }

    /**
     * verification doublon
     * retourne tout les departements
     * 
     */
    public void init1() {
        ArrayList<String> depts = (ArrayList<String>) allDept.clone();
        deleteDoublon(depts);
        setListeDepartement(DeptArrayToStr(depts,0,depts.size()-1));
        setListeDepartementN(new String[0]);
    }
    
    /**
     * retourne la liste de departements donnés en parametre si sont tous present 
     * verification doublon
     * 
     * @param dep2 
     */
    public void init2(String[] dep2) {
        if(dep2 == null || dep2.length == 0) throw new Error("liste numero departement vide ou null");
        ArrayList<String> depts = (ArrayList<String>) allDept.clone();
        deleteDoublon(depts);
        String[] dep = deleteDoublon(dep2);
        boolean flag = true;
        if(!depts.containsAll(Arrays.asList(dep))) flag = false;
        if(flag == true){
            setListeDepartement(dep); 
            depts.removeAll(Arrays.asList(getListeDepartement()));
            setListeDepartementN(DeptArrayToStr(depts,0,depts.size()-1));
        }
        else throw new Error("la liste contient un ou plusieur numeros de departements inconnu");
        
    }
    
    /**
     * retourne las liste des departement compris entre deb et fin si les 2 sont present 
     * deb peut etre plus grand que fin auquel cas on parcours dans l'autre sens
     * verification doublon
     *  TODO au lieu de mettre init3("01","15") pour l'ensemble mettre init3({"01..15"})
     * 
     * @param DepDeb
     * @param DepFin 
     */
    public void init3(String DepDeb, String DepFin){
        ArrayList<String> depts = (ArrayList<String>) allDept.clone();
        deleteDoublon(depts);
        if(depts.contains(DepDeb) && depts.contains(DepFin)){
             setListeDepartement(DeptArrayToStr(depts,depts.indexOf(DepDeb),depts.indexOf(DepFin)));
             depts.removeAll(Arrays.asList(getListeDepartement()));
             setListeDepartementN(DeptArrayToStr(depts,0,depts.size()-1));
        }else{
            throw new Error("numeros de departements inconnu");
        }    
    }
    
    
    
    /**
     * prends en parametre des ensemble et/ou intervalle de departement et retourne une liste de tout les departement
     * en gerant les doublons
     * String[] tmp1 = {"init3","01","15"};    String[] tmp2 = {"41","45","57","70"};
     * ArrayList<String[]> arg = new ArrayList<>(); arg.add(tmp1); arg.add(tmp2);
     * p.init4(arg); 
     * retourne les dep de 01 à 15 et 41,45,57,70 soit 19 dept au total       
     * TODO au lieu de mettre {"init3","01","15"} pour l'ensemble mettre {"01..15"}
     * 
     * @param arg 
     */
   
    public void init4(ArrayList<String[]> arg){
        if(arg.isEmpty() || arg == null) throw new Error("liste vide ou null");

            for(int i = 0; i<arg.size();i++){
                if(arg.get(i)[0].equals("init3")){
                    ArrayList<String> str = new ArrayList(Arrays.asList(getListeDepartement()));
                    init3(arg.get(i)[1], arg.get(i)[2]);
                    str.addAll(Arrays.asList(getListeDepartement()));
                    deleteDoublon(str);
                    setListeDepartement((String[])str.toArray(new String[str.size()]));
                }
                else{
                    ArrayList<String> str = new ArrayList(Arrays.asList(getListeDepartement()));
                    init2(arg.get(i));
                    str.addAll(Arrays.asList(getListeDepartement()));
                    deleteDoublon(str);
                    setListeDepartement((String[])str.toArray(new String[str.size()]));
                }
            }
            init2(getListeDepartement());
  
    }
    

    public String[] DeptArrayToStr(ArrayList<String> ldep, int deb, int fin){
        List<String> list;
        if(deb>fin){
            list = ldep.subList(fin, deb+1);
            Collections.reverse(list);
        }else 
            list = ldep.subList(deb, fin+1);

        return (String[])list.toArray(new String[list.size()]);
    }

        
       static public void deleteDoublon(ArrayList<String> arrayL2){
           ArrayList<String> tmp = new ArrayList<>();
           ArrayList<String> arrayL1 = new ArrayList<>();
           while(!arrayL2.isEmpty()){
               tmp.add(arrayL2.get(0));
                for (int j = 1; j < arrayL2.size(); j++) {
                    if(arrayL2.get(0).equals(arrayL2.get(j))){
                        tmp.add(arrayL2.get(j)); 
                    }
                }
                arrayL1.add(arrayL2.get(0));
                arrayL2.removeAll(tmp);
                tmp.removeAll(tmp);
           }
           arrayL2.removeAll(arrayL2);
           arrayL2.addAll(arrayL1);
       }
       

       static public String[] deleteDoublon(String[] tabStr){
            ArrayList arrayL = new ArrayList(Arrays.asList(tabStr));
            deleteDoublon(arrayL);
            return (String[])arrayL.toArray(new String[arrayL.size()]);
        }


}
