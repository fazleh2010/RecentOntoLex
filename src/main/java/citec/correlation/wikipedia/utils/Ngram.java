package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.analyzer.PosAnalyzer;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class Ngram {

    private Set<String> nGramTerms = new TreeSet<String>();
    private Map<Integer, Map<String, String>> nGramPos = new LinkedHashMap<Integer, Map<String, String>>();
    private static PosAnalyzer PosAnalyzer=new PosAnalyzer();


    public Ngram(String sentenceLine, Integer nLimit) {
        for (int n = 1; n <= nLimit; n++) {
            for (String ngram : this.ngrams(n, sentenceLine)) {
                nGramTerms.add(ngram);
            }
        }
    }

    public List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        try {
            String[] words = str.split(" ");
            for (int i = 0; i < words.length - n + 1; i++) {
                ngrams.add(concat(words, i, i + n));
            }
        } catch (Exception ex) {
            System.err.println(str + " " + ex.getMessage());
        }

        return ngrams;
    }

    public String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append((i > start ? " " : "") + words[i]);
        }
        return sb.toString();
    }

    public Set<String> getNGramTerms() {
        return nGramTerms;
    }

    public Map<String, Set<String>> getAlphabetNgrams(Ngram nGram) {
        Map<String, Set<String>> alphabetNgrams = new HashMap<String, Set<String>>();
        for (String gram : nGram.getNGramTerms()) {
            gram = FormatAndMatch.deleteChomma(gram);
            gram = FormatAndMatch.format(gram);
            if (gram.length() == 0) {
                continue;
            }
            Character ch = gram.charAt(0);
            String str = String.valueOf(ch).toLowerCase().trim();
            Set<String> set = new TreeSet<String>();
            if (alphabetNgrams.containsKey(str)) {
                set = alphabetNgrams.get(str);
                set.add(gram);
                alphabetNgrams.put(str, set);
            } else {
                set.add(gram);
                alphabetNgrams.put(str, set);
            }
        }
        return alphabetNgrams;
    }

    public void processNgram() throws Exception {
        for (String nGramStr : this.nGramTerms) {
            Integer nGram = 1;
            String posTag = null;
            String original = nGramStr.toLowerCase().strip().trim();

            Map<String, String> nGramPos = new HashMap<String, String>();
            if (nGramStr.contains(" ")) {
                String[] info = nGramStr.split(" ");
                nGram = info.length;
            }
            posTag = this.setPosTag(original);

            if (this.nGramPos.containsKey(nGram)) {
                nGramPos = this.nGramPos.get(nGram);
            }
            nGramPos.put(original, posTag);
            this.nGramPos.put(nGram, nGramPos);
        }
    }

    public Set<String> getnGramTerms() {
        return nGramTerms;
    }

    public Map<String, String> getnGramPos(Integer nGram, String givenPostag) {
        Map<String, String> filteres = new HashMap<String, String>();
        if (nGramPos.containsKey(nGram)) {
            Map<String, String> ngramPosTag = nGramPos.get(nGram);
            for (String str : ngramPosTag.keySet()) {
                String postagT = ngramPosTag.get(str);
                if (postagT.contains(" ")) {
                    String[] info = postagT.split(" ");
                    if (info[0].contains(givenPostag)) {
                        filteres.put(str, postagT);
                    }
                }

            }

        }
        return filteres;
    }

    private String setPosTag(String nGramStr) throws Exception {
        if (nGramStr.length() > 2) {
            String[] result = PosAnalyzer.posTaggerText(nGramStr);
            return result[1];
        }
        return "NN";
    }

}
