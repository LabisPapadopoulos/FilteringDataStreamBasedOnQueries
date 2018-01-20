package gr.di.uoa.mde515.grad1353.similarity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.dao.TimeType;

public class Utilities {
	
	public static final NumberFormat DECIMAL_FORMATTER = new DecimalFormat("#0.00000");
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-M-dd_HHmmss");
	public static long START_TIME;
	
	private static final Logger LOGGER = Logger.getLogger(Utilities.class.getName());
	
	// prevent instantiate
	private Utilities() { }
	
	public static void startTime() {
		START_TIME = System.currentTimeMillis();
	}
	
	public static TimeType getTimeType() {
		double currentTime = 0;
		final long executionTime = System.currentTimeMillis() - Utilities.START_TIME;
		String timeType = null;
		if ((currentTime = (executionTime / 1000d)) >= 60) {
			currentTime = currentTime/60; // minutes
			timeType = " minutes";
		} else {
			timeType = " seconds";
		}
		
		return new TimeType(currentTime, timeType);
	}
	
	public static void endTime() {
		final TimeType timeType = Utilities.getTimeType();
		LOGGER.log(Level.INFO, new StringBuilder().append("Total execution time is: ").
				append(Utilities.DECIMAL_FORMATTER.format(timeType.getCurrentTime())).
				append(timeType.getTimeType()).toString());
	}

}
