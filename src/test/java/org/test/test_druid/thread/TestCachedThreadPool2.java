package org.test.test_druid.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 抛出RejectedExecutionException
public class TestCachedThreadPool2 {

	public static void main(String[] args) {
		final ExecutorService executorService = new ThreadPoolExecutor(2, 4,
				100, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

		for (int i = 0; i < 100; i++) {
			final int id = i;
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					try {
						System.out.println("task " + id + " run");
						Thread.sleep(5000);
						System.err.println("task " + id + " run over");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
