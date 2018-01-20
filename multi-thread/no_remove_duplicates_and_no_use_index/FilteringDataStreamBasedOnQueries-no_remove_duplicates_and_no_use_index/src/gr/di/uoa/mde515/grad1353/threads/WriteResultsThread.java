package gr.di.uoa.mde515.grad1353.threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.dao.Results;

public class WriteResultsThread implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(WriteResultsThread.class.getName());
	private static final String RESULTS = "results.txt";
	private BlockingQueue<Results> resultsQueue;
	private static final Map<Integer, Results> sortedResults = new TreeMap<Integer, Results>();
	
	public WriteResultsThread(final BlockingQueue<Results> resultsQueue) {
		this.resultsQueue = resultsQueue;
	}

	@Override
	public void run() {
		try {
			while(!Thread.currentThread().isInterrupted()) {
				final Results currentResult = resultsQueue.take();
				if (currentResult != null) {
					sortedResults.put(currentResult.getMatchDocumentId(), currentResult);
				}
			}
			
		} catch (final InterruptedException e) { }
		
		writeResultsToFile();
		
	}
	
	private void writeResultsToFile() {
		PrintWriter printWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			//write results to file
			final File file = new File(RESULTS);
		
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			printWriter = new PrintWriter(bufferedWriter);

			for (Map.Entry<Integer, Results> currentResultEntity : sortedResults.entrySet()) {
				printWriter.println(currentResultEntity.getValue().toString());
			}
			LOGGER.log(Level.INFO, "You can find results to file: " + RESULTS);
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		} finally {
			try {
				if (bufferedWriter != null)
					bufferedWriter.close();
				if (printWriter != null)
					printWriter.close();
			} catch (final IOException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
	}

}
