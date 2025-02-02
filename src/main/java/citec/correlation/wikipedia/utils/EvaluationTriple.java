/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;
import citec.correlation.wikipedia.experiments.PredictionRules;

/**
 *
 * @author elahi
 */
public class EvaluationTriple implements PredictionRules {

    private static String predicate_object_pair_str = "predicate-object pair::";
    private static String subject_predicate_pair_str = "subject-predicate pair::";

    private static String object_str = "object::";
    private static String predicate_str = "predicate::";
    private static String subject_str = "subject::";

    private String type = null;
    private String id = null;
    private String predicate = null;
    private String subject = null;
    private String object = null;
    private String key = null;
    private String predictionRule = null;
    //private String word = null;
    private static Logger LOGGER = null;

    public EvaluationTriple(String type, String predictionRule, String id, String subject, String predicate, String object, String word, Logger LOGGER) {
        this.type = type;
        this.predictionRule = predictionRule;
        //this.predicate=this.modifyPredicate(predicate);
        //this.object=this.modifyObject(object);
        this.subject = subject;
        this.predicate = predicate;
        this.object = this.setObject(object);
        this.LOGGER = LOGGER;
        try {
            this.key = this.createKey(this.predictionRule, this.subject, this.predicate, this.object);
        } catch (Exception ex) {
            Logger.getLogger(EvaluationTriple.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public EvaluationTriple(String LEXICON, String predicationRule, String key) throws Exception {
        this.type = LEXICON;
        this.predictionRule = predicationRule;
        this.key = key;
        this.parseKey(predicationRule, key);
    }

    public EvaluationTriple(String LEXICON, String predicationRule, String toString, String tripleString, String word, Logger LOGGER) {
        this.predictionRule = predicationRule;
        String info[] = tripleString.split(" ");
        this.subject = info[0];
        this.predicate = this.preparePredicate(info[1]);
        this.object = this.setObject(info[2]);
        this.LOGGER = LOGGER;
        try {
            this.key = this.createKey(this.predictionRule, this.subject, this.predicate, this.object);
            // LOGGER.log(Level.WARNING, "word: "+word+" subject: " +subject+ " predicate: " +predicate+ "object: " +object);
            // LOGGER.log(Level.WARNING, "key: "+" "+key);
        } catch (Exception ex) {
            Logger.getLogger(EvaluationTriple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String createKey(String predictionRule, String subject, String predicate, String object) throws Exception {
        if (isPredict_l_for_s_given_po(predictionRule)
                || isPredict_po_for_s_given_l(predictionRule)
                || isPredict_po_for_s_given_localized_l(predictionRule)) {
            return predicate + " " + object;
        } else if (isPredict_l_for_s_given_o(predictionRule)) {
            return object;
        } else if (isPredict_l_for_o_given_s(predictionRule)) {
            return subject;
        } else if (isPredict_l_for_o_given_sp(predictionRule)) {
            return subject + " " + predicate;
        } else if (isPredict_l_for_o_given_p(predictionRule)) {
            return predicate;
        } else if (isPredict_l_for_s_given_p(predictionRule)) {
            return predicate;
        } else if (isPredict_localized_l_for_s_given_p(predictionRule)
                  ||isPredict_p_for_s_given_localized_l(predictionRule)
                  ||isPredict_p_for_o_given_localized_l(predictionRule)) {
            return predicate;
        } else {
            throw new Exception("can not create key, check the KB!!");
        }
    }

    public static boolean match(EvaluationTriple lexiconTriple, EvaluationTriple qaldTriple, String predicationRule) {
        if (predicationRule.equals(predict_l_for_s_given_po)
                || predicationRule.contains(predict_po_for_s_given_l)
                || predicationRule.contains(predict_po_for_s_given_localized_l)) {
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
        } else if (predicationRule.contains(predict_l_for_o_given_p)
                || predicationRule.contains(predict_l_for_s_given_p)
                || predicationRule.contains(predict_localized_l_for_s_given_p)
                || predicationRule.contains(predict_localized_l_for_s_given_p)
                || predicationRule.contains(predict_localized_l_for_s_given_p)
                || predicationRule.contains(predict_p_for_s_given_localized_l)
                || predicationRule.contains(predict_p_for_o_given_localized_l)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        }
        return false;
    }

    private void parseKey(String predictionRule, String keyT) throws Exception {
        if (predictionRule.equals(predict_l_for_s_given_po)
                || predictionRule.contains(predict_po_for_s_given_l)
                || predictionRule.contains(predict_po_for_s_given_localized_l)) {
            if (keyT.contains(" ")) {
                String[] info = keyT.split(" ");
                if (info.length >= 2) {
                    this.predicate = info[0];
                    this.object = info[1];
                } else if (info.length == 1) {
                    this.predicate = info[0];
                    this.object = "";
                }

            } else {
                throw new Exception("can not parse key, check the KB!!");
            }
        } else if (predictionRule.equals(predict_l_for_s_given_o)) {
            this.object = keyT;
        } else if (predictionRule.equals(predict_l_for_o_given_s)) {
            this.subject = keyT;
        } else if (predictionRule.equals(predict_l_for_o_given_sp)) {
            if (keyT.contains(" ")) {
                String[] info = keyT.split(" ");
                this.subject = info[0];
                this.predicate = info[1];
            } else {
                throw new Exception("can not parse keyy, check the KB!!");
            }
        } else if (predictionRule.equals(predict_localized_l_for_s_given_p)
                || predictionRule.equals(predict_l_for_o_given_p)
                || predictionRule.equals(predict_l_for_s_given_p)
                || predictionRule.contains(predict_p_for_s_given_localized_l)
                || predictionRule.contains(predict_p_for_o_given_localized_l)) {
            this.predicate = keyT;
        } else {
            throw new Exception("can not parse keyy, check the KB!!");
        }

    }

    public static Pair<String, String> getRankedString(List<String> ranking, String predictionRule, Integer rank) {
        String str = "\n";
        Integer index = 1;
        String type = getType(predictionRule);

        for (String key : ranking) {
            String line = null;
            if (predictionRule.equals(predict_l_for_s_given_po)
                    || predictionRule.contains(predict_po_for_s_given_l)
                    || predictionRule.contains(predict_po_for_s_given_localized_l)) {
                String predicate = null, object = null;
                String[] info = key.split(" ");
                if (info.length >= 2) {
                    predicate = info[0];
                    object = info[1];
                    line = index.toString() + " " + predicate + " " + object + "\n";
                } else if (info.length == 1) {
                    predicate = info[0];
                    object = "";
                    line = index.toString() + " " + predicate + " " + object + "\n";
                }
            } else if (predictionRule.equals(predict_l_for_s_given_o)) {
                line = index.toString() + " " + key + "\n";
            } else if (predictionRule.equals(predict_l_for_o_given_p)) {
                line = index.toString() + " " + key + "\n";
            } else if (predictionRule.equals(predict_l_for_s_given_p)) {
                line = index.toString() + " " + key + "\n";
            } else if (predictionRule.equals(predict_localized_l_for_s_given_p)
                    || predictionRule.contains(predict_p_for_s_given_localized_l)
                    || predictionRule.contains(predict_p_for_o_given_localized_l)) {
                line = index.toString() + " " + key + "\n";
            } else if (predictionRule.equals(predict_l_for_o_given_sp)) {
                String subject = null, predicate = null;
                String[] info = key.split(" ");
                if (info.length >= 2) {
                    subject = info[0];
                    predicate = info[1];
                    line = index.toString() + " " + subject + " " + predicate + "\n";
                } else if (info.length == 1) {
                    subject = info[0];
                    predicate = "";
                    line = index.toString() + " " + subject + " " + predicate + "\n";
                }
            } else if (predictionRule.equals(predict_l_for_o_given_s)) {
                line = index.toString() + " " + key + "\n";
            }
            str += line;
            if (rank == -1)
                ; else if (index > rank) {
                break;
            }
            index = index + 1;

        }

        str = str.substring(0, str.length() - 1);
        return new Pair<String, String>(type, str);
    }

    /*public static Pair<String, String> getString(List<String> ranking, String predictionRule) {
        String str = "\n";
        Integer index = 1;
        String type=getType(predictionRule);
        
        for (String key : ranking) {
            String line = null;
            if (predictionRule.contains(predict_l_for_s_given_po)) {
                String predicate = null, object = null;
                String[] info = key.split(" ");
                if (info.length >= 2) {
                    predicate = info[0];
                    object = info[1];
                    line = index.toString() + " " + predicate + " " + object + "\n";
                } else if (info.length == 1) {
                    predicate = info[0];
                    object = "";
                    line = index.toString() + " " + predicate + " " + object + "\n";
                }
            } else if (predictionRule.contains(predict_l_for_s_given_o)) {
                line = index.toString() + " " + key + "\n";
            } else if (predictionRule.contains(predict_l_for_o_given_p)) {
                line = index.toString() + " " + key + "\n";
            }

            str += line;
            index = index + 1;
        }
        str = str.substring(0, str.length() - 1);
        return new Pair<String, String>(type, str);
    }*/
    public static Pair<String, String> getString(String[] coulmns, String prediction) {
        String str = "", type = null;
        for (String kbLine : coulmns) {
            String line = kbLine + " ";
            str += line;
        }

        if (prediction.contains(predict_l_for_s_given_po)
                || prediction.contains(predict_po_for_s_given_l)
                || prediction.contains(predict_po_for_s_given_localized_l)) {
            type = predicate_object_pair_str;
        } else if (prediction.contains(predict_l_for_s_given_o)) {
            type = object_str;
        } else if (prediction.contains(predict_l_for_o_given_p)
                || prediction.contains(predict_p_for_s_given_localized_l)
                || prediction.contains(predict_p_for_o_given_localized_l)) {
            type = predicate_str;
        }

        if (prediction.contains(predict_l_for_s_given_po)
                || prediction.contains(predict_po_for_s_given_l)
                || prediction.contains(predict_po_for_s_given_localized_l)) {
            str = coulmns[2] + " " + coulmns[3];
        } else if (prediction.contains(predict_l_for_s_given_o)) {
            str = coulmns[3];
        } else if (prediction.contains(predict_l_for_o_given_p)
                  ||prediction.contains(predict_localized_l_for_s_given_p)
                  || prediction.contains(predict_p_for_s_given_localized_l)
                  || prediction.contains(predict_p_for_o_given_localized_l)) {
            str = coulmns[2];
        }

        return new Pair<String, String>(type, str);
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
        String str = "";
        if (key.contains(" ")) {
            String[] info = key.split(" ");
            String predicate = info[0];
            String object = info[1];
            return str + " predicate-object pair: " + predicate + " " + object;
        } else {
            return key;
        }
    }

    public String getSubject() {
        return subject;
    }

    public static void main(String[] args) {
        String blogName = "Java2blog is java blog";
        System.out.println("BlogName: " + blogName);
        // Let's put Java2blog in double quotes
        String line1 = "pair=\"2018-06-01\"^^http://www.w3.org/2001/XMLSchema#date";
        line1 = line1.replace("\"", "");
        System.out.println("line1: " + line1);
    }

    @Override
    public String toString() {
        return "EvaluationTriple{" + "type=" + type + ", id=" + id + ", predicate=" + predicate + ", subject=" + subject + ", object=" + object + ", key=" + key + ", predictionRule=" + predictionRule + ", LOGGER=" + LOGGER + '}';
    }

    private String preparePredicate(String string) {
        if (string.contains(":")) {
            String[] info = string.split(":");
            return info[1];
        }
        return string;
    }

    private String setObject(String object) {
        if (object.contains("@")) {
            String[] info = object.split("@");
            object = info[0];
        }
        return object;
    }

    private static String getType(String prediction) {
        String type = null;
        if (prediction.equals(predict_l_for_o_given_p)) {
            type = predicate_str;
        } else if (prediction.equals(predict_l_for_o_given_s)) {
            type = subject_str;
        } else if (prediction.equals(predict_l_for_o_given_sp)) {
            type = subject_predicate_pair_str;
        } else if (prediction.equals(predict_l_for_s_given_p)) {
            type = predicate_str;
        } else if (prediction.equals(predict_l_for_s_given_o)) {
            type = object_str;
        } else if (prediction.equals(predict_l_for_o_given_p)) {
            type = predicate_str;
        } else if (prediction.equals(predict_l_for_s_given_po)
                || prediction.contains(predict_po_for_s_given_l)
                || prediction.contains(predict_po_for_s_given_localized_l)) {
            type = predicate_object_pair_str;
        } else if (prediction.equals(predict_localized_l_for_s_given_p)
                || prediction.contains(predict_p_for_s_given_localized_l)
                || prediction.contains(predict_p_for_o_given_localized_l)) {
            type = predicate_str;
        }
        return type;
    }

    public static Boolean isValidForEvaluation(String[] coulmns, String prediction) {
        Boolean resultFlag = true;
        if (prediction.equals(predict_l_for_o_given_p)) {
            resultFlag = isValidForEvaluation(CsvFile.propertyIndex, coulmns);
        } else if (prediction.equals(predict_l_for_o_given_s)) {
            resultFlag = isValidForEvaluation(CsvFile.subjectIndex, coulmns);
        } else if (prediction.equals(predict_l_for_o_given_sp)) {
            resultFlag = isValidForEvaluation(CsvFile.subjectIndex, CsvFile.propertyIndex, coulmns);
        } else if (prediction.equals(predict_l_for_s_given_p)) {
            resultFlag = isValidForEvaluation(CsvFile.propertyIndex, coulmns);
        } else if (prediction.equals(predict_l_for_s_given_o)) {
            resultFlag = isValidForEvaluation(CsvFile.objectIndex, coulmns);
        } else if (prediction.equals(predict_l_for_o_given_p)) {
            resultFlag = isValidForEvaluation(CsvFile.propertyIndex, coulmns);
        } else if (prediction.equals(predict_localized_l_for_s_given_p)
                    || prediction.contains(predict_p_for_s_given_localized_l)
                    || prediction.contains(predict_p_for_o_given_localized_l)) {
            resultFlag = isValidForEvaluation(CsvFile.propertyIndex, coulmns);
        } else if (prediction.equals(predict_l_for_s_given_po)
                || prediction.equals(predict_po_for_s_given_l)
                || prediction.equals(predict_po_for_s_given_localized_l)) {
            resultFlag = isValidForEvaluation(CsvFile.propertyIndex, CsvFile.objectIndex, coulmns);
            // LOGGER.log(Level.INFO, "prediction: " + prediction + " " +coulmns[CsvFile.propertyIndex]+ " " +coulmns[CsvFile.objectIndex]);

        }

        return resultFlag;
    }

    private static Boolean isValidForEvaluation(Integer index, String[] coulmns) {
        if (coulmns[index].contains("-") || coulmns[index].length() <= 1) {
            return false;
        }
        return true;
    }

    private static Boolean isValidForEvaluation(Integer indexA, Integer indexB, String[] coulmns) {
        if (coulmns[indexA].contains("-") || coulmns[indexA].length() <= 1) {
            return false;
        }
        if (coulmns[indexB].contains("-") || coulmns[indexB].length() <= 1) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean isPredict_l_for_s_given_po(String predictionRule) {
        return predictionRule.contains(predict_l_for_s_given_po);
    }

    @Override
    public Boolean isPredict_l_for_s_given_o(String predictionRule) {
        return predictionRule.contains(predict_l_for_s_given_o);
    }

    @Override
    public Boolean isPredict_l_for_o_given_s(String predictionRule) {
        return predictionRule.contains(predict_l_for_o_given_s);
    }

    @Override
    public Boolean isPredict_l_for_o_given_sp(String predictionRule) {
        return predictionRule.contains(predict_l_for_o_given_sp);
    }

    @Override
    public Boolean isPredict_l_for_o_given_p(String predictionRule) {
        return predictionRule.contains(predict_l_for_o_given_p);
    }

    @Override
    public Boolean isPredict_l_for_s_given_p(String predictionRule) {
        return predictionRule.contains(predict_l_for_s_given_p);
    }

    @Override
    public Boolean isPredict_localized_l_for_s_given_p(String predictionRule) {
        return predictionRule.contains(predict_localized_l_for_s_given_p);
    }

    @Override
    public Boolean isPredict_po_for_s_given_l(String predictionRule) {
        return predictionRule.contains(predict_po_for_s_given_l);
    }

    @Override
    public Boolean isPredict_po_for_s_given_localized_l(String predictionRule) {
        return predictionRule.contains(predict_po_for_s_given_localized_l);
    }

    @Override
    public Boolean isPredict_p_for_s_given_localized_l(String predictionRule) {
        return predictionRule.contains(predict_p_for_s_given_localized_l);
    }

    @Override
    public Boolean isPredict_p_for_o_given_localized_l(String predictionRule) {
         return predictionRule.contains(predict_p_for_o_given_localized_l);
    }

}
