
import citec.correlation.core.analyzer.TextAnalyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.ADJECTIVE;
import static citec.correlation.core.analyzer.TextAnalyzer.POSTAGS;
import citec.correlation.wikipedia.calculation.InterestedWords;
import citec.correlation.wikipedia.parameters.LingPattern;
import citec.correlation.wikipedia.parameters.Parameters;
import citec.correlation.wikipedia.parameters.ProbabilityT;
import citec.correlation.wikipedia.calculation.WordCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.main.TableMain;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
public class ObjectLexTest implements PropertyNotation, DirectoryLocation, MenuOptions, TextAnalyzer {

    private static String dbo_ClassName = PropertyNotation.dbo_AAClass;
    private static String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
    private static String rawFiles = dbpediaDir + classDir + "rawFiles/";
    private static String objectDir = dbpediaDir + classDir + "object/";
    private static String propertyDir = objectDir + "propertyTables/";
    private static String selectedWordDir = objectDir + SELTECTED_WORDS_DIR;
    private static String resultDir = objectDir + RESULT_DIR;
    private static String selectedPropertiesFile = objectDir + "SelectedProperties.txt";
    private static String proccessedPropertiesFile = objectDir + "ProcessSelectedProperties.txt";



    //with taking all properties (10823) it takes almost an hour to finish
    @Ignore
    public void A_PROPRTY_TABLE_GENERATION_TEST() throws IOException, Exception {
        TableMain.generateClassPropertyTable(rawFiles, dbo_ClassName, propertyDir);
    }
    //takes very long. minimum two hours. the properties needs to be filter before running it.
    @Test
    public void B_INTERESTING_WORD_TEST() throws IOException, Exception {
        Integer numEnPerProp = 200;
        Integer numSelectWordGen = 100;
        Integer numEnPerWord = 20;
        List<Integer> numClasses= Arrays.asList(10,50,100);
        List<Integer> numSelectWordGens= Arrays.asList(20,100,500);
 
        LingPattern lingPattern = new LingPattern(numEnPerProp, numSelectWordGen, numEnPerWord);
        Parameters paramteter = new Parameters(lingPattern);
        InterestedWords interestedWords = new InterestedWords(paramteter, propertyDir, dbo_ClassName, objectDir + SELTECTED_WORDS_DIR);
        System.out.println("find interesting words!!!");
    }

    @Ignore
    public void C_PROBABILTY_CALCULATION_TEST() throws IOException, Exception {
        Integer numberEnPerProp = 200;
        Integer numEnForObj = 100;
        Integer numTopLingPat = 500;
        Double probWordGivenObj = 0.01;
        Double probObjGivenWord = 0.01;
        Integer resultTopWord = 5;
        ProbabilityT probabilityT=new ProbabilityT (numberEnPerProp, numEnForObj, numTopLingPat,
                                                    probWordGivenObj, probObjGivenWord, resultTopWord);
          //      = new Parameters.ProbabilityThresold(numberEnPerProp, numEnForObj, numTopLingPat,
           //             probWordGivenObj, probObjGivenWord, resultTopWord);
        Parameters parameters=new Parameters(probabilityT);
        WordCalculation wordCalculation = new WordCalculation(parameters, propertyDir, dbo_ClassName, selectedWordDir, resultDir, selectedPropertiesFile, proccessedPropertiesFile);
        System.out.println("calculate probabilty ended!!!");
    }

    @Ignore
    public void LEXICON_CREATION_TEST() throws IOException, Exception {
        Lexicon lexicon = new Lexicon(qald9Dir);
        lexicon.prepareObjectLexicon(resultDir, new HashSet<String>(TextAnalyzer.POSTAGS));
        System.out.println("Lexicon Creation!!!");
    }

    @Ignore
    public void MEAN_RECIPROCAL_OBJECT_LEX_TEST() throws IOException, Exception {
        List<String> POSTAGS2 = new ArrayList<String>(Arrays.asList( NOUN));
        for (String postag : POSTAGS2) {
            String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, postag);
            String conditionalFilename = FileFolderUtils.getLexiconFile(qald9Dir, OBJECT, postag);
            String outputFileName = FileFolderUtils.getMeanReciprocalFile(qald9Dir, OBJECT, postag);
            Comparision comparision = new Comparision(qald9Dir, qaldFileName, conditionalFilename, outputFileName);
            comparision.compersionsPattern();
        }
    }
}
