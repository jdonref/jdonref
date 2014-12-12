/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.jdonrefv3.entity;

import java.sql.Connection;
import java.sql.DriverManager;
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
    // TODO commenter le code !!!!
    
    /**
     * 
     */
    protected static InitParameters instance = null;
    public static InitParameters getInstance() throws SQLException
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

       
       
       
       
       
//        public static void main(String[] args) {
//            String[] IDF = {"95", "94", "93", "92", "91", "78", "77", "75"};
//            String[] departements = {"95", "75","85", "75", "75", "75","95"};
//            
//            for(int i=0;i<departements.length;i++){ 
//                System.out.println(departements[i]);
//            }
//            System.out.println("retrait des doublons :");
//            String[] tmp = deleteDoublon(departements);
//            for(int i=0;i<tmp.length;i++){ 
//                System.out.println(tmp[i]);
//            }
//            
//             ArrayList<String> arg2 = new ArrayList<>();
//            String tmp121 = "init3";    
//            String tmp122 = "init3";    
//            String tmp123 = "init3";    
//            String tmp13 = "15";    
//            String tmp141 = "19";    
//            String tmp142 = "19";    
//            String tmp143 = "19";    
//            String tmp15 = "66";           
//            String tmp16 = "30";  
//            String tmp171 = "41";  
//            String tmp172 = "41";  
//            String tmp173 = "41";  
//            arg2.add(tmp121);
//            arg2.add(tmp122);
//            arg2.add(tmp123);
//            arg2.add(tmp13);
//            arg2.add(tmp141);
//            arg2.add(tmp142);
//            arg2.add(tmp143);
//            arg2.add(tmp15);
//            arg2.add(tmp16);
//            arg2.add(tmp171);
//            arg2.add(tmp172);
//            arg2.add(tmp173);
//            deleteDoublon(arg2);
//            
//        String connectionString = "jdbc:postgresql://localhost:5432/JDONREF_IGN2";
//        String user = "postgres";
//        String passwd = "postgres";
//        Connection connection = null;
//        InitParameters p = null;
//        try {
//            connection = DriverManager.getConnection(connectionString,user,passwd);
//            p = InitParameters.getInstance();
//            p.allDeptInit(connection);
//        } catch (SQLException ex) {
//            Logger.getLogger(InitParameters.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//            p.init1();
//            String[] str1 = p.getListeDepartement();
//            String[] strN1 = p.getListeDepartementN();
//             System.out.println("dept complet = "+str1.length);
//             System.out.println("deptN 1 = "+strN1.length);
//
//            p.init2(departements);
//            String[] str2 = p.getListeDepartement();
//            String[] strN2 = p.getListeDepartementN();
//             System.out.println("prends en parametre liste departements et enleve les doublons) = "+str2.length);
//             System.out.println("deptN 2= "+strN2.length);
//             
//             //            p.init3("99","75");
//            p.init3("95","95");
//            String[] str3 = p.getListeDepartement();
//            String[] strN3 = p.getListeDepartementN();
//             System.out.println("intervalle departemet = "+str3.length);
//             System.out.println("deptN 3 = "+strN3.length);
//             
//            
//            p.init2(IDF);
//            String[] str4 = p.getListeDepartement();
//             System.out.println("IDF = "+str4.length);
//
//             ArrayList<String> str = new ArrayList(Arrays.asList(str3));
//             str.addAll(Arrays.asList(str4));
//             for(String s : str){
//                 System.out.println(s);
//             }
//             System.out.println("before (avec doublons)= "+str.size());
//             deleteDoublon(str);
//             for(String s : str){
//                 System.out.println(s);
//             }
//             
//
//             System.out.println("after (sans doublons)= "+str.size());
//             String[] str5 = (String[])str.toArray(new String[str.size()]);
//             int taille5 = str5.length;
//             System.out.println("taille5 = "+taille5);
//             
//            for(int i=0;i<str3.length;i++){ 
//                System.out.println(str3[i]);
//            }
//            System.out.println("taille = "+str3.length);
////            for(int i=0;i<strN3.length;i++){ 
////                System.out.println(strN3[i]);
////            }
////            System.out.println("taille = "+strN3.length);
//
//            ArrayList<String[]> arg = new ArrayList<>();
//            String[] tmp2 = {"init3","01","10"};    //10
//            String[] tmp3 = {"init3","15","05"};    //11 ==> 10+5=15   
//            String[] tmp4 = {"init3","19","21"};    //4 ==> 15+4 = 19
//            String[] tmp5 = {"66","67"};            //2 ==> 19+2 = 21    
//            String[] tmp6 = {"30","31","32","38"};  //4 ==> 21+4 = 25
//            String[] tmp7 = {"41","45","57","70"};  //4 ==> 25+4 = 29
//
//            arg.add(IDF);
//            arg.add(tmp2);
//            arg.add(tmp3);
//            arg.add(tmp4);
//            arg.add(tmp5);
//            arg.add(tmp6);
//            arg.add(tmp7);
//            arg.add(tmp7);
//            p.init4(arg);
//            String[] str7 = p.getListeDepartement();
//            String[] strN7 = p.getListeDepartementN();
//             System.out.println("dept liste = "+str7.length);
//             System.out.println("deptN 7 = "+strN7.length);
//             for(int i=0;i<str7.length;i++){ 
//                System.out.println(str7[i]);
//            }
//            
//            
//    }
    
}
