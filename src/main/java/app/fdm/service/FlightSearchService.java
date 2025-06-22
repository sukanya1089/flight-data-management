package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;

public interface FlightSearchService {

    FlightSearchResponse findFlights(FlightSearchRequest flightSearchRequest);
}
