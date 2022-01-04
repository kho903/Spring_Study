package com.example.jpa;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {

	@RequestMapping(value = "/hello-spring", method = RequestMethod.GET)
	public String helloSpring() {
		return "hello spring";
	}
}
