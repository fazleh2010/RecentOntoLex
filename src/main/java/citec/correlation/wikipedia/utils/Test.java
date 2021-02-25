/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

/**
 *
 * @author elahi
 */
public class Test {

    public static void main(String args[]) {
        String nGram = "This#string%contains^special*characters&.";
        /*nGram = "(born";
        nGram = nGram.replaceAll("[^a-zA-Z0-9]", "");
        System.out.println(nGram);
        nGram = "born 10";
        nGram = nGram.replaceAll("\\d", "");
        System.out.println(nGram);*/
        
        nGram = "(born";

        nGram = nGram.replaceAll("\\d", "");
        nGram = nGram.replaceAll("[^a-zA-Z0-9]", " ");
        
         System.out.println("nGram:"+nGram);

    }
}
