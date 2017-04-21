package cvut.cz.dp.nss.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TripWrapper implements Serializable {

    private String headSign;

    private Integer wheelChairCode;

    private RouteWrapper route;

    public String getHeadSign() {
        return headSign;
    }

    public void setHeadSign(String headSign) {
        this.headSign = headSign;
    }

    public Integer getWheelChairCode() {
        return wheelChairCode;
    }

    public void setWheelChairCode(Integer wheelChairCode) {
        this.wheelChairCode = wheelChairCode;
    }

    public RouteWrapper getRoute() {
        return route;
    }

    public void setRoute(RouteWrapper route) {
        this.route = route;
    }

}
