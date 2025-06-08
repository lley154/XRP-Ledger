package com.easya.assetmanager.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing application configuration properties.
 */
public class AppConfig {
  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  private static final Properties properties = new Properties();
  private static final String CONFIG_FILE = "application.properties";

  static {
    try {
      // Get the current thread's context classloader
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      logger.debug("Context ClassLoader: {}", contextClassLoader);

      // Try to load from the context classloader (this will find test resources when
      // running tests)
      InputStream input = contextClassLoader.getResourceAsStream(CONFIG_FILE);
      if (input != null) {
        logger.debug("Found properties file in context classloader: {}",
            contextClassLoader.getResource(CONFIG_FILE));
        properties.load(input);
        input.close();
      } else {
        logger.debug("Properties file not found in context classloader, trying system classloader");
        // If not found in context classloader, try the system classloader
        input = ClassLoader.getSystemClassLoader().getResourceAsStream(CONFIG_FILE);
        if (input != null) {
          logger.debug("Found properties file in system classloader: {}",
              ClassLoader.getSystemClassLoader().getResource(CONFIG_FILE));
          properties.load(input);
          input.close();
        } else {
          logger.error(
              "Properties file not found in either classloader. Context ClassLoader: {}, "
              + "System ClassLoader: {}",
              contextClassLoader, ClassLoader.getSystemClassLoader());
          throw new RuntimeException("Unable to find " + CONFIG_FILE + " in classpath");
        }
      }

      // Log all loaded properties (excluding sensitive values)
      properties.forEach((key, value) -> {
        if (key.toString().contains("key") || key.toString().contains("secret")) {
          logger.debug("Loaded property: {} = [REDACTED]", key);
        } else {
          logger.debug("Loaded property: {} = {}", key, value);
        }
      });

    } catch (IOException e) {
      logger.error("Error loading properties file", e);
      throw new RuntimeException("Error loading " + CONFIG_FILE, e);
    }
  }

  /**
   * Get a string property value.
   * 
   * @param key The property key
   * 
   * @return The property value
   */
  public static String getProperty(String key) {
    String value = properties.getProperty(key);
    if (value == null) {
      logger.warn("Property {} not found in configuration", key);
    }
    return value;
  }

  /**
   * Get a string property value with a default value if not found.
   * 
   * @param key          The property key
   * 
   * @param defaultValue The default value to return if key is not found
   * 
   * @return The property value or default value
   */
  public static String getProperty(String key, String defaultValue) {
    String value = properties.getProperty(key, defaultValue);
    if (value.equals(defaultValue)) {
      logger.debug("Using default value for property {}: {}", key, defaultValue);
    }
    return value;
  }
}