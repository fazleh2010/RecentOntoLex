/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.analyzer.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author elahi
 */
public class LogFormatter extends Formatter {

    /*@Override
    public String format(LogRecord record) {
        return record.getLevel() + "::"
                + record.getMessage() + "\n";
    }*/
    
    @Override
    public String format(LogRecord record) {
        return 
                
                record.getLevel() + "::"
                +record.getMessage()+"\n";
    }
    
    /*@Override
    public String format(LogRecord record) {
        return 
                new Date(record.getMillis())+"::"
                +record.getLevel() + "::"
                +record.getMessage()+"\n";
    }*/
    
    /*@Override
    public String format(LogRecord record) {
        return record.getThreadID()+"::"+record.getSourceClassName()+"::"
                +record.getSourceMethodName()+"::"
                +new Date(record.getMillis())+"::"
                +record.getMessage()+"\n";
    }*/

}
