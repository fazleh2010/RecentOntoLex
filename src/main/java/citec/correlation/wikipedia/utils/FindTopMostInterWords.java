/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.calculation.WordCalculation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class FindTopMostInterWords {

    public static List<String> findTopMostWords(String selectWordDir, String classNameAndProperty, Integer numberOfTopLinguisticPattern) {

        LinkedHashMap<String, String> selectedWordsHash = selectedWords(selectWordDir, classNameAndProperty, ".txt");

        List<String> selectedWords = new ArrayList<String>(selectedWordsHash.keySet());
        if (numberOfTopLinguisticPattern > -1 && !selectedWords.isEmpty()) {
            if (selectedWords.size() > numberOfTopLinguisticPattern) {
                selectedWords = selectedWords.subList(0, numberOfTopLinguisticPattern);
            }
        }
        return selectedWords;
    }

    private static LinkedHashMap<String, String> selectedWords(String selectWordDir, String classNameAndProperty, String txt) {
        LinkedHashMap<String, String> selectedWords = new LinkedHashMap<String, String>();
        Pair<Boolean, String> pair = FileFolderUtils.getSelectedFile(selectWordDir, classNameAndProperty, txt);
        if (pair.getValue0()) {
            String fileName = pair.getValue1();
            selectedWords = FileFolderUtils.getListString(selectWordDir + fileName);
        }
        return selectedWords;
    }
    
    private static LinkedHashMap<String, File> getEntities(String selectWordDir) throws Exception {
        LinkedHashMap<String,File> wordHash = new LinkedHashMap<String,File>();
        List<File> wordFiles = FileFolderUtils.getFiles(selectWordDir, ".json");
        Integer index=0;
        for (File file : wordFiles) {
            String word = file.getName().replace(".json", "");
            word =format(word);
            wordHash.put(word, file);
             List<String> entities=FileFolderUtils.readInterestingEntityEachToJsonFile(file);
              System.out.println(file.getName()+" size:"+entities.size());
             index=index+1;
        }
        return wordHash;
    }
     
    public static void main(String[] args) throws Exception {
        String selectWordDir = "src/main/resources/dbpedia/AAClass/object/selectedWords_Cl5_Prop100_Lp20/";
        selectWordDir = selectWordDir + "dbo:AAClass_dbo:party";
        // "academy"
        LinkedHashMap<String, File> wordHash = getEntities(selectWordDir);
        if (wordHash.containsKey("member")) {
            File file = wordHash.get("member");
            List<String> entities=FileFolderUtils.readInterestingEntityEachToJsonFile(file);
            System.out.println(file.getName()+" size:"+entities.size());
        }
    }
     
     /*Set<String> list = new HashSet<String>();
        String summaryFile = selectWordDir + tableName + "/" + "AAClass.txt";
        try {
            LinkedHashMap<String, String> wordFileNames = FileFolderUtils.fileToHashOrg(summaryFile);
             String fileName =null;
            for (String word2 : wordFileNames.keySet()) {
                fileName = wordFileNames.get(word2);
                fileName = selectWordDir + fileName;
              
                if(word2.contains(word)){
                      System.out.println(word2 + " " + fileName);
                    break;
                }
            }
            List<String> entities=FileFolderUtils.readInterestingEntityEachToJsonFile(new File(fileName));
            System.out.println(entities);
        } catch (IOException ex) {
            Logger.getLogger(WordCalculation.class.getName()).log(Level.SEVERE, null, ex);
        }*/

    private static String format(String word) {
          word = word.trim().strip();
          return word;
    }

}
