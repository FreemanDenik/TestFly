package com.gridnine.testing;

import com.gridnine.testing.models.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gridnine.testing.models.EnumFilter.*;

public final class FilterFlight {
    private final List<Flight> flights;
    private List<Flight> filter;
    private final Map<EnumFilter, Tuple<?>> predicateMap;

    public FilterFlight(List<Flight> flights) {
        this.filter = new ArrayList<>();

        // Подгружаем все рейсы
        this.flights = flights;

        predicateMap = new HashMap<>(Map.of(
                // Вылети до текущего момента
                DEPARTURE_BEFORE_NOW, new Tuple<>(Segment.class, x -> x.getDepartureDate().isBefore(LocalDateTime.now())),
                // Прилет раньше вылета
                ARRIVAL_BEFORE_DEPARTURE, new Tuple<>(Segment.class, x -> x.getArrivalDate().isBefore(x.getDepartureDate())),
                // Простой на земле больше 2 часов
                STAY_TWO_MORE_HOURS, new Tuple<>(Flight.class, x -> IntStream
                        .range(0, x.getSegments().size() - 1)
                        .mapToLong(i -> Duration.between(x.getSegments().get(i).getArrivalDate(), x.getSegments().get(i + 1).getDepartureDate()).toHours())
                        .sum() > 2)
        ));
    }

    /**
     * Фильтруем рейсы по указанным предикатам
     */
    public List<Flight> filter(final EnumFilter filters) {
        Tuple predicate = predicateMap.get(filters);
        if (predicate.getFirst() == Flight.class) {
            filter = flights.stream()
                    .filter(predicate.getSecond()).toList();
        } else if (predicate.getFirst() == Segment.class) {
            filter.clear();
            flights.forEach(fl -> {
                if (fl.getSegments().stream().anyMatch(predicate.getSecond())) {
                    filter.add(new Flight(fl.getSegments().stream().filter(predicate.getSecond()).toList()));
                }
            });
        }
        return filter;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        filter.stream()
                .map(m -> result.append(m))
                .map(m -> result.append("\n"))
                .collect(Collectors.joining(" "));
        return result.toString();
    }

}
