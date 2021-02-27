package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.PosAnalyzer;
import citec.correlation.wikipedia.analyzer.Lemmatizer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.GOLD;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import citec.correlation.wikipedia.analyzer.logging.LoggingExample;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.interestingness;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResultsHR;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.utils.CsvFile;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class Evaluation implements ThresoldConstants {

    private static String inputDir = dbpediaDir + "results/" + "new/";
    private static Set<String> classNames = new TreeSet<String>();
    private static Map<String, List<String>> rules = new HashMap<String, List<String>>();
    private static Map<String, Map<String, List<File>>> classRuleFiles = new TreeMap<String, Map<String, List<File>>>();
    private static Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();
    private static List<MeanReciprocalCalculation> adjectives = new ArrayList<MeanReciprocalCalculation>();
    private static List<MeanReciprocalCalculation> verbs = new ArrayList<MeanReciprocalCalculation>();
    private static List<MeanReciprocalCalculation> nouns = new ArrayList<MeanReciprocalCalculation>();

    private static Set<String> classFileNames = new HashSet<String>();
    private static String resources = "src/main/resources/";
    //private static Map<String, ThresoldsExperiment> allThresoldInterestingness = new TreeMap<String, ThresoldsExperiment>();

    private static Logger LOGGER = Logger.getLogger(Evaluation.class.getName());
    private static Lemmatizer lemmatizer = new Lemmatizer();

    public Evaluation() {
        classNames = getClassNames(inputDir);
        LOGGER.setLevel(Level.FINE);
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.setLevel(Level.CONFIG);
        LOGGER.setLevel(Level.FINE);
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.addHandler(new LogHandler());

        //LOGGER.log(Level.INFO, "generate experiments given thresolds");
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

    public static void calculateMeanReciprocal(String type, String givenPrediction, String givenInterestingness, String directory, String outputDir,Map<String, ThresoldsExperiment> allThresoldInterestingness) throws IOException, Exception {
        Handler fileHandler = new FileHandler(outputDir + givenPrediction + "-" + "logger.log");
        fileHandler.setFormatter(new LogFormatter());
        fileHandler.setFilter(new LogFilter());
        LOGGER.addHandler(fileHandler);

        for (String prediction : predictLinguisticGivenKB) {
            if (!prediction.contains(givenPrediction)) {
                continue;
            }
            Map<String, Map<String, Map<String, MeanReciprocalCalculation>>> ruleExpeResult = new TreeMap<String, Map<String, Map<String, MeanReciprocalCalculation>>>();
            for (String interestingness : allThresoldInterestingness.keySet()) {
                if (givenInterestingness != null) {
                    if (!interestingness.contains(givenInterestingness)) {
                        continue;
                    }
                }

                LOGGER.log(Level.CONFIG, "RULE ::" + prediction);
                LOGGER.log(Level.CONFIG, "INTERESTINGNESS::" + interestingness);

                ThresoldsExperiment thresoldsExperiment = allThresoldInterestingness.get(interestingness);
                Map<String, Map<String, MeanReciprocalCalculation>> expeResult = new TreeMap<String, Map<String, MeanReciprocalCalculation>>();

                /*adjectives = new ArrayList<MeanReciprocalCalculation>();
                verbs = new ArrayList<MeanReciprocalCalculation>();
                nouns = new ArrayList<MeanReciprocalCalculation>();*/
                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    
                /*if(!experiment.contains("numRule_10000-supA_100.0-supB_100.0-condAB_0.001-condBA_0.001-AllConf_0.001")){
                        continue;
                }*/
                        
                    List<File> expFileList = FileFolderUtils.getSpecificFiles(directory, interestingness, experiment, ".json").getValue1();
                    Map<String, MeanReciprocalCalculation> meanReciprocalsPos = meanReciprocalValues(prediction, interestingness, experiment, directory, expFileList);
                    if (!meanReciprocalsPos.isEmpty()) {
                        expeResult.put(experiment, meanReciprocalsPos);
                    }

                }

                ruleExpeResult.put(interestingness, expeResult);

            }
            String outputFileName = outputDir + "VB-NN-JJ-" + prediction + "MeanR" + ".csv";
            CsvFile csvFile = new CsvFile(new File(outputFileName), LOGGER);
            csvFile.createCsvExperimentData(type, ruleExpeResult);
            //FileFolderUtils.writeExperMeanResultsToJsonFile(expeResult, outputFileName);

        }
    }

    private static Map<String, MeanReciprocalCalculation> meanReciprocalValues(String predictionRule, String interestiness, String experiment, String directory, List<File> fileList) throws IOException {
        LOGGER.log(Level.INFO, "****************************Experiment START ************************************************************************");
        LOGGER.log(Level.INFO, "thresholds:: " + experiment);

        Map<String, MeanReciprocalCalculation> meanReciprocals = new TreeMap<String, MeanReciprocalCalculation>();
        for (String posTag : PosAnalyzer.POSTAGS) {
            String key = getInterestingnessThresold(experiment, interestiness) + "-" + posTag;
            MeanReciprocalCalculation meanReciprocalCalculation = null;

            /*if (!posTag.contains("NN")) {
                continue;
            }*/
            
            
            
            Pair<Boolean, File> pair = getFile(posTag, fileList);
            if (pair.getValue0()) {
                File file = pair.getValue1();
                String fileName = file.getName().replace(".json", "");
                String qaldFile = FileFolderUtils.getQaldCsvFile(qald9Dir + GOLD, OBJECT, posTag);
                CsvFile csvFile = new CsvFile(new File(qaldFile));
                csvFile.readQaldCsv(qaldFile);
                File conditionalFile = new File(directory + fileName + ".json");
                LOGGER.log(Level.INFO, "parts-of-speech:: " + posTag);
                LOGGER.log(Level.INFO, posTag + " File that contains all patterns within this thresholds:" + conditionalFile.getName());
                LOGGER.log(Level.INFO, "qald-9 file taken for evalution:" + csvFile.getFilename());
                Comparision comparision = new Comparision(lemmatizer, predictionRule, csvFile, conditionalFile, posTag, LOGGER, experiment, OBJECT);
                meanReciprocalCalculation = comparision.getMeanReciprocalResult();
            } else {
                meanReciprocalCalculation = new MeanReciprocalCalculation(experiment, LOGGER);
            }

            meanReciprocals.put(key, meanReciprocalCalculation);

            /*if (posTag.contains("JJ")) {
                    adjectives.add(meanReciprocalCalculation);
                }
                if (posTag.contains("VB")) {
                    verbs.add(meanReciprocalCalculation);
                }
                if (posTag.contains("NN")) {
                    nouns.add(meanReciprocalCalculation);
                }*/
            //String key=this.getInterestingnessThresold(experiment,interestiness);
            //String str=experiment.replace(interestiness, experiment);
            //System.out.println("experiment:" + experiment);
            //System.out.println("key:" + key);
            //LOGGER.log(Level.WARNING, "NO Lexicon FOUND for this thresolds:");
            //String key = getInterestingnessThresold(experiment, interestiness) + "-" + posTag;
            //mean }
        }

        LOGGER.log(Level.INFO, "**********************************************************************************************************************");

        return meanReciprocals;
    }

    public static String getInterestingnessThresold(String experiment, String interestingness) {
        String[] info = experiment.split("-");
        String str = null;
        for (Integer index = 0; info.length > index; index++) {
            str = info[index];
        }

        return str;
    }

    public static void setTopMeanReciprocal(String directory, String prediction, String associationRule) throws Exception {
        String outputFileName = null;
        if (!adjectives.isEmpty()) {
            Collections.sort(adjectives, new MeanReciprocalCalculation());
            Collections.reverse(adjectives);
            outputFileName = directory + associationRule + "-" + "JJ" + "-" + prediction + "-" + "MeanR" + ".json";
            FileFolderUtils.writeMeanSortToJsonFile(adjectives, outputFileName);
        } else if (!nouns.isEmpty()) {
            Collections.sort(nouns, new MeanReciprocalCalculation());
            Collections.reverse(nouns);
            outputFileName = directory + associationRule + "-" + "NN" + "-" + prediction + "-" + "MeanR" + ".json";
            FileFolderUtils.writeMeanSortToJsonFile(nouns, outputFileName);
        } else if (!verbs.isEmpty()) {
            Collections.sort(verbs, new MeanReciprocalCalculation());
            Collections.reverse(verbs);
            outputFileName = directory + associationRule + "-" + "VB-" + "-" + prediction + "-" + "MeanR" + ".json";
            FileFolderUtils.writeMeanSortToJsonFile(verbs, outputFileName);
        }

    }

    public static Map<String, ThresoldsExperiment> createExperiments(String type) throws Exception {
        Map<String, ThresoldsExperiment> associationRulesExperiment = new TreeMap<String, ThresoldsExperiment>();
        for (String associationRule : interestingness) {
            ThresoldsExperiment thresoldsExperiment = new ThresoldsExperiment(type, associationRule);
            associationRulesExperiment.put(associationRule, thresoldsExperiment);
        }
        return associationRulesExperiment;
    }

    public static boolean isKBValid(String word) {

        if (word.contains("#integer") || word.contains("#double")) {
            return true;
        }
        return false;
    }

    public static NewResultsHR readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        NewResultsHR result = mapper.readValue(file, NewResultsHR.class);
        return result;
    }

    private static Pair<Boolean, File> getFile(String posTag, List<File> fileList) {
        for (File file : fileList) {
            if (file.getName().contains(posTag)) {
                return new Pair<Boolean, File>(Boolean.TRUE, file);
            }
        }
        return new Pair<Boolean, File>(Boolean.FALSE, null);
    }

    private static Set<String> getClassNames(String inputDir) {
        Set<String> classNames = new TreeSet<String>();
        Pair<Boolean, List<File>> filesExistList = FileFolderUtils.getSpecificFiles(inputDir, Cosine, "json");
        if (filesExistList.getValue0()) {
            if (filesExistList.getValue0()) {
                List<File> fileList = filesExistList.getValue1();
                Map<String, List<File>> ruleFiles = new TreeMap<String, List<File>>();
                for (File file : fileList) {
                    classNames.add(getClassName(file.getName()));
                }
            }
        }
        return classNames;
    }

    private static String getClassName(String name) {
        name = name.replace("HR_", "");
        String[] info = name.split("-");
        return info[0].trim().strip();
    }

}
