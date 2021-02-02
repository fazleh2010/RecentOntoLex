/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.analyzer.logging;

import citec.correlation.wikipedia.parameters.DirectoryLocation;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author elahi https://www.journaldev.com/977/logger-in-java-logging-example
 */


public class LoggingExample implements DirectoryLocation {

    static Logger LOGGER = Logger.getLogger(LoggingExample.class.getName());

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(resourceDir + "mylogging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
        LOGGER.setLevel(Level.FINE);
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.setLevel(Level.CONFIG);
        LOGGER.setLevel(Level.FINE);
        LOGGER.addHandler(new ConsoleHandler());
        //adding custom handler
        LOGGER.addHandler(new LogHandler());
        try {
            //FileHandler file name with max size and number of log files limit
            Handler fileHandler = new FileHandler(resourceDir + "logger.log", 2000, 1000);
            fileHandler.setFormatter(new LogFormatter());
            //setting custom filter for FileHandler
            fileHandler.setFilter(new LogFilter());
            LOGGER.addHandler(fileHandler);

            LOGGER.log(Level.SEVERE, "SEVERE  SEVERE  SEVERE  SEVERE  SEVERE");
            LOGGER.log(Level.WARNING, "WARNING  WARNING  WARNING  WARNING  WARNING");
            LOGGER.log(Level.CONFIG, "CONFIG  CONFIG  CONFIG  CONFIG  CONFIG");
            LOGGER.log(Level.FINE, "FINE  FINE  FINE  FINE  FINE");
            LOGGER.log(Level.FINER, "FINER  FINER  FINER  FINER  FINER");
            LOGGER.info("Communication error");

            LOGGER.log(Level.CONFIG, "Config data Config data Config data Config data");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

}
