package com.gridnine.testing.models;

import java.util.function.Predicate;


public class Tuple<F> {
    private Class first;
    private Predicate<F> second;

    public Tuple(Class<F> first, Predicate<F> second) {
        this.first = first;
        this.second = second;
    }

    public Class getFirst() {
        return first;
    }

    public Predicate<F> getSecond() {
        return second;
    }
    class Builder{
        Builder(Class f){
            first = f;
        }
    }
}
