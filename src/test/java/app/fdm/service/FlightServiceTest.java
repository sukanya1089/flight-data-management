package app.fdm.service;

import app.fdm.dto.Flight;
import app.fdm.repository.FlightRepository;
import app.fdm.repository.model.FlightEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    private FlightService flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightService(flightRepository);
    }

    @Test
    void saveFlight_ShouldSaveAndReturnFlight() {
        // Arrange
        Flight inputFlight = createSampleFlight();
        FlightEntity savedEntity = createSampleFlightEntity();
        when(flightRepository.save(any(FlightEntity.class))).thenReturn(savedEntity);

        // Act
        Flight result = flightService.saveFlight(inputFlight);

        // Assert
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertEquals(savedEntity.getAirline(), result.getAirline());
        assertEquals(savedEntity.getFare(), result.getFare());
        verify(flightRepository).save(any(FlightEntity.class));
    }

    @Test
    void getFlightById_WhenFlightExists_ShouldReturnFlight() {
        // Arrange
        Long id = 1L;
        FlightEntity entity = createSampleFlightEntity();
        when(flightRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        Optional<Flight> result = flightService.getFlightById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals(entity.getAirline(), result.get().getAirline());
        verify(flightRepository).findById(id);
    }

    @Test
    void getFlightById_WhenFlightDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        Long id = 1L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Flight> result = flightService.getFlightById(id);

        // Assert
        assertTrue(result.isEmpty());
        verify(flightRepository).findById(id);
    }

    @Test
    void deleteFlight_ShouldDeleteFlight() {
        // Arrange
        Long id = 1L;
        doNothing().when(flightRepository).deleteById(id);

        // Act
        flightService.deleteFlight(id);

        // Assert
        verify(flightRepository).deleteById(id);
    }

    @Test
    void updateFlight_WhenFlightExists_ShouldUpdateAndReturnFlight() {
        // Arrange
        Long id = 1L;
        Flight updatedFlight = createSampleFlight();
        FlightEntity existingEntity = createSampleFlightEntity();
        FlightEntity updatedEntity = createSampleFlightEntity();
        updatedEntity.setAirline("Updated Airline");

        when(flightRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(flightRepository.save(any(FlightEntity.class))).thenReturn(updatedEntity);

        // Act
        Flight result = flightService.updateFlight(id, updatedFlight);

        // Assert
        assertNotNull(result);
        assertEquals(updatedEntity.getAirline(), result.getAirline());
        verify(flightRepository).findById(id);
        verify(flightRepository).save(any(FlightEntity.class));
    }

    @Test
    void updateFlight_WhenFlightDoesNotExist_ShouldThrowException() {
        // Arrange
        Long id = 1L;
        Flight updatedFlight = createSampleFlight();
        when(flightRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> flightService.updateFlight(id, updatedFlight));
        verify(flightRepository).findById(id);
        verify(flightRepository, never()).save(any(FlightEntity.class));
    }

    @Test
    void toFlightEntity_ShouldConvertFlightToEntity() {
        // Arrange
        Flight flight = createSampleFlight();

        // Act
        FlightEntity result = FlightService.toFlightEntity(flight);

        // Assert
        assertNotNull(result);
        assertEquals(flight.getId(), result.getId());
        assertEquals(flight.getAirline(), result.getAirline());
        assertEquals(flight.getSupplier(), result.getSupplier());
        assertEquals(flight.getFare(), result.getFare());
        assertEquals(flight.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(flight.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(flight.getDepartureTime(), result.getDepartureTime());
        assertEquals(flight.getArrivalTime(), result.getArrivalTime());
    }

    @Test
    void fromFlightEntity_ShouldConvertEntityToFlight() {
        // Arrange
        FlightEntity entity = createSampleFlightEntity();

        // Act
        Flight result = FlightService.fromFlightEntity(entity);

        // Assert
        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getAirline(), result.getAirline());
        assertEquals(entity.getSupplier(), result.getSupplier());
        assertEquals(entity.getFare(), result.getFare());
        assertEquals(entity.getDepartureAirport(), result.getDepartureAirport());
        assertEquals(entity.getDestinationAirport(), result.getDestinationAirport());
        assertEquals(entity.getDepartureTime(), result.getDepartureTime());
        assertEquals(entity.getArrivalTime(), result.getArrivalTime());
    }

    private Flight createSampleFlight() {
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAirline("Sample Airline");
        flight.setSupplier("Sample Supplier");
        flight.setFare(new BigDecimal("199.99"));
        flight.setDepartureAirport("JFK");
        flight.setDestinationAirport("LAX");
        flight.setDepartureTime(ZonedDateTime.now());
        flight.setArrivalTime(ZonedDateTime.now().plusHours(5));
        return flight;
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