/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author elahi
 */
public class GrepInJava {

    private static void printStream(InputStream in) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = is.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void main(String args[]) {
        try {
            Runtime rt = Runtime.getRuntime();
            String[] cmd = {"/home/elahi/new/dbpediaFiles/unlimited/unlimited/predict_po_for_s_given_l/", "-w", "grep 'australin' *.csv >test.txt"};
            Process proc = rt.exec(cmd);
            printStream(proc.getInputStream());
            System.out.println("Error : ");
            printStream(proc.getErrorStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    

}
