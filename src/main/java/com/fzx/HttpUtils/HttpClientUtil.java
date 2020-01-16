package com.fzx.HttpUtils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * 发送get、post请求
 */
public class HttpClientUtil {

    /**
     * 与iCollege对接，get方法
     *
     * @param url
     * @param map
     * @return
     */
    public static JSONObject doGet(String url, Map<String, Object> map) {
        JSONObject resJSON = new JSONObject();

        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        RequestConfig requestConfig = null;

        try {
            httpClient = HttpClients.createDefault();

            URIBuilder uriBuilder = new URIBuilder(url);

            List<NameValuePair> list = new ArrayList<>();
            Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, Object> elem = iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue().toString()));
            }
            if (list.size() > 0) {
                uriBuilder.setParameters(list);

                httpGet = new HttpGet(uriBuilder.build());

                requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();// 设置请求和传输超时时间
                httpGet.setConfig(requestConfig);
            }

            HttpResponse response = httpClient.execute(httpGet);

            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "utf-8");
                    resJSON = JSONObject.parseObject(result);
                    resJSON.put("httpStatus", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resJSON;
    }

    /**
     * 与iCollege对接，post方法
     *
     * @param url
     * @return
     */
    public static JSONObject doPost(String url, JSONObject paramJSON) {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        RequestConfig requestConfig = null;
        JSONObject resultJSON = null;

        try {
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);

            httpPost.addHeader("Content-Type", "application/json");

            StringEntity entity = new StringEntity(paramJSON.toString(), "utf-8");
            entity.setContentType("application/json");
            entity.setContentEncoding("utf-8");
            httpPost.setEntity(entity);

            requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(httpPost);

            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    resultJSON = (JSONObject) JSONObject.toJSON(EntityUtils.toString(resEntity, "utf-8"));
                    resultJSON.put("httpStatus", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJSON;
    }
}
