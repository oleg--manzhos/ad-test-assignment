package me.manzhos.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Env {

    private static final Properties props = new Properties();

    static {
        String env = System.getProperty("env", "prod");
        String path = "env/" + env + ".properties";
        try (InputStream is = Env.class.getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("Env file not found: " + path);
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String apiBaseUrl() {
        return props.getProperty("api.baseUrl");
    }
}