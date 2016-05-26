package org.test.test_druid.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCachedThreadPool {

	public static void main(String[] args) {
		final ExecutorService  executorService = Executors.newCachedThreadPool();
		
		
		for (int i = 0; i < 100;i++) {
			final int id = i;
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						System.out.println("task " + id + " run");
						Thread.sleep(1000);
						System.err.println("task " + id + " run over");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	} 
	 
}
