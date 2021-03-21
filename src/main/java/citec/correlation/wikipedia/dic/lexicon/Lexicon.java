/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import citec.correlation.wikipedia.analyzer.PosAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.experiments.PredictionRules;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_p;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_s;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_o_given_sp;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_o;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_p;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_l_for_s_given_po;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_localized_l_for_s_given_p;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_po_for_s_given_l;
import static citec.correlation.wikipedia.experiments.PredictionRules.predict_po_for_s_given_localized_l;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Lexicon implements PredictionRules{
   
    private String lexiconDirectory = null;
    private  Map<String, List<LexiconUnit>>lexiconPosTaggged = new TreeMap<String, List<LexiconUnit>>();
   

    public Lexicon(String outputDir) throws IOException {
        this.lexiconDirectory = outputDir;
    }
    
    public void preparePropertyLexicon(String predictionRule,String directory,String key,String associationType, Map<String, List<LineInfo>> lineLexicon) throws IOException, Exception {
        Map<String, List<LexiconUnit>> posTaggedLex = new TreeMap<String, List<LexiconUnit>>();
        Integer count=0,countJJ=0,countVB=0;
        for (String word : lineLexicon.keySet()) {
            String postagOfWord = null;
            LinkedHashMap<Integer, List<String>> kbList = new LinkedHashMap<Integer, List<String>>();
            Integer index = 0;
            //System.out.println("word:"+word);
            
            //System.out.println("postagOfWord:"+postagOfWord);
            //System.out.println("associationType:"+associationType);
             Set<String>duplicateCheck=new HashSet<String>();
             
             /*if(postagOfWord.contains(Analyzer.NOUN))
                    countNN=countNN+1;
                else if (postagOfWord.contains(Analyzer.ADJECTIVE)){
                     countJJ=countJJ+1;
                }
                else if (postagOfWord.contains(Analyzer.VERB)){
                     countVB=countVB+1;
                }*/
           count=count+1;
            for (LineInfo lineInfo : lineLexicon.get(word)) {
                postagOfWord = lineInfo.getPartOfSpeech();
                
                String object=lineInfo.getObject();
                List<String> pairs = new ArrayList<String>();
                if(duplicateCheck.contains(object)){
                   continue; 
                }
                String value=lineInfo.getProbabilityValue(associationType).toString();

                //System.out.println("pair="+lineInfo.getObject());
                //System.out.println("associationType:"+associationType+" "+value);
                //pairs.add("pair=" + lineInfo.getPredicate() + "_" + lineInfo.getObject());
                String kb=this.getPair(lineInfo,predictionRule);
                pairs.add("kb=" +kb);
                pairs.add(associationType + "=" + value);
                pairs.add("triple" + "=" + lineInfo.getSubject()+" "+lineInfo.getPredicate()+" "+lineInfo.getObject());
                pairs.add("class" + "=" + lineInfo.getClassName());
                pairs.add("line" + "=" +lineInfo.toString().replace("=", " ") );
                kbList.put(index, pairs);
                index = index + 1;
                duplicateCheck.add(object);
            }
            LexiconUnit LexiconUnit  = new LexiconUnit(count, word, postagOfWord, kbList);          
            posTaggedLex = this.setPartsOfSpeech(postagOfWord, LexiconUnit, posTaggedLex);
        }

        for (String postag : posTaggedLex.keySet()) {
            //String fileName = qald9Dir+ OBJECT + "/"+postag + "-" + key  + ".json";
            String fileName =directory+ "/"+postag + "-" + key  + ".json";
            List<LexiconUnit> lexiconUnts = posTaggedLex.get(postag);
            this.lexiconPosTaggged.put(postag, lexiconUnts);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(Paths.get(fileName).toFile(), lexiconUnts);
        }

    }

    private Map<String, List<LexiconUnit>> setPartsOfSpeech(String postagOfWord, LexiconUnit LexiconUnit, Map<String, List<LexiconUnit>> lexicon) {
        List<LexiconUnit> temp = new ArrayList<LexiconUnit>();
        if (lexicon.containsKey(postagOfWord)) {
            temp = lexicon.get(postagOfWord);
        }
        temp.add(LexiconUnit);
        lexicon.put(postagOfWord, temp);
        return lexicon;
    }

    /*public void prepareObjectLexicon(String resultDir,String dboProperty, Set<String> posTags) throws IOException {
       Map<String, List<WordObjectResults>> wordObjectResults =this.getWordObjectResults(resultDir,dboProperty+"_wordObject");
       System.out.println(wordObjectResults.keySet());
        for (String pos : posTags) {
            Map<String, List<WordObjectResults>> posEntitieInfos = entitiesSort(wordObjectResults, pos);
            this.preparePropertyLexicon(posEntitieInfos, pos,"word","fileName");
        }
    }*/
    
    public void prepareObjectLexicon(String resultDir, String lexiconDir,String fileType,HashSet<String> posTags) throws IOException {
        Map<String, List<WordObjectResults>> wordObjectResults = this.getWordObjectResults(resultDir, "wordObject");
        for (String postag : posTags) {
            Map<String, List<WordObjectResults>> posEntitieInfos = entitiesSort(wordObjectResults, postag);
             String conditionalFilename = FileFolderUtils.getLexiconFile(lexiconDir,fileType, postag);
             this.preparePropertyLexicon(lexiconDir,posEntitieInfos, postag, fileType,conditionalFilename);
        }
    }


    public void preparePropertyLexicon(String lexiconDir,Map<String, List<WordObjectResults>> nounEntitieInfos, String givenPartsOfSpeech, String type,String fileName) throws IOException {
        if (nounEntitieInfos.isEmpty()) {
            return;
        }
        List<LexiconUnit> lexiconUnts = new ArrayList<LexiconUnit>();
        Integer count=0;
        for (String word : nounEntitieInfos.keySet()) {
            List<WordObjectResults> list = nounEntitieInfos.get(word);
            LinkedHashMap<Integer, List<String>> entityInfos = new LinkedHashMap<Integer, List<String>>();
            Integer index = 0;
            String firstTag = null;
            Boolean flag = false;
             String postagOfWord =null;
             count=count+1;
            for (WordObjectResults entityInfo : list) {
                postagOfWord = entityInfo.getPosTag();
                firstTag = this.getFirstTag(entityInfo.getPosTag());
                if (firstTag.contains(givenPartsOfSpeech)) {
                    flag = true;
                }
                index = index + 1;
       
                List<String> pairs = new ArrayList<String>();
                //System.out.println("entityInfo.getPair():"+entityInfo.getPair());
                pairs.add("pair=" + entityInfo.getPair());
                pairs.add("multiplyValue=" + entityInfo.getMultiply().toString());
                entityInfos.put(index, pairs);
            }
            if (flag) {
                LexiconUnit LexiconUnit = new LexiconUnit(count,word, postagOfWord, entityInfos);
                lexiconUnts.add(LexiconUnit);
            }

        }
        if (!lexiconUnts.isEmpty()) {
            FileFolderUtils.createDirectory(lexiconDir);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(Paths.get(fileName).toFile(), lexiconUnts);
        }
      
    }
    
   
    
      private Map<String, List<WordObjectResults>> entitiesSort(Map<String, List<WordObjectResults>> wordEntities, String posTag) {
        Map<String, List<WordObjectResults>> posEntitieInfos = new TreeMap<String, List<WordObjectResults>>();
        for (String word : wordEntities.keySet()) {
            List<WordObjectResults> entityInfos = wordEntities.get(word);
            Collections.sort(entityInfos, new WordObjectResults());
            Collections.reverse(entityInfos);
            String[] info = word.split("-");
            if (info[1].contains(posTag)) {
                posEntitieInfos.put(info[0], entityInfos);
            }
            /*else if (info[1].contains(TextAnalyzer.ADJECTIVE)) {
                this.adjectiveEntitieInfos.put(info[0], entityInfos);
            }*/
        }
        return posEntitieInfos;
    }
    /*public   Map<String, List<EntityInfo>> prepareSeperateLexicon(Map<String, List<EntityInfo>> nounEntitieInfos,String partsOfSpeech) throws IOException {
        if (nounEntitieInfos.isEmpty()) {
            return nounEntitieInfos;
        }
        Map<String, List<EntityInfo>> nounEntitieInfosNew=new HashMap<String, List<EntityInfo>>();
        
        List<LexiconUnit> lexiconUnts = new ArrayList<LexiconUnit>();
        for (String word : nounEntitieInfos.keySet()) {
            List<EntityInfo> list = nounEntitieInfos.get(word);
            Integer index = 0;
            //String firstWord=null;
            for (EntityInfo entityInfo : list) {
                partsOfSpeech=entityInfo.getPosTag();
                index = index + 1;
                List<String> pairs = new ArrayList<String>();
                System.out.println(word+".."+entityInfo);
            }
            
            if(partsOfSpeech.contains("_")){
                String info[]=partsOfSpeech.split("_");
               
            }
            
            /*if(partsOfSpeech.contains("_")){
                String info[]=partsOfSpeech.split("_");
                firstWord=info[0];
            }
            else
                firstWord=partsOfSpeech;
            if(nounEntitieInfosNew.containsKey(firstWord)){
                Map<String, List<EntityInfo>> value=nounEntitieInfosNew.get(firstWord);
                nounEntitieInfosNew.put(firstWord, nounEntitieInfos);
            }
            else{
                Map<String, List<EntityInfo>> value=new HashMap<String, List<EntityInfo>>();
                nounEntitieInfosNew.put(firstWord, nounEntitieInfos);
            }
        }
      return nounEntitieInfosNew;
    }*/

    public String getOutputDir() {
        return lexiconDirectory;
    }

    private String getFirstTag(String posTag) {
        String firstWord=null;
        if (posTag.contains("_")) {
                    String info[] = posTag.split("_");
                    firstWord = info[0];
                } else {
                    firstWord =posTag;
                }
        return firstWord;
    }

   
    /*private Map<String, List<WordObjectResults>> getWordObjectResults(String resultDir, String dbo_property) {
        List<File> files = FileFolderUtils.getFiles(resultDir, dbo_property, resultDir);
        Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();
        for (File file : files) {
            wordObjectResults = FileFolderUtils.readWordObjectFromJsonFile(file);
        }
        return wordObjectResults;
    }*/

    private Map<String, List<WordObjectResults>> getWordObjectResults(String resultDir, String dboProperty) {
        List<File> files=new ArrayList<File>();
        Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();

        Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(resultDir,dboProperty , ".json");
        if(pair.getValue0())
            files=pair.getValue1();
        
        for (File file : files) {
            wordObjectResults = FileFolderUtils.readWordObjectFromJsonFile(file);
        }
        return wordObjectResults;
    }

    public String getLexiconDirectory() {
        return lexiconDirectory;
    }

    public Map<String, List<LexiconUnit>> getLexiconPosTaggged() {
        return lexiconPosTaggged;
    }

    @Override
    public String toString() {
        return "Lexicon{" + "lexiconDirectory=" + lexiconDirectory + ", lexiconPosTaggged=" + lexiconPosTaggged + '}';
    }

    private String getPair(LineInfo lineInfo, String predictionRule) throws Exception {
        if (predictionRule.equals(predict_l_for_s_given_po)
                ||predictionRule.equals(predict_po_for_s_given_l)
                ||predictionRule.equals(predict_po_for_s_given_localized_l)) {
            return lineInfo.getPredicate() + " " + lineInfo.getObject();
        } else if (predictionRule.equals(predict_l_for_s_given_o)) {
            return lineInfo.getObject();
        } else if (predictionRule.equals(predict_l_for_o_given_s)) {
            return lineInfo.getSubject();
        } else if (predictionRule.equals(predict_l_for_o_given_sp)) {
            return lineInfo.getSubject() + " " + lineInfo.getPredicate();
        } else if (predictionRule.equals(predict_l_for_o_given_p)) {
            return lineInfo.getPredicate();
        } else if (predictionRule.equals(predict_l_for_s_given_p)) {
            return lineInfo.getPredicate();
        } else if (predictionRule.equals(predict_localized_l_for_s_given_p)
                ||predictionRule.equals(PredictionRules.predict_p_for_s_given_localized_l)
                ||predictionRule.equals(PredictionRules.predict_p_for_o_given_localized_l)) {
            return lineInfo.getPredicate();
        } else {
            throw new Exception("can not create key, check the KB!!");
        }
    }

    @Override
    public Boolean isPredict_l_for_s_given_po(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_l_for_s_given_o(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_l_for_o_given_s(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_l_for_o_given_sp(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_l_for_o_given_p(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_l_for_s_given_p(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_localized_l_for_s_given_p(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_po_for_s_given_l(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_po_for_s_given_localized_l(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_p_for_s_given_localized_l(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean isPredict_p_for_o_given_localized_l(String predictionRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  

}
