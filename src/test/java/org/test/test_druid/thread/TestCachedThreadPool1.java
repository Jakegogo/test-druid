package org.test.test_druid.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestCachedThreadPool1 {

	public static void main(String[] args) {
		ExecutorService executorService = new ThreadPoolExecutor(2, 4, 100,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		for (int i = 0 ; i < 2;i++) {
			final int id = i;
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						System.out.println("thread " + id + " sleeping.");
						Thread.sleep(5000);
						System.out.println("thread " + id + " sleep over.");
					} catch (InterruptedException e) {
		 				e.printStackTrace();
					}
				}
			});
		}
		
		for (int j = 2; j < 5;j++) {
			final int id = j;
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						System.out.println("thread " + id + " sleeping.");
						Thread.sleep(5000);
						System.out.println("thread " + id + " sleep over.");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		
	} 
	 
}
