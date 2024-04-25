package com.tabling_batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TablingBatchApplication {
	public static void main(String[] args) {
		int exit = SpringApplication.exit(SpringApplication.run(TablingBatchApplication.class, args));
		log.info("exit = {}", exit);
		System.exit(exit);
	}
}
