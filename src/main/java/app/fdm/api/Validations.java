package app.fdm.api;

import app.fdm.dto.Flight;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Validations {

    public static void validateAirportCode(String airportCode, String fieldName) {
        if (airportCode == null || airportCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " cannot be null or blank");
        }
        if (airportCode.length() != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " must be 3 characters long");
        }
    }

    public static void validateFlight(Flight flight) {
        if (flight.getAirline() == null || flight.getAirline().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Airline cannot be null or blank");
        }
        if (flight.getSupplier() == null || flight.getSupplier().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier cannot be null or blank");
        }
        if (flight.getFare() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fare cannot be null");
        } else if (flight.getFare().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fare cannot be negative");
        }
        validateAirportCode(flight.getDepartureAirport(), "DepartureAirport");
        validateAirportCode(flight.getDestinationAirport(), "DestinationAirport");
        if (flight.getDepartureTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DepartureTime cannot be null or blank");
        }
        if (flight.getArrivalTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ArrivalTime cannot be null or blank");
        }
    }

    public static void validateDateTime(String dateTime, String fieldName) {
        if (dateTime == null || dateTime.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " cannot be null or blank");
        }
        try {
            java.time.ZonedDateTime.parse(dateTime);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, fieldName + " is not a valid date-time");
        }
    }
}
