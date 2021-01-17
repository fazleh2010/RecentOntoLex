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
public class ProbabilityT  {

    private Integer numEnForObj = 100;
    private Integer numTopLingPat = 500;
    private Double probWordGivenObj = 0.01;
    private Double probObjGivenWord = 0.01;
    private Integer resultTopWord = 5;
    private Integer numSelectWordGen = 100;
    private LingPattern lingPattern = null;

    public ProbabilityT(LingPattern lingPattern, Integer numEnForObj,
            Integer numTopLingPat, Integer numSelectWordGen,
            Double probWordGivenObj, Double probObjGivenWord, Integer resultTopWord) {
        this.lingPattern = lingPattern;
        this.numSelectWordGen = numSelectWordGen;
        this.numEnForObj = numEnForObj;
        this.numTopLingPat = numTopLingPat;
        this.probWordGivenObj = probWordGivenObj;
        this.probObjGivenWord = probObjGivenWord;
        this.resultTopWord = resultTopWord;
    }

    public Integer getNumberOfEntitiesForObject() {
        return numEnForObj;
    }

    public Integer getNumberOfTopLinguisticPattern() {
        return numTopLingPat;
    }

    public Double getProbabiltyOfwordGivenObjectThresold() {
        return probWordGivenObj;
    }

    public Double getProbabiltyOfObjectGivenWordThresold() {
        return probObjGivenWord;
    }

    public Integer getProbResultTopWordLimit() {
        return resultTopWord;
    }

    public Integer getNumberOfSelectedWordGenerated() {
        return numSelectWordGen;
    }

    public LingPattern getLingPattern() {
        return lingPattern;
    }

}
