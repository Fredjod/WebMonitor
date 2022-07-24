package webmonitor;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import toolbox.Param;
import toolbox.Texto;

public class VictorFreeMobilePlanMonitor extends AbstractWebMonitor {
	
	private void textoAlert (String msg) throws Exception {
		Texto texto = new Texto ();
    	texto.send(
    		Param.getProperty("FreeMobile.victor.userid"), 
    		Param.getProperty("FreeSMSService.victor.password"), 
    		this.getClass().getName()+": "+msg
    	);
    	texto.send(
        		Param.getProperty("FreeMobile.fred.userid"), 
        		Param.getProperty("FreeSMSService.fred.password"), 
        		this.getClass().getName()+": "+msg
        	);		
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
            
            // Read DATA conso info
            HashMap<String, Double> conso = new HashMap<String, Double>();
	    	conso = parseKeyStringValueDoubleHastable (response.body(), "^\\s*Hors forfait (\\w+) : <span class=\"info\">(.*)€</span>");

	        // Alert if any DATA service out of plan
	    	Iterator<Entry<String, Double>> it = conso.entrySet().iterator();
	    	String msg = null;
	    	
		    while (it.hasNext()) {
		        Map.Entry<String, Double> entry = (Map.Entry<String, Double>)it.next();
		        if (entry.getValue() > 0) {
		        	msg += "/!\\ Hors Fofait: " + entry.getKey() + " /!\\: " + entry.getValue() + "€.\n";
		        }
		    }
		    if (msg != null) {
		    	log.warn(msg);
		    	textoAlert(msg);
		    }
		    else {
		    	log.trace("No out of plan detected");
		    }
		        
		    // Check if the DATA option needs to be changed, update it and send an alert
            response = httpGetRequest ("https://mobile.free.fr/account/mes-options");
            String regexDataActivation = "^\\s*<a href=\"/account/mes-options\\?update=data&activate=(\\d+)\"";
            int activated = parseValueInteger (response.body(), regexDataActivation);
            
            log.trace("DATA option is "+((activated == 1)?"desactivated":"activated"));

			if (activated == 0 && conso.get("DATA") > 0) {
				response = httpGetRequest ("https://mobile.free.fr/account/mes-options?update=data&activate=0");
	            activated = parseValueInteger (response.body(),regexDataActivation);
	            if (activated == 1) {
	            	msg = "DATA service a été désactivé.";
	            	log.warn(msg);
			    	textoAlert(msg);
	            }
	            else {
	            	throw new Exception ("DATA service switch off failure");
	            }
			}
			
			if (activated == 1 && conso.get("DATA") == 0) {
				response = httpGetRequest ("https://mobile.free.fr/account/mes-options?update=data&activate=1");
				activated = parseValueInteger (response.body(),regexDataActivation);
	            if (activated == 0) {
	            	msg = "DATA service a été réactivé.";
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
