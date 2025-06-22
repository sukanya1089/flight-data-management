package app.fdm.service;

import app.fdm.model.Flight;
import app.fdm.repository.FlightRepository;
import app.fdm.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FlightServiceTest {

    private FlightRepository flightRepository;
    private FlightService flightService;

    @BeforeEach
    public void setup() {
        flightRepository = mock(FlightRepository.class);
        WebClient.Builder mockBuilder = WebClient.builder(); // won't actually call external API in test
        flightService = new FlightService(flightRepository, mockBuilder);
    }

    @Test
    public void testSaveFlight() {
        Flight flight = new Flight();
        flight.setAirline("TestAir");
        flight.setSupplier("Internal");
        flight.setFare(BigDecimal.valueOf(100));
        flight.setDepartureAirport("JFK");
        flight.setDestinationAirport("LHR");
        flight.setDepartureTime(ZonedDateTime.now());
        flight.setArrivalTime(ZonedDateTime.now().plusHours(6));

        when(flightRepository.save(flight)).thenReturn(flight);

        Flight saved = flightService.saveFlight(flight);
        assertEquals("TestAir", saved.getAirline());
        verify(flightRepository, times(1)).save(flight);
    }

    @Test
    public void testGetFlightById() {
        Flight flight = new Flight();
        flight.setId(1L);
        when(flightRepository.findById(1L)).thenReturn(Optional.of(flight));

        Optional<Flight> result = flightService.getFlightById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    public void testDeleteFlight() {
        flightService.deleteFlight(1L);
        verify(flightRepository, times(1)).deleteById(1L);
    }
}
