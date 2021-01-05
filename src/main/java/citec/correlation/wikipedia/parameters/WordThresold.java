/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

/**
 *
 * @author elahi
 */
public interface WordThresold {

    //public static Integer numberOfEntitiesrmSelected = 50;
    public static Integer numberOfEntitiesrmSelected = 200;

    public static Integer wordFoundInNumberOfEntities = 5;
    public static Integer TopNwords = 100;
    //public static Integer objectMinimumEntities = 2;
    public static Integer objectMinimumEntities = 100;
    

    public static Double wordGivenObjectThres =0.045;
    public static Double objectGivenWordThres = 0.045;
    public static Integer topWordLimitToConsiderThres = 5;
    public static Integer numberOfWordLimit=100;

    public static String ALL_WORDS = "all";
    public static String PROPRTY_WISE = "PROPRTY_WISE";
    public static String FILE_NOTATION = "_words.txt";
    public static String SELTECTED_WORDS_DIR =  "selectedWords/";
    public static String RESULT_DIR =  "result/";
    
     public static Integer numberOfSentencesOfAbstract=5;


}
