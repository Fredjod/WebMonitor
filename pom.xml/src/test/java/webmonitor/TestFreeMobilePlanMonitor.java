package webmonitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import webmonitor.VictorFreeMobilePlanMonitor;


public class TestFreeMobilePlanMonitor {
	private static final Logger log = LogManager.getLogger(TestFreeMobilePlanMonitor.class);
	
	@Test
	void encodeURI () throws Exception {
		VictorFreeMobilePlanMonitor webmon1 = new VictorFreeMobilePlanMonitor();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("param1", "parametre ave des espaces");
		params.put("param2", "paramètre avec des caractère spéciaux!");
	
		String result = webmon1.encodeURIWithParams ( "https://free.fr/testing", params);
		assertEquals("https://free.fr/testing?param1=parametre+ave+des+espaces&param2=param%C3%A8tre+avec+des+caract%C3%A8re+sp%C3%A9ciaux%21", result);
		log.trace("paramsencoding: "+ result);
	}
	
	
	@Test
	void parseFreeMobilePage () throws Exception {

		VictorFreeMobilePlanMonitor webmon1 = new VictorFreeMobilePlanMonitor();

		String html_extract_conso_et_factures = "      <div class=\"conso-infos conso-local\">\n" + 
				"         <div class=\"grid-l conso__grid\">\n" + 
				"            <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"               <div class=\"conso__content\">\n" + 
				"                  <div class=\"conso__text\">\n" + 
				"                     France : <span class=\"info\">11m 59s</span><br>\n" + 
				"                     International : <span class=\"info\">0s</span><br>\n" + 
				"                     Hors forfait voix : <span class=\"info\">0.00€</span>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"conso__icon\" data-target=\"voix\">\n" + 
				"                     <div class=\"wrapper-align\">\n" + 
				"                        <div class=\"i-mobile_appels icon\"></div>\n" + 
				"                        Appels\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"            <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"               <div class=\"conso__content\">\n" + 
				"                  <div class=\"conso__text\"><span class=\"info\">71</span> SMS / illimités<br>\n" + 
				"                     Hors forfait SMS : <span class=\"info\">0.00€</span>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"conso__icon\" data-target=\"sms\">\n" + 
				"                     <div class=\"wrapper-align\">\n" + 
				"                        <div class=\"i-sms icon\"></div>\n" + 
				"                        SMS\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"         </div>\n" + 
				"         <div class=\"grid-l conso__grid\">\n" + 
				"            <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"               <div class=\"conso__content\">\n" + 
				"                  <div class=\"conso__text\">\n" + 
				"                     <span class=\"info\">73,91Mo</span>\n" + 
				"                     / 50Mo                     <br>\n" + 
				"                     Hors forfait DATA : <span class=\"info\">0o - 1.20€</span><br>\n" + 
				"                     Empreinte carbone : <span class=\"info\">2g CO2e</span>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"conso__icon\" data-target=\"data\">\n" + 
				"                                             <div id=\"conso-progress\" class=\"progressbar\"\n" + 
				"                             data-progress-value=\"147,82543754578\"></div>\n" + 
				"                                          <div class=\"wrapper-align\">\n" + 
				"                                                <div>\n" + 
				"                           <span class=\"big info\">73</span>\n" + 
				"                           <span class=\"small info\" style=\"text-transform:capitalize;\">Mo</span>\n" + 
				"                        </div>\n" + 
				"                        DATA\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"            <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"               <div class=\"conso__content\">\n" + 
				"                  <div class=\"conso__text\">\n" + 
				"                     <span class=\"info\">6</span> MMS\n" + 
				"                                          <br>\n" + 
				"                     Hors forfait MMS : <span class=\"info\">0.00€</span>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"conso__icon\" data-target=\"mms\">\n" + 
				"                     <div class=\"wrapper-align\">\n" + 
				"                        <div class=\"i-mms icon\"></div>\n" + 
				"                        MMS\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"         </div>\n" + 
				"      </div>\n" + 
				"                  <div style=\"display:none\" class=\"conso-infos conso-roaming\">\n" + 
				"            <div class=\"grid-l conso__grid\">\n" + 
				"               <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"                  <div class=\"conso__content\">\n" + 
				"                     <div class=\"conso__text\">\n" + 
				"                        Appels émis : <span class=\"info\">0s</span><br>\n" + 
				"                        Appels reçus : <span class=\"info\">0s</span><br>\n" + 
				"                        Hors forfait voix : <span class=\"info\">0.00€</span>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"conso__icon\" data-target=\"voix\">\n" + 
				"                        <div class=\"wrapper-align\">\n" + 
				"                           <div class=\"i-mobile_appels icon\"></div>\n" + 
				"                           Appels\n" + 
				"                        </div>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"                  <div class=\"conso__content\">\n" + 
				"                     <div class=\"conso__text\"><span class=\"info\">0</span> SMS\n" + 
				"                        / illimités                        <br>\n" + 
				"                        Hors forfait SMS : <span class=\"info\">0.00€</span>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"conso__icon\" data-target=\"mms\">\n" + 
				"                        <div class=\"wrapper-align\">\n" + 
				"                           <div class=\"i-sms icon\"></div>\n" + 
				"                           SMS\n" + 
				"                        </div>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"\n" + 
				"            <div class=\"grid-l conso__grid\">\n" + 
				"               <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"                  <div class=\"conso__content\">\n" + 
				"                     <div class=\"conso__text\">\n" + 
				"                        <span class=\"info\">0o</span>\n" + 
				"                        / 50Mo\n" + 
				"                        <br>\n" + 
				"                        Hors forfait DATA : <span class=\"info\">0.00€</span>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"conso__icon\" data-target=\"data\">\n" + 
				"                                                   <div id=\"conso-progress\" class=\"progressbar\"\n" + 
				"                                data-progress-value=\"0\"></div>\n" + 
				"                                                <div class=\"wrapper-align\">\n" + 
				"                                                      <div>\n" + 
				"                              <span class=\"big info\">0</span>\n" + 
				"                              <span class=\"small info\" style=\"text-transform:capitalize;\">o</span>\n" + 
				"                           </div>\n" + 
				"                           DATA\n" + 
				"                        </div>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"grid-c w-4 w-tablet-4\">\n" + 
				"                  <div class=\"conso__content\">\n" + 
				"                     <div class=\"conso__text\">\n" + 
				"                        <span class=\"info\">0</span> MMS<br>\n" + 
				"                        Hors forfait MMS : <span class=\"info\">0.00€</span>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"conso__icon\" data-target=\"mms\">\n" + 
				"                        <div class=\"wrapper-align\">\n" + 
				"                           <div class=\"i-mms icon\"></div>\n" + 
				"                           MMS\n" + 
				"                        </div>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"         </div>";
		
		
		String html_extract_mes_options = "         <div class=\"grid-l as__item\">\n" + 
				"               <div class=\"grid-c w-desktop-8 as__cell as__item__name\">\n" + 
				"                  <div class=\"inner bold\">\n" + 
				"                                             <a href=\"/account/mes-options/data\">\n" + 
				"                                                   <span class=\"icon i-info\"></span>\n" + 
				"                                                <span>Service de données</span>\n" + 
				"                        </a>\n" + 
				"                                                                                 </div>\n" + 
				"               </div>\n" + 
				"                                                   <div class=\"grid-c w-2 w-desktop-2 w-tablet-4 as__cell as__status as_status--action as__status--on \">\n" + 
				"                     <div class=\"inner\">\n" + 
				"                                                                           <a href=\"/account/mes-options?update=data&activate=1\" title=\"Activer&#x20;cette&#x20;option\">\n" + 
				"                                                                              <span class=\"as__status__text\">Oui</span>\n" + 
				"                           <i class=\"icon i-check\"></i>\n" + 
				"                                                </a>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"grid-c w-2 w-desktop-2 w-tablet-4 as__cell as__status as_status--action as__status--off as__status--active\">\n" + 
				"                     <div class=\"inner\">\n" + 
				"                                                   <span class=\"as__status__text\">Non</span>\n" + 
				"                           <i class=\"icon i-cross\"></i>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n";
		
		
        HashMap<String, String> conso = webmon1.extractConso(html_extract_conso_et_factures);
        HashMap<String, Boolean> outOfPlan = webmon1.convertConsoToOutOfPlan(conso);
        
        Iterator<Entry<String, Boolean>> it = outOfPlan.entrySet().iterator();
        String msg = "";
	    while (it.hasNext()) {
	        Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>)it.next();
	        if (entry.getValue()) {
	        	msg += "/!\\ Hors Fofait: " + entry.getKey() + " /!\\: " + conso.get(entry.getKey()) + ".\n";
	        }
	    }

		log.trace("msg: "+ msg);
		
       String regexDataActivation = "^\\s*<a href=\"/account/mes-options\\?update=data&activate=(\\d+)\"";
       int activated = webmon1.parseValueInteger(html_extract_mes_options, regexDataActivation);
		

       assertEquals("0o - 1.20€0.00€", conso.get("DATA"));
       assertFalse(outOfPlan.get("MMS"));
       assertEquals(Integer.valueOf(1), Integer.valueOf(activated));
	}
}
