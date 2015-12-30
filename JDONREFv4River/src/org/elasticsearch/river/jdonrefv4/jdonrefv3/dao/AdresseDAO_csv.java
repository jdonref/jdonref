/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.elasticsearch.river.jdonrefv4.jdonrefv3.dao;

import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akchana
 */
public class AdresseDAO_csv {
    CSVReader csvReader;
    public List<String[]> datas = new ArrayList<>();

    public AdresseDAO_csv(CSVReader csvReader) {
        this.csvReader = csvReader;
        init();
    }
    
    public void init(){
        try {
            String[] nextLine = csvReader.readNext();
            if(!checkFile(nextLine)) throw new Error("cvs MalForm - Bad Column");
            while((nextLine = csvReader.readNext())!=null){
                if(nextLine.length == 0) continue;
                if(nextLine[0].trim().length()== 0 && nextLine.length == 1) continue;
                if(nextLine[0].trim().startsWith("#")) continue;
                datas.add(nextLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(AdresseDAO_csv.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
        public boolean checkFile(String[] firstLine){
        boolean goodFile = false;
        if(firstLine[0].equals("id")
            && firstLine[1].equals("nom_voie")
            && firstLine[2].equals("id_fantoir")	
            && firstLine[3].equals("numero")
            && firstLine[4].equals("rep")
            && firstLine[5].equals("code_insee")
            && firstLine[6].equals("code_post")
            && firstLine[7].equals("alias")
            && firstLine[8].equals("nom_ld")
            && firstLine[9].equals("nom_afnor")
            && firstLine[10].equals("libelle_acheminement")
            && firstLine[11].equals("x")
            && firstLine[12].equals("y")	
            && firstLine[13].equals("lon")	
            && firstLine[14].equals("lat")	
            && firstLine[15].equals("nom_commune")) goodFile = true;
        return goodFile;   
    }
    
    
}
