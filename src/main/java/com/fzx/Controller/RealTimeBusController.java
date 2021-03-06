package com.fzx.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fzx.Common.ApiUrl;
import com.fzx.HttpUtils.HttpClientUtil;
import com.fzx.entity.User;
import com.fzx.service.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 实时公交
 *
 * @author: zxfuc
 * @className: RealTimeBusController
 * @time: 2020/1/17
 */
@RestController
@RequestMapping("/realTimeBus")
public class RealTimeBusController {
    Logger logger = LoggerFactory.getLogger(RealTimeBusController.class);

    @Autowired
    private UserServiceImpl userService;

    /**
     * 根据公交路线名称，模糊匹配前10个公交车
     *
     * @description: * @Param: null
     * @return:
     * @author: zxfuc
     * @time: 2020/1/17 14:26
     */
    @RequestMapping(value = "/getBusLines", method = RequestMethod.POST)
    public JSONObject getAllBusLineByBusName(@RequestParam("busName") String busName, @RequestBody User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("keyword", busName);
        map.put("type", "line");
        JSONObject result = HttpClientUtil.doGet(ApiUrl.API_URL_01, map);
        // 过滤前10条数据
        JSONArray tmp = result.getJSONObject("data").getJSONArray("lines");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < tmp.size() && i < 10; i++) {
            jsonArray.add(tmp.get(i));
        }
        result.getJSONObject("data").put("lines", jsonArray);

        // 表记录查询
        user.setSearchResult(result.toJSONString());
        user.setSearchContent(busName);
        user.setType(1);
        user.setTime(LocalDateTime.now());
        userService.save(user);

        logger.info("保存用户数据信息：" + user);
        logger.info("获取所有公交数据：" + result);
        return result;
    }

    /**
     * @description: * @Param: null
     * @return:
     * @author: zxfuc
     * @time: 2020/1/22 10:09
     */
    @RequestMapping(value = "/getRealBusInfo", method = RequestMethod.POST)
    public JSONObject getRealBusInfo(@RequestParam("lineId") String lineId, @RequestBody User user) {
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

        // 表记录查询
        user.setSearchResult(result.toJSONString());
        user.setSearchContent(lineId);
        user.setType(2);
        user.setTime(LocalDateTime.now());
        userService.save(user);

        logger.info("保存用户数据信息：" + user);
        logger.info("获取实时公交数据：" + result);
        return result;
    }
}
