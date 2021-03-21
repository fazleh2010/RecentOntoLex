/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_po;

/**
 *
 * @author elahi
 */
public interface PredictionRulesInterface {
    public String predict_l_for_s_given_po_Key(String predictionRule,String subject, String predicate, String object);
    
}
