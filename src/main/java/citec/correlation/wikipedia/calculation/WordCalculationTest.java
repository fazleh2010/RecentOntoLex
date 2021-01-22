/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.wikipedia.parameters.ProbabilityT;
import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FindTopMostInterWords;
import static citec.correlation.wikipedia.utils.FindTopMostInterWords.getEntities;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.util.Sets;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class WordCalculationTest implements TextAnalyzer {

    private Map<String, List<ObjectWordResults>> objectWordResults = new HashMap<String, List<ObjectWordResults>>();
    private Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();
    private String className = null;
    private String proccessedPropertiesFile = null;
    private Set<String> selectedProperties = new TreeSet<String>();
    private ProbabilityT probabilityT;
    private String selectWordDir = null;

    public WordCalculationTest(ProbabilityT probabilityT, String className, String selectWordDir, String resultDir, List<File> selectedPropertiesFiles, String proccessedPropertiesFile) throws IOException, Exception {
        this.probabilityT = probabilityT;
        this.className = className;
        this.selectWordDir = selectWordDir;
        this.proccessedPropertiesFile = proccessedPropertiesFile;
        this.probabiltyCalculation(selectWordDir, resultDir, selectedPropertiesFiles, proccessedPropertiesFile);
    }

    private void probabiltyCalculation(String selectWordDir, String resultDir, List<File> selectedPropertiesFiles, String proccessedPropertiesFile) throws IOException, Exception {
        Set<File> selectedFiles = this.propertiyWiseFilter(selectedPropertiesFiles);

        Integer index = 0;
        for (File file : selectedFiles) {

            String tableName = file.getName();
            String property = Tables.getProperty(tableName);
          
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitysT = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            //List<String> dbpediaEntitys =this.getAllEntities(dbpediaEntitysT);
            /*if (dbpediaEntitys.size() < numberOfEntitiesPerProperty) {
                continue;
            }*/
            /*if (!tableName.contains("dbo:nationality")) {
                continue;
            }*/

            String classNameAndProperty = Tables.getClassAndProperty(tableName);
            LinkedHashMap<String, String> selectedWordsHash = this.selectedWords(selectWordDir, classNameAndProperty, ".txt");
            
            /*if(selectedWordsHash.isEmpty())
                continue;*/

 /* if (!tableName.contains("dbo:party")) {
                continue;
              }*/
            LinkedHashMap<String, List<String>> selectedWordEntities = getEntities(selectWordDir+classNameAndProperty);
            //System.out.println(selectedWordEntities.keySet());
            //System.out.println("tableName:" + tableName + " selectedWords:" + selectedWordsHash.size() + " entities:" + dbpediaEntitysT.size());

            Map<String, List<String>> entityCategories = new TreeMap<String, List<String>>();
            entityCategories = this.getObjectsOfproperties(property, dbpediaEntitysT);
            FileFolderUtils.appendToFile(proccessedPropertiesFile, property);
            probabiltyCalculation(selectedWordEntities,index, selectedFiles.size(), tableName, property, selectedWordsHash, dbpediaEntitysT, entityCategories, resultDir);
            index = index + 1;
            
        }
    }

    private void probabiltyCalculation( LinkedHashMap<String, List<String>> selectedWordEntities,Integer fileCount, Integer fileSize, String tableName, String property, LinkedHashMap<String, String> selectedWordsHash, List<DBpediaEntity> dbpediaEntities, Map<String, List<String>> entityCategories, String resultDir) throws IOException, Exception {
        //all KBs..........................
        Integer index = 0, count = 0;
        Integer numberOfTopLinguisticPattern = this.probabilityT.getNumberOfTopLinguisticPattern();
        List<ObjectWordResults> kbResults = new ArrayList<ObjectWordResults>();
        
      

        Set<String> filterObjects = this.filterObjects(entityCategories);
        if (filterObjects.isEmpty()) {
            return;
        }

        for (String objectOfProperty : filterObjects) {
            List<WordResult> results = new ArrayList<WordResult>();
            List<String> dbpediaEntitiesGroup = entityCategories.get(objectOfProperty);
            Integer numberOfEntitiesFoundInObject = dbpediaEntitiesGroup.size();

            /*if (dbpediaEntitiesGroup.size() < numberOfEntitiesForObject) {
                continue;
            }*/
            count = count + 1;
            System.out.println(" fileCount:" + fileCount + " FileSize:" + fileSize + " tableName:" + tableName);
            System.out.println(" objectCount:" + count + " totalObject:" + entityCategories.size() + " property:" + property + " object:" + objectOfProperty);
            System.out.println();

            //if (!FormatAndMatch.isValidForObject(objectOfProperty)) {
            //    continue;
            //}
            //objectOfProperty = this.shortForm(objectOfProperty);
            //all words
            List<String> selectedWords = new ArrayList<String>(selectedWordsHash.keySet());
            //LinkedHashMap<String, List<String>> selectedWordEntities = FindTopMostInterWords.getEntities(selectWordDir+tableName);
            if (numberOfTopLinguisticPattern > -1 && !selectedWords.isEmpty()) {
                if (selectedWords.size() > numberOfTopLinguisticPattern) {
                    selectedWords = selectedWords.subList(0, numberOfTopLinguisticPattern);
                }
            }
            
              List<String> objectFoundEntities =this.getObjectCount(property,objectOfProperty,dbpediaEntities);
              //System.out.println("From Entity"+"OBJECT_FOUND:" + objectFoundEntities);

            for (String word : selectedWords) {
                
               
                //System.out.println("word:" + word);
                index = index + 1;

                String partsOfSpeech = selectedWordsHash.get(word);

                WordResult result = null;
                ResultTriple pairWord = this.probWordGivenObject(selectedWordEntities, dbpediaEntitiesGroup, objectOfProperty, word);
                ResultTriple pairObject = this.probObjectGivenWord(selectedWordEntities,objectFoundEntities,dbpediaEntities, objectOfProperty, word,property);
                if (pairWord != null && pairObject != null) {
                    Double wordCount = (Double) pairWord.getProbability_value();
                    Double objectCount = (Double) pairObject.getProbability_value();
                    //System.out.println("objectOfProperty:" + objectOfProperty+" pairWord:"+pairWord+" pairObject:"+pairObject);

                    // if ((wordCount * objectCount) > 0.01 && !(wordCount == 0 && objectCount == 0) && wordCount != 1.0 && objectCount != 1.0) {
                    if ((wordCount * objectCount) > this.probabilityT.getMultiplyValue() && !(wordCount == 0 && objectCount == 0)) {
                        if (pairWord.getProbability_value() == 1.0 || pairObject.getProbability_value() == 1.0) {
                            //System.out.println("word:"+pairWord.getProbability_value()+" object"+pairObject.getProbability_value());
                        } /*else if(pairWord.getProbability_value()<0.045||pairObject.getProbability_value()<0.045){
                               //System.out.println("word:"+pairWord.getProbability_value()+" object"+pairObject.getProbability_value());
                        }*/ else {
                            result = new WordResult(pairWord, pairObject, word, partsOfSpeech);
                            //System.out.println("result:"+result);
                            results.add(result);
                        }

                        //System.out.println("result:" + result);
                    }
                }
                //}
            }//all words end

            if (!results.isEmpty()) {
                ObjectWordResults kbResult = new ObjectWordResults(property, objectOfProperty, numberOfEntitiesFoundInObject, results, this.probabilityT.getProbResultTopWordLimit());
                kbResults.add(kbResult);
            }

        }

        if (kbResults.isEmpty()) {
            return;
        }
        this.objectWordResults.put(tableName, kbResults);
        String str = entityResultToString(kbResults);
        //System.out.println("tableName:"+tableName+" "+kbResults.size());
        String filenameDisplay = resultDir + tableName.replaceAll(".json", "_probability.txt");
        String wordObjectsFileName = resultDir + "wordObject_" + tableName;
        String objectWordsFileName = resultDir + "objectWord_" + tableName;

        /*System.out.println(tableName+" size:"+dbpediaEntities.size());
         System.out.println(tableName+" kbResults:"+kbResults);
         System.out.println(tableName+" objectWordResults:"+objectWordResults);
          System.out.println(tableName+" wordObjectResults:"+wordObjectResults.size());*/
        FileFolderUtils.writeResultDetail(this.wordObjectResults, wordObjectsFileName);
        FileFolderUtils.writeEntityResults(this.objectWordResults, objectWordsFileName);
        FileFolderUtils.writeToTextFile(str, filenameDisplay);

    }

    private ResultTriple probWordGivenObject(LinkedHashMap<String, List<String>> selectedWordEntities, List<String> dbpediaEntities, String objectOfProperty, String word) throws IOException, Exception {
        Double OBJECT_AND_WORD_FOUND = 0.0, OBJECT_FOUND = 0.0, WORD_FOUND = 0.0;
        Integer transactionNumber = dbpediaEntities.size();
        Pair<String, Double> pair = null;
        ResultTriple triple = null;
        OBJECT_FOUND = Double.valueOf(dbpediaEntities.size());
        List<String> entities = selectedWordEntities.get(word);
        Set<String> wordEntities = this.intersection(dbpediaEntities, entities);
        if(wordEntities.isEmpty())
             WORD_FOUND =0.0;
        WORD_FOUND = Double.valueOf(wordEntities.size());
        Set<String> objectWordEntities = this.intersection(dbpediaEntities, new ArrayList(wordEntities));
        if(objectWordEntities.isEmpty())
             OBJECT_AND_WORD_FOUND =0.0;
        OBJECT_AND_WORD_FOUND = Double.valueOf(objectWordEntities.size());
        //System.out.println("probWordGivenObject"+"OBJECT_FOUND:" + OBJECT_FOUND+" "+"WORD_FOUND:" + WORD_FOUND+" "+"OBJECT_AND_WORD_FOUND:" + OBJECT_AND_WORD_FOUND);
        objectOfProperty = objectOfProperty.replaceAll("http://dbpedia.org/resource/", "");
        objectOfProperty = "object";
        String probability_word_object_str = "P(" + word + "|" + objectOfProperty + ")";

        Double probability_word_object = (OBJECT_AND_WORD_FOUND) / (OBJECT_FOUND);
        if (probability_word_object < this.probabilityT.getProbabiltyOfwordGivenObjectThresold()) {
            return null;
        }
        triple = new ResultTriple(probability_word_object_str, probability_word_object, WORD_FOUND, null, OBJECT_AND_WORD_FOUND, null, OBJECT_AND_WORD_FOUND, null, OBJECT_FOUND);

        return triple;

    }
    
    
     private ResultTriple probObjectGivenWord(LinkedHashMap<String, List<String>> selectedWordEntities, List<String> objectFoundEntities, List<DBpediaEntity> allDbpediaEntities,  String objectOfProperty, String word,String propertyName) throws IOException {
        Double OBJECT_AND_WORD_FOUND = 0.0, OBJECT_FOUND=0.0,WORD_FOUND = 0.0;
        Double confidenceWord =0.0,confidenceKB=0.0,confidenceKB_WORD=0.0,lift=0.0;
        Pair<String, Double> pair = null;
        ResultTriple triple = null;
        //Integer transactionNumber = allDbpediaEntities.size();
        
      

        OBJECT_FOUND = Double.valueOf(objectFoundEntities.size());
        //System.out.println("From all"+"OBJECT_FOUND:" + OBJECT_FOUND);
        
         List<String> wordEntities =new ArrayList<String>();
          Set<String> objectWordEntities =new HashSet<String>();
        
         try {
             wordEntities = selectedWordEntities.get(word);
             if (!wordEntities.isEmpty()) {
                 WORD_FOUND = Double.valueOf(wordEntities.size());
             }

         } catch (Exception ex) {
             WORD_FOUND = 0.0;
         }

         try {
             objectWordEntities = this.intersection(objectFoundEntities, new ArrayList(wordEntities));
             if (!objectWordEntities.isEmpty()) {
                 OBJECT_AND_WORD_FOUND = Double.valueOf(objectWordEntities.size());
             }
         } catch (Exception ex) {
             OBJECT_AND_WORD_FOUND = 0.0;
         }

      
        //System.out.println("Object"+"OBJECT_FOUND:" + OBJECT_FOUND+" "+"WORD_FOUND:" + WORD_FOUND+" "+"OBJECT_AND_WORD_FOUND:" + OBJECT_AND_WORD_FOUND);
        objectOfProperty = objectOfProperty.replaceAll("http://dbpedia.org/resource/", "");
        objectOfProperty = "object";
        String probability_object_word_str = "P(" + objectOfProperty + "|" + word + ")";

        //if (WORD_FOUND > 10) {
        Double probability_object_word = (OBJECT_AND_WORD_FOUND) / (WORD_FOUND);
        if (probability_object_word < this.probabilityT.getProbabiltyOfObjectGivenWordThresold()) {
            return null;
        }

        /*confidenceWord = (WORD_FOUND / transactionNumber);
        confidenceKB = (OBJECT_FOUND / transactionNumber);
        confidenceKB_WORD = (OBJECT_AND_WORD_FOUND / transactionNumber);
        lift = (confidenceKB_WORD / (confidenceWord * confidenceKB));*/
        triple = new ResultTriple(probability_object_word_str, probability_object_word, confidenceWord, confidenceKB, confidenceKB_WORD, lift, OBJECT_AND_WORD_FOUND, WORD_FOUND, null);

        return triple;

    }

    
    private Map<String, List<String>> getObjectsOfproperties(String property, List<DBpediaEntity> dbpediaEntities) {
        Map<String, List<String>> entityCategories = new HashMap<String, List<String>>();

        LinkedHashSet<String> allObjects = new LinkedHashSet<String>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            if (!dbpediaEntity.getProperties().isEmpty()) {
                if (dbpediaEntity.getProperties().containsKey(property)) {
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
                        List<String> list = new CopyOnWriteArrayList<String>();
                        if (entityCategories.containsKey(value)) {
                            list = entityCategories.get(value);
                            list.add(DBpediaEntity.getEntityIndex());
                            entityCategories.put(value, list);
                        } else {
                            list.add(DBpediaEntity.getEntityIndex());
                            entityCategories.put(value, list);
                        }

                    }
                }

            }
        }
        return entityCategories;
    }

   
    private boolean isWordContains(String text, String B) {
        if (text.toLowerCase().toString().contains(B)) {
            return true;
        }
        return false;
    }


   
 
    public String getFileString(List<ObjectWordResults> entityResults) throws Exception {
        if (entityResults.isEmpty()) {
            throw new Exception("No result available to read!!");
        }

        String str = "";

        for (ObjectWordResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getObject() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence = "Lift=" + wordResults.getLift() + " " + "{Confidence" + " " + "word=" + wordResults.getConfidenceWord() + " object=" + wordResults.getConfidenceObject() + " =" + wordResults.getConfidenceObjectAndKB() + " " + "Lift=" + wordResults.getOtherLift() + "}";
                liftAndConfidence = "";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " + wordResults.getPosTag() + "  " + multiply + "  " + probabilty + "  " + liftAndConfidence + "\n";
                wordSum += wordline;
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }

    private String entityResultToString(List<ObjectWordResults> entityResults) {
        if (entityResults.isEmpty()) {
            return null;
        }
        String str = "";
        for (ObjectWordResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getObject() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence = "Lift=" + wordResults.getLift() + " " + "{Confidence" + " " + "word=" + wordResults.getConfidenceWord() + " object=" + wordResults.getConfidenceObject() + " =" + wordResults.getConfidenceObjectAndKB() + " " + "Lift=" + wordResults.getOtherLift() + "}";
                liftAndConfidence = "";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " + wordResults.getPosTag() + "  " + multiply + "  " + probabilty + "  " + liftAndConfidence + "\n";
                wordSum += wordline;
                String key = wordResults.getWord() + "-" + wordResults.getPosTag();

                List<WordObjectResults> propertyObjects = new ArrayList<WordObjectResults>();
                WordObjectResults entityInfo = null;
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

    private LinkedHashMap<String, String> selectedWords(String selectWordDir, String classNameAndProperty, String txt) {
        LinkedHashMap<String, String> selectedWords = new LinkedHashMap<String, String>();
        Pair<Boolean, String> pair = FileFolderUtils.getSelectedFile(selectWordDir, classNameAndProperty, txt);
        if (pair.getValue0()) {
            String fileName = pair.getValue1();
            selectedWords = FileFolderUtils.getListString(selectWordDir + fileName);
        }
        return selectedWords;
    }

    private String shortForm(String url) {
        String objectUrl = "http://dbpedia.org/resource/";
        if (url.contains(objectUrl)) {
            return url.replace(objectUrl, "");
        }
        return url;
    }

    public static Boolean checkExistFile(String fileName) {
        return new File(fileName).exists();
    }

    public Map<String, List<WordObjectResults>> getWordEntities() {
        return wordObjectResults;
    }

    private boolean isValid(String objectOfProperty) {
        return FormatAndMatch.isValidForObject(objectOfProperty);
    }

   
    private Set<File> propertiyWiseFilter(List<File> files) throws IOException {
        Set<File> filterFiles = new TreeSet<File>();
        for (File file : files) {
            if (file.getName().contains("year") || file.getName().contains("Year")
                    || file.getName().contains("Date") || file.getName().contains("date")) {
                continue;
            }

            String property = getProperty(file.getName());
            /*if (property.contains("dbo:")) {
                continue;
            }*/

            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });

            if (dbpediaEntitys.size() < this.probabilityT.getLingPattern().getNumberOfEntitiesPerProperty()) {
                continue;
            }

            filterFiles.add(file);
        }
        return filterFiles;
    }

    private String getProperty(String fileName) {
        String property = fileName.replace(className, "");
        property = property.replace("_", "");
        property = property.replace(".json", "");
        property = property.strip().trim();
        return property;
    }

    private Set<String> filterObjects(Map<String, List<String>> entityCategories) {
        Set<String> filterObjects = new TreeSet<String>();
        for (String objectOfProperty : entityCategories.keySet()) {
            if (!this.isValid(objectOfProperty)) {
                continue;
            }
            if (entityCategories.get(objectOfProperty).size() < this.probabilityT.getNumberOfEntitiesForObject()) {
                continue;
            }
            filterObjects.add(objectOfProperty);
        }

        return filterObjects;
    }
    
   
    private List<File> selectedPropertyFilter(Set<File> filterFiles, String selectedPropFileName) throws IOException {
        Set<String> selectedProperties = FileFolderUtils.fileToSet(selectedPropFileName);
        List<File> selectedFiles = new ArrayList<File>();
        for (File file : filterFiles) {
            String tableName = file.getName();
            String property = Tables.getProperty(tableName);
            if (selectedProperties.contains(property)) {
                selectedFiles.add(file);
            } else {
                continue;
            }
        }
        return selectedFiles;
    }

    private Set<String> intersection(List<String> dbpediaEntitiesGroup, List<String> entities) {
        try {
            return Sets.intersection(new HashSet<String>(entities), new HashSet<String>(dbpediaEntitiesGroup));

        } catch (Exception ex) {
            return new HashSet<String>();
        }

    }

    private List<String> getAllEntities(List<DBpediaEntity> dbpediaEntitysT) {
        List<String> entities = new ArrayList<String>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntitysT) {
            entities.add(dbpediaEntity.getEntityIndex());
        }
        return entities;
    }

    private  List<String> getObjectCount(String propertyName, String objectOfProperty,List<DBpediaEntity> dbpediaEntities) {
        Double OBJECT_FOUND=0.0;
        List<String> objectDBpediaEntities=new ArrayList<String>();
         for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            String text = dbpediaEntity.getText();
            Boolean objectFlag = false, wordFlag = false;

            if (dbpediaEntity.getProperties().containsKey(propertyName)) {

                List<String> objects = dbpediaEntity.getProperties().get(propertyName);
                if (objects.contains(objectOfProperty)) {
                    OBJECT_FOUND++;
                    objectFlag = true;
                    objectDBpediaEntities.add(dbpediaEntity.getEntityIndex());
                }
            }
        }
         return objectDBpediaEntities;
    }
    
    /*private ResultTriple probObjectGivenWord(LinkedHashMap<String, List<String>> selectedWordEntities, List<String> dbpediaEntitiesGroup, List<String> allDbpediaEntities,  String objectOfProperty, String word) throws IOException {
        Double OBJECT_AND_WORD_FOUND = 0.0, OBJECT_FOUND = 0.0, WORD_FOUND = 0.0;
        Pair<String, Double> pair = null;
        ResultTriple triple = null;
        Integer transactionNumber = allDbpediaEntities.size();

        OBJECT_FOUND = Double.valueOf(dbpediaEntitiesGroup.size());
        List<String> wordEntities = selectedWordEntities.get(word);
        WORD_FOUND = Double.valueOf(wordEntities.size());
        Set<String> objectWordEntities = this.intersection(dbpediaEntitiesGroup, new ArrayList(wordEntities));
        OBJECT_AND_WORD_FOUND = Double.valueOf(objectWordEntities.size());
        System.out.println("Object"+"OBJECT_FOUND:" + OBJECT_FOUND+" "+"WORD_FOUND:" + WORD_FOUND+" "+"OBJECT_AND_WORD_FOUND:" + OBJECT_AND_WORD_FOUND);
        objectOfProperty = objectOfProperty.replaceAll("http://dbpedia.org/resource/", "");
        objectOfProperty = "object";
        String probability_object_word_str = "P(" + objectOfProperty + "|" + word + ")";

        //if (WORD_FOUND > 10) {
        Double probability_object_word = (OBJECT_AND_WORD_FOUND) / (WORD_FOUND);
        if (probability_object_word < this.probabilityT.getProbabiltyOfObjectGivenWordThresold()) {
            return null;
        }

        Double confidenceWord = (WORD_FOUND / transactionNumber);
        Double confidenceKB = (OBJECT_FOUND / transactionNumber);
        Double confidenceKB_WORD = (OBJECT_AND_WORD_FOUND / transactionNumber);
        Double lift = (confidenceKB_WORD / (confidenceWord * confidenceKB));
        triple = new ResultTriple(probability_object_word_str, probability_object_word, confidenceWord, confidenceKB, confidenceKB_WORD, lift, OBJECT_AND_WORD_FOUND, WORD_FOUND, null);

        return triple;

    }*/

}
