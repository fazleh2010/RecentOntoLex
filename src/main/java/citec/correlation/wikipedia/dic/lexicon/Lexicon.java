/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class Lexicon {
    private String lexiconDirectory=null;

    public Lexicon(String outputDir) throws IOException {
        this.lexiconDirectory=outputDir;
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

    public void prepareObjectLexicon(String resultDir, Set<String> posTags) throws IOException {
        Map<String, List<WordObjectResults>> wordEntities=this.getwordEntities(resultDir);
        for (String pos : posTags) {
            Map<String, List<WordObjectResults>> posEntitieInfos = entitiesSort(wordEntities, pos);
            this.preparePropertyLexicon(posEntitieInfos, pos,"word","fileName");
        }
    }

    public void preparePropertyLexicon(Map<String, List<WordObjectResults>> nounEntitieInfos, String givenPartsOfSpeech, String type,String fileName) throws IOException {
        if (nounEntitieInfos.isEmpty()) {
            return;
        }
        List<LexiconUnit> lexiconUnts = new ArrayList<LexiconUnit>();
        for (String word : nounEntitieInfos.keySet()) {
            List<WordObjectResults> list = nounEntitieInfos.get(word);
            LinkedHashMap<Integer, List<String>> entityInfos = new LinkedHashMap<Integer, List<String>>();
            Integer index = 0;
            String firstTag = null;
            Boolean flag = false;
             String postagOfWord =null;
            for (WordObjectResults entityInfo : list) {
                postagOfWord = entityInfo.getPosTag();
                firstTag = this.getFirstTag(entityInfo.getPosTag());
                if (firstTag.contains(givenPartsOfSpeech)) {
                    flag = true;
                }
                index = index + 1;
                List<String> pairs = new ArrayList<String>();
                pairs.add("pair=" + entityInfo.getPair());
                pairs.add("multiplyValue=" + entityInfo.getMultiply().toString());
                entityInfos.put(index, pairs);
            }
            if (flag) {
                LexiconUnit LexiconUnit = new LexiconUnit(word, postagOfWord, entityInfos);
                lexiconUnts.add(LexiconUnit);
            }

        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(fileName).toFile(), lexiconUnts);
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

    private Map<String, List<WordObjectResults>> getwordEntities(String resultDir) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
