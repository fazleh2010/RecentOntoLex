/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.dic.qald.Unit;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elahi
 */
public class CsvUtils {
     private static String WORD = "word";
    private static String POS = "pos";
    private static String ID = "id";
    private static String PROPERTY = "property";
    private static String OBJECT = "object";

    public static List<String[]> createCsvDataSimple(String[] qaldHeader, Map<String, Unit> qaldDic, String posTag) {
        List<String[]> list = new ArrayList<String[]>();
        list.add(qaldHeader);
        for (String word : qaldDic.keySet()) {
            Unit unit = qaldDic.get(word);
            System.out.println("key:" + word);
            System.out.println("Unit:" + unit);
            String pos = posTag;
            String property = PROPERTY;
            Integer index = 0;
            for (String key : unit.getQuestions().keySet()) {
                String question = unit.getQuestions().get(key);
                String sparql = unit.getSparqls().get("Sparql_" + key);
                String sparqlID = "Sparql_" + key;
                String object =unit.getPairs().toString();
                if (index == 0) {
                    String[] record = {word, key, "  ", object, sparql, question};
                    list.add(record);
                } else {
                    String[] record = {"  ", key, "  ", object, sparql, question};
                    list.add(record);
                }

                index = index + 1;
            }
        }
        return list;
    }

    public static void writeToCSV(String fileName, List<String[]> csvData) throws IOException {

        try ( CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(csvData);
        }
    }

}
