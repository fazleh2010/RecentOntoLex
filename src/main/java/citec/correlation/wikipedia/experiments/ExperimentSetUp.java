/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class ExperimentSetUp {

    private static List<String> classes = new ArrayList<String>();
    private static Integer numberOfClasses = 0;
    private static Integer numberOfEntityPerClass = 0;
    private static Map<String, Set<String>> classEntities = new TreeMap<String, Set<String>>();

    public ExperimentSetUp(String directory) {
        this.setValues(directory);
    }

    private void setValues(String directory) {
    }

    public static List<String> getClasses() {
        return classes;
    }

    public static Integer getNumberOfClasses() {
        return numberOfClasses;
    }

    public static Integer getNumberOfEntityPerClass() {
        return numberOfEntityPerClass;
    }

    public static Map<String, Set<String>> getClassEntities() {
        return classEntities;
    }

}
