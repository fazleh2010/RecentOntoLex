/*
 * To change this license header, choose License Headers in Project PropertyConst.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import static citec.correlation.wikipedia.element.DBpediaProperty.prefixesIncluded;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elahi
 */
public interface PropertyNotation {

    public static final String rdf_type = "rdf:type";
    public static final String dbo_abstract = "dbo:abstract";
    public static final String dbo_party = "dbo:party";
    public static final String dbo_country = "dbo:country";
    public static final String dbo_birthPlace = "dbo:birthPlace";
    public static final String dbo_deathPlace = "dbo:deathPlace";
    public static final String dbp_predecessor = "dbp:predecessor";
    public static final String dbp_successor = "dbp:successor";
    public static final String dbo_region = "dbo:region";
    public static final String dbp_occupation = "dbp:occupation";
    public static final String dbo_Politician = "dbo:Politician";
    public static final String dbo_Film = "dbo:Film";
    public static final String dbo_Album = "dbo:Album";
    public static final String dbo_TelevisionShow = "dbo:TelevisionShow";
    public static final String dbo_Organization = "dbo:Organization";
    public static final String dbo_AAClass = "dbo:AAClass";




    
    
    public static final String dbo_Person = "dbo:Person";
    public static final String dbo_Athlete = "dbo:Athlete";
    public static final String dbo_Actor = "dbo:Actor";
    public static final String dbo_City = "dbo:City";
    public static final String dbo_Country = "dbo:Country";
    public static final String dbo_Lake = "dbo:Lake";
    public static final String dbo_Stadium = "dbo:Stadium";
    
    
    public static final String dbo_Mountain = "dbo:Mountain";

    public static final String dbo_River = "dbo:River";
    public static final String dbo_Place = "dbo:Place";
    public static final String dbo_Instrument = "dbo:Instrument";
    public static final String dbo_Singer = "dbo:Singer";
    public static final String dbo_alliance = "dbo:alliance";
    public static final String dbo_Company = "dbo:Company";
    public static final String dbo_Writer = "dbo:Writer";
    public static final String dbo_Book = "dbo:Book";
    public static final String dbo_Capital = "dbo:Capital";
    public static final String dbo_battle = "dbo:battle";
    public static final String dbo_Colour = "dbo:Colour";
    public static final String dbo_Color = "dbo:Color";
    public static final String dbo_Currency = "dbo:Currency";
    public static final String dbo_state = "dbo:state";
    public static final String dbo_Location = "dbo:Location";

    

         



                      
                     



    public static final String dct_description = "dct:description";
    public static final String dbp_shortDescription = "dbp:shortDescription";
    public static final String dbo_activeYearsEndDate = "dbo:activeYearsEndDate";
    public static final String dbo_activeYearsStartDate = "dbo:activeYearsStartDate";
    public static final String dbp_termEnd = "dbp:termEnd";
    public static final String dbp_termStart = "dbp:termStart";
    public static final String dbo_thumbnail = "dbo_thumbnail";
    public static final Map<String, String> prefixesIncluded = new HashMap<String, String>() {
        {
            put("http://dbpedia.org/ontology/", "dbo:");
            put("http://dbpedia.org/property/", "dbp:");
            put("http://dbpedia.org/resource", "dbr:");
            put("https://dublincore.org/specifications/dublin-core/dcmi-terms/#description", "dct:");
        }
    };

    public static final List<String> excludeProperties = new ArrayList<String>(
            Arrays.asList("dbo:wikiPageExternalLink",
                    "dbo:wikiPageID",
                    "dbo:wikiPageRevisionID",
                    "dbo:wikiPageExternalLink",
                    "dbp:wordnet_type",
                    "dbo:wikiPageWikiLink"));
    public static final List<String> include = new ArrayList<String>(
            Arrays.asList(dbo_party, dbo_country));
    
    
    
      public static List<String> nameOfClasses = new ArrayList<String>() {
        {
            add(PropertyNotation.dbo_Actor);
            add(PropertyNotation.dbo_Album);
            add(PropertyNotation.dbo_Athlete);
            add(PropertyNotation.dbo_Book);
            add(PropertyNotation.dbo_City);
            add(PropertyNotation.dbo_Company);
            add(PropertyNotation.dbo_Country);
            add(PropertyNotation.dbo_Lake);
            add(PropertyNotation.dbo_Location);
            add(PropertyNotation.dbo_Mountain);
            add(PropertyNotation.dbo_Person);
            add(PropertyNotation.dbo_Politician);
            add(PropertyNotation.dbo_Stadium);
            add(PropertyNotation.dbo_Writer);
            add(PropertyNotation.dbo_TelevisionShow);
        }
    };
}
