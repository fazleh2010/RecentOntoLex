/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
public class QaldTriple {

    private String id = null;
    private String object = null;
    private String predicate = null;

    public QaldTriple(String id,String predicate, String object) {
        this.id=id;
        this.predicate = predicate;
        this.object = object;
    }

    public String getPredicate() {
        return predicate;
    }

    public String getObject() {
        return object;
    }

    public String getId() {
        return id;
    }

}
