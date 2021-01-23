/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

/**
 *
 * @author elahi
 */
public class RdfTriple {

    private String subject = null;
    private String predicate = null;
    private String object = null;

    public RdfTriple(String subject, String predicate, String object) {
        this.subject = subject.trim().strip();
        this.predicate = predicate.trim().strip();
        this.object = object.trim().strip();
    }

    RdfTriple(String tripleStr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   

    public String getSubject() {
        return subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "RdfTriple{" + "subject=" + subject + ", predicate=" + predicate + ", object=" + object + '}';
    }

}
