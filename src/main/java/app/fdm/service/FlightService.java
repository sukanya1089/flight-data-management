package app.fdm.service;

import app.fdm.dto.*;
import app.fdm.model.Flight;
import app.fdm.repository.FlightRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final WebClient webClient;

    @Value("${crazy-supplier.url}")
    private String crazySupplierUrl;

    public FlightService(FlightRepository flightRepository, WebClient.Builder webClientBuilder) {
        this.flightRepository = flightRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.crazy-supplier.com").build();
    }

    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }

    public Flight updateFlight(Long id, Flight updatedFlight) {
        Flight existing = flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        updatedFlight.setId(id);
        return flightRepository.save(updatedFlight);
    }

    public List<FlightResponse> searchFlights(FlightSearchRequest request) {
        // Step 1: Fetch local flights
        List<Flight> dbFlights = flightRepository.findAll().stream()
                .filter(f -> filterMatch(f, request))
                .collect(Collectors.toList());

        // Step 2: Fetch CrazySupplier flights
        List<FlightResponse> crazyFlights = getCrazySupplierFlights(request);

        // Step 3: Combine and return
        List<FlightResponse> results = new ArrayList<>();
        dbFlights.forEach(flight -> {
            FlightResponse res = new FlightResponse();
            res.setAirline(flight.getAirline());
            res.setSupplier(flight.getSupplier());
            res.setFare(flight.getFare());
            res.setDepartureAirport(flight.getDepartureAirport());
            res.setDestinationAirport(flight.getDestinationAirport());
            res.setDepartureTime(flight.getDepartureTime());
            res.setArrivalTime(flight.getArrivalTime());
            results.add(res);
        });
        results.addAll(crazyFlights);
        return results;
    }

    private boolean filterMatch(Flight f, FlightSearchRequest r) {
        return (r.getAirline() == null || f.getAirline().equalsIgnoreCase(r.getAirline())) &&
                (r.getFrom() == null || f.getDepartureAirport().equalsIgnoreCase(r.getFrom())) &&
                (r.getTo() == null || f.getDestinationAirport().equalsIgnoreCase(r.getTo())) &&
                (r.getDepartureTime() == null || !f.getDepartureTime().isBefore(r.getDepartureTime())) &&
                (r.getArrivalTime() == null || !f.getArrivalTime().isAfter(r.getArrivalTime()));
    }

    private List<FlightResponse> getCrazySupplierFlights(FlightSearchRequest request) {
        try {
            CrazySupplierRequest csReq = new CrazySupplierRequest();
            csReq.setFrom(request.getFrom());
            csReq.setTo(request.getTo());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("CET"));
            csReq.setOutboundDate(now.format(dateFormatter));
            csReq.setInboundDate(now.plusDays(2).format(dateFormatter));

            CrazySupplierResponse[] response = webClient.post()
                    .uri("/flights")
                    .bodyValue(csReq)
                    .retrieve()
                    .bodyToMono(CrazySupplierResponse[].class)
                    .block();

            if (response == null) return List.of();

            return Arrays.stream(response).map(c -> {
                FlightResponse res = new FlightResponse();
                res.setAirline(c.getCarrier());
                res.setSupplier("CrazySupplier");
                res.setFare(BigDecimal.valueOf(c.getBasePrice() + c.getTax()));
                res.setDepartureAirport(c.getDepartureAirportName());
                res.setDestinationAirport(c.getArrivalAirportName());

                LocalDateTime outbound = LocalDateTime.parse(c.getOutboundDateTime());
                LocalDateTime inbound = LocalDateTime.parse(c.getInboundDateTime());

                ZonedDateTime depZoned = outbound.atZone(ZoneId.of("CET")).withZoneSameInstant(ZoneOffset.UTC);
                ZonedDateTime arrZoned = inbound.atZone(ZoneId.of("CET")).withZoneSameInstant(ZoneOffset.UTC);

                res.setDepartureTime(depZoned);
                res.setArrivalTime(arrZoned);

                return res;
            }).collect(Collectors.toList());

        } catch (Exception ex) {
            // Log properly in production code
            return List.of();
        }
    }
}
