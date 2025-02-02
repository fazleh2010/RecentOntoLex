
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.qald.Qald;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.patternDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.trainingJson;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_PATTERN;
import static citec.correlation.wikipedia.parameters.MenuOptions.MEAN_RECIPROCAL_WORD;
import static citec.correlation.wikipedia.parameters.MenuOptions.PATTERN_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.PROPERTY_GENERATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.QALD;
import static citec.correlation.wikipedia.parameters.MenuOptions.WORD_CALCULATION;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE;
import static citec.correlation.wikipedia.parameters.MenuOptions.WRITE_PATTERNS;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.IOException;
import org.junit.Test;
import static citec.correlation.wikipedia.parameters.MenuOptions.POSTAGS;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elahi
 */
public class ComparisionTest {

    private String qaldFileName = qald9Dir + "VB-qald9-pattern" + ".json";
    private String conditionalFilename = qald9Dir + "lexicon-conditional-pattern" + ".json";
    private String outputFileName = qald9Dir + "meanReciprocal" + ".json";

    public void main() throws IOException, Exception {
        Set<String> POSTAGS = new HashSet<String>(Arrays.asList(TextAnalyzer.NOUN));
        for (String postag : POSTAGS) {
            File qaldFileName = new File (qald9Dir + postag + "-pattern-qald9" + ".json");
            File conditionalFilename = new File (qald9Dir + "lexicon-conditional-pattern" + ".json");
            File outputFileName = new File (qald9Dir + postag + "-pattern-mean-reciprocal" + ".json");
            String experiment="experiment";
            //Comparision comparision = new Comparision("VB",qald9Dir, qaldFileName, conditionalFilename, outputFileName);
            //comparision.compersionsPattern(experiment);
        }
    }

}
