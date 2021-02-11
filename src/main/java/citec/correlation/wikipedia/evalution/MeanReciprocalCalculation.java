/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.results.ReciprocalResult;
import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.utils.DoubleUtils;
import citec.correlation.wikipedia.utils.EvalutionUtil;
import citec.correlation.wikipedia.utils.EvaluationTriple;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
    private Integer totalPattern = null;
    @JsonProperty("Found")
    private Integer numberOfPatterrnFoundNonZeroRank = 0;
    //@JsonProperty("NotFound")
    @Ignore
    private Integer numberOfPatterrnFoundZeroRank = null;
    //@JsonProperty("Detail")
    @Ignore
    private Map<String, ReciprocalResult> patternFound = new TreeMap<String, ReciprocalResult>();
    //@JsonProperty("PatterrnFoundZeroRank")
    @JsonIgnore
    private Map<String, ReciprocalResult> patternNotFound = new TreeMap<String, ReciprocalResult>();
    @JsonIgnore
    private static Logger LOGGER = null;

    @JsonIgnore
    private List<String> commonWords = new ArrayList<String>();

    public MeanReciprocalCalculation() {

    }

    public MeanReciprocalCalculation(String experiment, List<Pair<String, Map<String, Double>>> rankings, List<Pair<String, Map<String, Boolean>>> gold, Logger LOGGER, List<String> commonWords) {
        this.experiment = experiment;
        this.LOGGER = LOGGER;
        this.commonWords = commonWords;
        this.computeWithRankingMap(rankings, gold);
    }

    public void computeWithRankingMap(List<Pair<String, Map<String, Double>>> rankings, List<Pair<String, Map<String, Boolean>>> gold) {
        EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(),
                "The size of predictions and gold should be identical, Usually not found element are in FALSE marked in gold");
        double mrr = 0;
        for (int i = 0; i < rankings.size(); i++) {
            Pair<String, Map<String, Double>> rankingsPredict = rankings.get(i);
            Pair<String, Map<String, Boolean>> wordGold = gold.get(i);
            String word = rankingsPredict.getValue0();
            Boolean commonFlag = false;

            LOGGER.log(Level.INFO, ">>>> checking  linguistic pattern:: " + word);

            if (this.commonWords.contains(word)) {
                commonFlag = true;
                LOGGER.log(Level.INFO, "######## ######## ######## pattern FOUND in qald ######## ######## ########");
            }

            ReciprocalResult reciprocalElement = getReciprocalRank(getKeysSortedByValue(rankingsPredict.getValue1(), DESCENDING),
                    wordGold.getValue1(), commonFlag);

            if (reciprocalElement.getRank() > 0) {
                this.patternFound.put(word, reciprocalElement);
            } else {
                patternNotFound.put(word, reciprocalElement);
                if (!commonFlag) {
                    LOGGER.log(Level.INFO,"pattern NOT FOUND in qald" + ",so rank::" + reciprocalElement.getRank() + " reciprocalRank!!:" + reciprocalElement.getRank());
                } else {
                    LOGGER.log(Level.INFO,"######## "+" KB NOT FOUND in qald" + ",so rank::" + reciprocalElement.getRank() + " reciprocalRank!!:" + reciprocalElement.getRank());
                    LOGGER.log(Level.INFO, "######## ######## ######## ######## ######## ######## ######## ########");
                }

            }

            mrr += reciprocalElement.getReciprocalRank();

        }

        mrr /= rankings.size();

        this.meanReciprocalRank = mrr;
        this.meanReciprocalRank = DoubleUtils.formatDouble(mrr);
        this.meanReciprocalRankStr = FormatAndMatch.doubleFormat(meanReciprocalRank);
        this.numberOfPatterrnFoundNonZeroRank = patternFound.size();
        this.numberOfPatterrnFoundZeroRank = patternNotFound.size();
        this.totalPattern = patternFound.size() + patternNotFound.size();

    }

    private static ReciprocalResult getReciprocalRank(final List<String> ranking, final Map<String, Boolean> gold, Boolean commonWordFlag) {
        ReciprocalResult reciprocalElement = new ReciprocalResult(ranking, 0, 0.0);
        Boolean rankFoundflag=false;

        EvalutionUtil.ifFalseCrash(IrAbstract.GoldContainsAllinRanking(ranking, gold),
                "I cannot compute MRR");
        
        //LOGGER.log(Level.INFO, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  :" + gold.keySet());


        double reciprocalRank = 0;
        for (Integer i = 0; i < ranking.size(); i++) {
            String kb = ranking.get(i);
            //LOGGER.log(Level.INFO, "rank:"+ i+" "+ "kb:"+ kb);
            //temporarily closed...
            /*if (i == 10) {
                continue;
            }*/

            if (gold.containsKey(ranking.get(i))) {

                if (gold.get(ranking.get(i))) {
                    String predicate = ranking.get(i);
                    reciprocalRank = 1.0 / (i + 1);
                    Integer rank = (i + 1);
                    LOGGER.log(Level.INFO, "###################### KB MATCHED with qald::"+EvaluationTriple.qaldStr(ranking.get(i)));
                    //LOGGER.log(Level.INFO, "########### matched KB :" + EvaluationTriple.qaldStr(ranking.get(i)));
                    //LOGGER.log(Level.INFO, "###################### check rank of this KB in our lexicon:");
                    LOGGER.log(Level.INFO, "######################  rank list of KB in our lexicon:" + EvaluationTriple.getString(ranking));
                    LOGGER.log(Level.INFO, "RANK::" + rank +" RECIPROCAL RANK:"+reciprocalRank);
                    //LOGGER.log(Level.INFO, "RECIPROCAL RANK::" + reciprocalRank);
                    //LOGGER.log(Level.INFO, "#####################################################################################");
                    rankFoundflag=true;
                    LOGGER.log(Level.INFO, "######## ######## ######## ######## ######## ######## ######## ########");
                    return new ReciprocalResult(predicate, rank, reciprocalRank);
                }
            }
        }
        
        /* if (commonWordFlag&&!rankFoundflag) {
            //LOGGER.log(Level.INFO, "\n" + "FOUND in QALD ");
            //LOGGER.log(Level.INFO, "now checking the KB in the QALD");
            //LOGGER.log(Level.INFO, "total number of KB for this pattern in our lexicon:" + EvaluationTriple.getString(ranking));
            //LOGGER.log(Level.INFO, ">>> qald KBs:");
            //for (String kb : gold.keySet()) {
            //    LOGGER.log(Level.INFO, kb+" "+gold.get(kb));
            //}
         LOGGER.log(Level.INFO, "######################  KBs for this pattern in qald:" + gold.keySet());   
         LOGGER.log(Level.INFO, "######################  Rank list of KB for this pattern:" + EvaluationTriple.getString(ranking));

        }*/
         
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

}
