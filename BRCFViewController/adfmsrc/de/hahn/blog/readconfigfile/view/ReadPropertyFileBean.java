package de.hahn.blog.readconfigfile.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.share.logging.ADFLogger;

import org.apache.commons.io.FileUtils;

/**
 * This class reads a property file given from a context init parameter
 * de.hahn.blog.readconfigfile.FILENAME defined in web.xml
 * It allows to change the prperties during runtime as the property file is reread using a action
 */
public class ReadPropertyFileBean {
    private static ADFLogger logger = ADFLogger.createADFLogger(ReadPropertyFileBean.class);
    private final static String PROPERTYFILE_PARAMETER = "de.hahn.blog.readconfigfile.FILENAME";
    private final static String PROPERTY_VERSION = "property.version";
    private final static String PROPERTY_NAME = "property.name";


    private Properties properties;

    /**
     * Setter
     * @param properties Properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * This method savely get the properties from a file if the file can be read, otherwise it return an empty properties object
     * @return Properties object read from file of empty properties object if hte file was empty or could not be found
     */
    public Properties getProperties() {
        if (properties == null) {
            FileInputStream fileInputStream = null;
            try {
                // read context parameter
                String initParameter = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(PROPERTYFILE_PARAMETER);
                logger.info("Read properties from " + initParameter);
                // try to read the file
                File file = FileUtils.getFile(initParameter);
                fileInputStream = FileUtils.openInputStream(file);
                properties = new Properties();
                properties.load(fileInputStream);
                logger.info(properties.size() + " properties successfully read.");
            } catch (IOException ioe) {
                logger.warning("Error: Property file could not be read!", ioe);
                properties = new Properties();
            } finally {
                if (fileInputStream != null)
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return properties;
    }

    /**
     * C'tor
     */
    public ReadPropertyFileBean() {
        super();
    }

    /**
     * Method returns a value for the given key from the properties object
     * @param key to get the value for
     * @return value of the key if found, otherwise null
     */
    public String getProperty(String key) {
        logger.info("Get property: " + key);
        String res = null;
        if (key != null) {
            res = getProperties().getProperty(key);
        }

        logger.info("Value: " + res);
        return res;
    }

    /**
     * Reset the read properties so that the next try to read a property ready the file again
     */
    public void readPropertiesAgain() {
        logger.info("Reset properties!");
        properties = null;
    }

    /**Method to return the version information of the configuration file.
     * this information is compiled from the keys PROPERY_NAME and PROPERTY_VERSION
     * @return version information read from the file
     */
    public String getPropertyVersionInfo() {
        String property = getProperty(PROPERTY_NAME);
        String property_2 = getProperty(PROPERTY_VERSION);
        String res = "Unkown";
        if (property != null && property_2 != null) {
            res = property + " " + property_2;
        }
        logger.info("Properyinfo: " + res);

        return res;
    }

    /**
     * Handler for a button which will reset the properties read from a configuration file
     * @param actionEvent Event to handle
     */
    public void rereadActionListener(ActionEvent actionEvent) {
        readPropertiesAgain();
    }
}
