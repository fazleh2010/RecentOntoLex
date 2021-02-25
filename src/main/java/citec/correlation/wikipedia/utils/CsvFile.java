/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import antlr.ByteBuffer;
import citec.correlation.wikipedia.analyzer.Analyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.analyzer.logging.LogFilter;
import citec.correlation.wikipedia.analyzer.logging.LogFormatter;
import citec.correlation.wikipedia.analyzer.logging.LogHandler;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import citec.correlation.wikipedia.main.CsvConstants;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.experiments.ThresoldsExperiment;
import citec.correlation.wikipedia.main.Evaluation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.resourceDir;
import citec.correlation.wikipedia.results.LineInfo;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class CsvFile implements CsvConstants {

    private String filename = null;
    public String[] qaldHeader = null;
    private Map<String, List<String[]>> wordRows = new TreeMap<String, List<String[]>>();
    private Map<String, Integer> interestingnessIndexes = new HashMap<String, Integer>();

    private List<String[]> rows = new ArrayList<String[]>();
    private static Logger LOGGER = null;

    public CsvFile(String filename, Logger LOGGER) {
        this.filename = filename;
        this.LOGGER = LOGGER;
    }

    public CsvFile(String filename) {
        this.filename = filename;

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

    public void createCsvExperimentData(String type,Map<String, Map<String, Map<String, MeanReciprocalCalculation>>> ruleExpeResult) {

        ThresoldsExperiment thresoldsExperiment = new ThresoldsExperiment(type);
        List<String[]> csvData = new ArrayList<String[]>();
        Integer coulmnSize = (thresoldsExperiment.interestingness.size() * thresoldsExperiment.AllConfList.size() * POSTAGS.size()) + 1;
        csvData = this.setHeader(coulmnSize, thresoldsExperiment);

        //LOGGER.log(Level.INFO, "creating header of the file!!");

        Map<String, Map<String, String>> experimentPosResults = new TreeMap<String, Map<String, String>>();
        for (String rule : ruleExpeResult.keySet()) {

            Map<String, Map<String, MeanReciprocalCalculation>> ruleResult = ruleExpeResult.get(rule);
            for (String experiment : ruleResult.keySet()) {
                //LOGGER.log(Level.INFO,"experiment:"+experiment);
                Map<String, MeanReciprocalCalculation> parts_of_speech = ruleResult.get(experiment);
                experiment = getExperiment(experiment, rule);
                Map<String, String> posResults = new TreeMap<String, String>();
                for (String postag : parts_of_speech.keySet()) {
                     String mean = parts_of_speech.get(postag).getMeanReciprocalRankStr();
                    posResults.put(postag, mean);
                    //LOGGER.log(Level.INFO,"postag:"+postag+" mean:"+mean);
                }
                Map<String, String> temp = new HashMap<String, String>();
                if (experimentPosResults.containsKey(experiment)) {
                    temp = experimentPosResults.get(experiment);
                    temp.putAll(posResults);
                    experimentPosResults.put(experiment, temp);
                } else {
                    experimentPosResults.put(experiment, posResults);
                }
            }

        }

        for (String experiment : experimentPosResults.keySet()) {
            //System.out.println("experiment:" + experiment);
            String[] record = new String[coulmnSize];
            record[0] = experiment;
            //record[coulmnSize+1] = "result";
            Map<String, String> parts_of_speech = experimentPosResults.get(experiment);
            //System.out.println("parts_of_speech:" + parts_of_speech);
            for (String element : parts_of_speech.keySet()) {
                String value = parts_of_speech.get(element);
                if (interestingnessIndexes.containsKey(element)) {
                    Integer elmentIndex = interestingnessIndexes.get(element);
                    record[elmentIndex] = value;
                    //System.out.println("element:" + element + " value:" + value);

                }
            }
            csvData.add(record);
        }


        /*for (String experiment : experimentPosResults.keySet()) {
            String[] record = new String[coulmnSize];
            record[0] = experiment;
            record[coulmnSize] = "result";
            Map<String, String> parts_of_speech = experimentPosResults.get(experiment);
            System.out.println("parts_of_speech:"+parts_of_speech);
            for (String element : parts_of_speech.keySet()) {
                String value = parts_of_speech.get(element);
                System.out.println("element:"+element+" value:"+value);
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
            System.out.println("writing csv file failed!!!");
            return;
        }
        try ( CSVWriter writer = new CSVWriter(new FileWriter(this.filename))) {
            writer.writeAll(csvData);
        } catch (IOException ex) {
            System.out.println("writing csv file failed!!!" + ex.getMessage());
        }
    }

    public List<LineInfo> readPropertyCsv(File filename, String predict, String interestingness, PropertyCSV propertyCSV) throws FileNotFoundException, IOException, CsvException, Exception {
        List<String[]> rows = new ArrayList<String[]>();
        List<LineInfo> lineInfos = new ArrayList<LineInfo>();
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        Stack<String> stack = new Stack<String>();
        CSVReader reader = new CSVReader(new FileReader(filename));
        rows = reader.readAll();
        Integer index = 0;
        String lastWord = null;
        String word = null;

        for (String[] row : rows) {
            LineInfo lineInfo =null;
            if (index == 0) {
                //this.qaldHeader = row;
            } else {
                lineInfo = new LineInfo("className",index, row, predict, interestingness, propertyCSV, LOGGER);
                if (lineInfo.getValidFlag()) {
                    lineInfos.add(lineInfo);
                }
            }

            index = index + 1;
        }
        return lineInfos;

    }

    public void readQaldCsv(String predictFile) {
        List<String[]> rows = new ArrayList<String[]>();
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        Stack<String> stack = new Stack<String>();
        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader(filename));
            rows = reader.readAll();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.log(Level.SEVERE, "CSV File not found:!!!" + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.log(Level.SEVERE, "CSV File not found:!!!" + ex.getMessage());
        } catch (CsvException ex) {
            Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
            LOGGER.log(Level.SEVERE, "CSV problems:!!!" + ex.getMessage());
        }

        Integer index = 0;
        String lastWord = null;
        String word = null;
        for (String[] row : rows) {
            if (index == 0) {
                this.qaldHeader = row;
            } else {

                word = row[0].trim().strip();

                if (!word.isEmpty()) {
                    lastWord = word;
                } else {
                    word = lastWord;
                }

                List<String[]> lines = new ArrayList<String[]>();
                if (this.wordRows.containsKey(word)) {
                    lines = this.wordRows.get(word);
                }
                lines.add(row);
                this.wordRows.put(word, lines);

            }

            index = index + 1;
        }
    }

    public List<EvaluationTriple> getRowValues(String word,String predictionRule) {
        List<EvaluationTriple> triples = new ArrayList<EvaluationTriple>();
        List<String[]> rows = wordRows.get(word);
        for (String[] row : rows) {
            try {
                String id = row[CsvConstants.idIndex];
                String predicate = row[CsvConstants.propertyIndex];
                String object = row[CsvConstants.objectIndex];
                EvaluationTriple qaldTriple = new EvaluationTriple(QALD,predictionRule,id,null,predicate, object,word,LOGGER);
                triples.add(qaldTriple);
            } catch (Exception ex) {
                Logger.getLogger(CsvFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return triples;
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
                String key = rule + "-" + posTag;
                qaldHeader[index] = rule + "-" + posTag;
                interestingnessIndexes.put(key, index);
                index = index + 1;
            }
        }
        return qaldHeader;
    }

    public String getExperiment(String experiment, String interestingness) {
        String[] info = experiment.split("-");
        String str = null;
        str = experiment.replace(interestingness + "-", "");
        str = str.replace("-" + interestingness, ">");
        return str.substring(0, str.indexOf(">"));
    }

    public static String getInterestingnessThresold(String experiment, String interestingness) {
        String[] info = experiment.split("-");
        String str = null;
        for (Integer index = 0; info.length > index; index++) {
            str = info[index];
        }

        return str;
    }

    private List<String[]> setHeader(Integer coulmnSize, ThresoldsExperiment thresoldsExperiment) {
        List<String[]> csvData = new ArrayList<String[]>();
        String[] header = new String[coulmnSize];
        String[] firstRowShow = new String[coulmnSize];
        String[] secondRowShow = new String[coulmnSize];
        String[] thirdRowShow = new String[coulmnSize];
        header[0] = EXPERIMENT;
        firstRowShow[0] = "Interestingness";
        secondRowShow[0] = "Thresold";
        thirdRowShow[0] = "Parts-of-speech";
        Integer index = 1;

        for (String rule : interestingness) {
            List<Double> thresoldsRule = thresoldsExperiment.getInterestingness().get(rule);
            for (Double value : thresoldsRule) {
                for (String posTag : Analyzer.POSTAGS) {
                    String coulmnStr = rule + "_" + value.toString() + "-" + posTag;
                    header[index] = coulmnStr;
                    firstRowShow[index] = rule;
                    secondRowShow[index] = value.toString();
                    thirdRowShow[index] = posTag;
                    interestingnessIndexes.put(coulmnStr, index);
                    index = index + 1;
                }
            }
        }

        csvData.add(firstRowShow);
        csvData.add(secondRowShow);
        csvData.add(thirdRowShow);
        return csvData;
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, CsvException, Exception {
        /*String qaldFile = "";
        qaldFile = qald9Dir + GOLD + "NN-object-qald9.csv";
        CsvFile csvFile = new CsvFile(qaldFile);
        csvFile.readQaldCsv(qaldFile);*/
        
         String baseDir = "/home/elahi/dbpediaFiles/unlimited/unlimited/";


        LOGGER = Logger.getLogger(CsvFile.class.getName());
        LOGGER.setLevel(Level.FINE);
        LOGGER.setLevel(Level.SEVERE);
        LOGGER.setLevel(Level.CONFIG);
        LOGGER.setLevel(Level.FINE);
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.addHandler(new LogHandler());

        //LOGGER.log(Level.INFO, "generate experiments given thresolds");
        try {
            //Handler fileHandler = new FileHandler(resourceDir + "logger.log", 2000, 1000);
            Handler fileHandler = new FileHandler(resourceDir + "logger.log");
            fileHandler.setFormatter(new LogFormatter());
            fileHandler.setFilter(new LogFilter());
            LOGGER.addHandler(fileHandler);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        Set<String> classNames = new HashSet<String>();
        classNames.add("Book");
        
        Set<String> predictLinguisticLocal=new HashSet<String>();
        predictLinguisticLocal.add(predict_localized_l_for_s_given_p);
        
    
      
        for (String prediction : predictLinguisticLocal) {
            if (!prediction.contains(predict_localized_l_for_s_given_p)) {
                continue;
            }
            ///for (String className : classNames) {
                String rawDir = "raw";
                //String predictFile = qald9Dir + "/" + prediction + "/" + rawDir + "/" + "rules-" + className + "-predict_l_for_s_given_p-100-10000-4-5-5-5-5.csv";
                String predictDir = baseDir + "/" +predict_localized_l_for_s_given_p + "/";
                List<File> files = FileFolderUtils.getFiles(predictDir, ".csv");
               for (File file : files) {
                PropertyCSV propertyCSV = null;
                if (file.getName().contains(PropertyCSV.localized)) {
                    propertyCSV = new PropertyCSV(PropertyCSV.localized);
                    CsvFile csvFile = new CsvFile(predictDir + file.getName(), LOGGER);
                    List<LineInfo> lineInfos=csvFile.readPropertyCsv(file, prediction,Cosine,propertyCSV);
                } else {

                }

                break;
            }
                /*CsvFile csvFile = new CsvFile(predictFile,LOGGER);
                File tmpDir = new File(predictFile);
                System.out.println(predictFile);
                boolean exists = tmpDir.exists();
                System.out.println(exists);
                csvFile.readPropertyCsv(predictFile, prediction);*/
            //}

        }
        
        

    }

   

}
