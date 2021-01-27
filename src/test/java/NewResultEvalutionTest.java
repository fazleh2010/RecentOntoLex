
import citec.correlation.wikipedia.analyzer.Analyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.GOLD;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.evalution.Comparision;
import citec.correlation.wikipedia.evalution.MeanReciprocalCalculation;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResults;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.results.ResultUnit;
import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class NewResultEvalutionTest {

    private static String inputDir = dbpediaDir + "results/" + "new/";
    private static Set<String> associationRules = new TreeSet<String>();
    private static Set<String> classNames = new TreeSet<String>();
    private static List<String> predicateRules = new ArrayList<String>();
    private static Map<String, List<String>> rules = new HashMap<String, List<String>>();
    private static Map<String, Map<String, List<File>>> classRuleFiles = new TreeMap<String, Map<String, List<File>>>();
    private static Map<String, List<WordObjectResults>> wordObjectResults = new TreeMap<String, List<WordObjectResults>>();
    private static final String AllConf = "AllConf";
    private static final String MaxConf = "MaxConf";
    private static final String IR = "IR";
    private static final String Kulczynski = "Kulczynski";
    private static final String Cosine = "Cosine";
    private static final String Coherence = "Coherence";

    private static final String linguisticRule = "linguisticRule";
    private static final String kbRule = "linguisticRule";

    private static final String predict_l_for_s_given_p = "predict_l_for_s_given_p";
    private static final String predict_l_for_s_given_o = "predict_l_for_s_given_o";
    private static final String predict_l_for_o_given_p = "predict_l_for_o_given_p";
    private static final String predict_l_for_s_given_po = "predict_l_for_s_given_po";

    private static final String predict_p_for_s_given_l = "predict_p_for_s_given_l";
    private static final String predict_o_for_s_given_l = "predict_o_for_s_given_l";
    private static final String predict_p_for_o_given_l = "predict_p_for_o_given_l";
    private static final String predict_po_for_s_given_l = "predict_po_for_s_given_l";

    private static Set<String> classFileNames = new HashSet<String>();
    private static String resources = "src/main/resources/";
    private static String stanfordModelFile = resources + "stanford-postagger-2015-12-09/models/english-left3words-distsim.tagger";
    private static MaxentTagger taggerModel = new MaxentTagger(stanfordModelFile);

    private static String[] findParameter(String[] info) {
        String[] parameters = new String[3];
        for (Integer index = 0; index < info.length; index++) {
            if (index == 0) {
                parameters[index] = info[index];
            }
            if (index == 1) {
                parameters[index] = info[index];
            } else if (index == 2) {
                parameters[index] = info[index];
            }
        }
        return parameters;
    }

    public NewResultEvalutionTest() {
        predicateRules = new ArrayList<String>(Arrays.asList(
                predict_l_for_s_given_p,
                predict_l_for_s_given_o,
                predict_l_for_o_given_p,
                predict_l_for_s_given_po,
                predict_p_for_s_given_l,
                predict_o_for_s_given_l,
                predict_p_for_o_given_l,
                predict_po_for_s_given_l));

        /*predictLinguisticPattern = new ArrayList<String>(Arrays.asList(
                predict_l_for_s_given_p,
                predict_l_for_s_given_o,
                predict_l_for_o_given_p,
                predict_l_for_s_given_po));
        predictObjectPredicate = new ArrayList<String>(Arrays.asList(
                predict_p_for_s_given_l,
                predict_o_for_s_given_l,
                predict_p_for_o_given_l,
                predict_po_for_s_given_l));*/
        classNames = getClassNames(inputDir);
        associationRules = new TreeSet(new ArrayList<String>(Arrays.asList(MaxConf, IR, Kulczynski, Cosine, Coherence)));

    }

    public static void main2(String[] args) throws Exception {
        NewResultEvalutionTest newResultEvalutionTest = new NewResultEvalutionTest();
        List<ObjectWordResults> entityResults = new ArrayList<ObjectWordResults>();
        Map<String, Map<String, Lexicon>> classRuleMap = new TreeMap<String, Map<String, Lexicon>>();

        //for (String className : classNames) {
        for (String prediction : predicateRules) {
            prediction = predict_l_for_s_given_po;
            for (String associationRule : associationRules) {
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, prediction, associationRule, "json");
                Map<String, Lexicon> preditcionRuleClassMap = new TreeMap<String, Lexicon>();
                for (File file : pair.getValue1()) {
                    String fileName = file.getName().replace("HR_", "");
                    if(!fileName.contains("Politician"))
                        continue;
                    String[] info = fileName.split("-");
                    String[] parameters =findParameter(info);
                    String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
                    NewResults result = readFromJsonFile(new File(inputDir + file.getName()));
                    Lexicon lexicon = createLexicon( parameters[0], parameters[1], parameters[2], result);
                    preditcionRuleClassMap.put(key, lexicon);
                    System.out.println(key+" "+lexicon);     
                }
               
                for (String key : preditcionRuleClassMap.keySet()) {
                    Lexicon lexicon = preditcionRuleClassMap.get(key);
                    String directory = qald9Dir + OBJECT + "/";
                    System.out.println("directory:" + directory);
                    List<File> fileList = FileFolderUtils.getSpecificFiles(directory, key, ".json").getValue1();
                    System.out.println(fileList);
                    Map<String,MeanReciprocalCalculation>meanReciprocals=new TreeMap<String,MeanReciprocalCalculation>();
                    String outputFileName =null;
                    for (String posTag : Analyzer.POSTAGS) {
                        List<LexiconUnit> list = lexicon.getLexiconPosTaggged().get(posTag);
                        File file = getFile(posTag, fileList);
                        String fileName = file.getName().replace(".json", "");
                        String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, posTag);
                        String conditionalFilename = directory + fileName + ".json";
                        outputFileName = directory + fileName + "-Mean" + ".json";
                        //System.out.println("qaldFileName:" + qaldFileName);
                        //System.out.println("conditionalFilename:" + conditionalFilename);
                        //System.out.println("outputFileName:" + outputFileName);
                        Comparision comparision = new Comparision(posTag,qald9Dir, qaldFileName, conditionalFilename, outputFileName);
                        comparision.compersionsPattern();
                        meanReciprocals.put(posTag, comparision.getMeanReciprocalResult());
                       
                    }
                     outputFileName = directory + key+"-Mean" + ".json";
                     FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocals, outputFileName);

                }
                break;
            }

            break;
        }

        //}
        //String str = entityResultToString(entityResults);
    }
    
    public static void main(String[] args) throws Exception {
        NewResultEvalutionTest newResultEvalutionTest = new NewResultEvalutionTest();
        Map<String,Lexicon> associationRuleLex=new TreeMap<String,Lexicon>();
        for (String prediction : predicateRules) {
            prediction = predict_l_for_s_given_po;
            Lexicon lexicon = null;
            for (String associationRule : associationRules) {
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, prediction, associationRule, "json");
                List<File> files = pair.getValue1();
                NewResults result = readFromJsonFile(files);
                lexicon = createLexicon("AllClass", prediction, associationRule, result);
                associationRuleLex.put(prediction+"-"+associationRule, lexicon);
                break;
            }
            break;
        }
        
        for (String rule : associationRuleLex.keySet()) {
            Lexicon lexicon = associationRuleLex.get(rule);
            String associationRule = "Coherence";
            String directory = qald9Dir + OBJECT + "/";
            List<File> fileList = FileFolderUtils.getSpecificFiles(directory, rule, ".json").getValue1();
            System.out.println(fileList);
            Map<String, MeanReciprocalCalculation> meanReciprocals = new TreeMap<String, MeanReciprocalCalculation>();
            for (String posTag : Analyzer.POSTAGS) {
                File file = getFile(posTag, fileList);
                String fileName = file.getName().replace(".json", "");
                String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, posTag);
                String conditionalFilename = directory + fileName + ".json";
                System.out.println("qaldFileName:" + qaldFileName);
                System.out.println("conditionalFilename:" + conditionalFilename);
                Comparision comparision = new Comparision(posTag, qald9Dir, qaldFileName, conditionalFilename);
                comparision.compersionsPattern();
                meanReciprocals.put(posTag, comparision.getMeanReciprocalResult());

            }
            String outputFileName = directory + rule + "-MeanR" + ".json";
            System.out.println("outputFileName:" + outputFileName);
            FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocals, outputFileName);

        }

        
    }
    
    private static Lexicon createLexicon(String dbo_className, String dbo_prediction, String dbo_associationRule, NewResults result) throws Exception {
        String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
        Analyzer analyzer = null;
        Lexicon lexicon = null;

        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        for (String className : result.getClassDistributions().keySet()) {
            List<String> lines = result.getClassDistributions().get(className);
            for (String line : lines) {
                System.out.println("line:" + line);
                LineInfo lineInfo = new LineInfo(className, line, 1, 0);
                if (!lineInfo.getValidFlag()) {
                    continue;
                }
                String word = lineInfo.getWord();
                //System.out.println("word:" + word);

                if (FormatAndMatch.isNumeric(lineInfo.getWord())) {
                    continue;
                }
                if (isKBValid(lineInfo.getObject())) {
                    continue;
                }
                String nGram = lineInfo.getWord().toLowerCase().trim().strip();
                //System.out.println("parts-of-sppech:" + lineInfo.getPartOfSpeech());
                List<LineInfo> results = new ArrayList<LineInfo>();
                if (lineLexicon.containsKey(nGram)) {
                    results = lineLexicon.get(nGram);
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                } else {
                    results.add(lineInfo);
                    lineLexicon.put(nGram, results);
                }
            }
        }

        lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(key, dbo_associationRule, lineLexicon);
        return lexicon;
    }

    /*private static Lexicon createLexicon(String dbo_className, String dbo_prediction, String dbo_associationRule,  NewResults result ) throws Exception {
        String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
        Analyzer analyzer = null;
        Lexicon lexicon = null;
 
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        for (String line : result.getDistributions()) {
            System.out.println("line:"+line);
            LineInfo lineInfo = new LineInfo(dbo_className, line, 1, 0);
            if (!lineInfo.getValidFlag()) {
                continue;
            }
            String word = lineInfo.getWord();
            //System.out.println("word:" + word);

            if (FormatAndMatch.isNumeric(lineInfo.getWord())) {
                continue;
            }
            if (isKBValid(lineInfo.getObject())) {
                continue;
            }
            String nGram = lineInfo.getWord().toLowerCase().trim().strip();
            //System.out.println("parts-of-sppech:" + lineInfo.getPartOfSpeech());
            List<LineInfo> results = new ArrayList<LineInfo>();
            if (lineLexicon.containsKey(nGram)) {
                results = lineLexicon.get(nGram);
                results.add(lineInfo);
                lineLexicon.put(nGram, results);
            } else {
                results.add(lineInfo);
                lineLexicon.put(nGram, results);
            }
        }

        lexicon = new Lexicon(qald9Dir);
        lexicon.preparePropertyLexicon(key, dbo_associationRule, lineLexicon);
        return lexicon;
    }*/

    public static boolean isKBValid(String word) {
        
        if (word.contains("#integer")||word.contains("#double")) {
            return true;
        }
        return false;
    }

    private static Set<String> getClassNames(String inputDir) {
        Set<String> classNames = new TreeSet<String>();
        Pair<Boolean, List<File>> filesExistList = FileFolderUtils.getSpecificFiles(inputDir, Cosine, "json");
        if (filesExistList.getValue0()) {
            if (filesExistList.getValue0()) {
                List<File> fileList = filesExistList.getValue1();
                Map<String, List<File>> ruleFiles = new TreeMap<String, List<File>>();
                for (File file : fileList) {
                    classNames.add(getClassName(file.getName()));
                }
            }
        }
        return classNames;
    }

    private static String getClassName(String name) {
        name = name.replace("HR_", "");
        String[] info = name.split("-");
        return info[0].trim().strip();
    }

    public static NewResults readFromJsonFile(File file) throws IOException, Exception {
        ObjectMapper mapper = new ObjectMapper();
        NewResults result = mapper.readValue(file, NewResults.class);
        return result;
    }
    
    public static NewResults readFromJsonFile(List<File> files) throws IOException, Exception {
        Map<String, List<String>> classDistributions = new TreeMap<String, List<String>>();
        Discription description = null;
        for (File file : files) {
            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];

            ObjectMapper mapper = new ObjectMapper();
            NewResults resultTemp = mapper.readValue(file, NewResults.class);
            description = resultTemp.getDescription();
            List<String> local = resultTemp.getDistributions();
            classDistributions.put(parameters[0], local);
        }
        return new NewResults(description, classDistributions);
    }

  
    private static File getFile(String posTag, List<File> fileList) {
        for (File file : fileList) {
            if (file.getName().contains(posTag)) {
                return file;
            }
        }
        return null;
    }

    private static boolean isValidFile(String fileName, String predict_l_for_s_given_po, String Cosine) {
        if (!fileName.contains(Cosine)
                || !fileName.contains(predict_l_for_s_given_po)) {
            return true;
        }
        return false;
    }
    
}
