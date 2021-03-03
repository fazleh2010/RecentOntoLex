/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.experiments.ThresoldConstants;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.Coherence;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.OBJECT;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.predict_l_for_o_given_p;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.predict_l_for_s_given_o;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.predict_l_for_s_given_po;
import static citec.correlation.wikipedia.experiments.ThresoldConstants.predict_localized_l_for_s_given_p;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import static citec.correlation.wikipedia.main.Evaluation.calculateMeanReciprocal;
import static citec.correlation.wikipedia.main.Evaluation.createExperiments;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class Main implements ThresoldConstants{
    
    public static void main(String[] args) throws Exception {
      //create experiments
      //createExperiments();
      //doEvalution();
      
    }
    
      public static void createExperiments() throws Exception {
        String rawFileDir = null;
        String directory = qald9Dir + OBJECT + "/";
        String baseDir = "/home/elahi/new/dbpediaFiles/unlimited/unlimited/";
        Logger LOGGER = Logger.getLogger(GeneratedExperimentData.class.getName());
        String outputDir = qald9Dir;
        String type = null;
        Map<String, ThresoldsExperiment> associationRulesExperiment = new HashMap<String, ThresoldsExperiment>();

        List<String> predictLinguisticGivenKB = new ArrayList<String>(Arrays.asList(
                //predict_l_for_o_given_p
                //predict_l_for_s_given_po
                //predict_l_for_s_given_o
                //predict_l_for_o_given_p,
                //predict_l_for_o_given_s,
                //predict_l_for_o_given_sp
                predict_localized_l_for_s_given_p
        ));
        List<String> interestingness = new ArrayList<String>();
        interestingness.add(ThresoldConstants.Cosine);
        interestingness.add(ThresoldConstants.Coherence);
        interestingness.add(ThresoldConstants.AllConf);
        interestingness.add(ThresoldConstants.MaxConf);
        interestingness.add(ThresoldConstants.Kulczynski);
        interestingness.add(ThresoldConstants.IR);

        for (String prediction : predictLinguisticGivenKB) {
            if (prediction.equals(predict_l_for_s_given_po)
                    || prediction.equals(predict_l_for_s_given_o)) {
                type = ThresoldConstants.OBJECT;
            } else if (prediction.contains(predict_l_for_o_given_p) || prediction.contains(predict_localized_l_for_s_given_p)) {
                type = ThresoldConstants.PREDICATE;
            }
            associationRulesExperiment = Evaluation.createExperiments(type);
            //or (String rule : interestingness) {
            GeneratedExperimentData ProcessFile = new GeneratedExperimentData(baseDir, outputDir, prediction, null, associationRulesExperiment, LOGGER, ".csv");

            //}
        }
    }
      
    


}
