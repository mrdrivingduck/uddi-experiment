package org.apache.juddi.example.wsdl2uddi;

import javax.jws.WebService;

import org.apache.juddi.samples.HelloWorld;

@WebService(endpointInterface = "org.apache.juddi.samples.HelloWorld", serviceName = "HelloWorld")

public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {
        // System.out.println("sayHi called");
        return "Hello " + text;
    }

}
