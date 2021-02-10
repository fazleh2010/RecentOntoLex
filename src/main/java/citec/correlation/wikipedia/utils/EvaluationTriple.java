/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.parameters.ThresoldConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author elahi
 */
public class EvaluationTriple implements ThresoldConstants {

    private String type = null;
    private String id = null;
    private String predicate = null;
    private String subject = null;
    private String object = null;
    private String key = null;
    private String predictionRule = null;
    private String word = null;
    private Logger LOGGER=null;

    public EvaluationTriple(String type, String predictionRule, String id, String predicate, String object,String word,Logger LOGGER) throws Exception {
        this.type = type;
        this.predictionRule = predictionRule;
        //this.predicate=this.modifyPredicate(predicate);
        //this.object=this.modifyObject(object);
        this.predicate=predicate;
        this.object=object;
        this.LOGGER=LOGGER;
        this.key=this.createKey(this.predictionRule,this.subject,this.predicate,this.object);
        
    }

    public EvaluationTriple(String LEXICON, String predicationRule, String key,String word) throws Exception {
        this.type = LEXICON;
        this.predictionRule = predicationRule;
        this.key=key;
        this.parseKey(predicationRule,key);
        this.word=word;
    }

    private String createKey(String predictionRule, String subject, String predicate, String object) throws Exception {
        if (predictionRule.contains(predict_l_for_s_given_po)) {
            return predicate + " " + object;
        } else if (predictionRule.contains(predict_l_for_s_given_o)) {
            return object;
        } else if (predictionRule.contains(predict_l_for_o_given_s)) {
            return subject;
        } else if (predictionRule.contains(predict_l_for_o_given_sp)) {
            return subject + " " + predicate;
        } else if (predictionRule.contains(predict_l_for_o_given_p)) {
            return predicate;
        } else if (predictionRule.contains(predict_l_for_s_given_p)) {
            return predicate;
        } else {
            throw new Exception("can not create key, check the KB!!");
        }
    }
    
    private void parseKey(String predictionRule, String keyT) throws Exception {
        if (predictionRule.contains(predict_l_for_s_given_po)) {
            if (keyT.contains(" ")) {
                String[] info = keyT.split(" ");
                this.predicate = info[0];
                this.object = info[1];
            } else {
                throw new Exception("can not parse key, check the KB!!");
            }
        } else if (predictionRule.contains(predict_l_for_s_given_o)) {
            this.object = keyT;
        } else if (predictionRule.contains(predict_l_for_o_given_s)) {
            this.subject = keyT;
        } else if (predictionRule.contains(predict_l_for_o_given_sp)) {
            if (keyT.contains(" ")) {
                String[] info = keyT.split(" ");
                this.subject = info[0];
                this.predicate = info[1];
            } else {
                throw new Exception("can not parse keyy, check the KB!!");
            }
        } else if (predictionRule.contains(predict_l_for_o_given_p)) {
            this.predicate = keyT;
        } else if (predictionRule.contains(predict_l_for_s_given_p)) {
            this.predicate = keyT;
        } else {
            throw new Exception("can not parse keyy, check the KB!!");
        }

    }

    
    public static boolean match(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple, String predicationRule) {
        if (predicationRule.contains(predict_l_for_s_given_po)) {
            if (matchPredicate(lexiconTriple, qaldTriple) && matchObject(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_s_given_o)) {
            if (matchObject(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_o_given_s)) {
            if (matchSubject(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_o_given_sp)) {
            if (matchSubject(lexiconTriple, qaldTriple) && matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_o_given_p)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_s_given_p)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        }
        return false;
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
    
    public static boolean match(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getPredicate().trim().strip();
        String qaldObject = qaldTriple.getPredicate().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }
    
     public static boolean matchPredicate(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getPredicate().trim().strip();
        String qaldObject = qaldTriple.getPredicate().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }
    
    public static boolean matchObject(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple) {
        String lexObject = lexiconTriple.getObject().trim().strip();
        String qaldObject = qaldTriple.getObject().trim().strip();
        if (lexObject.contains(qaldObject)) {
            return true;
        }
      return false;
    }
    
    public static boolean matchSubject(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple) {
        String lexSubject = lexiconTriple.getSubject().trim().strip();
        String qaldSubject = qaldTriple.getSubject().trim().strip();
        if (lexSubject.contains(qaldSubject)) {
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        return "EvluationTriple{" + "type=" + type + ", id=" + id + ", predicate=" + predicate + ", object=" + object + ", key=" + key + ", predictionRule=" + predictionRule + '}';
    }

   
    private String modifyObject(String object) {
        object = object.replace("\"", "");
        if (object.contains("@")) {
            String[] info = object.split("@");
            object = info[0];
        } else if (object.contains(" ")) {
            object = object.replace(" ", "+");
        } 
        return object;
    }

    private String modifyPredicate(String predicate) {
        if (predicate.contains(":")) {
            String[] info = predicate.split(":");
            predicate = info[1];
        } else if (predicate.contains("/")) {
            String[] info = predicate.split("/");
           predicate = info[1];
        } 
        return predicate;
    }
    
    public static String qaldStr(String key) {
        String str = "\n";
        if (key.contains(" ")) {
            String[] info = key.split(" ");
            String predicate = info[0];
            String object = info[1];
            return str + " predicate-object pair: " + predicate + " " + object + "\n";
        }
        else
            return key;
    }
    public static String getString(List<String> ranking) {
        String str = "\n";
        Integer index = 1;
        String line = null;
        for (String key : ranking) {
            if (key.contains(" ")) {
                String[] info = key.split(" ");
                String predicate = info[0];
                String object = info[1];
                line = index.toString() + " predicate-object pair: " + predicate + " " + object + "\n";
            } else {
                line = index.toString() + " object : " + key + "\n";
            }

            str += line;
            index = index + 1;
        }
        return str;
    }

    public String getSubject() {
        return subject;
    }

    public static void main(String[] args) {
        String blogName = "Java2blog is java blog";
        System.out.println("BlogName: " + blogName);
        // Let's put Java2blog in double quotes
        String line1 ="pair=\"2018-06-01\"^^http://www.w3.org/2001/XMLSchema#date";
        line1 = line1.replace("\"", "");
        System.out.println("line1: " + line1);
    }


}
