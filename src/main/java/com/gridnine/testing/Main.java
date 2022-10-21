package com.gridnine.testing;

import com.gridnine.testing.data.Flight;
import com.gridnine.testing.data.FlightBuilder;
import com.gridnine.testing.data.Segment;
import com.gridnine.testing.models.FlightFilter;
import com.gridnine.testing.models.Rule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static com.gridnine.testing.enums.EmRules.*;

public class Main {
    private static final FlightFilter flights = new FlightFilter(FlightBuilder.createFlights());

    public static void main(String[] args) {

        System.out.println("\nВылет до текущего момента");
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
