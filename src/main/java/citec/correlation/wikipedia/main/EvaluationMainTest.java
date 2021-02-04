package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.Analyzer;
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
import citec.correlation.wikipedia.parameters.ThresoldConstants;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.interestingness;
import citec.correlation.wikipedia.parameters.ThresoldsExperiment;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResultsHR;
import citec.correlation.wikipedia.results.Discription;
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
public class EvaluationMainTest implements ThresoldConstants {

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
    private static String stanfordModelFile = resources + "stanford-postagger-2015-12-09/models/english-left3words-distsim.tagger";
    private static MaxentTagger taggerModel = new MaxentTagger(stanfordModelFile);
    private static Map<String, ThresoldsExperiment> allInterestingness = new TreeMap<String, ThresoldsExperiment>();

    private static Logger LOGGER = Logger.getLogger(EvaluationMainTest.class.getName());

    public EvaluationMainTest() {
        classNames = getClassNames(inputDir);
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
        try {
            this.allInterestingness = createExperiments();
            LOGGER.log(Level.INFO, "successfully generated experiments for given thresolds");
        } catch (Exception ex) {
            LOGGER.log(Level.CONFIG, "generate experiments given thresolds is failed!!");
        }
    }

    public static void main(String[] args) throws Exception {
        Integer createFiles=1;
        Integer evaluate=2;
        Integer menu=2;
        
        String directory = qald9Dir + OBJECT + "/";
        String inputDir = dbpediaDir + "results/" + "new/test/";
        EvaluationMainTest evaluationMainTest = new EvaluationMainTest();

        String predict_l_for_s_given_po_dic = "/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/dic/";
        String predict_l_for_s_given_po_meanR = "/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/meanR/";
        //run it once. we dont need to run it very time..
        Map<String, ThresoldsExperiment> associationRulesExperiment = createExperiments();

        File files = new File(predict_l_for_s_given_po_dic);
        boolean exists = files.exists();
        if (exists) {
            System.out.println("directory  exists!!");
        } else {
            FileFolderUtils.createDirectory(predict_l_for_s_given_po_dic);
        }
     
        
        if(menu==createFiles)
           createEvalutionFiles(inputDir,predict_l_for_s_given_po_dic, associationRulesExperiment);
        else if(menu==evaluate)
           calculateMeanReciprocal(predict_l_for_s_given_po_dic, predict_l_for_s_given_po_meanR);

        //Calculate mean reciprocal 
        //calculateMeanReciprocal(predict_l_for_s_given_po_dic, predict_l_for_s_given_po_meanR);
    }

    public static void calculateMeanReciprocal(String directory, String outputDir) throws IOException, Exception {
        for (String prediction : predicateRules) {
            if (!prediction.contains(predict_l_for_s_given_po)) {
                continue;
            }
            for (String interestingness : allInterestingness.keySet()) {
                if (!interestingness.contains(ThresoldConstants.Cosine)) {
                    continue;
                }
                LOGGER.log(Level.INFO, "evalution for association rule ::" + prediction);
                LOGGER.log(Level.INFO, "and interesingness measure::" + interestingness);

                ThresoldsExperiment thresoldsExperiment = allInterestingness.get(interestingness);
                Map<String, Map<String, MeanReciprocalCalculation>> expeResult = new TreeMap<String, Map<String, MeanReciprocalCalculation>>();

                adjectives = new ArrayList<MeanReciprocalCalculation>();
                verbs = new ArrayList<MeanReciprocalCalculation>();
                nouns = new ArrayList<MeanReciprocalCalculation>();

                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    List<File> expFileList = FileFolderUtils.getSpecificFiles(directory, interestingness, experiment, ".json").getValue1();

                    Map<String, MeanReciprocalCalculation> meanReciprocalsPos = meanReciprocalValues(experiment, directory, expFileList);
                    if (!meanReciprocalsPos.isEmpty()) {
                        expeResult.put(experiment, meanReciprocalsPos);
                    }
                }
                setTopMeanReciprocal(outputDir, prediction, interestingness);
                String outputFileName = outputDir + interestingness + "-VB-NN-JJ-" + prediction + "MeanR" + ".json";
                FileFolderUtils.writeExperMeanResultsToJsonFile(expeResult, outputFileName);
            }

        }
    }

    private static Map<String, MeanReciprocalCalculation> meanReciprocalValues(String experiment, String directory, List<File> fileList) throws IOException {
        Map<String, MeanReciprocalCalculation> meanReciprocals = new TreeMap<String, MeanReciprocalCalculation>();
        for (String posTag : Analyzer.POSTAGS) {
            try {
                File file = getFile(posTag, fileList);
                String fileName = file.getName().replace(".json", "");
                File qaldFile = FileFolderUtils.getQaldFileObject(qald9Dir + GOLD, OBJECT, posTag);
                File conditionalFile = new File(directory + fileName + ".json");
                //LOGGER.log(Level.INFO, "evaluate for part-of-speech::" + posTag);
                //LOGGER.log(Level.INFO, "qald-9 file this parts-of-speech::" + qaldFile.getName());
                LOGGER.log(Level.INFO, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
                LOGGER.log(Level.INFO, "calculating mean reciprocal for " + experiment);
                LOGGER.log(Level.INFO, "take lexicon seperated by parts of speech: " + conditionalFile.getName());
                LOGGER.log(Level.INFO, "take corresponsding qald-9 file " + qaldFile.getName());
                LOGGER.log(Level.INFO, "parts-of-speech: " + posTag);

                Comparision comparision = new Comparision(qaldFile, conditionalFile, posTag, LOGGER);
                comparision.compersionsPattern(experiment);
                MeanReciprocalCalculation meanReciprocalCalculation = comparision.getMeanReciprocalResult();
                if (posTag.contains("JJ")) {
                    adjectives.add(meanReciprocalCalculation);
                }
                if (posTag.contains("VB")) {
                    verbs.add(meanReciprocalCalculation);
                }
                if (posTag.contains("NN")) {
                    nouns.add(meanReciprocalCalculation);
                }

                meanReciprocals.put(posTag, comparision.getMeanReciprocalResult());

            } catch (Exception exp) {
                System.out.println("File not found!!");
            }
        }
        return meanReciprocals;
    }

    public static void setTopMeanReciprocal(String directory, String prediction, String associationRule) throws Exception {
        Collections.sort(adjectives, new MeanReciprocalCalculation());
        Collections.reverse(adjectives);
        Collections.sort(nouns, new MeanReciprocalCalculation());
        Collections.reverse(nouns);
        Collections.sort(verbs, new MeanReciprocalCalculation());
        Collections.reverse(verbs);
        String outputFileName = directory + associationRule + "-" + "NN" + "-" + prediction + "-" + "MeanR" + ".json";
        FileFolderUtils.writeMeanSortToJsonFile(nouns, outputFileName);
        outputFileName = directory + associationRule + "-" + "JJ" + "-" + prediction + "-" + "MeanR" + ".json";
        FileFolderUtils.writeMeanSortToJsonFile(adjectives, outputFileName);
        outputFileName = directory + associationRule + "-" + "VB-" + "-" + prediction + "-" + "MeanR" + ".json";
        FileFolderUtils.writeMeanSortToJsonFile(verbs, outputFileName);

    }

    private static Map<String, ThresoldsExperiment> createExperiments() throws Exception {
        Map<String, ThresoldsExperiment> associationRulesExperiment = new TreeMap<String, ThresoldsExperiment>();
        for (String associationRule : interestingness) {
            ThresoldsExperiment thresold = new ThresoldsExperiment(associationRule);
            associationRulesExperiment.put(associationRule, thresold);
        }
        return associationRulesExperiment;
    }

    private static void createEvalutionFiles(String rawFileDir,String classDir, Map<String, ThresoldsExperiment> associationRulesExperiment) throws Exception {
        Map<String, Lexicon> associationRuleLex = new TreeMap<String, Lexicon>();
        for (String prediction : predicateRules) {
            if (prediction.contains(predict_l_for_s_given_po)) {
                Lexicon lexicon = null;
                for (String associationRule : interestingness) {
                    Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(rawFileDir, prediction, associationRule, "json");
                    List<File> files = pair.getValue1();
                    //System.out.println("files:" + files);
                    NewResultsHR allClassLines = readFromJsonFile(files);
                    ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(associationRule);
                    Integer index = 0;
                    for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                        index = index + 1;
                        ThresoldsExperiment.ThresoldELement element = thresoldsExperiment.getThresoldELements().get(experiment);
                        String experimentID = index + "-" + experiment;
                        lexicon = createLexicon(classDir, "AllClass", prediction, associationRule, allClassLines, element, experimentID);
                        System.out.println(classDir + " index" + index + " total:" + thresoldsExperiment.getThresoldELements().size() + " " + experiment);
                    }
                    //associationRuleLex.put(prediction + "-" + associationRule, lexicon);
                }
            }
            //meanReciprocal(directory, associationRuleLex,associationRulesExperiment);
        }

    }

    private static Lexicon createLexicon(String directory, String dbo_className, String dbo_prediction, String dbo_associationRule, NewResultsHR result, ThresoldsExperiment.ThresoldELement thresoldELement, String experimentID) throws Exception {
        String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
        Analyzer analyzer = null;
        Lexicon lexicon = null;

        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        for (String className : result.getClassDistributions().keySet()) {
            List<String> lines = result.getClassDistributions().get(className);
            for (String line : lines) {
                LineInfo lineInfo = new LineInfo(className, line, 1, 0);
                if (!LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresoldELement.getGivenThresolds())) {
                    continue;
                }
                if (!lineInfo.getValidFlag()) {
                    continue;
                }
                String word = lineInfo.getWord();
                //System.out.println("word:" + word);

                if (FormatAndMatch.isNumeric(lineInfo.getWord())) {
                    continue;
                }
                if (isKBValid(lineInfo.getObject())) {
                    continue;
                }
                String nGram = lineInfo.getWord().toLowerCase().trim().strip();
                //System.out.println("parts-of-sppech:" + lineInfo.getPartOfSpeech());
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
        }
        lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(directory, experimentID, dbo_associationRule, lineLexicon);
        /* System.out.println("-" + experimentID + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("JJ:" + lexicon.getLexiconPosTaggged().get(Analyzer.ADJECTIVE).size());
        System.out.println("NN:" + lexicon.getLexiconPosTaggged().get(Analyzer.NOUN).size());
        System.out.println("VB" + lexicon.getLexiconPosTaggged().get(Analyzer.VERB).size());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");*/
        return lexicon;
    }

    public static boolean isKBValid(String word) {

        if (word.contains("#integer") || word.contains("#double")) {
            return true;
        }
        return false;
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

    public static NewResultsHR readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        NewResultsHR result = mapper.readValue(file, NewResultsHR.class);
        return result;
    }

    public static NewResultsHR readFromJsonFile(List<File> files) throws IOException, Exception {
        Map<String, List<String>> classDistributions = new TreeMap<String, List<String>>();
        Discription description = null;
        for (File file : files) {

            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
            System.out.println("parameters[0]:" + parameters[0]);

            ObjectMapper mapper = new ObjectMapper();
            NewResultsHR resultTemp = mapper.readValue(file, NewResultsHR.class);
            description = resultTemp.getDescription();
            List<String> local = resultTemp.getDistributions();
            classDistributions.put(parameters[0], local);
        }
        return new NewResultsHR(description, classDistributions);
    }

    private static File getFile(String posTag, List<File> fileList) {
        for (File file : fileList) {
            if (file.getName().contains(posTag)) {
                return file;
            }
        }
        return null;
    }

    private static boolean isValidFile(String fileName, String predict_l_for_s_given_po, String Cosine) {
        if (!fileName.contains(Cosine)
                || !fileName.contains(predict_l_for_s_given_po)) {
            return true;
        }
        return false;
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

}
