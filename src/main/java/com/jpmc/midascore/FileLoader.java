package com.jpmc.midascore.repository;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Component
public class FileLoader {

    public String[] loadStrings(String resourcePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getResourceAsStream(resourcePath)
                )))) {
            return reader.lines().toArray(String[]::new);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file: " + resourcePath, e);
        }
    }
}
