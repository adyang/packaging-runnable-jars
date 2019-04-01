package com.example.shade;

import io.vavr.Tuple;

public class MavenApp {
    public static void main(String[] args) {
        System.out.println(Tuple.of("maven-shade", "MavenApp", "Running"));
    }
}
