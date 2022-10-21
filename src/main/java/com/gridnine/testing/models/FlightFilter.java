package com.gridnine.testing.models;

import com.gridnine.testing.data.Flight;
import com.gridnine.testing.data.Segment;
import com.gridnine.testing.enums.EmRules;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gridnine.testing.enums.EmRules.*;

/**
 * Фильтрация рейсов по указанным правилам, которые можно добавлять с помощью метода addRule
 */
public final class FlightFilter {
    private final List<Flight> flights;
    private List<Flight> sortFlights;
    public Map<EmRules, Rule<?>> getPredicateMap(){
        return new HashMap<>(predicateMap);
    }
    private final Map<EmRules, Rule<?>> predicateMap;
    /**
     * Конструктор в котором задаем начальные правила
     * @param flights
     */
    public FlightFilter(List<Flight> flights) {
        this.sortFlights = new ArrayList<>();
        // Подгружаем все рейсы
        this.flights = flights;
        // Задаем сюда начальные правила
        this.predicateMap = new HashMap<>(Map.of(
                // Вылети до текущего момента
                DEPARTURE_BEFORE_NOW, new Rule<>(Segment.class, x -> x.getDepartureDate().isBefore(LocalDateTime.now())),
                // Прилет раньше вылета
                ARRIVAL_BEFORE_DEPARTURE, new Rule<>(Segment.class, x -> x.getArrivalDate().isBefore(x.getDepartureDate())),
                // Простой на земле больше 2 часов
                STAY_TWO_MORE_HOURS, new Rule<>(Flight.class, x -> IntStream
                        .range(0, x.getSegments().size() - 1)
                        .mapToLong(i -> Duration.between(x.getSegments().get(i).getArrivalDate(), x.getSegments().get(i + 1).getDepartureDate()).toHours())
                        .sum() > 2)
        ));
    }
    /**
     * Добавить правило если его нет в списке
     */
    public <F> void addRule(EmRules rule, Class<F> clazz, Predicate<F> predicate) {
        predicateMap.putIfAbsent(rule, new Rule<>(clazz, predicate));
    }
    /**
     * Фильтруем рейсы по указанным предикатам
     */
    public List<Flight> filter(final EmRules filters) {
        Rule rule = predicateMap.get(filters);
        sortFlights = new ArrayList<>(flights.size());

        if (rule.compare(Flight.class)) {
            sortFlights = flights.stream()
                    .filter(rule.getPredicate()).toList();
        } else if (rule.compare(Segment.class)) {

            flights.forEach(fl -> {
                if (fl.getSegments().stream().anyMatch(rule.getPredicate())) {
                    sortFlights.add(new Flight(fl.getSegments().stream().filter(rule.getPredicate()).toList()));
                }
            });
        }
        return sortFlights;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        sortFlights.stream()
                .map(m -> result.append(m))
                .map(m -> result.append("\n"))
                .collect(Collectors.joining(" "));
        return result.toString();
    }

}
