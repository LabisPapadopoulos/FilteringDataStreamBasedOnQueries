package gr.di.uoa.mde515.grad1353.threads;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinResultQueriesThread implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(JoinResultQueriesThread.class.getName());
	private List<Thread> threads;
	private int numberOfJoinedThreads;

	public JoinResultQueriesThread(final List<Thread> threads) {
		this.threads = threads;
	}
	
	// From the threads list, join all the current threads and remove them from the list
	@Override
	public void run() {
		try {
			synchronized (threads) {
				final Iterator<Thread> resultsThreadIterator = threads.iterator();
				while(resultsThreadIterator.hasNext()) {
					final Thread currentResultThread = resultsThreadIterator.next();
					currentResultThread.join();
					resultsThreadIterator.remove();
					LOGGER.log(Level.INFO, "Joined and removed thread with id: " + currentResultThread.getId());
				}
			}
		} catch (final InterruptedException e) { }
		
		/* ok
		synchronized (threads) {
			
			while(!threads.isEmpty()) {
				final Iterator<Thread> threadIterator = threads.iterator();
				while(threadIterator.hasNext()) {
					final Thread currentThread = threadIterator.next();
					if (!currentThread.isAlive()) {
						LOGGER.info("Is going to remove thread with id: " + currentThread.getId());
						threadIterator.remove();
					}
				}
			}
		}
		*/
	}


	public int getNumberOfJoinedThreads() {
		return numberOfJoinedThreads;
	}


	public void setNumberOfJoinedThreads(int numberOfJoinedThreads) {
		this.numberOfJoinedThreads = numberOfJoinedThreads;
	}
}
