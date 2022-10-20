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
		
		json = "{\"errors\":[],\"message\":null,\"success\":true,\"d\":{\"AccountAbstractMessageCollection\":[{\"Message\":\"Vous avez emprunté 5 documents\",\"Priority\":3,\"Type\":0},{\"Message\":\"5 sont à rendre le 05\\/11\\/2022\",\"Priority\":0,\"Type\":0},{\"Message\":\"Vous n'avez pas de réservation\",\"Priority\":3,\"Type\":1}],\"AccountSummary\":{\"BookingsAvailableCount\":0,\"BookingsNotAvailableCount\":0,\"BookingsTotalCount\":0,\"DisplayName\":\" JAUDIN\",\"HandingsCount\":0,\"LoansLateCount\":0,\"LoansNextHandingCount\":5,\"LoansNextHandingDate\":\"\\/Date(1667602800000+0100)\\/\",\"LoansNextHandingIsSoonLate\":false,\"LoansNotLateCount\":5,\"LoansTotalCount\":5,\"ProvisionsCount\":0,\"ProvisionsHistoCount\":0,\"SerialRoutingListsCount\":0},\"Label\":\"Mon compte\"}}";
		
			jsonNode = new ObjectMapper().readTree(json);
			status = jsonNode.get("success").asBoolean();
			jsonDataNode = jsonNode.get("d");
			jsonAccountNode = jsonDataNode.get("AccountSummary");
			loansCount = jsonAccountNode.get("LoansNextHandingCount").asInt();
			dateEnding = jsonAccountNode.get("LoansNextHandingDate").asText();
			String regexDate = "^/Date\\((\\d+)\\+0";
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
			assertEquals(false, currentCal2.after(endingCal));
			assertEquals(Integer.valueOf(5), Integer.valueOf(loansCount));
			
			
	}

}
