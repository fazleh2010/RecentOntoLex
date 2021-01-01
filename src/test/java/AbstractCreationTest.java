
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.qald.Qald;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.main.TableMain;
import static citec.correlation.wikipedia.main.TableMain.makeClassDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.input;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.patternDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.trainingJson;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_PATTERN;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_WORD;
import static citec.correlation.wikipedia.parameters.MenuOptions.PATTERN_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.POSTAGS;
import static citec.correlation.wikipedia.parameters.MenuOptions.PROPERTY_GENERATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.QALD;
import static citec.correlation.wikipedia.parameters.MenuOptions.WORD_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE_PATTERNS;
import static citec.correlation.wikipedia.parameters.WordThresold.TopNwords;
import static citec.correlation.wikipedia.parameters.WordThresold.numberOfEntitiesrmSelected;
import static citec.correlation.wikipedia.parameters.WordThresold.objectGivenWordThres;
import static citec.correlation.wikipedia.parameters.WordThresold.topWordLimitToConsiderThres;
import static citec.correlation.wikipedia.parameters.WordThresold.wordFoundInNumberOfEntities;
import static citec.correlation.wikipedia.parameters.WordThresold.wordGivenObjectThres;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static citec.correlation.wikipedia.parameters.WordThresold.objectMinimumEntities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class AbstractCreationTest {

    private String dbpediaDir = "src/main/resources/dbpedia/";
    private DbpediaClass dbpediaClass = null;


    public static void main(String[] args) throws Exception {
        String dbpediaDir = "src/main/resources/dbpedia/";
        String fileType = DbpediaClass.ALL;
        LinkedHashMap<String,String> classFileName = new LinkedHashMap<String,String>();
        //classFileName.put(PropertyNotation.dbo_Album,"albums.txt");
        //classFileName.put(PropertyNotation.dbo_Film,"flims.txt");
        //classFileName.put(PropertyNotation.dbo_TelevisionShow,"televisionShow.txt");
        classFileName.put(PropertyNotation.dbo_alliance, "alliance.txt");
        classFileName.put(PropertyNotation.dbo_Company, "Company.txt");
        classFileName.put(PropertyNotation.dbo_Writer, "Writer.txt");
        classFileName.put(PropertyNotation.dbo_battle, "battle.txt");
        classFileName.put(PropertyNotation.dbo_Colour, "Colour.txt");
        classFileName.put(PropertyNotation.dbo_Color, "Color.txt");
        classFileName.put(PropertyNotation.dbo_Currency, "Currency.txt");
        classFileName.put(PropertyNotation.dbo_state, "state.txt");
          classFileName.put(PropertyNotation.dbo_Location, "Location.txt");

  

         //classFileName.put(PropertyNotation.dbo_Person,"persons.txt");


        for (String dbo_ClassName : classFileName.keySet()) {
            if (fileType.contains(DbpediaClass.ALL)) {
                String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
                String rawFiles = dbpediaDir + classDir + "rawFiles/";
                String inputFile = dbpediaDir + classDir +classFileName.get(dbo_ClassName);
                DbpediaClass dbpediaClass = new DbpediaClass(dbo_ClassName, inputFile, TextAnalyzer.POS_TAGGER_WORDS, fileType);
                makeClassDir(dbpediaDir + classDir);
                write(inputFile, rawFiles, dbpediaClass, dbpediaClass.getPropertyEntities());
            }
        }

    }

    private static void write(String inputJsonFile, String outputDir, DbpediaClass dbpediaClass, Map<String, LinkedHashSet<String>> propertyEntities) throws Exception {
        Tables tables = new Tables( outputDir);
        tables.writingTable(dbpediaClass, propertyEntities);
    }

}
