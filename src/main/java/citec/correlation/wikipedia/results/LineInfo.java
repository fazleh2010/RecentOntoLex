/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.POS_TAGGER_WORDS;
import citec.correlation.wikipedia.parameters.ThresoldConstants;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class LineInfo implements ThresoldConstants{

    private String line = null;
    private String subject = null;
    private String predicate = null;
    private String posTag = null;
    private String object = null;
    private String rule = null;
    private String word = null;
    private String wordOriginal = null;
    private String className = null;
    private Boolean validFlag = false;
    private Integer nGramNumber = 0;
    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    private Analyzer analyzer = null;
    public static String CHECK_THRESOLD_VALUE="CHECK_THRESOLD_VALUE";
    
    //supA=93, supB=115, supAB=93, condBA=1, condAB=0.808, AllConf=0.808, Coherence=0.447, Cosine=0.899, Kulczynski=0.904, MaxConf=1, IR=0.191

    
    public LineInfo(){
        
    }
    
     
    public LineInfo( String interestingness,Rule rule) throws Exception {
        this.line = rule.getAs_string();
        this.className = rule.getC();
        this.subject = "e";
        this.predicate = rule.getP();
        this.object = rule.getO();
        this.wordOriginal = rule.getL();
        if (wordOriginal != null) {
            this.validFlag = true;
        }
        String[] info = rule.getPatterntype().split("-");
        this.nGramNumber = Integer.parseInt(info[0]);

        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(interestingness, rule);
        }

    }
    
    
    public LineInfo(String className, String line, Integer wordIndex, Integer kbIndex) throws Exception {
        this.line = line;
        this.className = className;
        //this.setParameters(wordIndex,kbIndex);
        String[] rule = line.split("=>");
        String leftRule = StringUtils.substringBetween(rule[kbIndex], "(", ")");
        String rightRule = StringUtils.substringBetween(rule[wordIndex], "{", "}");
        this.setTriple(leftRule);
        this.setWord(rightRule);
        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            String[] info = str.split(" ");
            if (info.length > 1) {
                this.nGramNumber = info.length;
            }
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(line);
        }
        
    }
    
    public static Boolean isThresoldValid(Map<String, Double> lineProbabiltyValue, Map<String, Double> givenThresold) throws Exception {
        boolean thresoldValid = true;
        Set<String> commonAtt=Sets.intersection(lineProbabiltyValue.keySet(), givenThresold.keySet());
        //System.out.println("lineProbabiltyValue:"+lineProbabiltyValue);
        //System.out.println("givenThresold:"+givenThresold);
         //System.out.println("commonAtt:"+commonAtt);
        
        
        for (String attribute : commonAtt) {
            if (lineProbabiltyValue.containsKey(attribute) && givenThresold.containsKey(attribute)) {
                Double probValue = lineProbabiltyValue.get(attribute);
                Double thresold = givenThresold.get(attribute);
                if (probValue >thresold) {
                    thresoldValid = true;
                }
                else
                return false;
            }
        }
        return thresoldValid;
    }


   
    
    private void setParameters(Integer wordIndex, Integer kbIndex) throws Exception {
        String[] rule = line.split("=>");
        String leftRule = StringUtils.substringBetween(rule[kbIndex], "(", ")");
        String rightRule = StringUtils.substringBetween(rule[wordIndex], "{", "}");
        this.setTriple(leftRule);
        this.setWord(rightRule);
        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            String[] info = str.split(" ");
            if (info.length > 1) {
                this.nGramNumber = info.length;
            }
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(line);
        }
    }

    private void setRule() {
        String str = "[" + this.line.replace("|", "]");
        str = StringUtils.substringBetween(str, "[", "]");
        this.rule = str;
    }

    /*private void setProbabilityValue(String line) {
        line = line.replace("AllConf", "[AllConf");
        line = line + "]";
        String values = StringUtils.substringBetween(line, "[AllConf", "]").replace(",", "");
        String[] info = values.split(" ");
        for (Integer i = 0; i < info.length; i++) {
            Pair<String, String> pair = this.setValue(info[i]);
            double dnum = Double.parseDouble(pair.getValue1());
            this.probabilityValue.put(pair.getValue0(), dnum);
        }
    }*/
    
    public void setProbabilityValue(String line) {
        line = line.replace("supA=", "$supA=");
        line = line + "$";
        String values = StringUtils.substringBetween(line, "$", "$").replace(",", "");
        String[] info = values.split(" ");
        for (Integer i = 0; i < info.length; i++) {
            Pair<String, String> pair = this.setValue(info[i]);
            double dnum = Double.parseDouble(pair.getValue1());
            this.probabilityValue.put(pair.getValue0(), dnum);
        }
    }
    
    private void setProbabilityValue(String interestingness, Rule rule) {
        this.probabilityValue.put(supA, rule.getSupA());
        this.probabilityValue.put(supB, rule.getSupB());
        this.probabilityValue.put(condAB, rule.getCondAB());
        this.probabilityValue.put(condBA, rule.getCondBA());
        if (interestingness.contains(AllConf)) {
            this.probabilityValue.put(AllConf, rule.getInterestingness().getAllConf());
        } else if (interestingness.contains(Cosine)) {
            this.probabilityValue.put(Cosine, rule.getInterestingness().getCosine());
        } else if (interestingness.contains(Coherence)) {
            this.probabilityValue.put(Coherence, rule.getInterestingness().getCoherence());
        } else if (interestingness.contains(Kulczynski)) {
            this.probabilityValue.put(Kulczynski, rule.getInterestingness().getKulczynski());
        } else if (interestingness.contains(MaxConf)) {
            this.probabilityValue.put(MaxConf, rule.getInterestingness().getMaxConf());
        } else if (interestingness.contains(IR)) {
            this.probabilityValue.put(IR, rule.getInterestingness().getIR());
        }
    }

    private Pair<String, String> setValue(String string) {
        String[] info = string.split("=");
        String key = info[0].trim().strip();
        return new Pair<String, String>(key, info[1]);
    }

    private void setWord(String rightRule) {
        String word = StringUtils.substringBetween(rightRule, "(", ")");
        this.wordOriginal = StringUtils.substringBetween(word, "'", "'");
        if (wordOriginal != null) {
            this.validFlag = true;
        } else {
            this.validFlag = false;
        }
    }

    private void setTriple(String leftRule) {
        leftRule = leftRule.replace(",", " , ");
        String[] info = leftRule.split(",");
        this.correct(leftRule);
        this.subject = this.correct(info[0]);
        this.predicate = this.correct(info[1]);
        this.object =this.setObject(this.correct(info[2]));       
    }
    
    private String setObject(String object) {
        if(object.contains(":")){
            String []info=object.split(":");
            return info[1];
        }
        else
            return object;
      
    }

    private String processWords(String nGram) throws Exception {
        StringTokenizer st = new StringTokenizer(nGram);
        String str = "";
        while (st.hasMoreTokens()) {
            String tokenStr = st.nextToken();
            if (this.isStopWord(tokenStr)) {
                continue;
            }

            String line = tokenStr + "_";
            str += line;
        }
        str = str.replace("_", " ");
        str = str.trim().stripTrailing();

        return str;
    }

    private void getPosTag(String word) throws Exception {
        analyzer = new Analyzer(word, POS_TAGGER_WORDS, 5);
        if (!analyzer.getNouns().isEmpty()) {
            this.posTag = Analyzer.NOUN;
        } else if (!analyzer.getAdjectives().isEmpty()) {
            this.posTag = Analyzer.ADJECTIVE;
        } else if (!analyzer.getVerbs().isEmpty()) {
            this.posTag = Analyzer.VERB;
        } else {
            this.posTag = Analyzer.NOUN;
        }
        this.word = word.trim().strip();
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

    public String getWordOriginal() {
        return wordOriginal;
    }

    public String getClassName() {
        return className;
    }

    public Boolean getValidFlag() {
        return validFlag;
    }

    public String getPartOfSpeech() {
        return this.posTag;
    }

    private Boolean isStopWord(String tokenStr) {
        tokenStr = tokenStr.toLowerCase().trim().strip();
        if (TextAnalyzer.ENGLISH_STOPWORDS.contains(tokenStr)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String line = this.line + "\n";
        return "LineInfo{" + subject + ", predicate=" + predicate + ", object=" + object + ", word=" + word + ", probabilityValue=" + probabilityValue + '}';
    }

   

    


}
