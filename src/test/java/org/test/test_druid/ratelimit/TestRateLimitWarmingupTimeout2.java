package org.test.test_druid.ratelimit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.test.test_druid.firewall.ratelimiter.RateLimiter;

public class TestRateLimitWarmingupTimeout2 {

	public static void main(String[] args) {

		final RateLimiter rateLimiter = RateLimiter.create(1000, 500,
				TimeUnit.MILLISECONDS);

		long t1 = System.currentTimeMillis();

		final CountDownLatch ct = new CountDownLatch(2);

		final AtomicLong t2 = new AtomicLong(System.currentTimeMillis());

		final AtomicInteger c = new AtomicInteger();



		new Thread() {
			public void run() {
				for (int i = 0; i < 2000; i++) {
					rateLimiter.acquire();
				}
				ct.countDown();
			};
		}.start();

		
		for (int t = 0; t < 100; t++) {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < 20; i++) {
						long failTime = System.currentTimeMillis();
						if (rateLimiter.tryAcquire(1, 100, TimeUnit.MILLISECONDS)) {
							int cur = c.incrementAndGet();
							if (cur % 500 == 0) {
								System.out.println("take by "
										+ cur
										+ " in "
										+ (System.currentTimeMillis() - t2
												.get()));
								t2.set(System.currentTimeMillis());
							}
						} else {
							System.err.println("fast fail in " + (System.currentTimeMillis() - failTime));
						}
					}
					ct.countDown();
				};
			}.start();
		}

		try {
			ct.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("take time:" + (System.currentTimeMillis() - t1));
	}
}
