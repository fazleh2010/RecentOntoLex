/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import citec.correlation.wikipedia.main.CsvConstants;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class CsvFile implements CsvConstants {

    private String filename = null;
    public String[] qaldHeader =null;
    private Map<String, List<String[]>> wordRows = new TreeMap<String, List<String[]>>();
    
    private List<String[]> rows = new ArrayList<String[]>();

    public CsvFile(String filename) {
        this.filename = filename;
    }

    public CsvFile() {

    }
    

    public static List<String[]> createCsvQaldData(String[] qaldHeader, Map<String, Unit> qaldDic, String posTag) {
        List<String[]> csvData = new ArrayList<String[]>();
        csvData.add(qaldHeader);
        for (String word : qaldDic.keySet()) {
            Unit unit = qaldDic.get(word);
            String pos = posTag;
            String property = PROPERTY;
            Integer index = 0;
            for (String id : unit.getQuestions().keySet()) {
                String question = unit.getQuestions().get(id);
                String sparql = unit.getSparqls().get("Sparql_" + id);
                String sparqlID = "Sparql_" + id;
                String object = unit.getPairs().toString();
                if (index == 0) {
                    String[] record = {word, id, "  ", object, sparql, question};
                    csvData.add(record);
                } else {
                    String[] record = {"  ", id, "  ", object, sparql, question};
                    csvData.add(record);
                }

                index = index + 1;
            }
        }
        return csvData;
    }
    
    public void createCsvExperimentData(Map<String, Map<String, Map<String, MeanReciprocalCalculation>>> ruleExpeResult) {
        List<String[]> csvData = new ArrayList<String[]>();
        String[] header = new String[20];
        header[0] = EXPERIMENT;
        header[19] = EXPERIMENT_RESULT;
        Integer index = 1;
        Map<String, Integer> interestingnessIndexes = new HashMap<String, Integer>();
        for (String rule : interestingness) {
            for (String posTag : Analyzer.POSTAGS) {
                String key = rule + "-" + posTag;
                header[index] = rule + "-" + posTag;
                interestingnessIndexes.put(key, index);
                index = index + 1;
            }
        }
        csvData.add(header);
      
        Map<String, Map<String, String>> experimentPosResults = new TreeMap<String, Map<String, String>>();
        for (String rule : ruleExpeResult.keySet()) {
            Map<String, Map<String, MeanReciprocalCalculation>> ruleResult = ruleExpeResult.get(rule);
            for (String experiment : ruleResult.keySet()) {
                Map<String, MeanReciprocalCalculation> parts_of_speech = ruleResult.get(experiment);
                Map<String, String> posResults = new TreeMap<String, String>();
                for (String postag : parts_of_speech.keySet()) {
                    String mean = parts_of_speech.get(postag).getMeanReciprocalRankStr();
                    posResults.put(rule + "-" + postag, mean);
                }
                experimentPosResults.put(experiment, posResults);
                System.out.println("experiment:"+experiment);
                System.out.println("posResults:"+posResults);
            }
        }
        
            /*  for (String experiment : experimentPosResults.keySet()) {
            String[] record = new String[20];
            record[0] = experiment;
            Map<String, String> parts_of_speech = experimentPosResults.get(experiment);
            for (String element : parts_of_speech.keySet()) {
                String value = parts_of_speech.get(element);
                if (interestingnessIndexes.containsKey(element)) {
                    Integer elmentIndex = interestingnessIndexes.get(element);
                    record[elmentIndex] = value;
                }
            }
            csvData.add(record);
        }*/
       writeToCSV(csvData);
    }

    public void writeToCSV(List<String[]> csvData) {
        if (csvData.isEmpty()) {
            System.out.println("writing csv file failed!!!" );
            return;
        }
        try ( CSVWriter writer = new CSVWriter(new FileWriter(this.filename))) {
            writer.writeAll(csvData);
        } catch (IOException ex) {
            System.out.println("writing csv file failed!!!" + ex.getMessage());
        }
    }

    public void readCsv() throws IOException, CsvException {
        List<String[]> rows = new ArrayList<String[]>();
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        try ( CSVReader reader = new CSVReader(new FileReader(this.filename))) {
            rows = reader.readAll();
            Integer index = 0;
            for (String[] row : rows) {
                if (index == 0) {
                    this.qaldHeader = row;
                } else {
                    String word = row[0].trim().strip();
                    List<String[]> lines = new ArrayList<String[]>();
                    if (this.wordRows.containsKey(word)) {
                        lines = this.wordRows.get(row);
                    }
                    lines.add(row);
                    this.wordRows.put(word, lines);
                }
            }
        }
    }

    public List<String> getObjects(String word) {
        List<String> kbs = new ArrayList<String>();
        List<String[]> rows = wordRows.get(word);
        for (String[] row : rows) {
            kbs.add(row[objectIndex]);
        }
        return kbs;
    }

  

    public String getFilename() {
        return filename;
    }

    public String[] getQaldHeader() {
        return this.qaldHeader;
    }

    public Map<String, List<String[]>> getRow() {
        return wordRows;
    }

    private String[] experimentHeader() {
        String[] qaldHeader = new String[20];
        qaldHeader[0] = EXPERIMENT;
        qaldHeader[19] = EXPERIMENT_RESULT;
        Integer index = 1;
        Map<String, Integer> interestingnessIndexes = new HashMap<String, Integer>();
        for (String rule : interestingness) {
            for (String posTag : Analyzer.POSTAGS) {
                String key=rule + "-" + posTag;
                qaldHeader[index] = rule + "-" + posTag;
                interestingnessIndexes.put(key, index);
                index = index + 1;
            }
        }
        return qaldHeader;
    }

   

}
