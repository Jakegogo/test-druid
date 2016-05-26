package org.test.test_druid.ratelimit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.test.test_druid.firewall.ratelimiter.RateLimiter;

public class TestRateLimitWarmingupAsync {

	public static void main(String[] args) {

		final RateLimiter rateLimiter = RateLimiter.create(1000, 500,
				TimeUnit.MILLISECONDS);

		long t1 = System.currentTimeMillis();
		
		final CountDownLatch ct = new CountDownLatch(5);
		
		final AtomicLong t2 = new AtomicLong(System.currentTimeMillis());

		final AtomicInteger c = new AtomicInteger();
		
		
		for (int t = 0; t < 5; t++) {

			new Thread() {
				public void run() {
					for (int i = 0; i < 400; i++) {
						rateLimiter.acquire();
						
						int cur = c.incrementAndGet();
						if (cur % 500 == 0) {
							System.out.println("take by " + cur + " in "
									+ (System.currentTimeMillis() - t2.get()));
							t2.set(System.currentTimeMillis());
						}
					}
					ct.countDown();
				};
			}.start();

		}
		
		
		try {
			ct.await(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("take time:" + (System.currentTimeMillis() - t1));
	}
}
