import com.alibaba.fastjson.JSONObject;
import com.fzx.HttpUtils.HttpClientUtil;
import com.fzx.MD5.MD5;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class T1 {
    @Test
    public void t1() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("optype", "city");

        for (int i = 0; i < 10000; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String s = 13237150000l + finalI + "";
                    map.put("uname", s);
                    JSONObject json = HttpClientUtil.doGet("http://api.dwmm136.cn/z_busapi/BusApi.php", map);
                    if (json.getString("error_code").equals("001")) {
                        System.out.println("电话：" + s + ",数据：" + json);
                    } else {
                        // System.out.println("无");
                    }
                }
            }).start();
        }

        Thread.sleep(600000);
    }

    @Test
    public void t2() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("optype", "luxian");
        map.put("uname", "13296524960");
        map.put("cityid", 281);
        map.put("keywords", 18);
        map.put("keySecret", MD5.md5Encryption("13296524960" + "f7c5a9ed4d58637782efd397f907d8c6" + "luxian"));
        System.out.println(HttpClientUtil.doGet("http://api.dwmm136.cn/z_busapi/BusApi.php", map));
    }

    @Test
    public void t3() throws Exception {
        Map<String, Object> map = new HashMap<>();

        map.put("optype", "rtbus");
        map.put("uname", "13296524960");
        map.put("cityid", 281);
        map.put("bus_linestrid", "NjAwMDAxNDEzMTI1MjAwMDY=");
        map.put("bus_linenum", "4308");
        map.put("bus_staname", "4308");
        map.put("keySecret", MD5.md5Encryption("13296524960" + "f7c5a9ed4d58637782efd397f907d8c6" + "rtbus"));
        System.out.println(HttpClientUtil.doGet("http://api.dwmm136.cn/z_busapi/BusApi.php", map));
    }

    @Test
    public void t4() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("optype", "luxian");
        map.put("uname", "13296524960");
        map.put("cityid", 281);
        map.put("keywords", 538);
        map.put("keySecret", MD5.md5Encryption("13296524960" + "f7c5a9ed4d58637782efd397f907d8c6" + "luxian"));
        JSONObject jsonObject01 = HttpClientUtil.doGet("http://api.dwmm136.cn/z_busapi/BusApi.php", map);

        map.put("optype", "rtbus");
        map.put("uname", "13296524960");
        map.put("cityid", 281);
        map.put("bus_linestrid", jsonObject01.getJSONArray("returl_list").getJSONObject(0).getString("bus_linestrid"));
        map.put("bus_linenum", jsonObject01.getJSONArray("returl_list").getJSONObject(0).getString("bus_linenum"));
        map.put("bus_staname", jsonObject01.getJSONArray("returl_list").getJSONObject(0).getString("bus_staname"));
        map.put("keySecret", MD5.md5Encryption("13296524960" + "f7c5a9ed4d58637782efd397f907d8c6" + "rtbus"));
        System.out.println(HttpClientUtil.doGet("http://api.dwmm136.cn/z_busapi/BusApi.php", map));
    }

    @Test
    public void t5() {
        System.out.println(LocalDateTime.now());
    }
}
