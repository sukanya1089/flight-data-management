
package app.fdm.api;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.dto.FlightSearchResult;
import app.fdm.service.CombinedFlightSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlightSearchControllerTest {

    @Mock
    private CombinedFlightSearchService searchService;

    private FlightSearchController controller;

    @BeforeEach
    void setUp() {
        controller = new FlightSearchController(searchService);
    }

    @Test
    void searchFlights_WithValidParameters_ShouldReturnFlights() {
        // Arrange
        String airline = "TestAirline";
        String from = "JFK";
        String to = "LAX";
        String departureTime = "2025-06-22T10:00:00Z";
        String arrivalTime = "2025-06-22T15:00:00Z";

        FlightSearchResponse mockResponse = createMockResponse();
        when(searchService.findFlights(any(FlightSearchRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<FlightSearchResponse> response = controller.searchFlights(
                airline, from, to, departureTime, arrivalTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getResults().size());

        verify(searchService).findFlights(any(FlightSearchRequest.class));
    }

    @Test
    void searchFlights_WithNoParameters_ShouldReturnFlights() {
        // Arrange
        FlightSearchResponse mockResponse = createMockResponse();
        when(searchService.findFlights(any(FlightSearchRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<FlightSearchResponse> response = controller.searchFlights(
                null, null, null, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(searchService).findFlights(any(FlightSearchRequest.class));
    }

    @Test
    void searchFlights_WithInvalidDateTime_ShouldReturnBadRequest() {
        // Act
        ResponseEntity<FlightSearchResponse> response = controller.searchFlights(
                "TestAirline", "JFK", "LAX", "invalid-date", "invalid-date");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void searchFlights_WithValidDepartureTimeOnly_ShouldReturnFlights() {
        // Arrange
        String departureTime = "2025-06-22T10:00:00Z";
        FlightSearchResponse mockResponse = createMockResponse();
        when(searchService.findFlights(any(FlightSearchRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<FlightSearchResponse> response = controller.searchFlights(
                null, null, null, departureTime, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(searchService).findFlights(any(FlightSearchRequest.class));
    }

    @Test
    void searchFlights_WithValidArrivalTimeOnly_ShouldReturnFlights() {
        // Arrange
        String arrivalTime = "2025-06-22T15:00:00Z";
        FlightSearchResponse mockResponse = createMockResponse();
        when(searchService.findFlights(any(FlightSearchRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<FlightSearchResponse> response = controller.searchFlights(
                null, null, null, null, arrivalTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(searchService).findFlights(any(FlightSearchRequest.class));
    }

    private FlightSearchResponse createMockResponse() {
        FlightSearchResult result = new FlightSearchResult();
        result.setAirline("TestAirline");
        result.setSupplier("TestSupplier");
        result.setFare(new BigDecimal("199.99"));
        result.setDepartureAirport("JFK");
        result.setDestinationAirport("LAX");
        result.setDepartureTime(ZonedDateTime.parse("2025-06-22T10:00:00Z"));
        result.setArrivalTime(ZonedDateTime.parse("2025-06-22T15:00:00Z"));

        return new FlightSearchResponse(List.of(result));
    }
}