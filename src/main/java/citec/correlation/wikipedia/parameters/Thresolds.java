/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author elahi
 */
public class Thresolds implements ThresoldConstants {

    private List<Double> supAs = Arrays.asList(100.0, 500.0);
    private List<Double> supBs = Arrays.asList(100.0, 500.0);
    private List<Double> confABs = Arrays.asList(0.045, 0.5);
    private List<Double> confBAs = Arrays.asList(0.045, 0.5);

    private List<Double> AllConfs = Arrays.asList(0.001, 0.045);
    private List<Double> MaxConfs = Arrays.asList(0.001, 0.045);
    private List<Double> IRs = Arrays.asList(0.001, 0.045);
    private List<Double> Kulczynskis = Arrays.asList(0.001, 0.045);
    private List<Double> Cosines = Arrays.asList(0.001, 0.045);
    private List<Double> Coherences = Arrays.asList(0.001, 0.045);
    private List<Integer> numberOfRules = Arrays.asList(200, 1000);
    private List<ThresoldELement> thresoldELements = new ArrayList<ThresoldELement>();

    public Thresolds(String type) {
        for (Integer numberOfRule : numberOfRules) {
            for (Double supA : supAs) {
                for (Double supB : supBs) {
                    for (Double confAB : confABs) {
                        for (Double confBA : confBAs) {
                            List<Double> interestingness = this.getInterestingList(type);
                            for (Double probabiltyValue : interestingness) {
                                ThresoldELement thresoldELement = new ThresoldELement(supA, supB, confAB, confBA, probabiltyValue, numberOfRule);
                                 thresoldELements.add(thresoldELement);
                            }

                        }

                    }
                }
            }
        }
        this.display();

    }

    public List<ThresoldELement> getThresoldELements() {
        return thresoldELements;
    }

    public void display() {
        Integer index=0;
        for (ThresoldELement thresoldELement : thresoldELements) {
             index=index+1;
             System.out.println(index+" "+thresoldELement);
        }
    }



    private List<Double> getInterestingList(String type) {
        if (type.contains(AllConf)) {
            return AllConfs;
        } else if (type.contains(MaxConf)) {
            return MaxConfs;
        } else if (type.contains(IR)) {
            return IRs;
        } else if (type.contains(Kulczynski)) {
            return Kulczynskis;
        } else if (type.contains(Cosine)) {
            return Cosines;
        } else if (type.contains(Coherence)) {
            return Coherences;
        } else {
            return new ArrayList<Double>();
        }
    }

    public class ThresoldELement {

        private Double supA = 0.0;
        private Double supB = 0.0;
        private Double confAB = 0.0;
        private Double confBA = 0.0;
        private Double probabiltyValue = 0.0;
        private Integer numberOfRules = 0;

        public ThresoldELement(Double supA, Double supB, Double confAB, Double confBA, Double probabiltyValue, Integer numberOfRules) {
            this.supA = supA;
            this.supB = supB;
            this.confAB = confAB;
            this.confBA = confBA;
            this.probabiltyValue = probabiltyValue;
            this.numberOfRules = numberOfRules;
        }

        @Override
        public String toString() {
            return "supA=" + supA + ", supB=" + supB + ", confAB=" + confAB + ", confBA=" + confBA + ", probabiltyValue=" + probabiltyValue + ", numberOfRules=" + numberOfRules;
        }

    }
    
    public static void main(String []args){
         Thresolds thresold= new Thresolds(ThresoldConstants.Cosine);
         thresold.display();
    }

}
