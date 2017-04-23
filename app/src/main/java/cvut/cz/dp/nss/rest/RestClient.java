package cvut.cz.dp.nss.rest;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class RestClient {

    /**
     * root adresa serveru. (10.0.2.2 je localhost)
     */
//    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/";
    private static final String BASE_URL = "http://192.168.0.12:8080/api/v1/";

    private static SyncHttpClient syncClient;
    private static AsyncHttpClient asyncClient;

    static {
        syncClient = new SyncHttpClient();
        asyncClient = new AsyncHttpClient();

        //timeout 45s
        asyncClient.setTimeout(45000);
    }

    public static void getSync(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        syncClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getAsync(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        asyncClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
