/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.PosAnalyzer;
import citec.correlation.wikipedia.analyzer.Lemmatizer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.Cosine;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResultsMR;
import citec.correlation.wikipedia.results.Rule;
import citec.correlation.wikipedia.utils.CsvFile;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.PropertyCSV;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class GeneratedExperimentData_2 implements ThresoldConstants {

    private static Logger LOGGER = null;
    // private  static Lemmatizer lemmatizer=null;
    //the files are ready at /opt/rulepatterns/results
    //bzip2 -d filename.bz2
    //bzip2 -d *.json.bz2

    public GeneratedExperimentData_2(String baseDir, String qald9Dir, String givenPrediction, String givenInterestingness, Map<String, ThresoldsExperiment> associationRulesExperiment, Logger givenLOGGER, String fileType) throws Exception {
        // this.lemmatizer=lemmatizer;
        this.setUpLog(givenLOGGER);

        for (String prediction : predictLinguisticGivenKB) {
            String outputDir = qald9Dir + "/" + prediction + "/" + "dic";
            if (!prediction.equals(givenPrediction)) {
                continue;
            }
            for (String rule : interestingness) {
                String rawFileDir = null;
                Pair<Boolean, List<File>> pair = new Pair<Boolean, List<File>>(Boolean.TRUE, new ArrayList<File>());
                if (givenInterestingness != null) {
                    if (!rule.contains(givenInterestingness)) {
                        continue;
                    }
                }
              
                rawFileDir = baseDir + prediction + "/";
                pair = FileFolderUtils.getSpecificFiles(rawFileDir, ".csv");

                if (pair.getValue0()) {
                    createEvalutionFiles(outputDir, prediction, rule, pair.getValue1(), associationRulesExperiment, fileType);
                } else {
                    throw new Exception("NO files found for " + prediction + " " + rawFileDir);
                }
            }
        }
    }

   /* private static void createEvalutionFiles(String outputDir, String prediction, String associationRule, List<File> files, Map<String, ThresoldsExperiment> associationRulesExperiment, String fileType) throws Exception {
        ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(associationRule);
        Integer index = 0;
        for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
            index = index + 1;
            ThresoldsExperiment.ThresoldELement element = thresoldsExperiment.getThresoldELements().get(experiment);
            String experimentID = index + "-" + experiment;
            //LOGGER.log(Level.INFO, " element::" + element );
            Lexicon lexicon = createLexiconCsv(outputDir, prediction, associationRule, files, element, experimentID);
            LOGGER.log(Level.INFO, " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment);
            //System.out.println( outputDir + " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment);
            //break;
        }
    }*/
    
    private static void createEvalutionFiles(String outputDir, String prediction, String associationRule, List<File> files, Map<String, ThresoldsExperiment> associationRulesExperiment, String fileType) throws Exception {
        ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(associationRule);
        Integer index = 0;
        //createLexiconCsv(outputDir, prediction, associationRule, files);

       
       createLexiconCsv(outputDir, prediction, associationRule, files, thresoldsExperiment);

    }


    private static void createLexiconCsv(String directory, String dbo_prediction, String interestingness, List<File> classFiles, ThresoldsExperiment thresoldsExperiment) throws Exception {
       
        Map<String, List<LineInfo>> experimentLines = new TreeMap<String, List<LineInfo>>();
        Map<String, ThresoldsExperiment.ThresoldELement> experimentThresolds = new TreeMap<String,ThresoldsExperiment.ThresoldELement>();

        Integer numberOfClass=0;
       
        for (File classFile : classFiles) {
            String fileName = classFile.getName();
            CSVReader reader = new CSVReader(new FileReader(classFile));
            List<String[]> rows = reader.readAll();
            PropertyCSV propertyCSV = null;
            numberOfClass=numberOfClass+1;
            /*if(numberOfClass>100)
                break;*/
            String className=classFile.getName().replace("rules-predict_localized_l_for_s_given_p-http%3A%2F%2Fdbpedia.org%2Fontology%2F", "");
            
            /*if(!className.contains("Politician"))
                continue;*/
            LOGGER.log(Level.INFO, numberOfClass+"  className:" + className);
                
            if (classFile.getName().contains(PropertyCSV.localized)) {
                propertyCSV = new PropertyCSV(PropertyCSV.localized);
            } else {
                propertyCSV = new PropertyCSV(PropertyCSV.general);
            }
            Integer index = 0, rowCount = 0;
            for (String[] row : rows) {
                if (rowCount == 0) {
                    rowCount = rowCount + 1;
                    continue;
                }

                LineInfo lineInfo = new LineInfo(index, row, dbo_prediction, interestingness, propertyCSV, LOGGER);
                

                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    ThresoldsExperiment.ThresoldELement thresoldELement = thresoldsExperiment.getThresoldELements().get(experiment);
                    experimentThresolds.put(experiment, thresoldELement);
                    
                    if (!LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresoldELement.getGivenThresolds())) {
                       continue;
                    }
                    //LOGGER.log(Level.INFO, " lineInfo::" + lineInfo);

                        List<LineInfo> lines = new ArrayList<LineInfo>();
                        if (experimentLines.containsKey(experiment)) {
                            lines=experimentLines.get(experiment);
                        }
                        lines.add(lineInfo);
                        experimentLines.put(experiment, lines);
                   
                }
               

            }
            
        }
        Integer index=0;
        
        for (String experiment : experimentLines.keySet()) {
            ThresoldsExperiment.ThresoldELement thresoldELement = experimentThresolds.get(experiment);
            Integer numOfRules = thresoldELement.getNumberOfRules();
            List<LineInfo> lineInfos = experimentLines.get(experiment);
            String experimentID = index + "-" + experiment;
            Lexicon lexicon = createLexicon(directory, dbo_prediction, interestingness, lineInfos, experimentID, numOfRules);
            LOGGER.log(Level.INFO, " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment+" numberOfRules:"+thresoldELement.getNumberOfRules());
            index=index+1;
        }

    }
    
    private static Lexicon createLexicon(String directory, String dbo_prediction, String interestingness, List<LineInfo> lineInfos, String experimentID, Integer numberOfRules) throws Exception {
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();

        Integer index = 0;
        for (LineInfo lineInfo : lineInfos) {
            if (index >= numberOfRules) {
                break;
            }
            if (!lineInfo.getValidFlag()) {
                continue;
            }
            String nGram = lineInfo.getWord();
            nGram = nGram.replaceAll("[^a-zA-Z0-9]", " ");
            nGram = nGram.toLowerCase().trim().strip();
            nGram = nGram.replaceAll(" ", "_");

            List<LineInfo> results = new ArrayList<LineInfo>();
            if (lineLexicon.containsKey(nGram)) {
                results = lineLexicon.get(nGram);
                results.add(lineInfo);
                lineLexicon.put(nGram, results);
            } else {
                results.add(lineInfo);
                lineLexicon.put(nGram, results);

            }
        }
        Lexicon lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(dbo_prediction, directory, experimentID, interestingness, lineLexicon);
        return lexicon;
    }
    
       /*private static Lexicon createLexiconCsv(String directory, String dbo_prediction, String interestingness, List<File> classFiles, ThresoldsExperiment.ThresoldELement thresoldELement, String experimentID) throws Exception {
       
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        Integer numberOfRules = thresoldELement.getNumberOfRules();
       
        for (File classFile : classFiles) {
            String fileName = classFile.getName();
            CSVReader reader = new CSVReader(new FileReader(classFile));
            List<String[]> rows = reader.readAll();
            PropertyCSV propertyCSV = null;
            //LOGGER.log(Level.INFO, "file.getName()::" + classFile.getName());
            if (classFile.getName().contains(PropertyCSV.localized)) {
                propertyCSV = new PropertyCSV(PropertyCSV.localized);
            } else {
                propertyCSV = new PropertyCSV(PropertyCSV.general);
            }
            Integer index = 0,rowCount=0;
            for (String[] row : rows) {
                if (rowCount == 0) {
                    rowCount = rowCount + 1;
                    continue;
                } 
                
               LineInfo lineInfo = new LineInfo(index, row, dbo_prediction, interestingness, propertyCSV, LOGGER);
              // LOGGER.log(Level.INFO, " lineInfo::" + lineInfo );

      
                if (index >= numberOfRules) {
                    break;
                }
                if (!lineInfo.getValidFlag()) {
                    continue;
                }

                String nGram = lineInfo.getWord();
                nGram = nGram.replaceAll("[^a-zA-Z0-9]", " ");
                nGram = nGram.toLowerCase().trim().strip();
                nGram = nGram.replaceAll(" ", "_");

                List<LineInfo> results = new ArrayList<LineInfo>();
                if (lineLexicon.containsKey(nGram)) {
                    results = lineLexicon.get(nGram);
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                } else {
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                }
               index = index + 1;

            }
           
        }

        Lexicon lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(dbo_prediction, directory, experimentID, interestingness, lineLexicon);
        return lexicon;
    }*/

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

    private void setUpLog(Logger givenLOGGER) {
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
    }

    public static boolean isKBValid(String word) {

        if (word.contains("#integer") || word.contains("#double")) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
      //create experiments
      createExperiments();
      
    }
    
      public static void createExperiments() throws Exception {
        String rawFileDir = null;
        String directory = qald9Dir + OBJECT + "/";
        String baseDir = "/home/elahi/new/dbpediaFiles/unlimited/unlimited/";
        Logger LOGGER = Logger.getLogger(GeneratedExperimentData.class.getName());
        String outputDir = qald9Dir;
        String type = null;
        Map<String, ThresoldsExperiment> associationRulesExperiment = new HashMap<String, ThresoldsExperiment>();

        List<String> predictLinguisticGivenKB = new ArrayList<String>(Arrays.asList(
                //predict_l_for_o_given_p
                //predict_l_for_s_given_po
                predict_l_for_s_given_o
                //predict_l_for_o_given_p,
                //predict_l_for_o_given_s,
                //predict_l_for_o_given_sp
               // predict_localized_l_for_s_given_p
        ));
        List<String> interestingness = new ArrayList<String>();
        interestingness.add(ThresoldConstants.Cosine);
        interestingness.add(ThresoldConstants.Coherence);
        interestingness.add(ThresoldConstants.AllConf);
        interestingness.add(ThresoldConstants.MaxConf);
        interestingness.add(ThresoldConstants.Kulczynski);
        interestingness.add(ThresoldConstants.IR);

        for (String prediction : predictLinguisticGivenKB) {
            if (prediction.equals(predict_l_for_s_given_po)
                    || prediction.equals(predict_l_for_s_given_o)) {
                type = ThresoldConstants.OBJECT;
            } else if (prediction.contains(predict_l_for_o_given_p) 
                    || prediction.contains(predict_localized_l_for_s_given_p)) {
                type = ThresoldConstants.PREDICATE;
            }
            associationRulesExperiment = Evaluation.createExperiments(type);
            //or (String rule : interestingness) {
            GeneratedExperimentData ProcessFile = new GeneratedExperimentData(baseDir, outputDir, prediction, null, associationRulesExperiment, LOGGER, ".csv");

            //}
        }
    }
    

    /*if (!LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresoldELement.getGivenThresolds())) {
                    continue;
                }
                if (!lineInfo.getValidFlag()) {
                    continue;
                }
                String word = lineInfo.getWord();
                if (FormatAndMatch.isNumeric(lineInfo.getWord())) {
                    continue;
                }
                if (isKBValid(lineInfo.getObject())) {
                    continue;
                }*/
}
