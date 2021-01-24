/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class ResultUnit {

    private String className = null;
    private String associationRuleTye = null;
    private String predictType = null;
    private Map<String, List<LineInfo>> lexicon = new TreeMap<String, List<LineInfo>>();

    public ResultUnit(String className, String associationRuleTye, String predictType, Map<String, List<LineInfo>> lexicon) {
        this.className = className;
        this.associationRuleTye = associationRuleTye;
        this.predictType = predictType;
        this.lexicon = lexicon;
    }

    public String getClassName() {
        return className;
    }

    public String getAssociationRuleTye() {
        return associationRuleTye;
    }

    public String getPredict() {
        return predictType;
    }

    public Map<String, List<LineInfo>> getLexicon() {
        return lexicon;
    }

}
