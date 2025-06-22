package app.fdm.api;

import app.fdm.dto.FlightResponse;
import app.fdm.dto.FlightSearchRequest;
import app.fdm.model.Flight;
import app.fdm.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.saveFlight(flight));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight flight) {
        return ResponseEntity.ok(flightService.updateFlight(id, flight));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlight(@PathVariable Long id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightResponse>> searchFlights(
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) String arrivalTime
    ) {
        FlightSearchRequest req = new FlightSearchRequest();
        req.setAirline(airline);
        req.setFrom(from);
        req.setTo(to);

        try {
            if (departureTime != null)
                req.setDepartureTime(java.time.ZonedDateTime.parse(departureTime));
            if (arrivalTime != null)
                req.setArrivalTime(java.time.ZonedDateTime.parse(arrivalTime));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(flightService.searchFlights(req));
    }
}
