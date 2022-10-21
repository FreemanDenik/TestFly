import com.gridnine.testing.models.FlightFilter;
import com.gridnine.testing.enums.EmRules;
import com.gridnine.testing.data.Flight;
import com.gridnine.testing.data.FlightBuilder;
import com.gridnine.testing.data.Segment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Tests {


    FlightFilter flightFilter;

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
            flightFilter = new FlightFilter(FlightBuilder.createFlights());
        }

    }

    @Test
    public void add_false_rule() {
        int size = flightFilter.getPredicateMap().size();
        flightFilter.addRule(EmRules.DEPARTURE_BEFORE_NOW, Segment.class, x -> true);
        assertEquals(flightFilter.getPredicateMap().size(), size);

    }

    @Test
    public void departure_before_now() {
        List<Flight> departureList = flightFilter.filter(EmRules.DEPARTURE_BEFORE_NOW);
        assertTrue(departureList.size() == 1);
        assertTrue(departureList.get(0).getSegments().size() == 1);
        long days = Duration.between(departureList.get(0).getSegments().get(0).getDepartureDate(), departureList.get(0).getSegments().get(0).getArrivalDate()).toDays();
        assertEquals(days, 4);
    }

    @Test
    public void arrival_before_departure() {
        List<Flight> arrivalList = flightFilter.filter(EmRules.ARRIVAL_BEFORE_DEPARTURE);
        assertTrue(arrivalList.size() == 2);
        assertTrue(arrivalList.get(1).getSegments().size() == 1);
        long hours = Duration.between(arrivalList.get(0).getSegments().get(0).getArrivalDate(), arrivalList.get(0).getSegments().get(0).getDepartureDate()).toHours();
        assertEquals(hours, 2);
    }

    @Test
    public void stay_two_more_hours() {
        List<Flight> twoHoursList = flightFilter.filter(EmRules.STAY_TWO_MORE_HOURS);
        assertTrue(twoHoursList.size() == 1);
        assertTrue(twoHoursList.get(0).getSegments().size() == 4);
        long minutes = IntStream.range(0, twoHoursList.get(0).getSegments().size() - 1)
                .mapToLong(i -> Duration.between(twoHoursList.get(0).getSegments().get(i).getArrivalDate(), twoHoursList.get(0).getSegments().get(i + 1).getDepartureDate()).toMinutes())
                .sum();
        assertEquals(minutes, 213);
    }
}
