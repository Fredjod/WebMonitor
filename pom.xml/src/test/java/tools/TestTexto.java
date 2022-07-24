package tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import toolbox.Param;
import toolbox.Texto;

public class TestTexto {
	
	@Test
	void sendTxt () throws Exception {
	/*	
		Texto texto = new Texto ();
		int status = texto.send(
				Param.getProperty("FreeMobile.fred.userid"), 
    			Param.getProperty("FreeSMSService.fred.password"), 
    			"Bose Monitor: soundlink_revolve_plus_ii_fr est disponible. https://www.bose.fr/fr_fr/products/outlet/soundlink-revolve-plus-ii-factory-renewed.html");
		assertEquals(Integer.valueOf(200), Integer.valueOf(status));
	*/
	}

	@Test
	void TestBreak () {
		int i;
		for (i=0; i<10; i++) {
			if (i==5) {
				break;
			}
		}
		assertEquals(Integer.valueOf(5), Integer.valueOf(i));
		
	}
	
	
}
