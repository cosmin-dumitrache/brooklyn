package com.cloudsoftcorp.monterey.example.noapisimple.impl;

import java.util.Map;
import java.util.HashMap;

/**
 * Generated by Cloudsoft Monterey.
 */
public class HelloCloudLocalServiceLocatorImpl implements com.cloudsoftcorp.monterey.example.noapisimple.HelloCloudServiceLocator {

    private final Map<String,com.cloudsoftcorp.monterey.example.noapisimple.Helloee> services = new HashMap<String,com.cloudsoftcorp.monterey.example.noapisimple.Helloee>();
    
    public com.cloudsoftcorp.monterey.example.noapisimple.Helloee getService(String name) {
        synchronized (services) {
            com.cloudsoftcorp.monterey.example.noapisimple.Helloee result = services.get(name);
            if (result == null) {
                result = new com.cloudsoftcorp.monterey.example.noapisimple.impl.HelloeeImpl();
                services.put(name, result);
            }
            return result;
        }
    }
}