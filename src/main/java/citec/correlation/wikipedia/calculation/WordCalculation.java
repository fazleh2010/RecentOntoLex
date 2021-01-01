 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.parameters.WordThresold;
import citec.correlation.wikipedia.table.Tables;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class WordCalculation implements TextAnalyzer,WordThresold {

    private Map<String, List<ObjectWordResults>> objectWordResults = new HashMap<String, List<ObjectWordResults>>();
    private Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();

    public WordCalculation(String tableDir,String className,String selectWordDir,String resultDir) throws IOException, Exception {
        this.probabiltyCalculation(tableDir,selectWordDir,resultDir);
    }
    
    private void probabiltyCalculation(String inputDir, String selectWordDir,String resultDir) throws IOException, Exception {
        List<File> allFiles = FileFolderUtils.getFiles(inputDir, ".json");
        if (allFiles.isEmpty()) {
            throw new Exception("There is no files in " + inputDir + " to generate properties!!");
        }

        for (File file : allFiles) {
            String tableName = file.getName();
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            if (dbpediaEntitys.size() < numberOfEntitiesrmSelected) {
                continue;
            }
            if (!tableName.contains("dbo:party")) {
                continue;
            }
            System.out.println("tableName:" + tableName);

            String property = Tables.getProperty(tableName);
            String classNameAndProperty = Tables.getClassAndProperty(tableName);
            LinkedHashMap<String,String> selectedWordsHash = this.selectedWords(selectWordDir,classNameAndProperty,".txt");
           
            Map<String, List<DBpediaEntity>> entityCategories = new HashMap<String, List<DBpediaEntity>>();
            entityCategories = this.getObjectsOfproperties(property, dbpediaEntitys);
            probabiltyCalculation(tableName,property,selectedWordsHash,dbpediaEntitys, entityCategories,resultDir);

        }
    }
    
    private void probabiltyCalculation(String tableName,String property, LinkedHashMap<String,String> selectedWordsHash,List<DBpediaEntity> dbpediaEntities, Map<String, List<DBpediaEntity>> entityCategories,String resultDir) throws IOException, Exception {
        //all KBs..........................
        Integer index = 0;
        List<ObjectWordResults> kbResults = new ArrayList<ObjectWordResults>();
        
        for (String objectOfProperty : entityCategories.keySet()) {
            List<WordResult> results = new ArrayList<WordResult>();
            List<DBpediaEntity> dbpediaEntitiesGroup = entityCategories.get(objectOfProperty);
            System.out.println(dbpediaEntitiesGroup);
            Integer numberOfEntitiesFoundInObject = dbpediaEntitiesGroup.size();
            if (dbpediaEntitiesGroup.size() < objectMinimumEntities) {
                continue;
            }
                        System.out.println("objectOfProperty:" + objectOfProperty);


            /*if (!FormatAndMatch.isValidForObject(objectOfProperty)) {
                continue;
            }
            objectOfProperty = this.shortForm(objectOfProperty);*/


            //all words
            List<String> selectedWords = new ArrayList<String>(selectedWordsHash.keySet());
            if (this.numberOfWordLimit > -1 && !selectedWords.isEmpty()) {
                if (selectedWords.size() > numberOfWordLimit) {
                    selectedWords = selectedWords.subList(0, numberOfWordLimit);
                }
            }
           
                
            for (String word : selectedWords) {
                //System.out.println("word:" + word);
                index = index + 1;

                String partsOfSpeech = selectedWordsHash.get(word);
                /*if (this.interestedWords.getAdjectives().contains(word)) {
                    partsOfSpeech = TextAnalyzer.ADJECTIVE;
                } else if (this.interestedWords.getNouns().contains(word)) {
                    partsOfSpeech = TextAnalyzer.NOUN;
                }*/

                WordResult result = null;
                ResultTriple pairWord = this.countConditionalProbabilities(tableName, dbpediaEntitiesGroup, property, objectOfProperty, word, WordResult.PROBABILITY_WORD_GIVEN_OBJECT);
                ResultTriple pairObject = this.countConditionalProbabilities(tableName, dbpediaEntities, property, objectOfProperty, word, WordResult.PROBABILITY_OBJECT_GIVEN_WORD);

                
                if (pairWord != null && pairObject != null) {
                    Double wordCount = (Double) pairWord.getProbability_value();
                    Double objectCount = (Double) pairObject.getProbability_value();
                    System.out.println("objectOfProperty:" + objectOfProperty+" pairWord:"+pairWord+" pairObject:"+pairObject);


                    // if ((wordCount * objectCount) > 0.01 && !(wordCount == 0 && objectCount == 0) && wordCount != 1.0 && objectCount != 1.0) {
                    //if ((wordCount * objectCount) > 0.01 && !(wordCount == 0 && objectCount == 0)) {
                    result = new WordResult(pairWord, pairObject, word, partsOfSpeech);
                    results.add(result);
                    System.out.println("result:" + result);
                    //}
                }
                //}
            }//all words end

            if (!results.isEmpty()) {
                ObjectWordResults kbResult = new ObjectWordResults(property, objectOfProperty, numberOfEntitiesFoundInObject, results, topWordLimitToConsiderThres);
                kbResults.add(kbResult);
            }

        }

        this.objectWordResults.put(tableName, kbResults);
        String str = entityResultToString(kbResults);
        String filenameDisplay = resultDir + tableName.replaceAll(".json", "_probability.txt");
        FileFolderUtils.writeToTextFile(str, filenameDisplay);
        String wordObjectsFileName = resultDir + tableName.replaceAll(".json", "_wordObject.json");
        FileFolderUtils.writeResultDetail(this.wordObjectResults,wordObjectsFileName);
        String objectWordsFileName = resultDir + tableName.replaceAll(".json", "_objectWord.json");
        FileFolderUtils.writeEntityResults(this.objectWordResults,objectWordsFileName);

    }
    

    private  Map<String, List<DBpediaEntity>> getObjectsOfproperties(String property,List<DBpediaEntity> dbpediaEntities) {
        Map<String, List<DBpediaEntity>> entityCategories = new HashMap<String, List<DBpediaEntity>>();
       
        LinkedHashSet<String> allObjects = new LinkedHashSet<String>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            if (!dbpediaEntity.getProperties().isEmpty()) {
                if(dbpediaEntity.getProperties().containsKey(property)){
                   LinkedHashSet<String> objects = new LinkedHashSet<String>(dbpediaEntity.getProperties().get(property));
                   allObjects.addAll(objects); 
                }
            }
        }

        for (DBpediaEntity DBpediaEntity : dbpediaEntities) {
            for (String key : DBpediaEntity.getProperties().keySet()) {
                if (!DBpediaEntity.getProperties().get(key).isEmpty()) {
                    String value = DBpediaEntity.getProperties().get(key).iterator().next();
                    if (allObjects.contains(value)) {
                        List<DBpediaEntity> list = new CopyOnWriteArrayList<DBpediaEntity>();
                        if (entityCategories.containsKey(value)) {
                            list = entityCategories.get(value);
                            list.add(DBpediaEntity);
                            entityCategories.put(value, list);
                        } else {
                            list.add(DBpediaEntity);
                            entityCategories.put(value, list);
                        }

                    }
                }

            }
        }
        return entityCategories;
    }


    private ResultTriple countConditionalProbabilities(String tableName, List<DBpediaEntity> dbpediaEntities, String propertyName, String objectOfProperty, String word, Integer flag) throws IOException {
        Double OBJECT_AND_WORD_FOUND = 0.0, OBJECT_FOUND = 0.0, WORD_FOUND = 0.0;
        Integer transactionNumber=dbpediaEntities.size();
        
        Pair<String, Double> pair = null;
        ResultTriple triple=null;   

        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            String text = dbpediaEntity.getText();
            Boolean objectFlag = false, wordFlag = false;

            if (dbpediaEntity.getProperties().containsKey(propertyName)) {

                List<String> objects = dbpediaEntity.getProperties().get(propertyName);
                if (objects.contains(objectOfProperty)) {
                    OBJECT_FOUND++;
                    objectFlag = true;
                }
            }
                      
            if (isWordContains(dbpediaEntity.getText(),word)){
                WORD_FOUND++;
                wordFlag = true;                 
            }

            if (objectFlag && wordFlag) {
                OBJECT_AND_WORD_FOUND++;
            }

        }

        objectOfProperty=objectOfProperty.replaceAll("http://dbpedia.org/resource/", "");
        //objectOfProperty="object[res:"+objectOfProperty+"]";
        objectOfProperty="object";
        String probability_object_word_str =  "P(" + objectOfProperty + "|" + word + ")";
        String probability_word_object_str = "P(" + word + "|" + objectOfProperty + ")";

        //if (WORD_FOUND > 10) {
        if (flag == WordResult.PROBABILITY_OBJECT_GIVEN_WORD) {
            Double probability_object_word = (OBJECT_AND_WORD_FOUND) / (WORD_FOUND);
            if(probability_object_word<objectGivenWordThres)
                return null;
            
            Double confidenceWord=(WORD_FOUND/transactionNumber);
            Double confidenceKB=(OBJECT_FOUND/transactionNumber);
            Double confidenceKB_WORD=(OBJECT_AND_WORD_FOUND/transactionNumber);
            Double lift=(confidenceKB_WORD/(confidenceWord*confidenceKB));
            
            triple=new ResultTriple(probability_object_word_str,probability_object_word,confidenceWord,confidenceKB,confidenceKB_WORD,lift,OBJECT_AND_WORD_FOUND,WORD_FOUND,null) ;
            //pair = new Pair<Triple, Double>(probability_object_word_str, probability_object_word);
            
        }
        else if (flag == WordResult.PROBABILITY_WORD_GIVEN_OBJECT) {
            Double probability_word_object = (OBJECT_AND_WORD_FOUND) / (OBJECT_FOUND);
            if(probability_word_object<wordGivenObjectThres)
                return null;
            //pair = new Pair<Triple, Double>(probability_word_object_str, probability_word_object);
            triple=new ResultTriple(probability_word_object_str,probability_word_object,WORD_FOUND,null,OBJECT_AND_WORD_FOUND,null,OBJECT_AND_WORD_FOUND,null,OBJECT_FOUND) ;
        } 
        
        return triple;

    }
  
    private boolean isWordContains(String text,String B) {
       if (text.toLowerCase().toString().contains(B)) {
            return true;
        }
        return false;
    }


    /*private  void findInterestedWordsForEntities(Tables tables,String fileName) {
         Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
         Map<String, List<String>> tableTopwords = new HashMap<String, List<String>>();

        
        for (String tableName : tables.getEntityTables().keySet()) {
             List<DBpediaEntity> dbpediaEntities = tables.getEntityTables().get(tableName).getDbpediaEntities();     
             for(DBpediaEntity dbpediaEntity:dbpediaEntities){
                 for (String word:dbpediaEntity.getWords()){
                     word=word.toLowerCase().trim();
                    
                      Integer count=0;
                      if(mostCommonWords.containsKey(word)){
                         count= mostCommonWords.get(word);
                         count=count+1;
                         mostCommonWords.put(word, count);
                      }else{
                         count=count+1;
                         mostCommonWords.put(word, count);
                      }     
                 }
                
             }
             String str = SortUtils.sort(mostCommonWords,new TreeMap<String,String>(),100);
             FileFolderUtils.stringToFiles(str, tableName);
             //tableTopwords.put(tableName, topWords);
        }
        
    }*/
    
    /*private void findInterestedWordsForEntities(List<DBpediaEntity> dbpediaEntities,String fileName,Integer number) {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            Set<String> adjectives=dbpediaEntity.getAdjectives();
            Set<String> list=dbpediaEntity.getNouns();
            list.addAll(adjectives);
            for (String word : list) {
                word = word.toLowerCase().trim();
                if(TextAnalyzer.ENGLISH_STOPWORDS.contains(word))
                    continue;
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
             String str = SortUtils.sort(mostCommonWords,new TreeMap<String,String>(),number);
             FileFolderUtils.stringToFiles(str, fileName);

    }*/
    
    public String getFileString(List<ObjectWordResults> entityResults) throws Exception {
        if (entityResults.isEmpty()) {
            throw new Exception("No result available to read!!");
        }

        String str = "";
            
        for (ObjectWordResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getObject()+ "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject()+ "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence="Lift="+wordResults.getLift()+" "+"{Confidence"+ " "+"word="+wordResults.getConfidenceWord()+" object="+wordResults.getConfidenceObject()+" ="+wordResults.getConfidenceObjectAndKB()+" "+"Lift="+wordResults.getOtherLift()+"}";
                liftAndConfidence="";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " +wordResults.getPosTag()+ "  " + multiply + "  " + probabilty + "  "+liftAndConfidence+"\n";
                wordSum += wordline;
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }

  private  String entityResultToString(List<ObjectWordResults> entityResults) {
      if(entityResults.isEmpty()){
          return null;
      }
        String str = "";
        for (ObjectWordResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getObject() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject()+ "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence="Lift="+wordResults.getLift()+" "+"{Confidence"+ " "+"word="+wordResults.getConfidenceWord()+" object="+wordResults.getConfidenceObject()+" ="+wordResults.getConfidenceObjectAndKB()+" "+"Lift="+wordResults.getOtherLift()+"}";
                liftAndConfidence="";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " + wordResults.getPosTag()+ "  "+multiply + "  " + probabilty + "  "+liftAndConfidence+"\n";
                wordSum += wordline;
                String key=wordResults.getWord();
                
                 List<WordObjectResults> propertyObjects =new ArrayList<WordObjectResults>();
                 WordObjectResults entityInfo =null;
                if (wordObjectResults.containsKey(key)) {
                    propertyObjects = wordObjectResults.get(key);
                    entityInfo = new WordObjectResults(wordResults.getPosTag(), entities.getProperty(), entities.getObject(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                } else {
                    entityInfo = new WordObjectResults(wordResults.getPosTag(), entities.getProperty(), entities.getObject(), wordResults.getMultipleValue(), wordResults.getProbabilities());

                }
                propertyObjects.add(entityInfo);
                this.wordObjectResults.put(key, propertyObjects);
                    
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }


    private  LinkedHashMap<String,String> selectedWords(String selectWordDir, String classNameAndProperty, String txt) throws IOException {
        String fileName = FileFolderUtils.getFile(selectWordDir, classNameAndProperty, txt);
        LinkedHashMap<String,String> selectedWords = FileFolderUtils.getListString(selectWordDir + fileName);
        return selectedWords;
    }
    
      
    private String shortForm(String url) {
        String objectUrl = "http://dbpedia.org/resource/";
        if (url.contains(objectUrl)) {
            return url.replace(objectUrl, "");
        }
        return url;
    }
    
     public Map<String, List<WordObjectResults>> getWordEntities() {
        return wordObjectResults;
    }

   
}
