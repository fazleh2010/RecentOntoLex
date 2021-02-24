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
public interface ObjectParamters {
    public List<Double> supAList = Arrays.asList(10.0, 200.0);
    public List<Double> supBList = Arrays.asList(20.0, 100.0);
    public List<Double> confABList = Arrays.asList(0.1, 0.8);
    public List<Double> confBAList = Arrays.asList(0.001, 0.8);
    public List<Double> probabiltyThresold = Arrays.asList(0.001, 0.045);
    public static List<Integer> numberOfRules = Arrays.asList(1000, 4000, 8000);
    public static List<Integer> nGram = Arrays.asList(1);
    
      /*private List<Double> supList = Arrays.asList(100.0, 500.0);
    private List<Double> supBList = Arrays.asList(100.0, 500.0);
    private List<Double> confABList = Arrays.asList(0.045, 0.5);
    private List<Double> confBAList = Arrays.asList(0.045, 0.5);
    private List<Double> AllConfList = Arrays.asList(0.001, 0.045);
    private List<Double> MaxConfList = Arrays.asList(0.001, 0.045);
    private List<Double> IrList = Arrays.asList(0.001, 0.045);
    private List<Double> KulczynskiList = Arrays.asList(0.001, 0.045);
    private List<Double> CosineList = Arrays.asList(0.001, 0.045);
    private List<Double> CoherenceList = Arrays.asList(0.001, 0.045);
    private List<Integer> numberOfRules = Arrays.asList(200, 1000);*/
    
    
    
    
  
    
    
    ///////////////////Main Thresold For p////////////////////////
    /*private List<Double> supAList = Arrays.asList(1.0,10.0,50.0);
    private List<Double> supBList = Arrays.asList(1.0,10.0,50.0);
    private List<Double> confABList = Arrays.asList(0.001,0.05);
    private List<Double> confBAList = Arrays.asList(0.001,0.05);

    public List<Double> CosineList = Arrays.asList(0.001,0.045);
    public List<Double> AllConfList = Arrays.asList(0.001,0.045);
    public List<Double> MaxConfList = Arrays.asList(0.001,0.045);
    public List<Double> IrList = Arrays.asList(0.001,0.045);
    public List<Double> KulczynskiList = Arrays.asList(0.001,0.045);
    public List<Double> CoherenceList = Arrays.asList(0.001,0.045);
    public Map<String, List<Double>> interestingness = new TreeMap<String, List<Double>>();
    public static List<Integer> numberOfRules = Arrays.asList(10000);
    public static List<Integer> nGram = Arrays.asList(1);*/
    
    
    /*private List<Double> supAList = Arrays.asList(10.0, 200.0);
    private List<Double> supBList = Arrays.asList(10.0, 200.0);
    private List<Double> confABList = Arrays.asList(0.05, 0.5);
    private List<Double> confBAList = Arrays.asList(0.05, 0.5);
    
    public List<Double> CosineList = Arrays.asList(0.05, 0.5);
    public List<Double> AllConfList = Arrays.asList(0.05, 0.5);
    public List<Double> MaxConfList = Arrays.asList(0.05, 0.5);
    public List<Double> IrList = Arrays.asList(0.05, 0.5);
    public List<Double> KulczynskiList = Arrays.asList(0.05, 0.5);
    public List<Double> CoherenceList = Arrays.asList(0.05, 0.5);
    public Map<String, List<Double>> interestingness = new TreeMap<String, List<Double>>();
    public static List<Integer> numberOfRules = Arrays.asList(3000, 10000);
    */
    //public static List<Integer> nGram = Arrays.asList(1,2,3,4);


    
}
