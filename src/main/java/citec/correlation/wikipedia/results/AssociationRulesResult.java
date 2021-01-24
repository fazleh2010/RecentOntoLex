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
import jdk.nashorn.internal.ir.annotations.Ignore;

/**
 *
 * @author elahi
 */
public class AssociationRulesResult {

    @JsonProperty("_description")
    private _Discription _description;
    @JsonProperty("rules")
    List<String> distributions = new ArrayList<String>();

    public AssociationRulesResult() {

    }

    public List<String> getDistributions() {
        return distributions;
    }

    public _Discription getDescription() {
        return _description;
    }

    @Override
    public String toString() {
        return "NewResults{" + "_description=" + _description + ", distributions=" + distributions + '}';
    }

}
