/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.results.ReciprocalResult;
import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.utils.CsvFile;
import citec.correlation.wikipedia.utils.DoubleUtils;
import citec.correlation.wikipedia.utils.EvalutionUtil;
import citec.correlation.wikipedia.utils.EvaluationTriple;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class MeanReciprocalCalculation implements Comparator {

    @JsonIgnore
    public static final boolean ASCENDING = true;
    @JsonIgnore
    public static final boolean DESCENDING = false;
    @JsonProperty("MeanReciProcalRank")
    private Double meanReciprocalRank = 0.0;
    @JsonProperty("experiment")
    private String experiment = null;
    @JsonIgnore
    private String meanReciprocalRankStr = "0.0";

    @JsonProperty("TotalPattern")
    private Integer totalPattern = 0;
    @JsonProperty("Found")
    private Integer numberOfPatterrnFoundNonZeroRank = 0;
    //@JsonProperty("NotFound")
    @Ignore
    private Integer numberOfPatterrnFoundZeroRank = 0;
    //@JsonProperty("Detail")
    @Ignore
    private Map<String, ReciprocalResult> patternFound = new TreeMap<String, ReciprocalResult>();

    @Ignore
    private Map<String, Double> hitsValue = new TreeMap<String, Double>();
    
    
    @Ignore
    private static String Hits = "Hits";
    private static String MRR = "MRR";
    

    @Ignore
    //private static LinkedHashSet<String> hitsString = new LinkedHashSet<String>(Arrays.asList(MRR,Hits+1, Hits+2, Hits+4));    
     private static LinkedHashSet<String> hitsString = new LinkedHashSet<String>(Arrays.asList(MRR,Hits+1, Hits+5, Hits+10));
    
    //private static LinkedHashSet<String> hitsString = new LinkedHashSet<String>(Arrays.asList(MRR,Hits+1, Hits+2, Hits+4));    

    //@JsonProperty("PatterrnFoundZeroRank")
    @JsonIgnore
    private Map<String, ReciprocalResult> patternNotFound = new TreeMap<String, ReciprocalResult>();
    @JsonIgnore
    private static Logger LOGGER = null;

    @JsonIgnore
    private CsvFile csvFile = null;
    @JsonIgnore
    private static String predictionRule = null;



    public MeanReciprocalCalculation() {

    }

    public MeanReciprocalCalculation(String experiment, String predictionRule, CsvFile csvFile, Map<String, Map<String, Double>> rankings, Map<String, String> lexiconLemma, Map<String, Map<String, Boolean>> gold, Logger LOGGER) {
        this.experiment = experiment;
        this.LOGGER = LOGGER;
        this.predictionRule = predictionRule;
        this.csvFile = csvFile;
        this.computeWithRankingMap(rankings, lexiconLemma, gold);
    }

    public MeanReciprocalCalculation(String experiment, Logger LOGGER) {
        this.experiment = experiment;
        this.LOGGER = LOGGER;
    }

    public void computeWithRankingMap(Map<String, Map<String, Double>> lexiconWordKbs, Map<String, String> lexiconLemma, Map<String, Map<String, Boolean>> goldWordKbs) {
        EvalutionUtil.ifFalseCrash(lexiconWordKbs.size() == goldWordKbs.size(),
                "The size of predictions and gold should be identical, Usually not found element are in FALSE marked in gold");
        double mrr = 0;
        double count = 0.0;
        Boolean matchFlag = false;
        hitsValue = new TreeMap<String, Double>();
        
         //LOGGER.log(Level.WARNING, "lexiconWordKbs: "+lexiconWordKbs.keySet());
           
          
           //lexiconWordKbs=this.filter(lexiconWordKbs);
          // LOGGER.log(Level.WARNING, "lexiconWordKbs: "+lexiconWordKbs);
         Map<String, Integer> hits=new HashMap<String, Integer>(); 
        //LOGGER.log(Level.WARNING, "lexiconWordKbs.keySet(): "+lexiconWordKbs.keySet());

        for (String word : this.csvFile.getRow().keySet()) {
            for (String[] coulmns : this.csvFile.getRow().get(word)) {
                Boolean validFlag = false;
                String str = "";
                ReciprocalResult reciprocalResult = null;
                Map<String, Double> rankedList = lexiconWordKbs.get(word);
                Map<String, Boolean> gold = goldWordKbs.get(word);
                Pair<String, String> pair = EvaluationTriple.getString(coulmns, predictionRule);

                //LOGGER.log(Level.WARNING, "word: "+word);
                validFlag = this.isWordMatched(lexiconWordKbs.keySet(), word);

                /*else if (lexiconLemma.containsKey(word)) {
                    word = lexiconLemma.get(word);
                    rankedList = lexiconWordKbs.get(word);
                    gold.put(word, true);
                }*/
                //isValid(predictionRule, word, lexiconWordKbs, coulmns)
                //lexiconWordKbs.containsKey(word)
                //LOGGER.log(Level.INFO,"lexiconLemma.keySet(): "+lexiconLemma.keySet());
                if (validFlag && isValid(predictionRule, coulmns)) {
                    matchFlag = true;
                    LOGGER.log(Level.INFO, ">> now checking qald pattern::" + word + " " + " >>>> Pattern FOUND in our LEXICON");
                    validFlag = true;
                    reciprocalResult = getReciprocalRank(getKeysSortedByValue(rankedList, DESCENDING), gold, validFlag, pair);
                    if (reciprocalResult.getRank() > 0) {
                        patternFound.put(word, reciprocalResult);
                    } else {
                        this.patternNotFound.put(word, reciprocalResult);
                    }
                    //mrr += reciprocalResult.getReciprocalRank();
                    //count += 1;

                } else {
                    LOGGER.log(Level.INFO, ">> now checking QUERY::" + word + " >> Pattern NOT FOUND in our LEXICON");
                    reciprocalResult = new ReciprocalResult();
                    //LOGGER.log(Level.INFO, ">> lexiconWordKbs::"+lexiconWordKbs);
                    //LOGGER.log(Level.INFO, ">> RANK::" + reciprocalResult.getRank() + " >> RECIPROCAL RANK::" + reciprocalResult.getRank() + "\n" + "\n");
                }

                // not all lexicon count += 1;
                Integer rank = reciprocalResult.getRank();
                mrr += reciprocalResult.getReciprocalRank();

                if (rank > 0) {
                    for (String key : hitsString) {
                        if(!key.contains(Hits))
                            continue;
                        String temp=key.replace(Hits, "");
                        int rankType = Integer.parseInt(temp);
                        Integer hitrank = 0;
                        if (rank <= rankType) {
                            hitrank = 1;
                            if(hits.containsKey(key)){
                                Integer hitrankList = hits.get(key) + hitrank; 
                                hits.put(key, hitrankList);
                            }
                            else 
                               hits.put(key, hitrank);                                
                            
                        }
                    }

                }
                   
               

                //temporary code to show 0 rank
               
                count += 1;

            }
        }
        
       double size = count;
        double totalMrr=mrr;
        mrr = (mrr / size);
        this.meanReciprocalRank = DoubleUtils.formatDouble(mrr);
        this.meanReciprocalRankStr = FormatAndMatch.doubleFormat(meanReciprocalRank);
        this.numberOfPatterrnFoundNonZeroRank = patternFound.size();
        this.numberOfPatterrnFoundZeroRank = patternNotFound.size();
        this.totalPattern = patternFound.size() + patternNotFound.size();
       
        for (String key : hits.keySet()) {
            Integer rankTotal = hits.get(key);
            Double value = (rankTotal / size);
            this.hitsValue.put(key, value);
            LOGGER.log(Level.INFO, key + "::" + "(" + rankTotal + "/" + size + "):" + value);
        }
       
       this.hitsValue.put(MRR, this.meanReciprocalRank);
       LOGGER.log(Level.INFO, "#### #### #### #### Mean Reciprocal Value::" + "(" + totalMrr + "/" + size + ")" + "=" + this.meanReciprocalRank);

        LOGGER.log(Level.INFO, "#### #### #### #### calculating Hits::"   );
        /*for(Integer index: hits.keySet()){
            Integer rankTotal=hits.get(index);
            Double value=(rankTotal/size);
            String key=Hits+index;
            if(hitsString.contains(key)){
              this.hitsValue.put(key, value);
              LOGGER.log(Level.INFO, key+"::" + "(" + rankTotal + "/" + size + "):" + value  );
            }
        }
        for(String key: hitsString){
            if(!hitsValue.containsKey(key)){
              this.hitsValue.put(key, 0.0);
            }
        }*/
        
     


       /*if (matchFlag) {
            double size = count;
            mrr = mrr / size;
            LOGGER.log(Level.INFO, "#### #### #### #### Mean Reciprocal Value::" + "(" + mrr + "/" + size + ")" + "=" + mrr);
        } else {
            mrr = 0.0;
            LOGGER.log(Level.INFO, "#### #### #### #### not a linguistic pattern MATCHED in our lexicon #### #### ignore the experiment!");
        }*/

     

    }


    private static ReciprocalResult getReciprocalRank(final List<String> ranking, final Map<String, Boolean> gold, Boolean commonWordFlag, Pair<String, String> qaldKb) {
        ReciprocalResult reciprocalElement = new ReciprocalResult(ranking, 0, 0.0);

        double reciprocalRank = 0;
//        EvalutionUtil.ifFalseCrash(IrAbstract.GoldContainsAllinRanking(ranking, gold),
//                "I cannot compute MRR");
        String qaldKB = qaldKb.getValue1();

        if (gold.containsKey(qaldKB)) {
            for (Integer i = 0; i < ranking.size(); i++) {
                String kb = ranking.get(i);
                if (kb.contains(qaldKb.getValue1())) {
                    String predicate = ranking.get(i);
                    reciprocalRank = 1.0 / (i + 1);
                    Integer rank = (i + 1);
                    Pair<String, String> rankedPair = EvaluationTriple.getRankedString(ranking, predictionRule, rank);
                    LOGGER.log(Level.INFO, ">>>> >>>>  >>>> >>>> >>>>  >>>> >>>> >>>> >>>> Proposed results:" + rankedPair.getValue0() + " " + rankedPair.getValue1());
                    LOGGER.log(Level.INFO, ">>>> >>>>  >>>> >>>> >>>>  >>>> >>>> >>>> >>>> QALD KB :" + qaldKB + " >>>> >>>> FOUND :" + " >>>> >>>>" + " RANK::" + rank + " RECIPROCAL RANK:" + reciprocalRank + "\n" + "\n");
                    ReciprocalResult reciprocalResult = new ReciprocalResult(predicate, rank, reciprocalRank);
                    //LOGGER.log(Level.INFO, ">>>> >>>> >>>> >>>> FOUND :" + qaldKb.getValue1() + " RANK::" + rank + " RECIPROCAL RANK:" + reciprocalRank);
                    return new ReciprocalResult(predicate, rank, reciprocalRank);
                }
            }
        } else {
            Pair<String, String> rankedPair = EvaluationTriple.getRankedString(ranking, predictionRule, -1);
            LOGGER.log(Level.INFO, ">>>> Proposed results:" + rankedPair.getValue0() + " " + rankedPair.getValue1());
            LOGGER.log(Level.INFO, ">>>>  QALD KB :" + qaldKB + " >>>> NOT FOUND" + "  RANK::" + reciprocalElement.getRank() + " RECIPROCAL RANK::" + reciprocalElement.getRank() + "\n" + "\n");
        }

        return reciprocalElement;
    }

    private static List<String> getKeysSortedByValue(
            Map<String, Double> unsortedMap, final boolean order) {
        List<Map.Entry<String, Double>> list
                = new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        List<String> sortedList = new ArrayList<String>();
        for (Map.Entry<String, Double> entry : list) {
            sortedList.add(entry.getKey());
        }
        return sortedList;
    }

    public String getMeanReciprocalRankStr() {
        return meanReciprocalRankStr;
    }

    @Override
    public int compare(Object arg0, Object arg1) {
        MeanReciprocalCalculation s1 = (MeanReciprocalCalculation) arg0;
        MeanReciprocalCalculation s2 = (MeanReciprocalCalculation) arg1;
        if (s1.meanReciprocalRank == s2.meanReciprocalRank) {
            return 0;
        } else if (s1.meanReciprocalRank > s2.meanReciprocalRank) {
            return 1;
        } else {
            return -1;
        }

    }

    public String getExperiment() {
        return experiment;
    }

    public Double getMeanReciprocalRank() {
        return meanReciprocalRank;
    }

    public Integer getTotalPattern() {
        return totalPattern;
    }

    public Integer getNumberOfPatterrnFoundNonZeroRank() {
        return numberOfPatterrnFoundNonZeroRank;
    }

    public Integer getNumberOfPatterrnFoundZeroRank() {
        return numberOfPatterrnFoundZeroRank;
    }

    public Map<String, ReciprocalResult> getPatternFound() {
        return patternFound;
    }

    public Map<String, ReciprocalResult> getPatternNotFound() {
        return patternNotFound;
    }

    @Override
    public String toString() {
        return "MeanReciprocalCalculation{" + "meanReciprocalRank=" + meanReciprocalRank + ", experiment=" + experiment + ", meanReciprocalRankStr=" + meanReciprocalRankStr + ", totalPattern=" + totalPattern + ", numberOfPatterrnFoundNonZeroRank=" + numberOfPatterrnFoundNonZeroRank + ", numberOfPatterrnFoundZeroRank=" + numberOfPatterrnFoundZeroRank + ", patternFound=" + patternFound + ", patternNotFound=" + patternNotFound + ", LOGGER=" + LOGGER + '}';
    }

    private boolean isValid(String prediction, String[] coulmns) {
        return EvaluationTriple.isValidForEvaluation(coulmns, prediction);
    }

    private Boolean isWordMatched(Set<String> lexiconWords, String word) {
        if (lexiconWords.contains(word)) {
            return true;
        } /*else {
            for (String nGram : lexiconWords) {
                 //nGram = nGram.replaceAll("\\d", " ");
                 //nGram = nGram.replaceAll("[^a-zA-Z0-9]", " ");
                 if(nGram.contains(word))
                     return true;
            }
        }*/
        return false;
    }

    private Map<String, Map<String, Double>> filter(Map<String, Map<String, Double>> lexiconWordKbs) {
        Map<String, Map<String, Double>> filtered=new TreeMap<String, Map<String, Double>>();
        for (String nGram : lexiconWordKbs.keySet()) {
            Map<String, Double> kb=lexiconWordKbs.get(nGram);
              nGram = nGram.replaceAll("\\d", " ");
              nGram = nGram.replaceAll("[^a-zA-Z0-9]", " ");
              nGram=nGram.strip().trim();
              
              Map<String, Double> results = new HashMap<String,Double>();
                if (filtered.containsKey(nGram)) {
                    results = filtered.get(nGram);
                    results.putAll(kb);
                    filtered.put(nGram, results);
                } else {
                    results.putAll(kb);
                    filtered.put(nGram, results);
                }
              
              filtered.put(nGram, kb); 
            }
        return filtered;
    }

    public static  Set<String> getHitsString() {
        return hitsString;
    }

    public Map<String, Double> getHitsValue() {
        return hitsValue;
    }
   


       /*if (rank > 0) {
                    Integer hitRank = 1;
                    if (hits.containsKey(rank) && rank > 0) {
                        hitRank = hits.get(rank) + 1;
                    }
                    hits.put(rank, hitRank);
                }*/

  

}
