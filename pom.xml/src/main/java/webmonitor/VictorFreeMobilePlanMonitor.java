package webmonitor;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import toolbox.Param;

public class VictorFreeMobilePlanMonitor extends AbstractWebMonitor {
	
	
	public HashMap<String, String> extractConso (String body) throws Exception {
		
	    // Read conso info
	    HashMap<String, String> conso;
		conso = parseKeyStringValueStringHastable (body, "^\\s*Hors forfait (\\w+) : <span class=\"info\">(.*)</span>");
	    return conso;
		
	}	
	
	public HashMap<String, Boolean> convertConsoToOutOfPlan (HashMap<String, String> conso) throws Exception {
		
	    HashMap<String, Boolean> outOfPlan = new HashMap<String, Boolean>();
		Iterator<Entry<String, String>> it = conso.entrySet().iterator();
		
		// Convert conso to outOfPlan
	    while (it.hasNext()) {
	        Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
	        outOfPlan.put(entry.getKey(), ! entry.getValue().contains("0.00€0.00€"));
	    }
	    return outOfPlan;
		
	}
	
	@Override
	public void run() {
		try {
	        
	    	// Call home page
			httpGetRequest("https://mobile.free.fr/account/");
	        
	        // Authentication step
    		String user_id = Param.getProperty("FreeMobile.victor.userid");
    		String password = Param.getProperty("FreeMobile.victor.password");
    		if (user_id == null || password == null) {
    			throw new Exception ("user_id/passord are null");
    		}
    		if (user_id.length() != 8 || password.length()<6) {
    			throw new Exception ("user_id/passord lenght are invalid");
    		}
    		log.trace("Call authentication request with userID: "+user_id);
    		HttpResponse<String> response = httpPostRequest("https://mobile.free.fr/account/", "login-ident="+user_id+"&login-pwd="+password+"&bt-login=1");
            loginFailureCheck(response.body(), "utilisateur ou mot de passe incorrect");
            log.trace("Authentication success");
            
            HashMap<String, String> conso = extractConso(response.body());
            HashMap<String, Boolean> outOfPlan = convertConsoToOutOfPlan(conso);
            
            // Alert if any out of plan
            Iterator<Entry<String, Boolean>> it = outOfPlan.entrySet().iterator();
            String msg = "";
    	    while (it.hasNext()) {
    	        Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>)it.next();
		        if (entry.getValue()) {
		        	msg += "/!\\ Hors Fofait: " + entry.getKey() + " /!\\: " + conso.get(entry.getKey());
		        }
    	    }
		    if (! msg.isEmpty()) {
		    	log.warn(msg);
		    }
    	    if ( msg.isEmpty()) {
		    	log.trace("No out of plan detected");
		    }
		        
		    // Check if the DATA option needs to be changed, update it and send an alert
            response = httpGetRequest ("https://mobile.free.fr/account/mes-options");
            String regexDataActivation = "^\\s*<a href=\"/account/mes-options\\?update=data&activate=(\\d+)\"";
            int activated = parseValueInteger (response.body(), regexDataActivation);
            
            log.trace("DATA option is "+((activated == 1)?"desactivated":"activated"));

			if (activated == 0 && outOfPlan.get("DATA")) {
				response = httpGetRequest ("https://mobile.free.fr/account/mes-options?update=data&activate=0");
	            activated = parseValueInteger (response.body(),regexDataActivation);
	            if (activated == 1) {
	            	msg = "/!\\ Hors Fofait DATA:" + conso.get("DATA")+" - DATA a été désactivé.";
	            	log.warn(msg);
			    	textoAlert(msg);
	            }
	            else {
	            	throw new Exception ("DATA service switch off failure");
	            }
			}
			
			if (activated == 1 && ! outOfPlan.get("DATA")) {
				response = httpGetRequest ("https://mobile.free.fr/account/mes-options?update=data&activate=1");
				activated = parseValueInteger (response.body(),regexDataActivation);
	            if (activated == 0) {
	            	msg =  "DATA conso:" + conso.get("DATA")+"DATA service a été réactivé.";
	            	log.warn(msg);
			    	textoAlert(msg);
	            }
	            else {
	            	throw new Exception ("DATA service switch on failure");
	            }
			}
	    }
        catch(Exception e) {
        	  log.error("Error :", e);
        }
        finally {
        	// Finally, login off
        	try {
        		httpGetRequest ("https://mobile.free.fr/account/?logout=user");
			} catch (Exception e) {
				log.error("logoff request failed :", e);
			}        	
        }
	}
}
