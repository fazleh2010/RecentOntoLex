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
public class Interestingness {

    @JsonProperty("AllConf")
    private Double AllConf = null;
    @JsonProperty("MaxConf")
    private Double MaxConf = null;
    @JsonProperty("Coherence")
    private Double Coherence = null;
    @JsonProperty("IR")
    private Double IR = null;
    @JsonProperty("Cosine")
    private Double Cosine = null;
    @JsonProperty("Kulczynski")
    private Double Kulczynski = null;

    public Interestingness() {

    }

    public Double getAllConf() {
        return AllConf;
    }

    public Double getMaxConf() {
        return MaxConf;
    }

    public Double getCoherence() {
        return Coherence;
    }

    public Double getIR() {
        return IR;
    }

    public Double getCosine() {
        return Cosine;
    }

    public Double getKulczynski() {
        return Kulczynski;
    }

}
