package org.test.test_druid.filter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.test.test_druid.firewall.LimitCounter;
import org.test.test_druid.firewall.LimitCounterBuilder;
import org.test.test_druid.firewall.ratelimiter.RateLimiter;

public class TestRateLimitWarmingup2 {

	public static void main(String[] args) {

		final LimitCounter rateLimiter = LimitCounterBuilder.createRateLimiter("企业限流").build();;

		rateLimiter.setUpperLimit(200);
		rateLimiter.setLimit(5);
		
		
		long t1 = System.currentTimeMillis();
		
		final CountDownLatch ct = new CountDownLatch(10);
		
		final AtomicLong t2 = new AtomicLong(System.currentTimeMillis());

		final AtomicInteger c = new AtomicInteger();
		
		
		for (int t = 0; t < 10; t++) {

			new Thread() {
				public void run() {
					for (int i = 0; i < 50; i++) {
						rateLimiter.acquire();
						
						int cur = c.incrementAndGet();
						if (cur % 5 == 0) {
							System.out.println("take by " + cur + " in "
									+ (System.currentTimeMillis() - t2.get()));
							t2.set(System.currentTimeMillis());
						}
						
//						if (rateLimiter.hasWarn()) {
//							System.err.println("has warn c" + c + ", "
//									+ rateLimiter.getName() + ":"
//									+ rateLimiter.getCurCount());
//						}
//						System.out.println(rateLimiter.getCurCount());
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
