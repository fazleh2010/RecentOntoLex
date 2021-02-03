/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author elahi
 */
public class CFG {
    @JsonProperty("min_supB")
    private Integer min_supB = null;
    @JsonProperty("min_pattern_frequency")
    private Double min_pattern_frequency = null;
    @JsonProperty("max_entities_per_class")
    private Integer max_entities_per_class = null;
    @JsonProperty("min_class_frequency")
    private Integer min_class_frequency = null;
    @JsonProperty("min_supAB")
    private Integer min_supAB = null;
    @JsonProperty("min_supA")
    private Integer min_supA = null;
    @JsonProperty("min_property_frequency")
    private Double min_property_frequency = null;
    @JsonProperty("min_onegram_length")
    private Integer min_onegram_length = null;
    
    public CFG(){
        
    }

    public Integer getMin_supB() {
        return min_supB;
    }

    public Double getMin_pattern_frequency() {
        return min_pattern_frequency;
    }

    public Integer getMax_entities_per_class() {
        return max_entities_per_class;
    }

    public Integer getMin_class_frequency() {
        return min_class_frequency;
    }

    public Integer getMin_supAB() {
        return min_supAB;
    }

    public Integer getMin_supA() {
        return min_supA;
    }

    public Double getMin_property_frequency() {
        return min_property_frequency;
    }

    public Integer getMin_onegram_length() {
        return min_onegram_length;
    }
    
}

/*
"CFG" : {
            "min_supB" : "10",
            "min_pattern_frequency" : "0.001",
            "max_entities_per_class" : "10000",
            "min_class_frequency" : "10000",
            "min_supAB" : "10",
            "min_supA" : "10",
            "min_property_frequency" : "0.001",
            "min_onegram_length" : "4"
         },
*/
