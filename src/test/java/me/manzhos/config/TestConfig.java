package me.manzhos.config;

import io.restassured.RestAssured;

public class TestConfig {
    public static void init() {
        RestAssured.baseURI = Env.apiBaseUrl();
    }
}
