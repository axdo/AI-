import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增强大脑：生肖配对、天气预报、星座运势、手机归属地
 */
public class SuperBrain {

    //备份接口地址：
    //手机号正则
    private static final String PHONE_REGEX="(0|86|17951)?((13[0-9]|15[012356789]|17[0135678]|18[0-9]|14[57])[0-9]{8})";
    //天气正则
    private static final String WEATHER_REGEX="(说)?(给我说)?(告诉我?)?(查询?)?(今天)?的?([\\u4e00-\\u9fa5]{0,10})?天气";
    //生肖正则
    private static final String SHENGXIAO_REGEX = ".*(([男,女][鼠牛,虎,兔,龙,蛇,马羊,猴,鸡,狗,猪])|([鼠牛,虎,兔,龙,蛇,马羊,猴,鸡,狗,猪][男,女])).*(([男,女][鼠牛,虎,兔,龙,蛇,马羊,猴,鸡,狗,猪])|([鼠牛,虎,兔,龙,蛇,马羊,猴,鸡,狗,猪][男,女])).*";
    //星座正则
    private static final String XINGZUO_REGEX = ".*((白羊)|(金牛)|(双子)|(巨蟹)|(狮子)|(处女)|(天秤)|(天蝎)|(射手)|(摩羯)|(水瓶)|(双鱼)).*";
    //地区正则
    private static final String PROVINCE_REGEX="((湖南)|(湖北)|(广东)|(广西)|(河南)|(河北)|(山东)|(山西)|" +
            "(江苏)|(浙江)|(江西)|(黑龙江)|(新疆)|(云南)|(贵州)|(福建)|(吉林)|(安徽)|" +
            "(四川)|(西藏)|(宁夏)|(辽宁)|(青海)|(甘肃)|(陕西)|" +
            "(内蒙古)|(台湾))(省份?)?";

    private Gson gson = new Gson();

    //请求地址：http://apis.juhe.cn/sxpd/query请求参数：men=%E8%9B%87&women=%E7%BE%8A&key=f62454bd43216dc56b3c0309d1240ee5
    //生肖配对
    private String sxpd = "83d894d04a6d0b9333d26ca5d092db59";
    //请求地址：http://apis.juhe.cn/simpleWeather/query?city=%E5%8C%97%E4%BA%AC&key=ade7e3d0551d95c14bfb42b25180ba87
    //天气预报
    private String simpleWeather = "2c25bd1d2ee9f18013d9c83780fe78a2";
    //请求地址：http://web.juhe.cn:8080/constellation/getAll 请求参数：consName=%E5%8F%8C%E9%B1%BC%E5%BA%A7&type=today&key=4e31a327009ebc229ce54f5878589bad
    //星座运势
    private String constellation = "920e94d94046314a9668a3b100ccf41a";
    //请求地址：http://apis.juhe.cn/mobile/get 请求参数：phone=18401790000&dtype=json&key=ce75d575f1e353cd879c599cf10d1044
    //手机归属地
    private String mobilePhone = "e062dce7b4f1c3567e759cabdfee7f2d";

    public String solve(String content) {

        String result=  solveShengxiao(content);
        if(StringUtils.isBlank(result)) {
            result = solvePhone(content);
        }
        if(StringUtils.isBlank(result)) {
            result = solveWeather(content);
        }
        if(StringUtils.isBlank(result)) {
            result = solveXingzuo(content);
        }
        return result;
    }

    /**
     * 天气预报
     */
    private  String solveWeather(String message){
        String result = null;
        Pattern pattern = Pattern.compile(WEATHER_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String city = matcher.group(6);
            city = city.replace("今天","");
            if(city.endsWith("的")){
                city=city.substring(0,city.length()-1);
            }

            if(city.matches(PROVINCE_REGEX)){
                 result="您要问"+city+"哪个城市的天气呢？请对我说：城市+天气，比如：北京天气";

                return result;
            }

            try {
                String url = "http://apis.juhe.cn/simpleWeather/query?city=" + URLEncoder.encode(city, "utf-8") + "&key=" + simpleWeather;

                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {

                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        Map<String,Object> realTime = (Map<String, Object>) jhResult.getResult().get("realtime");
                        String info = (String) realTime.get("info");//信息
                        String temperature = (String) realTime.get("temperature");//气温
                        String direct = (String) realTime.get("direct");//风向
                        String power = (String) realTime.get("power");//风力
                        String aqi = (String) realTime.get("aqi");//空气质量指数

                        result="["+new SimpleDateFormat("MM月dd日").format(new Date())+"]"+city+"天气:"+info;
                        result = StringUtils.isBlank(temperature) ? result : result+",气温"+temperature+"℃";
                        result = StringUtils.isBlank(direct) ? result : result +","+direct+" "+power;
                        result = StringUtils.isBlank(aqi) ? result : result +",空气质量指数 "+aqi;

                    }else if(jhResult.getError_code() == 207301 || jhResult.getError_code() == 207302){
                        result="对不起，小乔没有找到"+city+"的天气情况，请检查一下城市名称是否正确~";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return result;
    }
    /**
     * 星座运势
     */
    private  String solveXingzuo(String message) {
        String result = null;
        Pattern pattern = Pattern.compile(XINGZUO_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String xingzuo = matcher.group(1);
            xingzuo+="座";
            try {

                String url = "http://web.juhe.cn:8080/constellation/getAll?consName="+URLEncoder.encode(xingzuo,"UTF-8")+ "&type=today&key=" + constellation;
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {
                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        Map mapResult = gson.fromJson(response.getBody(), Map.class);
                        result="["+xingzuo+"-今日运势]";
                        result+="综合指数："+mapResult.get("all");
                        result+=";爱情指数："+mapResult.get("love");
                        result+=";财运指数："+mapResult.get("money");
                        result+=";健康指数："+mapResult.get("health");
                        result+=";工作指数："+mapResult.get("work");
                        result+=";幸运色："+mapResult.get("color");
                        result+=";幸运数字："+mapResult.get("number");
                        result+=";星座速配："+mapResult.get("QFriend");
                        result+=";总结："+mapResult.get("summary");
                    }
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * 手机号归属地
     */
    private  String solvePhone(String message){
        String result = null;
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String phone = matcher.group(2);
            try {

                String url = "http://apis.juhe.cn/mobile/get?phone=" + phone + "&key=" + mobilePhone;
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {
                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        String province = (String) jhResult.getResult().get("province");
                        String ciity = (String) jhResult.getResult().get("city");
                        String company = (String) jhResult.getResult().get("company");
                        result = StringUtils.isBlank(province) ? "" : province;
                        result = StringUtils.isBlank(ciity) || ciity.equals(province)  ? result : result+ciity;
                        result = StringUtils.isBlank(company) ? result : result +"-"+company;
                    }
                }
            } catch (IOException e) {
            }

        }
        return result;
    }

    /**
     * 生肖匹配
     */
    private  String solveShengxiao(String message) {
        String result = null;
        Pattern pattern = Pattern.compile(SHENGXIAO_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String p1 = matcher.group(1);
            String p2 = matcher.group(4);
            //优先使用缓存
            String url = "http://apis.juhe.cn/sxpd/query?key=" + sxpd + "&" + solveShengxiao2kv(p1) + "&" + solveShengxiao2kv(p2);
            try {
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {
                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        result = String.valueOf(jhResult.getResult().get("data"));
                    }
                }
            } catch (IOException e) {}
        }
        return result;
    }
    /**
     * 生肖匹配
     * @param shengxiao
     * @return
     */
    private static String solveShengxiao2kv(String shengxiao) {
        try {
            String str = shengxiao.startsWith("男")|| shengxiao.startsWith("女")?shengxiao:StringUtils.reverse(shengxiao);
            String kv = str.replace("男", "men=").replace("女", "women=");
            String[] strs = kv.split("=");
            return strs[0] + "=" + URLEncoder.encode(strs[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }



}
class JHResult {
    private String reason;
    private Integer error_code;
    private Map result;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getError_code() {
        return error_code;
    }

    public void setError_code(Integer error_code) {
        this.error_code = error_code;
    }

    public Map getResult() {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }
}

enum HttpStatus {
    OK(200,"OK");

    private int state_code;
    
    private String reason;

    HttpStatus(int state_code, String reason) {
        this.state_code = state_code;
        this.reason = reason;
    }

    public int getState_code() {
        return state_code;
    }

    public void setState_code(int state_code) {
        this.state_code = state_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}