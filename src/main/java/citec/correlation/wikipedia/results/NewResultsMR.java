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

/**
 *
 * @author elahi
 */
public class NewResultsMR {

    @JsonProperty("_description")
    private Discription _description;
    @JsonProperty("rules")  
    List<Rule> distributions = new ArrayList<Rule>();
 
    @JsonIgnore
    Map<String,List<Rule>> classDistributions = new TreeMap<String,List<Rule>> ();

    public NewResultsMR() {

    }
    
    public NewResultsMR(Discription _description, List<Rule> distributions) {
        this._description = _description;
        this.distributions = distributions;
    }

    public NewResultsMR(Discription _description, Map<String, List<Rule>> classDistributions) {
        this._description = _description;
        this.classDistributions = classDistributions;
    }

    public List<Rule> getDistributions() {
        return distributions;
    }

    public Discription getDescription() {
        return _description;
    }

    public Map<String, List<Rule>> getClassDistributions() {
        return classDistributions;
    }

    @Override
    public String toString() {
        return "NewResults{" + "_description=" + _description + ", distributions=" + distributions + '}';
    }

}
