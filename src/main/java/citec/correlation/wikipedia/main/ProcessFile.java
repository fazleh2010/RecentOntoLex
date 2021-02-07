/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.parameters.ThresoldConstants;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.AllConf;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.Cosine;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.predict_l_for_o_given_p;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.predict_l_for_s_given_po;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.results.MR;
import citec.correlation.wikipedia.results.NewResultsHR;
import citec.correlation.wikipedia.results.NewResultsMR;
import citec.correlation.wikipedia.results.Rule;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class ProcessFile implements ThresoldConstants {

    private static Logger LOGGER = null;

    public ProcessFile(String baseDir, String givenPrediction,String givenInterestingness,Logger givenLOGGER) throws Exception {
        LOGGER = givenLOGGER;
        LOGGER.setLevel(Level.FINE);
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.setLevel(Level.CONFIG);
        LOGGER.setLevel(Level.FINE);
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.addHandler(new LogHandler());
        LOGGER.log(Level.INFO, "generate experiments given thresolds");
        try {
            //Handler fileHandler = new FileHandler(resourceDir + "logger.log", 2000, 1000);
            Handler fileHandler = new FileHandler(resourceDir + "logger.log");

            fileHandler.setFormatter(new LogFormatter());
            fileHandler.setFilter(new LogFilter());
            LOGGER.addHandler(fileHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        //String rawFileDir = null;
        //String directory = qald9Dir + OBJECT + "/";
        //rawFileDir = dbpediaDir + "results/" + "new/MR/";
        //String baseDir = "/home/elahi/dbpediaFiles/unlimited/unlimited/";

        for (String prediction : predictionLinguisticRules) {
            String rawFileDir = baseDir + prediction + "/";
            if (!prediction.contains(givenPrediction)) {
                continue;
            }
            for (String rule : interestingness) {
                if (!rule.contains(givenInterestingness)) {
                    continue;
                }
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(rawFileDir, rule, ".json");
                if (pair.getValue0()) {
                    NewResultsMR NewResultsMR = readFromJsonFile(pair.getValue1());
                }

            }

        }

    }

    private static NewResultsMR readFromJsonFile(List<File> files) throws IOException, Exception {
        Map<String, List<Rule>> classDistributions = new TreeMap<String, List<Rule>>();
        Discription description = null;
        Integer totalFiles = files.size();
        Integer index = 0;
        for (File file : files) {
            index = index + 1;
            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];

            LOGGER.log(Level.INFO, "now processing index:" + index + " total:" + totalFiles + "  file:" + fileName);
            ObjectMapper mapper = new ObjectMapper();
            NewResultsMR resultTemp = mapper.readValue(file, NewResultsMR.class);
            description = resultTemp.getDescription();
            List<Rule> local = resultTemp.getDistributions();
            classDistributions.put(parameters[0], local);
        }
        return new NewResultsMR(description, classDistributions);
    }

    private static String[] findParameter(String[] info) {
        String[] parameters = new String[3];
        for (Integer index = 0; index < info.length; index++) {
            if (index == 0) {
                parameters[index] = info[index];
            }
            if (index == 1) {
                parameters[index] = info[index];
            } else if (index == 2) {
                parameters[index] = info[index];
            }
        }
        return parameters;
    }

    public static void main(String[] args) throws Exception {
        String rawFileDir = null;
        String directory = qald9Dir + OBJECT + "/";
        //rawFileDir = dbpediaDir + "results/" + "new/MR/";
        String baseDir = "/home/elahi/dbpediaFiles/unlimited/unlimited/";
        Logger LOGGER = Logger.getLogger(ProcessFile.class.getName());
        String prediction = predict_l_for_s_given_po;
        String associationRule = Coherence;

        ProcessFile ProcessFile = new ProcessFile(baseDir, prediction, associationRule, LOGGER);

        /*for (String prediction : predictionLinguisticRules) {
            rawFileDir = baseDir + prediction + "/";
            if (!prediction.contains(predict_l_for_s_given_po)) {
                continue;
            }
            for (String rule : interestingness) {
                if (!rule.contains(Coherence)) {
                    continue;
                }
                rawFileDir = baseDir + prediction + "/";
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(rawFileDir, rule, ".json");
                if (pair.getValue0()) {
                    NewResultsMR NewResultsMR = readFromJsonFile(pair.getValue1());
                }

            }

        }*/
    }

    /*public static void readMR(List<File> files) throws IOException, Exception {
        for (File file : files) {
            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
            System.out.println("parameters[0]:" + parameters[0]);

            ObjectMapper mapper = new ObjectMapper();
            MR resultTemp = mapper.readValue(file, MR.class);
            break;
        }
    }*/
}
