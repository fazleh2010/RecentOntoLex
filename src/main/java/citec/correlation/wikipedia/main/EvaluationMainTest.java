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
import citec.correlation.wikipedia.results.NewResults;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.IOException;
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

    private static Logger LOGGER = Logger.getLogger(LoggingExample.class.getName());

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
            Handler fileHandler = new FileHandler(resourceDir + "logger.log", 2000, 1000);
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
        String directory = qald9Dir + OBJECT + "/";
        EvaluationMainTest evaluationMainTest = new EvaluationMainTest();
        String predictionDir="1";
        String dic="dic";
        directory="/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/dic/";
        String outputDir="/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/meanR/";
        //run it once. we dont need to run it very time..
        /*for(String className:classNames) {
           
             FileFolderUtils.createDirectory(directory+predict_l_for_s_given_po+"_"+className);
             String classDir=directory+predict_l_for_s_given_po+"_"+className;
             createEvalutionFiles(classDir,associationRulesExperiment,className);
        }*/

        //Calculate mean reciprocal 
        calculateMeanReciprocal(directory,outputDir);

        /*Integer index = 0;
        for (String prediction : predicateRules) {
             if(!prediction.contains(predict_l_for_s_given_po))
                continue;
            for (String associationRule : associationRulesExperiment.keySet()) {
                associationRule = ThresoldConstants.Cosine;
                ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(associationRule);
                Map<String, Map<String, MeanReciprocalCalculation>> expeResult = new TreeMap<String, Map<String, MeanReciprocalCalculation>>();

                adjectives = new ArrayList<MeanReciprocalCalculation>();
                verbs = new ArrayList<MeanReciprocalCalculation>();
                nouns = new ArrayList<MeanReciprocalCalculation>();

                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    List<File> expFileList = FileFolderUtils.getSpecificFiles(directory, associationRule, experiment, ".json").getValue1();
                    index = index + 1;
                    ThresoldsExperiment.ThresoldELement thresoldELement = thresoldsExperiment.getThresoldELements().get(experiment);
                    Map<String, MeanReciprocalCalculation> meanReciprocalsPos = meanReciprocalValues(experiment, directory, expFileList);
                    if (!meanReciprocalsPos.isEmpty()) {
                        expeResult.put(experiment, meanReciprocalsPos);
                    }
                }
                setTopMeanReciprocal(directory, prediction, associationRule);
                String outputFileName = directory + associationRule + "-" + prediction + "-NN-JJ-VB-" + "MeanR" + ".json";
                FileFolderUtils.writeExperMeanResultsToJsonFile(expeResult, outputFileName);
                break;
            }
            break;
        }*/
    }

    public static void calculateMeanReciprocal(String directory,String outputDir) throws IOException, Exception {
        //Integer index = 0;
        for (String prediction : predicateRules) {
            if (!prediction.contains(predict_l_for_s_given_po)) {
                continue;
            }
            for (String interestingness : allInterestingness.keySet()) {
                if (!interestingness.contains(ThresoldConstants.Cosine)) {
                    continue;
                }
                LOGGER.log(Level.INFO, "runnng for rule ::" + prediction);
                LOGGER.log(Level.INFO, "runnng for interesingness ::" + interestingness);

                ThresoldsExperiment thresoldsExperiment = allInterestingness.get(interestingness);
                Map<String, Map<String, MeanReciprocalCalculation>> expeResult = new TreeMap<String, Map<String, MeanReciprocalCalculation>>();

                adjectives = new ArrayList<MeanReciprocalCalculation>();
                verbs = new ArrayList<MeanReciprocalCalculation>();
                nouns = new ArrayList<MeanReciprocalCalculation>();

                for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
                    List<File> expFileList = FileFolderUtils.getSpecificFiles(directory, interestingness, experiment, ".json").getValue1();
                    LOGGER.log(Level.INFO, "now running experiment ::" + experiment);
                    LOGGER.log(Level.INFO, "file found for this experiment ::" + expFileList.size());
                    Map<String, MeanReciprocalCalculation> meanReciprocalsPos = meanReciprocalValues(experiment, directory, expFileList);
                    if (!meanReciprocalsPos.isEmpty()) {
                        expeResult.put(experiment, meanReciprocalsPos);
                    }
                }
                setTopMeanReciprocal(outputDir, prediction, interestingness);
                String outputFileName = outputDir +interestingness+ "-NN-JJ-VB-" + prediction + "MeanR" + ".json";
                FileFolderUtils.writeExperMeanResultsToJsonFile(expeResult, outputFileName);
            }
            break;

        }
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

    private static void createEvalutionFiles(String classDir, Map<String, ThresoldsExperiment> associationRulesExperiment, String className) throws Exception {
        Map<String, Lexicon> associationRuleLex = new TreeMap<String, Lexicon>();
        for (String prediction : predicateRules) {
            if (prediction.contains(predict_l_for_s_given_po)) {
                Lexicon lexicon = null;
                for (String associationRule : interestingness) {
                    Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, prediction, associationRule, "json");
                    List<File> files = pair.getValue1();
                    //System.out.println("files:" + files);
                    NewResults allClassLines = readFromJsonFile(files, className);
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

    private static Lexicon createLexicon(String directory, String dbo_className, String dbo_prediction, String dbo_associationRule, NewResults result, ThresoldsExperiment.ThresoldELement thresoldELement, String experimentID) throws Exception {
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

    public static NewResults readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        NewResults result = mapper.readValue(file, NewResults.class);
        return result;
    }

    public static NewResults readFromJsonFile(List<File> files, String className) throws IOException, Exception {
        Map<String, List<String>> classDistributions = new TreeMap<String, List<String>>();
        Discription description = null;
        for (File file : files) {

            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];

            if (!parameters[0].contains(className)) {
                continue;
            }
            System.out.println("parameters[0]:" + parameters[0]);

            ObjectMapper mapper = new ObjectMapper();
            NewResults resultTemp = mapper.readValue(file, NewResults.class);
            description = resultTemp.getDescription();
            List<String> local = resultTemp.getDistributions();
            classDistributions.put(parameters[0], local);
        }
        return new NewResults(description, classDistributions);
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

    private static Map<String, MeanReciprocalCalculation> meanReciprocalValues(String experiment, String directory, List<File> fileList) throws IOException {
        Map<String, MeanReciprocalCalculation> meanReciprocals = new TreeMap<String, MeanReciprocalCalculation>();
        for (String posTag : Analyzer.POSTAGS) {
            try {

                File file = getFile(posTag, fileList);
                String fileName = file.getName().replace(".json", "");
                //System.out.println("fileName:" + fileName);
                String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, posTag);
                String conditionalFilename = directory + fileName + ".json";
                //System.out.println("qaldFileName:" + qaldFileName);
                //System.out.println("conditionalFilename:" + conditionalFilename);
                Comparision comparision = new Comparision(qaldFileName, conditionalFilename);
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
            System.out.println("adjectives:" + adjectives);
            System.out.println("verbs:" + verbs);
            System.out.println("nouns:" + nouns);
        }
        return meanReciprocals;
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

    /*public static void main2(String[] args) throws Exception {
        NewResultEvalutionTest newResultEvalutionTest = new NewResultEvalutionTest();
        List<ObjectWordResults> entityResults = new ArrayList<ObjectWordResults>();
        Map<String, Map<String, Lexicon>> classRuleMap = new TreeMap<String, Map<String, Lexicon>>();

        //for (String className : classNames) {
        for (String prediction : predicateRules) {
            prediction = predict_l_for_s_given_po;
            for (String associationRule : interestingness) {
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, prediction, associationRule, "json");
                Map<String, Lexicon> preditcionRuleClassMap = new TreeMap<String, Lexicon>();
                for (File file : pair.getValue1()) {
                    String fileName = file.getName().replace("HR_", "");
                    if(!fileName.contains("Politician"))
                        continue;
                    String[] info = fileName.split("-");
                    String[] parameters =findParameter(info);
                    String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
                    NewResults result = readFromJsonFile(new File(inputDir + file.getName()));
                    Lexicon lexicon = createLexicon( parameters[0], parameters[1], parameters[2], result);
                    preditcionRuleClassMap.put(key, lexicon);
                    System.out.println(key+" "+lexicon);     
                }
               
                for (String key : preditcionRuleClassMap.keySet()) {
                    Lexicon lexicon = preditcionRuleClassMap.get(key);
                    String directory = qald9Dir + OBJECT + "/";
                    System.out.println("directory:" + directory);
                    List<File> fileList = FileFolderUtils.getSpecificFiles(directory, key, ".json").getValue1();
                    System.out.println(fileList);
                    Map<String,MeanReciprocalCalculation>meanReciprocals=new TreeMap<String,MeanReciprocalCalculation>();
                    String outputFileName =null;
                    for (String posTag : Analyzer.POSTAGS) {
                        List<LexiconUnit> list = lexicon.getLexiconPosTaggged().get(posTag);
                        File file = getFile(posTag, fileList);
                        String fileName = file.getName().replace(".json", "");
                        String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, posTag);
                        String conditionalFilename = directory + fileName + ".json";
                        outputFileName = directory + fileName + "-Mean" + ".json";
                        //System.out.println("qaldFileName:" + qaldFileName);
                        //System.out.println("conditionalFilename:" + conditionalFilename);
                        //System.out.println("outputFileName:" + outputFileName);
                        Comparision comparision = new Comparision(posTag,qald9Dir, qaldFileName, conditionalFilename, outputFileName);
                        comparision.compersionsPattern();
                        meanReciprocals.put(posTag, comparision.getMeanReciprocalResult());
                       
                    }
                     outputFileName = directory + key+"-Mean" + ".json";
                     FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocals, outputFileName);

                }
                break;
            }

            break;
        }

        //}
        //String str = entityResultToString(entityResults);
    }*/

 /*private static void meanReciprocal(String directory, Map<String, Lexicon> associationRuleLex, Map<String, ThresoldsExperiment> associationRulesExperiment) throws IOException {

        Integer experimentNumber = 0;
        for (String associationRule : associationRuleLex.keySet()) {
            ThresoldsExperiment thresold = associationRulesExperiment.get(associationRule);
            for (String experiment : thresold.getThresoldELements().keySet()) {
                experimentNumber = experimentNumber + 1;
                ThresoldsExperiment.ThresoldELement thresoldELement = thresold.getThresoldELements().get(experiment);
                experiment = experimentNumber + "-" + experiment;
                List<File> fileList = FileFolderUtils.getSpecificFiles(directory, associationRule, ".json").getValue1();
                Map<String, MeanReciprocalCalculation> meanReciprocals = meanReciprocalValues(directory, fileList);
                String outputFileName = directory + experiment + "-" + associationRule + "-NN-JJ-VB-" + "MeanR" + ".json";
                System.out.println("outputFileName:" + outputFileName);
                FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocals, outputFileName);
                break;
            }

        }
    }*/
 /*private static Lexicon createLexicon(String dbo_className, String dbo_prediction, String dbo_associationRule,  NewResults result ) throws Exception {
        String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
        Analyzer analyzer = null;
        Lexicon lexicon = null;
 
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        for (String line : result.getDistributions()) {
            System.out.println("line:"+line);
            LineInfo lineInfo = new LineInfo(dbo_className, line, 1, 0);
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

        lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(key, dbo_associationRule, lineLexicon);
        return lexicon;
    }*/
}
