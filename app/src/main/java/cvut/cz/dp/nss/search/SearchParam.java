package cvut.cz.dp.nss.search;

/**
 * Parametry SEARCH requestu.
 *
 * @author jakubchalupa
 * @since 22.04.17
 */
public enum SearchParam {

    TIME_TABLE("timeTable"),
    STOP_FROM("stopFromName"),
    STOP_TO("stopToName"),
    STOP_THROUGH("stopThroughName"),
    DATE("date"),
    MAX_TRANSFERS("maxTransfers"),
    WITH_WHEELCHAIR("withWheelChair"),
    BY_ARRIVAL("byArrival");

    private final String value;

    SearchParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}