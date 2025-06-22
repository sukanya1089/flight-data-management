package app.fdm.service;

import app.fdm.dto.Flight;
import app.fdm.repository.FlightRepository;
import app.fdm.repository.model.FlightEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FlightService {


    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Flight saveFlight(Flight flight) {
        return fromFlightEntity(flightRepository.save(toFlightEntity(flight)));
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id).map(FlightService::fromFlightEntity);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public Flight updateFlight(Long id, Flight updatedFlight) {
        FlightEntity existing = flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        FlightEntity updatedFlightEntity = toFlightEntity(updatedFlight);
        updatedFlightEntity.setId(existing.getId());
        return fromFlightEntity(flightRepository.save(updatedFlightEntity));
    }

    static FlightEntity toFlightEntity(Flight flight) {
        if (flight == null) {
            return null;
        }
        FlightEntity entity = new FlightEntity();
        entity.setId(flight.getId());
        entity.setAirline(flight.getAirline());
        entity.setSupplier(flight.getSupplier());
        entity.setFare(flight.getFare());
        entity.setDepartureAirport(flight.getDepartureAirport());
        entity.setDestinationAirport(flight.getDestinationAirport());
        entity.setDepartureTime(flight.getDepartureTime());
        entity.setArrivalTime(flight.getArrivalTime());
        return entity;

    }

    static Flight fromFlightEntity(FlightEntity flightEntity) {
        if (flightEntity == null) {
            return null;
        }
        Flight flight = new Flight();
        flight.setId(flightEntity.getId());
        flight.setAirline(flightEntity.getAirline());
        flight.setSupplier(flightEntity.getSupplier());
        flight.setFare(flightEntity.getFare());
        flight.setDepartureAirport(flightEntity.getDepartureAirport());
        flight.setDestinationAirport(flightEntity.getDestinationAirport());
        flight.setDepartureTime(flightEntity.getDepartureTime());
        flight.setArrivalTime(flightEntity.getArrivalTime());
        return flight;
    }

}
