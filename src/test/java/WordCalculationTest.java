
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.calculation.InterestedWords;
import citec.correlation.wikipedia.calculation.WordCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.main.TableMain;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.parameters.WordThresold;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.IOException;
import java.util.HashSet;
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
public class WordCalculationTest implements PropertyNotation, DirectoryLocation, MenuOptions, WordThresold {

    private static String dbo_ClassName = PropertyNotation.dbo_AAClass;
    private static String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
    private static String rawFiles = dbpediaDir + classDir + "rawFiles/";
    private static String objectDir = dbpediaDir + classDir + "object/";
    private static String selectedWordDir = objectDir + SELTECTED_WORDS_DIR;
    private static String resultDir = objectDir + RESULT_DIR;

    @Ignore
    public void propertyTableGenerationTest() throws IOException, Exception {
        TableMain.generateClassPropertyTable(rawFiles, dbo_ClassName, objectDir);
    }

    @Ignore
    public void interrestingWordTest() throws IOException, Exception {
        InterestedWords interestedWords = new InterestedWords(objectDir, dbo_ClassName);
        System.out.println("find interesting words!!!");
    }

    @Test
    public void wordCalculationTest() throws IOException, Exception {
        WordCalculation wordCalculation = new WordCalculation(objectDir,dbo_ClassName, selectedWordDir,resultDir);
        System.out.println("calculate probabilty ended!!!");
    }

    @Ignore
    public void wordLexiconTest() throws IOException, Exception {
        Lexicon lexicon = new Lexicon(qald9Dir);
        lexicon.prepareObjectLexicon(resultDir, new HashSet<String>(TextAnalyzer.POSTAGS));
        System.out.println("Lexicon Creation!!!");
    }
}
