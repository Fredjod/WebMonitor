package tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import toolbox.Param;

public class TestParam {

	@Test
	void readAuth () throws Exception {
		String user_id = Param.getProperty("FreeMobile.userid");
		assertEquals(user_id, "88888888");
		String password = Param.getProperty("FreeMobile.password");
		assertEquals(password, "password");		
	}	

	@Test
	void readParam () throws Exception {
		String user_id = Param.getProperty("smtp.server");
		assertEquals(user_id, "smtp.free.fr");
	
	}		
	
}
