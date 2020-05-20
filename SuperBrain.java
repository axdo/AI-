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
 * ��ǿ���ԣ���Ф��ԡ�����Ԥ�����������ơ��ֻ�������
 */
public class SuperBrain {

    //���ݽӿڵ�ַ��
    //�ֻ�������
    private static final String PHONE_REGEX="(0|86|17951)?((13[0-9]|15[012356789]|17[0135678]|18[0-9]|14[57])[0-9]{8})";
    //��������
    private static final String WEATHER_REGEX="(˵)?(����˵)?(������?)?(��ѯ?)?(����)?��?([\\u4e00-\\u9fa5]{0,10})?����";
    //��Ф����
    private static final String SHENGXIAO_REGEX = ".*(([��,Ů][��ţ,��,��,��,��,����,��,��,��,��])|([��ţ,��,��,��,��,����,��,��,��,��][��,Ů])).*(([��,Ů][��ţ,��,��,��,��,����,��,��,��,��])|([��ţ,��,��,��,��,����,��,��,��,��][��,Ů])).*";
    //��������
    private static final String XINGZUO_REGEX = ".*((����)|(��ţ)|(˫��)|(��з)|(ʨ��)|(��Ů)|(���)|(��Ы)|(����)|(Ħ��)|(ˮƿ)|(˫��)).*";
    //��������
    private static final String PROVINCE_REGEX="((����)|(����)|(�㶫)|(����)|(����)|(�ӱ�)|(ɽ��)|(ɽ��)|" +
            "(����)|(�㽭)|(����)|(������)|(�½�)|(����)|(����)|(����)|(����)|(����)|" +
            "(�Ĵ�)|(����)|(����)|(����)|(�ຣ)|(����)|(����)|" +
            "(���ɹ�)|(̨��))(ʡ��?)?";

    private Gson gson = new Gson();

    //�����ַ��http://apis.juhe.cn/sxpd/query���������men=%E8%9B%87&women=%E7%BE%8A&key=f62454bd43216dc56b3c0309d1240ee5
    //��Ф���
    private String sxpd = "83d894d04a6d0b9333d26ca5d092db59";
    //�����ַ��http://apis.juhe.cn/simpleWeather/query?city=%E5%8C%97%E4%BA%AC&key=ade7e3d0551d95c14bfb42b25180ba87
    //����Ԥ��
    private String simpleWeather = "2c25bd1d2ee9f18013d9c83780fe78a2";
    //�����ַ��http://web.juhe.cn:8080/constellation/getAll ���������consName=%E5%8F%8C%E9%B1%BC%E5%BA%A7&type=today&key=4e31a327009ebc229ce54f5878589bad
    //��������
    private String constellation = "920e94d94046314a9668a3b100ccf41a";
    //�����ַ��http://apis.juhe.cn/mobile/get ���������phone=18401790000&dtype=json&key=ce75d575f1e353cd879c599cf10d1044
    //�ֻ�������
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
     * ����Ԥ��
     */
    private  String solveWeather(String message){
        String result = null;
        Pattern pattern = Pattern.compile(WEATHER_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String city = matcher.group(6);
            city = city.replace("����","");
            if(city.endsWith("��")){
                city=city.substring(0,city.length()-1);
            }

            if(city.matches(PROVINCE_REGEX)){
                 result="��Ҫ��"+city+"�ĸ����е������أ������˵������+���������磺��������";

                return result;
            }

            try {
                String url = "http://apis.juhe.cn/simpleWeather/query?city=" + URLEncoder.encode(city, "utf-8") + "&key=" + simpleWeather;

                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {

                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        Map<String,Object> realTime = (Map<String, Object>) jhResult.getResult().get("realtime");
                        String info = (String) realTime.get("info");//��Ϣ
                        String temperature = (String) realTime.get("temperature");//����
                        String direct = (String) realTime.get("direct");//����
                        String power = (String) realTime.get("power");//����
                        String aqi = (String) realTime.get("aqi");//��������ָ��

                        result="["+new SimpleDateFormat("MM��dd��").format(new Date())+"]"+city+"����:"+info;
                        result = StringUtils.isBlank(temperature) ? result : result+",����"+temperature+"��";
                        result = StringUtils.isBlank(direct) ? result : result +","+direct+" "+power;
                        result = StringUtils.isBlank(aqi) ? result : result +",��������ָ�� "+aqi;

                    }else if(jhResult.getError_code() == 207301 || jhResult.getError_code() == 207302){
                        result="�Բ���С��û���ҵ�"+city+"���������������һ�³��������Ƿ���ȷ~";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return result;
    }
    /**
     * ��������
     */
    private  String solveXingzuo(String message) {
        String result = null;
        Pattern pattern = Pattern.compile(XINGZUO_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String xingzuo = matcher.group(1);
            xingzuo+="��";
            try {

                String url = "http://web.juhe.cn:8080/constellation/getAll?consName="+URLEncoder.encode(xingzuo,"UTF-8")+ "&type=today&key=" + constellation;
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (response.getStatusCode() == HttpStatus.OK.getState_code()) {
                    JHResult jhResult = gson.fromJson(response.getBody(), JHResult.class);
                    if (jhResult.getError_code() == 0) {
                        Map mapResult = gson.fromJson(response.getBody(), Map.class);
                        result="["+xingzuo+"-��������]";
                        result+="�ۺ�ָ����"+mapResult.get("all");
                        result+=";����ָ����"+mapResult.get("love");
                        result+=";����ָ����"+mapResult.get("money");
                        result+=";����ָ����"+mapResult.get("health");
                        result+=";����ָ����"+mapResult.get("work");
                        result+=";����ɫ��"+mapResult.get("color");
                        result+=";�������֣�"+mapResult.get("number");
                        result+=";�������䣺"+mapResult.get("QFriend");
                        result+=";�ܽ᣺"+mapResult.get("summary");
                    }
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    /**
     * �ֻ��Ź�����
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
     * ��Фƥ��
     */
    private  String solveShengxiao(String message) {
        String result = null;
        Pattern pattern = Pattern.compile(SHENGXIAO_REGEX);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String p1 = matcher.group(1);
            String p2 = matcher.group(4);
            //����ʹ�û���
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
     * ��Фƥ��
     * @param shengxiao
     * @return
     */
    private static String solveShengxiao2kv(String shengxiao) {
        try {
            String str = shengxiao.startsWith("��")|| shengxiao.startsWith("Ů")?shengxiao:StringUtils.reverse(shengxiao);
            String kv = str.replace("��", "men=").replace("Ů", "women=");
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