package com.mipt.kirillchesnokov;

public class MainClass {
    private int someInt;
    private String someString;
    protected static double someDouble;
    public final long someLong = 123456789L;

    public static void main(String[] args) {
        for (int i = 0; i <= 15; i++) {
            System.out.println("Iter: " + i);
        }
    }
}