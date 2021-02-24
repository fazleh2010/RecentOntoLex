/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.POS_TAGGER_WORDS;
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.List;
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
    private String subject = "e";
    private String predicate = "p";
    private String object = "o";
    private String posTag = null;
    private String rule = null;
    private String word = null;
    private String wordOriginal = null;
    private String className = null;
    private Boolean validFlag = false;
    private Integer nGramNumber = 0;
    private  static String http = "http://dbpedia.org/resource/";
    private  static String ONTOLOGY = "ontology";
    private  static String PROPERTY = "property";
    private   String checkedAssociationRule = null;
    private   String checkedAssociationRuleValue = null;


    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    private Analyzer analyzer = null;
    public static String CHECK_THRESOLD_VALUE="CHECK_THRESOLD_VALUE";
    
    //supA=93, supB=115, supAB=93, condBA=1, condAB=0.808, AllConf=0.808, Coherence=0.447, Cosine=0.899, Kulczynski=0.904, MaxConf=1, IR=0.191

    
    public LineInfo(){
        
    }
    
    public LineInfo(String[] row,String prediction) throws Exception {
        Integer classIndex=0,ruletypeIndex=1,linguisticPatternIndex=2,patterntypeIndex=3;
        Integer subjectIndex=4,predicateIndex=5,objectIndex=6,stringIndex=18;
       
        System.out.println("stringIndex:"+row[stringIndex]);
        System.out.println("classIndex:"+row[classIndex]);
        System.out.println("predicateIndex:"+row[predicateIndex]);
        
        this.line =row[stringIndex];
        this.className = row[classIndex];

        if (prediction.contains(predict_l_for_s_given_po)) {
            //this.predicate = this.setProperty(rule);
            //this.object = this.setObject(rule);
        } else if (prediction.contains(predict_l_for_s_given_o)) {
           // this.object = this.setObject(rule);
        } else if (prediction.contains(predict_l_for_o_given_s)) {
            //this.subject = this.setSubject(rule);
        } else if (prediction.contains(predict_l_for_o_given_sp)) {
            //this.subject = this.setSubject(rule);
            //this.predicate = this.setProperty(rule);
        } else if (prediction.contains(predict_l_for_o_given_p)) {
            this.predicate =row[predicateIndex]; 
        } else if (prediction.contains(predict_l_for_s_given_p)) {
            this.predicate =row[predicateIndex]; 
        }

        this.wordOriginal = row[linguisticPatternIndex];
        if (wordOriginal != null) {
            this.validFlag = true;
        }
        String []info=row[patterntypeIndex].split("-");
        this.nGramNumber = Integer.parseInt(info[0]);
        
       
       
        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(interestingness,row);
        }
    }
    
    private void setProbabilityValue(LinkedHashSet<String> interestingness, String[] row) {
        Integer condABIndex=7,condBAIndex=8,supAIndex=9,supBIndex=10,supABIndex=11,AllConfIndex=12,CoherenceIndex=13,CosineIndex=14,IRIndex=15,KulczynskiIndex=16,MaxConfIndex=17;
        Double givenSupA = Double.parseDouble(row[supAIndex]);
        Double givenSupB = Double.parseDouble(row[supBIndex]);
        Double givenCondAB = Double.parseDouble(row[condABIndex]);
        Double givenCondBA = Double.parseDouble(row[condBAIndex]);
        Double givenAllConf = Double.parseDouble(row[AllConfIndex]);
        Double givenCoherence = Double.parseDouble(row[CoherenceIndex]);
        Double givenCosine = Double.parseDouble(row[CosineIndex]);
        Double givenIR = Double.parseDouble(row[IRIndex]);
        Double givenKulczynski = Double.parseDouble(row[KulczynskiIndex]);
        Double givenMaxConf = Double.parseDouble(row[MaxConfIndex]);
        System.out.println("supAIndex:"+row[supAIndex]);
        System.out.println("supBIndex:"+row[supBIndex]);
        System.out.println("condABIndex:"+row[condABIndex]);
        System.out.println("condBAIndex:"+row[condBAIndex]);
        System.out.println("CosineIndex:"+row[CosineIndex]);
        System.out.println("AllConfIndex:"+row[AllConfIndex]);
        System.out.println("CoherenceIndex:"+row[CoherenceIndex]);
        System.out.println("CosineIndex:"+row[CosineIndex]);
        System.out.println("KulczynskiIndex:"+row[KulczynskiIndex]);
        System.out.println("IRIndex:"+row[IRIndex]);

        /*this.probabilityValue.put(supA, givenSupA);
        this.probabilityValue.put(supB, givenSupB);
        this.probabilityValue.put(condAB, givenCondAB);
        this.probabilityValue.put(condBA, givenCondBA);
        if (interestingness.contains(AllConf)) {
            this.probabilityValue.put(AllConf, givenAllConf);
        } else if (interestingness.contains(Cosine)) {
            this.probabilityValue.put(Cosine, givenCosine);
        } else if (interestingness.contains(Coherence)) {
            this.probabilityValue.put(Coherence, givenCoherence);
        } else if (interestingness.contains(Kulczynski)) {
            this.probabilityValue.put(Kulczynski, givenKulczynski);
        } else if (interestingness.contains(MaxConf)) {
            this.probabilityValue.put(MaxConf, givenMaxConf);
        } else if (interestingness.contains(IR)) {
            this.probabilityValue.put(IR, givenIR);
        }*/
    }

    
    public LineInfo(LineInfo lineInfo, String associationRule, String associationValue) {
        this.line = lineInfo.getLine();
        this.className = lineInfo.getClassName();
        this.subject = lineInfo.getSubject();
        this.predicate = lineInfo.getPredicate();
        this.object = lineInfo.getObject();
        this.wordOriginal = lineInfo.getWordOriginal();
        this.validFlag = lineInfo.getValidFlag();
        this.nGramNumber = lineInfo.getnGramNumber();
        this.word = lineInfo.getWord();
        this.posTag = lineInfo.getPosTag();
        this.rule = lineInfo.getRule();
        this.probabilityValue = lineInfo.getProbabilityValue();
        this.checkedAssociationRule = associationRule;
        this.checkedAssociationRuleValue = associationValue;
    }

     
    public LineInfo(String prediction, String interestingness, Rule rule) throws Exception {
        this.line = rule.getAs_string();
        this.className = rule.getC();

        if (prediction.contains(predict_l_for_s_given_po)) {
            this.predicate = this.setProperty(rule);
            this.object = this.setObject(rule);
        } else if (prediction.contains(predict_l_for_s_given_o)) {
            this.object = this.setObject(rule);
        } else if (prediction.contains(predict_l_for_o_given_s)) {
            this.subject = this.setSubject(rule);
        } else if (prediction.contains(predict_l_for_o_given_sp)) {
            this.subject = this.setSubject(rule);
            this.predicate = this.setProperty(rule);
        } else if (prediction.contains(predict_l_for_o_given_p)) {
            this.predicate = this.setProperty(rule);
        } else if (prediction.contains(predict_l_for_s_given_p)) {
            this.predicate = this.setProperty(rule);
        }

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
    
      private String setSubject(Rule rule) {
        String object = rule.getS();
        if (object.contains("http")) {
            object = object.replace(http, "");
            object = object.replace("<", "");
            object = object.replace(">", "");
        } else if (object.contains("@")) {
            object = object.replace("\"", "");
        }

        return object;
    }

    
    private String setObject(Rule rule) {
        String object = rule.getO();
        if (object.contains("http")) {
            object = object.replace(http, "");
            object = object.replace("<", "");
            object = object.replace(">", "");
        } else if (object.contains("@")) {
            object = object.replace("\"", "");
        }

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

    private String setProperty(Rule rule) {
        String property = rule.getP();
        String prefix = null;
        if (property.contains("/")) {
            String[] info = property.split("/");

            if (info[0].contains(ONTOLOGY)) {
                prefix = "dbo:";
            } else if (info[0].contains(PROPERTY)) {
                prefix = "dbp:";
            }

            property = prefix + info[1];

        }
        return property;
    }

    public String getCheckedAssociationRule() {
        return checkedAssociationRule;
    }

    public String getCheckedAssociationRuleValue() {
        return checkedAssociationRuleValue;
    }

   

   

    


}
