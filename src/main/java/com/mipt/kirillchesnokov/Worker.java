package com.mipt.kirillchesnokov;

public abstract class Worker {
    public abstract void work(int hours);

    public boolean goHome(String a, String b) {
        return a.equals(b);
    }
}