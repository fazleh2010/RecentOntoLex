/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.results.PropertyDictionary;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.wikipedia.calculation.InterestingPatterns;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.MenuOptions.FILE_ENTITY_NOTATION;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author elahi
 */
public class FileFolderUtils implements TextAnalyzer{

    private static String anchors = "src/main/resources/dbpedia/anchors/";
     private static String input = "input/";
    private static String achorFileTsv = "anchors_sorted_by_frequency.tsv";

   
  
    private String inputFile = allPoliticianFile;
  
    //private static Set<String> alphabetSets = Stream.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z").collect(Collectors.toCollection(HashSet::new));

    public static void main(String a[]) throws IOException {
        String fileName = anchors+input+achorFileTsv;
        //generate terms
        //getAlphabetic(fileName, alphabetSets);
      
        /* 
         StringTokenizer st;
        BufferedReader TSVFile = new BufferedReader(new FileReader(anchors + File.separator + achorFileTsv));
        String dataRow = TSVFile.readLine(); // Read first line.
         
         
         while (dataRow != null) {
            st = new StringTokenizer(dataRow, "\t");
            List<String> dataArray = new ArrayList<String>();
            while (st.hasMoreElements()) {
                dataArray.add(st.nextElement().toString());
            }
            for (String item : dataArray) {
                System.out.print("item:"+item + "  ");
            }
            System.out.println(); // Print the data line.
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
        // Close the file once all data has been read.
        TSVFile.close();

        // End the printout with a blank line.
        System.out.println();

        /*FileFolderUtils mfe = new FileFolderUtils();
        mfe.printFileList(anchors);*/
 /*String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>JSoup Example</title>"
                + "</head>"
                + "<body>"
                + "<table><tr><td>"
                + "<h1>HelloWorld</h1></tr>"
                + "</table>"
                + "</body>"
                + "</html>";*/
    }
    
    public static  Map<String, TreeMap<String, List<String>>> getAlphabetInfo(String anchors,String fileExtension) throws IOException, Exception {
        List<File> alphabetFiles = getFiles(anchors, fileExtension);
        Map<String, TreeMap<String, List<String>>> alphabetInfo = new TreeMap<String, TreeMap<String, List<String>>>();
        for (File file : alphabetFiles) {
            TreeMap<String, List<String>> alphabet = fileToHash(anchors + file.getName());
            String alphabetStr = file.getName().replaceAll(fileExtension, "");
            alphabetInfo.put(alphabetStr, alphabet);
        }
        return alphabetInfo;
    }

    public static void createDirectory(String location) throws IOException {
        Path location_path = Paths.get(location);
        /*if (Files.exists(location_path)) {
            Files.delete(location_path);
        }*/
        Files.createDirectories(location_path);

    }

    public static List<File> getFiles(String fileDir, String ntriple) throws Exception {
        System.out.println(fileDir);
        try {
            File dir = new File(fileDir);
            FileFilter fileFilter = new WildcardFileFilter("*" + ntriple);
            File[] files = dir.listFiles(fileFilter);
            return List.of(files);
        } catch (Exception exp) {
           throw new Exception("No file is found in the directory:"+fileDir);
        }

    }

    public static Pair<Boolean, List<File>> getSpecificFiles(String fileDir, String category, String extension) {
        List<File> selectedFiles = new ArrayList<File>();
        Pair<Boolean, List<File>> pair = new Pair<Boolean, List<File>>(Boolean.FALSE, new ArrayList<File>());
        try {
            String[] files = new File(fileDir).list();
            for (String fileName : files) {
                if (fileName.contains(category) && fileName.contains(extension)) {
                    selectedFiles.add(new File(fileDir + fileName));
                }
            }

        } catch (Exception exp) {
            System.out.println("file not found!!");
            return new Pair<Boolean, List<File>>(Boolean.FALSE, new ArrayList<File>());
        }

        return new Pair<Boolean, List<File>>(Boolean.TRUE, selectedFiles);
    }
    
     public static Pair<Boolean, List<File>> getSpecificFiles(String fileDir, String interestingness, String experiment, String extension) {
        List<File> selectedFiles = new ArrayList<File>();
        Pair<Boolean, List<File>> pair = new Pair<Boolean, List<File>>(Boolean.FALSE, new ArrayList<File>());
        try {
            String[] files = new File(fileDir).list();
            for (String fileName : files) {
                if (fileName.contains(interestingness) && fileName.contains(experiment) && fileName.contains(extension)) {
                    selectedFiles.add(new File(fileDir + fileName));
                }
            }

        } catch (Exception exp) {
            System.err.println("file not found!!"+exp.getMessage());
            return new Pair<Boolean, List<File>>(Boolean.FALSE, new ArrayList<File>());
        }

        return new Pair<Boolean, List<File>>(Boolean.TRUE, selectedFiles);
    }
    
    /*public static String getFile(String fileDir, String category, String extension) {
        String[] files = new File(fileDir).list();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(extension)) {
                return fileName;
            }
        }
        return null;
    }*/
    
    public static Pair<Boolean, String> getSelectedFile(String fileDir, String category, String extension) {
        Pair<Boolean, String> pair = new Pair<Boolean, String>(Boolean.FALSE, null);
        try {
            String[] files = new File(fileDir).list();
            for (String fileName : files) {
                if (fileName.contains(category) && fileName.contains(extension)) {
                    return new Pair<Boolean, String>(Boolean.TRUE, fileName);
                }
            }
        } catch (Exception exp) {
            System.out.println("file not found!!");
             return pair;
        }

        return pair;
    }

    public static List<String> getHash(String fileName) throws FileNotFoundException, IOException {
        List<String> lists = new ArrayList<String>();
        List<String> lines = new ArrayList<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();

                if (line != null) {
                    line = line.replace("http", "\nhttp");
                    lines = IOUtils.readLines(new StringReader(line));
                    for (String value : lines) {
                        //System.out.println("test:" + value);
                        lists.add(value);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lists;
    }
    
    public static TreeMap<String, List<String>> fileToHash(String fileName) throws FileNotFoundException, IOException {
        TreeMap<String, List<String>> hash = new TreeMap<String, List<String>>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        String key = info[0];
                        String value = info[1];
                        key = FormatAndMatch.format(key);
                        List<String> values = new ArrayList<String>();
                        if (hash.containsKey(key)) {
                            values = hash.get(key);
                        }
                        values.add(value);
                        hash.put(key, values);
                    }

                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }
    
     public static LinkedHashMap<String, String> fileToHashOrg(String fileName) throws FileNotFoundException, IOException {
        LinkedHashMap<String, String> hash = new LinkedHashMap<String, String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains(" ")) {
                        String[] info = line.split(" ");
                        String key = info[0];
                        String value = info[1];  
                        hash.put(key, value);
                    }

                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }
    
    public static Set<String> fileToSet(String fileName) throws FileNotFoundException, IOException {
        Set<String> set = new TreeSet<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    line = line.strip().trim();
                    set.add(line);
                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    public static List<String> getList(String fileName) throws FileNotFoundException, IOException {
        List<String> entities = new ArrayList<String>();

        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    String url = line.trim();
                    entities.add(url);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }
    
    public static LinkedHashMap<String,String> getListString(String fileName)  {
       LinkedHashMap<String,String> selectedWords= new LinkedHashMap<String,String>();

        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                //System.out.println(line);
                if (line != null) {
                    String url = line.trim();
                    String []info=url.split(" ");
                    selectedWords.put(info[1],info[2]);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("the file "+fileName+" does not exist"+e.getMessage());
            e.printStackTrace();
        }
        return selectedWords;
    }
    
    
     

    public static List<String> getSortedList(String fileName, Integer thresold, Integer listSize) throws FileNotFoundException, IOException {
        List<String> words = new ArrayList<String>();
        List<String> finalWords = new ArrayList<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains(" ")) {
                        //System.out.println(line);
                        String[] info = line.split(" ");
                        Integer count = Integer.parseInt(info[0].trim());
                        String word = info[1].trim();

                        if (count > thresold) {
                            //System.out.println(line);
                            words.add(word);
                        }
                    }

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer size = 0;
        for (String word : words) {

            if (size == listSize) {
                break;
            } else {
                finalWords.add(word);
            }
            size = size + 1;
        }
        System.out.println("finalWords:"+finalWords.size());
        System.out.println("finalWords:"+finalWords);

        return finalWords;
    }

    public static void listToFiles(List<String> list, String fileName) {
        String str = "";
        Integer number = -1, index = 0;
        for (String element : list) {
            if (element.contains("http")) {
                index++;
                String line = element + "\n";
                str += line;
                if (index == number) {
                    break;
                }
            }

        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            System.out.println(str);
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void stringToFiles(String str, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeToTextFile(String str, String filename) {
        if (str != null) {
            stringToFiles(str, filename);
        } else {
            return;
        }
    }

    private static Map<String, TreeMap<String, String>> getAlphabetic(String fileName, Set<String> alphabetSets) {
        Map<String, TreeMap<String, String>> alphabeticAnchors = new TreeMap<String, TreeMap<String, String>>();

        BufferedReader reader;
        String line = "";
        String firstLetter = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            Integer index=0;
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains("\t")) {
                        String[] info = line.split("\t");
                        String anchor = info[1];
                        anchor = anchor.replace("\"", "");
                     
                        String kb = info[0];
                        Character ch = null;
                        String str = null;
                        String alphabetFileName=null;
                        if (anchor.length() >= 1) {
                            ch = anchor.charAt(0);
                            str = String.valueOf(ch).toLowerCase().trim();
                            
                            /*if(!str.endsWith("a"))
                                continue;
                            */
                            if(alphabetSets.contains(str))
                               alphabetFileName=anchors+str+".txt";  
                            else
                              alphabetFileName=anchors+"other"+".txt";

                            index=index+1;
                            System.out.println(index+"line= "+line);
                             //anchor=anchor.toLowerCase().replaceAll(" ", "_").strip();
                             //kb=kb.strip();
                             anchor=anchor.stripLeading();
                             line=anchor+" = "+kb;
                             appendToFile(alphabetFileName,line);
                        }
                    }
                }
            }
            System.out.println("total= "+index);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alphabeticAnchors;
    }

   

    public static void writeToJsonFile(List<ObjectWordResults> entityResults, String entityDir, String tableName) throws IOException {
        String filename = entityDir + "result/" + tableName.replaceAll(".json", "_probability.json");
        if (entityResults.isEmpty()) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), entityResults);
    }
    
    public static void writeMeanResultsToJsonFile(MeanReciprocalCalculation results, String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), results);
    }
    
     public static void writeExperMeanResultsToJsonFile(Map<String,Map<String, MeanReciprocalCalculation>> expeResult, String filename) throws IOException, Exception {
        if(expeResult.isEmpty())
             throw new Exception("no data found to write in the file!!");
        
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), expeResult);
    }
     
    public static void writeMeanSortToJsonFile(List<MeanReciprocalCalculation> results, String filename) throws IOException, Exception {
        if (results.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }

        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), results);
    }

     
    
    public static void writeMeanResultsToJsonFile(Map<String,MeanReciprocalCalculation>meanReciprocals, String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), meanReciprocals);
    }
    
    

    public static void writeToJsonFile(List<Unit> units, String filename) throws IOException, Exception {
        if (units.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }
    
    public static void writeInterestingEntityToJsonFile( Map<String, List<String>> interestingEntitities, String filename) throws IOException, Exception {
        if (interestingEntitities.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), interestingEntitities);
    }
    public static void writeInterestingEntityEachToJsonFile(Map<String, List<String>> interestingEntitities, String location) throws IOException, Exception {
        if (interestingEntitities.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        location=location.replace(".json", "");
        String str="";
        FileFolderUtils.createDirectory(location);
        for (String word : interestingEntitities.keySet()) {
            String finalFileName=location+ word+".json";
            System.out.println("finalFileName:"+finalFileName);
            List<String> entityList=interestingEntitities.get(word);
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(Paths.get(finalFileName).toFile(), entityList);
            String line=word+" "+word+".json"+"\n";
            str+=line;
        }
         FileFolderUtils.stringToFiles(str, location+"AAClass.txt");

    }
    
     public static List<String>  readInterestingEntityEachToJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<String> entityList = mapper.readValue(file, new TypeReference<List<String>>() {
        });
        return entityList;
    }
     
      public static LinkedHashMap<String, List<String>>  readInterestingFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        LinkedHashMap<String, List<String>> entityList = mapper.readValue(file, new TypeReference<List<String>>() {
        });
        return entityList;
    }

    public static void writeDictionaryToJsonFile(Map<String, Map<Integer, String>> units, String fileName) throws Exception {
     if (units.isEmpty()) {
            throw new Exception("no data found to write in the json file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(fileName).toFile(), units);
    }

    public static List<File> mergeFiles(String inputDir, String dbo_Class,List<File> fileList) throws IOException, Exception {
        Map<String, List<File>> alphabetFiles = new TreeMap<String, List<File>>();
        List<File> mergeFiles = new ArrayList<File>();

        for (File file : fileList) {
            String[] info = file.getName().split("_");
            String alphabetFileName = inputDir + dbo_Class + "_" + info[1];
            List<File> list = new ArrayList<File>();
            if (alphabetFiles.containsKey(alphabetFileName)) {
                list = alphabetFiles.get(alphabetFileName);
            }
            list.add(file);
            alphabetFiles.put(alphabetFileName, list);
        }
        
     
        return joinFiles(alphabetFiles);
    }

    private static List<File> joinFiles(Map<String, List<File>> alphabetFiles) throws IOException, Exception {
        List<File> mergerFiles=new ArrayList<File>();
        for (String alphabetFileName : alphabetFiles.keySet()) {
            List<File> list = alphabetFiles.get(alphabetFileName);
            List<DBpediaEntity> allDbpediaEntitys = new ArrayList<DBpediaEntity>();
            for (File file : list) {
                List<DBpediaEntity> dbpediaEntitys = readFromJsonFile(file);
                allDbpediaEntitys.addAll(dbpediaEntitys);
            }
            writeDBpediaEntityToJsonFile(allDbpediaEntitys, alphabetFileName);
            mergerFiles.add(new File(alphabetFileName));
        }
        return mergerFiles;
    }
    
    public static List<DBpediaEntity> readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
        });
        return dbpediaEntitys;
    }
    
    
  
    public static Map<String, List<WordObjectResults>> readWordObjectFromJsonFile(File file)  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<WordObjectResults>> wordObjectResults=new TreeMap<String, List<WordObjectResults>>();
        try {
            wordObjectResults = mapper.readValue(file, new TypeReference<Map<String, List<WordObjectResults>>>() {
            });
            return wordObjectResults;
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("no files found to read data!!!");
        }
        return wordObjectResults;
    }

    public static void writeDBpediaEntityToJsonFile(List<DBpediaEntity> units, String filename) throws IOException, Exception {
        if (units.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }
    
    public static void deleteFiles(List<File> files) {
        for (File file : files) {
            if (file.delete()) {
                System.out.println("File deleted successfully");
            } else {
                System.out.println("Failed to delete the file");
            }
        }

    }

    public static void writeResultDetail(Map<String, List<WordObjectResults>> units, String filename) throws Exception {
        if (units.isEmpty()) {
            return;
            //throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }

    public static void writeEntityResults(Map<String, List<ObjectWordResults>> units, String filename) throws Exception {
         if (units.isEmpty()) {
             return;
            //throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }

    
    public void writeDictionaryToJsonFile(List<InterestingPatterns.Property> units, String filename) throws IOException, Exception {
        if (units.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }

   



    /*public static void writeToTextFile(List<EntityResults> entityResults, String entityDir, String tableName) throws IOException {
        String filename = entityDir + "result/" + tableName.replaceAll(".json", "_probability.txt");
        if (entityResults.isEmpty()) {
            return;
        }

        String str = "";
            
        for (EntityResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getKB() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String wordline = wordResults.getWord() + "  " + multiply + "  " + probabilty + "\n";
                wordSum += wordline;
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        stringToFiles(str, filename);

    }*/
    public static String urlUnicodeToString(String url) throws Exception {
        URI uri = new URI(url);
        String urlStr = uri.getQuery();
        return urlStr;
    }

    public static String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    public static String readHtmlFile() {

        return null;
    }

  
    public static void appendToFile(String fileName, String line) {
        File file = new File(fileName);
        boolean b;
        if (file.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.append(System.getProperty("line.separator"));
                bw.append(line);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stringToFiles(line, fileName);
        }
    }
    
    public static void convertToJson(List<DBpediaEntity> dbpediaEntities, String filename) throws Exception {
        if (dbpediaEntities.isEmpty()) {
            throw new Exception("the list is empty!!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            mapper.writeValue(Paths.get(filename + ".json").toFile(), dbpediaEntities);
        } catch (IOException ex) {
           System.out.println("can not write following file!!");
        }
    }

    public static void convertToJson(List<DBpediaEntityPattern> correctedEntities, String dir, String filename) throws IOException, Exception {
        if (correctedEntities.isEmpty()) {
            throw new Exception("the list is empty!!!");
        }
        filename = dir + filename;
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename + ".json").toFile(), correctedEntities);
    }
    
      public static String getClassDir(String dbo_Politician) {
        return dbo_Politician.split(":")[1];
    }
   
    public static String getLexiconFile(String directory, String type,String postag) {
        return directory + File.separator +type +"-"+ postag + "-" +   ONTO_LEX + ".json";
    }

    public static String getQaldFile(String directory,String type, String postag) {
        return directory + File.separator + postag + "-" + type + "-" + QLAD9 + ".json";
    }
    
    public static File getQaldJsonFile(String directory,String type, String postag) {
        return new File(directory + File.separator + postag + "-" + type + "-" + QLAD9 + ".json");
    }
    
    public static CsvFile getQaldCsvFile(String directory,String type, String postag) {
        return new CsvFile(directory + File.separator + postag + "-" + type + "-" + QLAD9 + ".csv");
    }

    public static String getMeanReciprocalFile(String directory, String type,String postag) {
        return directory + File.separator +type +"-"+ postag + "-" + MEAN_RECIPROCAL + ".json";
    }
    
     public static Map<String, Unit> getQald(File qaldFile) throws IOException {
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        ObjectMapper mapper = new ObjectMapper();
        List<Unit> units = mapper.readValue(qaldFile, new TypeReference<List<Unit>>() {
        });
        for (Unit unit : units) {
            qald.put(unit.getWord(), unit);
        }
        return qald;
    }

    

}
