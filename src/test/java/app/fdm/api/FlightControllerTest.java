package app.fdm.api;

import app.fdm.dto.Flight;
import app.fdm.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightControllerTest {

    @Mock
    private FlightService flightService;

    private FlightController flightController;

    @BeforeEach
    void setUp() {
        flightController = new FlightController(flightService);
    }

    @Test
    void getFlight_WhenFlightExists_ShouldReturnFlight() {
        // Arrange
        Flight mockFlight = createSampleFlight();
        when(flightService.getFlightById(1L)).thenReturn(Optional.of(mockFlight));

        // Act
        ResponseEntity<Flight> response = flightController.getFlight(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockFlight.getId(), response.getBody().getId());
        assertEquals(mockFlight.getAirline(), response.getBody().getAirline());
        verify(flightService).getFlightById(1L);
    }

    @Test
    void getFlight_WhenFlightDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(flightService.getFlightById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Flight> response = flightController.getFlight(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(flightService).getFlightById(1L);
    }

    @Test
    void createFlight_ShouldReturnCreatedFlight() {
        // Arrange
        Flight inputFlight = createSampleFlight();
        when(flightService.saveFlight(any(Flight.class))).thenReturn(inputFlight);

        // Act
        ResponseEntity<Flight> response = flightController.createFlight(inputFlight);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(inputFlight.getAirline(), response.getBody().getAirline());
        verify(flightService).saveFlight(inputFlight);
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldReturnUpdatedFlight() {
        // Arrange
        Long flightId = 1L;
        Flight updatedFlight = createSampleFlight();
        when(flightService.updateFlight(eq(flightId), any(Flight.class))).thenReturn(updatedFlight);

        // Act
        ResponseEntity<Flight> response = flightController.updateFlight(flightId, updatedFlight);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedFlight.getAirline(), response.getBody().getAirline());
        verify(flightService).updateFlight(flightId, updatedFlight);
    }

    @Test
    void updateFlight_WhenFlightDoesNotExist_ShouldThrowException() {
        // Arrange
        Long flightId = 1L;
        Flight updatedFlight = createSampleFlight();
        when(flightService.updateFlight(eq(flightId), any(Flight.class)))
                .thenThrow(new RuntimeException("Not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                flightController.updateFlight(flightId, updatedFlight)
        );
        verify(flightService).updateFlight(flightId, updatedFlight);
    }

    @Test
    void deleteFlight_ShouldReturnNoContent() {
        // Arrange
        Long flightId = 1L;
        doNothing().when(flightService).deleteFlight(flightId);

        // Act
        ResponseEntity<Void> response = flightController.deleteFlight(flightId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(flightService).deleteFlight(flightId);
    }

    private Flight createSampleFlight() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAirline("Test Airline");
        flight.setSupplier("Test Supplier");
        flight.setFare(new BigDecimal("199.99"));
        flight.setDepartureAirport("JFK");
        flight.setDestinationAirport("LAX");
        flight.setDepartureTime(ZonedDateTime.now());
        flight.setArrivalTime(ZonedDateTime.now().plusHours(5));
        return flight;
    }
}