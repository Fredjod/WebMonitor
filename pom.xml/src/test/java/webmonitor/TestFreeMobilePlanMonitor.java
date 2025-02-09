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

		String html_extract_conso_et_factures = "               <div class=\"page p-conso\">\n" + 
				"      <h1 class=\"page__title\"><span class=\"bold\">Bonjour</span> Victor<span class=\"dot\"></span></h1>\n" + 
				"      <div class=\"details\">\n" + 
				"         <div class=\"title\">Votre Forfait 2€ en détail - Avec Option Booster</div>\n" + 
				"                     <div class=\"sub-title\">Du 5 mars au <span>4 avril 2023</span></div>\n" + 
				"               </div>\n" + 
				"            <div class=\"toggle-conso\">\n" + 
				"         <div class=\"toggle-select-container\">\n" + 
				"            <div class=\"toggle-selected\"></div>\n" + 
				"         </div>\n" + 
				"         <a href=\"#france\" data-target=\"local\" class=\"selected local-country\">En France</a>\n" + 
				"                     <a href=\"#etranger\" data-target=\"roaming\" class=\"foreign\">À l’étranger</a>\n" + 
				"               </div>\n" + 
				"      <div class=\"conso-infos conso-local\">\n" + 
				"         <div class=\"grid-l conso__grid conso-local-roaming-container\">\n" + 
				"                     <div class=\"conso-container-infos\">\n" + 
				"                              <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle  cx=\"64\" cy=\"64\" r=\"64\" style=\" stroke-dashoffset: 158.38 \" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle  cx=\"52\" cy=\"52\" r=\"52\" style=\" stroke-dashoffset: 158.38 \" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <div class=\"number-circle\">\n" + 
				"                                             <p><span> 426,2Mo</span> <br/> /1Go</p>\n" + 
				"                                       </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"text-conso\">\n" + 
				"                  <div class=\"title-conso\" data-target=\"data\">\n" + 
				"                     <img src=\"/assets/images/icons/rss.svg\" alt=\"internet\" />\n" + 
				"                     <p>Internet</p>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso-content\">\n" + 
				"                                       <p>Restant :<span>0o</span></p>\n" + 
				"                                       <p>Hors-forfait : 0.00€</p>\n" + 
				"                     <p>Empreinte carbone : 10g CO2e</p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"                        <div class=\"conso-container-infos\">\n" + 
				"                                             <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\" stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\" stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <div class=\"number-circle\">\n" + 
				"                                             <p><span>Illimité</span></p>\n" + 
				"                                       </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"text-conso\">\n" + 
				"                  <div class=\"title-conso\" data-target=\"voix\">\n" + 
				"                     <img src=\"/assets/images/icons/phone_red.svg\" alt=\"téléphone\" />\n" + 
				"                     <p>Appels</p>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso-content\">\n" + 
				"                     <p>Vers la France :<span>35m 12s</span></p>\n" + 
				"                     <p>Vers l'international : <span>0s</span></p>\n" + 
				"                     <p>Hors-forfait : 0.00€</p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"                        <div class=\"conso-container-infos\">\n" + 
				"               <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <div class=\"number-circle\">\n" + 
				"                     <p><span>Illimité</span></p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"text-conso\">\n" + 
				"                  <div class=\"title-conso\" data-target=\"sms\">\n" + 
				"                     <img src=\"/assets/images/icons/message_2.svg\" alt=\"SMS\" />\n" + 
				"                     <p>SMS</p>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso-content\">\n" + 
				"                     <p>105 SMS <span>/ illimité</span></p>\n" + 
				"                     <p>Hors-forfait : 0.00€</p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"                        <div class=\"conso-container-infos\">\n" + 
				"               <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <div class=\"number-circle\">\n" + 
				"                     <p><span>Illimité</span></p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"               <div class=\"text-conso\">\n" + 
				"                  <div class=\"title-conso\" data-target=\"mms\">\n" + 
				"                     <img src=\"/assets/images/icons/image_mms.svg\" alt=\"MMS\" />\n" + 
				"                     <p>MMS</p>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso-content\">\n" + 
				"                     <p>8 MMS <span>/ illimité</span></p>\n" + 
				"                     <p>Hors-forfait : 0.00€</p>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"         </div>\n" + 
				"      </div>\n" + 
				"                  <div style=\"display:none\" class=\"conso-infos conso-roaming\">\n" + 
				"            <div class=\"grid-l conso__grid conso-local-roaming-container\">\n" + 
				"                              <div class=\"conso-container-infos\">\n" + 
				"                                                      <div class=\"rounded-progress\">\n" + 
				"                     <svg class=\"visible-desktop\">\n" + 
				"                        <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                        <circle cx=\"64\" cy=\"64\" r=\"64\" style=\" stroke-dashoffset: 192.33 \" pathLength=\"100\"></circle>\n" + 
				"                     </svg>\n" + 
				"                     <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                        <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                        <circle cx=\"52\" cy=\"52\" r=\"52\" style=\" stroke-dashoffset: 192.33 \" pathLength=\"100\"></circle>\n" + 
				"                     </svg>\n" + 
				"                     <div class=\"number-circle\">\n" + 
				"                                                   <p><span> 78,53Mo</span> <br/> /1Go</p>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso\">\n" + 
				"                     <div class=\"title-conso\" data-target=\"data\">\n" + 
				"                        <img src=\"/assets/images/icons/rss.svg\" alt=\"internet\" />\n" + 
				"                        <p>Internet</p>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"text-conso-content\">\n" + 
				"                                             <p>Restant :<span>945,47Mo</span></p>\n" + 
				"                                             <p>Hors-forfait : 0.00€</p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"                              <div class=\"conso-container-infos\">\n" + 
				"                                                      <div class=\"rounded-progress\">\n" + 
				"                    <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\" stroke-dashoffset: 200 \" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\" stroke-dashoffset: 200 \" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                     <div class=\"number-circle\">\n" + 
				"                                                   <p><span>Illimité</span></p>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso\">\n" + 
				"                     <div class=\"title-conso\" data-target=\"voix\">\n" + 
				"                        <img src=\"/assets/images/icons/phone_red.svg\" alt=\"téléphone\" />\n" + 
				"                        <p>Appels</p>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"text-conso-content\">\n" + 
				"                        <p>Émis :<span>0s</span></p>\n" + 
				"                        <p>Reçus : <span>0s</span></p>\n" + 
				"                        <p>Hors-forfait : 0.00€</p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"                              <div class=\"conso-container-infos\">\n" + 
				"                  <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                     <div class=\"number-circle\">\n" + 
				"                        <p><span>Illimité</span></p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso\">\n" + 
				"                     <div class=\"title-conso\" data-target=\"sms\">\n" + 
				"                        <img src=\"/assets/images/icons/message_2.svg\" alt=\"SMS\" />\n" + 
				"                        <p>SMS</p>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"text-conso-content\">\n" + 
				"                        <p>53 SMS <span>/ illimité</span></p>\n" + 
				"                        <p>Hors forfait : 0.00€</p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"                              <div class=\"conso-container-infos\">\n" + 
				"                  <div class=\"rounded-progress\">\n" + 
				"                  <svg class=\"visible-desktop\">\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\"></circle>\n" + 
				"                     <circle cx=\"64\" cy=\"64\" r=\"64\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                  <svg class=\"visible-phone visible-tablet-only\">\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\"></circle>\n" + 
				"                     <circle cx=\"52\" cy=\"52\" r=\"52\" style=\"stroke-dashoffset: 200\" pathLength=\"100\"></circle>\n" + 
				"                  </svg>\n" + 
				"                     <div class=\"number-circle\">\n" + 
				"                        <p><span>Illimité</span></p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"text-conso\">\n" + 
				"                     <div class=\"title-conso\" data-target=\"mms\">\n" + 
				"                        <img src=\"/assets/images/icons/image_mms.svg\" alt=\"MMS\" />\n" + 
				"                        <p>MMS</p>\n" + 
				"                     </div>\n" + 
				"                     <div class=\"text-conso-content\">\n" + 
				"                        <p>6 MMS <span>/ illimité</span></p>\n" + 
				"                        <p>Hors forfait : 0.00€</p>\n" + 
				"                     </div>\n" + 
				"                  </div>\n" + 
				"               </div>\n" + 
				"            </div>\n" + 
				"         </div>\n" + 
				"         \n" + 
				"      \n" + 
				"            <p class=\"next-period\">Votre forfait sera réinitialisé le 5 avril 2023</p>\n" + 
				"      \n" + 
				"   <div class=\"buttons\">\n" + 
				"      <div class=\"text-center ecolo\">\n" + 
				"         <a href=\"/account/conso-et-factures/empreinte-carbone\" class=\"bt bt-green bt-label bt-eur2 bt-label-left bt-big bt-bold bt-round\">\n" + 
				"            <span class=\"bt-icon bt-icon-green i-ecologie\"></span> Voir mon empreinte carbone\n" + 
				"         </a>\n" + 
				"      </div>\n" + 
				"   </div>\n" + 
				"\n" + 
				"      <div id=\"conso-selector\" class=\"user-lines\">\n" + 
				"   <h2 class=\"title pointer toggle-bt\">\n" + 
				"      <span class=\"local bold\">Ma conso en France</span>\n" + 
				"      <span class=\"roaming bold\">Ma conso à l'étranger</span>\n" + 
				"      <img id=\"conso-details\" src=\"/assets/images/icons/arrow-down.svg\" width=\"40\" height=\"40\" alt=\"chevron bas\" />\n" + 
				"   </h2>\n" + 
				"   <div id=\"conso-data\" style=\"display: none;\"> \n" + 
				"      <div class=\"table-details\">\n" + 
				"                                          <div class=\"conso-data local \"> \n" + 
				"         <div class=\"preheader\">\n" + 
				"                                                         <img src=\"/assets/images/icons/phone.svg\" width=\"24\" height=\"24\" alt=\"téléphone\" />\n" + 
				"                        <span class=\"bold\">Mes appels</span>\n" + 
				"         </div>\n" + 
				"";
		
		
		String html_extract_mes_options = "         <div class=\"grid-l as__item\">\n" + 
				"               <div class=\"grid-c w-desktop-8 as__cell as__item__name\">\n" + 
				"                  <div class=\"inner bold\">\n" + 
				"                                             <a href=\"/account/mes-options/data\">\n" + 
				"                                                   <span class=\"icon i-info\"></span>\n" + 
				"                                                <span>Service de données</span>\n" + 
				"                        </a>\n" + 
				"                                                                                 </div>\n" + 
				"               </div>\n" + 
				"                                                   <div class=\"grid-c w-2 w-desktop-2 w-tablet-4 as__cell as__status as_status--action as__status--on as__status--active\">\n" + 
				"                     <div class=\"inner\">\n" + 
				"                                                   <span class=\"as__status__text\">Oui</span>\n" + 
				"                           <i class=\"icon i-check\"></i>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n" + 
				"                  <div class=\"grid-c w-2 w-desktop-2 w-tablet-4 as__cell as__status as_status--action as__status--off \">\n" + 
				"                     <div class=\"inner\">\n" + 
				"                                                <a href=\"/account/mes-options?update=data&activate=0\" title=\"D&#x00E9;sactiver&#x20;cette&#x20;option\">\n" + 
				"                                                   <span class=\"as__status__text\">Non</span>\n" + 
				"                           <i class=\"icon i-cross\"></i>\n" + 
				"                                                </a>\n" + 
				"                                             </div>\n" + 
				"                  </div>\n" + 
				"                                             </div>";
		
		
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
		

       assertEquals("0.00€", conso.get("SMS"));
       assertTrue(outOfPlan.get("SMS"));
       assertEquals(Integer.valueOf(0), Integer.valueOf(activated));
	}
}
