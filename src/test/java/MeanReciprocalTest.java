
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.ADJECTIVE;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.GOLD;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.NOUN;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.POSTAGS;
import citec.correlation.wikipedia.calculation.PatternCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.allPoliticianFile;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.patternDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class MeanReciprocalTest {

    private static String dbo_ClassName = PropertyNotation.dbo_Politician;
    private static String inputFile = allPoliticianFile;
    private static String PATTERN = "pattern";
    private static String QLAD9 = "qald9";
    private static String ONTO_LEX = "lexicon";
    private static String MEAN_RECIPROCAL = "meanReciprocal";

    public static void main(String []args)  throws IOException, Exception {
        List<String> POSTAGS2 = new ArrayList<String>(Arrays.asList(TextAnalyzer.ADJECTIVE));

        for (String postag : POSTAGS) {
            String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, PATTERN, postag);
            String conditionalFilename = FileFolderUtils.getLexiconFile(qald9Dir, PATTERN, postag);
            String outputFileName = FileFolderUtils.getMeanReciprocalFile(qald9Dir, PATTERN, postag);
            Comparision comparision = new Comparision(qald9Dir, qaldFileName, conditionalFilename, outputFileName);
            comparision.compersionsPattern();
        }
    }

    /*private String getLexiconFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + ONTO_LEX + ".json";
    }

    private String getQaldFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + QLAD9 + ".json";
    }

    private String getMeanReciprocalFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + MEAN_RECIPROCAL + ".json";
    }*/

}
