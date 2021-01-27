/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import static citec.correlation.wikipedia.parameters.ThresoldConstants.conAB;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.conBA;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.supA;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.supB;
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
    
    private List<Double> supList = Arrays.asList(100.0);
    private List<Double> supBList = Arrays.asList(100.0);
    private List<Double> confABList = Arrays.asList(0.045, 0.5);
    private List<Double> confBAList = Arrays.asList(0.045, 0.5);
    private List<Double> AllConfList = Arrays.asList(0.001, 0.045);
    private List<Double> MaxConfList = Arrays.asList(0.001, 0.045);
    private List<Double> IrList = Arrays.asList(0.001, 0.045);
    private List<Double> KulczynskiList = Arrays.asList(0.001, 0.045);
    private List<Double> CosineList = Arrays.asList(0.001, 0.045);
    private List<Double> CoherenceList = Arrays.asList(0.001, 0.045);
    private List<Integer> numberOfRules = Arrays.asList(200, 1000);
    
    
    private LinkedHashMap<String,ThresoldELement> thresoldELements = new LinkedHashMap<String,ThresoldELement>();

    public ThresoldsExperiment(String associationRule) {
        Integer index=0;
        for (Integer numberOfRule : numberOfRules) {
            for (Double supA : supList) {
                for (Double supB : supBList) {
                    for (Double confAB : confABList) {
                        for (Double confBA : confBAList) {
                            for (Double probabiltyValue : this.getInterestingList(associationRule)) {
                                index= index+1;
                                ThresoldELement thresoldELement=new ThresoldELement(supA, supB, confAB, confBA, associationRule, probabiltyValue, numberOfRule);
                                String line=associationRule+index.toString()+"-"+ thresoldELement;
                                thresoldELements.put(line,thresoldELement);
                            }
                        }
                    }
                }
            }
        }

    }


    public LinkedHashMap<String,ThresoldELement> getThresoldELements() {
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

    public class ThresoldELement {

        private Integer rules = 0;
        private String type = null;
        private LinkedHashMap<String, Double> values = new LinkedHashMap<String, Double>();

        public ThresoldELement(Double supA, Double supB, Double confAB, Double confBA, String type, Double probabiltyValue, Integer numberOfRules) {
            this.values.put(ThresoldConstants.supA, supA);
            this.values.put(ThresoldConstants.supB, supB);
            this.values.put(ThresoldConstants.conAB, confAB);
            this.values.put(ThresoldConstants.conBA, confBA);
            this.type=type;
            this.values.put(type, probabiltyValue);
            this.rules = numberOfRules;
        }

        public Integer getNumberOfRules() {
            return rules;
        }

        public Map<String, Double> getValues() {
            return values;
        }

        @Override
        public String toString() {
            return  NUMBER_OF_RULES+ "_"+rules+ "-"
                   + supA+ "_"+values.get(supA)+"-"
                   + supB+"_"+values.get(supB)+"-"
                   + conAB+"_"+values.get(conAB)+"-"
                   + conBA+"_"+values.get(conBA)+"-"
                   + this.type+"_"+values.get(type);
        }

    }

}
