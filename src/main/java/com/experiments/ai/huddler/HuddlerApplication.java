package com.experiments.ai.huddler;

import com.experiments.ai.huddler.data.DataInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HuddlerApplication implements CommandLineRunner {

	@Autowired
	DataInitializer dataInitializer;

	public static void main(String[] args) {
		SpringApplication.run(HuddlerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		dataInitializer.init();
	}
}
