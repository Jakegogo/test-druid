package org.test.test_druid.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// 抛出RejectedExecutionException
public class TestCachedThreadPool4 {

	public static void main(String[] args) {
		final ExecutorService executorService = new ThreadPoolExecutor(2, 4,
				100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50),
				new RejectedExecutionHandler() {

					/**
					 * <pre>
					 * 同步<b>提交</b>超出队列范围的任务,并且使用线程池的线程执行任务
					 * 如果提交方模块或处理方使用了锁等同步机制,此方式可以解决reject后使用同步<b>调用</b>方案所带来的死锁问题
					 * </pre>
					 */
					@Override
					public void rejectedExecution(Runnable r,
							ThreadPoolExecutor executor) {
						try {
							executor.getQueue().put(r);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							throw new RuntimeException(
									"Interrupted while submitting task", e);
						}
					}
				});

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
			System.out.println("submitted " + id);
		}
	}

}
