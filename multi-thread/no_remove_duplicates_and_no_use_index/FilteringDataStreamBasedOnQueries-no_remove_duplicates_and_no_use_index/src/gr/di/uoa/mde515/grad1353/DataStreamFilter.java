package gr.di.uoa.mde515.grad1353;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.dao.Results;
import gr.di.uoa.mde515.grad1353.similarity.Utilities;
import gr.di.uoa.mde515.grad1353.threads.DataStreamFilterThread;
import gr.di.uoa.mde515.grad1353.threads.WriteResultsThread;

public class DataStreamFilter {
	
	private static final Logger LOGGER = Logger.getLogger(DataStreamFilter.class.getName());
	private static final String FILE_ARG = "-file";
	
	private static void errorMessage() {
		LOGGER.log(Level.WARNING, "Please use syntax: java -jar <jarFileName> -file <dataset>");
		LOGGER.log(Level.WARNING, "Example: java -jar FilteringDataStream.jar -file smallDataset.txt");
		System.exit(-1);
	}
	
	public static void main(String[] args) {
		if (args.length != 2) {
			errorMessage();
		} else {
			
			String datasetName = null;
			for (int i = 0; i < args.length; i++) {
				if (FILE_ARG.equals(args[i])) {
					datasetName = args[i + 1];
				}
			}
			
			if (datasetName == null)
				errorMessage();
			
			// Disable logging to save execution time
			// LOGGER.setLevel(Level.OFF);
			
			Utilities.startTime();
		
			final BlockingQueue<Results> resultsQueue = new ArrayBlockingQueue<Results>(1000);
			final List<Thread> resultsThreadList = new ArrayList<Thread>();
			
			WriteResultsThread writeResultsThread = new WriteResultsThread(resultsQueue);
			final Thread writeResultsThreadThread = new Thread(writeResultsThread);
			// LOGGER.log(Level.INFO, "Created writeResultsThread");
			
			DataStreamFilterThread dataStreamFilterThread = 
					new DataStreamFilterThread(datasetName, resultsQueue, resultsThreadList);
			final Thread dataStreamFilterThreadThread = new Thread(dataStreamFilterThread);
			// LOGGER.log(Level.INFO, "Created dataStreamFilterThread");
			
			// start dataStreamFilterThread
			dataStreamFilterThreadThread.start();
			
			// start writeResultsThread
			writeResultsThreadThread.start();
			
			// join all the application threads
			final Thread joinAllThreads = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						dataStreamFilterThreadThread.join();
						
						writeResultsThreadThread.interrupt();
						writeResultsThreadThread.join();
						
						Utilities.endTime();
						
					} catch (final InterruptedException e) { }
				}
			});
			
			joinAllThreads.start();
				
		}

	}
}
