package com.gridnine.testing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Main {
    private static final FilterFlight flights = new FilterFlight();
    static Predicate<Flight> predicate;

    public static void main(String[] args) {

        predicate = x -> x.getSegments()
                .stream()
                .anyMatch(y -> y.getDepartureDate().isBefore(LocalDateTime.now()));

        System.out.println("\nВылети до текущего момента");
        System.out.println(flights.filter(predicate));

        predicate = x -> x.getSegments()
                .stream()
                .anyMatch(y -> y.getArrivalDate().isBefore(y.getDepartureDate()));

        System.out.println("Прилет раньше вылета");
        System.out.println(flights.filter(predicate));

        predicate = x -> IntStream
                .range(0, x.getSegments().size() - 1)
                .mapToLong(i -> Duration.between(x.getSegments().get(i).getArrivalDate(), x.getSegments().get(i + 1).getDepartureDate()).toHours())
                .sum() > 2;

        System.out.println("Простой на земле больше 2 часов");
        System.out.println(flights.filter(predicate));

    }
}