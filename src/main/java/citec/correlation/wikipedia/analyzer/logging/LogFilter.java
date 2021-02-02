/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.analyzer.logging;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author elahi
 */
public class LogFilter implements Filter {

	@Override
	public boolean isLoggable(LogRecord log) {
		//don't log CONFIG logs in file
		if(log.getLevel() == Level.CONFIG) return false;
		return true;
	}

}