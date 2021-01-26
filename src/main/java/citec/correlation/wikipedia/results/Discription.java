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
public class Discription {

    @JsonProperty("min_supAB")
    private Integer min_supAB = 0;
    @JsonProperty("class")
    private String className = null;
    @JsonProperty("min_property_frequency")
    private Double min_property_frequency = 0.0;
    @JsonProperty("measure")
    private String measure =null;
    @JsonProperty("min_onegram_length")
    private Integer min_onegram_length = 0;
    @JsonProperty("max_number_of_rules")
    private Integer max_number_of_rules = 0;
    @JsonProperty("max_entities_per_class")
    private Integer max_entities_per_class = 10000;
    @JsonProperty("min_supB")
    private Integer min_supB = 0;
    @JsonProperty("threshold")
    private Double threshold = 0.0;
    @JsonProperty("min_class_frequency")
    private Integer min_class_frequency = 10000;
    @JsonProperty("min_supA")
    private Integer min_supA = 0;
    @JsonProperty("rulepattern")
    private String rulepattern = null;
    @JsonProperty("min_pattern_frequency")
    private Double min_pattern_frequency = 0.0;

    public Discription() {

    }

    /*"_description": {
        "min_supAB": "50",
        "class": "AdministrativeRegion",
        "min_property_frequency": 0.005,
        "measure": "AllConf",
        "min_onegram_length": "4",
        "max_number_of_rules": "1000",
        "max_entities_per_class": "10000",
        "min_supB": "50",
        "threshold": 0.1,
        "min_class_frequency": "10000",
        "min_supA": "50",
        "rulepattern": "predict_l_for_o_given_p",
        "min_pattern_frequency": 0.005
    },*/
    public Integer getMin_supAB() {
        return min_supAB;
    }

    public String getClassName() {
        return className;
    }

    public Double getMin_property_frequency() {
        return min_property_frequency;
    }

    public String getMeasure() {
        return measure;
    }

  

    public Integer getMin_onegram_length() {
        return min_onegram_length;
    }

    public Integer getMax_number_of_rules() {
        return max_number_of_rules;
    }

    public Integer getMax_entities_per_class() {
        return max_entities_per_class;
    }

    public Integer getMin_supB() {
        return min_supB;
    }

    public Double getThreshold() {
        return threshold;
    }

    public Integer getMin_class_frequency() {
        return min_class_frequency;
    }

    public Integer getMin_supA() {
        return min_supA;
    }

    public String getRulepattern() {
        return rulepattern;
    }

    public Double getMin_pattern_frequency() {
        return min_pattern_frequency;
    }

    @Override
    public String toString() {
        return "_Discription{" + "min_supAB=" + min_supAB + ", className=" + className + ", min_property_frequency=" + min_property_frequency + ", measure=" + measure + ", min_onegram_length=" + min_onegram_length + ", max_number_of_rules=" + max_number_of_rules + ", max_entities_per_class=" + max_entities_per_class + ", min_supB=" + min_supB + ", threshold=" + threshold + ", min_class_frequency=" + min_class_frequency + ", min_supA=" + min_supA + ", rulepattern=" + rulepattern + ", min_pattern_frequency=" + min_pattern_frequency + '}';
    }
    
}
