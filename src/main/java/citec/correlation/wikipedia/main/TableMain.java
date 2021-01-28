/*
 * To change this license header, choose License Headers in Project PropertyConst.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.parameters.DirectoryLocation;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.calculation.WordCalculation;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.element.DBpediaProperty;
import citec.correlation.wikipedia.calculation.InterestedWords;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.dic.qald.Qald;
import citec.correlation.wikipedia.linking.EntityAnnotation;
import citec.correlation.wikipedia.linking.EntityLinker;
import citec.correlation.wikipedia.utils.NLPTools;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.table.EntityTable;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.UrlUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author elahi
 */
public class TableMain implements PropertyNotation,DirectoryLocation,MenuOptions {
    //Horrible codes..just a play ground for all kinds of work
    private static Set<String> freqClasses = new HashSet<String>();    
    private static Map<String, TreeMap<String, List<String>>> alphabetInfo = new TreeMap<String, TreeMap<String, List<String>>>();

    public static void main(String[] args) throws IOException, Exception {
        String type = PROPERTY_GENERATION;
        String dbo_ClassName = PropertyNotation.dbo_AAClass;
        freqClasses.add(dbo_ClassName);
        String inputFile = allPoliticianFile;
        String fileType = DbpediaClass.ALL;
        String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
        String rawFiles = dbpediaDir + classDir + "rawFiles/";

     
        if (type.equals(WRITE)) {
            DbpediaClass dbpediaClass = new DbpediaClass(dbo_ClassName, inputFile, TextAnalyzer.POS_TAGGER_WORDS, fileType);
            makeClassDir(dbpediaDir + classDir);

            /*if (fileType.contains(DbpediaClass.FREQUENT_TRIPLE)) {
                trainingTable.write(inputFile, rawFiles, dbpediaClass, checkProperties);
            } else*/
            if (fileType.contains(DbpediaClass.ALL)) {
                write(inputFile, rawFiles, dbpediaClass, dbpediaClass.getPropertyEntities());
            }
        }
        if (type.equals(PROPERTY_GENERATION)) {
            String outputDir=dbpediaDir + classDir + "object/";
            generateClassPropertyTable(rawFiles, dbo_ClassName, outputDir);
        }
        if (type.equals(WORD_CALCULATION )) {
            //currently not working (out of memory)
            //calculation(inputFile, classDir, dbo_ClassName);
        }

        if (type.equals(QALD)) {
            Qald qaldMain = new Qald(POSTAGS, qald9Dir, trainingJson);

        }
        if (type.equals(MEAN_RECIPROCAL_WORD)) {
           String qaldFileName = qald9Dir + "JJ-qald9" + ".json";
           String conditionalFilename = qald9Dir + "lexicon-conditional-JJ" + ".json";
           Comparision comparision = new Comparision("JJ",qald9Dir,qaldFileName, conditionalFilename,null);
           //comparision.comparisionsWords();
        }
        if (type.equals(MEAN_RECIPROCAL_PATTERN)) {
           String qaldFileName = qald9Dir + "VB-qald9-pattern" + ".json";
           String conditionalFilename = qald9Dir + "lexicon-conditional-pattern" + ".json";
           String outputFileName= qald9Dir +"meanReciprocal" + ".json";
           Comparision comparision = new Comparision("VB",qald9Dir,qaldFileName,conditionalFilename,outputFileName);
           String experiment="experiment";
           comparision.compersionsPattern(experiment);
           //FileFolderUtils.writeMeanResultsToJsonFile(comparision.getResults(), outputFileName);
        }
        if (type.equals(WRITE_PATTERNS)) {
            addPatterns(inputFile, rawFiles, dbo_ClassName, classDir);

        }
        if (type.equals(PATTERN_CALCULATION)) {
            String inputDir = dbpediaDir + classDir + patternDir;
            String PATTERN = "pattern";
            PatternCalculation patternCalculation = new PatternCalculation(inputDir, inputFile, dbo_ClassName);
            Lexicon lexicon = new Lexicon(qald9Dir);
            String fileName = qald9Dir + File.separator + "lexicon-" + PATTERN + "-" + "JJ" + ".json";
            String lexiconDir=qald9Dir;
            lexicon.preparePropertyLexicon(lexiconDir,patternCalculation.getPatternEntities(), PATTERN, "JJ", fileName);
        }

    }

   
   
    /*public static void objectLexicon(String tableDir, String classDir, String dbo_ClassName,String selectedWordDir,String resultDir) throws IOException, Exception {
        Lexicon lexicon=new Lexicon(qald9Dir);
        lexicon.prepareObjectLexicon(resultDir,new HashSet<String>(TextAnalyzer.POSTAGS));
        System.out.println("Lexicon Creation!!!");
    }*/

    public static void generateClassPropertyTable(String rawFiles, String dbo_ClassName,String outputDir) throws Exception {
        Tables tables= new Tables();
        tables.readSplitTables(rawFiles,dbo_ClassName);
        tables.writeTable(outputDir,dbo_ClassName);
    }

    private static void addPatterns(String inputFile, String rawFiles, String dbo_ClassName, String classDir) throws Exception {
        alphabetInfo = FileFolderUtils.getAlphabetInfo(anchors, ".txt");
        Integer windowSize = 5, nGram = 3;
        //List<Integer> slots = Arrays.asList(2,10, 200,400,600,800,1000,1200,1400,1600,1624,1625);
        //Map<Integer, String> tripples = new TreeMap<Integer, String>();
        Tables tables = new Tables( rawFiles);
        Map<String, List<DBpediaEntity>> fileDBpediaEntities = tables.readAlphabetSplitTables(rawFiles, dbo_ClassName);

        for (String fileName : fileDBpediaEntities.keySet()) {
            List<DBpediaEntityPattern> correctedEntities = new ArrayList<DBpediaEntityPattern>();
            List<DBpediaEntity> DBpediaEntitys = fileDBpediaEntities.get(fileName);
            Integer total=DBpediaEntitys.size();
            Integer count=0;
            for (DBpediaEntity dbpediaEntity : DBpediaEntitys) {
                String subject = UrlUtils.getLastPartOfUrl(dbpediaEntity.getEntityUrl());
                System.out.println();
                System.out.println(subject+" count="+count+" total="+total+" "+fileName+"........................");
                System.out.println(dbpediaEntity.getEntityUrl());

                String text = dbpediaEntity.getText();
                List<String> sentences = NLPTools.getSentencesFromText(text);
                Set<String> propertyValues = new HashSet<String>();

                Map<Integer, String> tripples = new TreeMap<Integer, String>();
                Integer index = 0;
                for (String key : dbpediaEntity.getProperties().keySet()) {
                    if (DBpediaProperty.isExcludedProperty(key)) {
                        continue;
                    }

                    List<String> properties = dbpediaEntity.getProperties().get(key);

                    for (String property : properties) {
                        index = index + 1;
                        String kb = UrlUtils.getLastPartOfUrl(property);
                        propertyValues.add(kb);
                        //System.out.println("propertyValue:"+kb);
                        String tripple = "s(" + subject + ")" + " " + key + " o'<" + kb + ">";
                        tripples.put(index, tripple);
                    }
                }

                Map<Integer, String> annotatedEntities = new TreeMap<Integer, String>();
                Map<Integer, String> patterns = new TreeMap<Integer, String>();
                EntityLinker linking = new EntityLinker(subject, sentences, windowSize, nGram, alphabetInfo, propertyValues);
                for (EntityAnnotation entityAnnotation : linking.getAnnotatedSentences()) {
                    annotatedEntities.put(entityAnnotation.getSentenceNumber(), entityAnnotation.getAnnotatedSentence());
                    patterns.put(entityAnnotation.getSentenceNumber(), entityAnnotation.getPatterns());
                }
                DBpediaEntityPattern DBpediaEntityPattern = new DBpediaEntityPattern(dbpediaEntity, tripples, patterns, annotatedEntities);
                correctedEntities.add(DBpediaEntityPattern);
               // if(slots.contains(count)){
                  //FileFolderUtils.convertToJson(correctedEntities, dbpediaDir + classDir+"pattern/", fileName.replace(".json", "")+"_"+count);  
                  //break;
                //}
                count=count+1;
                
                
            }
            if (!correctedEntities.isEmpty()) {
                FileFolderUtils.convertToJson(correctedEntities, dbpediaDir + classDir+patternDir, fileName.replace(".json", "")+"_pattern");
            }
            
        }

    }
    
    private void createTable(Map<String, List<DBpediaEntity>> propertyEntities) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void display(List<DBpediaEntity> dbpediaEntities) {
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            System.out.println(dbpediaEntity);
        }
    }

  
    private static  void write(String inputJsonFile, String outputDir, DbpediaClass dbpediaClass, Map<String, LinkedHashSet<String>> propertyEntities) {
        Tables tables = new Tables( outputDir);
        try {
            tables.writingTable(dbpediaClass, propertyEntities);
        } catch (Exception ex) {
            Logger.getLogger(TableMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void write(String inputJsonFile, String outputDir, DbpediaClass dbpediaClass, Set<String> checkProperties) {
        Tables tables = new Tables( outputDir);
        try {
            tables.writingTable(dbpediaClass, checkProperties);
        } catch (Exception ex) {
            Logger.getLogger(TableMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void moveDirectory(String source, String destination) throws IOException {
        FileUtils.deleteDirectory(new File(destination));
        FileUtils.moveDirectory(new File(source), new File(destination));
    }

    public static Boolean makeClassDir(String director) {
        try {
            Path path = Paths.get(director);
            Files.createDirectories(path);
            path = Paths.get(director + "rawFiles/");
            Files.createDirectories(path);
            path = Paths.get(director + "tables/" + "result/");
            Files.createDirectories(path);
            path = Paths.get(director + "tables/" + "selectedWords/");
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to create directory!" + e.getMessage());
            return false;

        }
    }

}
