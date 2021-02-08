/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LoggingExample;
import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.results.ReciprocalResult;
import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.dic.qald.Unit;
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
public class Comparision {
    
    private Map<String, LexiconUnit> lexiconDic = new TreeMap<String, LexiconUnit> ();
    private CsvFile csvFile=null;
    private File outputFileName = null;
    private String posTag = null;
    private String type = null;
    private MeanReciprocalCalculation meanReciprocalResult =null;
    private  Logger LOGGER ;
    
  

    public Comparision(String postag,String qald9Dir, File qaldFileName, File methodFileName,File outputFileName,String experiment,String type) throws IOException {
        this.lexiconDic = getLexicon(methodFileName);
        //this.qaldDic = getQaldFromJson(qaldFileName);
        this.outputFileName=outputFileName;
    }
    
     public Comparision(CsvFile csv, File conditionalFilename,String posTag,Logger LOGGER,String experiment,String type) throws IOException, CsvException {
        this.LOGGER=LOGGER;
        this.lexiconDic = getLexicon(conditionalFilename);
        this.csvFile=csv;
        this.posTag=posTag;
        this.type=type;
        this.compersionsPattern(experiment, type);
    }
    
    
    public Comparision(File qaldFileName, File conditionalFilename,Boolean classSpecific,String className) throws IOException {
        if(!classSpecific)
           this.lexiconDic = getLexicon(conditionalFilename);
        else
           this.lexiconDic = getLexiconPerClass(conditionalFilename,className);
        //this.qaldDic = getQaldFromJson(qaldFileName);
    }

    public Comparision(File qaldFileName, File conditionalFilename, Boolean classSpecific, String className, String experiment, String OBJECT) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private void compersionsPattern(String experiment,String type) throws IOException {
        List<Pair<String,Map<String, Double>>> lexicon = new ArrayList<Pair<String,Map<String, Double>>>();
        List<Pair<String,Map<String, Boolean>>> qald_gold = new ArrayList<Pair<String,Map<String, Boolean>>>();
         if (csvFile.getRow().keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "qald file is empty!!::" );
            throw new IOException("qald file is empty!!::");
        }
        
        if (lexiconDic.keySet().isEmpty()) {
            LOGGER.log(Level.WARNING, "No lexicon file is found::" );
            return;
        }
            
        //Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        LOGGER.log(Level.INFO, "csv:",csvFile.getRow().keySet().toString());
        List<String> commonWords = new ArrayList<String>(Sets.intersection(csvFile.getRow().keySet(), lexiconDic.keySet()));
        if (!commonWords.isEmpty()) {
            LOGGER.log(Level.INFO, "common pattern found between lexicon and qald-9");
            LOGGER.log(Level.INFO, commonWords.toString());
        }
        else{
            LOGGER.log(Level.INFO, "no linguistic pattern matched between lexicon and qald-9"); 
        }
          
        for (String word : lexiconDic.keySet()) {
            /*if(!word.contains("canada"))
                continue;*/
            LexiconUnit lexiconElement = lexiconDic.get(word);
            Map<String, Double> predict = this.getPredictMap(lexiconElement);
            Map<String, Boolean> goldRelevance = this.getGoldRelevance(word, predict,type);
            Pair<String,Map<String, Double>> predictPair = new Pair<String,Map<String, Double>>(word,predict);
            Pair<String,Map<String, Boolean>> goldRelevancePair = new Pair<String,Map<String, Boolean>>(word,goldRelevance);
            lexicon.add(predictPair);
            qald_gold.add(goldRelevancePair);
        }
     
        this. meanReciprocalResult =new MeanReciprocalCalculation(experiment,lexicon, qald_gold,LOGGER);
        LOGGER.log(Level.FINE, ">>>>>>>>>>>>>>>>>>>>>  Summary of the experiment >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        LOGGER.log(Level.INFO, "experiment::"+experiment);
        LOGGER.log(Level.INFO, "postag::"+this.posTag);
        LOGGER.log(Level.INFO, "meanReciprocalRank value::"+this. meanReciprocalResult.getMeanReciprocalRankStr());
        LOGGER.log(Level.INFO, "Lexicon size::"+this. meanReciprocalResult.getTotalPattern());
        LOGGER.log(Level.INFO, "number of pattern matched with Qald-9::"+this. meanReciprocalResult.getPatternFound().size());
        LOGGER.log(Level.INFO, "detail of matched pattern::"+this. meanReciprocalResult.getPatternFound());
        LOGGER.log(Level.INFO, "number of pattern DOES NOT match with Qald-9::::"+this. meanReciprocalResult.getPatternNotFound().size());
        LOGGER.log(Level.FINE, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        //System.out.println("meanReciprocalRank:" + meanReciprocalResult.getMeanReciprocalElements());
       //FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocalResult, outputFileName);
        
    }
    
    

    
   /* public void compersionsPattern2() {
        //Map<String, Double> meanReciprocal = new TreeMap<String, Double>();
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        Map<String, MeanReciprocalCalculation> wordReciprocalRank = new TreeMap<String, MeanReciprocalCalculation>();
        List<String> commonWords = new ArrayList<String>(intersection);
        Double sum=0.0;
        for (String word : qaldDic.keySet()) {
            //System.out.println("word:"+word);
            ReciprocalResult reciprocalElement = null;
            if (commonWords.contains(word)) {
                Unit qaldElement = qaldDic.get(word);
                LexiconUnit lexiconElement = lexiconDic.get(word);
                reciprocalElement = this.compersionsPattern(word,qaldElement,lexiconElement);
                  if(reciprocalElement!=null)
                      System.out.println(word + " " + reciprocalElement);
                   else
                      reciprocalElement = new ReciprocalResult("no matched predicate found for "+word,0,0.0);

            }
            else 
               reciprocalElement = new ReciprocalResult(word+"  not found "+word,0,0.0);
            sum+=reciprocalElement.getReciprocalRank();
        }
        Double meanReciprocal=sum/qaldDic.size();
        //System.out.println("meanReciprocal:"+meanReciprocal);
        
    }*/

     private ReciprocalResult compersionsPattern(String word,Unit unit,LexiconUnit LexiconUnit) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
        Map<String, Double> predict = new HashMap<String, Double>();
        List<String> rankpredicates=new ArrayList<String>();
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
         return this.calculateMeanReciprocal(word,rankpredicates, goldRelevance);
    }


    /*public void comparisionsWords() {
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        List<String> commonWords = new ArrayList<String>(intersection);

        Integer index = 0;
        for (String word : commonWords) {
            //predictionsMaps.add(new HashMap<String, Double>());
            //golds = new ArrayList<Map<String, Boolean>>();
            Unit unit = qaldDic.get(word);
            //"dbo:country res:Australia";
            String sparql = "dbo:country res:Australia";
            if (!unit.getPairs().isEmpty()) {
                sparql = unit.getPairs().get(0);
            }
            LexiconUnit LexiconUnit = lexiconDic.get(word);
            Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
            Map<String, Double> predict = new HashMap<String, Double>();
            for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
                List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
                String key = pairs.get(0).split("=")[1];
                Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
                //predictionsMaps.get(index).put(key, value);
                predict.put(key, value);
            }
            for (String pairT : predict.keySet()) {
                if (pairT.contains(sparql)) {
                    goldRelevance.put(pairT, Boolean.TRUE);
                    //golds.get(index).put(pairT, Boolean.TRUE);
                } else {
                    goldRelevance.put(pairT, Boolean.FALSE);
                    //golds.get(index).put(pairT, Boolean.FALSE);
                }

            }
            Double predictedReciprocalRank = 0.0;
            //temporary closed
                    //this.calculateMeanReciprocal(predict, goldRelevance);
            //System.out.println(word + " predictedReciprocalRank: " + predictedReciprocalRank);
            index = index + 1;

        }

    }*/

    /*private Double calculateMeanReciprocal(List<String> rankedList, Map<String, Boolean> goldRelevance) {
       List<Pair<Integer,Double>> reciprocalRankPairs= this.getReciprocalRank(rankedList, goldRelevance);
       Double sum=0.0;
       for(Pair<Integer,Double> pair:reciprocalRankPairs){
           Integer rank=pair.getValue0();
           Double reciprocalRank=pair.getValue1();
           sum+=reciprocalRank;
           
       }
       sum=sum/reciprocalRankPairs.size();
        System.out.println("OthersumTest:"+sum);
        return sum;
    }*/
   public ReciprocalResult  calculateMeanReciprocal(String word,List<String> ranking, Map<String, Boolean> gold) {
        double reciprocalRank = 0;
        Double meanReciprocal = 0.0;
        Map<Integer,String> reciprocalRankPairs = new TreeMap<Integer, String>();        
        Integer index = 0, rank = 0, foundCount = 0;

        for (index = 0; index < ranking.size(); index++) {
            String predicate = ranking.get(index);
            if (gold.containsKey(predicate)) {
                if (gold.get(ranking.get(index))) {
                    rank = index + 1;
                    reciprocalRank = 1.0 / (rank);
                    return  new ReciprocalResult(predicate,rank,reciprocalRank);
                    
                }
            }
        }
        return  null;
    }
    private Map<String, LexiconUnit> getLexicon(File file) {
        Map<String, LexiconUnit> lexicons = new TreeMap<String, LexiconUnit>();
        ObjectMapper mapper = new ObjectMapper();

        List<LexiconUnit> lexiconUnits = new ArrayList<LexiconUnit>();
        try {
            lexiconUnits = mapper.readValue(file, new TypeReference<List<LexiconUnit>>() {
            });
            for (LexiconUnit LexiconUnit : lexiconUnits) {
                lexicons.put(LexiconUnit.getWord(), LexiconUnit);
            }
        } catch (IOException ex) {
            System.out.println("no file is found for lexicon!!"+ex.getMessage());
            return lexicons;
        }

        return lexicons;
    }
    
    
    
    private Map<String, LexiconUnit> getLexiconPerClass(File conditionalFilename, String className) {
        Map<String, LexiconUnit> lexicons = new TreeMap<String, LexiconUnit>();
        ObjectMapper mapper = new ObjectMapper();

        List<LexiconUnit> lexiconUnits = new ArrayList<LexiconUnit>();
        try {
            lexiconUnits = mapper.readValue(conditionalFilename, new TypeReference<List<LexiconUnit>>() {
            });
            for (LexiconUnit LexiconUnit : lexiconUnits) {
                LinkedHashMap<Integer, List<String>> newEntityInfos = new LinkedHashMap<Integer, List<String>>();
                Integer count = 0;
                for (Integer index : LexiconUnit.getEntityInfos().keySet()) {
                    System.out.println("Index:" + index);
                    List<String> values = LexiconUnit.getEntityInfos().get(index);
                    //List<String> selectedPair = new ArrayList<String>();
                    Boolean found = false;
                    String selectedElement = null;
                    for (String eachElement : values) {
                        if (eachElement.contains("=")) {
                            String[] info = eachElement.split("=");
                            if (info[0].contains("class")) {
                                String classNameValue = info[1];
                                if (classNameValue.contains(className)) {
                                    System.out.println("selectedElement:" + eachElement);
                                    selectedElement = eachElement;
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (found) {
                        count = count + 1;
                        System.out.println("count:"+count+" values:"+values);
                        newEntityInfos.put(count, values);
                       
                    }
                  if(!newEntityInfos.isEmpty()){
                  LexiconUnit newLexiconUnit = new LexiconUnit(LexiconUnit, newEntityInfos);
                  lexicons.put(LexiconUnit.getWord(), newLexiconUnit);
                    }
                }
                

            }
        } catch (IOException ex) {
            System.out.println("no file is found for lexicon!!" + ex.getMessage());
            return lexicons;
        }
        
        /*for(String key:lexicons.keySet()){
            System.out.println(key);
             LexiconUnit newLexiconUnit =lexicons.get(key);
              System.out.println(newLexiconUnit);
        }*/
        
        return lexicons;
    }

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

   
   
    /*private double calculateMeanReciprocal(Map<String, Double> predictMap, Map<String, Boolean> goldRelevance) {
        double predictedReciprocalRank
                = MeanReciprocalRank.getReciprocalRank(predictMap, goldRelevance);
        return predictedReciprocalRank;
    }*/
    private String getPredicate(String predicate) {
        predicate = predicate.strip();
        return predicate;
    }

    private List<String> getCommonWords(Set<String>set1,Set<String>set2) {
         Set<String> intersection = Sets.intersection(set1, set2);
         return new ArrayList<String>(intersection);
    }

    private Map<String, Double> getPredictMap(LexiconUnit lexiconElement) {
                Map<String, Double> predict = new HashMap<String, Double>();

        for (Integer rank : lexiconElement.getEntityInfos().keySet()) {
                List<String> pairs = lexiconElement.getEntityInfos().get(rank);
                String key = pairs.get(0).split("=")[1];
                key = this.getPredicate(key);
                key=key.strip();
                Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
                predict.put(key, value);
            }
        return predict;
    }
    
    private Map<String, Boolean> getGoldRelevance(String word, Map<String, Double> predict,String object) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
     
        if (csvFile.getRow().containsKey(word)) {
           List<String> qaldPredicates =csvFile.getObjects(word);
            //List<String> qaldPredicates = new ArrayList<String>(qaldElement.getPairs());
            for (String predicatePattern : predict.keySet()) {
               
                if (qaldPredicates.contains(predicatePattern)) {
                     //LOGGER.log(Level.INFO, "checking word in qald::"+word);
                     //LOGGER.log(Level.INFO, "object::"+qaldPredicates);
                     //LOGGER.log(Level.INFO, "MATCHED predicatePattern::"+predicatePattern);
                    goldRelevance.put(predicatePattern, Boolean.TRUE);
                } else {
                    goldRelevance.put(predicatePattern, Boolean.FALSE);
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



    /*private Map<String, Boolean> getGoldRelevance(String word, Map<String, Double> predict) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();

        if (qaldDic.containsKey(word)) {
            Unit qaldElement = qaldDic.get(word);
            List<String> qaldPredicates = new ArrayList<String>(qaldElement.getPairs());
            for (String predicatePattern : predict.keySet()) {
                if (qaldPredicates.contains(predicatePattern)) {
                    goldRelevance.put(predicatePattern, Boolean.TRUE);
                } else {
                    goldRelevance.put(predicatePattern, Boolean.FALSE);
                }
            }
            return goldRelevance;
        } else {
            for (String predicatePattern : predict.keySet()) {
                goldRelevance.put(predicatePattern, Boolean.FALSE);
            }
            return goldRelevance;
        }

    }*/

    public String getPosTag() {
        return posTag;
    }

    public MeanReciprocalCalculation getMeanReciprocalResult() {
        return meanReciprocalResult;
    }

    private void setLog(String word, Map<String, Double> predict) {
        LOGGER.log(Level.INFO, "checking :" + word);
        for (String key : predict.keySet()) {
            Double value = predict.get(key);
            LOGGER.log(Level.INFO, "key::" + key);
            LOGGER.log(Level.INFO, "value::" + value);
        }

    }

  

   
}
