package org.test.test_druid;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.core.util.StatusPrinter;

public class TestMain {

	public static void loadLoggerContext() {
//		System.getProperties().put("logback.configurationFile",
//				"./logback.xml");
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//		StatusPrinter.setPrintStream(System.err);
//		StatusPrinter.print(lc);
	}

	public static void main(String[] args) {
		try {
			loadLoggerContext();
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
					"classpath:applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
}