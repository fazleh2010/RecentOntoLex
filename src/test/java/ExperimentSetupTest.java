
import citec.correlation.wikipedia.parameters.ThresoldConstants;
import citec.correlation.wikipedia.parameters.ThresoldsExperiment;
import citec.correlation.wikipedia.parameters.ThresoldsExperiment.ThresoldELement;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elahi
 */
public class ExperimentSetupTest implements ThresoldConstants {
    
    public static void main(String[] args) {
        Map<String, ThresoldsExperiment> associationRulesExperiment = new TreeMap<String, ThresoldsExperiment>();
        for (String associationRule : interestingness) {
            ThresoldsExperiment thresold = new ThresoldsExperiment(associationRule);
            associationRulesExperiment.put(associationRule, thresold);
        }
        Integer index=0;
        for (String associationRule : associationRulesExperiment.keySet()) {
            ThresoldsExperiment thresold = associationRulesExperiment.get(associationRule);
            for (String experiment : thresold.getThresoldELements().keySet()) {
                index = index + 1;
                ThresoldELement thresoldELement = thresold.getThresoldELements().get(experiment);
                 System.out.println(index+"-"+experiment);
            }

        }
        
    }
    
}
