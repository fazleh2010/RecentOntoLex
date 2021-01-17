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

    private LingPattern lingPattern = null;
    private ProbabilityT probabiltyT = null;

    public Parameters(LingPattern lingPattern, ProbabilityT probabiltyT) {
        this.lingPattern = lingPattern;
        this.probabiltyT = probabiltyT;
    }

    public Parameters(LingPattern lingPattern) {
        this.lingPattern = lingPattern;
    }

    public Parameters(ProbabilityT probabiltyT) {
        this.probabiltyT = probabiltyT;
    }

    public LingPattern getLingPattern() {
        return lingPattern;
    }

    public ProbabilityT getProbabiltyT() {
        return probabiltyT;
    }

}
