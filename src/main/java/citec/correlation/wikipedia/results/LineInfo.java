/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.POS_TAGGER_WORDS;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class LineInfo {
    private String line = null;
    private String subject = null;
    private String predicate = null;
    private String posTag = null;
    private String object = null;    private String rule = null;
    private String word = null;
    private Integer nGramNumber=0;
    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    private Analyzer analyzer=null;
    //line:{ dbo:Politician in c_e and (e, dbp:state, "Ohio"@en) in G } => { occurs('Ohio', d_e) } | supA=64, supB=64, supAB=64, condBA=1, condAB=1, AllConf=1, Coherence=0.5, Cosine=1, Kulczynski=1, MaxConf=1, IR=0

    public LineInfo(String line,Integer wordIndex,Integer kbIndex) throws Exception {
        this.line=line;
        String[] rule = line.split("=>");
        String leftRule = StringUtils.substringBetween(rule[kbIndex], "(", ")");
        String rightRule = StringUtils.substringBetween(rule[wordIndex], "{", "}");
        this.setTriple(leftRule);
        this.setWord(rightRule);
        this.setRule();
        this.setProbabilityValue(line);
    }

    private void setRule() {
        String str="["+this.line.replace("|", "]");
        str = StringUtils.substringBetween(str, "[", "]");
        this.rule = str;
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

    private void setWord(String rightRule) throws Exception {
        String word = StringUtils.substringBetween(rightRule, "(", ")");
        this.word = StringUtils.substringBetween(word, "'", "'");
        this.processWords(this.word);
    }

    private void setTriple(String leftRule) {
        leftRule = leftRule.replace(",", " , ");
        String[] info = leftRule.split(",");
        this.correct(leftRule);
        this.subject = this.correct(info[0]);
        this.predicate = this.correct(info[1]);
        this.object = this.correct(info[2]);;
    }
    
    private void processWords(String nGram) throws Exception {
        StringTokenizer st =new StringTokenizer(nGram);
        nGram = nGram.toLowerCase().trim().strip();
        String str = "";
        while (st.hasMoreTokens()) {
            String tokenStr = st.nextToken();
            if (TextAnalyzer.ENGLISH_STOPWORDS.contains(tokenStr)) {
                continue;
            }
            String line = tokenStr + "_";
            str += line;
        }
        str = str.replace("_", " ");
        str =str.toLowerCase().trim().stripTrailing();
        String []info=str.split(" ");
        this.nGramNumber=info.length;
        this.getPosTag(str);        
    }
    
     private void getPosTag(String nGram) throws Exception {
        analyzer = new Analyzer(nGram, POS_TAGGER_WORDS, 5);
        System.out.println("noun:"+analyzer.getNouns());
        System.out.println("adjective:"+analyzer.getAdjectives());
        System.out.println("verb:"+analyzer.getVerbs());

        
        if (!analyzer.getNouns().isEmpty()) {
            this.posTag = "NN";
        } else if (!analyzer.getAdjectives().isEmpty()) {
            this.posTag = "JJ";
        } else if (!analyzer.getVerbs().isEmpty()) {
            this.posTag = "VB";
        }
    }


    private String correct(String string) {
        return string.trim().strip();
    }

    public String getPosTag() {
        return posTag;
    }

    public Integer getnGramNumber() {
        return nGramNumber;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
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
    

    public String getLine() {
        return line;
    }

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public Map<String, Double> getProbabilityValue() {
        return probabilityValue;
    }

    @Override
    public String toString() {
        String line=this.line+"\n";
        return "LineInfo{" + "line=" + line + ", subject=" + subject + ", predicate=" + predicate + ", object=" + object + ", rule=" + rule + ", word=" + word + ", probabilityValue=" + probabilityValue + '}';
    }

    public String getPartOfSpeech() {
        return this.posTag;
    }

}
