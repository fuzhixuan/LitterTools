package com.fzx.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fzx.Common.ApiUrl;
import com.fzx.HttpUtils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
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
        JSONArray busesTmp = data.getJSONArray("buses");
        int count = 0;
        JSONObject buses = new JSONObject();

        for (int i = 1; i < stops.size() * 2; i++) {
            // 对应车站num，1开始
            int k = i / 2 + 1;
            // 是否是2站之间
            boolean isMid = i % 2 == 1 ? false : true;
            boolean flag = false;

            for (int j = 0; j < busesTmp.size(); j++) {
                String[] busInfo = busesTmp.getString(j).split("\\|");
                String stopNum = busInfo[2];
                String status = busInfo[3];

                // 有这个车站num的数据
                if (stopNum.equals(k + "")) {
                    // 在2车站中间，或在站点
                    if ((isMid && status.equals("1")) || (!isMid && status.equals("0"))) {
                        if (buses.containsKey(i)) {
                            buses.put(i + "", buses.getIntValue(i + "") + 1);
                        } else {
                            buses.put(i + "", 1);
                        }

                        flag = true;
                    }
                }
            }

            // 当前位置是否有数据
            if (!flag) {
                buses.put(i + "", 0);
            }
        }

        data.put("buses", buses);
        result.put("data", data);
        return result;
    }
}
