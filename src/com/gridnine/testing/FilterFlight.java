package com.gridnine.testing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterFlight {
    public List<Flight> flights;

    public FilterFlight() {
        this.flights = FlightBuilder.createFlights();
    }

    public String filter(final Predicate predicate) {
        StringBuilder result = new StringBuilder();
        flights.stream()
                .filter(predicate)
                .map(m -> result.append(m))
                .map(m -> result.append("\n"))
                .collect(Collectors.joining(" "));

        return result.toString();
    }
}
