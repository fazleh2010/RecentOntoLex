
import citec.correlation.core.analyzer.TextAnalyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.ADJECTIVE;
import static citec.correlation.core.analyzer.TextAnalyzer.NOUN;
import static citec.correlation.core.analyzer.TextAnalyzer.POSTAGS;
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

    private String dbo_ClassName = PropertyNotation.dbo_Politician;
    private String inputFile = allPoliticianFile;
    private String PATTERN = "pattern";
    private String QLAD9 = "qald9";
    private String ONTO_LEX = "lexicon";
    private String MEAN_RECIPROCAL = "meanReciprocal";

    @Ignore
    public void MEAN_RECIPROCAL_PATTERN_TEST() throws IOException, Exception {
        List<String> POSTAGS2 = new ArrayList<String>(Arrays.asList(ADJECTIVE));

        for (String postag : POSTAGS) {
            String qaldFileName = getQaldFile(qald9Dir, postag);
            String conditionalFilename = this.getLexiconFile(qald9Dir, postag);;
            String outputFileName =getMeanReciprocalFile(qald9Dir, postag);
            Comparision comparision = new Comparision(qald9Dir, qaldFileName, conditionalFilename, outputFileName);
            comparision.compersionsPattern();
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
