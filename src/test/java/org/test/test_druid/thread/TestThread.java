package org.test.test_druid.thread;

public class TestThread {
	
	public static void main(String[] args) throws InterruptedException {
		
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(25000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		Thread.sleep(1000000);
		
		
		
	}

}
