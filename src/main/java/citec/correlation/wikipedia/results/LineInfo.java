/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import citec.correlation.wikipedia.analyzer.PosAnalyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.POS_TAGGER_WORDS;
import citec.correlation.wikipedia.experiments.NullInterestingness;
import citec.correlation.wikipedia.main.Generated_6;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.PropertyCSV;
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
import citec.correlation.wikipedia.experiments.PredictionRules;

/**
 *
 * @author elahi
 */
public class LineInfo implements NullInterestingness,PredictionRules{

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
    private  Logger LOGGER = null;



    private Map<String, Double> probabilityValue = new TreeMap<String, Double>();
    private PosAnalyzer analyzer = null;
    public static String CHECK_THRESOLD_VALUE="CHECK_THRESOLD_VALUE";
    
    //supA=93, supB=115, supAB=93, condBA=1, condAB=0.808, AllConf=0.808, Coherence=0.447, Cosine=0.899, Kulczynski=0.904, MaxConf=1, IR=0.191

    
    public LineInfo(){
        
    }
    
    public LineInfo(Integer index,String[] row,String prediction,String interestingness,PropertyCSV propertyCSV,Logger logger) throws Exception {
        this.LOGGER=logger;
        if (row.length < propertyCSV.getStringIndex()) {
            this.validFlag = false;
            //LOGGER.log(Level.INFO, "line No ::" + index + " line does not work!!!!!!!!!!");
            return;
        }
         

        this.line =row[propertyCSV.getStringIndex()];
        if(line.contains("http://www.w3.org/2001/XMLSchema#integer"))
            line="http://www.w3.org/2001/XMLSchema#integer";
        this.className = setClassName(row[propertyCSV.getClassNameIndex()]);

        if (isPredict_l_for_s_given_po(prediction)
                || isPredict_po_for_s_given_l(prediction)
                || isPredict_po_for_s_given_localized_l(prediction)) {
            this.predicate = this.setProperty(row[propertyCSV.getPredicateIndex()]);
            this.object = this.setObject(row[propertyCSV.getObjectIndex()]);
        } else if (isPredict_l_for_s_given_o(prediction)) {
            this.object = this.setObject(row[propertyCSV.getObjectIndex()]);
        } else if (isPredict_l_for_o_given_s(prediction)) {
            //this.subject = this.setSubject(rule);
        } else if (isPredict_l_for_o_given_sp(prediction)) {
            //this.subject = this.setSubject(rule);
            //this.predicate = this.setProperty(rule);
        } else if (isPredict_l_for_o_given_p(prediction)
                || isPredict_l_for_s_given_p(prediction)
                || isPredict_p_for_s_given_localized_l(prediction)
                || isPredict_p_for_o_given_localized_l(prediction)) {
            this.predicate = this.setProperty(row[propertyCSV.getPredicateIndex()]);
        } else if (isPredict_localized_l_for_s_given_p(prediction)) {
            this.predicate = this.setProperty(row[propertyCSV.getPredicateIndex()]);
        }


        if(!isKBValid()){
            this.validFlag=false;
            return; 
        }
            
        

        this.wordOriginal = row[propertyCSV.getLinguisticPatternIndex()];
        /*if(this.wordOriginal.contains("ustralian"))
            System.out.println("@@@@@@@@@@@@@@@22:"+wordOriginal);*/
        
        if (wordOriginal != null) {
            this.validFlag = true;
        }
        this.nGramNumber=this.setNGram(row,propertyCSV.getPatterntypeIndex());
       
       
        if (this.validFlag) {
            String str = this.processWords(this.wordOriginal);
            this.getPosTag(str);
            this.setRule();
            this.setProbabilityValue(index,interestingness,row,propertyCSV);
        }
    }
    
    private Integer setNGram(String[] row, Integer patterntypeIndex) {
        String patternType = row[patterntypeIndex];
        /*if (patternType.contains("-")) {
            String[] info = row[patterntypeIndex].split("-");
            return Integer.parseInt(info[0]);
        } else if (patternType.contains("one")) {
            return 1;
        } else if (patternType.contains("two")) {
            return 2;
        } else if (patternType.contains("three")) {
            return 3;
        } else if (patternType.contains("four")) {
            return 4;
        }*/
        return 5;

    }
    
    private void setProbabilityValue(Integer index, String interestingness, String[] row, PropertyCSV propertyCSV) {
        Double givenSupA, givenSupB, givenCondAB, givenCondBA, givenAllConf, givenCoherence, givenCosine, givenIR, givenKulczynski, givenMaxConf;

        try {
            givenSupA = Double.parseDouble(row[propertyCSV.getSupAIndex()]);
            givenSupB = Double.parseDouble(row[propertyCSV.getSupBIndex()]);
            givenCondAB = Double.parseDouble(row[propertyCSV.getCondABIndex()]);
            givenCondBA = Double.parseDouble(row[propertyCSV.getCondBAIndex()]);
            givenAllConf = Double.parseDouble(row[propertyCSV.getAllConfIndex()]);
            givenCoherence = Double.parseDouble(row[propertyCSV.getCoherenceIndex()]);
            givenCosine = Double.parseDouble(row[propertyCSV.getCosineIndex()]);
            givenIR = Double.parseDouble(row[propertyCSV.getIRIndex()]);
            givenKulczynski = Double.parseDouble(row[propertyCSV.getKulczynskiIndex()]);
            givenMaxConf = Double.parseDouble(row[propertyCSV.getMaxConfIndex()]);
            this.probabilityValue.put(supA, givenSupA);
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
            }
        } catch (Exception ex) {
            this.validFlag = false;
            return;
        }

        //LOGGER.log(Level.INFO, "index:" + index + " class:" + this.className + " predicate:" + this.predicate + " probabilityValue:" + this.probabilityValue);
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
        //System.out.println("commonAtt:"+commonAtt);
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
        if (object.contains("http:")) {
            if (object.contains("http://dbpedia.org/resource/")) {
                object = object.replace("http://dbpedia.org/resource/", "");
            }
        } else if (object.contains("\"\"")) {
            object = object.replace("\"\"", "");
        }
        object = object.replace("\"", "");

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
        analyzer = new PosAnalyzer(word, POS_TAGGER_WORDS, 5);
        if (!analyzer.getNouns().isEmpty()) {
            this.posTag = PosAnalyzer.NOUN;
        } else if (!analyzer.getAdjectives().isEmpty()) {
            this.posTag = PosAnalyzer.ADJECTIVE;
        } else if (!analyzer.getVerbs().isEmpty()) {
            this.posTag = PosAnalyzer.VERB;
        } else {
            this.posTag = PosAnalyzer.NOUN;
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

    public PosAnalyzer getAnalyzer() {
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
        return "LineInfo{"+this.className+" ," + subject + ", predicate=" + predicate + ", object=" + object + ", word=" + word + ", probabilityValue=" + probabilityValue + '}';
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
    
    private String setProperty(String property) {
        String prefix = "prefix:";
        if (property.contains("http:")) {
            if (property.contains("http://dbpedia.org/ontology/")) {
                property=property.replace("http://dbpedia.org/ontology/", "");
                prefix = "dbo:";
            } else if (property.contains("http://dbpedia.org/property/")) {
                property=property.replace("http://dbpedia.org/property/", "");
                prefix = "dbp:";
            }
            else if (property.contains("http://xmlns.com/foaf/0.1/")) {
                property=property.replace("http://xmlns.com/foaf/0.1/", "");
                prefix = "foaf:";
            }
            

            property = prefix + property;

        }
        return property;
    }
    private String setClassName(String className) {
        String prefix = null;
        if (className.contains("/")) {
            className = className.replace("http://dbpedia.org/ontology/", "");
        }

        return className;
    }

    public String getCheckedAssociationRule() {
        return checkedAssociationRule;
    }

    public String getCheckedAssociationRuleValue() {
        return checkedAssociationRuleValue;
    }
    
     private boolean isKBValid() {
        if (this.object != null) {
            if (this.object.strip().trim().contains("http://www.w3.org/2001/XMLSchema#date")) {
                return false;
            }
        }
        if (this.predicate != null) {
            if (this.predicate.strip().trim().contains("date")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean isPredict_l_for_s_given_po(String predictionRule) {
        return predictionRule.equals(predict_l_for_s_given_po);
    }

    @Override
    public Boolean isPredict_l_for_s_given_o(String predictionRule) {
        return predictionRule.equals(predict_l_for_s_given_o);
    }

    @Override
    public Boolean isPredict_l_for_o_given_s(String predictionRule) {
         return predictionRule.equals(predict_l_for_o_given_s);
    }

    @Override
    public Boolean isPredict_l_for_o_given_sp(String predictionRule) {
         return predictionRule.equals(predict_l_for_o_given_sp);
    }

    @Override
    public Boolean isPredict_l_for_o_given_p(String predictionRule) {
         return predictionRule.equals(predict_l_for_o_given_p);
    }

    @Override
    public Boolean isPredict_l_for_s_given_p(String predictionRule) {
         return predictionRule.equals(predict_l_for_s_given_p);
    }

    @Override
    public Boolean isPredict_localized_l_for_s_given_p(String predictionRule) {
         return predictionRule.equals(predict_localized_l_for_s_given_p);
    }

    @Override
    public Boolean isPredict_po_for_s_given_l(String predictionRule) {
         return predictionRule.equals(predict_po_for_s_given_l);
    }

    @Override
    public Boolean isPredict_po_for_s_given_localized_l(String predictionRule) {
        return predictionRule.equals(predict_po_for_s_given_localized_l);
    }

    @Override
    public Boolean isPredict_p_for_s_given_localized_l(String predictionRule) {
        return predictionRule.equals(predict_p_for_s_given_localized_l);
    }

    @Override
    public Boolean isPredict_p_for_o_given_localized_l(String predictionRule) {
       return predictionRule.equals(predict_p_for_o_given_localized_l);
    }

  

   

   

    


}
