package toolbox;

import static java.util.Objects.nonNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/** This class is responsible for loading configuration from properties files.
 * @author FredJod
 *
 */
public class Param {

		private static final Logger log = LogManager.getLogger(Param.class);
    	// to make this singleton
        private Param() {

        }

        private static final Properties PROPERTIES = new Properties();

        static {
            try {
				loadPropertiesFile("auth.properties");
				loadPropertiesFile("params.properties");
	            // loadPropertiesFile("def.properties", DEF_CONFIG);
	            // you can have any number of properties file loaded here
				// properties file have to be in classpath, like the resources package
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Stacktrace:", e);
			}
        }   


        /**
         * It will load properties file
         * 
         * @param filePath
         * @throws Exception 
         */
        private static void loadPropertiesFile(String filePath) throws Exception {
            try {
                InputStream input = Param.class.getClassLoader().getResourceAsStream(filePath);
                PROPERTIES.load(input);
                if (nonNull(input)) {
                    input.close();
                }
            	
            } catch (IOException e) {
                throw e;
            }
        }

        /**
         * It will give the property value present in properties file.
         * 
         * @param propertyName
         * @return property value
         */
        public static String getProperty(String propertyName) {
            return PROPERTIES.getProperty(propertyName);
        }
}
