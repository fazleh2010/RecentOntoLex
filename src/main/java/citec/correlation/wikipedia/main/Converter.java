/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import static citec.correlation.wikipedia.analyzer.TextAnalyzer.OBJECT;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.qald9Dir;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.AllConf;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.Cosine;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.predict_l_for_o_given_p;
import static citec.correlation.wikipedia.parameters.ThresoldConstants.predict_l_for_s_given_po;
import citec.correlation.wikipedia.results.Discription;
import citec.correlation.wikipedia.results.MR;
import citec.correlation.wikipedia.results.NewResultsHR;
import citec.correlation.wikipedia.results.NewResultsMR;
import citec.correlation.wikipedia.results.Rule;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Converter {

    public static void main(String[] args) throws Exception {
        String directory = qald9Dir + OBJECT + "/";
        String rawFileDir = dbpediaDir + "results/" + "new/MR/";
        String prediction = predict_l_for_o_given_p;
        String associationRule = AllConf;
        EvaluationMainTest evaluationMainTest = new EvaluationMainTest();

        String predict_l_for_s_given_po_dic = "/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/dic/";
        String predict_l_for_s_given_po_meanR = "/home/elahi/new/RecentOntoLex/src/main/resources/qald9/data/predict_l_for_s_given_po/meanR/";
        //run it once. we dont need to run it very time..

        System.out.println(rawFileDir);

        Pair<Boolean, List<File>> pair = FileFolderUtils.getSpecificFiles(rawFileDir, prediction, associationRule, "json");
        List<File> files = pair.getValue1();
        System.out.println("files:" + files.toString());
        readFromJsonFile(files);

    }

    /*public static void readMR(List<File> files) throws IOException, Exception {
        for (File file : files) {
            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
            System.out.println("parameters[0]:" + parameters[0]);

            ObjectMapper mapper = new ObjectMapper();
            MR resultTemp = mapper.readValue(file, MR.class);
            break;
        }
    }*/
    public static NewResultsMR readFromJsonFile(List<File> files) throws IOException, Exception {
        Map<String, List<Rule>> classDistributions = new TreeMap<String, List<Rule>>();
        Discription description = null;
        for (File file : files) {

            String fileName = file.getName();
            String[] info = fileName.split("-");
            String[] parameters = findParameter(info);
            String key = parameters[0] + "-" + parameters[1] + "-" + parameters[2];
            System.out.println("parameters[0]:" + parameters[0]);

            ObjectMapper mapper = new ObjectMapper();
            NewResultsMR resultTemp = mapper.readValue(file, NewResultsMR.class);
            description = resultTemp.getDescription();
            System.out.println(description);
            List<Rule> local = resultTemp.getDistributions();
            System.out.println(local);

            classDistributions.put(parameters[0], local);
        }
        return new NewResultsMR(description, classDistributions);
    }

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

}
