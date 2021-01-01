/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author elahi
 */
public class Measure {

    public static double minimum(double value1, double value2) {
        double[] array = {value1, value2};
        return NumberUtils.min(array);
    }

    public static double maximum(double value1, double value2) {
        double[] array = {value1, value2};
        return NumberUtils.max(array);
    }

    public static void main(String[] args) {
     System.out.println(minimum(0.4, 0.2));
    }
}
