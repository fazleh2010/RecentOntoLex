/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class ExperimentThresold {

    private static List<Integer> numClasses = Arrays.asList(5, 10, 15);
    private static List<Integer> numEnPerProps = Arrays.asList(100,500,1000);
    private static List<Integer> numEnPerLps = Arrays.asList(20, 100, 500);
    private List<Integer> numEnForObjs = Arrays.asList(100, 200, 500);
    private List<Integer> numTopLingPats = Arrays.asList(100, 500);
    private List<Integer> resultTopWords = Arrays.asList(5, 10, 15);
    private ProbabilityT constantProbabilityT = null;
    private Map<String, LingPattern> interestLingP = new TreeMap<String, LingPattern>();
    private static String CLASS_NOTATION = "Cl";
    private static String PROPERTY_NOTATION = "Prop";
    private static String LINGUISTIC_PATTERN_NOTATION = "Lp";

    public ExperimentThresold() {
        
    }

    public void setConstantProbabilityT(ProbabilityT constantProbabilityT) {
        this.constantProbabilityT = constantProbabilityT;
    }

    public void setInterestLingP() {
        this.interestLingP = getInterestingLp();
    }

    public List<Integer> getNumEnForObjs() {
        return numEnForObjs;
    }

    public List<Integer> getNumTopLingPats() {
        return numTopLingPats;
    }

    public List<Integer> getResultTopWords() {
        return resultTopWords;
    }

    public ProbabilityT getConstantProbabilityT() {
        return constantProbabilityT;
    }

    public static List<Integer> getNumClasses() {
        return numClasses;
    }

    public static List<Integer> getNumEnPerProps() {
        return numEnPerProps;
    }

    public static List<Integer> getNumEnPerLps() {
        return numEnPerLps;
    }

    public Map<String, LingPattern> getInterestLingP() {
        return interestLingP;
    }

    private static Map<String, LingPattern> getInterestingLp() {
        Map<String, LingPattern> interestLingP = new TreeMap<String, LingPattern>();
        for (Integer i = 0; i < numClasses.size(); i++) {
            Integer numOfClass = numClasses.get(i);
            Integer numEnPerProp = numEnPerProps.get(i);
            for (Integer j = 0; j < numEnPerLps.size(); j++) {
                Integer numEnPerLp = numEnPerLps.get(j);
                LingPattern lingPattern = new LingPattern(true, numOfClass, numEnPerProp, numEnPerLp);
                String outputFolderDir = CLASS_NOTATION + numOfClass + "_" + PROPERTY_NOTATION
                        + numEnPerProp + "_" + LINGUISTIC_PATTERN_NOTATION + numEnPerLp + "/";
                interestLingP.put(outputFolderDir, lingPattern);
            }
        }
        return interestLingP;
    }

    
    public static List<File> getSelectedFiles(String propertyDir, String selectedPropertiesFile) {
        List<File> selectedFiles = new ArrayList<File>();
        try {
            List<File> allFiles = FileFolderUtils.getFiles(propertyDir, ".json");
            if (!allFiles.isEmpty()) {
                selectedFiles = selectedPropertyFilter(allFiles, selectedPropertiesFile);
            } else {
                throw new Exception("There is no files in " + propertyDir + " to generate properties!!");
            }

        } catch (Exception ex) {
            Logger.getLogger(ExperimentThresold.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("no files found:");
        }
        return selectedFiles;
    }

    private static List<File> selectedPropertyFilter(List<File> filterFiles, String selectedPropFileName) throws IOException {
        Set<String> selectedProperties = FileFolderUtils.fileToSet(selectedPropFileName);
        List<File> selectedFiles = new ArrayList<File>();
        for (File file : filterFiles) {
            String tableName = file.getName();
            String property = Tables.getProperty(tableName);
            if (selectedProperties.contains(property)) {
                selectedFiles.add(file);
            } else {
                continue;
            }
        }
        return selectedFiles;
    }

}
