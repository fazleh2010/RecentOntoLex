/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import citec.correlation.wikipedia.analyzer.Analyzer;
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.GOLD;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.qald.Qald;
import citec.correlation.wikipedia.dic.qald.Unit;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.element.DBpediaProperty;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import citec.correlation.wikipedia.linking.EntityAnnotation;
import citec.correlation.wikipedia.linking.EntityLinker;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.anchors;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.patternDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.trainingJson;
import citec.correlation.wikipedia.parameters.MenuOptions;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_PATTERN;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_WORD;
import static citec.correlation.wikipedia.parameters.MenuOptions.PATTERN_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.POSTAGS;
import static citec.correlation.wikipedia.parameters.MenuOptions.PROPERTY_GENERATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.QALD;
import static citec.correlation.wikipedia.parameters.MenuOptions.WORD_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE_PATTERNS;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.NLPTools;
import citec.correlation.wikipedia.utils.UrlUtils;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author elahi
 */
public class QaldProcess implements PropertyNotation, DirectoryLocation, MenuOptions {

    //Qald qaldMain = new Qald(POSTAGS, qald9Dir, trainingJson);
    private static Map<String, Unit> qaldDic = new TreeMap<String, Unit>();

    public static void main(String[] args) throws IOException, Exception {
        String directory = qald9Dir + OBJECT + "/";
        qald(directory);
    }

    private static void qald(String directory) throws IOException {

        for (String posTag : Analyzer.POSTAGS) {
            try {
                File qaldFile = FileFolderUtils.getQaldFileObject(qald9Dir + GOLD, OBJECT, posTag);
                qaldDic = FileFolderUtils.getQald(qaldFile);
                List<String[]> csvData = createCsvDataSimple();
                String fileName = qald9Dir + GOLD + "test.csv";
                writeToCSV(fileName, csvData);
                for (String key : qaldDic.keySet()) {
                    Unit unit = qaldDic.get(key);
                    System.out.println("key:" + key);
                    System.out.println("Unit:" + unit);
                }
            } catch (Exception exp) {
                System.out.println("File not found!!");
            }
        }
    }

    public static List<String[]> createCsvDataSimple() {
        String[] header = {"word", "pos", "property", "object"};
        String[] record1 = {"1", "first name", "address 1", "11111"};
        String[] record2 = {"2", "second name", "address 2", "22222"};

        List<String[]> list = new ArrayList<String[]>();
        list.add(header);
        list.add(record1);
        list.add(record2);

        return list;
    }

    public static void writeToCSV(String fileName, List<String[]> csvData) throws IOException {

        try ( CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(csvData);
        }
    }

}
