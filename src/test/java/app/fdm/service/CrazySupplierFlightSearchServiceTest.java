
package app.fdm.service;

import app.fdm.dto.FlightSearchRequest;
import app.fdm.dto.FlightSearchResponse;
import app.fdm.dto.FlightSearchResult;
import app.fdm.service.CrazySupplierFlightSearchService.CrazySupplierRequest;
import app.fdm.service.CrazySupplierFlightSearchService.CrazySupplierResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Disabled("TODO")
@ExtendWith(MockitoExtension.class)
class CrazySupplierFlightSearchServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    private CrazySupplierFlightSearchService searchService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        searchService = new CrazySupplierFlightSearchService(webClientBuilder);
        ReflectionTestUtils.setField(searchService, "crazySupplierUrl", "http://test.com");
    }

    @Test
    void findFlights_WhenSupplierReturnsResults_ShouldReturnMappedFlights() {
        // Arrange
        FlightSearchRequest request = createSearchRequest();
        CrazySupplierResult[] supplierResults = createSupplierResults();

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/flights")).thenReturn(requestBodySpec);
       // when(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CrazySupplierResult[].class))
                .thenReturn(Mono.just(supplierResults));

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        FlightSearchResult result = response.getResults().get(0);
        assertEquals("TestCarrier", result.getAirline());
        assertEquals(CrazySupplierFlightSearchService.SUPPLIER_NAME, result.getSupplier());
        assertEquals(new BigDecimal("110.00"), result.getFare());

        verify(webClient).post();
        verify(requestBodyUriSpec).uri("/flights");
        verify(requestBodySpec).bodyValue(any(CrazySupplierRequest.class));
        verify(requestBodySpec).retrieve();
        verify(responseSpec).bodyToMono(CrazySupplierResult[].class);
    }

    @Test
    void findFlights_WhenSupplierReturnsNull_ShouldReturnEmptyResponse() {
        // Arrange
        FlightSearchRequest request = createSearchRequest();

        WebClient.RequestBodyUriSpec requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/flights")).thenReturn(requestBodySpec);
        // when(requestBodySpec.bodyValue(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CrazySupplierResult[].class))
                .thenReturn(Mono.empty());

        // Act
        FlightSearchResponse response = searchService.findFlights(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.getResults().isEmpty());
    }

    @Test
    void mapToServiceEntity_ShouldMapAllFields() {
        // Arrange
        CrazySupplierResult supplierResult = new CrazySupplierResult();
        supplierResult.setCarrier("TestCarrier");
        supplierResult.setBasePrice(100.0);
        supplierResult.setTax(10.0);
        supplierResult.setDepartureAirportName("JFK");
        supplierResult.setArrivalAirportName("LAX");
        supplierResult.setOutboundDateTime("2025-06-22T10:00:00");
        supplierResult.setInboundDateTime("2025-06-22T15:00:00");

        // Act
        FlightSearchResult result = CrazySupplierFlightSearchService.mapToServiceEntity(supplierResult);

        // Assert
        assertNotNull(result);
        assertEquals("TestCarrier", result.getAirline());
        assertEquals(CrazySupplierFlightSearchService.SUPPLIER_NAME, result.getSupplier());
        assertEquals(new BigDecimal("110.00"), result.getFare());
        assertEquals("JFK", result.getDepartureAirport());
        assertEquals("LAX", result.getDestinationAirport());
        assertNotNull(result.getDepartureTime());
        assertNotNull(result.getArrivalTime());
    }

    @Test
    void asCetDateTime_ShouldConvertToCorrectFormat() {
        // Arrange
        ZonedDateTime utcDateTime = ZonedDateTime.of(
                2025, 6, 22, 10, 0, 0, 0,
                ZoneId.of("UTC")
        );

        // Act
        String result = CrazySupplierFlightSearchService.asCetDateTime(utcDateTime);

        // Assert
        assertNotNull(result);
        assertEquals("2025-06-22", result);
    }

    @Test
    void toUtcDateTime_ShouldConvertToUtc() {
        // Arrange
        String cetDateTime = "2025-06-22T10:00:00";

        // Act
        ZonedDateTime result = CrazySupplierFlightSearchService.toUtcDateTime(cetDateTime);

        // Assert
        assertNotNull(result);
        assertEquals(ZoneId.of("UTC"), result.getZone());
    }

    private FlightSearchRequest createSearchRequest() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFrom("JFK");
        request.setTo("LAX");
        request.setDepartureTime(ZonedDateTime.now());
        request.setArrivalTime(ZonedDateTime.now().plusHours(5));
        return request;
    }

    private CrazySupplierResult[] createSupplierResults() {
        CrazySupplierResult result = new CrazySupplierResult();
        result.setCarrier("TestCarrier");
        result.setBasePrice(100.0);
        result.setTax(10.0);
        result.setDepartureAirportName("JFK");
        result.setArrivalAirportName("LAX");
        result.setOutboundDateTime("2025-06-22T10:00:00");
        result.setInboundDateTime("2025-06-22T15:00:00");
        return new CrazySupplierResult[]{result};
    }
}