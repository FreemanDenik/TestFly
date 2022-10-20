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

    public void setFirst(Class first) {
        this.first = first;
    }

    public Predicate<F> getSecond() {
        return second;
    }

    public void setSecond(Predicate<F> second) {
        this.second = second;
    }
}
