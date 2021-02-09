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
public class Rule {

    @JsonProperty("c")
    private String c = null;
    @JsonProperty("ruletype")
    private String ruletype = null;
    @JsonProperty("condAB")
    private Double condAB = null;
    @JsonProperty("condBA")
    private Double condBA = null;
    @JsonProperty("supAB")
    private Double supAB = null;
    @JsonProperty("l")
    private String l = null;
    @JsonProperty("as_string")
    private String as_string = null;
    @JsonProperty("patterntype")
    private String patterntype = null;
    @JsonProperty("p")
    private String p = null;
    @JsonProperty("o")
    private String o = null;
    @JsonProperty("s")
    private String s = null;
    @JsonProperty("supB")
    private Double supB = null;
    @JsonProperty("supA")
    private Double supA = null;
    @JsonProperty("CFG")
    private CFG cfg = null;
    @JsonProperty("interestingness")
    private Interestingness interestingness = null;

    public Rule() {

    }

    public String getC() {
        return c;
    }

    public String getRuletype() {
        return ruletype;
    }

    public Double getCondAB() {
        return condAB;
    }

    public Double getCondBA() {
        return condBA;
    }

    public Double getSupAB() {
        return supAB;
    }

    public String getL() {
        return l;
    }

    public String getAs_string() {
        return as_string;
    }

    public String getPatterntype() {
        return patterntype;
    }

    public String getP() {
        return p;
    }

    public Double getSupB() {
        return supB;
    }

    public Double getSupA() {
        return supA;
    }

    public CFG getCfg() {
        return cfg;
    }

    public Interestingness getInterestingness() {
        return interestingness;
    }

    public String getO() {
        return o;
    }

    public String getS() {
        return s;
    }

    @Override
    public String toString() {
        return "Rule{" + "c=" + c + ", ruletype=" + ruletype + ", condAB=" + condAB + ", condBA=" + condBA + ", supAB=" + supAB + ", l=" + l + ", as_string=" + as_string + ", patterntype=" + patterntype + ", p=" + p + ", o=" + o + ", s=" + s + ", supB=" + supB + ", supA=" + supA + ", cfg=" + cfg + ", interestingness=" + interestingness + '}';
    }


}
