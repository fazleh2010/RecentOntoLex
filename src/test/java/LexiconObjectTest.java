
import citec.correlation.wikipedia.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.element.PropertyNotation;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.parameters.ExperimentThresold;
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
public class LexiconObjectTest implements PropertyNotation, DirectoryLocation, MenuOptions, TextAnalyzer {

    private static String dbo_ClassName = PropertyNotation.dbo_AAClass;
    private static String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
    private static String objectDir = dbpediaDir + classDir + "object/";
    private static ExperimentThresold experThresold=new ExperimentThresold();
  
    @Ignore
    public void LEXICON_CREATION_TEST() throws IOException, Exception {
        experThresold.setInterestLingP();
        for (String interestingResultDir : experThresold.getInterestLingP().keySet()) {

            String resultDirVariable = objectDir + RESULT_DIR + "_" + interestingResultDir;
            if (!resultDirVariable.contains("Cl10_Prop500_Lp20")) {
                continue;
            }
            
            String lexiconDir = objectDir + interestingResultDir;
            FileFolderUtils.createDirectory(lexiconDir);
            Lexicon lexicon = new Lexicon(qald9Dir);
            System.out.println("lexiconDir: "+lexiconDir);
            lexicon.prepareObjectLexicon(resultDirVariable, lexiconDir, FileFolderUtils.OBJECT, new HashSet<String>(TextAnalyzer.POSTAGS));
        }
        System.out.println("Lexicon Creation!!!");
    }

    @Test
    public void MEAN_RECIPROCAL_OBJECT_LEX_TEST() throws IOException, Exception {
        experThresold.setInterestLingP();
        for (String interestingResultDir : experThresold.getInterestLingP().keySet()) {
             interestingResultDir=objectDir + interestingResultDir;
            List<String> POSTAGS2 = new ArrayList<String>(Arrays.asList(NOUN,ADJECTIVE));
            /*if(!interestingResultDir.contains("Cl10_Prop500_Lp100"))
                continue;
            */
           
            if (interestingResultDir.contains("Cl10_Prop500_Lp20")) {
             System.out.println(interestingResultDir);
               for (String postag : POSTAGS2) {
                    String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, postag);
                    String conditionalFilename = FileFolderUtils.getLexiconFile(interestingResultDir, OBJECT, postag);
                    String outputFileName = FileFolderUtils.getMeanReciprocalFile(interestingResultDir, OBJECT, postag);
                    Comparision comparision = new Comparision(postag,qald9Dir, qaldFileName, conditionalFilename, outputFileName);
                    comparision.compersionsPattern();

                }
            }
            

        }
    }
    
    /*
    @Test
    public void LEXICON_CREATION_TEST() throws IOException, Exception {
        experThresold.setInterestLingP();
        for (String interestingResultDir : experThresold.getInterestLingP().keySet()) {
            String resultDirVariable = objectDir + RESULT_DIR + "_" + interestingResultDir;
            String lexiconDir = objectDir + interestingResultDir;
            if (resultDirVariable.contains("Cl10_Prop500_Lp20")) {
                Lexicon lexicon = new Lexicon(qald9Dir);
                lexicon.prepareObjectLexicon(resultDirVariable, lexiconDir, FileFolderUtils.OBJECT, new HashSet<String>(TextAnalyzer.POSTAGS));
            }

        }
        System.out.println("Lexicon Creation!!!");
    }
    */


  
}
