package freemobile;


import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import tools.Config;

import org.apache.logging.log4j.LogManager;


// https://golb.hplar.ch/2019/01/java-11-http-client.html
// https://zetcode.com/java/httpclient/


public class MobilePlanMngt {

	private static final Logger log = LogManager.getLogger(MobilePlanMngt.class);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .cookieHandler(new CookieManager())
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    
    public static final String encodeURIWithParams (String uri, HashMap<String, String> params) throws Exception {
    	Iterator<Entry<String, String>> it = params.entrySet().iterator();
    	String uriWithParams = uri+"?";
	    while (it.hasNext()) {
	        Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
	        uriWithParams += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
	        if (it.hasNext()) { uriWithParams += "&"; }
	    }
    	return uriWithParams;

    }
    
    public final static int sendText (String msg) throws Exception {


    	HashMap<String, String> params = new HashMap<String, String>();
    	
		String user_id = Config.getProperty("FreeMobile.userid");
		String password = Config.getProperty("FreeSMSService.password");
    	params.put("user", user_id);
    	params.put("pass", password);
    	params.put("msg", msg);
    	
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(encodeURIWithParams ("https://smsapi.free-mobile.fr/sendmsg", params)))
                .GET()
                .build();

        HttpResponse<Void> response = httpClient.send(request,
                HttpResponse.BodyHandlers.discarding());
        
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
    
    public final static HashMap<String, Double> parseOutOfPlanData ( String html ) throws Exception {
		String regex = "^\\s*Hors forfait (\\w+) : <span class=\"info\">(.*)€</span>";
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		while (matcher.find()) {
			result.put( matcher.group(1), Double.parseDouble(matcher.group(2) ));
		}
		
		if (result.isEmpty()) { throw new Exception ("Out of plan info are not readable"); }
		
    	return result;
    }

    public final static Integer parseOptionStatus ( String html, String option ) throws Exception {
    	
    	int activated = -1;
        String regex = "^\\s*<a href=\"/account/mes-options\\?update="+option+"&activate=(\\d+)\"";
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			activated = Integer.parseInt(matcher.group(1));
		}
		else {
	    	throw new Exception (option+" Service status is not readable");
		}
		return activated;
    }
    
  public final static void loginFailureCheck ( String html ) throws Exception {
    	
        String regex = "utilisateur ou mot de passe incorrect";
        
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			throw new Exception("Authentication failure, incorrect login/password");
		}
    }
    
    public static void main(String[] args) {

        try {
	    	Builder builder = HttpRequest.newBuilder()
	                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"); // add request header
	        
	    	// Call home page
	    	HttpRequest request = builder.copy()
	                .GET()
	                .uri(URI.create("https://mobile.free.fr/account/"))
	                .build();
	
	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        if (response.statusCode() == 200) {
	        	log.trace("GET https://mobile.free.fr/account/");
	        }
	        else {
	        	throw new Exception ("https://mobile.free.fr/account/ - Response code: "+ response.statusCode());
	        }
	        
	        // Authentication step
    		String user_id = Config.getProperty("FreeMobile.userid");
    		String password = Config.getProperty("FreeMobile.password");
    		if (user_id == null || password == null) {
    			throw new Exception ("user_id/passord are null");
    		}
    		if (user_id.length() != 8 || password.length()<6) {
    			throw new Exception ("user_id/passord lenght are invalid");
    		}
    		log.trace("Call authentication request with userID: "+user_id);
        	request = builder.copy()
                    .POST(BodyPublishers.ofString("login-ident="+user_id+"&login-pwd="+password+"&bt-login=1"))
                    .uri(URI.create("https://mobile.free.fr/account/"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
        	
        	response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            loginFailureCheck(response.body());
            log.trace("Authentication success");
            
            // Read DATA conso info
            HashMap<String, Double> conso = new HashMap<String, Double>();
	    	conso = parseOutOfPlanData (response.body());

	        // Alert if any DATA sevice out of plan
	    	Iterator<Entry<String, Double>> it = conso.entrySet().iterator();
	    	String msg = null;
		    while (it.hasNext()) {
		        Map.Entry<String, Double> entry = (Map.Entry<String, Double>)it.next();
		        if (entry.getValue() > 0) {
		        	msg += "/!\\ Hors Fofait " + entry.getKey() + " /!\\: " + entry.getValue() + "€.\n";
		        	log.trace("/!\\ Hors Fofait " + entry.getKey() + " /!\\: " + entry.getValue() + "€.\n");
		        }
		    }
		    if (msg != null) {
		    	sendText(msg);
		    }
		    else {
		    	log.trace("No out of plan detected");
		    }
		        
		    // Check if the DATA option needs to be changed, update it and send an alert
            request = builder.copy()
                    .GET()
                    .uri(URI.create("https://mobile.free.fr/account/mes-options"))
                    .build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            int activated = parseOptionStatus (response.body(), "data");
            
            log.trace("data option is "+((activated == 1)?"desactivated":"activated"));

			if (activated == 0 && conso.get("DATA") > 0) {
	            request = builder.copy()
	                    .GET()
	                    .uri(URI.create("https://mobile.free.fr/account/mes-options?update=data&activate=0"))
	                    .build();
	            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	            activated = parseOptionStatus (response.body(), "data");
	            if (activated == 1) {
	            	sendText("Info: DATA service a été désactivé.");
	            	log.trace("Info: DATA service a été désactivé.");
	            }
	            else {
	            	throw new Exception ("DATA service switch off failure");
	            }
			}
			
			if (activated == 1 && conso.get("DATA") == 0) {
	            request = builder.copy()
	                    .GET()
	                    .uri(URI.create("https://mobile.free.fr/account/mes-options?update=data&activate=1"))
	                    .build();
	            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	            activated = parseOptionStatus (response.body(), "data");
	            if (activated == 0) {
	            	sendText("Info: DATA service a été réactivé.");
	            	log.trace("Info: DATA service a été réactivé.");
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
	    	Builder builder = HttpRequest.newBuilder()
	                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"); // add request header
	        
	    	HttpRequest request = builder.copy()
	                .GET()
	                .uri(URI.create("https://mobile.free.fr/account/?logout=user"))
	                .build();
	
	        try {
				httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        	log.trace("Logoff request success");
			} catch (Exception e) {
				log.error("logoff request failed :", e);
			}        	
        }
    }	
}
