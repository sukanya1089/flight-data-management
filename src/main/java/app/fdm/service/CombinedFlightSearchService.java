package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class CombinedFlightSearchService implements FlightSearchService {

    private static final Logger logger = LoggerFactory.getLogger(CombinedFlightSearchService.class);

    private final LocalFlightSearchService localFlightSearchService;
    private final CrazySupplierFlightSearchService crazySupplierFlightSearchService;

    public CombinedFlightSearchService(LocalFlightSearchService localFlightSearchService, CrazySupplierFlightSearchService crazySupplierFlightSearchService) {
        this.localFlightSearchService = localFlightSearchService;
        this.crazySupplierFlightSearchService = crazySupplierFlightSearchService;
    }

    @Override
    public FlightSearchResponse findFlights(FlightSearchRequest flightSearchRequest) {
        List<FlightSearchResponse> results = Stream.of(localFlightSearchService, crazySupplierFlightSearchService)
                .map(r -> safeSearch(r, flightSearchRequest))
                .filter(Objects::nonNull)
                .toList();
        return FlightSearchResponse.combine(results);
    }

    static FlightSearchResponse safeSearch(FlightSearchService flightSearchService,  FlightSearchRequest flightSearchRequest) {
        try {
            return flightSearchService.findFlights(flightSearchRequest);
        } catch (Exception e) {
            logger.warn("Error while trying to search for flights, ignoring", e);
            return null;
        }
    }
}
