package cvut.cz.dp.nss.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * @author jakubchalupa
 * @since 24.02.17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResultWrapper implements Serializable {

    private String departureDate;

    private String arrivalDate;

    private Long travelTime;

    private List<SearchStopTimeWrapper> stopTimes;

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Long getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Long travelTime) {
        this.travelTime = travelTime;
    }

    public List<SearchStopTimeWrapper> getStopTimes() {
        return stopTimes;
    }

    public void setStopTimes(List<SearchStopTimeWrapper> stopTimes) {
        this.stopTimes = stopTimes;
    }
}
