
import citec.correlation.wikipedia.analyzer.Analyzer;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.GOLD;
import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.dic.lexicon.WordObjectResults;
import citec.correlation.wikipedia.evalution.Comparision;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import citec.correlation.wikipedia.results.LineInfo;
import citec.correlation.wikipedia.results.NewResults;
import citec.correlation.wikipedia.results.ObjectWordResults;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.results.ResultUnit;
import citec.correlation.wikipedia.results.WordResult;
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

    private static File getFile(String posTag, List<File> fileList) {
        for(File file:fileList){
            if(file.getName().contains(posTag)){
               return file;
            }
        }
        return null;
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

    private static Lexicon createLexicon(String dbo_className,String dbo_prediction, String dbo_associationRule, File file) throws Exception {
        String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
        Analyzer analyzer = null;
        Lexicon lexicon = null;
        NewResults result = readFromJsonFile(new File(inputDir + file.getName()));
        Map<String, List<LineInfo>> lineLexicon = new TreeMap<String, List<LineInfo>>();
        for (String line : result.getDistributions()) {
            LineInfo lineInfo = new LineInfo(dbo_className,line, 1, 0);
            String word=lineInfo.getWord();
            if(FormatAndMatch.isNumeric(lineInfo.getWord()))
               continue;
            if(isKBValid(lineInfo.getObject())){
                continue;
            }
            String nGram = lineInfo.getWord().toLowerCase().trim().strip(); 
            System.out.println("word:"+nGram);
            System.out.println("parts-of-sppech:"+lineInfo.getPartOfSpeech());
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
    }
    
    
    public static boolean isKBValid(String word) {
        if(word.contains("#integer")) {
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
    
    public static void main2(String [] args){
        String type="\"2014\"^^<http://www.w3.org/2001/XMLSchema#integer>";
        if(!isKBValid(type))
            System.out.print(type);
    }
    
     public static void main(String[] args) throws Exception {
        NewResultEvalutionTest newResultEvalutionTest = new NewResultEvalutionTest();
        List<ObjectWordResults> entityResults = new ArrayList<ObjectWordResults>();
        Map<String,Map<String, Lexicon>> classRuleMap = new TreeMap<String,Map<String, Lexicon>>();

    
        for (String className : classNames) {
            for (String associationRule : associationRules) {
                Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(inputDir, className, associationRule, "json");
                Map<String, Lexicon> ruleMap = new TreeMap<String, Lexicon>();
                for (File file : pair.getValue1()) {
                    String dbo_className = null, dbo_prediction = null, dbo_associationRule = null;
                    String fileName = file.getName().replace("HR_", "");

                    if (!fileName.contains("Politician")
                            || !fileName.contains(Cosine)
                            || !fileName.contains(predict_l_for_s_given_po)) {
                        continue;
                    }
                    String[] info = fileName.split("-");
                    for (Integer index = 0; index < info.length; index++) {
                        if (index == 0) {
                            dbo_className = info[index];
                        }
                        if (index == 1) {
                            dbo_prediction = info[index];
                        } else if (index == 2) {
                            dbo_associationRule = info[index];
                        }
                    }
                    String key = dbo_className + "-" + dbo_prediction + "-" + dbo_associationRule;
                    Lexicon lexicon = createLexicon(dbo_className, dbo_prediction, dbo_associationRule, file);
                    ruleMap.put(key, lexicon);
                    System.out.println("key:" + key);
                    
                   

                }
                classRuleMap.put(className, ruleMap);
                
                for (String key : ruleMap.keySet()) {
                    Lexicon lexicon = ruleMap.get(key);
                    String directory = qald9Dir + OBJECT + "/";
                    System.out.println("directory:" + directory);
                    List<File> fileList = FileFolderUtils.getSpecificFiles(directory, key, ".json").getValue1();
                    System.out.println(fileList);
                    for (String posTag : Analyzer.POSTAGS) {
                        List<LexiconUnit> list = lexicon.getLexiconPosTaggged().get(posTag);
                        File file = getFile(posTag, fileList);
                        String fileName = file.getName().replace(".json", "");
                        String qaldFileName = FileFolderUtils.getQaldFile(qald9Dir + GOLD, OBJECT, posTag);
                        String conditionalFilename = directory + fileName + ".json";
                        String outputFileName = directory + fileName + "-Mean" + ".json";
                        System.out.println("qaldFileName:" + qaldFileName);
                        System.out.println("conditionalFilename:" + conditionalFilename);
                        System.out.println("outputFileName:" + outputFileName);
                        Comparision comparision = new Comparision(qald9Dir, qaldFileName, conditionalFilename, outputFileName);
                        comparision.compersionsPattern();
                    }

                }

            }

        }
        
       

        //String str = entityResultToString(entityResults);
    }
    
     
    //ResultTriple pairWord=new ResultTriple(LineInfo.getRule(), LineInfo.getProbabilityValue(ruleName));      
    //WordResult wordResult = new WordResult(pairWord, LineInfo.getWord(), "NN");

   
}
