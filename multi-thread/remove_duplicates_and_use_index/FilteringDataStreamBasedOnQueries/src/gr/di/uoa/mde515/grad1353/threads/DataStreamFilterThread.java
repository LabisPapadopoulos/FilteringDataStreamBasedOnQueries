package gr.di.uoa.mde515.grad1353.threads;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.DatasetParser;
import gr.di.uoa.mde515.grad1353.dao.Results;

public class DataStreamFilterThread implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(DataStreamFilterThread.class.getName());
	
	private String datasetName;
	private BlockingQueue<Results> resultsQueue;
	private List<Thread> threads;
	
	public DataStreamFilterThread(final String datasetName, final BlockingQueue<Results> resultsQueue, final List<Thread> threads) {
		this.datasetName = datasetName;
		this.resultsQueue = resultsQueue;
		this.threads = threads;
	}
	
	@Override
	public void run() {

		BufferedReader bufferReader = null;
		
		try {
			String currentLine = null;
			bufferReader = new BufferedReader(new FileReader(datasetName));
			
			LOGGER.log(Level.INFO, "Parsing file: " + datasetName);
			LOGGER.log(Level.INFO, "Please wait...");
			
			final DatasetParser datasetParser = new DatasetParser(resultsQueue, threads);
			while((currentLine = bufferReader.readLine()) != null) {
				datasetParser.parseInput(currentLine);
			}
			
			// Found EOF and wait for the last batch of documents
			datasetParser.waitForDocumentBatchExecution();
		} catch (final SecurityException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		} catch (final FileNotFoundException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		} catch (final IOException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		} finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
	}
}
