/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.experiments;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public interface PredicateParamters {

    /*public List<Double> supAList = Arrays.asList(10.0,50.0);
    public List<Double> supBList = Arrays.asList(10.0,50.0);
    public List<Double> confABList = Arrays.asList(0.001,0.05);
    public List<Double> confBAList = Arrays.asList(0.001,0.05);
    public List<Double> probabiltyThresold = Arrays.asList(0.001, 0.045);
    public  List<Integer> numberOfRules = Arrays.asList(10000);
    public  List<Integer> nGram = Arrays.asList(1);*/
    
    public List<Double> supAList = Arrays.asList(100.0,1000.0);
    public List<Double> supBList = Arrays.asList(100.0,1000.0);
    public List<Double> confABList = Arrays.asList(0.05,0.6);
    public List<Double> confBAList = Arrays.asList(0.05,0.6);
    public List<Double> probabiltyThresold = Arrays.asList(0.05, 0.6);
    public  List<Integer> numberOfRules = Arrays.asList(100);
    public  List<Integer> nGram = Arrays.asList(1);
    
}
