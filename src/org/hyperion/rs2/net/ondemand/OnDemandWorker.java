package org.hyperion.rs2.net.ondemand;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;

import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;

/**
 * <p>A class which waits for ondemand requests to queue up and then processes
 * them.</p>
 * @author Graham Edgecombe
 *
 */
public class OnDemandWorker implements Runnable {
	
	/**
	 * The cache instance.
	 */
	private Cache cache;
	
	/**
	 * The array of request queues.
	 */
	private BlockingQueue<OnDemandRequest>[] queues;

	/**
	 * Creates the ondemand worker.
	 * @param queues The array of request queues.
	 * @throws FileNotFoundException if the cache could not be found.
	 * @throws InvalidCacheException if the cache is invalid.
	 */
	public OnDemandWorker(BlockingQueue<OnDemandRequest>[] queues) throws FileNotFoundException, InvalidCacheException {
		this.cache = new Cache(new File("./data/cache/"));
		this.queues = queues;
	}

	@Override
	public void run() {
		while(true) {
			for(BlockingQueue<OnDemandRequest> activeQueue : queues) {
				OnDemandRequest request;
				while((request = activeQueue.poll()) != null) {
					request.service(cache);
				}
			}
			synchronized(OnDemandPool.getOnDemandPool()) {
				try {
					OnDemandPool.getOnDemandPool().wait();
				} catch(InterruptedException e) {
					continue;
				}
			}
		}
	}

}
