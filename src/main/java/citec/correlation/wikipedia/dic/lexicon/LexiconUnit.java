/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import citec.correlation.wikipedia.results.LineInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
public class LexiconUnit {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("pattern")
    private String word;
    @JsonProperty("partsOfSpeech")
    private String partsOfSpeech;
    @JsonProperty("index")
    private LinkedHashMap<Integer, List<String>> entityInfos = new LinkedHashMap<Integer, List<String>>();
    @JsonIgnore
    private LinkedHashMap<Integer, LineInfo> lineInfos = new LinkedHashMap<Integer, LineInfo>();
   

    public LexiconUnit() {

    }
    
      public LexiconUnit(Integer id,String word, String partsOfSpeech, LinkedHashMap<Integer, List<String>> entityInfos) {
        this.id=id;
        this.partsOfSpeech = partsOfSpeech;
        this.word = word;
        this.entityInfos = entityInfos;
    }


    public LexiconUnit(Integer id,String word, String partsOfSpeech, LinkedHashMap<Integer, List<String>> entityInfos,LinkedHashMap<Integer, LineInfo> kbLineList) {
        this.id=id;
        this.partsOfSpeech = partsOfSpeech;
        this.word = word;
        this.entityInfos = entityInfos;
        this.lineInfos=kbLineList;
    }

    /*public LexiconUnit(LexiconUnit LexiconUnit, LinkedHashMap<Integer, List<String>> newEntityInfos) {
        this.id = LexiconUnit.getId();
        this.partsOfSpeech = LexiconUnit.getPartsOfSpeech();
        this.word = LexiconUnit.getWord();
        this.entityInfos = newEntityInfos;
    }*/

    public String getWord() {
        return word;
    }

    public LinkedHashMap<Integer, List<String>> getEntityInfos() {
        return entityInfos;
    }

    public String getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public Integer getId() {
        return id;
    }

    public LinkedHashMap<Integer, LineInfo> getLineInfos() {
        return lineInfos;
    }

    @Override
    public String toString() {
        return "LexiconUnit{" + "Integer=" + id + ", word=" + word + ", partsOfSpeech=" + partsOfSpeech + ", entityInfos=" + entityInfos + '}';
    }
    
   

}
