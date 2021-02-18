/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.Lemmatizer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResultsMR;
import citec.correlation.wikipedia.results.Rule;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class GeneratedExperimentData implements ThresoldConstants {

    private static Logger LOGGER = null;
     private  static Lemmatizer lemmatizer=null;
    //the files are ready at /opt/rulepatterns/results
    //bzip2 -d filename.bz2
    //bzip2 -d *.json.bz2


    public GeneratedExperimentData(Lemmatizer lemmatizer,String baseDir, String qald9Dir, String givenPrediction, String givenInterestingness,Map<String, ThresoldsExperiment> associationRulesExperiment, Logger givenLOGGER) throws Exception {
        this.lemmatizer=lemmatizer;
        this.setUpLog(givenLOGGER);

        for (String prediction : predictLinguisticGivenKB) {
            String outputDir = qald9Dir + "/" + prediction + "/" + "dic";
            if (!prediction.equals(givenPrediction)) {
                continue;
            }
            for (String rule : interestingness) {
                String rawFileDir = baseDir + prediction + "/"+rule+"/";
                if (!rule.contains(givenInterestingness)) {
                    continue;
                }
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(rawFileDir,".json");
                if (pair.getValue0()) {
                    //NewResultsMR newResultsMR = readFromJsonFile(pair.getValue1());
                    createEvalutionFiles(outputDir, prediction, rule, pair.getValue1(), associationRulesExperiment);
                }
                else
                    throw new Exception("NO files found for "+prediction+" "+rule);
            }
        }
    }

    private static void createEvalutionFiles(String outputDir, String prediction, String associationRule,List<File> files , Map<String, ThresoldsExperiment> associationRulesExperiment) throws Exception {
        Map<String, Lexicon> associationRuleLex = new TreeMap<String, Lexicon>();
        Lexicon lexicon = null;
        ThresoldsExperiment thresoldsExperiment = associationRulesExperiment.get(associationRule);
        Integer index = 0;
        for (String experiment : thresoldsExperiment.getThresoldELements().keySet()) {
            index = index + 1;
            ThresoldsExperiment.ThresoldELement element = thresoldsExperiment.getThresoldELements().get(experiment);
            String experimentID = index + "-" + experiment;
            lexicon = createLexicon(outputDir, prediction, associationRule, files, element, experimentID);
            LOGGER.log(Level.INFO, " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment);
            //System.out.println( outputDir + " index" + index + " experiment size::" + thresoldsExperiment.getThresoldELements().size() + " " + experiment);
        }
    }


    private static Lexicon createLexicon(String directory, String dbo_prediction, String interestingness, List<File> files, ThresoldsExperiment.ThresoldELement thresoldELement, String experimentID) throws Exception {
        Analyzer analyzer = null;
        Lexicon lexicon = null;
        Integer index = 0;
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        Integer numberOfRules = thresoldELement.getNumberOfRules();

        for (File file : files) {
            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
            //System.out.println("now processing class:::::" + file.getName());
             //LOGGER.log(Level.INFO, "now processing class:::::" + file.getName() );
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            NewResultsMR resultTemp = mapper.readValue(file, NewResultsMR.class);
            Discription description = resultTemp.getDescription();
            List<Rule> rules = resultTemp.getDistributions();
            //LOGGER.log(Level.INFO, " rules.size()" + rules.size() );
            for (Rule line : rules) {
              // LOGGER.log(Level.INFO, " line" + line );
                index = index + 1;
                if (index >= numberOfRules) {
                    break;
                }
                LineInfo lineInfo = new LineInfo(dbo_prediction, interestingness, line);

                if (!LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresoldELement.getGivenThresolds())) {
                    continue;
                }
                if (!lineInfo.getValidFlag()) {
                    continue;
                }
                //System.out.println("lineInfo.getnGramNumber():" + lineInfo.getnGramNumber());
                //System.out.println("thresoldELement.getN_gram():" + thresoldELement.getN_gram());
                /*if (lineInfo.getnGramNumber() != thresoldELement.getN_gram()) {
                    continue;
                }*/
                //LOGGER.log(Level.INFO, " lineInfo" + lineInfo.getnGramNumber() );

                String word = lineInfo.getWord();
                //System.out.println("@@@@@@@:" + lineInfo.getnGramNumber()+"@@@@@@@@@");
                //System.out.println("word:" + word);

                if (FormatAndMatch.isNumeric(lineInfo.getWord())) {
                    continue;
                }
                if (isKBValid(lineInfo.getObject())) {
                    continue;
                }
                String nGram = lineInfo.getWord();
                
                Pair<Boolean, String> pair = lemmatizer.getLemmaWithoutPos(nGram);
                if (pair.getValue0()) {
                    nGram = pair.getValue1();
                    System.out.println(word+" nGram:"+nGram);
                }
                nGram = nGram.toLowerCase().trim().strip();
                
                //LOGGER.log(Level.INFO,  "index:" + index + " total::" + numberOfRules + " nGram:" + nGram);
                //System.out.println( "index:" + index + " total::" + numberOfRules + " nGram:" + nGram);
                List<LineInfo> results = new ArrayList<LineInfo>();
                if (lineLexicon.containsKey(nGram)) {
                    results = lineLexicon.get(nGram);
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                } else {
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                }
                //System.out.println("className:"+line.getC());

            }

        }

        lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(dbo_prediction,directory, experimentID, interestingness, lineLexicon);
        return lexicon;
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
    
     /* private static NewResultsMR readFromJsonFile(List<File> files) throws IOException, Exception {
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

            //LOGGER.log(Level.INFO, "now processing index:" + index + " total:" + totalFiles + "  file:" + fileName);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            NewResultsMR resultTemp = mapper.readValue(file, NewResultsMR.class);
            description = resultTemp.getDescription();
            List<Rule> local = resultTemp.getDistributions();
            System.out.println("@@@@@@@@@@@@ "+description.getClassName()+" "+local.size());
            
            //classDistributions.put(parameters[0], local);
        }
        //System.out.println(description+" "+local.size());
        return new NewResultsMR(description, classDistributions);
    }*/
    
    public static void main(String[] args) throws Exception {
        String rawFileDir = null;
        String directory = qald9Dir + OBJECT + "/";
        //rawFileDir = dbpediaDir + "results/" + "new/MR/";
        String baseDir = "/home/elahi/dbpediaFiles/unlimited/unlimited/";
        Logger LOGGER = Logger.getLogger(GeneratedExperimentData.class.getName());
        String outputDir = qald9Dir;
        Map<String, ThresoldsExperiment> associationRulesExperiment = Evaluation.createExperiments();

        List<String> predictLinguisticGivenKB = new ArrayList<String>(Arrays.asList(
        predict_l_for_o_given_p
        //predict_l_for_s_given_po
        //predict_l_for_s_given_o
        //predict_l_for_o_given_p,
        //predict_l_for_o_given_s,
        //predict_l_for_o_given_sp
        ));
        List<String> interestingness = new ArrayList<String>();
        interestingness.add(ThresoldConstants.Cosine);
        /*interestingness.add(ThresoldConstants.Coherence);
        interestingness.add(ThresoldConstants.AllConf);
        interestingness.add(ThresoldConstants.MaxConf);
        interestingness.add(ThresoldConstants.Kulczynski);
        interestingness.add(ThresoldConstants.IR);
        */
        Lemmatizer lemmatizer=new Lemmatizer ();

        for (String prediction : predictLinguisticGivenKB) {
            for (String rule : interestingness) {
                GeneratedExperimentData ProcessFile = new GeneratedExperimentData(lemmatizer,baseDir, outputDir, prediction, rule, associationRulesExperiment, LOGGER);

            }
        }
    }
}
