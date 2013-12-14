<%
/*  
 *   PresenceNotifierPlugin.java
 * 
 *   Copyright Álvaro García <maxpowel@gmail.com>
 *   This file is part of PresenceNotifierPlugin.
 *
 *   PresenceNotifierPlugin is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   PresenceNotifierPlugin is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */    
%>


<%@ page import="java.util.*,
                 org.jivesoftware.openfire.XMPPServer,
                 org.jivesoftware.util.*,
                 com.wixet.openfire.plugin.PresenceNotifierPlugin"
    errorPage="error.jsp"
%>

<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

<%-- Define Administration Bean --%>
<jsp:useBean id="admin" class="org.jivesoftware.util.WebManager"  />
<c:set var="admin" value="${admin.manager}" />
<% admin.init(request, response, session, application, out ); %>

<%  // Get parameters
    boolean save = request.getParameter("save") != null;
    boolean success = request.getParameter("success") != null;
    String accessToken = ParamUtils.getParameter(request, "accessToken");
    String destinationUrl = ParamUtils.getParameter(request, "destinationUrl");
    boolean enabled = ParamUtils.getBooleanParameter(request, "enabled");

    PresenceNotifierPlugin plugin = (PresenceNotifierPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("presencenotifier");

    // Handle a save
    Map errors = new HashMap();
    if (save) {
        if (errors.size() == 0) {
            plugin.setEnabled(enabled);
        	plugin.setAccessToken(accessToken);
        	plugin.setDestinationUrl(destinationUrl);
            response.sendRedirect("presence-notifier.jsp?success=true");
            return;
        }
    }

    enabled = plugin.isEnabled();
    accessToken = plugin.getAccesToken();
    destinationUrl = plugin.getDestinationUrl();
%>

<html>
    <head>
        <title>Presence notifier Properties</title>
        <meta name="pageID" content="presence-notifier"/>
    </head>
    <body>


<p>
Use the form below to enable or disable the Presence Notifier and configure the destination url and access token.
</p>

<%  if (success) { %>

    <div class="jive-success">
    <table cellpadding="0" cellspacing="0" border="0">
    <tbody>
        <tr><td class="jive-icon"><img src="images/success-16x16.gif" width="16" height="16" border="0"></td>
        <td class="jive-icon-label">
            Presence notifier properties edited successfully.
        </td></tr>
    </tbody>
    </table>
    </div><br>
<% } %>

<form action="presence-notifier.jsp?save" method="post">

<fieldset>
    <legend>Presence Notifier</legend>
    <div>

    <ul>
        <input type="radio" name="enabled" value="true" id="rb01"
        <%= ((enabled) ? "checked" : "") %>>
        <label for="rb01"><b>Enabled</b> - Presence notifications will be sent.</label>
        <br>
        <input type="radio" name="enabled" value="false" id="rb02"
         <%= ((!enabled) ? "checked" : "") %>>
        <label for="rb02"><b>Disabled</b> - Presence notifications will be ignored.</label>
        <br><br>

        <label for="text_secret">Access Token:</label>
        <input type="text" name="accessToken" value="<%= accessToken %>" id="text_secret">
        <br><br>

        <label for="text_secret">Destination Url:</label>
        <input type="text" name="destinationUrl" value="<%= destinationUrl %>" id="text_secret">
        <br><br>
    </ul>
    </div>
</fieldset>

<br><br>

<input type="submit" value="Save Settings">
</form>


</body>
</html>
