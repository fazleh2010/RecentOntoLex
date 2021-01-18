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
public class Parameters {

    private ProbabilityT probabiltyT = null;

    public Parameters(ProbabilityT probabiltyT) {
        this.probabiltyT = probabiltyT;
    }


    public ProbabilityT getProbabiltyT() {
        return probabiltyT;
    }

}
