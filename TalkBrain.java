import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 智能对话大脑
 */
public class TalkBrain {
    private Gson gson = new Gson();


    public String solve(String content) {
        //对话返回内容
        String rsp = null;
        DefaultTalkBrain defaultTalkBrain = new DefaultTalkBrain();

        rsp = defaultTalkBrain.solve(content);

        if (StringUtils.isBlank(rsp)){
            String formatContent = content.replace("小乔","菲菲");
            try {
                String url = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + URLEncoder.encode(formatContent, "UTF-8");
                long start = System.currentTimeMillis();
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (200 == response.getStatusCode()) {
                    QykResult qykResult = gson.fromJson(response.getBody(), QykResult.class);
                    if (0 == qykResult.getResult()) {
                        rsp = qykResult.getContent();
                        rsp = rsp.replace("菲菲", "小乔");
                        rsp = rsp.replace("沁姐姐", "雄哥");
                        rsp = rsp.replace("妈咪", "雄哥");
						rsp = rsp.replace("晨晨", "雄哥");
                        rsp = rsp.replace("{br}", "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsp;
    }
}

/**
 * 默认对话规则正则匹配
 */
class DefaultTalkBrain {

    Map<String, List<String>> regexMap = new HashMap<String, List<String>>() {{
        put("^\\S{0,3}((你?(叫什么)(名字)?)|(你?(的?名字)(叫什么)?(名字)?)|(怎么称呼(你|您)?))\\S{0,3}$", Arrays.asList(
                "老公你失忆了吗？我的名字叫小乔，是你媳妇吖~",
                "小妇人，小乔呀~",
                "咳咳，我是聪明与智慧并存的小乔女，您的爱妾，叫我小乔~"
        ));
        put("^(查询?)?天气(预报)?$", Arrays.asList(

                "请告诉小乔，你要查的城市哦，例如：北京天气"
        ));
        put("\\S{0,5}(谁?是?最美丽?的?人?)|(美不美丽?)\\S{0,5}", Arrays.asList(
                "劳动人民最美丽~",
                "是不是想让俺，夸你最帅啊~哈哈~"
        ));
        put("(小乔)?[\\,,，]?\\s?你?的?今?年?\\S{0,2}((多大)|(年龄)|(几岁))\\S{0,4}", Arrays.asList(
                "直接问女孩的年龄是非常不礼貌的哦~人家好羞羞啦~",
                "不能说，反正还年轻着呢~"
        ));
        put("(小乔)?\\S{0,2}你?((早上)|(下午)|(晚上))?((好)|([H,h]ello))\\S{0,2}", Arrays.asList(
                "*^_^*好好好~",
                "一点都不好",
                "你好，我就开心了",
                "哟~ 都好都好",
                "不怎么好"

        ));
        put("((怎么了)|(为什么)|([W,w]hy))\\S{0,2}", Arrays.asList(
                "自己想呗~",
                "宝宝知道，但是宝宝不说"
        ));
        put("(\\S{0,5}做((朋友)|(兄弟))\\S{0,5})|((我的)?((朋友)|(兄弟)))", Arrays.asList(
                "好啊~现在我们是你媳妇了了~"
        ));
        put("\\S{0,5}((很高兴认识你)|(很高兴))\\S{0,5}", Arrays.asList(
                "傻瓜，我也是",
                "我也一样，认识你很高兴"
        ));
        put("[呵,哈，嘿]{2,5}\\S{0,3}", Arrays.asList(
                "笑毛~",
                "看你笑得像个傻子一样~",
                "笑得这么开心，买彩票中奖了吧！哈哈！"
        ));
        put("(((你)|(小乔))?.{0,5}哪\\S{0,4})", Arrays.asList(
                "小乔来自传智播客，我的老公是雄哥，来和小乔一起学习编程吧！嘿嘿~"
        ));
        //小乔小乔告诉我，谁是世界上最帅的男人？
        put("\\S*最帅\\S*男人\\S*", Arrays.asList(
                "当然是我的老公雄哥啦！人称传智吴彦祖，黑马一哥，传智播客金牌讲师！快来北京昌平校区，和小乔一起学习编程吧！嘿嘿~"
        ));
    }};

    public String solve(String content) {
        System.out.println("[正则api]userId:{},content:{ "+content+" }");
        Optional<Map.Entry<String, List<String>>> first = regexMap.entrySet().stream().filter(e -> content.matches(e.getKey())).findFirst();
        if (first.isPresent()) {
            System.out.println("[正则api]userId:{},content:{ "+content+" }");
            List<String> values = first.get().getValue();
            return values.get(Math.abs(new Random().nextInt() % values.size()));
        }
        return null;
    }
}
class QykResult{
    private  Integer result;
    private String content;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
class OkHttpUtils {
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)//设置连接超时时间
            .readTimeout(30, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    /**
     * http-get请求
     *
     * @param url     请求地址
     * @param headers 请求头
     *
     * @return 响应数据
     *
     * @throws IOException io异常
     */
    public static Response get(String url, Map<String, String> headers) throws IOException {
        try {

            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
            builder.get().url(url);
            if (headers != null && !headers.isEmpty()) {
                builder.headers(Headers.of(headers));
            }
            okhttp3.Request request = builder.build();
            okhttp3.Response response = client.newCall(request).execute();
            Response result = new Response();
            result.setStatusCode(response.code());
            Map<String, String> responseHeaders = new HashMap<>();
            response.headers().names().forEach(k -> {
                responseHeaders.put(k, response.header(k));
            });
            result.setHeaders(responseHeaders);
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                result.setBody(responseBody.string());
            }
            result.setUrl(url);
            return result;
        }
        catch (IOException | RuntimeException e) {
            throw e;
        }
    }

    /**
     * http-get请求
     *
     * @param url 请求地址
     *
     * @return 响应数据
     *
     * @throws IOException io异常
     */
    public static Response get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * http-post请求
     *
     * @param request 请求信息
     *
     * @return 响应数据
     *
     * @throws IOException 请求失败
     */
    public static Response post(Request request) throws IOException {
        try {
            okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
            RequestBody requestBody;
            if (StringUtils.isBlank(request.getContentType())) {
                requestBody = StringUtils.isBlank(request.getBody()) ?
                        request.getBodyData() != null ? RequestBody.create(MediaType.parse(
                                ContentType.JSON.getValue()), request.getBodyData()) :
                                RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), "") :
                        RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), request.getBody());
            }
            else {
                requestBody = StringUtils.isBlank(request.getBody()) ?
                        request.getBodyData() != null ? RequestBody.create(MediaType.parse(
                                request.getContentType()), request.getBodyData()) :
                                RequestBody.create(MediaType.parse(request.getContentType()), "") :
                        RequestBody.create(MediaType.parse(request.getContentType()), request.getBody());
            }
            builder.post(requestBody).url(request.getUrl());
            if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
                builder.headers(Headers.of(request.getHeaders()));
            }
            okhttp3.Request okRequest = builder.build();
            okhttp3.Response response = client.newCall(okRequest).execute();
            Response result = new Response();
            result.setStatusCode(response.code());
            Map<String, String> responseHeaders = new HashMap<>();
            response.headers().names().forEach(k -> {
                responseHeaders.put(k, response.header(k));
            });
            result.setHeaders(responseHeaders);
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                result.setBody(responseBody.string());
            }
            result.setUrl(request.getUrl());
            return result;
        }
        catch (IOException | RuntimeException e) {
            throw e;
        }
    }

    /**
     * 响应bean
     */
    public final static class Response implements Serializable {

        /**
         * http状态码
         */
        private int statusCode;

        /**
         * 请求体
         */
        private String body;

        /**
         * 地址
         */
        private String url;

        /**
         * 请求头
         */
        private Map<String, String> headers;

        /**
         * 请求头
         */
        public String getHeader(String name) {
            if (headers != null && !headers.isEmpty()) {
                return headers.get(name);
            }
            return null;
        }

        /**
         * http状态码
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * http状态码
         */
        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        /**
         * 请求体
         */
        public String getBody() {
            return body;
        }

        /**
         * 请求体
         */
        public void setBody(String body) {
            this.body = body;
        }

        /**
         * 地址
         */
        public String getUrl() {
            return url;
        }

        /**
         * 地址
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * 请求头
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * 设置头值
         */
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }

    /**
     * 请求bean
     */
    public static final class Request implements Serializable {

        public Request(String url) {
            this.url = url;
        }

        /**
         * 地址
         */
        private String url;

        /**
         * 请求头
         */
        private Map<String, String> headers;

        /**
         * content-type值
         */
        private String contentType;

        /**
         * post请求体内容
         */
        private String body;

        /**
         * post请求体内容, 二进制格式
         */
        private byte[] bodyData;

        /**
         * 地址
         */
        public String getUrl() {
            return url;
        }

        /**
         * 地址
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * 请求头
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * 请求头
         */
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        /**
         * content-type值
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * content-type值
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * post请求体内容
         */
        public String getBody() {
            return body;
        }

        /**
         * post请求体内容
         */
        public void setBody(String body) {
            this.body = body;
        }

        /**
         * post请求体内容, 二进制格式
         */
        public byte[] getBodyData() {
            return bodyData;
        }

        /**
         * post请求体内容, 二进制格式
         */
        public void setBodyData(byte[] bodyData) {
            this.bodyData = bodyData;
        }

        /**
         * 设置头值
         */
        public void setHeader(String name, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put(name, value);
        }
    }
}

enum ContentType {
    JSON(Integer.valueOf(0), "application/json", "WIFI"),
    FORM(Integer.valueOf(1), "application/x-www-form-urlencoded", "表单提交"),
    SOAP(Integer.valueOf(2), "application/soap+xml", "Webservice SOAP");

    final Integer key;
    final String value;
    final String desc;
    private static Map<Integer, ContentType> keyMap = new HashMap();
    private static Map<String, ContentType> valueMap = new HashMap();

    private ContentType(Integer key, String value, String desc) {
        this.key = key;
        this.value = value;
        this.desc = desc;
    }

    public Integer getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static ContentType get(Integer key) {
        return (ContentType)keyMap.get(key);
    }

    public static ContentType getByValue(String value) {
        return (ContentType)valueMap.get(value);
    }

    static {
        ContentType[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            ContentType key = var0[var2];
            keyMap.put(key.key, key);
            valueMap.put(key.value, key);
        }

    }
}