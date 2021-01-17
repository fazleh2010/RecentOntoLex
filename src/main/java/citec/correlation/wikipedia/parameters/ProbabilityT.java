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
public class ProbabilityT extends CommonParameter{

    private Integer numEnForObj = 100;
    private Integer numTopLingPat = 500;
    private Double probWordGivenObj = 0.01;
    private Double probObjGivenWord = 0.01;
    private Integer resultTopWord = 5;

    public ProbabilityT(Integer numberEnPerProp, Integer numEnForObj, Integer numTopLingPat,
            Double probWordGivenObj, Double probObjGivenWord, Integer resultTopWord) {
        super(numberEnPerProp);
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

}
