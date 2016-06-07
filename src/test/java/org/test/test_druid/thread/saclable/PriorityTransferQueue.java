package org.test.test_druid.thread.saclable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

//1.   创建一个类，名为 PriorityTransferQueue，扩展 PriorityBlockingQueue 类并实现 TransferQueue 接口。
public class PriorityTransferQueue<E> extends PriorityBlockingQueue<E> implements TransferQueue<E> {

	// 2. 声明一个私有 AtomicInteger 属性，名为 counter，用来储存正在等待元素的消费者的数量。
	private AtomicInteger counter;

	// 3. 声明一个私有 LinkedBlockingQueue 属性，名为 transferred。
	private LinkedBlockingQueue<E> transfered;

	// 4. 声明一个私有 ReentrantLock 属性，名为 lock。
	private ReentrantLock lock;

	// 5. 实现类的构造函数，初始化它的属性值。
	public PriorityTransferQueue() {
		counter = new AtomicInteger(0);
		lock = new ReentrantLock();
		transfered = new LinkedBlockingQueue<E>();
	}

	// 6. 实现 tryTransfer() 方法。此方法尝试立刻发送元素给正在等待的消费者（如果可能）。如果没有任何消费者在等待，此方法返回
	// false 值。
	@Override
	public boolean tryTransfer(E e) {
		lock.lock();
		boolean value;
		if (counter.get() == 0) {
			value = false;
		} else {
			put(e);
			value = true;
		}
		lock.unlock();
		return value;
	}

	// 7. 实现 transfer() 方法。此方法尝试立刻发送元素给正在等待的消费者（如果可能）。如果没有任何消费者在等待，
	// 此方法把元素存入一个特殊queue，为了发送给第一个尝试获取一个元素的消费者并阻塞线程直到元素被消耗。

	@Override
	public void transfer(E e) throws InterruptedException {
		lock.lock();
		if (counter.get() != 0) {
			put(e);
			lock.unlock();
		} else {
			transfered.add(e);
			lock.unlock();
			synchronized (e) {
				e.wait();
			}
		}
	}

	// 8. 实现 tryTransfer() 方法，它接收3个参数：
	// 元素，和需要等待消费者的时间（如果没有消费者的话），和用来注明时间的单位。如果有消费者在等待，立刻发送元素。否则，转化时间到毫秒并使用
	// wait() 方法让线程进入休眠。当消费者取走元素时，如果线程在 wait() 方法里休眠，你将使用 notify() 方法唤醒它。
	@Override
	public boolean tryTransfer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		lock.lock();
		if (counter.get() != 0) {
			put(e);
			lock.unlock();
			return true;
		} else {
			transfered.add(e);
			long newTimeout = TimeUnit.MILLISECONDS.convert(timeout, unit);
			lock.unlock();
			e.wait(newTimeout);
			lock.lock();

			if (transfered.contains(e)) {
				transfered.remove(e);
				lock.unlock();
				return false;
			} else {
				lock.unlock();
				return true;
			}
		}
	}

	// 9. 实现 hasWaitingConsumer() 方法。使用 counter 属性值来计算此方法的返回值。如果counter 的值大于0，放回
	// true。不然，返回 false。
	@Override
	public boolean hasWaitingConsumer() {
		return (counter.get() != 0);
	}

	// 10. 实现 getWaitingConsumerCount() 方法。返回counter 属性值。
	@Override
	public int getWaitingConsumerCount() {
		return counter.get();
	}

	// 11.实现 take() 方法。此方法是当消费者需要元素时被消费者调用的。首先，获取之前定义的锁并增加在等待的消费者数量。
	@Override
	public E take() throws InterruptedException {
		lock.lock();
		counter.incrementAndGet();

		// 12.如果在 transferred queue 中无任何元素。释放锁并使用 take()
		// 方法尝试从queue中获取元素，此方法将让线程进入睡眠直到有元素可以消耗。
		E value = transfered.poll();
		if (value == null) {
			lock.unlock();
			value = super.take();
			lock.lock();

			// 13. 否则，从transferred queue 中取走元素并唤醒正在等待要消耗元素的线程（如果有的话）。
		} else {
			synchronized (value) {
				value.notify();
			}
		}

		// 14. 最后，增加正在等待的消费者的数量并释放锁。
		counter.decrementAndGet();
		lock.unlock();
		return value;
	}

}

// 15. 实现一个类，名为 Event，扩展 Comparable 接口，把 Event 类参数化。
class Event implements Comparable<Event> {

	// 16. 声明一个私有 String 属性，名为 thread，用来储存创建事件的线程的名字。
	private String thread;

	// 17. 声明一个私有 int 属性，名为 priority，用来储存事件的优先级。
	private int priority;

	// 18. 实现类的构造函数，初始化它的属性值。
	public Event(String thread, int priority) {
		this.thread = thread;
		this.priority = priority;
	}

	// 19. 实现一个方法，返回 thread 属性值。
	public String getThread() {
		return thread;
	}

	// 20. 实现一个方法，返回 priority 属性值。
	public int getPriority() {
		return priority;
	}

	// 21. 实现 compareTo() 方法。此方法把当前事件与接收到的参数事件进行对比。返回
	// -1，如果当前事件的优先级的级别高于参数；返回 1，如果当前事件的优先级低于参数；如果相等，则返回
	// 0。你将获得一个按优先级递减顺序排列的list。有高等级的事件就会被排到queue的最前面。
	public int compareTo(Event e) {
		if (this.priority > e.getPriority()) {
			return -1;
		} else if (this.priority < e.getPriority()) {
			return 1;
		} else {
			return 0;
		}
	}
}

// 22. 实现一个类，名为 Producer，它实现 Runnable 接口。
class Producer implements Runnable {

	// 23. 声明一个私有 PriorityTransferQueue 属性，接收参数化的 Event 类属性，名为
	// buffer，用来储存这个生产者生成的事件。
	private PriorityTransferQueue<Event> buffer;

	// 24. 实现类的构造函数，初始化它的属性值。
	public Producer(PriorityTransferQueue<Event> buffer) {
		this.buffer = buffer;
	}

	// 25. 这个类的实现 run() 方法。创建 100 个 Event
	// 对象，用他们被创建的顺序决定优先级（越先创建的优先级越高）并使用 put() 方法把他们插入queue中。
	public void run() {
		for (int i = 0; i < 100; i++) {
			Event event = new Event(Thread.currentThread().getName(), i);
			buffer.put(event);
		}
	}
}

// 26. 实现一个类，名为 Consumer，它要实现 Runnable 接口。
class Consumer implements Runnable {

	// 27. 声明一个私有 PriorityTransferQueue 属性，参数化 Event 类属性，名为
	// buffer，用来获取这个类的事件消费者。
	private PriorityTransferQueue<Event> buffer;

	// 28. 实现类的构造函数，初始化它的属性值。
	public Consumer(PriorityTransferQueue<Event> buffer) {
		this.buffer = buffer;
	}

	// 29. 实现 run() 方法。它使用 take() 方法消耗1002 Events
	// (这个例子实现的全部事件）并把生成事件的线程数量和它的优先级别写入操控台。
	@Override
	public void run() {
		for (int i = 0; i < 1002; i++) {
			try {
				Event value = buffer.take();
				System.out.printf("Consumer: %s: %d\n", value.getThread(), value.getPriority());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

// 30. 创建例子的主类通过创建一个类，名为 Main 并添加 main()方法。
class Main {

	public static void main(String[] args) throws Exception {

		// 31. 创建一个 PriorityTransferQueue 对象，名为 buffer。
		PriorityTransferQueue<Event> buffer = new PriorityTransferQueue<Event>();

		// 32. 创建一个 Producer 任务并运行 10 线程来执行任务。
		Producer producer = new Producer(buffer);
		Thread producerThreads[] = new Thread[10];
		for (int i = 0; i < producerThreads.length; i++) {
			producerThreads[i] = new Thread(producer);
			producerThreads[i].start();
		}

		// 33.创建并运行一个 Consumer 任务。
		Consumer consumer = new Consumer(buffer);
		Thread consumerThread = new Thread(consumer);
		consumerThread.start();

		// 34. 写入当前的消费者数量。
		System.out.printf("Main: Buffer: Consumer count: %d\n", buffer.getWaitingConsumerCount());

		// 35. 使用 transfer() 方法传输一个事件给消费者。
		Event myEvent = new Event("Core Event", 0);
		buffer.transfer(myEvent);
		System.out.printf("Main: My Event has ben transfered.\n");

		// 36. 使用 join() 方法等待生产者的完结。
		for (int i = 0; i < producerThreads.length; i++) {
			try {
				producerThreads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// 37. 让线程休眠1秒。
		TimeUnit.SECONDS.sleep(1);

		// 38.写入当前的消费者数量。
		System.out.printf("Main: Buffer: Consumer count: %d\n", buffer.getWaitingConsumerCount());

		// 39. 使用 transfer() 方法传输另一个事件。
		myEvent = new Event("Core Event 2", 0);
		buffer.transfer(myEvent);

		// 40. 使用 join() 方法等待消费者完结。
		consumerThread.join();

		// 41. 写信息表明程序结束。
		System.out.printf("Main: End of the program\n");
	}
}
