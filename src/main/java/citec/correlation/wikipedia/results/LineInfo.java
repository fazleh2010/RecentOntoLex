/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class LineInfo {
    private String line = null;
    private RdfTriple rdfTriple = null;
    private String rule = null;
    private String word = null;
    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    //line:{ dbo:Politician in c_e and (e, dbp:state, "Ohio"@en) in G } => { occurs('Ohio', d_e) } | supA=64, supB=64, supAB=64, condBA=1, condAB=1, AllConf=1, Coherence=0.5, Cosine=1, Kulczynski=1, MaxConf=1, IR=0

    public LineInfo(String line,Integer wordIndex,Integer kbIndex) {
        this.line=line;
        String[] rule = line.split("=>");
        String leftRule = StringUtils.substringBetween(rule[kbIndex], "(", ")");
        String rightRule = StringUtils.substringBetween(rule[wordIndex], "{", "}");
        this.setTriple(leftRule);
        this.setWord(rightRule);
        this.setRule(line);
        this.setProbabilityValue(line);
    }

    private void setRule(String line) {
        String[] value = line.split("|");
        this.rule = value[0];
    }

    private void setProbabilityValue(String line) {
        line = line.replace("AllConf", "[AllConf");
        line = line + "]";
        String values = StringUtils.substringBetween(line, "[", "]").replace(",", "");
        String[] info = values.split(" ");
        for (Integer i = 0; i < info.length; i++) {
            Pair<String, String> pair = this.setValue(info[i]);
            double dnum = Double.parseDouble(pair.getValue1());
            this.probabilityValue.put(pair.getValue0(), dnum);
        }
    }

    private Pair<String, String> setValue(String string) {
        String[] info = string.split("=");
        String key = info[0];
        //Double value = Double.parseDouble(info[1]);
        return new Pair<String, String>(key, info[1]);
    }

    private void setWord(String rightRule) {
        String word = StringUtils.substringBetween(rightRule, "(", ")");
        this.word = StringUtils.substringBetween(word, "'", "'");
    }

    private void setTriple(String leftRule) {
        leftRule = leftRule.replace(",", " , ");
        String[] info = leftRule.split(",");
        this.rdfTriple = new RdfTriple(info[0], info[1], info[2]);

    }

    public RdfTriple getRdfTriple() {
        return rdfTriple;
    }

    public String getRule() {
        return rule;
    }

    public String getWord() {
        return word;
    }

    public Double getProbabilityValue(String key) {
        return probabilityValue.get(key);
    }

    @Override
    public String toString() {
        String line=this.line+"\n";
        return line+"LineInfo{" + "rdfTriple=" + rdfTriple + ", rule=" + rule + ", word=" + word + ", probabilityValue=" + probabilityValue + '}';
    }

}
