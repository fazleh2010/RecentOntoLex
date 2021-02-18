/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.analyzer.Lemmatizer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LoggingExample;
import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.results.ReciprocalResult;
import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.predict_l_for_s_given_po;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.utils.CsvFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.EvaluationTriple;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Comparision implements ThresoldConstants {

    //private Map<String, LexiconUnit> lexiconDic = new TreeMap<String, LexiconUnit>();
    private Map<String, LexiconUnit> lexiconDic = new TreeMap<String, LexiconUnit>();
    private Map<String, String> lexiconLemma = new TreeMap<String, String>();
    private CsvFile csvFile = null;
    private File outputFileName = null;
    private String predicationRule = null;
    private String posTag = null;
    private String type = null;
    private MeanReciprocalCalculation meanReciprocalResult = null;
    private Logger LOGGER;
    private Lemmatizer lemmatizer=null;
    

    public Comparision(String postag, String qald9Dir, File qaldFileName, File methodFileName, File outputFileName, String experiment, String type) throws IOException {
        this.getLexicon(methodFileName);
        //this.qaldDic = getQaldFromJson(qaldFileName);
        this.outputFileName = outputFileName;
    }

    public Comparision(Lemmatizer lemmatizer,String predicationRule, CsvFile csv, File conditionalFilename, String posTag, Logger LOGGER, String experiment, String type) {
        this.LOGGER = LOGGER;
        this.lemmatizer=lemmatizer;
        this.posTag = posTag;
        this.getLexicon(conditionalFilename);
        this.csvFile = csv;
        this.type = type;
        this.predicationRule = predicationRule;
        this.compersionsPattern(experiment, type);
    }

    public Comparision(File qaldFileName, File conditionalFilename, Boolean classSpecific, String className) throws IOException {
        if (!classSpecific) {
            getLexicon(conditionalFilename);
        }
        //this.qaldDic = getQaldFromJson(qaldFileName);
    }

    public Comparision(File qaldFileName, File conditionalFilename, Boolean classSpecific, String className, String experiment, String OBJECT) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*private void compersionsPattern(String experiment, String type) throws IOException {
        List<Pair<String, Map<String, Double>>> lexicon = new ArrayList<Pair<String, Map<String, Double>>>();
        List<Pair<String, Map<String, Boolean>>> qald_gold = new ArrayList<Pair<String, Map<String, Boolean>>>();
        if (csvFile.getRow().keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "qald file is empty!!::");
            throw new IOException("qald file is empty!!::");
        }

        if (lexiconDic.keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "No lexicon file is found::");
            return;
        }

        List<String> commonWords = new ArrayList<String>(Sets.intersection(csvFile.getRow().keySet(), lexiconDic.keySet()));
        if (!commonWords.isEmpty()) {

        } else {
            LOGGER.log(Level.WARNING, "NO linguistic pattern matched between lexicon and qald-9");
        }

        for (String word : lexiconDic.keySet()) {
            LexiconUnit lexiconElement = lexiconDic.get(word);
            Map<String, Double> predict = this.getPredictMap(word, lexiconElement);
            Map<String, Boolean> goldRelevance = this.getGoldRelevance(word, predict, type);
            Pair<String, Map<String, Double>> predictPair = new Pair<String, Map<String, Double>>(word, predict);
            Pair<String, Map<String, Boolean>> goldRelevancePair = new Pair<String, Map<String, Boolean>>(word, goldRelevance);
            lexicon.add(predictPair);
            qald_gold.add(goldRelevancePair);
        }

        this.meanReciprocalResult = new MeanReciprocalCalculation(experiment, lexicon, qald_gold, LOGGER, commonWords);
        LOGGER.log(Level.INFO, "***** ***** ***** SUMMARY of RESULT ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****");
        LOGGER.log(Level.INFO, "***** RESULT of ANALYSIS of POS TAG::" + this.posTag);
        LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN in LEXICON::" + this.meanReciprocalResult.getTotalPattern());
        LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN with NONZERO RANK::" + this.meanReciprocalResult.getPatternFound().size());
        LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN with ZERO RANK::::" + this.meanReciprocalResult.getPatternNotFound().size());
        LOGGER.log(Level.INFO, "MEAN RECIPROCAL::" + this.meanReciprocalResult.getMeanReciprocalRankStr());
        LOGGER.log(Level.INFO, "***** ***** ***** ****** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****");
    }*/
    
    private void compersionsPattern(String experiment, String type)  {
        Map<String, Map<String, Double>> lexiconWordKbs = new TreeMap<String, Map<String, Double>>();
        Map<String, Map<String, Boolean>> goldWordKbs = new TreeMap<String,Map<String, Boolean>>();
        if (csvFile.getRow().keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "qald file is empty!!::");
            return;
        }else if (lexiconDic.keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "No lexicon file is found::");
            return;
        }

        List<String> commonWords = new ArrayList<String>(Sets.intersection(csvFile.getRow().keySet(), lexiconDic.keySet()));
        if (!commonWords.isEmpty()) {

        } else {
            LOGGER.log(Level.WARNING, "NO linguistic pattern matched between lexicon and qald-9");
        }
      
        for (String word : lexiconDic.keySet()) {
            LexiconUnit lexiconElement = lexiconDic.get(word);
            //LOGGER.log(Level.WARNING, "word: "+word+" "+lexiconDic.get(word));
            Map<String, Double> predict = this.getPredictMap(word, lexiconElement);
            Map<String, Boolean> goldRelevance = this.getGoldRelevance(word, predict, type);
            lexiconWordKbs.put(word, predict);
            goldWordKbs.put(word, goldRelevance); 
        }

        /*for (String word : this.csvFile.getRow().keySet()) {
            Map<String, Double> predict =new HashMap<String,Double>();
            if (lexiconDic.containsKey(word)) {
                LexiconUnit lexiconElement = lexiconDic.get(word);
                predict = this.getPredictMap(word, lexiconElement);
            }
            Map<String, Boolean> goldRelevance = this.getGoldRelevance(word, predict, type);
            System.out.println("goldRelevance:"+goldRelevance);
                Pair<String, Map<String, Double>> predictPair = new Pair<String, Map<String, Double>>(word, predict);
                Pair<String, Map<String, Boolean>> goldRelevancePair = new Pair<String, Map<String, Boolean>>(word, goldRelevance);
                lexicon.add(predictPair);
                qald_gold.add(goldRelevancePair);

        }*/

        this.meanReciprocalResult = new MeanReciprocalCalculation(experiment, this.predicationRule,csvFile,lexiconWordKbs, this.lexiconLemma,goldWordKbs, LOGGER);
        //LOGGER.log(Level.INFO, "***** ***** ***** SUMMARY of RESULT ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****");
        //LOGGER.log(Level.INFO, "***** RESULT of ANALYSIS of POS TAG::" + this.posTag);
        //LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN in LEXICON::" + this.meanReciprocalResult.getTotalPattern());
        //LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN with NONZERO RANK::" + this.meanReciprocalResult.getPatternFound().size());
        //LOGGER.log(Level.INFO, "***** NUMBER OF PATTERN with ZERO RANK::::" + this.meanReciprocalResult.getPatternNotFound().size());
        LOGGER.log(Level.INFO, "MEAN RECIPROCAL::" + this.meanReciprocalResult.getMeanReciprocalRankStr());
        LOGGER.log(Level.INFO, "***** ***** ***** ****** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****");
        
    }

    private ReciprocalResult compersionsPattern(String word, Unit unit, LexiconUnit LexiconUnit) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
        Map<String, Double> predict = new HashMap<String, Double>();
        List<String> rankpredicates = new ArrayList<String>();
        for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
            List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
            String key = pairs.get(0).split("=")[1];
            key = this.getPredicate(key);
            Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
            predict.put(key, value);
            rankpredicates.add(key);
        }
        for (String pairT : predict.keySet()) {
            //Since qald is hand annotaed to require to read the list one by one and strip.
            for (String qaldPredicate : unit.getPairs()) {
                qaldPredicate = qaldPredicate.strip();
                if (unit.getPairs().contains(qaldPredicate)) {
                    goldRelevance.put(qaldPredicate, Boolean.TRUE);
                } else {
                    goldRelevance.put(qaldPredicate, Boolean.FALSE);
                }
            }

        }
        //return MeanReciprocalRank.getReciprocalRank(predict, goldRelevance);
        return this.calculateMeanReciprocal(word, rankpredicates, goldRelevance);
    }

    public ReciprocalResult calculateMeanReciprocal(String word, List<String> ranking, Map<String, Boolean> gold) {
        double reciprocalRank = 0;
        Double meanReciprocal = 0.0;
        Map<Integer, String> reciprocalRankPairs = new TreeMap<Integer, String>();
        Integer index = 0, rank = 0, foundCount = 0;

        for (index = 0; index < ranking.size(); index++) {
            String predicate = ranking.get(index);
            if (gold.containsKey(predicate)) {
                if (gold.get(ranking.get(index))) {
                    rank = index + 1;
                    reciprocalRank = 1.0 / (rank);
                    return new ReciprocalResult(predicate, rank, reciprocalRank);

                }
            }
        }
        return null;
    }
    
    private void getLexicon(File file) {
        ObjectMapper mapper = new ObjectMapper();

        List<LexiconUnit> lexiconUnits = new ArrayList<LexiconUnit>();
        try {
            lexiconUnits = mapper.readValue(file, new TypeReference<List<LexiconUnit>>() {
            });
            for (LexiconUnit LexiconUnit : lexiconUnits) {
                 
                String word=LexiconUnit.getWord();
                lexiconDic.put(word, LexiconUnit);
              
                Pair<Boolean, String> pair = this.lemmatizer.getLemmaWithoutPos(word);
                if (pair.getValue0()) {
                    String lemma=pair.getValue1();
                    this.lexiconLemma.put(lemma, word);
                }
               
            }
        } catch (IOException ex) {
            System.out.println("no file is found for lexicon!!" + ex.getMessage());
        }
    }

   /* private void getLexicon(File file) {
        ObjectMapper mapper = new ObjectMapper();

        List<LexiconUnit> lexiconUnits = new ArrayList<LexiconUnit>();
        try {
            lexiconUnits = mapper.readValue(file, new TypeReference<List<LexiconUnit>>() {
            });
            for (LexiconUnit LexiconUnit : lexiconUnits) {
                 
                String word=LexiconUnit.getWord();
                lexiconDic.put(word, LexiconUnit);
              
                Pair<Boolean, String> pair = this.lemmatizer.getLemmaWithoutPos(word);
                if (pair.getValue0()) {
                    String lemma=pair.getValue1();
                    this.lexiconLemma.put(lemma, word);
                }
               
            }
        } catch (IOException ex) {
            System.out.println("no file is found for lexicon!!" + ex.getMessage());
        }

    }*/
    
    
   

    private Map<String, Unit> getQaldFromJson(File qaldFile) throws IOException {
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        ObjectMapper mapper = new ObjectMapper();
        List<Unit> units = mapper.readValue(qaldFile, new TypeReference<List<Unit>>() {
        });
        for (Unit unit : units) {
            qald.put(unit.getWord(), unit);
        }
        return qald;
    }

    private String getPredicate(String predicate) {
        predicate = predicate.strip();
        return predicate;
    }

    private List<String> getCommonWords(Set<String> set1, Set<String> set2) {
        Set<String> intersection = Sets.intersection(set1, set2);
        return new ArrayList<String>(intersection);
    }

    private Map<String, Double> getPredictMap(String word, LexiconUnit lexiconElement) {
        Map<String, Double> predict = new HashMap<String, Double>();
        Integer pairIndex = 0, valueIndex = 1, tripleIndex = 2, classIndex = 3;

        for (Integer rank : lexiconElement.getEntityInfos().keySet()) {
            List<String> pairs = lexiconElement.getEntityInfos().get(rank);
            String tripleString = pairs.get(tripleIndex);
            EvaluationTriple triple;
            try {
                triple = new EvaluationTriple(LEXICON, this.predicationRule, rank.toString(), tripleString, word, LOGGER);
                Double value = Double.parseDouble(pairs.get(valueIndex).split("=")[1]);
                predict.put(triple.getKey(), value);
                 LOGGER.log(Level.FINEST, "triple.getKey():"+triple.getKey()+" "+pairs.get(valueIndex).split("=")[0]+ " "+value);
            } catch (Exception ex) {
                Logger.getLogger(Comparision.class.getName()).log(Level.SEVERE, null, ex);
                LOGGER.log(Level.INFO, "fail to create triple::");

            }

        }
        return predict;
    }

    private Map<String, Boolean> getGoldRelevance(String word, Map<String, Double> predict, String object) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();

        if (csvFile.getRow().containsKey(word)) {
            for (String predicatePattern : predict.keySet()) {
                Boolean flag = this.isFoundinGold(word, predicatePattern);
                //LOGGER.log(Level.INFO, "predicatePattern::" + predicatePattern);
                goldRelevance.put(predicatePattern, flag);
                if (flag) {
                    //LOGGER.log(Level.INFO, "predicatePattern::" + predicatePattern);
                    //LOGGER.log(Level.INFO, "flag::" + flag);
                }

            }
            return goldRelevance;
        } else {
            for (String predicatePattern : predict.keySet()) {
                goldRelevance.put(predicatePattern, Boolean.FALSE);
            }
            return goldRelevance;
        }

    }

    private void setLog(String word, Map<String, Double> predict) {
        LOGGER.log(Level.INFO, "checking :" + word);
        for (String key : predict.keySet()) {
            Double value = predict.get(key);
            LOGGER.log(Level.INFO, "key::" + key);
            LOGGER.log(Level.INFO, "value::" + value);
        }

    }

    private Boolean isFoundinGold(String word, String lexiconStr) {
        EvaluationTriple lexiconTriple = null;
        try {
            lexiconTriple = new EvaluationTriple(LEXICON, this.predicationRule, lexiconStr);
        } catch (Exception ex) {
            Logger.getLogger(Comparision.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.log(Level.INFO, "lexiconTriple parse failed");
        }
        //LOGGER.log(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@lexiconTriple::" + lexiconTriple);
        List<EvaluationTriple> qaldPredicateObject = new ArrayList<EvaluationTriple>();
        //if (predicationRule.contains(this.predicationRule)) {
            qaldPredicateObject = csvFile.getRowValues(word, this.predicationRule);
            for (EvaluationTriple qaldTriple : qaldPredicateObject) {
                //LOGGER.log(Level.INFO, "$$$$$$$$$$$ qaldTriple::" + qaldTriple);
                if (EvaluationTriple.match(lexiconTriple, qaldTriple, this.predicationRule)) {
                    return true;
                }
            }
        //}
        return false;

    }

    public String getPosTag() {
        return posTag;
    }

    public MeanReciprocalCalculation getMeanReciprocalResult() {
        return meanReciprocalResult;
    }

}
