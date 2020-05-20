import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ���ܶԻ�����
 */
public class TalkBrain {
    private Gson gson = new Gson();


    public String solve(String content) {
        //�Ի���������
        String rsp = null;
        DefaultTalkBrain defaultTalkBrain = new DefaultTalkBrain();

        rsp = defaultTalkBrain.solve(content);

        if (StringUtils.isBlank(rsp)){
            String formatContent = content.replace("С��","�Ʒ�");
            try {
                String url = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + URLEncoder.encode(formatContent, "UTF-8");
                long start = System.currentTimeMillis();
                OkHttpUtils.Response response = OkHttpUtils.get(url);
                if (200 == response.getStatusCode()) {
                    QykResult qykResult = gson.fromJson(response.getBody(), QykResult.class);
                    if (0 == qykResult.getResult()) {
                        rsp = qykResult.getContent();
                        rsp = rsp.replace("�Ʒ�", "С��");
                        rsp = rsp.replace("�߽��", "�۸�");
                        rsp = rsp.replace("����", "�۸�");
						rsp = rsp.replace("����", "�۸�");
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
 * Ĭ�϶Ի���������ƥ��
 */
class DefaultTalkBrain {

    Map<String, List<String>> regexMap = new HashMap<String, List<String>>() {{
        put("^\\S{0,3}((��?(��ʲô)(����)?)|(��?(��?����)(��ʲô)?(����)?)|(��ô�ƺ�(��|��)?))\\S{0,3}$", Arrays.asList(
                "�Ϲ���ʧ�������ҵ����ֽ�С�ǣ�����ϱ��߹~",
                "С���ˣ�С��ѽ~",
                "�ȿȣ����Ǵ������ǻ۲����С��Ů�����İ�檣�����С��~"
        ));
        put("^(��ѯ?)?����(Ԥ��)?$", Arrays.asList(

                "�����С�ǣ���Ҫ��ĳ���Ŷ�����磺��������"
        ));
        put("\\S{0,5}(˭?��?������?��?��?)|(��������?)\\S{0,5}", Arrays.asList(
                "�Ͷ�����������~",
                "�ǲ������ð���������˧��~����~"
        ));
        put("(С��)?[\\,,��]?\\s?��?��?��?��?\\S{0,2}((���)|(����)|(����))\\S{0,4}", Arrays.asList(
                "ֱ����Ů���������Ƿǳ�����ò��Ŷ~�˼Һ�������~",
                "����˵����������������~"
        ));
        put("(С��)?\\S{0,2}��?((����)|(����)|(����))?((��)|([H,h]ello))\\S{0,2}", Arrays.asList(
                "*^_^*�úú�~",
                "һ�㶼����",
                "��ã��ҾͿ�����",
                "Ӵ~ ���ö���",
                "����ô��"

        ));
        put("((��ô��)|(Ϊʲô)|([W,w]hy))\\S{0,2}", Arrays.asList(
                "�Լ�����~",
                "����֪�������Ǳ�����˵"
        ));
        put("(\\S{0,5}��((����)|(�ֵ�))\\S{0,5})|((�ҵ�)?((����)|(�ֵ�)))", Arrays.asList(
                "�ð�~������������ϱ������~"
        ));
        put("\\S{0,5}((�ܸ�����ʶ��)|(�ܸ���))\\S{0,5}", Arrays.asList(
                "ɵ�ϣ���Ҳ��",
                "��Ҳһ������ʶ��ܸ���"
        ));
        put("[��,������]{2,5}\\S{0,3}", Arrays.asList(
                "Цë~",
                "����Ц�����ɵ��һ��~",
                "Ц����ô���ģ����Ʊ�н��˰ɣ�������"
        ));
        put("(((��)|(С��))?.{0,5}��\\S{0,4})", Arrays.asList(
                "С�����Դ��ǲ��ͣ��ҵ��Ϲ����۸磬����С��һ��ѧϰ��̰ɣ��ٺ�~"
        ));
        //С��С�Ǹ����ң�˭����������˧�����ˣ�
        put("\\S*��˧\\S*����\\S*", Arrays.asList(
                "��Ȼ���ҵ��Ϲ��۸������˳ƴ��������棬����һ�磬���ǲ��ͽ��ƽ�ʦ������������ƽУ������С��һ��ѧϰ��̰ɣ��ٺ�~"
        ));
    }};

    public String solve(String content) {
        System.out.println("[����api]userId:{},content:{ "+content+" }");
        Optional<Map.Entry<String, List<String>>> first = regexMap.entrySet().stream().filter(e -> content.matches(e.getKey())).findFirst();
        if (first.isPresent()) {
            System.out.println("[����api]userId:{},content:{ "+content+" }");
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
            .connectTimeout(60, TimeUnit.SECONDS)//�������ӳ�ʱʱ��
            .readTimeout(30, TimeUnit.SECONDS)//���ö�ȡ��ʱʱ��
            .writeTimeout(30,TimeUnit.SECONDS)
            .build();
    /**
     * http-get����
     *
     * @param url     �����ַ
     * @param headers ����ͷ
     *
     * @return ��Ӧ����
     *
     * @throws IOException io�쳣
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
     * http-get����
     *
     * @param url �����ַ
     *
     * @return ��Ӧ����
     *
     * @throws IOException io�쳣
     */
    public static Response get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * http-post����
     *
     * @param request ������Ϣ
     *
     * @return ��Ӧ����
     *
     * @throws IOException ����ʧ��
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
     * ��Ӧbean
     */
    public final static class Response implements Serializable {

        /**
         * http״̬��
         */
        private int statusCode;

        /**
         * ������
         */
        private String body;

        /**
         * ��ַ
         */
        private String url;

        /**
         * ����ͷ
         */
        private Map<String, String> headers;

        /**
         * ����ͷ
         */
        public String getHeader(String name) {
            if (headers != null && !headers.isEmpty()) {
                return headers.get(name);
            }
            return null;
        }

        /**
         * http״̬��
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * http״̬��
         */
        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        /**
         * ������
         */
        public String getBody() {
            return body;
        }

        /**
         * ������
         */
        public void setBody(String body) {
            this.body = body;
        }

        /**
         * ��ַ
         */
        public String getUrl() {
            return url;
        }

        /**
         * ��ַ
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * ����ͷ
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * ����ͷֵ
         */
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }

    /**
     * ����bean
     */
    public static final class Request implements Serializable {

        public Request(String url) {
            this.url = url;
        }

        /**
         * ��ַ
         */
        private String url;

        /**
         * ����ͷ
         */
        private Map<String, String> headers;

        /**
         * content-typeֵ
         */
        private String contentType;

        /**
         * post����������
         */
        private String body;

        /**
         * post����������, �����Ƹ�ʽ
         */
        private byte[] bodyData;

        /**
         * ��ַ
         */
        public String getUrl() {
            return url;
        }

        /**
         * ��ַ
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * ����ͷ
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * ����ͷ
         */
        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        /**
         * content-typeֵ
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * content-typeֵ
         */
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        /**
         * post����������
         */
        public String getBody() {
            return body;
        }

        /**
         * post����������
         */
        public void setBody(String body) {
            this.body = body;
        }

        /**
         * post����������, �����Ƹ�ʽ
         */
        public byte[] getBodyData() {
            return bodyData;
        }

        /**
         * post����������, �����Ƹ�ʽ
         */
        public void setBodyData(byte[] bodyData) {
            this.bodyData = bodyData;
        }

        /**
         * ����ͷֵ
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
    FORM(Integer.valueOf(1), "application/x-www-form-urlencoded", "���ύ"),
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