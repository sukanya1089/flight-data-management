
package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.dto.FlightSearchResult;
import app.fdm.repository.FlightRepository;
import app.fdm.repository.model.FlightEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalFlightSearchServiceTest {

    @Mock
    private FlightRepository flightRepository;

    private LocalFlightSearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new LocalFlightSearchService(flightRepository);
    }

    @Test
    void findFlights_WhenNoFlightsFound_ShouldReturnEmptyResponse() {
        // Arrange
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFrom("JFK");
        request.setTo("LAX");
        when(flightRepository.findByDepartureAirportAndDestinationAirport("JFK", "LAX"))
                .thenReturn(Collections.emptyList());

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getResults().isEmpty());
        verify(flightRepository).findByDepartureAirportAndDestinationAirport("JFK", "LAX");
    }

    @Test
    void findFlights_WhenFlightsExist_ShouldReturnMatchingFlights() {
        // Arrange
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFrom("JFK");
        request.setTo("LAX");

        FlightEntity flight = createSampleFlightEntity();
        when(flightRepository.findByDepartureAirportAndDestinationAirport("JFK", "LAX"))
                .thenReturn(List.of(flight));

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        FlightSearchResult result = response.getResults().get(0);
        assertEquals(flight.getAirline(), result.getAirline());
        assertEquals(flight.getDepartureAirport(), result.getDepartureAirport());
        verify(flightRepository).findByDepartureAirportAndDestinationAirport("JFK", "LAX");
    }

    @Test
    void findFlights_WithAirlineFilter_ShouldReturnOnlyMatchingAirlines() {
        // Arrange
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFrom("JFK");
        request.setTo("LAX");
        request.setAirline("Delta");

        FlightEntity deltaflight = createSampleFlightEntity();
        deltaflight.setAirline("Delta");
        FlightEntity unitedFlight = createSampleFlightEntity();
        unitedFlight.setAirline("United");

        when(flightRepository.findByDepartureAirportAndDestinationAirport("JFK", "LAX"))
                .thenReturn(List.of(deltaflight, unitedFlight));

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        assertEquals("Delta", response.getResults().get(0).getAirline());
    }

    @Test
    void findFlights_WithTimeFilters_ShouldReturnMatchingFlights() {
        // Arrange
        ZonedDateTime now = ZonedDateTime.now();
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFrom("JFK");
        request.setTo("LAX");
        request.setDepartureTime(now);
        request.setArrivalTime(now.plusHours(6));

        FlightEntity validFlight = createSampleFlightEntity();
        validFlight.setDepartureTime(now.plusHours(1));
        validFlight.setArrivalTime(now.plusHours(5));

        FlightEntity invalidFlight = createSampleFlightEntity();
        invalidFlight.setDepartureTime(now.minusHours(1));
        invalidFlight.setArrivalTime(now.plusHours(7));

        when(flightRepository.findByDepartureAirportAndDestinationAirport("JFK", "LAX"))
                .thenReturn(List.of(validFlight, invalidFlight));

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        assertEquals(validFlight.getDepartureTime(), response.getResults().get(0).getDepartureTime());
    }

    @Test
    void filterMatch_WithNullCriteria_ShouldReturnTrue() {
        // Arrange
        FlightEntity flight = createSampleFlightEntity();
        FlightSearchRequest request = new FlightSearchRequest();

        // Act
        boolean result = LocalFlightSearchService.filterMatch(flight, request);

        // Assert
        assertTrue(result);
    }

    @Test
    void filterMatch_WithNonMatchingCriteria_ShouldReturnFalse() {
        // Arrange
        FlightEntity flight = createSampleFlightEntity();
        FlightSearchRequest request = new FlightSearchRequest();
        request.setAirline("NonExistentAirline");

        // Act
        boolean result = LocalFlightSearchService.filterMatch(flight, request);

        // Assert
        assertFalse(result);
    }

    @Test
    void mapToServiceEntity_ShouldMapAllFields() {
        // Arrange
        FlightEntity flight = createSampleFlightEntity();

        // Act
        FlightSearchResult result = LocalFlightSearchService.mapToServiceEntity(flight);

        // Assert
        assertNotNull(result);
        assertEquals(flight.getAirline(), result.getAirline());
        assertEquals(flight.getSupplier(), result.getSupplier());
        assertEquals(flight.getFare(), result.getFare());
        assertEquals(flight.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(flight.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(flight.getDepartureTime(), result.getDepartureTime());
        assertEquals(flight.getArrivalTime(), result.getArrivalTime());
    }

    private FlightEntity createSampleFlightEntity() {
        FlightEntity entity = new FlightEntity();
        entity.setId(1L);
        entity.setAirline("Sample Airline");
        entity.setSupplier("Sample Supplier");
        entity.setFare(new BigDecimal("199.99"));
        entity.setDepartureAirport("JFK");
        entity.setDestinationAirport("LAX");
        entity.setDepartureTime(ZonedDateTime.now());
        entity.setArrivalTime(ZonedDateTime.now().plusHours(5));
        return entity;
    }
}