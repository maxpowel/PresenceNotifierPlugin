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

package com.wixet.openfire.plugin;

import java.io.File;
import java.util.Map;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

import org.jivesoftware.openfire.user.PresenceEventDispatcher;
import org.jivesoftware.openfire.user.PresenceEventListener;

import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;

import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.auth.UnauthorizedException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresenceNotifierPlugin implements Plugin, PropertyEventListener, PresenceEventListener {

	private static final Logger Log = LoggerFactory.getLogger(PresenceNotifierPlugin.class);
	
	private static String DESTINATION_URL = "plugin.presencenotifier.destinationUrl";
	private static String ENABLED = "plugin.presencenotifier.enabled";
	private static String ACCESS_TOKEN = "plugin.presencenotifier.accessToken";
	
    private boolean enabled;
    private String destinationUrl;
    private String accessToken;

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
		accessToken = JiveGlobals.getProperty(ACCESS_TOKEN, "");
		
        destinationUrl = JiveGlobals.getProperty(DESTINATION_URL, "");
        
        // See if the service is enabled or not.
        enabled = JiveGlobals.getBooleanProperty(ENABLED, false);

        // Listen to presence events
        PresenceEventDispatcher.addListener(this);
        
        // Listen to system property events
        PropertyEventDispatcher.addListener(this);
    }

    public void destroyPlugin() {
        // Stop listening to presence events
        PresenceEventDispatcher.removeListener(this);
        // Stop listening to system property events
        PropertyEventDispatcher.removeListener(this);
    }
    
    private void notifyPresence(String username, boolean online) throws UnauthorizedException{
		if(enabled){
			Log.debug("PresenceNotifier: User " + username + " online status: " + online);
			String urlStr = destinationUrl+ "?token="+ accessToken +"&user=" + username + "&online="+(online?"true":"false");
			System.out.println(urlStr);
			try{
				
				URL url = new URL(urlStr);
				URLConnection conn = url.openConnection();
				conn.getInputStream().close();
				System.out.println(urlStr);
				
			
			}catch(Exception e){
			    Log.debug("PresenceNotifier: Error while notifying to the server");
			    Log.debug("PresenceNotifier: "+ e.getMessage());
				e.printStackTrace();
			}
		}     
	}
    
    ////////////////////////
    //Presence methods /////
    ////////////////////////
    
    public void availableSession(ClientSession session, Presence presence) {
        try{
			notifyPresence(session.getUsername(), true);
		} catch(Exception e){
			// Do nothing
		}
    }
    
    public void unavailableSession(ClientSession session, Presence presence) {
        try{
			notifyPresence(session.getUsername(), false);
		} catch(Exception e){
			// Do nothing
		}
    }
    
    public void presenceChanged(ClientSession session, Presence presence) {
        // Do nothing

    }
    
    public void subscribedToPresence(JID subscriberJID, JID authorizerJID) {
		// Do nothing
    }
    
    
    public void unsubscribedToPresence(JID unsubscriberJID, JID recipientJID) {
        // Do nothing
    }
    
    
    /////////////////////////
    // Getters and Setters //
    /////////////////////////
    
    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String url) {
        JiveGlobals.setProperty(DESTINATION_URL, url);
        this.destinationUrl = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        JiveGlobals.setProperty(ENABLED,  enabled ? "true" : "false");
    }

	public String getAccesToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        JiveGlobals.setProperty(ACCESS_TOKEN, token);
        this.accessToken = token;
    }
    
    public void propertySet(String property, Map<String, Object> params) {
        if (property.equals(DESTINATION_URL)) {
            this.destinationUrl = (String)params.get("value");
        }
        else if (property.equals(ENABLED)) {
            this.enabled = Boolean.parseBoolean((String)params.get("value"));
        }else if (property.equals(ACCESS_TOKEN)) {
            this.accessToken = (String)params.get("value");
        }
    }

    public void propertyDeleted(String property, Map<String, Object> params) {
        if (property.equals(DESTINATION_URL)) {
            this.destinationUrl = "";
        }
        else if (property.equals(ENABLED)) {
            this.enabled = false;
        }else if (property.equals(ACCESS_TOKEN)) {
            this.accessToken = "";
        }
    }

    public void xmlPropertySet(String property, Map<String, Object> params) {
        // Do nothing
    }

    public void xmlPropertyDeleted(String property, Map<String, Object> params) {
        // Do nothing
    }
}
