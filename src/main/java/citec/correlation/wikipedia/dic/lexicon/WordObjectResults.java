/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import citec.correlation.wikipedia.results.WordResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class WordObjectResults implements Comparator<WordObjectResults>{
    @JsonProperty("object")
    private String pair;
    @JsonProperty("multiply")
    private Double multiply;
    @JsonProperty("posTag")
    private String posTag;
    @JsonIgnore
    private static String ENTITY_NOTATION = "http://dbpedia.org/resource/";
    @JsonProperty("probabilities")
    private Map<String, Double> probabilities = new TreeMap<String, Double>();

    public WordObjectResults(String postag,String property, String objectOfProperty, Double multiply, Map<String, Double> probabilities) {
        this.posTag=postag;
        this.pair = this.setObjectOfProperty(objectOfProperty);
        this.multiply = multiply;
        this.probabilities = probabilities;
    }

    public WordObjectResults() {
    }

    public String getPair() {
        return pair;
    }

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public String getPosTag() {
        return posTag;
    }

    public Double getMultiply() {
        return multiply;
    }

    private String setObjectOfProperty(String objectOfProperty) {
        if (objectOfProperty.contains(ENTITY_NOTATION)) {
            objectOfProperty = objectOfProperty.replace(ENTITY_NOTATION, "res:");
        } /*else {
            objectOfProperty = "\"" + objectOfProperty + "\"";
        }*/
        return objectOfProperty;

    }
    
    @Override
    public int compare(WordObjectResults entityInfoA, WordObjectResults entityInfoB) {
        return Double.compare(entityInfoA.getMultiply(), entityInfoB.getMultiply());
    }

    @Override
    public String toString() {
        return "EntityInfo{" + "pair=" + pair + ", multiply=" + multiply + ", posTag=" + posTag + ", probabilities=" + probabilities + '}';
    }

  

}
