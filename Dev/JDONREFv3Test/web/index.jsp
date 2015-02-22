<%-- 
    Document   : index
    Created on : 2 mai 2008, 09:27:34
    Author     : jmoquet
    
  Version 2.1.5 – Juin 2009
  CeCILL Copyright © Préfecture de Police
  Contributeurs : MIOCT/PP/DOSTL/SDSIC, MIOCT/PP/DPJ 
  julien.moquet@interieur.gouv.fr
  
  Ce logiciel est un service web servant à valider et géocoder des adresses postales.
  Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant 
  les principes de diffusion des logiciels libres. Vous pouvez utiliser, modifier 
  et/ou redistribuer ce programme sous les conditions de la licence CeCILL telle que 
  diffusée par le CEA, le CNRS et l'INRIA sur le site "http://www.cecill.info".
  En contrepartie de l'accessibilité au code source et des droits de copie, de 
  modification et de redistribution accordés par cette licence, il n'est offert aux 
  utilisateurs qu'une garantie limitée.  Pour les mêmes raisons, seule une 
  responsabilité restreinte pèse sur l'auteur du programme, le titulaire des droits 
  patrimoniaux et les concédants successifs.
  A cet égard l'attention de l'utilisateur est attirée sur les risques associés au 
  chargement,  à l'utilisation,  à la modification et/ou au développement et à la 
  reproduction du logiciel par l'utilisateur étant donné sa spécificité de logiciel 
  libre, qui peut le rendre complexe à manipuler et qui le réserve donc à des 
  développeurs et des professionnels avertis possédant  des  connaissances 
  informatiques approfondies.  Les utilisateurs sont donc invités à charger  et tester
  l'adéquation  du logiciel à leurs besoins dans des conditions permettant d'assurer la
  sécurité de leurs systèmes et ou de leurs données et, plus généralement, à l'utiliser
  et l'exploiter dans les mêmes conditions de sécurité. 
  Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris 
  connaissance de la licence CeCILL, et que vous en avez accepté les termes.
--%>
<%!
String JADRREFJson = null;
public void jspInit()
{
    JADRREFJson = getServletConfig().getServletContext().getInitParameter("JDONREFv3JSON");
}
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tests de JDONREF v3</title>
    </head>
    <script language="javascript">
        // diverses initialisations
        function init()
        {
            currentindex = 0;
            departements = "";
        }
        
        var adr_4 = "";
        var adr_6 = "";
        //
        //
        // Obtient le résultat d'une requête et la traite par la fonction spécifiée
        //
        function getfile(url,data,fonction)
        {
            if(window.XMLHttpRequest) // FIREFOX
                xhr_object = new XMLHttpRequest(); 
            else if(window.ActiveXObject) // IE
                xhr_object = new ActiveXObject("Microsoft.XMLHTTP"); 
            else 
                return(null);
            
            if (data==null)
                xhr_object.open("GET", url, false);
            else
                xhr_object.open("GET", url+"?"+data, false);
            
            //xhr_object.onreadystatechange = fonction;
            xhr_object.setRequestHeader("Content-Type", "text/text; charset=utf-8");
            xhr_object.send(null);
            
            fonction();
            
            return xhr_object;
        }
        
        // retourne un objet date.
        function parseDate(strdate)
        {
            var reg = new RegExp("/","g");
            
            strdate = strdate.replace("-","/").substr(0,10);
            
            var data = strdate.split(reg);
            var date = new Date();
            date.setYear(eval(data[2])); // Dans l'ordre Year, Month, Date pour la correction du 31 en fin de mois.
            date.setMonth(eval(data[1])-1); // les mois vont de 0 à 11
            date.setDate(eval(data[0]));
            return date;
        }
        
        // Retourne si l'intervalle de dates spécifié contient la date actuelle.
        function estCourant(t0,t1)
        {
            var reg = new RegExp("/","g");
            
            t0 = t0.replace("-","/").substr(0,10);
            t1 = t1.replace("-","/").substr(0,10);
        
            var data0 = t0.split(reg);
            var date0 = new Date();
            date0.setYear(eval(data0[2])); // Dans l'ordre Year, Month, Date pour la correction du 31 en fin de mois.
            date0.setMonth(eval(data0[1])-1); // les mois vont de 0 à 11
            date0.setDate(eval(data0[0]));
            
            var data1 = t1.split(reg);
            var date1 = new Date();
            date1.setYear(eval(data1[2])); // Dans l'ordre Year, Month, Date pour la correction du 31 en fin de mois.
            date1.setMonth(eval(data1[1])-1); // les mois vont de 0 à 11
            date1.setDate(eval(data1[0]));
            
            var date = new Date();
            if (date0.getTime()<=date.getTime() && date.getTime()<=date1.getTime())
                return true;
            return false;
        }
        
        // Choisi l'adresse sélectionnée dans la liste.
        function choisi(index)
        {
             var formulaire = document.getElementById("formulaire");
             formulaire.ligne1.value = data.propositions[index].donnees[0];
             formulaire.ligne2.value = data.propositions[index].donnees[1];
             formulaire.ligne3.value = data.propositions[index].donnees[2];
             formulaire.ligne4.value = data.propositions[index].donnees[3];
             formulaire.ligne5.value = data.propositions[index].donnees[4];
             formulaire.ligne6.value = data.propositions[index].donnees[5];
             formulaire.ligne7.value = (data.propositions[index].donnees.length > 6 && data.propositions[index].donnees[6] != null) ? data.propositions[index].donnees[6] : "";
             
             clearAdds();
        }
        
        //
        // Suit la selection dans la liste de choix des adresses:
        // Pour les voies:
        //   Effectue une revalidation,un géocodage et affiche le code fantoir associé à la voie.
        // Pour les communes:
        //   Effectue un géocodage.
        function change(index)
        {
            // Commence par changer de couleur la ligne sélectionnée.
            var table = document.getElementById("table");
            table.tBodies[0].rows[currentindex].cells[0].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[1].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[2].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[3].style.color = "black";
            table.tBodies[0].rows[index].cells[0].style.color = "blue";
            table.tBodies[0].rows[index].cells[1].style.color = "blue";
            table.tBodies[0].rows[index].cells[2].style.color = "blue";
            table.tBodies[0].rows[index].cells[3].style.color = "blue";
            currentindex = index;
            var projection = document.getElementById("inp_projection").value;
            var distance = document.getElementById("inp_distance").value;
            var services = document.getElementById("formulaire").services;
            var pays_id = data.propositions[index].ids[6];
            var ligne7 = data.propositions[index].donnees[6];
            adr_4 = data.propositions[index].donnees[3];
            adr_6 = data.propositions[index].donnees[5];
            
            //var choix = document.getElementById("choix");
            //var index = choix.selectedIndex;
            var tValidation,tRevalidation,misesajour;
            if (data.codeRetour==1 || data.codeRetour==2) // S'il s'agit d'une voie
            {
                    // Affiche le code fantoir
                    //var fantoir = document.getElementById("fantoir");
                    //fantoir.innerHTML = "Code Fantoir:"+data.propositions[index].options['codefantoire'];
                    
                    // S'occupe de la revalidation
                    tValidation = parseDate(data.propositions[index].t0);
                    
                    // Effectue la revalidation si nécessaire.
                    // l'objet data est toujours en mémoire (cf valide)
                    // Si l'élément choisi dans la liste n'a pas une date récente de validité,
                    if (!estCourant(data.propositions[index].t0,data.propositions[index].t1))
                    {
                        tRevalidation = new Date();
                        
                        //formulaire,action,fonction,ligne4,ligne6,dateValidation,dateRevalidation
                        revalide("Revalide",traiteRevalide,services,
                            data.propositions[index].ids[0],
                            data.propositions[index].ids[3],
                            data.propositions[index].ids[5],
                            "",
                            data.propositions[index].donnees[3],
                            data.propositions[index].donnees[5],
                            "",
                            tValidation,tRevalidation);
                    }
                    else
                    {
                        misesajour = document.getElementById("misesajour");
                        misesajour.innerHTML = "";
                    }
                    
                    // Effectue le géocodage.                    
                    if (data.codeRetour==1 || data.codeRetour==2)
                        geocode("Geocode",traiteGeocodage,services,data.propositions[index].ids[3],data.propositions[index].donnees[3],data.propositions[index].ids[5], pays_id, projection, tValidation, distance);
            }
            else if(data.codeRetour==3 || data.codeRetour==4) // S'il s'agit d'une commune
            {
                // S'occupe de la revalidation
                tValidation = parseDate(data.propositions[index].t0);
                    
                // Effectue la revalidation si nécessaire.
                // l'objet data est toujours en mémoire (cf valide)
                // Si l'élément choisi dans la liste n'a pas une date récente de validité,
                if (!estCourant(data.propositions[index].t0,data.propositions[index].t1))
                {
                    tRevalidation = new Date();
                        
                    //formulaire,action,fonction,ligne4,ligne6,dateValidation,dateRevalidation
                    revalide("Revalide",traiteRevalide,services,
                            data.propositions[index].ids[0],
                            data.propositions[index].ids[3],
                            data.propositions[index].ids[5],
                            "",
                            "",
                            data.propositions[index].donnees[5],
                            "",
                            tValidation,tRevalidation);
                }
                else
                {
                    var misesajour = document.getElementById("misesajour");
                    misesajour.innerHTML = "";
                }

                geocode("Geocode",traiteGeocodage,data.services,"","",data.propositions[index].ids[5], pays_id, projection, tValidation, distance);
            }
            else// S'il s'agit d'un pays
            {
                // S'occupe de la revalidation
                tValidation = parseDate(data.propositions[index].t0);

                // Effectue la revalidation si nécessaire.
                // l'objet data est toujours en mémoire (cf valide)
                // Si l'élément choisi dans la liste n'a pas une date récente de validité,
                if (!estCourant(data.propositions[index].t0,data.propositions[index].t1))
                {
                    tRevalidation = new Date();

                    //formulaire,action,fonction,ligne4,ligne6,dateValidation,dateRevalidation
                    revalide("Revalide",traiteRevalide,services,
                            data.propositions[index].ids[0],
                            data.propositions[index].ids[3],
                            data.propositions[index].ids[5],
                            "",
                            "",
                            "",
                            data.propositions[index].donnees[6],
                            tValidation,tRevalidation);
                }
                else
                {
                    var misesajour = document.getElementById("misesajour");
                    misesajour.innerHTML = "";
                }

                geocode("Geocode",traiteGeocodage,data.services,"",data.propositions[index].ids[6], "", pays_id, projection, tValidation, distance);
            }
        }
        //
        // Permet de traiter le résultat du géocodage.
        //
        function traiteGeocodage()
        {
            if (xhr_object.readyState==4)
            {
                var geocodage = document.getElementById("geocodage");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;
                        
                    if (res==null || res=="")
                    {
                        geocodage.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    }
                    else
                    {
                        var geocodagedata = eval("("+res+")");
                            
                        // Retourne un objet JSON de format : 
                        // { etat:code, ...} où code détermine le reste de la structure.<br>
                        // si code==0, il s'agit d'une erreur et la structure est:<br>
                        // {etat:0,codeerreur:code erreur,message:"message d'erreur"}<br>
                        // sinon code==7 , il s'agit d'un géocodage, et la structure est:<br>
                        // {etat:7, compte:nb de lignes, geocodage:{typegeocodage:int,x:double,y:double,dategeocodage:date,referentiel:string,projection:string}}
                        if (geocodagedata.codeRetour==0)
                        {
                            geocodage.innerHTML = "Erreur " + geocodagedata.erreurs[0].code + " : " + geocodagedata.erreurs[0].message;
                        }
                        else
                        {
                            
                            
                            if (geocodagedata.propositions == null || geocodagedata.propositions.length == 0)
                            {
                                geocodage.innerHTML = "Aucun géocodage trouvé.";
                            }
                            else
                            {
                                var size = geocodagedata.propositions.length;
                                var adr= adr_4 + " " + adr_6;
                                var balise = "géocodage<br>";
                                balise += "<table>";
                                balise += "<tr><td>type</td><td>";
                                switch(geocodagedata.propositions[0].type)
                                {
                                    case '1': balise += "a la plaque ou au point"; break;
                                    case '2': balise += "a l'interpolation à la plaque"; break;
                                    case '3': balise += "a l'interpolation métrique du tronçon ou aux bornes du troncon"; break;
                                    case '4': balise += "au centroïde du troncon"; break;
                                    case '5': balise += "au centroide de la voie"; break;
                                    case '6': balise += "a la commune,l'arrondissement ou la zone"; break;
                                    case '7': balise += "au département"; break;
                                    case '8': balise += "au pays"; break;
                                }
                                balise += "</td></tr>";
                                balise += "<tr><td>x</td><td>"+geocodagedata.propositions[0].x+"</td></tr>"; 
                                balise += "<tr><td>y</td><td>"+geocodagedata.propositions[0].y+"</td></tr>";
                                balise += "<tr><td>date</td><td>"+geocodagedata.propositions[0].date+"</td></tr>";
                                balise += "<tr><td>referentiel</td><td>"+geocodagedata.propositions[0].referentiel+"</td></tr>";
                                balise += "<tr><td>projection</td><td>"+geocodagedata.propositions[0].projection+"</td></tr>";
                                balise += "<tr><td colspan='2'><a href='reverse.jsp?ids4="+data.propositions[0].ids[3]+"&x="+geocodagedata.propositions[0].x+"&y="+geocodagedata.propositions[0].y+"&adr="+adr+"&projection="+geocodagedata.propositions[0].projection+"'>Inverse</a></td></tr>";
                                balise += "</table>";
                                geocodage.innerHTML = balise;
                            }
                        }
                    }
                }
                else
                    geocodage.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        }
        //
        //
        // Permet de traiter le résultat de la revalidation.
        //
        function traiteRevalide()
        {
            if (xhr_object.readyState==4)
            {
                var misesajour = document.getElementById("misesajour");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;
                        
                    if (res==null || res=="")
                    {
                        misesajour.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    }
                    else
                    {
                        revalidedata = eval("("+res+")"); // ATTENTION : Cette variable est globale

                        // Retourne un objet JSON de format : 
                        // { etat:code, ...} où code détermine le reste de la structure.<br>
                        // si code==0, il s'agit d'une erreur et la structure est:<br>
                        // {etat:0,codeerreur:code erreur,message:"message d'erreur"}<br>
                        // sinon code==6 , il s'agit d'un choix d'adresses, et la structure est:<br>
                        // {etat:6, compte:nb de lignes, adresses:[{idvoie:id,ligne4:ligne 4,ligne6:ligne 6,code_insee:code insee,t0:t0,t1:t1}]}
                        if (revalidedata.codeRetour==0)
                        {
                            misesajour.innerHTML = "Erreur " + revalidedata.erreurs[0].code + " : " + revalidedata.erreurs[0].message;
                        }
                        else
                        {
                            var size = revalidedata.propositions.length;
                            
                            if (size>0)
                            {
                                var balise = "est devenu : <select>";
                                for(i=0;i<size;i++)
                                {
                                    balise += "<option value=";
                                    balise += i;
                                    balise += ">";
                                    if(revalidedata.propositions[i].service < 8){
                                        balise += revalidedata.propositions[i].donnees[3] + " " + revalidedata.propositions[i].donnees[5];
                                    }else if(revalidedata.propositions[i].service >= 100 && revalidedata.propositions[i].service <= 102){
                                        balise +=revalidedata.propositions[i].donnees[0];
                                    }
                                    balise += "</option>";
                                }
                                balise += "</select>";
                                misesajour.innerHTML = balise;
                            }
                            else
                            {
                                misesajour.innerHTML = " n'existe plus.";
                            }
                        }
                    }
                }
                else
                    misesajour.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        }
       
        // Permet de traiter le résultat de la validation.
        // 
        function traiteValide()
        {
            if (xhr_object.readyState==4)
            {
                var propositions = document.getElementById("propositions");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;
                                               
                    if (res==null || res=="")
                    {
                        propositions.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                        end = new Date();
                        afficheTimer('dureevalidation', startvalide, end);
                    }
                    else
                    {
                        var end;
                        data = eval("("+res+")"); // ATTENTION: ne pas surcharger ce nom de variable, elle est utilisée en global!
                    
                        // Retourne un objet JSON de format : 
                        // { etat:code, ...} où code détermine le reste de la structure.<br>
                        // si code==0, il s'agit d'une erreur et la structure est:<br>
                        // {etat:0,codeerreur:code erreur,message:"message d'erreur"}<br>
                        // si code==1 ou 2, il s'agit d'un choix de voies, et la structure est:<br>
                        // {etat:1 ou 2, compte:nb de lignes, adresses:[{idvoie:id,ligne4:ligne4,codeinsee:codeinsee,ligne6:ligne6,t0:t0,t1:t1,note:note}]}
                        // si code==3 ou 4, il s'agit d'un choix de communes, et la structure est:<br>
                        // {etat:3 ou 4, compte:nb de lignes, communes:[{codeinsee:codeinsee,ligne6:ligne6,t0:t0,t1:t1,note:note}]}
                        if (data.codeRetour==0)
                        {
                            end = new Date();
                            afficheTimer('dureevalidation', startvalide, end);
                            
                            propositions.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                        }else{
                            if (data.propositions == null || data.propositions.length == 0)
                            {
                                end = new Date();
                                afficheTimer('dureevalidation', startvalide, end);
                                        
                                propositions.innerHTML = "Aucun résultat trouvé.";
                            }
                            else
                            {
                                var size = data.propositions.length;
                                var balise = "<table id=\"table\">";
                                for(i=0;i<size;i++) {
                                    if (estCourant(data.propositions[i].t0,data.propositions[i].t1))
                                        strestcourant = "ACTUEL";
                                    else
                                        strestcourant = "ANCIEN";
                                    if (data.propositions[i].code==1 || data.propositions[i].code==2)
                                    {
                                    balise += "<tr><td>";
                                    balise += "</td><td>";
                                    balise += data.propositions[i].donnees[0];
                                    balise += "</td><td>";
                                    balise += data.propositions[i].donnees[3];
                                    balise += "</td><td>";
                                    balise += data.propositions[i].donnees[5];
                                    balise += "</td><td>";
                                    if(data.propositions[i].donnees.length > 6 && data.propositions[i].donnees[6] != null )
                                    {
                                        balise += data.propositions[i].donnees[6];
                                    }
                                    balise += "</td><td>";
                                    balise += strestcourant;
                                    balise += "</td><td>"
                                    balise += "note :"+ data.propositions[i].note;
                                    balise += "</td><td>";
                                    balise += "service :"+ data.propositions[i].service;
                                    balise += "</td><td>";
                                    balise += "<button onclick=\"change("+i+");\">Geocoder</button>";
                                    balise += "<button onclick=\"revalidation(" + i + ");\">Revalider</button>";
                                    balise += "<button onclick=\"choisi("+i+");\">Choisir</button>";
                                    balise += "</td></tr>"
                                    }else if (data.propositions[i].code==3 || data.propositions[i].code==4){
                                    balise += "<tr><td>";
                                    balise += "</td><td>";
                                    balise += "</td><td>";
                                    balise += data.propositions[i].donnees[5];
                                    balise += "</td><td>";
                                    balise += "</td><td>";
                                    balise += strestcourant;
                                    balise += "</td><td>"
                                    balise += "note :"+ data.propositions[i].note;
                                    balise += "</td><td>";
                                    balise += "service :"+ data.propositions[i].service;
                                    balise += "</td><td>";
                                    balise += "<button onclick=\"change("+i+");\">Geocoder</button>";
                                    balise += "<button onclick=\"revalidation(" + i + ");\">Revalider</button>";
                                    balise += "<button onclick=\"choisi("+i+");\">Choisir</button>";
                                    balise += "</td></tr>";
                                    }else if (data.propositions[i].code==5 || data.propositions[i].code==6){
                                    balise += "<tr><td>";
                                    balise += "</td><td>";
                                    balise += "</td><td>";
                                    balise += "</td><td>";
                                    balise += data.propositions[i].donnees[6]; 
                                    balise += "</td><td>";
                                    balise += strestcourant;
                                    balise += "</td><td>"
                                    balise += "note :"+ data.propositions[i].note;
                                    balise += "</td><td>";
                                    balise += "service :"+ data.propositions[i].service;
                                    balise += "</td><td>";
                                    balise += "<button onclick=\"change("+i+");\">Geocoder</button>";
                                    balise += "<button onclick=\"revalidation(" + i + ");\">Revalider</button>";
                                    balise += "<button onclick=\"choisi("+i+");\">Choisir</button>";
                                    balise += "</td></tr>";
                                    }else if (data.propositions[i].code==100 || data.propositions[i].code==8)
                                    {
                                        balise += "<tr><td>";
                                        balise += data.propositions[i].donnees[0];
                                        balise += "</td><td>";
                                        balise += data.propositions[i].donnees[3];
                                        balise += "</td><td>";
                                        balise += data.propositions[i].donnees[5];
                                        balise += "</td><td>";
                                        if(data.propositions[i].donnees.length > 6 && data.propositions[i].donnees[6] != null )
                                        {
                                            balise += data.propositions[i].donnees[6];
                                        }
                                        balise += "</td><td>";
                                        balise += strestcourant;
                                        balise += "</td><td>"
                                        balise += "note :"+ data.propositions[i].note;
                                        balise += "</td><td>";
                                        balise += "service :"+ data.propositions[i].service;
                                        balise += "</td><td>";
                                        balise += "<button onclick=\"changePoizon(" + i + ");\">Geocoder</button>";
                                        balise += "<button onclick=\"revalidation(" + i + ");\">Revalider</button>";
                                        balise += "<button onclick=\"choisi("+i+");\">Choisir</button>";
                                        balise += "</td></tr>";
                                    }
                            }
                            balise += "</table>";
                            propositions.innerHTML = balise;
                            end = new Date();
                            afficheTimer('dureevalidation', startvalide, end);

                            currentindex = 0;
                            if(data.propositions[0].code > 0 && data.propositions[0].code < 7){
                                change(0);
                            }else if(data.propositions[0].code == 100 || data.propositions[0].code == 8){
                                changePoizon(0);
                            }
                          }
                        
                        }
                    }
                }
                else{
                    propositions.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
                }
            }
            else
            {
                var end = new Date();
                afficheTimer('dureevalidation', startvalide, end);
            }            
        };
        //
        // Permet de traiter le résultat de la récupération de la version de JDONREF.
        // 
        function traiteGetVersion()
        {
            if (xhr_object.readyState==4){
                var signalements = document.getElementById("signalements");
                if (xhr_object.status==200){
                    var res = xhr_object.responseText;
                    
                    if (res==null || res==""){
                        signalements.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    }else{
                        var data = eval("("+res+")");
                    
                        if (data.CodeRetour==0){
                            signalements.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                        }else{
                            if (data.propositions == null || data.propositions.length == 0){
                                 signalements.innerHTML = "Aucune version.";
                            }else{
                                var size = data.propositions.length;
                                for(i=0;i<size;i++){
                                document.Normalise.ligne1.value += data.propositions[i].nom + ';';
                                document.Normalise.ligne2.value += data.propositions[i].version + ';';
                                }
                            }
                        }
                    }
                }else
                    signalements.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        };
        //
        // Permet de traiter le résultat du signalement de l'erreur.
        // 
        function traiteSignale()
        {
            if (xhr_object.readyState==4)
            {
                var signalements = document.getElementById("signalements");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;               
                    
                    if (res==null || res=="")
                        signalements.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    else
                    {
                        var data = eval("("+res+")");
                    
                        if (data.etat==0)
                        {
                            signalements.innerHTML = data.message;
                        }
                        else
                        {
                            signalements.innerHTML = "Message envoyé.";
                        }
                    }
                }
                else
                    signalements.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        };
        //
        // Permet de traiter le résultat de la restructuration.
        // 
        function traiteRestructure()
        {
            if (xhr_object.readyState==4)
            {
                var normalisations = document.getElementById("normalisations");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;
                    
                    if (res==null || res=="")
                        normalisations.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    else
                    {
                        var data = eval("("+res+")");
                    
                         if (data.codeRetour==0)
                        {
                            normalisations.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                        }
                        else
                        {
                            document.Normalise.ligne1.value = data.propositions[0].donnees[0];
                            document.Normalise.ligne2.value = data.propositions[0].donnees[1];
                            document.Normalise.ligne3.value = data.propositions[0].donnees[2];
                            document.Normalise.ligne4.value = data.propositions[0].donnees[3];
                            document.Normalise.ligne5.value = data.propositions[0].donnees[4];
                            document.Normalise.ligne6.value = data.propositions[0].donnees[5];
                            document.Normalise.ligne7.value = (data.propositions[0].donnees.length > 6 && data.propositions[0].donnees[6] != null) ? data.propositions[0].donnees[6] : "" ;
                            
                            if (data.propositions[0].options!=null && data.propositions[0].options.length > 0){
                                // options
                                for(i = 0; i < data.propositions[0].options.length; i++){
                                    var option = data.propositions[0].options[i];
                                    var tokens = option.split('=');
                                    if(tokens != null && tokens.length == 2){
                                        if(tokens[0] == "numeros"){
                                            document.Normalise.supplements.value = tokens[1];
                                        }else if(tokens[0] == "dpt"){
                                            departements = tokens[1];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                    normalisations.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        };
        //
        // Permet de traiter le résultat de la normalisation.
        // 
        function traiteNormalise(){
            if (xhr_object.readyState==4){
                var normalisations = document.getElementById("normalisations");
                if (xhr_object.status==200){
                    var res = xhr_object.responseText;  
                    if (res==null || res==""){
                        normalisations.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    }else{
                        var data = eval("("+res+")");
                        if (data.codeRetour==0){
                            normalisations.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                        }else{
                            document.Normalise.ligne1.value = data.propositions[0].donnees[0];
                            document.Normalise.ligne2.value = data.propositions[0].donnees[1];
                            document.Normalise.ligne3.value = data.propositions[0].donnees[2];
                            document.Normalise.ligne4.value = data.propositions[0].donnees[3];
                            document.Normalise.ligne5.value = data.propositions[0].donnees[4];
                            document.Normalise.ligne6.value = data.propositions[0].donnees[5];
                            document.Normalise.ligne7.value = (data.propositions[0].donnees.length > 6 && data.propositions[0].donnees[6] != null) ? data.propositions[0].donnees[6] : "" ;
                            if (data.propositions[0].options!=null && data.propositions[0].options.length > 0){
                                // options
                                for(i = 0; i < data.propositions[0].options.length; i++){
                                    var option = data.propositions[0].options[i];
                                    var tokens = option.split('=');
                                    if(tokens != null && tokens.length == 2){
                                        if(tokens[0] == "numeros"){
                                            document.Normalise.supplements.value = tokens[1];
                                        }else if(tokens[0] == "dpt"){
                                            departements = tokens[1];
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    normalisations.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
                }
            }
        };
        
        //
        // Permet de traiter le résultat d'un découpage.
        // 
        function traiteDecoupe()
        {
            if (xhr_object.readyState==4)
            {
                var normalisations = document.getElementById("normalisations");
                if (xhr_object.status==200)
                {
                    var res = xhr_object.responseText;               
                    
                    if (res==null || res=="")
                        normalisations.innerHTML = "Aucun résultat n'a été retourné par le web service.";
                    else
                    {
                        var data = eval("("+res+")");
                    
                        if (data.codeRetour==0)
                        {
                            normalisations.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                        }
                        else
                        { 
                            document.Normalise.ligne1.value = (data.propositions[0].donnees.length > 0 && data.propositions[0].donnees[0] != null) ? data.propositions[0].donnees[0] : "";
                            document.Normalise.ligne2.value = (data.propositions[0].donnees.length > 1 && data.propositions[0].donnees[1] != null) ? data.propositions[0].donnees[1] : "";
                            document.Normalise.ligne3.value = (data.propositions[0].donnees.length > 2 && data.propositions[0].donnees[2] != null) ? data.propositions[0].donnees[2] : "";
                            document.Normalise.ligne4.value = (data.propositions[0].donnees.length > 3 && data.propositions[0].donnees[3] != null) ? data.propositions[0].donnees[3] : "";
                            document.Normalise.ligne5.value = (data.propositions[0].donnees.length > 4 && data.propositions[0].donnees[4] != null) ? data.propositions[0].donnees[4] : "";
                            document.Normalise.ligne6.value = (data.propositions[0].donnees.length > 5 && data.propositions[0].donnees[5] != null) ? data.propositions[0].donnees[5] : "";
                            document.Normalise.ligne7.value = (data.propositions[0].donnees.length > 6 && data.propositions[0].donnees[6] != null) ? data.propositions[0].donnees[6] : "" ;
                            
                            if (data.propositions[0].options!=null && data.propositions[0].options.length > 0){
                                // options
                                //document.Normalise.supplements.value = data.supplements;
                            }
                            //departements = null;
                        }
                    }
                }
                else
                    normalisations.innerHTML = "Erreur durant la communication avec le web service, statut"+xhr_object.status;
            }
        };
        // 
        // Permet de récupérer le résultat du géocodage
        // 
        function geocode(action,fonction,services,id_voie,ligne4,code_insee, pays_id, projection, dateValidation, distance)
        {
            var start = new Date();
            var date=new Date();
            var servicesParam = "";
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                }
            }
            var url = '<%=JADRREFJson%>/'+action;
            var params = 'application=2'+
                servicesParam +
                '&ids=&ids=&ids=&ids='+encodeURIComponent(id_voie)+ 
                '&ids=&ids='+encodeURIComponent(code_insee)+
                '&ids='+encodeURIComponent(pays_id)+
                '&donnees=&donnees=&donnees=&donnees='+encodeURIComponent(ligne4)+
                '&donnees=&donnees='+
                '&options=distance=' + encodeURIComponent(distance)+
                '&options=projection=' + encodeURIComponent(projection)+
                '&options=date='+encodeURIComponent(dateValidation.getDate()+"/"+(dateValidation.getMonth()+1)+"/"+dateValidation.getFullYear());
                
            var xhr = getfile(url,params,fonction);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
            var end = new Date();
            afficheTimer('dureegeocodage',start, end);
        }
        
        // 
        // Permet de récupérer le résultat de la validation
        // 
        function revalide(action,fonction,services,id1,id4,id6,id7,ligne4,ligne6,ligne7,dateValidation,dateRevalidation)
        {
            var start = new Date();
            var date=new Date();
            var servicesParam = "";
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                }
            }
            var url = '<%=JADRREFJson%>/'+action;
            var params = 'application=2'+
                servicesParam +
                '&ids='+encodeURIComponent(id1)+
                '&ids=&ids='+
                '&ids='+((id4 != null) ? encodeURIComponent(id4) : '')+
                '&ids='+
                '&ids='+((id6 != null ) ? encodeURIComponent(id6) : '')+
                '&ids='+((id7 != null ) ? encodeURIComponent(id7) : '') +
                '&donnees=&donnees=&donnees='+
                '&donnees='+encodeURIComponent(ligne4)+
                '&donnees='+
                '&donnees='+encodeURIComponent(ligne6)+
                (((ligne7 != null) && (ligne7 != "")) ? '&donnees='+ encodeURIComponent(ligne7) : '') +
                '&date='+encodeURIComponent(dateValidation.getDate()+"/"+(dateValidation.getMonth()+1)+"/"+dateValidation.getFullYear())+
                '&options=date_revalidation='+encodeURIComponent(dateRevalidation.getDate()+"/"+(dateRevalidation.getMonth()+1)+"/"+dateRevalidation.getFullYear());
                
            var xhr = getfile(url,params,fonction);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
            var end = new Date();
            afficheTimer('dureerevalidation',start, end);
        }
        // 
        // Permet de récupérer le résultat de la validation
        // des 6, 7 lignes d'une adresse.
        //
        function valide(fonction, operation, services, ligne1, ligne2, ligne3, ligne4, ligne5, ligne6, ligne7, force)
        {
            startvalide = new Date(); // global car appelée dans fonction.
            var servicesParam = "";
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                }
            }
            var url = '<%=JADRREFJson%>/Valide';
            var params = 'application=2'+
                servicesParam +
                '&operation='+encodeURIComponent(operation)+
                '&donnees='+encodeURIComponent(ligne1)+
                '&donnees='+encodeURIComponent(ligne2)+
                '&donnees='+encodeURIComponent(ligne3)+
                '&donnees='+encodeURIComponent(ligne4)+
                '&donnees='+encodeURIComponent(ligne5)+
                '&donnees='+encodeURIComponent(ligne6)+
                '&donnees='+encodeURIComponent(ligne7)+
                '&options=fantoire=true'+
                '&options=indexes='+encodeURIComponent(document.getElementById("indexes").value);
            if ((operation&4)!=0) 
                params += '&options=dpt='+encodeURIComponent(departements);
            if (force) params += '&options=force=true';
            var xhr = getfile(url,params,fonction);
        
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
        }
        // 
        // Permet de récupérer le résultat d'une étape de normalisation
        // sur les 6 lignes d'une adresse.
        // 
        function normalise(fonction,operation,services,ligne1,ligne2,ligne3,ligne4,ligne5,ligne6, ligne7, pays)
        {       
                var getDpt = false;
                var desabreviation = false;
                var reduction38 = false;                
                if(operation == 36){
                    operation = 4;
                    desabreviation = true;
                }else if(operation == 44){
                    operation = 4;
                    desabreviation = true;
                    reduction38 = true;
                }else if(operation == 130){
                    operation = 2;
                    getDpt = true;
                }
                
                var servicesParam = "";
                for (var i = 0; i < services.options.length; i++){ 
                    if (services.options[i].selected){ 
                        servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                    }
                }
                
                var url = '<%=JADRREFJson%>/Normalise';
                var params = 'application=2'+
                             servicesParam +
                             '&operation='+encodeURIComponent(operation)+
                             '&donnees='+encodeURIComponent(ligne1)+
                             '&donnees='+encodeURIComponent(ligne2)+
                             '&donnees='+encodeURIComponent(ligne3)+
                             '&donnees='+encodeURIComponent(ligne4)+
                             '&donnees='+encodeURIComponent(ligne5)+
                             '&donnees='+encodeURIComponent(ligne6)+
                             '&donnees='+encodeURIComponent(ligne7);
                if(desabreviation){
                    params += '&options=desabreviation=true';
                }
                if(reduction38){
                    params += '&options=reduction38=true';
                }
                if(getDpt){
                    params += '&options=getDpt=true';
                }
                if(pays){
                    params += '&options=pays=true';
                }
                
                var xhr = getfile(url,params,fonction);
                
                if (xhr==null)
                    alert('L objet XMLHttpRequest n a pas été crée.');
        }
        // Permet de récupérer le résultat d'une étape de découpage
        // sur les 6 lignes d'adresse
        function decoupe(fonction,services,ligne1,ligne2,ligne3,ligne4,ligne5,ligne6, ligne7)
        {
            var servicesParam = "";
            var serviceAdr = false;
            var servicePoizon = false;
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                    if(services.options[i].value == '1'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '2'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '3'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '4'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '5'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '6'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '7'){
                        serviceAdr = true;
                    }else if(services.options[i].value == '100'){
                        servicePoizon = true;
                    }else if(services.options[i].value == '101'){
                        servicePoizon = true;
                    }else if(services.options[i].value == '102'){
                        servicePoizon = true;
                    }
                }
            }
            var operationsParam = "";
            if(serviceAdr){
                operationsParam += '&operations=1'+
                              '&operations=2'+
                              '&operations=8'+
                              '&operations=32'+
                              '&operations=64'+
                              '&operations=128';
              ((ligne7 == "")?'': operationsParam += '&operations=65536');
            }
            if(servicePoizon){
                operationsParam += '&operations=524288'+
                                  '&operations=1048576';
            }
            var url = '<%=JADRREFJson%>/Decoupe';
            var params = 'application=2'+
                         servicesParam +
                         operationsParam +
                         '&donnees='+encodeURIComponent(ligne1)+
                         '&donnees='+encodeURIComponent(ligne2)+
                         '&donnees='+encodeURIComponent(ligne3)+
                         '&donnees='+encodeURIComponent(ligne4)+
                         '&donnees='+encodeURIComponent(ligne5)+
                         '&donnees='+encodeURIComponent(ligne6);
                         ((ligne7 == "")?'':params +='&donnees='+encodeURIComponent(ligne7));
                          
            
            var xhr = getfile(url,params,fonction);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
        }
        //
        // Obtient la version de JDONREF
        //
        function getVersion(fonction)
        {
            var url = '<%=JADRREFJson%>/GetVersion?application=2&services=2&services=101';
            var xhr = getfile(url,null,fonction);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
        }
        //
        // Signale un problème aux administrateurs
        //
        function signale(fonction)
        {
            var url = '<%=JADRREFJson%>/Contacte';
            var params = 'application=2'+
                         '&title='+encodeURIComponent('Erreur signalée dans JDONREFv2')+
                         '&from='+encodeURIComponent('jdonrefv2test@ppol.mi')+
                         '&application='+encodeURIComponent('JDONREFv2Test')+
                         '&ligne1='+encodeURIComponent(lastligne1)+
                         '&ligne2='+encodeURIComponent(lastligne2)+
                         '&ligne3='+encodeURIComponent(lastligne3)+
                         '&ligne4='+encodeURIComponent(lastligne4)+
                         '&ligne5='+encodeURIComponent(lastligne5)+
                         '&ligne6='+encodeURIComponent(lastligne6)+
                         ((lastligne7 == "")?'':'&ligne7='+encodeURIComponent(lastligne7));
            var xhr = getfile(url,params,fonction);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
        }
        // 
        // Permet de récupérer le résultat de la validation des 6 lignes du formulaire
        // à la date actuelle.
        function valide_form_action(formulaire,fonction,operation)
        {
            var force;
            var check = document.getElementById("check");
            if (check.checked) force = true;
            else force = false;
            
            var pays = document.getElementById("checkPays");
            if(pays.checked){
                operation += 256; // PAYS
            }               
            valide(fonction,
                   operation,
                   formulaire.services,
                   formulaire.ligne1.value,
                   formulaire.ligne2.value,
                   formulaire.ligne3.value,
                   formulaire.ligne4.value,
                   formulaire.ligne5.value,
                   formulaire.ligne6.value,
                   formulaire.ligne7.value,
                   force
                  );
        }
        // 
        // Permet de récupérer le résultat d'une étape de normalisation
        // sur les 6 ou 7 lignes entrées dans un formulaire.
        // 
        function normalise_form_action(formulaire,fonction,operation,balise)
        {
            // Gestion du pays ?
            var gestPays = false;
            var rpcb = document.getElementById("checkPays");
            if (rpcb.checked) gestPays = true;

            var start = new Date();
            normalise(fonction,
                      operation,
                      formulaire.services,
                      formulaire.ligne1.value,
                      formulaire.ligne2.value,
                      formulaire.ligne3.value,
                      formulaire.ligne4.value,
                      formulaire.ligne5.value,
                      formulaire.ligne6.value,
                      formulaire.ligne7.value,
                      gestPays);
            var end = new Date();
            afficheTimer(balise,start,end);
        }
        // 
        // Permet de récupérer de lancer le découpage et d'afficher la durée d'exécution.
        function decoupe_form_action(formulaire,fonction,balise)
        {
            var start = new Date();

            decoupe(fonction,
                    formulaire.services,
                    formulaire.ligne1.value,
                    formulaire.ligne2.value,
                    formulaire.ligne3.value,
                    formulaire.ligne4.value,
                    formulaire.ligne5.value,
                    formulaire.ligne6.value,
                    formulaire.ligne7.value);
            var end = new Date();
            afficheTimer(balise,start,end);
        }        
    
        // Effectue la tâche de validation sur le formulaire
        function valide_form(formulaire)
        {
            var start = new Date();
            save(formulaire);
            clearAdds();
            valide_form_action(formulaire,traiteValide,0);
            var end = new Date();
            afficheTimer('dureetotal',start,end);
        }
        // Effectue la tâche première tâche de normalisation sur le formulaire
        function normalise1_form(formulaire)
        {
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,1,'dureenormalise1');
        }
        // Effectue la tâche deuxième tâche de normalisation sur le formulaire sans réduction à 38 caractères
        function normalise2_form(formulaire)
        {
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,36,'dureenormalise2');
        }
        // Effectue la tâche première tâche de normalisation sur le formulaire avec réduction à 38 caractères
        function normalise2_38_form(formulaire)
        {
            var start = new Date();
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,1,'dureenormalise1');
            normalise_form_action(formulaire,traiteRestructure,2,'dureerestructure');
            normalise_form_action(formulaire,traiteNormalise,44,'dureenormalise2_38');
            var end = new Date();
            afficheTimer('dureenormalise2_38total',start,end);
        }
        // Effectue la tâche de restructuration sur le formulaire
        function restructure_form(formulaire)
        {
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteRestructure,2+128,'dureerestructure');
        }
        // Effectue la tâche de restructuration sur le formulaire
        function sansarticles_form(formulaire)
        {
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,64,'dureesansarticles');
        }
        // Effectue la tâche de phonetisation sur le formulaire
        function phonetise_form(formulaire)
        {
            var start = new Date();
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,1,'dureenormalise1');
            normalise_form_action(formulaire,traiteNormalise,16,'dureephonetise');
            var end = new Date();
            afficheTimer('dureephonetisetotal',start,end);
        }
        // Effectue la tâche de découpage sur le formulaire
        function decoupe_form(formulaire)
        {
            var start = new Date();
            save(formulaire);
            clearAdds();
            normalise_form_action(formulaire,traiteNormalise,1,'dureenormalise1');
            normalise_form_action(formulaire,traiteRestructure,2+128,'dureerestructure');
            normalise_form_action(formulaire,traiteNormalise,4,'dureenormalise2');
            decoupe_form_action(formulaire,traiteDecoupe,'dureedecoupage');
            var end = new Date();
            afficheTimer('dureedecoupagetotal',start,end);
        }
        //
        // Effectue toutes les tâches de normalisation et de validation sur les lignes
        // du formulaire.
        //
        function allInOne_form(formulaire)
        {
            var start = new Date();
            save(formulaire);
            clearAdds();

            var restructPays;
            var rpcb = document.getElementById("checkPays");
            if (rpcb.checked) restructPays = true;
            else restructPays = false;

            normalise_form_action(formulaire,traiteNormalise,1,'dureenormalise1');
            normalise_form_action(formulaire,traiteRestructure,2+128,'dureerestructure');
            normalise_form_action(formulaire,traiteNormalise,36,'dureenormalise2');
            valide_form_action(formulaire,traiteValide,0);
            var end = new Date();
            afficheTimer('dureetotal',start,end);
        }
        //
        // Affiche le temps de calcul estimé.
        //
        function afficheTimer(balise,start,end)
        {
            var total = end.getMilliseconds()-start.getMilliseconds()+
                        1000*(end.getSeconds()-start.getSeconds())+
                        60000*(end.getMinutes()-start.getMinutes())+
                        3600000*(end.getHours()-start.getHours()); // je m'arrête à l'heure. Ne faîtes pas de relevés de temps de réponse aux alentours de minuit...
            var duree = document.getElementById(balise);
            duree.innerHTML = total+' ms';
        }
        //
        // Obtient la version de JDONREF v2
        //
        function getVersion_form(formulaire)
        {
            clear(formulaire);
            getVersion(traiteGetVersion);
        }
        //
        // Signale à l'administrateur une erreur de traitement. 
        //
        function signale_form()
        {
            signale(traiteSignale);
        }
        // sauvegarde le contenu des lignes du formulaire
        // dans des variables globales
        function save(formulaire)
        {
            lastligne1 = formulaire.ligne1.value;
            lastligne2 = formulaire.ligne2.value;
            lastligne3 = formulaire.ligne3.value;
            lastligne4 = formulaire.ligne4.value;
            lastligne5 = formulaire.ligne5.value;
            lastligne6 = formulaire.ligne6.value;
            lastligne7 = formulaire.ligne7.value;
        }
        //
        // efface le formulaire et ses compléments
        //
        function clear(formulaire)
        {
            formulaire.ligne1.value='';
            formulaire.ligne2.value='';
            formulaire.ligne3.value='';
            formulaire.ligne4.value='';
            formulaire.ligne5.value='';
            formulaire.ligne6.value='';
            formulaire.ligne7.value='';
            formulaire.supplements.value='';
            clearAdds();
        }
        // efface les compléments
        function clearAdds()
        {
            var signalements = document.getElementById("signalements");
            var propositions = document.getElementById("propositions");
            var fantoir = document.getElementById("fantoir");
            var misesajour = document.getElementById("misesajour");
            var geocodage = document.getElementById("geocodage");
            var normalisations = document.getElementById("normalisations");
            var dureenormalise1 = document.getElementById("dureenormalise1");
            var dureenrestructure = document.getElementById("dureerestructure");
            var dureenormalise2 = document.getElementById("dureenormalise2");
            var dureevalidation = document.getElementById("dureevalidation");
            var dureegeocodage = document.getElementById("dureegeocodage");
            var dureerevalidation = document.getElementById("dureerevalidation");
            var dureephonetise = document.getElementById("dureephonetise");
            var dureephonetisetotal = document.getElementById("dureephonetisetotal");
            var dureedecoupage = document.getElementById("dureedecoupage");
            var dureedecoupagetotal = document.getElementById("dureedecoupagetotal");
            var dureetotal = document.getElementById("dureetotal");
            dureenormalise1.innerHTML = '';
            dureenrestructure.innerHTML = '';
            dureenormalise2.innerHTML = '';
            dureevalidation.innerHTML = '';
            dureegeocodage.innerHTML = '';
            dureerevalidation.innerHTML = '';
            dureetotal.innerHTML = '';
            dureephonetise.innerHTML = '';
            dureephonetisetotal.innerHTML = '';
            dureedecoupage.innerHTML = '';
            dureedecoupagetotal.innerHTML = '';
            signalements.innerHTML = '';
            propositions.innerHTML = '';
            fantoir.innerHTML='';
            misesajour.innerHTML= '';
            geocodage.innerHTML='';
            normalisations.innerHTML='';
        }
        //
        // Remplit le formulaire avec un exemple.
        //
        function exemple(formulaire,numero)
        {
            clear(formulaire);
            if (numero==1)
            {
                formulaire.ligne2.value='24   bis boulevard du maréchal de lattre de tassigny';
                formulaire.ligne4.value='entrée A Appt 70';
                formulaire.ligne5.value='PARIS';
                formulaire.ligne6.value='75013';
            }
            else if (numero==2)
            {
                formulaire.ligne2.value='apt 70 ent B';
                formulaire.ligne3.value='de 24 à 26  fbg st jacques';
                formulaire.ligne4.value='75013 PARIS 13ème';
            }
            else if (numero==3)
            {
                formulaire.ligne2.value='appartement 70 entrée B';
                formulaire.ligne4.value='24-26 ter rue des 4 chênes';
                formulaire.ligne6.value='76039 LES AUTHIEUX SUR LE PART SAINT OUEN';
            }
            else if (numero==4)
            {
                formulaire.ligne2.value='24 bis rue de paris';
                formulaire.ligne3.value='Bobigny 93000';
            }
            else if (numero==5)
            {
                formulaire.ligne1.value = 'MS S POLLARD';
                formulaire.ligne2.value = '1 CHAPEL STREET';
                formulaire.ligne3.value = 'HESWALL';
                formulaire.ligne4.value = 'BOURNEMOUTH';
                formulaire.ligne6.value = 'BH1 1AA';
                formulaire.ligne7.value = 'ROYAUME UNI';
            }
            else if (numero==6)
            {
                formulaire.ligne4.value = '2 BOULEVARD DE L HOPITAL';
                formulaire.ligne6.value = '75005 PARIS';
            }
        }
        function comment(ligne)
        {
            commentaires = document.getElementById("commentaires");
            var texte;
            switch(ligne)
            {
                default:
                case 0: texte = ""; break;
                case 1: texte = "Ligne 1 : Civilité - Titre - Prénom - Nom"; break;
                case 2: texte = "Ligne 2 : Appt, Chez, Boite aux lettres, Etage, Couloir, Escalier, ..."; break;
                case 3: texte = "Ligne 3 : Entrée, Batiment, Immeuble, Résidence, ..."; break;
                case 4: texte = "Ligne 4 : Rue, Avenue, Hameau, ..."; break;
                case 5: texte = "Ligne 5 : Poste restante, BP, lieu-dit, ..."; break;
                case 6: texte = "Ligne 6 : Code Postal et localité."; break;
                case 7: texte = "Ligne 4 : Deuxième numéro de voie trouvé."; break;
                case 8: texte = "Decoupe : isole le numero, la répétition, le type de voie, le libelle,<br> le code postal, la ville."; break;
                case 9: texte = "Normalise_1 : Première passe de normalisation, traitement par caractères."; break;
                case 10: texte = "Restructure : Réordonne les éléments de l'adresse."; break;
                case 11: texte = "Normalise_2 : Deuxième passe de normalisation, traitement grammatical<br>(sans réduction à 38 caractères)."; break;
                case 12: texte = "Valide : Obtiens une liste d'adresse pouvant correspondre à celle proposée."; break;
                case 13: texte = "Tout d'un bloc : Normalise_1+Restructure+Normalise_2+Valide"; break;
                case 14: texte = "Signaler un problème : envoi un mail à l'administrateur avec l'adresse<br> qui pose problème."; break;
                case 15: texte = "Normalise_2+38 : Deuxième passe de normalisation avec réduction à 38 caractères.<br>La première passe et la restructuration sont effectuées au préalable."; break;
                case 16: texte = "Phonétise : Retourne l'équivalent phonétique de chaque ligne"; break;
                case 18: texte = "getVersion : Obtient la version de JDONREF v3"; break;
                case 19: texte = "sans articles: Supprime les articles trouvés dans l'adresse"; break;
                case 20: texte = "Ligne 7 : Pays"; break;
                case 21: texte = "Code de la projection"; break;
                case 22: texte = "Choix du service concerné"; break;
                case 23: texte = "Valide POIZON : Obtiens une liste bouchon de POI/ZONE."; break;
                case 24: texte = "Opération"; break;
                case 25: texte = "Distance du décalage gauche/droite lors du géocodage"; break;
                case 26: texte = "ES_Indexes : index1,index2..."; break;
            }
                    
            commentaires.innerHTML = texte+"<br>";
        }
        // 
        // Permet de récupérer le résultat du géocodage POI/ZONE
        // 
        function changePoizon(index)
        {
            // Commence par changer de couleur la ligne sélectionnée.
            var table = document.getElementById("table");
            table.tBodies[0].rows[currentindex].cells[0].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[1].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[2].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[3].style.color = "black";
            table.tBodies[0].rows[index].cells[0].style.color = "blue";
            table.tBodies[0].rows[index].cells[1].style.color = "blue";
            table.tBodies[0].rows[index].cells[2].style.color = "blue";
            table.tBodies[0].rows[index].cells[3].style.color = "blue";
            currentindex = index;
            
            var projection = document.getElementById("inp_projection").value;
            var start = new Date();
            var date=new Date();
            var services = document.getElementById("formulaire").services;
            var servicesParam = "";
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                }
            }            
            var url = '<%=JADRREFJson%>/Geocode';
            var params = 'application=2'+
                servicesParam +
                '&ids='+encodeURIComponent(data.propositions[index].ids[0])+
                '&donnees='+encodeURIComponent(data.propositions[index].donnees[0])+
                '&options=date='+encodeURIComponent(date.getDate()+ "/" + 
                    (date.getMonth()+1)+ "/" + date.getFullYear()) +
                '&options=projection=' + encodeURIComponent(projection);
                
            var xhr = getfile(url,params,traiteGeocodage);
            
            if (xhr==null)
                alert('L objet XMLHttpRequest n a pas été crée.');
            var end = new Date();
            afficheTimer('dureegeocodage',start, end);
        }
        
        function revalidation(index){
            // Commence par changer de couleur la ligne sélectionnée.
            var table = document.getElementById("table");
            table.tBodies[0].rows[currentindex].cells[0].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[1].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[2].style.color = "black";
            table.tBodies[0].rows[currentindex].cells[3].style.color = "black";
            table.tBodies[0].rows[index].cells[0].style.color = "blue";
            table.tBodies[0].rows[index].cells[1].style.color = "blue";
            table.tBodies[0].rows[index].cells[2].style.color = "blue";
            table.tBodies[0].rows[index].cells[3].style.color = "blue";
            currentindex = index;
            
            var start = new Date();
            var date=new Date();
            var services = document.getElementById("formulaire").services;
            var servicesParam = "";
            for (var i = 0; i < services.options.length; i++){ 
                if (services.options[i].selected){ 
                    servicesParam += '&services='+encodeURIComponent(services.options[i].value);
                }
            }
             var tValidation = parseDate(data.propositions[index].t0); 
            // Effectue la revalidation si nécessaire.
            // l'objet data est toujours en mémoire (cf valide)
            // Si l'élément choisi dans la liste n'a pas une date récente de validité,
            var tRevalidation = new Date();
                        
            //formulaire,action,fonction,ligne4,ligne6,dateValidation,dateRevalidation
            revalide("Revalide",traiteRevalide,services,
                data.propositions[index].ids[0],
                data.propositions[index].ids[3],
                data.propositions[index].ids[5],
                "",
                data.propositions[index].donnees[3],
                data.propositions[index].donnees[5],
                "",
                tValidation,tRevalidation);
        }
        
    </script>
    
    <body onload="init()">
        <h2>Page de test de JDONREF v3</h2>
        <form name="Normalise" id="formulaire">
        <table>
        <tr>
        <td>
            <table>
                <tr><td colspan="2">
                    <input type="button" value="Exemple 1" onclick="exemple(this.form,1);"/> 
                    <input type="button" value="Exemple 2" onclick="exemple(this.form,2);"/> 
                    <input type="button" value="Exemple 3" onclick="exemple(this.form,3);"/> 
                    <input type="button" value="Exemple 4" onclick="exemple(this.form,4);"/>
                    <input type="button" value="Exemple 5" onclick="exemple(this.form,5);"/>
                    <input type="button" value="Exemple 6" onclick="exemple(this.form,6);"/>
                    <input type="button" value="Clear" onclick="exemple(this.form,0);"/>
                </td></tr>
                <tr><td>Ligne 1</td><td><input type="text" name="ligne1" size="50" onmouseover="comment(1);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Ligne 2</td><td> <input type="text" name="ligne2" size="50" onmouseover="comment(2);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Ligne 3</td><td> <input type="text" name="ligne3" size="50" onmouseover="comment(3);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Ligne 4</td><td> <input type="text" name="ligne4" size="50" onmouseover="comment(4);" onmouseout="comment(0);"/> Suppléments: <input type="text" name="supplements" size="10" onmouseover="comment(7);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Ligne 5</td><td> <input type="text" name="ligne5" size="50" onmouseover="comment(5);" onmouseout="comment(0);"/></td></tr>
                <tr><td> Ligne 6</td><td> <input type="text" name="ligne6" size="50" onmouseover="comment(6);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Ligne 7</td><td> <input type="text" name="ligne7" size="50" onmouseover="comment(20);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Projection</td><td><input type="text" id="inp_projection" size="50" value="2154" onmouseover="comment(21);" onmouseout="comment(0);"/></td></tr>
                <tr><td>Distance</td><td><input type="text" id="inp_distance" size="50" value="0" onmouseover="comment(25);" onmouseout="comment(0);"/></td></tr>
                <tr><td style="vertical-align:top;">Services</td>
                    <td>
                        <select name="services" multiple="true" size="9" onmouseover="comment(22);" onmouseout="comment(0);">
                            <option value="1" selected>Adresse</option>
                            <option value="2">point adresse</option>
                            <option value="3">troncon</option>
                            <option value="4">voie</option>
                            <option value="5">commune</option>
                            <option value="6">departement</option>
                            <option value="7">pays</option>
                            <option value="10001">ES_Adresse</option>
                            <option value="10002">ES_point adresse</option>
                            <option value="10003">ES_troncon</option>
                            <option value="10004">ES_voie</option>
                            <option value="10005">ES_commune</option>
                            <option value="10006">ES_departement</option>
                            <option value="10007">ES_pays</option>
                            <option value="10008">ES_POIZON</option>
                            <option value="100">POIZON</option>
                            <option value="101">POI</option>
                            <option value="102">ZON</option>
                        </select>
                    </td>
                </tr>
                <tr><td>ES_Indexes</td><td><input id="indexes" type="text" name="indexes" value="jdonref" size="50" onmouseover="comment(26);" onmouseout="comment(0);"/></td></tr>
                </table>
            <br /><br/>
            <div id="commentaires" style="color:red"><br></div>
         </td>
         <td>
            
            <br>
            <table>
               <tr>
                   <td><input type="button" value="Normalise_1" onmouseover="comment(9);" onmouseout="comment(0);" onclick="normalise1_form(this.form);"/></td>
                   <td><div id="dureenormalise1" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Restructure" onmouseover="comment(10);" onmouseout="comment(0);" onclick="restructure_form(this.form);"/></td>
                   <td><div id="dureerestructure" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Normalise_2" onmouseover="comment(11);" onmouseout="comment(0);" onclick="normalise2_form(this.form);"/></td>
                   <td><div id="dureenormalise2" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Valide" onmouseover="comment(12);" onmouseout="comment(0);" onclick="valide_form(this.form);"/></td>
                   <td><div id="dureevalidation" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input id="check" type="checkbox" value="unchecked">Forcer la validation orthographique</td>
               </tr>
               <tr>
                   <td><input id="checkPays" type="checkbox" value="unchecked">Gérer les pays.</td>
               </tr>
               <tr>
                   <td><input type="button" value="Tout d'un bloc" onmouseover="comment(13);" onmouseout="comment(0);" onclick="allInOne_form(this.form);"/></td>
                   <td><table><tr><td>Geocodage</td><td><div id="dureegeocodage" style="color:red"></div></td></tr>
                              <tr><td>Revalidation</td><td><div id="dureerevalidation" style="color:red"></div></td></tr>
                              <tr><td>Total</td><td><div id="dureetotal" style="color:red"></div></td></tr>
                       </table>
                   </td>
               </tr>
               <tr>
                   <td><input type="button" value="Decoupe" onmouseover="comment(8);" onmouseout="comment(0);" onclick="decoupe_form(this.form);"/></td>
                   <td><div id="dureedecoupage" style="color:red"></div></td>
                   <td>Total:</td>
                   <td><div id="dureedecoupagetotal" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Sans articles" onmouseover="comment(19);" onmouseout="comment(0);" onclick="sansarticles_form(this.form);"/></td>
                   <td><div id="dureesansarticles" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Phonétise" onmouseover="comment(16);" onmouseout="comment(0);" onclick="phonetise_form(this.form);"/></td>
                   <td><div id="dureephonetise" style="color:red"></div></td>
                   <td>Total:</td>
                   <td><div id="dureephonetisetotal" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Normalise_2+38" onmouseover="comment(15);" onmouseout="comment(0);" onclick="normalise2_38_form(this.form);"/></td>
                   <td><div id="dureenormalise2_38" style="color:red"></div></td>
                   <td>Total:</td>
                   <td><div id="dureenormalise2_38total" style="color:red"></div></td>
               </tr>
               <tr>
                   <td><input type="button" value="Version de JDONREF v3" onmouseover="comment(18);" onmouseout="comment(0);" onclick="getVersion_form(this.form);"/></td>
               </tr>
               <tr>
                   <td><input type="button" value="Signaler un problème" onmouseover="comment(14);" onmouseout="comment(0);" onclick="signale_form();"/></td>
               </tr>
            </table>
         </td>
        </tr>
       </table>
       </form>
       <div id="signalements"></div>
       <div id="normalisations"></div>
       <div id="fantoir"></div>
       <div id="misesajour"></div>
       <div id="geocodage" style="color:blue"></div>
       <div id="propositions"></div>
    </body>
</html>