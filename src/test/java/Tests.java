import com.gridnine.testing.FilterFlight;
import com.gridnine.testing.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {


    FilterFlight filterFlight;

    @BeforeAll
    public void init() {

        LocalDateTime now = LocalDateTime.now().plusDays(3);
        List<Flight> flightList = List.of(
                new Flight(List.of(
                        new Segment(now.minusDays(4), now)
                )),
                new Flight(List.of(
                        new Segment(now, now.minusHours(2))
                )),
                new Flight(List.of(
                        new Segment(now, now.plusHours(1)),
                        new Segment(now.plusHours(1), now.plusHours(3)),
                        new Segment(now.plusHours(4).plusMinutes(10), now.plusHours(6)),
                        new Segment(now.plusHours(8).plusMinutes(23), now.plusHours(8)))
                )
        );
        try (MockedStatic<FlightBuilder> utilities = Mockito.mockStatic(FlightBuilder.class)) {
            utilities.when(FlightBuilder::createFlights).thenReturn(flightList);
            filterFlight = new FilterFlight(FlightBuilder.createFlights());
        }

    }

    @Test
    public void test1() {

        List<Flight> departureList = filterFlight.filter(EnumFilter.DEPARTURE_BEFORE_NOW);
        assertTrue(departureList.size() == 1);
        assertTrue(departureList.get(0).getSegments().size() == 1);
        long days = Duration.between(departureList.get(0).getSegments().get(0).getDepartureDate(), departureList.get(0).getSegments().get(0).getArrivalDate()).toDays();
        assertEquals(days, 4);

        List<Flight> arrivalList = filterFlight.filter(EnumFilter.ARRIVAL_BEFORE_DEPARTURE);
        assertTrue(arrivalList.size() == 2);
        assertTrue(arrivalList.get(0).getSegments().size() == 1);
        long hours = Duration.between(arrivalList.get(0).getSegments().get(0).getArrivalDate(), arrivalList.get(0).getSegments().get(0).getDepartureDate()).toHours();
        assertEquals(hours, 2);

        List<Flight> twoHoursList = filterFlight.filter(EnumFilter.STAY_TWO_MORE_HOURS);
        assertTrue(twoHoursList.size() == 1);
        assertTrue(twoHoursList.get(0).getSegments().size() == 4);
        long minutes = IntStream.range(0, twoHoursList.get(0).getSegments().size() - 1)
                .mapToLong(i -> Duration.between(twoHoursList.get(0).getSegments().get(i).getArrivalDate(), twoHoursList.get(0).getSegments().get(i + 1).getDepartureDate()).toMinutes())
                .sum();
        assertEquals(minutes, 213);
    }
}
