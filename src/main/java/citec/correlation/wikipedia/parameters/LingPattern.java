/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import java.util.Set;

/**
 *
 * @author elahi
 */
public class LingPattern extends CommonParameter {

    public Integer numEnPerWord = 20;

    public LingPattern(Boolean selectClassFlag,  Integer numberOfClasses, Integer numberOfEntitiesPerProperty, Integer numberOfEntitiesPerWord) {
        super(selectClassFlag, numberOfClasses, numberOfEntitiesPerProperty);
        this.numEnPerWord = numberOfEntitiesPerWord;
    }

    public Integer getNumberOfEntitiesPerWord() {
        return numEnPerWord;
    }

    @Override
    public String toString() {
        return "LingPattern{" + "numEnPerWord=" + numEnPerWord + '}';
    }

}
