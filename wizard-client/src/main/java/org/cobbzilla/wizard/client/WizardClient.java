package org.cobbzilla.wizard.client;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cobbzilla.wizard.util.RestResponse;

import java.io.IOException;
import java.io.InputStream;

@Slf4j @NoArgsConstructor @AllArgsConstructor
public class WizardClient {

    @Getter private String baseUri;

    protected HttpClient getHttpClient () { return new DefaultHttpClient(); }

    public RestResponse doGet(String path) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpGet httpGet = new HttpGet(url);
        return getResponse(client, httpGet);
    }

    public RestResponse doPost(String path, String json) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpPost httpPost = new HttpPost(url);
        if (json != null) {
            httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            log.info("doPost("+url+") sending JSON=" + json);
        }
        return getResponse(client, httpPost);
    }

    public RestResponse doPut(String path, String json) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpPut httpPut = new HttpPut(url);
        if (json != null) {
            httpPut.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            log.info("doPut sending JSON="+json);
        }
        return getResponse(client, httpPut);
    }

    public RestResponse doDelete(String path) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpDelete httpDelete = new HttpDelete(url);
        return getResponse(client, httpDelete);
    }

    private String getUrl(String path, String clientUri) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path; // caller has supplied an absolute path

        } else if (path.startsWith("/") && clientUri.endsWith("/")) {
            path = path.substring(1); // caller has supplied a relative path
        }
        return clientUri + path;
    }

    protected RestResponse getResponse(HttpClient client, HttpRequestBase request) throws IOException {
        final HttpResponse response = client.execute(request);
        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseJson;
        final HttpEntity entity = response.getEntity();
        if (entity != null) {
            try (InputStream in = entity.getContent()) {
                responseJson = IOUtils.toString(in);
                log.info("read response callback server: "+responseJson);
            }
        } else {
            responseJson = null;
        }
        return new RestResponse(statusCode, responseJson, getLocationHeader(response));
    }

    public static final String LOCATION_HEADER = "Location";
    private String getLocationHeader(HttpResponse response) {
        final Header header = response.getFirstHeader(LOCATION_HEADER);
        return header == null ? null : header.getValue();
    }

}
