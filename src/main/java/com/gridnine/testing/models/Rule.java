package com.gridnine.testing.models;
import java.util.function.Predicate;
/**
 * Класс, для создания правила фильтрации <br/>
 * с указанием типа по которому протекает фильтрация
 * @param <F>
 */
public class Rule<F> {
    // Тип класс, по которому делается фильтрация
    private Class<F> type;
    // Предикат фильтрации
    private Predicate<F> predicate;

    public Rule(Class<F> type, Predicate<F> predicate) {
        this.type = type;
        this.predicate = predicate;
    }
    // Сравнение типов
    public boolean compare(Class<?> clazz) {
        return type == clazz;
    }

    public Predicate<F> getPredicate() {
        return predicate;
    }

}
