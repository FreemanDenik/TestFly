package com.gridnine.testing.models;

import lombok.EqualsAndHashCode;

import java.util.function.Predicate;


public class Tuple<F> {
    private Class<F> type;
    private Predicate<F> predicate;

    public Tuple(Class<F> type, Predicate<F> predicate) {
        this.type = type;
        this.predicate = predicate;
    }
    public boolean compare(Class<?> clazz) {
        return type == clazz;
    }

    public Predicate<F> getPredicate() {
        return predicate;
    }

}
