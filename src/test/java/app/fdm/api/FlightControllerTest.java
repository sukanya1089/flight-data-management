package app.fdm.api;

import app.fdm.model.Flight;
import app.fdm.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightControllerTest {

    @Test
    public void testGetFlight() {
        FlightService flightService = mock(FlightService.class);
        FlightController controller = new FlightController(flightService);

        Flight mockFlight = new Flight();
        mockFlight.setId(10L);
        mockFlight.setAirline("TestLine");
        when(flightService.getFlightById(10L)).thenReturn(Optional.of(mockFlight));

        ResponseEntity<Flight> response = controller.getFlight(10L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("TestLine", response.getBody().getAirline());
    }
}
