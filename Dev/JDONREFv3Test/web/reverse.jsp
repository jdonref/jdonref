<%-- 
    Document   : reverse
    Created on : 22 mai 2012, 14:26:13
    Author     : usersdsic
--%>

<%!
    String JADRREFJson = null;

    public void jspInit() {
        JADRREFJson = getServletConfig().getServletContext().getInitParameter("JDONREFv3JSON");
    }


%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JDONREF&nbsp;v3</title>
        <script src="reverse.js" type="text/javascript"></script>
    </head>
    <body>
        <h2>Page&nbsp;de&nbsp;test&nbsp;du&nbsp;g&eacute;ocodage&nbsp;inverse&nbsp;de&nbsp;JDONREF&nbsp;v3</h2>
        <div id="adresse">
            <%= request.getParameter("adr")%>
        </div>
        <br />
        <form name="reverse" id="formulaire">
            <fieldset>
                <legend>G&eacute;ocodage&nbsp;inverse</legend>
                <table>
                    <tr>
                        <td><label id=lab_x for="inp_x">x&nbsp;:&nbsp;</label></td>
                        <td><input type="text" id="inp_x" size="50" value='<%= request.getParameter("x")%>'/></td>
                    </tr>
                    <tr>
                        <td><label id=lab_y for="inp_y">y&nbsp;:&nbsp;</label></td>
                        <td><input type="text" id="inp_y" size="50" value='<%= request.getParameter("y")%>'/></td>
                    </tr>
                    <tr>
                        <td><label id=lab_distance for="inp_distance">distance&nbsp;:&nbsp;</label></td>
                        <td><input type="text" id="inp_distance" size="50" value="1"/></td>
                    </tr>
                    <tr>
                        <td><label id=lab_service for="sel_service">services&nbsp;:&nbsp;</label></td>
                        <td>
                            <select id="sel_service" multiple="true">
                                <option value="1" selected>Adresse</option>
                                <option value="2">point adresse</option>
                                <option value="3">troncon</option>
                                <option value="4">voie</option>
                                <option value="5">commune</option>
                                <option value="6">departement</option>
                                <option value="7">pays</option>
                                <option value="100">POIZON</option>
                                <option value="101">POI</option>
                                <option value="102">ZON</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <td><label id=lab_projection for="inp_projection">projection&nbsp;:&nbsp;</label></td>
                        <td><input type="text" id="inp_projection" size="50" value='<%= request.getParameter("projection")%>'/></td>
                    </tr>
                    <tr>
                        <td><input type="button" value="Reverse" onclick="callReverse('<%=JADRREFJson%>/Reverse');"/></td>
                        <td><div id="duree_inverse" style="color:red"></div></td>
                    </tr>
                </table>
            </fieldset>
        </form>
        <div id="propositions"></div>
    </body>
</html>
