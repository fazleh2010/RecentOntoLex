/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;

import lombok.NoArgsConstructor;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
@NoArgsConstructor

public class EvaluationLogging {
    private static final Logger logger = Logger.getLogger(EvaluationLogging.class.getName());
    
/*SEVERE (highest)
WARNING
INFO
CONFIG
FINE
FINER
FINEST*/

    public static void main(String[] args) {
        String inputDir = "src/main/resources/lexicon/en/nouns/input/";
        String outputDir = "src/main/resources/lexicon/en/nouns/new/output/";

        /*LOG.info("Starting {} with language parameter '{}'", EvaluationLogging.class.getName(), "language");
        LOG.info("Input directory: {}", inputDir);
        LOG.info("Output directory: {}", outputDir);
        LOG.warn("To get optimal combinations of sentences please add the following types to {}\n{}", "test");*/

    }

}
