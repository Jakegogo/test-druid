package org.test.test_druid.ratelimit;

import org.test.test_druid.firewall.ratelimiter.RateLimiter;

public class TestRateLimitBrusty {

	public static void main(String[] args) {

		RateLimiter rateLimiter = RateLimiter.create(1000);

		long t1 = System.currentTimeMillis();
		long t2 = System.currentTimeMillis();
		
		
		int c = 0;
		
		for (int i = 0; i < 2000; i++) {
			rateLimiter.acquire();
			if (++c % 500 == 0) {
				System.out.println("take by " + c + " in " + (System.currentTimeMillis() - t2));
				t2 = System.currentTimeMillis();
			}
		}
		
		System.out.println("take time:" + (System.currentTimeMillis() - t1));
	}
}
