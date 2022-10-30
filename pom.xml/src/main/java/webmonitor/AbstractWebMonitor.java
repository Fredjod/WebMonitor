package webmonitor;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import toolbox.Param;
import toolbox.Texto;


public abstract class AbstractWebMonitor {

	protected final Logger log = LogManager.getLogger(this.getClass().getName());

	protected static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .cookieHandler(new CookieManager())
            .followRedirects(Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

	protected static final Builder builder = HttpRequest.newBuilder()
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"); // add request header


	protected final HttpResponse<String> httpGetRequest (String urlRequest) throws Exception {

    	HttpRequest request = builder.copy()
                .GET()
                .uri(URI.create(urlRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
        	log.trace("GET success: "+ urlRequest);
        }
        else {
        	throw new Exception ("GET failure: "+urlRequest+" - Response code: "+ response.statusCode());
        }
        return response;
	}
	protected final HttpResponse<String> httpPostRequest (String urlRequest, String postParams) throws Exception {

    	HttpRequest request = builder.copy()
                .GET()
                .uri(URI.create(urlRequest))
                .build();
    	
		request = builder.copy()
	            .POST(BodyPublishers.ofString(postParams))
	            .uri(URI.create(urlRequest))
	            .header("Content-Type", "application/x-www-form-urlencoded")
	            .build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
        	log.trace("POST success: "+ urlRequest);
        }
        else {
        	throw new Exception ("POST failure: "+urlRequest+" - Response code: "+ response.statusCode());
        }
		return response;
	}
	
	
    public final HashMap<String, Double> parseKeyStringValueDoubleHastable ( String html, String regex ) throws Exception {
    	
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		
		HashMap<String, Double> result = new HashMap<String, Double>();
		while (matcher.find()) {
			result.put( matcher.group(1), Double.parseDouble(matcher.group(2) ));
		}
		
		if (result.isEmpty()) { throw new Exception ("Regex not found: "+regex); }
		
    	return result;
    }
    
    public final HashMap<String, String> parseKeyStringValueStringHastable ( String html, String regex ) throws Exception {
    	
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		
		HashMap<String, String> result = new HashMap<String, String>();
		while (matcher.find()) {
			if (result.get(matcher.group(1)) == null) {
				result.put(matcher.group(1), "");
			}
			
			result.put( matcher.group(1), result.get(matcher.group(1))+matcher.group(2) );
		}
		
		if (result.isEmpty()) { throw new Exception ("Regex not found: "+regex); }
		
    	return result;
    }

    public final int parseValueInteger ( String html, String regex ) throws Exception {
    	
    	int activated = -1;
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			activated = Integer.parseInt(matcher.group(1));
		}
		else {
	    	throw new Exception ("Regex not found: "+regex);
		}
		return activated;
    }

    
    public final long parseValueLong ( String html, String regex ) throws Exception {
    	
    	long returnVal = 0;
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			returnVal = Long.parseLong(matcher.group(1));
		}
		else {
	    	throw new Exception ("Regex not found: "+regex);
		}
		return returnVal;
    }    
    
    public final String encodeURIWithParams (String uri, HashMap<String, String> params) throws Exception {
    	Iterator<Entry<String, String>> it = params.entrySet().iterator();
    	String uriWithParams = uri+"?";
	    while (it.hasNext()) {
	        Map.Entry<String, String> entry = (Map.Entry<String, String>)it.next();
	        uriWithParams += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
	        if (it.hasNext()) { uriWithParams += "&"; }
	    }
    	return uriWithParams;

    }
    
   protected final void loginFailureCheck ( String html, String regex ) throws Exception {
        
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			throw new Exception("Authentication failure, incorrect login/password");
		}
    }
   
	protected final void textoAlert (String msg) throws Exception {
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
    
    public abstract void run ();
}
