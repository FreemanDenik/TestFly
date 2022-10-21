package com.gridnine.testing;

import com.gridnine.testing.models.FlightBuilder;

import static com.gridnine.testing.models.EnumFilter.*;

public class Main {
    private static final FilterFlight flights = new FilterFlight(FlightBuilder.createFlights());

    public static void main(String[] args) {

        System.out.println("\nВылети до текущего момента");
        flights.filter(DEPARTURE_BEFORE_NOW);
        System.out.println(flights);

        System.out.println("Прилет раньше вылета");
        flights.filter(ARRIVAL_BEFORE_DEPARTURE);
        System.out.println(flights);

        System.out.println("Простой на земле больше 2 часов");
        flights.filter(STAY_TWO_MORE_HOURS);
        System.out.println(flights);

    }
}
