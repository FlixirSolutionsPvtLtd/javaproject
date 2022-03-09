package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.text.*;
import java.util.*;


@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping
	public String apiHealth() {
		return "Running Successfully at " + new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date());
	}
}
