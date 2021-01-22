/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.wikipedia.parameters.Parameters;
import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.parameters.LingPattern;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FindTopMostInterWords;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class InterestedWords implements MenuOptions {

    //private Map<String, List<String>> propertyInterestedWords = new HashMap<String, List<String>>();
    private Map<String, String> posTagger = new HashMap<String, String>();
    private Integer numberOfEntitiesToLimitInFile = -1;
    private Integer listSize = -1;
    private List<String> sortFiles = new ArrayList<String>();
    //private Tables tables = null;
    private String className = null;
    private String outputLocation = null;
    private Set<String> properties = new HashSet<String>();
    private LingPattern lingPattern = null;

    public InterestedWords(LingPattern lingPattern, List<File>selectedPropertiesFiles, String dbo_ClassName, String outputDir,Integer limit) throws IOException, Exception {
        this.lingPattern = lingPattern;
        //this.tables = new Tables(propertyDir);
        this.className = dbo_ClassName;
        this.outputLocation = outputDir;
        this.prepareInterestingWords(selectedPropertiesFiles,limit);
        //this is for propertyInterestedWords 
        //this.getWords();
    }

    private void prepareInterestingWords(List<File>selectedPropertiesFiles,Integer limit) throws IOException, Exception {
        Integer index = 0;
        for (File file : selectedPropertiesFiles) {
            index = index + 1;
            if(index==limit)
                break;
            /*if (!file.getName().contains("dbo:nationality")) {
                continue;
              }*/
            String property = this.getProperty(file);
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            /*if(property.contains("dbo:birthDate")||property.contains("dbo:deathDate")){
                continue;
            }
            if(dbpediaEntitys.size()<numberOfEntitiesPerProperty)
                continue;
             */

            posTagger = new HashMap<String, String>();
            System.out.println(index + " fileSize:" + selectedPropertiesFiles.size() + " property:" + property + " numberOfEntities:" + dbpediaEntitys.size() + " table:" + file.getName());

            this.prepareInterestingWords(property, dbpediaEntitys);
        }
    }

    /*public void getWords() throws IOException {
        for (String sortFileName : sortFiles) {
            List<String> interestedWords = FileFolderUtils.getSortedList(sortFileName, this.parameters.getLingPattern().getNumberOfEntitiesPerWord(), this.parameters.getLingPattern().getNumberOfSelectedWordGenerated());
            System.out.println("interestedWords size:"+interestedWords.size());
            List<String> alphabeticSorted = new ArrayList<String>();
            alphabeticSorted.addAll(interestedWords);
            Collections.sort(alphabeticSorted);
            String tableName = new File(sortFileName).getName().replace(FILE_NOTATION, "");
            if (!alphabeticSorted.isEmpty()) {
                propertyInterestedWords.put(tableName, alphabeticSorted);
            }
        }
     
    }*/

    public void prepareInterestingWords(String property, List<DBpediaEntity> dbpediaEntitys) throws Exception {
        String str = this.prepareForAllProperties(property,dbpediaEntitys);
        /*if (str != null) {
            String sortFile = outputLocation + className + "_" + property + FILE_NOTATION;
            FileFolderUtils.stringToFiles(str, sortFile);
            this.sortFiles.add(sortFile);
        }*/
    }

    private String prepareForAllProperties(String property,List<DBpediaEntity> dbpediaEntities) throws Exception {
        Map<String, Integer> interestingWords = new HashMap<String, Integer>();
        Map<String, List<String>> interestingEntitities = new HashMap<String, List<String>>();

        posTagger = new HashMap<String, String>();
        Integer index = 0;
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            String url=dbpediaEntity.getEntityIndex();
           
            index = index + 1;
            Set<String> words = this.wordHash(dbpediaEntity);
            //System.out.println("running:"+index+" total Entities:"+dbpediaEntities.size()+ " totalWords:"+words.size());
            for (String word : words) {
                word = word.toLowerCase().trim();
                //System.out.println("word:"+word+" pos"+posTagger.get(word));
                if(word.contains("/")){
                    continue;
                }

                if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
                    continue;
                }
                if (TextAnalyzer.MONTH.contains(word)) {
                    continue;
                }
                //System.out.println("word"+word);
                Integer count = 0;
                if (interestingWords.containsKey(word)) {
                    count = interestingWords.get(word);
                    count = count + 1;
                    interestingWords.put(word, count);
                } else {
                    count = count + 1;
                    interestingWords.put(word, count);
                }
                
                if (interestingEntitities.containsKey(word)) {
                    List<String> entityList =entityList = interestingEntitities.get(word);
                    entityList.add(url);
                    interestingEntitities.put(word, entityList);
                } else {
                    List<String> entityList =new ArrayList<String>();
                    entityList.add(url);
                    interestingEntitities.put(word, entityList);
                }
            }

        }
        
        String str = SortUtils.sort(interestingWords, posTagger, numberOfEntitiesToLimitInFile);
        if (str != null) {
            String sortFile = outputLocation + className + "_" + property + FILE_NOTATION;
            FileFolderUtils.stringToFiles(str, sortFile);
            LinkedHashMap<String, List<String>> selectWordsEntities=this.saveEntities(interestingEntitities,property,1000);
            String jsonFile = outputLocation + "/"+className +"_" + property+ "/" ;
            //String jsonFile =outputLocation + className + "_" + property + FILE_ENTITY_NOTATION;
            FileFolderUtils.writeInterestingEntityEachToJsonFile(selectWordsEntities, jsonFile);
             System.out.println(" property:" + property + " numberOfWord:" + selectWordsEntities.size());
            this.sortFiles.add(sortFile);
        }
        
       
        return SortUtils.sort(interestingWords, posTagger, numberOfEntitiesToLimitInFile);
    }
    
     /*private String prepareForAllProperties(List<DBpediaEntity> dbpediaEntities) throws Exception {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        posTagger = new HashMap<String, String>();
        Integer index = 0;
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            index = index + 1;
            Set<String> words = this.wordHash(dbpediaEntity);
            //System.out.println("running:"+index+" total Entities:"+dbpediaEntities.size()+ " totalWords:"+words.size());
            for (String word : words) {
                word = word.toLowerCase().trim();
                //System.out.println("word:"+word+" pos"+posTagger.get(word));

                if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
                    continue;
                }
                if (TextAnalyzer.MONTH.contains(word)) {
                    continue;
                }
                //System.out.println("word"+word);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
            }

        }
        return SortUtils.sort(mostCommonWords, posTagger, numberOfEntitiesToLimitInFile);
    }*/

    
    private Set< String> wordHash(DBpediaEntity dbpediaEntity) {
        Set<String> words = new HashSet<String>();
        for (String word : dbpediaEntity.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.ADJECTIVE);
        }
        for (String word : dbpediaEntity.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.NOUN);
        }
        for (String word : dbpediaEntity.getVerbs()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.VERB);
        }
        return words;

    }


    /*private Set< String> wordHash(String text) throws Exception {
        Set<String> words = new HashSet<String>();
        Analyzer analyzer = new Analyzer(text, TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
        for (String word : analyzer.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.ADJECTIVE);
        }
        for (String word : analyzer.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.NOUN);
        }
        for (String word : analyzer.getVerbs()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.VERB);
        }
        return words;
    }*/
    private String getProperty(File file) {
        String property = file.getName().replace(className, "");
        property = property.replace("_", "");
        return property;
    }

    private Boolean isvalid(String word) {
        /*if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
            return false;
        } else*/
        if (TextAnalyzer.MONTH.contains(word)) {
            return false;
        }
        return true;
    }

    /*public Map<String, List<String>> getPropertyInterestedWords() {
        return propertyInterestedWords;
    }*/

    private Pair<Boolean, String> findPosTag(String word, Analyzer analyzer) {
        if (analyzer.getNouns().contains(word)) {
            return new Pair<Boolean, String>(Boolean.TRUE, TextAnalyzer.NOUN);
        } else if (analyzer.getAdjectives().contains(word)) {
            return new Pair<Boolean, String>(Boolean.TRUE, TextAnalyzer.ADJECTIVE);
        } else if (analyzer.getVerbs().contains(word)) {
            return new Pair<Boolean, String>(Boolean.TRUE, TextAnalyzer.VERB);
        }
        return new Pair<Boolean, String>(Boolean.FALSE, word);
    }

    /*private String prepareForAllPropertiesWithPostagging(List<DBpediaEntity> dbpediaEntities, Integer numberEntitiesSelected) throws Exception {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        Integer index = 0;
        Integer total=dbpediaEntities.size(),entitySize=100;
        
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            index = index + 1;
              if (index >entitySize) 
                  break;
        
           Analyzer analyzer = new Analyzer(dbpediaEntity.getText(), TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
           //Set<String> words = this.wordHash(dbpediaEntity.getText());
            Set<String> words = analyzer.getWords();
            Integer wordIndex=0,wordSize=words.size();
            for (String word : words) {
                wordIndex=wordIndex+1;
                String posTag=null;
                if (word.length() < 3) {
                    continue;
                }
                if (!this.isvalid(word)) {
                    continue;
                }
               Pair<Boolean,String>  posTagCheck=this.findPosTag(word,analyzer);
               
               if(!posTagCheck.getValue0())
                   continue;
               else
                   posTag=posTagCheck.getValue1();

                //System.out.println("word:"+word +" postag:"+posTag+" index:"+index+" total:"+entitySize+ " wordIndex:"+wordIndex+" wordSize"+wordSize);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
               
                posTagger.put(word, posTag);
            }

        }
        if (index < numberEntitiesSelected) {
            return null;
        }

        return SortUtils.sort(mostCommonWords, posTagger, numberOfEntitiesToLimitInFile);
    }*/
 /*private Set< String> wordHash(String text) throws Exception {
        Set<String> words = new HashSet<String>();
        Analyzer analyzer = new Analyzer(text, TextAnalyzer.POS_TAGGER_WORDS, numberOfSentencesOfAbstract);
        for (String word : analyzer.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.ADJECTIVE);
        }
        for (String word : analyzer.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.NOUN);
        }
        for (String word : analyzer.getVerbs()) {
            word = word.toLowerCase().trim();
            words.add(word);
            posTagger.put(word, TextAnalyzer.VERB);
        }
        return analyzer.g;
    }*/

    private LinkedHashMap<String, List<String>> saveEntities(Map<String, List<String>> interestingEntitities, String property, Integer numberOfTopLinguisticPattern) {
        LinkedHashMap<String, List<String>> selectedEntities = new LinkedHashMap<String, List<String>>();
        List<String> words = FindTopMostInterWords.findTopMostWords(outputLocation, property, numberOfTopLinguisticPattern);
        Set<String> tempWords=new HashSet<String>(words);
         Set<String> common = Sets.intersection(interestingEntitities.keySet(), tempWords); 
        
        for (String word : words) {
            if (interestingEntitities.containsKey(word)) {
                List<String> entities = interestingEntitities.get(word);
                selectedEntities.put(word, entities);
                //System.out.println("word:" + word +" "+selectedEntities.size());

            }
        }
        return selectedEntities;
       // Set<String> 
        //    answer = Sets.intersection(interestingEntitities.keySet(), tempWords); 

        //FileFolderUtils.writeInterestingEntityToJsonFile(interestingEntitities, jsonFile);
    }


}
