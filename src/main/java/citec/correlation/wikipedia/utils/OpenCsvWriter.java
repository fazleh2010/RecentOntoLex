
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class OpenCsvWriter {

    public static void writeToCSV(String fileName,List<String[]> csvData) throws IOException {
      
        try ( CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeAll(csvData);
        }
    }

  
}
