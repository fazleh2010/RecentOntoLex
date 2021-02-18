/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.analyzer;

import static citec.correlation.wikipedia.experiments.ThresoldConstants.IR;
import edu.stanford.nlp.ling.TaggedWord;
import opennlp.tools.langdetect.*;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 * Dictionary LemmaAnalyzer Example in Apache OpenNLP
 */
public class Lemmatizer implements TextAnalyzer {

    private static Map<String, String> lemmasMap = new TreeMap<String, String>();
    private static Map<String, String> withOutPosTag = new TreeMap<String, String>();

    public Lemmatizer() {
        prepareLemmaMap();
    }

    private void prepareLemmaMap() {
        try {
            InputStream posModelIn = new FileInputStream(modelDir + posTagFile);
            POSModel posModel = new POSModel(posModelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            InputStream dictLemmatizer = new FileInputStream(modelDir + lemmaDictionary);
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

            for (List<String> lemmaPair : lemmatizer.getDictMap().keySet()) {
                List<String> posTags = lemmatizer.getDictMap().get(lemmaPair);
                String posTag = posTags.get(0);
                String key = lemmaPair.get(0) + "/" + posTag;
                key = key.strip();
                String value = lemmaPair.get(1);

                String plainToken = lemmaPair.get(0).strip();
                lemmasMap.put(key, value);
                withOutPosTag.put(plainToken, value);

                if (posTag.contains("VBN")) {
                    key = lemmaPair.get(0) + "/" + "VBD";
                    key = key.strip();
                    lemmasMap.put(key, value);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLemma(String taggedText) {
        String[] taggedTexts = taggedText.split(" ");
        List<String> tSentence = new ArrayList<String>();
        for (String string : taggedTexts) {
            String lemma = string;
            if (lemmasMap.containsKey(string)) {
                String value = lemmasMap.get(string);
                String[] info = string.split("/");
                lemma = value;
            } else {
                String[] info = string.split("/");
                lemma = info[0];
            }

            tSentence.add(lemma);
        }
        System.out.println(lemmasMap);

        return joinString(tSentence);
    }

    private String joinString(List<String> tSentence) {
        String str = "";
        for (String taggedWord : tSentence) {
            String line = taggedWord + " ";
            str += line;
        }
        str = StringUtils.substring(str, 0, str.length() - 1);
        return str;
    }

    public Map<String, String> getLemmasMap() {
        return lemmasMap;
    }

    public Map<String, String> getWithOutPosTag() {
        return withOutPosTag;
    }
    
    
    public Pair<Boolean, String> getLemmaWithoutPos(String word) {
        word = word.strip().trim();
        if (!word.contains(" ")) {
            if (withOutPosTag.containsKey(word)) {
                String lemma = withOutPosTag.get(word);
                return new Pair<Boolean, String>(Boolean.TRUE, lemma);
            } else {
                return new Pair<Boolean, String>(Boolean.FALSE, null);
            }

        } else {
            return new Pair<Boolean, String>(Boolean.FALSE, null);
        }

    }

    public static void main(String[] args) {
        String text = "abashes/VBZ";
        String text3 = "abasements/NNS abashes/VBZ";
        String text4 = "attended/JJ";
        String text5 = "attended/VBD";
        text5 = "produced/VBD";

        Lemmatizer lemmaAnalyzer = new Lemmatizer();
        for (String token : withOutPosTag.keySet()) {
            System.out.println(token + " " + withOutPosTag.get(token));
        }
        
        Pair<Boolean, String> pair=lemmaAnalyzer.getLemmaWithoutPos("attended");
        if(pair.getValue0()){
            System.out.println(pair.getValue1());
        }
        //String lemma=lemmaAnalyzer.getLemma(text5);
        //System.out.println(lemma);

        /*for(String key:lemmaAnalyzer.getLemmasMap().keySet()){
               String value=lemmaAnalyzer.getLemmasMap().get(key);
               if(key.contains("attended"))
                  System.out.println(key+" "+value);
           }*/
    }

    /*public static void main(String[] args){
        try{
            InputStream posModelIn = new FileInputStream(modelDir+posTagFile);
            POSModel posModel = new POSModel(posModelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            InputStream dictLemmatizer = new FileInputStream(modelDir+lemmaDictionary);
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
            
            for (List<String> lemmaPair : lemmatizer.getDictMap().keySet()) {
                List<String> posTags = lemmatizer.getDictMap().get(lemmaPair);
                String key = lemmaPair.get(0) + "/" + posTags.get(0);
                key=key.strip();
                String value = lemmaPair.get(1);
                System.out.println(key + " " + value);
                lemmasMap.put(key, value);
            }
 
            // finding the lemmas
                             
           String[] lemmas = lemmatizer.lemmatize(tokens, tags);
 
            // printing the results
            System.out.println("\nPrinting lemmas for the given sentence...");
            System.out.println("WORD -POSTAG : LEMMA");
            for(int i=0;i< tokens.length;i++){
                System.out.println(tokens[i]+" -"+tags[i]+" : "+lemmas[i]);
            }
 
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
