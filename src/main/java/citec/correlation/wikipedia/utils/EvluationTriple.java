/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.parameters.ThresoldConstants;

/**
 *
 * @author elahi
 */
public class EvluationTriple implements ThresoldConstants {

   

    private String type = null;
    private String id = null;
    private String predicate = null;
    private String object = null;
    private String key = null;
    private String predictionRule = null;
    private String word = null;

    public EvluationTriple(String type, String predictionRule, String id, String predicate, String object,String word) {
        this.type = type;
        this.predictionRule = predictionRule;
        this.setPredicate(predicate);
        this.object = object;
        this.createKey();
    }

    public EvluationTriple(String LEXICON, String predicationRule, String key,String word) {
        this.type = type;
        this.predictionRule = predicationRule;
        this.key=key;
        this.word=word;
        this.parseKey();
    }

    private void createKey() {
        if (this.predictionRule.contains(ThresoldConstants.predict_l_for_s_given_po)) {
            this.key = this.predicate + " " + this.object;
        } else {
            this.key = this.object;
        }

    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }
    
    public static boolean match(EvluationTriple lexiconTriple, EvluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getPredicate().trim().strip();
        String qaldObject = qaldTriple.getPredicate().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }
    
    public static boolean match(EvluationTriple lexiconTriple, EvluationTriple qaldTriple, String predicationRule) {
        if (predicationRule.contains(predict_l_for_s_given_po)) {
            Boolean predeciateFlag=matchPredicate(lexiconTriple, qaldTriple);
            Boolean objectFlag=matchObject(lexiconTriple,qaldTriple);
           if(predeciateFlag&&objectFlag)
           return true;
        }
        return false;
    }
    
     public static boolean matchPredicate(EvluationTriple lexiconTriple, EvluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getPredicate().trim().strip();
        String qaldObject = qaldTriple.getPredicate().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }
    
    public static boolean matchObject(EvluationTriple lexiconTriple, EvluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getObject().trim().strip();
        String qaldObject = qaldTriple.getObject().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }


    @Override
    public String toString() {
        return "EvluationTriple{" + "type=" + type + ", id=" + id + ", predicate=" + predicate + ", object=" + object + ", key=" + key + ", predictionRule=" + predictionRule + '}';
    }

    private void parseKey() {
        String[] info = this.key.split(" ");
        this.predicate = info[0];
        this.object = info[1];
    }

    private void setPredicate(String predicate) {
        if (predicate.contains(":")) {
            String[] info = predicate.split(":");
            this.predicate = info[1];
        } else {
            this.predicate = predicate;
        }
    }

}
