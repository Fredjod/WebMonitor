package tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import tools.Config;

public class TestConfig {

	@Test
	void readParam () throws Exception {
		String user_id = Config.getProperty("FreeMobile.userid");
		assertEquals(user_id, "88888888");
		String password = Config.getProperty("FreeMobile.password");
		assertEquals(password, "password");		
	}	
	
}
