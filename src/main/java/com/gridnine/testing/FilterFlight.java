package com.gridnine.testing;

import com.gridnine.testing.models.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gridnine.testing.models.EnumFilter.*;

public final class FilterFlight {
    private final List<Flight> flights;
    private List<Flight> filter;

    public List<Flight> getFilter() {
        return filter;
    }

    private final Map<EnumFilter, Predicate<Flight>> predicateMap;

    public FilterFlight(List<Flight> flights) {
        // Подгружаем все рейсы
        this.flights = flights;

        // Создаем правила
        predicateMap = new HashMap<>(Map.of(
                // Вылети до текущего момента
                DEPARTURE_BEFORE_NOW, x -> x.getSegments()
                        .stream()
                        .anyMatch(y -> y.getDepartureDate().isBefore(LocalDateTime.now())),
                // Прилет раньше вылета

//                // Прилет раньше вылета
                ARRIVAL_BEFORE_DEPARTURE, x -> x.getSegments()
                        .stream()
                        .anyMatch(y -> y.getArrivalDate().isBefore(y.getDepartureDate())),
                // Простой на земле больше 2 часов
                STAY_TWO_MORE_HOURS, x -> IntStream
                        .range(0, x.getSegments().size() - 1)
                        .mapToLong(i -> Duration.between(x.getSegments().get(i).getArrivalDate(), x.getSegments().get(i + 1).getDepartureDate()).toHours())
                        .sum() > 2
        ));
    }

    /**
     * Фильтруем рейсы по указанным предикатам
     *
     * @param filters
     * @return
     */
    public List<Flight> filter(final EnumFilter... filters) {
        List<Predicate<Flight>> predicates = predicateMap.entrySet().stream()
                .filter(w -> Arrays.stream(filters).anyMatch(t -> t.equals(w.getKey())))
                .map(w -> w.getValue())
                .collect(Collectors.toList());
        Predicate predicate = predicateBuilder(predicates.get(0), predicates.stream().skip(1).toList());


        filter = flights.stream()
                .filter(predicateBuilder(predicates.get(0), predicates.stream().skip(1).toList())).toList();
        return filter;
    }
    public String toString(){
        StringBuilder result = new StringBuilder();
        filter.stream()
                .map(m -> result.append(m))
                .map(m -> result.append("\n"))
                .collect(Collectors.joining(" "));
        return result.toString();
    }
//    public String filter1(final EnumFilter... filters) {
//        StringBuilder result = new StringBuilder();
//        var predicates = predicateMap.entrySet().stream()
//                .filter(w -> Arrays.stream(filters).anyMatch(t -> t.equals(w.getKey())))
//                .map(w -> w.getValue())
//                .collect(Collectors.toList());
//        flights.stream()
//                .filter(predicateBuilder(predicates.get(0), predicates.stream().skip(1).toList()))
//                .map(m -> result.append(m))
//                .map(m -> result.append("\n"))
//                .collect(Collectors.joining(" "));
//        return result.toString();
//    }

    /**
     * Рекурсивный строитель цепочки условия фильтрации, через OR
     *
     * @param result
     * @param predicates
     * @return
     */
    private Predicate predicateBuilder(Predicate result, List<? extends Predicate> predicates) {
        return
                predicates.size() >= 1 ?
                        predicateBuilder(result.or(predicates.get(0)), predicates.stream().skip(1).toList()) :
                        result;
    }
}
