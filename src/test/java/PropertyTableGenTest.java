
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.core.yaml.ParseYaml;
import citec.correlation.wikipedia.calculation.InterestedWords;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.calculation.WordCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.qald.Qald;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.element.DBpediaProperty;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.linking.EntityAnnotation;
import citec.correlation.wikipedia.linking.EntityLinker;
import citec.correlation.wikipedia.main.TableMain;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.anchors;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.output;
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
import java.io.File;
import java.io.FileNotFoundException;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class PropertyTableGenTest implements PropertyNotation, DirectoryLocation, MenuOptions {

    public static void main(String[] args) throws IOException, Exception {
        String dbo_ClassName = PropertyNotation.dbo_AAClass;
        String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
        String rawFiles = dbpediaDir + classDir + "rawFiles/";
        String outputDir = dbpediaDir + classDir + "object/";
        TableMain.generateClassPropertyTable(rawFiles, dbo_ClassName, outputDir);

    }



}
