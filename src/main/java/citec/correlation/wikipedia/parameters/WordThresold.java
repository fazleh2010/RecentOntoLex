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

    public static Integer numberOfEntitiesPerProperty = 200;
    public static Integer numberOfEntitiesPerWord = 20;
    public static Integer numberOfSelectedWordGenerated = 100;
    public static Integer numberOfEntitiesForObject = 100;
    public static Double probabiltyOfwordGivenObjectThresold =0.045;
    public static Double probabiltyOfObjectGivenWordThresold = 0.045;
    public static Integer probResultTopWordLimit = 5;
    public static Integer numberOfSelectedWordsForCalProbabilty=100;

    public static String ALL_WORDS = "all";
    public static String PROPRTY_WISE = "PROPRTY_WISE";
    public static String FILE_NOTATION = "_words.txt";
    public static String SELTECTED_WORDS_DIR =  "selectedWords/";
    public static String RESULT_DIR =  "result/";
    
     public static Integer numberOfSentencesOfAbstract=5;
     
     //very dirty solution. needs to be change in future
     
   


}
