package com.fzx.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fzx.Common.ApiUrl;
import com.fzx.HttpUtils.HttpClientUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/realTimeBus")
public class RealTimeBusController {

    @RequestMapping(value = "/getBusLines", method = RequestMethod.GET)
    public static JSONObject getAllBusLineByBusName(@RequestParam("busName") String busName) {
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", busName);
        map.put("type", "line");
        return HttpClientUtil.doGet(ApiUrl.API_URL_01, map);
    }

    @RequestMapping(value = "/getRealBusInfo", method = RequestMethod.GET)
    public static JSONObject getRealBusInfo(@RequestParam("lineId") String lineId) {
        Map<String, Object> map = new HashMap<>();
        map.put("Type", "LineDetail");

        JSONObject result = HttpClientUtil.doGet(ApiUrl.API_URL_02.replace("?", lineId), map);
        JSONObject data = result.getJSONObject("data");

        // 停靠站点排序
        JSONArray stops = data.getJSONArray("stops");
        stops.sort(Comparator.comparing(json -> ((JSONObject) json).getInteger("stopOrder")));
        data.put("stops", stops);

        // 实时公交数据
        JSONArray buses = data.getJSONArray("buses");
        for (int i = 0; i < buses.size(); i++) {
            String[] busInfo = buses.getString(i).split("|");
            JSONObject json = new JSONObject();
        }

        data.put("buses", buses);
        result.put("data", data);
        return result;
    }
}
