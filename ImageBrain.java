import com.baidu.aip.imageclassify.AipImageClassify;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * 百度图片识别分析器类，AI女友大脑，连接云端！
 发送http请求连接，百度智能AI开发平台
 */
public class ImageBrain {

    //百度API
    //设置APPID/AK/SK
    private static final String APP_ID = "18539727";
    private static final String API_KEY = "xWnqi7XFMH7OWNl5zpGi7Q6j";
    private static final String SECRET_KEY = "RB8kwntZ1K7jFLrq1aB3Upznj0A0hFbt";
    static AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);


    /**
     * 调用百度API分析图片结果
     * @param file
     * @return
     */
    public String analysisImage(File file) {
        //调用接口，返回分析对象
        JSONObject res = null;
        try {
            // 传入可选参数调用接口，参数
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("baike_num", "5");//返回百科信息的结果数，默认不返回
            //获取文件字节数组
            byte[] fileBytes = readFileByBytes(file.getAbsolutePath());
            //调用百度api分析，返回分析对象
            res = client.advancedGeneral(fileBytes, options);
            JSONArray resultArray = res.getJSONArray("result");
            //解析分析json对象
            if (resultArray != null) {
                //获取解析后的第一个结果
                JSONObject resultOne = resultArray.getJSONObject(0);
                JSONObject baike_info = resultOne.getJSONObject("baike_info");//获取解析信息
                if (baike_info.has("description")) {
                    String description = baike_info.getString("description");//获取分析结果中的百科介绍
                    description = description.substring(0,60);//截取部分显示
                    description = description + "...";
                    return "这个图片里是"+description;
                } else {
                    String keyword = (String) resultOne.get("keyword");
                    return "这个图片里是"+keyword;
                }
            }
            if(resultArray == null){
                return "油猴~！出错啦，错误原因："+res.toString(2);
            }
        } catch (Exception e) {
            System.out.println("出错啦，请检查一下网络是否通畅~");
            return "油猴~！出错啦，错误原因："+res.toString(2);
        }
        return "无！";
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }
                return bos.toByteArray();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }
}
