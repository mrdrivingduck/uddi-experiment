package edu.nuaa.juddi.api;

import javax.jws.WebService;

import edu.nuaa.juddi.samples.HelloWorld;

@WebService(endpointInterface = "edu.nuaa.juddi.samples.HelloWorld", serviceName = "HelloWorld")

public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {
        // System.out.println("sayHi called");
        return "Hello " + text;
    }

}
