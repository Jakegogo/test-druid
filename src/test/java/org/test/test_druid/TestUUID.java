package org.test.test_druid;

import java.util.Random;

public class TestUUID {
	
	public static void main(String[] args) {
		
		String[] dic = {
				"P9LMO0K4NI", 
				"8JB7UHV2Y3", 
				"GC1T6FXRDZ", 
				"ESW5AQ+-=!"
				};
		Random r = new Random();
		
		long sed = System.currentTimeMillis();
		
		StringBuilder key = new StringBuilder();
		
		long loopVal = sed;
		for (int i = 0;i < 13;i++) {
			key.append(dic[r.nextInt(4)].charAt((int) (loopVal % 10)));
			loopVal /= 10;
		}        
		
		System.out.println(key.toString());
	}
	
}
