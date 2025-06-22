package app.fdm.api;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.service.CombinedFlightSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flights/search")
public class FlightSearchController {

    private final CombinedFlightSearchService combinedFlightSearchService;

    public FlightSearchController(CombinedFlightSearchService combinedFlightSearchService) {
        this.combinedFlightSearchService = combinedFlightSearchService;
    }

    @GetMapping
    public ResponseEntity<FlightSearchResponse> searchFlights(
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

        return ResponseEntity.ok(combinedFlightSearchService.findFlights(req));
    }
}
