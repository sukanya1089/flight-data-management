package app.fdm.repository;

import app.fdm.repository.model.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FlightRepository extends JpaRepository<FlightEntity, Long>, JpaSpecificationExecutor<FlightEntity> {

    // composite index (departureAirport, destinationAirport) improves the search speed
    List<FlightEntity> findByDepartureAirportAndDestinationAirport(String departureAirport, String destinationAirport);
}
