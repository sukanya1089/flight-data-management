package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.dto.FlightSearchResult;
import app.fdm.repository.FlightRepository;
import app.fdm.repository.model.FlightEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalFlightSearchService implements FlightSearchService {

    private final FlightRepository flightRepository;

    public LocalFlightSearchService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public FlightSearchResponse findFlights(FlightSearchRequest search) {
        List<FlightSearchResult> dbFlights = flightRepository.findByDepartureAirportAndDestinationAirport(search.getFrom(), search.getTo()).stream()
                .filter(f -> filterMatch(f, search))
                .map(LocalFlightSearchService::mapToServiceEntity)
                .toList();
        return new FlightSearchResponse(dbFlights);
    }


    static boolean filterMatch(FlightEntity flight, FlightSearchRequest r) {
        return (r.getAirline() == null || flight.getAirline().equalsIgnoreCase(r.getAirline())) &&
                (r.getFrom() == null || flight.getDepartureAirport().equalsIgnoreCase(r.getFrom())) &&
                (r.getTo() == null || flight.getDestinationAirport().equalsIgnoreCase(r.getTo())) &&
                (r.getDepartureTime() == null || !flight.getDepartureTime().isBefore(r.getDepartureTime())) &&
                (r.getArrivalTime() == null || !flight.getArrivalTime().isAfter(r.getArrivalTime()));
    }

    static FlightSearchResult mapToServiceEntity(FlightEntity flight) {
        FlightSearchResult sr = new FlightSearchResult();
        sr.setAirline(flight.getAirline());
        sr.setSupplier(flight.getSupplier());
        sr.setFare(flight.getFare());
        sr.setDepartureAirport(flight.getDepartureAirport());
        sr.setDestinationAirport(flight.getDestinationAirport());
        sr.setArrivalTime(flight.getArrivalTime());
        sr.setDepartureTime(flight.getDepartureTime());
        return sr;
    }
}
