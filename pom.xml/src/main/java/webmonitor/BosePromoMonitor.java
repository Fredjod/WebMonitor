package webmonitor;

import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import toolbox.Param;
import toolbox.Texto;

public class BosePromoMonitor extends AbstractWebMonitor {

	private void bosePromoStockCheck (String productName, String productURL, String stockURL) throws Exception {
		HttpResponse<String> response = httpGetRequest(stockURL);
		JsonNode variantOptionArryNode = new ObjectMapper().readTree(response.body()).get("variantOptions");
		if (variantOptionArryNode.isArray()) {
		    for (final JsonNode objNode : variantOptionArryNode) {
		    	JsonNode atpDataNode = objNode.get("atpData");
		    	JsonNode stockStatusDataNode = objNode.get("stockStatusData");
		    	log.trace( objNode.get("code").asText()+", sellable: "+atpDataNode.get("sellable").asBoolean()+", stockStatus: "+stockStatusDataNode.get("stockStatus").asText() );
		    	if (atpDataNode.get("sellable").asBoolean() || ! stockStatusDataNode.get("stockStatus").asText().equals("OUT_OF_STOCK")) {
		    		Texto texto = new Texto ();
			    	texto.send(
			    		Param.getProperty("FreeMobile.fred.userid"), 
			    		Param.getProperty("FreeSMSService.fred.password"), 
			    		"BosePromoMonitor: "+productName+" est disponible. "+productURL
			    	);
			    	break;
		    	}
		    }
		    
		}
		else {
			throw new Exception ("Bose Promo: Error while reading stock data: "+stockURL);
		}		
	}

	@Override
	public void run() {
		try {
			bosePromoStockCheck(
					"Soundlink_revolve_plus_ii_fr",
					"https://www.bose.fr/fr_fr/products/outlet/soundlink-revolve-plus-ii-factory-renewed.html",
					"https://www.bose.fr/ecommerce/webservice/v2/b2c/website_ce_fr/fr_FR/consumer_b2c_categories/Current/products/Online/users/anonymous/atp/soundlink_revolve_plus_ii_fr"
			);
			
			bosePromoStockCheck(
					"Soundlink_revolve_plus_ii_fr",
					"https://www.bose.fr/fr_fr/products/outlet/soundlink-revolve-ii-factory-renewed.html",
					"https://www.bose.fr/ecommerce/webservice/v2/b2c/website_ce_fr/fr_FR/consumer_b2c_categories/Current/products/Online/users/anonymous/atp/soundlink_revolve_ii_fr"
			);
		}
		catch (Exception e) {
			log.error("Error :", e);
		}

	}

}
