/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

/**
 *
 * @author elahi
 */
public class ResultTriple {

    private final String probability_Str;
    private final Double probability_value;
    
    public ResultTriple(String probability_Str, Double probability_value) {
        this.probability_Str = probability_Str;
        this.probability_value = probability_value;
    }

    public String getProbability_Str() {
        return probability_Str;
    }

    public Double getProbability_value() {
        return probability_value;
    }

    @Override
    public String toString() {
        return "ResultTriple{" + "probability_Str=" + probability_Str + ", probability_value=" + probability_value + '}';
    }

   
}
