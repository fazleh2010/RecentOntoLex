/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elahi
 */
public class Test {

    public static void main(String []args) {
        String experiment = "Cosine-numRule_1000-supA_10.0-supB_100.0-condAB_0.1-condBA_0.8-Cosine_0.9";
        String interestingness = "Cosine";
        String []info=experiment.split("-");
        String str=null;
        for(Integer index=0; info.length>index;index++){
          System.out.println("index:"+info[index]);
          str=info[index];
        }
         System.out.println("experiment:"+experiment);
         System.out.println("str:"+str);
    }

   

}
