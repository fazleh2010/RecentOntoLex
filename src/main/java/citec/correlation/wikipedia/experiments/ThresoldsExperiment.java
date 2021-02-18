/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.experiments;

import static citec.correlation.wikipedia.experiments.ThresoldConstants.supA;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.supB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class ThresoldsExperiment implements ThresoldConstants {

    /*private List<Double> supList = Arrays.asList(100.0, 500.0);
    private List<Double> supBList = Arrays.asList(100.0, 500.0);
    private List<Double> confABList = Arrays.asList(0.045, 0.5);
    private List<Double> confBAList = Arrays.asList(0.045, 0.5);
    private List<Double> AllConfList = Arrays.asList(0.001, 0.045);
    private List<Double> MaxConfList = Arrays.asList(0.001, 0.045);
    private List<Double> IrList = Arrays.asList(0.001, 0.045);
    private List<Double> KulczynskiList = Arrays.asList(0.001, 0.045);
    private List<Double> CosineList = Arrays.asList(0.001, 0.045);
    private List<Double> CoherenceList = Arrays.asList(0.001, 0.045);
    private List<Integer> numberOfRules = Arrays.asList(200, 1000);*/
    
    
    
    
    ///////////////////Main Thresold For po and o rules////////////////////////
    private List<Double> supAList = Arrays.asList(10.0, 200.0);
    private List<Double> supBList = Arrays.asList(20.0, 100.0);
    private List<Double> confABList = Arrays.asList(0.1, 0.8);
    private List<Double> confBAList = Arrays.asList(0.001, 0.8);

    public List<Double> CosineList = Arrays.asList(0.001, 0.045);
    public List<Double> AllConfList = Arrays.asList(0.001, 0.045);
    public List<Double> MaxConfList = Arrays.asList(0.001, 0.045);
    public List<Double> IrList = Arrays.asList(0.001, 0.045);
    public List<Double> KulczynskiList = Arrays.asList(0.001, 0.045);
    public List<Double> CoherenceList = Arrays.asList(0.001, 0.045);
    public Map<String, List<Double>> interestingness = new TreeMap<String, List<Double>>();
    public static List<Integer> numberOfRules = Arrays.asList(1000,4000, 8000);
    public static List<Integer> nGram = Arrays.asList(1);
    ///////////////////Main Thresold For po and o rules////////////////////////
    
    
    ///////////////////Main Thresold For p////////////////////////
    /*private List<Double> supAList = Arrays.asList(1.0,10.0,50.0);
    private List<Double> supBList = Arrays.asList(1.0,10.0,50.0);
    private List<Double> confABList = Arrays.asList(0.001,0.05);
    private List<Double> confBAList = Arrays.asList(0.001,0.05);

    public List<Double> CosineList = Arrays.asList(0.001,0.045);
    public List<Double> AllConfList = Arrays.asList(0.001,0.045);
    public List<Double> MaxConfList = Arrays.asList(0.001,0.045);
    public List<Double> IrList = Arrays.asList(0.001,0.045);
    public List<Double> KulczynskiList = Arrays.asList(0.001,0.045);
    public List<Double> CoherenceList = Arrays.asList(0.001,0.045);
    public Map<String, List<Double>> interestingness = new TreeMap<String, List<Double>>();
    public static List<Integer> numberOfRules = Arrays.asList(10000);
    public static List<Integer> nGram = Arrays.asList(1);*/
    
    
    /*private List<Double> supAList = Arrays.asList(10.0, 200.0);
    private List<Double> supBList = Arrays.asList(10.0, 200.0);
    private List<Double> confABList = Arrays.asList(0.05, 0.5);
    private List<Double> confBAList = Arrays.asList(0.05, 0.5);
    
    public List<Double> CosineList = Arrays.asList(0.05, 0.5);
    public List<Double> AllConfList = Arrays.asList(0.05, 0.5);
    public List<Double> MaxConfList = Arrays.asList(0.05, 0.5);
    public List<Double> IrList = Arrays.asList(0.05, 0.5);
    public List<Double> KulczynskiList = Arrays.asList(0.05, 0.5);
    public List<Double> CoherenceList = Arrays.asList(0.05, 0.5);
    public Map<String, List<Double>> interestingness = new TreeMap<String, List<Double>>();
    public static List<Integer> numberOfRules = Arrays.asList(3000, 10000);
    */
    //public static List<Integer> nGram = Arrays.asList(1,2,3,4);


    private LinkedHashMap<String, ThresoldELement> thresoldELements = new LinkedHashMap<String, ThresoldELement>();

    public ThresoldsExperiment() {
        interestingness.put(ThresoldConstants.Cosine, CosineList);
        interestingness.put(ThresoldConstants.AllConf, AllConfList);
        interestingness.put(ThresoldConstants.MaxConf, MaxConfList);
        interestingness.put(ThresoldConstants.IR, IrList);
        interestingness.put(ThresoldConstants.Kulczynski, KulczynskiList);
        interestingness.put(ThresoldConstants.Coherence, CoherenceList);
    }

    /*public ThresoldsExperiment(String associationRule) {
        Integer index = 0;
        for (Integer numberOfRule : numberOfRules) {
            for (Double supA : supAList) {
                for (Double supB : supBList) {
                    for (Double confAB : confABList) {
                        for (Double confBA : confBAList) {
                            for (Double probabiltyValue : this.getInterestingList(associationRule)) {
                                index = index + 1;
                                ThresoldELement thresoldELement = new ThresoldELement(supA, supB, confAB, confBA, associationRule, probabiltyValue, numberOfRule);
                                //String line=associationRule+index.toString()+"-"+ thresoldELement;
                                String line = associationRule + "-" + thresoldELement;
                                thresoldELements.put(line, thresoldELement);
                            }
                        }
                    }
                }
            }
        }

    }*/

    public ThresoldsExperiment(String associationRule) {
        Integer index = 0;
        for ( Integer n_gram:nGram) {
            for (Integer numberOfRule : numberOfRules) {
                for (Double supA : supAList) {
                    for (Double supB : supBList) {
                        for (Double confAB : confABList) {
                            for (Double confBA : confBAList) {
                                for (Double probabiltyValue : this.getInterestingList(associationRule)) {
                                    index = index + 1;
                                    ThresoldELement thresoldELement = new ThresoldELement(supA, supB, confAB, confBA, associationRule, probabiltyValue, numberOfRule,n_gram);
                                    //String line=associationRule+index.toString()+"-"+ thresoldELement;
                                    String line = associationRule + "-" + thresoldELement;
                                    thresoldELements.put(line, thresoldELement);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public LinkedHashMap<String, ThresoldELement> getThresoldELements() {
        return thresoldELements;
    }

    private List<Double> getInterestingList(String type) {
        if (type.contains(AllConf)) {
            return AllConfList;
        } else if (type.contains(MaxConf)) {
            return MaxConfList;
        } else if (type.contains(IR)) {
            return IrList;
        } else if (type.contains(Kulczynski)) {
            return KulczynskiList;
        } else if (type.contains(Cosine)) {
            return CosineList;
        } else if (type.contains(Coherence)) {
            return CoherenceList;
        } else {
            return new ArrayList<Double>();
        }
    }

    public Map<String, List<Double>> getInterestingness() {
        return interestingness;
    }

    @Override
    public String toString() {
        return "ThresoldsExperiment{" + "thresoldELements=" + thresoldELements + '}';
    }

    public class ThresoldELement implements ThresoldConstants {

        private Integer rules = 0;
        private Integer n_gram = 0;
        private String type = null;
        private LinkedHashMap<String, Double> givenThresolds = new LinkedHashMap<String, Double>();

        public ThresoldELement(Double supA, Double supB, Double confAB, Double confBA, String type, Double probabiltyValue, Integer numberOfRules,Integer n_gram) {
            this.givenThresolds.put(ThresoldConstants.supA, supA);
            this.givenThresolds.put(ThresoldConstants.supB, supB);
            this.givenThresolds.put(ThresoldConstants.condAB, confAB);
            this.givenThresolds.put(ThresoldConstants.condBA, confBA);
            this.type = type;
            this.givenThresolds.put(type, probabiltyValue);
            this.rules = numberOfRules;
            this.n_gram=n_gram;
        }
         public ThresoldELement(Double supA, Double supB, Double confAB, Double confBA, String type, Double probabiltyValue, Integer numberOfRules) {
            this.givenThresolds.put(ThresoldConstants.supA, supA);
            this.givenThresolds.put(ThresoldConstants.supB, supB);
            this.givenThresolds.put(ThresoldConstants.condAB, confAB);
            this.givenThresolds.put(ThresoldConstants.condBA, confBA);
            this.type = type;
            this.givenThresolds.put(type, probabiltyValue);
            this.rules = numberOfRules;
        }

        public Integer getNumberOfRules() {
            return rules;
        }

        public Integer getN_gram() {
            return n_gram;
        }

        public LinkedHashMap<String, Double> getGivenThresolds() {
            return givenThresolds;
        }

        @Override
        public String toString() {
            String n_gramString=null;
            if(this.n_gram==0)
                n_gramString="1-4";
            else
                n_gramString=this.n_gram.toString();
            
            //nGram + "_" + n_gramString + "-"
            
            return   nGram + "_" + n_gramString + "-"
                    +numRule + "_" + rules + "-"
                    + supA + "_" + givenThresolds.get(supA) + "-"
                    + supB + "_" + givenThresolds.get(supB) + "-"
                    + condAB + "_" + givenThresolds.get(condAB) + "-"
                    + condBA + "_" + givenThresolds.get(condBA) + "-"
                    + type + "_" + givenThresolds.get(type);
        }

    }

}
