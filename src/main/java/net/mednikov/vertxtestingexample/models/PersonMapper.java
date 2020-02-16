package net.mednikov.vertxtestingexample.models;

import io.vertx.core.json.JsonObject;

public class PersonMapper {

    public static Person fromJson (JsonObject json){
        return new Person(json.getString("firstName"), 
        json.getString("lastName"), 
        json.getInteger("age"));
    }
}