package webmonitor;

import java.net.http.HttpResponse;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import toolbox.Param;

public class MediathequeMonitor extends AbstractWebMonitor {

	private boolean loginFailureCheck(String json) throws Exception {
		
		JsonNode jsonNode = new ObjectMapper().readTree(json);
		boolean success = jsonNode.get("success").asBoolean();
		if (!success) {
			log.error("json");
			throw new Exception("Authentication failure, incorrect login/password");
		}
		return success;
	}
	
	@Override
	public void run() {
		try {
	        
	    	// Call home page
			httpGetRequest("https://mediatheques.montpellier3m.fr/");
			
			// Sending of RGPD cookies preferences
			String cookiePref = "{\"preferences\":\"{\\\"AnalyticsTracking\\\":{\\\"GoogleAnalyticsEnabled\\\":true},\\\"ThirdPartyProviders\\\":{\\\"Enrichments\\\":true,\\\"Thumbnails\\\":true}}\"}";
			httpPostRequest("https://mediatheques.montpellier3m.fr/default/CookiesPreferencesService.svc/SetCookiesPreferences", cookiePref);
			
	        // Authentication step
    		String user_id = Param.getProperty("Mediatheque.chloe.userid");
    		String password = Param.getProperty("Mediatheque.chloe.password");
    		if (user_id == null || password == null) {
    			throw new Exception ("user_id/passord are null");
    		}
    		if (user_id.length() < 6 || password.length()<6) {
    			throw new Exception ("user_id/passord lenght are invalid");
    		}
    		log.trace("Call authentication request with userID: "+user_id);

    		HttpResponse<String> response = httpPostRequest("https://mediatheques.montpellier3m.fr/default/Portal/Recherche/logon.svc/logon", "username="+user_id+"&password="+password+"&rememberMe=false");
            loginFailureCheck(response.body());
            log.trace("Authentication success");
            
            // Get loans info and send alert if any
            httpGetRequest("https://mediatheques.montpellier3m.fr/Default/account.aspx?s=1#/transactions/loans");
            response = httpGetRequest("https://mediatheques.montpellier3m.fr/default/Portal/Services/UserAccountService.svc/RetrieveAccountSummary?serviceCode=CAMO&userUniqueIdentifier=&token="+new Date().getTime()+"&timestamp="+new Date().getTime());
			JsonNode jsonNode = new ObjectMapper().readTree(response.body());
			if (! jsonNode.get("success").asBoolean()) {
				throw new Exception("Failure of reading RetrieveAccountSummary");
			}
			JsonNode jsonDataNode = jsonNode.get("d");
			JsonNode jsonAccountNode = jsonDataNode.get("AccountSummary");
			int loansCount = jsonAccountNode.get("LoansNextHandingCount").asInt();
			String dateEnding = jsonAccountNode.get("LoansNextHandingDate").asText();
			
			if (! dateEnding.equals("null")) { // the return date value is null
				String regexDate = "^/Date\\((\\d+)\\+0";
				long miliseconds = parseValueLong(dateEnding, regexDate);
				Date endingDate = new Date(miliseconds);
	
				log.trace("Mediathèque Pérols: "+loansCount+" prets à la bibliothèques sont à rendre avant le "+endingDate.toString());
				
				Calendar currentCal= Calendar.getInstance(); 
				Calendar endingCal = Calendar.getInstance();
				endingCal.setTime(endingDate);
				currentCal.add(Calendar.DAY_OF_MONTH, 3);
				if (currentCal.after(endingCal)) {
					String msg = "Alerte Mediathèque Pérols: "+loansCount+" prets à la bibliothèques sont à rendre avant le "+endingCal.getTime();
	            	log.warn(msg);
			    	textoAlert(msg);
				}
			}
	    }
        catch(Exception e) {
        	  log.error("Error :", e);
        }
        finally {
        	// Finally, login off
        	try {
        		httpPostRequest ("https://mediatheques.montpellier3m.fr/Default/Portal/Recherche/logon.svc/logoff", "");
			} catch (Exception e) {
				log.error("logoff request failed :", e);
			}        	
        }
	}

}
