package org.test.test_druid.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

import org.test.test_druid.thread.saclable.ScalableThreadPoolExecutor;

// 抛出RejectedExecutionException
public class TestScalableThreadPool {

	public static void main(String[] args) {
		final ExecutorService executorService = new ScalableThreadPoolExecutor(2, 50,
				100, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());

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
		System.err.println("submit over.");
	}

}
