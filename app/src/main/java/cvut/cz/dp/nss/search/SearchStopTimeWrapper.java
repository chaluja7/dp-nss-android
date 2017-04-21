package cvut.cz.dp.nss.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author jakubchalupa
 * @since 24.02.17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchStopTimeWrapper implements Serializable {

    /**
     * stanice
     */
    private StopWrapper stop;

    private TripWrapper trip;

    private String arrival;

    private String departure;

    public StopWrapper getStop() {
        return stop;
    }

    public void setStop(StopWrapper stop) {
        this.stop = stop;
    }

    public TripWrapper getTrip() {
        return trip;
    }

    public void setTrip(TripWrapper trip) {
        this.trip = trip;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }
}
