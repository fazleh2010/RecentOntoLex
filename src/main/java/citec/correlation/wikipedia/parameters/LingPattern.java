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
public class LingPattern extends CommonParameter {

        public Integer numSelectWordGen = 100;
        public Integer numEnPerWord = 20;

        public LingPattern(Integer numberOfEntitiesPerProperty, Integer numberOfSelectedWordGenerated, Integer numberOfEntitiesPerWord) {
            super(numberOfEntitiesPerProperty);
            this.numSelectWordGen = numberOfSelectedWordGenerated;
            this.numEnPerWord = numberOfEntitiesPerWord;
        }

        public Integer getNumberOfSelectedWordGenerated() {
            return numSelectWordGen;
        }

        public Integer getNumberOfEntitiesPerWord() {
            return numEnPerWord;
        }

    }
