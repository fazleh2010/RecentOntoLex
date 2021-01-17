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
public class CommonParameter{

    public Integer numEnPerProp = 200;
    public Integer numOfClasses = 10;

    public CommonParameter(Integer numEnPerProp) {
        this.numEnPerProp = numEnPerProp;
    }

    public Integer getNumberOfEntitiesPerProperty() {
        return numEnPerProp;
    }

    public Integer getNumOfClasses() {
        return numOfClasses;
    }

}
