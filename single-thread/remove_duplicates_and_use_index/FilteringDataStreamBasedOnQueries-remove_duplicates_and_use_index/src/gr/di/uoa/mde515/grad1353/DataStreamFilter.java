package gr.di.uoa.mde515.grad1353;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import gr.di.uoa.mde515.grad1353.similarity.Utilities;

public class DataStreamFilter {
	
	private static final Logger LOGGER = Logger.getLogger(DataStreamFilter.class.getName());
	private static final String FILE_ARG = "-file";
	private static final String RESULTS = "results.txt";
	
	private static void errorMessage() {
		LOGGER.warning("Please use syntax: java -jar <jarFileName> -file <dataset>");
		LOGGER.warning("Example: java -jar FilteringDataStream.jar -file smallDataset.txt");
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
			
			BufferedReader bufferReader = null;
			BufferedWriter bufferedWriter = null;
			PrintWriter printWriter = null;
			
			try {
				Utilities.startTime();
				
				// LOGGER.log(Level.INFO, "[Single-thread] DataStreamFilter just started");

				String currentLine = null;
				bufferReader = new BufferedReader(new FileReader(datasetName));

				//write results to file
				final File file = new File(RESULTS);
				bufferedWriter = new BufferedWriter(new FileWriter(file));
				printWriter = new PrintWriter(bufferedWriter);
				
				LOGGER.log(Level.INFO, "[Single-thread] Parsing file: " + datasetName);
				LOGGER.log(Level.INFO, "Please wait...");
				
				final DatasetParser datasetParser = new DatasetParser();
				while((currentLine = bufferReader.readLine()) != null) {
					final Results results = datasetParser.parseInput(currentLine);
					if (results != null)
						printWriter.println(results.toString());
				}

				LOGGER.log(Level.INFO, "You can find results to file: " + RESULTS);

				Utilities.endTime();
				
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
					if (bufferedWriter != null)
						bufferedWriter.close();
					if (printWriter != null)
						printWriter.close();
				} catch (IOException e) {
					LOGGER.log(Level.WARNING, e.getMessage());
				}
			}
		}

	}
}
