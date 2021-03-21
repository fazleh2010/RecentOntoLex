/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.experiments.NullInterestingness;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.utils.CsvFile;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.PropertyCSV;
import citec.correlation.wikipedia.utils.StopWordRemoval;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;
import citec.correlation.wikipedia.experiments.PredictionRules;

/**
 *
 * @author elahi //BusinessPerson malformed CSV o
 */
public class GeneratedForP implements NullInterestingness {

    private static Logger LOGGER = null;
    public static String createLexicon = "createLexicon";
    static String createExperimentLine = "createExperimentLine";

    public GeneratedForP(String baseDir, String qald9Dir, String givenPrediction, String givenInterestingness, Map<String, ThresoldsExperiment> associationRulesExperiment, Logger givenLOGGER, String fileType, String creationType) throws Exception {
        // this.lemmatizer=lemmatizer;
        this.setUpLog(givenLOGGER);

        for (String prediction : PredictionRules.predictKBGivenLInguistic) {
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

                ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(rule);

                if (creationType.contains(createLexicon)) {
                    createLexicon(outputDir, prediction, rule, thresoldsExperiment);

                } else if (pair.getValue0() && creationType.contains(createExperimentLine)) {
                    rawFileDir = baseDir + prediction + "/";
                    pair = FileFolderUtils.getSpecificFiles(rawFileDir, ".csv");
                    if (pair.getValue0()) {
                        List<File> files = pair.getValue1();
                        createExperimentLinesCsv(outputDir, prediction, rule, files, thresoldsExperiment, creationType);
                    } else {
                        throw new Exception("NO files found for " + prediction + " " + rawFileDir);
                    }

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
    //outputDir, prediction, rule, pair.getValue1(), associationRulesExperiment, fileType,creationType
    private static void createEvalutionFiles(String outputDir, String prediction, String rule, List<File> files, Map<String, ThresoldsExperiment> associationRulesExperiment, String fileType, String creationType) throws Exception {
        ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(rule);
        Integer index = 0;
        //createLexiconCsv(outputDir, prediction, associationRule, files);

        createExperimentLinesCsv(outputDir, prediction, rule, files, thresoldsExperiment, creationType);

    }

    private static void createExperimentLinesCsv(String directory, String dbo_prediction, String interestingness, List<File> classFiles, ThresoldsExperiment thresoldsExperiment, String creationType) throws Exception {

        Map<String, ThresoldsExperiment.ThresoldELement> experimentThresolds = new TreeMap<String, ThresoldsExperiment.ThresoldELement>();
        List<String[]> rows = new ArrayList<String[]>();
        Integer numberOfClass = 0;
        //Integer maximumNumberOflines = 20000;
        Integer maximumNumberOflines = 300000;

        for (File classFile : classFiles) {
            List<String[]> csvData = new ArrayList<String[]>();
            String[] header = null;
            Map<String, List<String[]>> experimentLines = new TreeMap<String, List<String[]>>();

            String fileName = classFile.getName();

            //System.out.println("className:"+fileName);
            /* CsvFile experimentCsvFile=new CsvFile(classFile);
            rows =experimentCsvFile.getRows(classFile);*/
 /*if(!fileName.contains("AcademicJournal"))
                continue;*/
            CsvFile csvFile = new CsvFile(classFile);
            rows = csvFile.getRows(classFile, 1000.0, 300000);

            /*if(FileFolderUtils.isFileSizeManageable(classFile,40.0)){
                System.out.println( "..........."+classFile.getName());
            }
            else{
            CSVReader reader = new CSVReader(new FileReader(classFile));
            rows = reader.readAll(); 
            }*/
            PropertyCSV propertyCSV = null;
            numberOfClass = numberOfClass + 1;
            /*if(numberOfClass>=10)
                    break;*/
 /*if(numberOfClass>100)
                break;*/

            String className = classFile.getName().replace("http%3A%2F%2Fdbpedia.org%2Fontology%2F", "");
            LOGGER.log(Level.INFO, "interestingness:" + interestingness + " " + numberOfClass + "  className:" + className + " totalClasses:" + classFiles.size());

            /*if(!className.contains("Politician"))
                continue;*/
            if (classFile.getName().contains(PropertyCSV.localized)) {
                propertyCSV = new PropertyCSV(PropertyCSV.localized);
            } else {
                propertyCSV = new PropertyCSV(PropertyCSV.general);
            }
            Integer index = 0, rowCount = 0;
            for (String[] row : rows) {
                if (rowCount == 0) {
                    header = new String[row.length];
                    rowCount = rowCount + 1;
                    header = row;
                    continue;
                } else {
                    rowCount = rowCount + 1;
                }

                LineInfo lineInfo = new LineInfo(index, row, dbo_prediction, interestingness, propertyCSV, LOGGER);

                if (lineInfo.getLine() != null) {
                    if (lineInfo.getLine().contains("XMLSchema#integer")) {
                        continue;
                    }

                }
                if (rowCount > maximumNumberOflines) {
                    break;
                }

                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    ThresoldsExperiment.ThresoldELement thresoldELement = thresoldsExperiment.getThresoldELements().get(experiment);
                    experimentThresolds.put(experiment, thresoldELement);

                    if (!LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresoldELement.getGivenThresolds())) {
                        continue;
                    }
                    if (!lineInfo.getValidFlag()) {
                        continue;
                    }

                    // LOGGER.log(Level.INFO, " lineInfo::" + lineInfo);
                    List<String[]> lines = new ArrayList<String[]>();
                    if (experimentLines.containsKey(experiment)) {
                        lines = experimentLines.get(experiment);
                    }
                    lines.add(row);
                    experimentLines.put(experiment, lines);
                }

            }
            for (String experiment : experimentLines.keySet()) {
                ThresoldsExperiment.ThresoldELement thresoldELement = experimentThresolds.get(experiment);
                List<String[]> exRows = new ArrayList<String[]>();
                List<String[]> lines = experimentLines.get(experiment);
                //String experimentID = experiment + "-" + className + ".csv";
                String experimentID = experiment + ".csv";
                File file = new File(directory + "/" + experimentID);
                CsvFile CsvFile = new CsvFile(file);
                if (!file.exists()) {
                    exRows.add(header);
                    exRows.addAll(lines);
                    CsvFile.writeToCSV(exRows);
                } else {
                    List<String[]> previous = CsvFile.getRows(file);
                    previous.addAll(lines);
                    CsvFile.writeToCSV(previous);
                }
            }

        }


        /*for (String experiment : experimentLines.keySet()) {
            ThresoldsExperiment.ThresoldELement thresoldELement = experimentThresolds.get(experiment);
            Integer numOfRules = thresoldELement.getNumberOfRules();
            List<LineInfo> lineInfos = experimentLines.get(experiment);
            String experimentID = index + "-" + experiment;
            Lexicon lexicon = createLexicon(directory, dbo_prediction, interestingness, lineInfos, experimentID, numOfRules);
            LOGGER.log(Level.INFO, " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment + " numberOfRules:" + thresoldELement.getNumberOfRules());
            index = index + 1;
        }*/
    }

    private static void createLexicon(String directory, String dbo_prediction, String interestingness, ThresoldsExperiment thresoldsExperiment) throws Exception {

        Map<String, ThresoldsExperiment.ThresoldELement> experimentThresolds = new TreeMap<String, ThresoldsExperiment.ThresoldELement>();

        for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
            ThresoldsExperiment.ThresoldELement thresoldELement = thresoldsExperiment.getThresoldELements().get(experiment);
            experimentThresolds.put(experiment, thresoldELement);
        }
        Integer index = 0;
        for (String experiment : experimentThresolds.keySet()) {
            ThresoldsExperiment.ThresoldELement thresoldELement = experimentThresolds.get(experiment);
            Integer numOfRules = thresoldELement.getNumberOfRules();
            String experimentID = experiment + ".csv";
            Lexicon lexicon = createLexicon(directory, dbo_prediction, interestingness, experimentID, numOfRules);
            LOGGER.log(Level.INFO, " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment + " numberOfRules:" + thresoldELement.getNumberOfRules());
            index = index + 1;
        }
    }

    private static Lexicon createLexicon(String directory, String dbo_prediction, String interestingness, String experimentID, Integer numberOfRules) throws Exception {
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        List<String[]> rows = new ArrayList<String[]>();
        PropertyCSV propertyCSV = null;
        if (dbo_prediction.contains(PropertyCSV.localized)) {
            propertyCSV = new PropertyCSV(PropertyCSV.localized);
        } else {
            propertyCSV = new PropertyCSV(PropertyCSV.general);
        }

        File file = new File(directory + "/" + experimentID);
        CsvFile csvFile = new CsvFile(file);
        rows = csvFile.getRows(file);
        //rows = csvFile.getRows(file, 1000.0, 300000);
       

        Integer index = 0, rowCount = 0;
        for (String[] row : rows) {
            if (rowCount == 0) {
                rowCount = rowCount + 1;
                continue;
            } else {
                rowCount = rowCount + 1;
            }
            LineInfo lineInfo = new LineInfo(index, row, dbo_prediction, interestingness, propertyCSV, LOGGER);
            index = index + 1;

            if (index >= numberOfRules) {
                break;
            }
            if (!lineInfo.getValidFlag()) {
                continue;
            }

            String nGram = lineInfo.getWord();
            nGram = nGram.replace("\"", "");
            nGram = nGram.toLowerCase().trim().strip();
            nGram = nGram.replaceAll(" ", "_");
            nGram = StopWordRemoval.deleteStopWord(nGram);

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
    
    private static Lexicon createExperimentGrep(String directory, String dbo_prediction, String interestingness, String experimentID, Integer numberOfRules) throws Exception {
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        List<String[]> rows = new ArrayList<String[]>();
        PropertyCSV propertyCSV = null;
        if (dbo_prediction.contains(PropertyCSV.localized)) {
            propertyCSV = new PropertyCSV(PropertyCSV.localized);
        } else {
            propertyCSV = new PropertyCSV(PropertyCSV.general);
        }

        File file = new File(directory + "/" + experimentID);
        CsvFile csvFile = new CsvFile(file);
        rows = csvFile.getRows(file);

        Integer index = 0, rowCount = 0;
        for (String[] row : rows) {
            if (rowCount == 0) {
                rowCount = rowCount + 1;
                continue;
            } else {
                rowCount = rowCount + 1;
            }
            LineInfo lineInfo = new LineInfo(index, row, dbo_prediction, interestingness, propertyCSV, LOGGER);
            index = index + 1;

            if (index >= numberOfRules) {
                break;
            }
            if (!lineInfo.getValidFlag()) {
                continue;
            }

            String nGram = lineInfo.getWord();
            nGram = nGram.replace("\"", "");
            nGram = nGram.toLowerCase().trim().strip();
            nGram = nGram.replaceAll(" ", "_");
            nGram = StopWordRemoval.deleteStopWord(nGram);

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

    /*private static Lexicon createLexicon(String directory, String dbo_prediction, String interestingness, List<LineInfo> lineInfos, String experimentID, Integer numberOfRules) throws Exception {
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
    }*/

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
        String creationType = GeneratedExperimentData.createExperimentLine;

        Map<String, ThresoldsExperiment> associationRulesExperiment = new HashMap<String, ThresoldsExperiment>();

        List<String> predictLinguisticGivenKB = new ArrayList<String>(Arrays.asList(//predict_l_for_o_given_p
                //predict_l_for_s_given_po
                //predict_l_for_s_given_o
                //predict_l_for_o_given_p,
                //predict_l_for_o_given_s,
                //predict_l_for_s_given_p,
                //predict_localized_l_for_o_given_p
                //predict_l_for_o_given_sp
                //predict_localized_l_for_s_given_p 
                //predict_po_for_s_given_l
                //PredictionRules.predict_po_for_s_given_localized_l
                PredictionRules.predict_p_for_s_given_localized_l
                //PredictionRules.predict_p_for_o_given_localized_l
        ));
        List<String> interestingness = new ArrayList<String>();
        //interestingness.add(Cosine);
        
        //interestingness.add(AllConf);
         interestingness.add(Coherence);
         //interestingness.add(Kulczynski);
         //interestingness.add(IR);
         //interestingness.add(MaxConf);

        for (String prediction : predictLinguisticGivenKB) {
            if (prediction.equals(PredictionRules.predict_l_for_s_given_o)) {
                type = OBJECT;
            } else if (prediction.equals(PredictionRules.predict_po_for_s_given_l)
                    || prediction.equals(PredictionRules.predict_po_for_s_given_localized_l)
                    || prediction.equals(PredictionRules.predict_p_for_s_given_localized_l)
                    || prediction.equals(PredictionRules.predict_p_for_o_given_localized_l)) {
                type = PREDICATE;
            }
            for(String inter:interestingness){
            associationRulesExperiment = Evaluation.createExperiments(type);
            GeneratedExperimentData ProcessFile = new GeneratedExperimentData(baseDir, outputDir, prediction, inter, associationRulesExperiment, LOGGER, ".csv", creationType);
          
            }
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
