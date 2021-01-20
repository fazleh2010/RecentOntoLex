/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

}
