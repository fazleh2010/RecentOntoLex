/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import static citec.correlation.wikipedia.element.PropertyNotation.nameOfClasses;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class CommonParameter {

    public Integer numEnPerProp = 200;
    public Integer numOfClasses = 10;
    public Boolean selectClassFlag = false;
    public Set<String> selectedClasses = new TreeSet<String>();

    public CommonParameter(Boolean selectClassFlag, Integer numOfClasses, Integer numEnPerProp) {
        if (selectClassFlag) {
            this.selectedClasses = getClasses(nameOfClasses, numOfClasses);
        }
        this.numOfClasses = numOfClasses;
        this.numEnPerProp = numEnPerProp;

    }

    private static Set<String> getClasses(List<String> classes, Integer numOfClass) {
        return new TreeSet<String>(classes.subList(0, numOfClass));
    }

    public Integer getNumberOfEntitiesPerProperty() {
        return numEnPerProp;
    }

    public Integer getNumOfClasses() {
        return numOfClasses;
    }

    public Integer getNumEnPerProp() {
        return numEnPerProp;
    }

    public Set<String> getSelectedClasses() {
        return selectedClasses;
    }

    public Boolean getSelectClassFlag() {
        return selectClassFlag;
    }

    @Override
    public String toString() {
        return "CommonParameter{" + "numEnPerProp=" + numEnPerProp + ", numOfClasses=" + numOfClasses + ", selectClassFlag=" + selectClassFlag + ", selectedClasses=" + selectedClasses + '}';
    }

}
