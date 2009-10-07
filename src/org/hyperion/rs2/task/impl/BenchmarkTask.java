package org.hyperion.rs2.task.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.task.Task;

/**
 * A utility class that wraps around another, benchmarking it.
 * @author Graham Edgecombe
 *
 */
public class BenchmarkTask implements Task {
	
	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(BenchmarkTask.class.getName());
	
	/**
	 * Sample count.
	 */
	private static final int SAMPLES = 100;
	
	/**
	 * A list of samples.
	 */
	private static final List<Long> samples = new LinkedList<Long>();
	
	/**
	 * The task.
	 */
	private final Task task;
	
	/**
	 * Creates the benchmark task.
	 * @param task The task to wrap around.
	 */
	public BenchmarkTask(Task task) {
		this.task = task;
	}

	@Override
	public void execute(GameEngine context) {
		long start = System.nanoTime();
		task.execute(context);
		long elapsed = System.nanoTime() - start;
		samples.add(elapsed);
		if(samples.size() >= SAMPLES) {
			long total = 0;
			for(long sample : samples){
				total += sample;
			}
			logger.info((((double) total / (double) samples.size() / 1000000D)) + " milliseconds (average over " + samples.size() + " samples)");
			samples.clear();
		}
	}

}
