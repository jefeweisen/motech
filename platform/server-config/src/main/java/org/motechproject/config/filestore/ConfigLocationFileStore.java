package org.motechproject.config.filestore;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.motechproject.config.MotechConfigurationException;
import org.motechproject.config.domain.ConfigLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Used to read default platform config location(s) from config-location.properties and also to save in the same file.
 */
@Component
public class ConfigLocationFileStore {

    private PropertiesConfiguration propertiesConfiguration;
    public static final String CONFIG_LOCATION_PROPERTY_KEY = "config.location";
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLocationFileStore.class);

    @Autowired
    public ConfigLocationFileStore(PropertiesConfiguration propertiesConfiguration) {
        this.propertiesConfiguration = propertiesConfiguration;
    }

    public Iterable<ConfigLocation> getAll() {
        List<String> configLocations = loadAll();

        return map(configLocations);
    }

    private Iterable<ConfigLocation> map(List<String> configPaths) {
        List<ConfigLocation> configLocations = new ArrayList<>();

        for (String configLocation : configPaths) {
            configLocations.add(new ConfigLocation(configLocation));
        }

        return configLocations;
    }

    private List<String> loadAll() {
        return asList(propertiesConfiguration.getStringArray(CONFIG_LOCATION_PROPERTY_KEY));
    }

    public void add(String location) {
        List<String> configLocations = new ArrayList<>(loadAll());
        configLocations.add(location);
        save(configLocations);
    }

    private void save(List<String> configLocations) {
        try {
            propertiesConfiguration.setProperty(CONFIG_LOCATION_PROPERTY_KEY, StringUtils.join(configLocations, ","));
            propertiesConfiguration.save();
        } catch (ConfigurationException e) {
            String errorMessage = String.format("Could not save %s in this location %s.", propertiesConfiguration.getFileName(), propertiesConfiguration.getBasePath());
            LOGGER.error(errorMessage);
            throw new MotechConfigurationException(errorMessage, e);
        }
    }
}
