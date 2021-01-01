
import citec.correlation.core.analyzer.TextAnalyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.ADJECTIVE;
import static citec.correlation.core.analyzer.TextAnalyzer.NOUN;
import static citec.correlation.core.analyzer.TextAnalyzer.VERB;
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
public class PatternCalculationTest {

    private String dbo_ClassName = PropertyNotation.dbo_Politician;
    private String inputFile = allPoliticianFile;
    private String PATTERN = "pattern";
    private String QLAD9 = "qald9";
    private String ONTO_LEX = "lexicon";
    private String MEAN_RECIPROCAL = "meanReciprocal";

    @Test
    public void PATTERN_CALCULATION_TEST() throws IOException, Exception {
        String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
        String inputDir = dbpediaDir + classDir + patternDir;
        System.out.println(inputDir);
        PatternCalculation patternCalculation = new PatternCalculation(inputDir, inputFile, dbo_ClassName);
        Lexicon lexicon = new Lexicon(qald9Dir);
        for (String postag : TextAnalyzer.POSTAGS) {
            String fileName = this.getLexiconFile(qald9Dir, postag);
            lexicon.prepareLexiconForPattern(patternCalculation.getPatternEntities(), postag, PATTERN, fileName);
        }
    }

    private String getLexiconFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + ONTO_LEX + ".json";
    }

    private String getQaldFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + QLAD9 + ".json";
    }

    private String getMeanReciprocalFile(String qald9Dir, String postag) {
        return qald9Dir + File.separator + postag + "-" + PATTERN + "-" + MEAN_RECIPROCAL + ".json";
    }

}
