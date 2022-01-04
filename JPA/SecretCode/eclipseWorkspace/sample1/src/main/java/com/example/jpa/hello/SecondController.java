package com.example.jpa.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {

	@RequestMapping(value = "/hello-spring", method = RequestMethod.GET)
	public String helloSpring() {
		return "hello spring";
	}
	
	@GetMapping("/hello-rest")
	public String helloRest() {
		return "hello rest";
	}
	
	@GetMapping("/api/helloworld")
	public String helloRestApi() {
		return "hello rest api";
	}
}
