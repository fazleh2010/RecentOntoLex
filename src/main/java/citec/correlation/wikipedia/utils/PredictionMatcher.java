/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_p;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_s;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_sp;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_o;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_p;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_po;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_po_for_s_given_l;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_po_for_s_given_localized_l;
import static citec.correlation.wikipedia.utils.EvaluationTriple.matchObject;
import static citec.correlation.wikipedia.utils.EvaluationTriple.matchPredicate;
import static citec.correlation.wikipedia.utils.EvaluationTriple.matchSubject;
import java.util.List;
import org.javatuples.Pair;
import citec.correlation.wikipedia.experiments.PredictionRules;

/**
 *
 * @author elahi
 */
public class PredictionMatcher implements PredictionRules {

    private static String object_str = "object::";
    private static String predicate_str = "predicate::";
    private static String subject_str = "subject::";
    private static String predicate_object_pair_str = "predicate-object pair::";
    private static String subject_predicate_pair_str = "subject-predicate pair::";

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
        } else if (predicationRule.contains(predict_l_for_o_given_p)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(predict_l_for_s_given_p)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        } else if (predicationRule.contains(PredictionRules.predict_localized_l_for_s_given_p)) {
            if (matchPredicate(lexiconTriple, qaldTriple)) {
                return true;
            }
        }
        return false;
    }

    public static Pair<String, String> getRankedString(List<String> ranking, String predictionRule, Integer rank) {
        String str = "\n";
        Integer index = 1;
        String type = getType(predictionRule);

        for (String key : ranking) {
            String line = null;
            if (predictionRule.equals(predict_l_for_s_given_po) || predictionRule.contains(predict_po_for_s_given_l)) {
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
            } else if (predictionRule.equals(predict_localized_l_for_s_given_p)) {
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

    private static String getType(String predictionRule) {
        String type = null;
        if (predictionRule.equals(predict_l_for_o_given_p)) {
            type = predicate_str;
        } else if (predictionRule.equals(predict_l_for_o_given_s)) {
            type = subject_str;
        } else if (predictionRule.equals(predict_l_for_o_given_sp)) {
            type = subject_predicate_pair_str;
        } else if (predictionRule.equals(predict_l_for_s_given_p)) {
            type = predicate_str;
        } else if (predictionRule.equals(predict_l_for_s_given_o)) {
            type = object_str;
        } else if (predictionRule.equals(predict_l_for_o_given_p)) {
            type = predicate_str;
        } else if (predictionRule.equals(predict_l_for_s_given_po) || predictionRule.contains(predict_po_for_s_given_l)) {
            type = predicate_object_pair_str;
        } else if (predictionRule.equals(predict_localized_l_for_s_given_p)) {
            type = predicate_str;
        }
        return type;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_p_for_o_given_localized_l(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
