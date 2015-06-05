package org.cobbzilla.wizard.client;

import lombok.Cleanup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cobbzilla.util.http.*;
import org.cobbzilla.util.json.JsonUtil;
import org.cobbzilla.wizard.api.ApiException;
import org.cobbzilla.wizard.api.ForbiddenException;
import org.cobbzilla.wizard.api.NotFoundException;
import org.cobbzilla.wizard.api.ValidationException;
import org.cobbzilla.wizard.util.RestResponse;

import java.io.*;
import java.util.Stack;

import static org.cobbzilla.util.daemon.ZillaRuntime.die;
import static org.cobbzilla.util.daemon.ZillaRuntime.empty;

@Slf4j @NoArgsConstructor
public class ApiClientBase {

    public static final ContentType CONTENT_TYPE_JSON = ContentType.APPLICATION_JSON;

    @Getter protected ApiConnectionInfo connectionInfo;
    @Getter protected String token;

    public void setToken(String token) {
        this.token = token;
        this.tokenCtime = empty(token) ? 0 : System.currentTimeMillis();
    }

    private long tokenCtime = 0;
    public boolean hasToken () { return !empty(token); }
    public long getTokenAge () { return System.currentTimeMillis() - tokenCtime; }

    public ApiClientBase (ApiConnectionInfo connectionInfo) { this.connectionInfo = connectionInfo; }
    public ApiClientBase (String baseUri) { connectionInfo = new ApiConnectionInfo(baseUri); }

    public ApiClientBase (ApiConnectionInfo connectionInfo, HttpClient httpClient) {
        this(connectionInfo);
        setHttpClient(httpClient);
    }

    public ApiClientBase (String baseUri, HttpClient httpClient) {
        this(baseUri);
        setHttpClient(httpClient);
    }

    public String getBaseUri () { return connectionInfo.getBaseUri(); }

    @Getter @Setter private HttpClient httpClient = new DefaultHttpClient();

    public RestResponse process(HttpRequestBean requestBean) throws Exception {
        switch (requestBean.getMethod()) {
            case HttpMethods.GET:
                return doGet(requestBean.getUri());
            case HttpMethods.POST:
                return doPost(requestBean.getUri(), getJson(requestBean));
            case HttpMethods.PUT:
                return doPut(requestBean.getUri(), getJson(requestBean));
            case HttpMethods.DELETE:
                return doDelete(requestBean.getUri());
            default:
                return die("Unsupported request method: "+requestBean.getMethod());
        }
    }

    public <T> RestResponse process_raw(HttpRequestBean<T> requestBean) throws Exception {
        switch (requestBean.getMethod()) {
            case HttpMethods.GET:
                return doGet(requestBean.getUri());
            case HttpMethods.POST:
                return doPost(requestBean.getUri(), requestBean.getData(), requestBean.getContentType());
            case HttpMethods.PUT:
                return doPut(requestBean.getUri(), requestBean.getData(), requestBean.getContentType());
            case HttpMethods.DELETE:
                return doDelete(requestBean.getUri());
            default:
                return die("Unsupported request method: "+requestBean.getMethod());
        }
    }

    protected void assertStatusOK(RestResponse response) {
        if (response.status != HttpStatusCodes.OK
                && response.status != HttpStatusCodes.CREATED
                && response.status != HttpStatusCodes.NO_CONTENT) throw new ApiException(response);
    }

    private String getJson(HttpRequestBean requestBean) throws Exception {
        Object data = requestBean.getData();
        if (data == null) return null;
        if (data instanceof String) return (String) data;
        return JsonUtil.toJson(data);
    }

    protected ApiException specializeApiException(ApiException e) {
        return specializeApiException(e.getResponse());
    }

    protected ApiException specializeApiException(RestResponse response) {
        if (response.isSuccess()) {
            die("specializeApiException: cannot specialize exception for a successful response: "+response);
        }
        switch (response.status) {
            case HttpStatusCodes.NOT_FOUND:
                return new NotFoundException(response);
            case HttpStatusCodes.FORBIDDEN:
                return new ForbiddenException(response);
            case HttpStatusCodes.UNPROCESSABLE_ENTITY:
                return new ValidationException(response);
            default: return new ApiException(response);
        }
    }

    public RestResponse doGet(String path) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpGet httpGet = new HttpGet(url);
        return getResponse(client, httpGet);
    }

    public RestResponse get(String path) throws Exception {
        final RestResponse restResponse = doGet(path);
        if (!restResponse.isSuccess()) throw specializeApiException(restResponse);
        return restResponse;
    }

    protected <T> void setRequestEntity(HttpEntityEnclosingRequest entityRequest, T data, ContentType contentType) {
        if (data != null) {
            if (data instanceof InputStream) {
                entityRequest.setEntity(new InputStreamEntity((InputStream) data, contentType));
                log.info("doPut sending JSON=(InputStream)");
            } else {
                entityRequest.setEntity(new StringEntity(data.toString(), contentType));
                log.info("doPut sending JSON=" + data);
            }
        }
    }

    public RestResponse doPost(String path, String json) throws Exception {
        return doPost(path, json, CONTENT_TYPE_JSON);
    }

    public <T> RestResponse doPost(String path, T data, ContentType contentType) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpPost httpPost = new HttpPost(url);
        setRequestEntity(httpPost, data, contentType);
        return getResponse(client, httpPost);
    }

    public RestResponse post(String path, String json) throws Exception {
        return post(path, json, CONTENT_TYPE_JSON);
    }

    public RestResponse post(String path, String data, ContentType contentType) throws Exception {
        final RestResponse restResponse = doPost(path, data, contentType);
        if (!restResponse.isSuccess()) throw specializeApiException(restResponse);
        return restResponse;
    }

    public RestResponse doPut(String path, String json) throws Exception {
        return doPut(path, json, CONTENT_TYPE_JSON);
    }

    public <T> RestResponse doPut(String path, T data, ContentType contentType) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpPut httpPut = new HttpPut(url);
        setRequestEntity(httpPut, data, contentType);
        return getResponse(client, httpPut);
    }

    public RestResponse put(String path, String json) throws Exception {
        final RestResponse restResponse = doPut(path, json);
        if (!restResponse.isSuccess()) throw specializeApiException(restResponse);
        return restResponse;
    }

    public RestResponse doDelete(String path) throws Exception {
        HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpDelete httpDelete = new HttpDelete(url);
        return getResponse(client, httpDelete);
    }

    public RestResponse delete(String path) throws Exception {
        final RestResponse restResponse = doDelete(path);
        if (!restResponse.isSuccess()) throw specializeApiException(restResponse);
        return restResponse;
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
        request = beforeSend(request);
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

    public File getFile (String path) throws IOException {

        final HttpClient client = getHttpClient();
        final String url = getUrl(path, getBaseUri());
        @Cleanup("releaseConnection") HttpRequestBase request = new HttpGet(url);
        request = beforeSend(request);

        final HttpResponse response = client.execute(request);
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 404) return null;
        if (!RestResponse.isSuccess(statusCode)) die("getFile("+url+"): error: "+statusCode);

        final HttpEntity entity = response.getEntity();
        if (entity == null) die("getFile("+url+"): No entity");

        final File file = File.createTempFile(getClass().getName()+"-", getTempFileSuffix(path, HttpUtil.getContentType(response)));
        try (InputStream in = entity.getContent()) {
            try (OutputStream out = new FileOutputStream(file)) {
                IOUtils.copyLarge(in, out);
            }
        }

        return file;
    }

    protected String getTempFileSuffix(String path, String contentType) {
        if (empty(contentType)) return ".temp";
        switch (contentType) {
            case "image/jpeg": return ".jpg";
            case "image/gif": return ".gif";
            case "image/png": return ".png";
            default: return ".temp";
        }
    }

    protected HttpRequestBase beforeSend(HttpRequestBase request) {
        if (!empty(token)) {
            final String tokenHeader = getTokenHeader();
            if (empty(tokenHeader)) die("token set but getTokenHeader returned null");
            request.setHeader(tokenHeader, token);
        }
        return request;
    }

    protected String getTokenHeader() { return null; }

    protected final Stack<String> tokenStack = new Stack<>();
    public void pushToken(String token) {
        synchronized (tokenStack) {
            if (tokenStack.isEmpty()) tokenStack.push(getToken());
            tokenStack.push(token);
            setToken(token);
        }
    }

    /**
     * Pops the current token off the stack.
     * Now the top of the stack is the previous token, so it becomes the active one.
     * @return The current token popped off (NOT the current active token, call getToken() to get that)
     */
    public String popToken() {
        synchronized (tokenStack) {
            tokenStack.pop();
            setToken(tokenStack.peek());
            return getToken();
        }
    }

    public static final String LOCATION_HEADER = "Location";
    private String getLocationHeader(HttpResponse response) {
        final Header header = response.getFirstHeader(LOCATION_HEADER);
        return header == null ? null : header.getValue();
    }

}
