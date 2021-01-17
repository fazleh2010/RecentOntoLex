
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
import citec.correlation.wikipedia.parameters.CommonParameter;
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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
    public void PARAMETER_WISE_INTERESTING_() throws IOException, Exception {
        Map<Integer, TreeSet<String>> classInformations = new TreeMap<Integer, TreeSet<String>>();

        List<Integer> numClasses = Arrays.asList(5, 10, 15);
        List<Integer> numEnPerProps = Arrays.asList(20, 100,200);
        List<Integer> numEnPerLps = Arrays.asList(20, 100, 500);
        for (Integer i = 0; i < numClasses.size(); i++) {
            Integer numOfClass = numClasses.get(i);
            Integer numEnPerProp = numEnPerProps.get(i);
            for (Integer j = 0; j < numEnPerLps.size(); j++) {
                Integer numEnPerLp = numEnPerLps.get(j);
                LingPattern lingPattern = new LingPattern(true, numOfClass, numEnPerProp, numEnPerLp);
                Parameters paramteter = new Parameters(lingPattern);
                String outputFolderDir = objectDir + SELTECTED_WORDS_DIR+"_"+"Cl"+numOfClass+"_"+"Prop"+numEnPerProp+"_"+"Lp"+numEnPerLp+"/";
                System.out.println("outputFolderDir:"+outputFolderDir);
                FileFolderUtils.createDirectory(outputFolderDir);
                InterestedWords interestedWords = new InterestedWords(paramteter, propertyDir, dbo_ClassName, outputFolderDir,-1);
            }
        }

        System.out.println("find interesting words!!!");
    }

    @Ignore
    public void C_PROBABILTY_CALCULATION_TEST() throws IOException, Exception {
         Integer numberOfClasses = 20;
        Integer numberEnPerProp = 200;
        Integer numEnForObj = 100;
        Integer numTopLingPat = 500;
        Double probWordGivenObj = 0.01;
        Double probObjGivenWord = 0.01;
        Integer resultTopWord = 5;
        List<Integer> numSelectWordGens = Arrays.asList(50, 100, 500);
         Integer numSelectWordGen = numSelectWordGens.get(0);

        //ProbabilityT probabilityT=new ProbabilityT (numberOfClasses,numberEnPerProp,numEnForObj,  numSelectWordGen,numTopLingPat,
        //                                            probWordGivenObj, probObjGivenWord, resultTopWord);
          //      = new Parameters.ProbabilityThresold(numberEnPerProp, numEnForObj, numTopLingPat,
           //             probWordGivenObj, probObjGivenWord, resultTopWord);
        //Parameters parameters=new Parameters(probabilityT);
        //WordCalculation wordCalculation = new WordCalculation(parameters, propertyDir, dbo_ClassName, selectedWordDir, resultDir, selectedPropertiesFile, proccessedPropertiesFile);
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
