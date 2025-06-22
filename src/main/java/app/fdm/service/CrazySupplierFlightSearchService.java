package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.dto.FlightSearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class CrazySupplierFlightSearchService implements FlightSearchService {

    public static final String SUPPLIER_NAME = "CrazySupplier";

    static class CrazySupplierRequest {
        private String from;
        private String to;
        private String outboundDate;
        private String inboundDate;

        // Getters and Setters

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getOutboundDate() {
            return outboundDate;
        }

        public void setOutboundDate(String outboundDate) {
            this.outboundDate = outboundDate;
        }

        public String getInboundDate() {
            return inboundDate;
        }

        public void setInboundDate(String inboundDate) {
            this.inboundDate = inboundDate;
        }
    }

    static class CrazySupplierResult {
        private String carrier;
        private double basePrice;
        private double tax;
        private String departureAirportName;
        private String arrivalAirportName;
        private String outboundDateTime;
        private String inboundDateTime;

        // Getters and Setters

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public double getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(double basePrice) {
            this.basePrice = basePrice;
        }

        public double getTax() {
            return tax;
        }

        public void setTax(double tax) {
            this.tax = tax;
        }

        public String getDepartureAirportName() {
            return departureAirportName;
        }

        public void setDepartureAirportName(String departureAirportName) {
            this.departureAirportName = departureAirportName;
        }

        public String getArrivalAirportName() {
            return arrivalAirportName;
        }

        public void setArrivalAirportName(String arrivalAirportName) {
            this.arrivalAirportName = arrivalAirportName;
        }

        public String getOutboundDateTime() {
            return outboundDateTime;
        }

        public void setOutboundDateTime(String outboundDateTime) {
            this.outboundDateTime = outboundDateTime;
        }

        public String getInboundDateTime() {
            return inboundDateTime;
        }

        public void setInboundDateTime(String inboundDateTime) {
            this.inboundDateTime = inboundDateTime;
        }
    }

    private final WebClient webClient;

    @Value("${crazy-supplier.url}")
    private String crazySupplierUrl;

    public CrazySupplierFlightSearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(crazySupplierUrl).build();
    }


    @Override
    public FlightSearchResponse findFlights(FlightSearchRequest searchRequest) {

        CrazySupplierRequest csReq = new CrazySupplierRequest();
        csReq.setFrom(searchRequest.getFrom());
        csReq.setTo(searchRequest.getTo());
        csReq.setOutboundDate(asCetDateTime(searchRequest.getDepartureTime()));
        csReq.setInboundDate(asCetDateTime(searchRequest.getDepartureTime()));

        CrazySupplierResult[] response = webClient.post()
                .uri("/flights")
                .bodyValue(csReq)
                .retrieve()
                .bodyToMono(CrazySupplierResult[].class)
                .block();

        if (response == null) return new FlightSearchResponse(List.of());

        List<FlightSearchResult> flights = Arrays.stream(response).map(CrazySupplierFlightSearchService::mapToServiceEntity).toList();
        return new FlightSearchResponse(flights);

    }

    static FlightSearchResult mapToServiceEntity(CrazySupplierResult flight) {
        FlightSearchResult res = new FlightSearchResult();
        res.setAirline(flight.getCarrier());
        res.setSupplier(SUPPLIER_NAME);
        res.setFare(BigDecimal.valueOf(flight.getBasePrice() + flight.getTax()));
        res.setDepartureAirport(flight.getDepartureAirportName());
        res.setDestinationAirport(flight.getArrivalAirportName());
        res.setDepartureTime(toUtcDateTime(flight.getOutboundDateTime()));
        res.setArrivalTime(toUtcDateTime(flight.getInboundDateTime()));
        return res;
    }

    static String asCetDateTime(ZonedDateTime zonedDateTime) {
        ZonedDateTime cetZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("CET"));
        return DateTimeFormatter.ISO_LOCAL_DATE.format(cetZonedDateTime);
    }

    static ZonedDateTime toUtcDateTime(String zonedDateTime) {
        return LocalDateTime.parse(zonedDateTime).atZone(ZoneId.of("CET")).withZoneSameInstant(ZoneOffset.UTC);
    }

}
