package webmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMediathequeMonitor {
	private static final Logger log = LogManager.getLogger(TestMediathequeMonitor.class);

	
	@Test
	void parseJsonResponse () throws Exception {
		MediathequeMonitor webmon3 = new MediathequeMonitor();
		
		String json = "{\"errors\":[],\"message\":null,\"success\":true,\"d\":{\"AccountAbstractMessageCollection\":[{\"Message\":\"Vous n'avez pas de prêt en cours\",\"Priority\":3,\"Type\":0},{\"Message\":\"0 est à rendre prochainement\",\"Priority\":0,\"Type\":0},{\"Message\":\"Vous n'avez pas de réservation\",\"Priority\":3,\"Type\":1}],\"AccountSummary\":{\"BookingsAvailableCount\":0,\"BookingsNotAvailableCount\":0,\"BookingsTotalCount\":0,\"DisplayName\":\" JAUDIN\",\"HandingsCount\":0,\"LoansLateCount\":0,\"LoansNextHandingCount\":0,\"LoansNextHandingDate\":null,\"LoansNextHandingIsSoonLate\":false,\"LoansNotLateCount\":0,\"LoansTotalCount\":0,\"ProvisionsCount\":0,\"ProvisionsHistoCount\":0,\"SerialRoutingListsCount\":0},\"Label\":\"Mon compte\"}}";

		JsonNode jsonNode = new ObjectMapper().readTree(json);
		Boolean status = jsonNode.get("success").asBoolean();
		JsonNode jsonDataNode = jsonNode.get("d");
		JsonNode jsonAccountNode = jsonDataNode.get("AccountSummary");
		int loansCount = jsonAccountNode.get("LoansNextHandingCount").asInt();
		String dateEnding = jsonAccountNode.get("LoansNextHandingDate").asText();

		assertEquals("null", dateEnding);
		
		json = "{\n" + 
				"    \"errors\": [],\n" + 
				"    \"message\": null,\n" + 
				"    \"success\": true,\n" + 
				"    \"d\": {\n" + 
				"        \"AccountAbstractMessageCollection\": [\n" + 
				"            {\n" + 
				"                \"Message\": \"Vous avez emprunté 3 documents\",\n" + 
				"                \"Priority\": 3,\n" + 
				"                \"Type\": 0\n" + 
				"            },\n" + 
				"            {\n" + 
				"                \"Message\": \"3 sont à rendre le 23\\/09\\/2022\",\n" + 
				"                \"Priority\": 0,\n" + 
				"                \"Type\": 0\n" + 
				"            },\n" + 
				"            {\n" + 
				"                \"Message\": \"Vous n'avez pas de réservation\",\n" + 
				"                \"Priority\": 3,\n" + 
				"                \"Type\": 1\n" + 
				"            }\n" + 
				"        ],\n" + 
				"        \"AccountSummary\": {\n" + 
				"            \"BookingsAvailableCount\": 0,\n" + 
				"            \"BookingsNotAvailableCount\": 0,\n" + 
				"            \"BookingsTotalCount\": 0,\n" + 
				"            \"DisplayName\": \" JAUDIN\",\n" + 
				"            \"HandingsCount\": 0,\n" + 
				"            \"LoansLateCount\": 0,\n" + 
				"            \"LoansNextHandingCount\": 3,\n" + 
				"            \"LoansNextHandingDate\": \"\\/Date(1663884000000+0200)\\/\",\n" + 
				"            \"LoansNextHandingIsSoonLate\": false,\n" + 
				"            \"LoansNotLateCount\": 3,\n" + 
				"            \"LoansTotalCount\": 3,\n" + 
				"            \"ProvisionsCount\": 0,\n" + 
				"            \"ProvisionsHistoCount\": 0,\n" + 
				"            \"SerialRoutingListsCount\": 0\n" + 
				"        },\n" + 
				"        \"Label\": \"Mon compte\"\n" + 
				"    }\n" + 
				"}";
		
			jsonNode = new ObjectMapper().readTree(json);
			status = jsonNode.get("success").asBoolean();
			jsonDataNode = jsonNode.get("d");
			jsonAccountNode = jsonDataNode.get("AccountSummary");
			loansCount = jsonAccountNode.get("LoansNextHandingCount").asInt();
			dateEnding = jsonAccountNode.get("LoansNextHandingDate").asText();
			String regexDate = "^/Date\\((\\d+)\\+0200\\)";
			long miliseconds = webmon3.parseValueLong(dateEnding, regexDate);
			Date endingDate = new Date(miliseconds);
			
			log.trace("status: "+status+"; loansCount: "+loansCount+"; loansEnding: "+ endingDate.toString());
						
			Calendar currentCal1 = Calendar.getInstance(); 
			Calendar currentCal2 = Calendar.getInstance(); 
			Calendar endingCal = Calendar.getInstance();
			
			// faire le test si date est à moins de 4 jours
			currentCal1.set(2022, 8, 18);
			currentCal2.set(2022, 8, 19);
			endingCal.setTime(endingDate);
			currentCal1.add(Calendar.DAY_OF_MONTH, 4);
			currentCal2.add(Calendar.DAY_OF_MONTH, 4);
			
			log.trace("currentCal1: "+currentCal1.getTime());
			log.trace("currentCal2: "+currentCal2.getTime());
			
			assertEquals(true, status);
			assertEquals(false, currentCal1.after(endingCal));
			assertEquals(true, currentCal2.after(endingCal));
			assertEquals(Integer.valueOf(3), Integer.valueOf(loansCount));
			
			
	}

}
