package app.fdm.dto;

import java.util.ArrayList;
import java.util.List;

public class FlightSearchResponse {

    private List<FlightSearchResult> results;

    public FlightSearchResponse() {
        // empty
    }

    public FlightSearchResponse(List<FlightSearchResult> results) {
        this.results = results;
    }

    public List<FlightSearchResult> getResults() {
        return results;
    }

    public void setResults(List<FlightSearchResult> results) {
        this.results = results;
    }

    public static FlightSearchResponse combine(List<FlightSearchResponse> responses) {
        List<FlightSearchResult> combinedResults = new ArrayList<>();
        for (FlightSearchResponse response : responses) {
            if (response != null && response.getResults() != null) {
                combinedResults.addAll(response.getResults());
            }
        }
        return new  FlightSearchResponse(combinedResults);
    }

}
