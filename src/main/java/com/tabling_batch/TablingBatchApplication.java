package com.tabling_batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//@EnableBatchProcessing
@SpringBootApplication
public class TablingBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TablingBatchApplication.class, args);
	}
}
