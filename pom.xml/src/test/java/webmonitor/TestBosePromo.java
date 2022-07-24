package webmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestBosePromo {

		
	private static final Logger log = LogManager.getLogger(TestBosePromo.class);

	@Test
	void parseJsonResponse () throws Exception {

		String json = "{\n" + 
				"   \"type\" : \"boseProductWsDTO\",\n" + 
				"   \"code\" : \"soundlink_revolve_plus_ii_fr\",\n" + 
				"   \"variantOptions\" : [ {\n" + 
				"      \"type\" : \"boseColourVariantOptionWsDTO\",\n" + 
				"      \"code\" : \"soundlink_revolve_plus_ii_fr_luxe_silver_eu\",\n" + 
				"      \"atpData\" : {\n" + 
				"         \"message\" : \"Temporairement en rupture de stock\",\n" + 
				"         \"sellable\" : false\n" + 
				"      },\n" + 
				"      \"stockStatusData\" : {\n" + 
				"         \"stockStatus\" : \"OUT_OF_STOCK\"\n" + 
				"      }\n" + 
				"   }, {\n" + 
				"      \"type\" : \"boseColourVariantOptionWsDTO\",\n" + 
				"      \"code\" : \"soundlink_revolve_plus_ii_fr_triple_black_eu\",\n" + 
				"      \"atpData\" : {\n" + 
				"         \"message\" : \"Temporairement en rupture de stock\",\n" + 
				"         \"sellable\" : false\n" + 
				"      },\n" + 
				"      \"stockStatusData\" : {\n" + 
				"         \"stockStatus\" : \"OUT_OF_STOCK\"\n" + 
				"      }\n" + 
				"   } ],\n" + 
				"   \"eligibleForHSAOrFSA\" : false\n" + 
				"}";

		JsonNode variantOptionArryNode = new ObjectMapper().readTree(json).get("variantOptions");
		if (variantOptionArryNode.isArray()) {
		    for (final JsonNode objNode : variantOptionArryNode) {
		    	JsonNode atpDataNode = objNode.get("atpData");
		    	JsonNode stockStatusDataNode = objNode.get("stockStatusData");
		    	assertEquals(false, atpDataNode.get("sellable").asBoolean());
		    	assertEquals("OUT_OF_STOCK", stockStatusDataNode.get("stockStatus").asText());
		    	log.trace( objNode.get("code").asText()+", sellable: "+atpDataNode.get("sellable").asBoolean()+", stockStatus: "+stockStatusDataNode.get("stockStatus").asText() );
		    }
		}
	}
	
}
