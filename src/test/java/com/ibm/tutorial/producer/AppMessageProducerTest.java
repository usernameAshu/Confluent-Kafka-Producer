package com.ibm.tutorial.producer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppMessageProducerTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	final void testPushJsonData() throws InterruptedException {
		Random random = new Random();
		while(true) {
		int nextInt = 100000000 + random.nextInt(99999999);
		System.out.println(nextInt);
		Thread.sleep(1500);
		}
	}

}
