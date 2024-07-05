package entpack.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PropKit;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    private static int timeOut = PropKit.getInt("okHttpTimeOut", 30);

    private static OkHttpClient getClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(timeOut, TimeUnit.SECONDS)//设置读取超时时间
                .build();

    }

    /**
     * GET请求
     *
     * @param address
     * @param params
     * @return
     */
    public static String get(String address, Map<String, String> params) {
        return get(address, params, null);
    }

    /**
     * GET请求
     *
     * @param address
     * @param params
     * @return
     */
    public static String get(String address, Map<String, String> params, Headers headers) {

        OkHttpClient client = getClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(address)
                .newBuilder();

        // 参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();

        logger.info(request.url().toString());
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("get.error", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    /**
     * GET请求
     *
     * @param address
     * @param params
     * @return
     */
    public static String[] getWithCode(String address, Map<String, String> params) {
        return getWithCode(address, params, null);
    }

    /**
     * GET请求
     *
     * @param address
     * @param params
     * @return
     */
    public static String[] getWithCode(String address, Map<String, String> params, Headers headers) {

        OkHttpClient client = getClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(address)
                .newBuilder();

        // 参数
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();

        logger.info(request.url().toString());
        Response response = null;
        try {
            response = client.newCall(request).execute();

            String[] r= new String[2];

            r[0]=String.valueOf(response.code());
            r[1]="";

            if (response.code() == 200) {
                r[1] = response.body().string();
            } else {
                logger.error("getWithCode code={},body={}", response.code(), response.body().string());
            }

            return r;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("get.error", e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * POST请求
     *
     * @param address
     * @param map
     * @return
     */
    public static String post(String address, Map<String, String> map) {
        return post(address, map, null);
    }

    /**
     * POST请求
     *
     * @param address
     * @param map
     * @return
     */
    public static String post(String address, Map<String, String> map, Headers headers) {
        OkHttpClient client = getClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            // 参数
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(body);
        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();


            if (response.code() == 200) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * POST请求
     *
     * @param address
     * @param map
     * @return
     */
    public static String postObject(String address, Map<String, Object> map, Headers headers) {
        OkHttpClient client = getClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            // 参数
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        FormBody body = builder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(body);
        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();


            if (response.code() == 200) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    public static String[] postWithCode(String address, Map<String, String> map) {
        return postWithCode(address, map, null);
    }

    /**
     * POST 并返回状态嗎
     *
     * @param address
     * @param map
     * @param headers
     * @return
     */
    public static String[] postWithCode(String address, Map<String, String> map, Headers headers) {


        OkHttpClient client = getClient();
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
            // 参数
            for (Map.Entry<String, String> entry : map.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(body);
        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            String[] r = new String[2];

            r[0] = String.valueOf(response.code());
            r[1] = "";
            if (response.code() == 200) {
                r[1] = response.body().string();
            }

            return r;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    /**
     * POST请求
     *
     * @param address
     * @param param
     * @return
     */
    public static String postJSON(String address, Map<String, Object> param) {
        return postJSON(address, param, null);
    }

    /**
     * POST请求
     *
     * @param address
     * @param param
     * @return
     */
    public static String postJSON(String address, Map<String, Object> param, Headers headers) {
        OkHttpClient client = getClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, JSONObject.toJSONString(param));

        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(requestBody);

        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    /**
     * Post json with status Code
     *
     * @param address
     * @param param
     * @return array of string , index 0, status code  index 1, return data
     */
    public static String[] postJSONWithCode(String address, Map<String, Object> param) {

        return postJSONWithCode(address, param, null);
    }

    /**
     * Post json with status Code
     *
     * @param address
     * @param param
     * @param headers
     * @return array of string , index 0, status code  index 1, return data
     */
    public static String[] postJSONWithCode(String address, Map<String, Object> param, Headers headers) {
        OkHttpClient client = getClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");


        RequestBody requestBody = RequestBody.create(JSON, JSONObject.toJSONString(param));

        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(requestBody);

        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();

            String[] r = new String[2];

            r[0] = String.valueOf(response.code());
            r[1] = "";
            if (response.code() == 200) {
                r[1] = response.body().string();
            }

            return r;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }


    /**
     * POST XML
     *
     * @param address
     * @param xml
     * @return
     */
    public static Response postXML(String address, String xml) {

        OkHttpClient client = getClient();
        MediaType xmlType = MediaType.parse("application/xml; charset=utf-8");

        RequestBody requestBody = RequestBody.create(xmlType, xml);
        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(requestBody);
        Request request = requestBuilder.build();
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String address, String postData, Headers headers) {
        OkHttpClient client = getClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

        RequestBody body = RequestBody.create(mediaType, postData);

        Request.Builder requestBuilder = new Request.Builder()
                .url(address)
                .post(body);
        if (headers != null) {
            requestBuilder.headers(headers);
        }

        Request request = requestBuilder.build();
        Response response = null;
        try {
            response = client.newCall(request).execute();


            if (response.code() == 200) {
                return response.body().string();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

}
