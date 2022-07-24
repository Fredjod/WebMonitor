package toolbox;



import java.net.http.HttpResponse;
import java.util.HashMap;

import webmonitor.AbstractWebMonitor;


public class Texto extends AbstractWebMonitor {
	
	   private final int sendText (String user_id, String password, String msg) throws Exception {


	    	HashMap<String, String> params = new HashMap<String, String>();
	    	
	    	params.put("user", user_id);
	    	params.put("pass", password);
	    	params.put("msg", msg);
	    	
	    	HttpResponse<String> response = httpGetRequest(encodeURIWithParams ("https://smsapi.free-mobile.fr/sendmsg", params));
	        
	        log.trace("SMS sending response status: "+response.statusCode()); 
			/*
				200 : Le SMS a été envoyé sur votre mobile.
				400 : Un des paramètres obligatoires est manquant.
				402 : Trop de SMS ont été envoyés en trop peu de temps.
				403 : Le service n'est pas activé sur l'espace abonné, ou login / clé incorrect.
				500 : Erreur côté serveur. Veuillez réessayer ultérieurement.
			 */
	        
	        return response.statusCode();
	        
	   }
	   
	   public int send (String user_id, String password, String msg) throws Exception {
		   return sendText (user_id, password, msg);
	   }
	   
	   @Override
	   public void run() {
	   }
}
