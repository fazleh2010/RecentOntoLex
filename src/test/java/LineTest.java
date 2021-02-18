
import citec.correlation.wikipedia.experiments.ThresoldConstants;
import citec.correlation.wikipedia.results.LineInfo;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class LineTest {

    public static void main(String[] args) throws Exception {
        String line = "dbo:AdministrativeRegion in c_e and exists s : (s, dbp:admCtrOf, e) in G } => { occurs('Russian', d_e) } | supA=60, supB=60, supAB=60, condBA=1, condAB=1, AllConf=1, Coherence=0.5, Cosine=1, Kulczynski=1, MaxConf=1, IR=0";
        LineInfo lineInfo = new LineInfo();
        lineInfo.setProbabilityValue(line);
        System.out.println(lineInfo.getProbabilityValue());
        Map<String, Double> thresolds = new TreeMap<String, Double>();
        thresolds.put(ThresoldConstants.Cosine, 1.0);
        thresolds.put(ThresoldConstants.supA, 1.0);
        thresolds.put(ThresoldConstants.supB, 60.0);
        thresolds.put(ThresoldConstants.condAB, 1.0);
        thresolds.put(ThresoldConstants.condBA, 60.0);
      
        if (LineInfo.isThresoldValid(lineInfo.getProbabilityValue(), thresolds)) {
            System.out.println("valid line:" + line);
        } else {
            System.out.println("invalid line:");
        }

    }

}
