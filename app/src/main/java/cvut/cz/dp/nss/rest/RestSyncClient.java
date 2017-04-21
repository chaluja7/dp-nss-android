package cvut.cz.dp.nss.rest;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * @author jakubchalupa
 * @since 21.04.17
 */
public class RestSyncClient {

    /**
     * root adresa serveru. (10.0.2.2 je localhost)
     */
    private static final String BASE_URL = "http://10.0.2.2:8080/api/v1/x-pid/";

    private static SyncHttpClient client = new SyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
