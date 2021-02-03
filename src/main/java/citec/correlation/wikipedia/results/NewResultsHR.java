/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author elahi
 */
public class NewResultsHR {

    @JsonProperty("_description")
    private Discription _description;
    //@JsonProperty("rules")  
    @JsonIgnore
    List<String> distributions = new ArrayList<String>();
 
    @JsonIgnore
    Map<String,List<String>> classDistributions = new TreeMap<String,List<String>> ();

    public NewResultsHR() {

    }
    
    public NewResultsHR(Discription _description, List<String> distributions) {
        this._description = _description;
        this.distributions = distributions;
    }

    public NewResultsHR(Discription _description, Map<String, List<String>> classDistributions) {
        this._description = _description;
        this.classDistributions = classDistributions;
    }

    public List<String> getDistributions() {
        return distributions;
    }

    public Discription getDescription() {
        return _description;
    }

    public Map<String, List<String>> getClassDistributions() {
        return classDistributions;
    }

    @Override
    public String toString() {
        return "NewResults{" + "_description=" + _description + ", distributions=" + distributions + '}';
    }

}
