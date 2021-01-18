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
public class ProbabilityT {

    private Integer numEnForObj = 100;
    private Integer numTopLingPat = 500;
    private Double probWordGivenObj = 0.01;
    private Double probObjGivenWord = 0.01;
    private Double multiplyValue = 0.01;
    private Integer resultTopWord = 5;
    private LingPattern lingPattern = null;

    public ProbabilityT(Double probWordGivenObj, Double probObjGivenWord, Double multiplyValue, Integer resultTopWord) {
        this.probWordGivenObj = probWordGivenObj;
        this.probObjGivenWord = probObjGivenWord;
        this.multiplyValue = multiplyValue;
        this.resultTopWord = resultTopWord;
    }

    public ProbabilityT(LingPattern lingPattern, Integer numEnForObj, Integer numTopLingPat,ProbabilityT probabilityT) {
        this.lingPattern = lingPattern;
        this.numEnForObj = numEnForObj;
        this.numTopLingPat = numTopLingPat;
        this.probWordGivenObj = probabilityT.getProbabiltyOfwordGivenObjectThresold();
        this.probObjGivenWord = probabilityT.getProbabiltyOfObjectGivenWordThresold();
        this.resultTopWord = probabilityT.getProbResultTopWordLimit();
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

    public LingPattern getLingPattern() {
        return lingPattern;
    }

    public Double getMultiplyValue() {
        return multiplyValue;
    }

    @Override
    public String toString() {
        return "ProbabilityT{" + "numEnForObj=" + numEnForObj + ", numTopLingPat=" + numTopLingPat + ", probWordGivenObj=" + probWordGivenObj + ", probObjGivenWord=" + probObjGivenWord + ", multiplyValue=" + multiplyValue + ", resultTopWord=" + resultTopWord + ", lingPattern=" + lingPattern + '}';
    }

}
