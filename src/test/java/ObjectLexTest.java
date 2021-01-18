
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.calculation.InterestedWords;
import citec.correlation.wikipedia.parameters.LingPattern;
import citec.correlation.wikipedia.parameters.ProbabilityT;
import citec.correlation.wikipedia.calculation.WordCalculation;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.main.TableMain;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.parameters.ExperimentThresold;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.File;
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
    private static ExperimentThresold experThresold=new ExperimentThresold();

   

    //with taking all properties (10823) it takes almost an hour to finish
    @Ignore
    public void A_PROPRTY_TABLE_GENERATION_TEST() throws IOException, Exception {
        TableMain.generateClassPropertyTable(rawFiles, dbo_ClassName, propertyDir);
    }
    //takes very long. minimum two hours. the properties needs to be filter before running it.
   
    @Ignore
    public void B_PARAMETER_WISE_INTERESTING_() throws IOException, Exception {
        Map<Integer, TreeSet<String>> classInformations = new TreeMap<Integer, TreeSet<String>>();
        Integer runLImit = 5;

        for (Integer i = 0; i < experThresold.getNumClasses().size(); i++) {
            Integer numOfClass = experThresold.getNumClasses().get(i);
            Integer numEnPerProp = experThresold.getNumEnPerProps().get(i);
            for (Integer j = 0; j < experThresold.getNumEnPerLps().size(); j++) {
                Integer numEnPerLp = experThresold.getNumEnPerLps().get(j);
                LingPattern lingPattern = new LingPattern(true, numOfClass, numEnPerProp, numEnPerLp);
                String outputFolderDir = objectDir + SELTECTED_WORDS_DIR + "_" + "Cl" + numOfClass + "_" + "Prop" + numEnPerProp + "_" + "Lp" + numEnPerLp + "/";
                FileFolderUtils.createDirectory(outputFolderDir);
                InterestedWords interestedWords = new InterestedWords(lingPattern, propertyDir, dbo_ClassName, outputFolderDir, runLImit);  
            }
        }

        System.out.println("find interesting words!!!");
    }

    @Test
    public  void C_PARAMETER_WISE_PROBABILTY() throws IOException, Exception {
        experThresold.setInterestLingP();
        experThresold.setConstantProbabilityT(new ProbabilityT(0.01, 0.01, 0.01, 10));
        List<File>selectedPropertiesFiles = experThresold.getSelectedFiles(propertyDir,selectedPropertiesFile);
        
        for (String interestingResultDir : experThresold.getInterestLingP().keySet()) {
            LingPattern lingPattern = experThresold.getInterestLingP().get(interestingResultDir);
            for (Integer numEnForObj : experThresold.getNumEnForObjs()) {
                for (Integer numTopLingPat : experThresold.getNumTopLingPats()) {
                    ProbabilityT probabilityT = new ProbabilityT(lingPattern, numEnForObj, numTopLingPat,
                            experThresold.getConstantProbabilityT());
                    String selectedDirVariable = objectDir + SELTECTED_WORDS_DIR + "_" + interestingResultDir;
                    String resultDirVariable = objectDir + RESULT_DIR + "_" + interestingResultDir;
                    FileFolderUtils.createDirectory(resultDirVariable);
                    WordCalculation wordCalculation = new WordCalculation(probabilityT, dbo_ClassName,
                                 selectedDirVariable, resultDirVariable, selectedPropertiesFiles, proccessedPropertiesFile);
                }
               
            }
           
        }
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
