
//
// Obtient le résultat d'une requête et la traite par la fonction spécifiée
//
function getfile(url,params){
    var xhr_object = null;
    if(window.XMLHttpRequest) // FIREFOX
        xhr_object = new XMLHttpRequest(); 
    else if(window.ActiveXObject) // IE
        xhr_object = new ActiveXObject("Microsoft.XMLHTTP"); 
    else 
        return(null);
    
    if (params == null)
        xhr_object.open("GET", url, false);
    else
        xhr_object.open("GET", url + "?" + params, false);
    
    //xhr_object.onreadystatechange = fonction;
    xhr_object.setRequestHeader("Content-Type", "text/text; charset=utf-8");
    xhr_object.send(null);
    
    return xhr_object;
};
// 
// Appel de la méthode reverse.
//
function callReverse(url){
    var x = document.getElementById('inp_x').value;
    var y = document.getElementById('inp_y').value;
    var distance = document.getElementById('inp_distance').value;
    var services = '';
    var projection = document.getElementById('inp_projection').value;
    var sel_service = document.getElementById('sel_service');
    for (var i = 0; i < sel_service.options.length; i++){ 
        if (sel_service.options[i].selected){ 
            services += '&services='+sel_service.options[i].value;
        }
    }
    
    var date = new Date(); 
    var params = 'application=2'+ services + 
    '&donnees='+encodeURIComponent(x)+
    '&donnees='+encodeURIComponent(y)+
    '&distance='+encodeURIComponent(distance)+
    '&options=projection='+encodeURIComponent(projection);
    var xhr = getfile(url,params);
    
    if (xhr==null){
        alert('L objet XMLHttpRequest n a pas été crée.');
    }else{
        traiteReverse(xhr, date);
    }
};
// Permet de traiter le résultat du geocodage inverse.
// 
function traiteReverse(xhr_object, start){
    var end = new Date();
    afficheTimer("duree_inverse",start,end);
    if (xhr_object.readyState==4){  
        var propositions = document.getElementById("propositions");
        if (xhr_object.status==200){
            var res = xhr_object.responseText;
            if (res==null || res==""){
                propositions.innerHTML = "<br /><br />Aucun résultat n'a été retourné par le web service.";
            }else{
                var data = eval("("+res+")"); 
                if (data.codeRetour==0){
                    propositions.innerHTML = "Erreur " + data.erreurs[0].code + " : " + data.erreurs[0].message;
                }else{
                    var balise = "<br /><b>&nbsp;";
                    if(data.propositions == null || data.propositions.length == 0){
                        balise +="Aucun&nbsp;résultat.";
                    }else if(data.propositions.length == 1){
                        balise +="Un&nbsp;résultat&nbsp;:&nbsp;";
                    }else{
                        balise += data.propositions.length + "&nbsp;";
                        balise +="résultats&nbsp;:&nbsp;";
                    }
                    if(data.propositions != null && data.propositions.length >= 1){
                        balise +="</b><br /><br />";
                        balise +="<table>";
                        balise +="<tr>";
                        balise +="<th>";
                        balise +="nom";
                        balise +="</th>";
                        balise +="<th>";
                        balise +="adresse";
                        balise +="</th>";
                        balise +="<th>";
                        balise +="pays";
                        balise +="</th>";
                        balise +="<th>";
                        balise +="position";
                        balise +="</th>";
                        balise +="<th></th>";
                        balise +="<th>";
                        balise +="distance";
                        balise +="</th>";
                        balise +="</tr>";
                        for(var i=0;i<data.propositions.length;i++){
                            balise +="<tr>";
                            balise +="<td>";
                            balise += data.propositions[i].donnees[0];
                            balise +="</td>";
                            balise +="<td>";
                            balise += data.propositions[i].donnees[3] + " " + data.propositions[i].donnees[5];
                            balise +="</td>";
                            balise +="<td>";
                            balise += data.propositions[i].donnees[6];
                            balise +="</td>";
                            balise +="<td>";
                            balise += data.propositions[i].x + " " + data.propositions[i].y;
                            balise +="</td>";
                            balise +="<td>&nbsp;&nbsp;</td>";
                            balise +="<td>";
                            balise += data.propositions[i].distance;
                            balise +="</td>";
                            balise +="</tr>";
                        }
                        balise +="</table>";
                    }
                    propositions.innerHTML = balise;
                }
            }
        }
    }
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