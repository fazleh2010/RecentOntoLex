/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class PropertyCSV {
    private String classNameIndex = "classNameIndex";
    private String ruletypeIndex = "ruletypeIndex";
    private String linguisticPatternIndex = "linguisticPatternIndex";
    private String orderOfArgumentsIndex = "orderOfArgumentsIndex";
    private String patterntypeIndex = "patterntypeIndex";
    private String subjectIndex = "subjectIndex";
    private String predicateIndex = "predicateIndex";
    private String objectIndex = "objectIndex";
    private String condABIndex = "condABIndex";
    private String condBAIndex = "condBAIndex";
    private String supAIndex = "supAIndex";
    private String supBIndex = "supBIndex";
    private String supABIndex = "supABIndex";
    private String AllConfIndex = "AllConfIndex";
    private String CoherenceIndex = "CoherenceIndex";
    private String CosineIndex = "CosineIndex";
    private String IRIndex = "IRIndex";
    private String KulczynskiIndex = "KulczynskiIndex";
    private String MaxConfIndex = "MaxConfIndex";
    private String stringIndex = "stringIndex";
    public static String localized="localized";
    public static String general="general";
    private Map<String,Integer> indexMap=new TreeMap<String,Integer> ();
    
    public PropertyCSV(String type) {
        if (type.contains(localized)) {
            indexMap.put(classNameIndex, 0);
            indexMap.put(ruletypeIndex, 1);
            indexMap.put(linguisticPatternIndex, 2);
            indexMap.put(orderOfArgumentsIndex, 3);
            indexMap.put(patterntypeIndex, 4);
            indexMap.put(subjectIndex, 5);
            indexMap.put(predicateIndex, 6);
            indexMap.put(objectIndex, 7);
            indexMap.put(condABIndex, 8);
            indexMap.put(condBAIndex, 9);
            indexMap.put(supAIndex, 10);
            indexMap.put(supBIndex, 11);
            indexMap.put(supABIndex, 12);
            indexMap.put(AllConfIndex, 13);
            indexMap.put(CoherenceIndex, 14);
            indexMap.put(CosineIndex, 15);
            indexMap.put(IRIndex, 16);
            indexMap.put(KulczynskiIndex, 17);
            indexMap.put(MaxConfIndex, 18);
            indexMap.put(stringIndex, 19);

        } else {
            indexMap.put(classNameIndex, 0);
            indexMap.put(ruletypeIndex, 1);
            indexMap.put(linguisticPatternIndex, 2);
            indexMap.put(orderOfArgumentsIndex, -1);
            indexMap.put(patterntypeIndex, 3);
            indexMap.put(subjectIndex, 4);
            indexMap.put(predicateIndex, 5);
            indexMap.put(objectIndex, 6);
            indexMap.put(condABIndex, 7);
            indexMap.put(condBAIndex, 8);
            indexMap.put(supAIndex, 9);
            indexMap.put(supBIndex, 10);
            indexMap.put(supABIndex, 11);
            indexMap.put(AllConfIndex, 12);
            indexMap.put(CoherenceIndex, 13);
            indexMap.put(CosineIndex, 14);
            indexMap.put(IRIndex, 15);
            indexMap.put(KulczynskiIndex, 16);
            indexMap.put(MaxConfIndex, 17);
            indexMap.put(stringIndex, 18);

        }

    }

    public Integer getClassNameIndex() {
        return indexMap.get(classNameIndex);
    }

    public Integer getRuletypeIndex() {
        return indexMap.get(ruletypeIndex);
    }

    public Integer getOrderOfArgumentsIndex() {
        return indexMap.get(orderOfArgumentsIndex);
    }

    public Integer getPatterntypeIndex() {
        return indexMap.get(patterntypeIndex);
    }

    public Integer getSubjectIndex() {
        return indexMap.get(subjectIndex);
    }

    public Integer getPredicateIndex() {
        return indexMap.get(predicateIndex);
    }

    public Integer getObjectIndex() {
        return indexMap.get(objectIndex);
    }

    public Integer getCondABIndex() {
        return indexMap.get(condABIndex);
    }

    public Integer getCondBAIndex() {
        return indexMap.get(condBAIndex);
    }

    public Integer getSupAIndex() {
        return indexMap.get(supAIndex);
    }

    public Integer getSupBIndex() {
        return indexMap.get(supBIndex);
    }

    public Integer getSupABIndex() {
        return indexMap.get(supABIndex);
    }

    public Integer getAllConfIndex() {
        return indexMap.get(AllConfIndex);
    }

    public Integer getCoherenceIndex() {
        return indexMap.get(CoherenceIndex);
    }

    public Integer getCosineIndex() {
        return indexMap.get(CosineIndex);
    }

    public Integer getIRIndex() {
        return indexMap.get(IRIndex);
    }

    public Integer getKulczynskiIndex() {
        return indexMap.get(KulczynskiIndex);
    }

    public Integer getMaxConfIndex() {
        return indexMap.get(MaxConfIndex);
    }

    public Integer getStringIndex() {
        return indexMap.get(stringIndex);
    }
   
    public Integer getLinguisticPatternIndex() {
        return indexMap.get(linguisticPatternIndex);
    }


    public static String getLocalized() {
        return localized;
    }

    public Map<String, Integer> getIndexMap() {
        return indexMap;
    }
    
    
    ////// without localize
     //Integer classIndex=0, ruletypeIndex=1,linguisticPatternIndex=2,patterntypeIndex=3,
            //subjectIndex=4,predicateIndex=5, objectIndex=6,stringIndex=18;
            // Integer condABIndex=7,condBAIndex=8,supAIndex=9,supBIndex=10,supABIndex=11,AllConfInex=12,CoherenceIndex=13,
            //CosineIndex=14,IRIndex=15,KulczynskiIndex=16,MaxConfINdex=17;stringIndex=18;
    
    ////localize
    
           // Integer classNameIndex=0, ruletypeIndex=1,linguisticPatternIndex=2, orderOfArgumentsIndex=3,	
           //         patterntypeIndex=4,	subjectIndex=5,	predicateIndex=6,	objectIndex=7,	condABIndex=8,	
            //        condBAIndex=9,	supAIndex=10,	supBIndex=11,	supABIndex=12,	AllConfIndex=13,	
     //CoherenceIndex=14,	CosineIndex=15,	IRINdex=16	KulczynskiIndex=17,	MaxConfIndex=18,	stringIndex=19;

   
    
}
