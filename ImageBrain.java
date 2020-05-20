import com.baidu.aip.imageclassify.AipImageClassify;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * �ٶ�ͼƬʶ��������࣬AIŮ�Ѵ��ԣ������ƶˣ�
 ����http�������ӣ��ٶ�����AI����ƽ̨
 */
public class ImageBrain {

    //�ٶ�API
    //����APPID/AK/SK
    private static final String APP_ID = "18539727";
    private static final String API_KEY = "xWnqi7XFMH7OWNl5zpGi7Q6j";
    private static final String SECRET_KEY = "RB8kwntZ1K7jFLrq1aB3Upznj0A0hFbt";
    static AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);


    /**
     * ���ðٶ�API����ͼƬ���
     * @param file
     * @return
     */
    public String analysisImage(File file) {
        //���ýӿڣ����ط�������
        JSONObject res = null;
        try {
            // �����ѡ�������ýӿڣ�����
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("baike_num", "5");//���ذٿ���Ϣ�Ľ������Ĭ�ϲ�����
            //��ȡ�ļ��ֽ�����
            byte[] fileBytes = readFileByBytes(file.getAbsolutePath());
            //���ðٶ�api���������ط�������
            res = client.advancedGeneral(fileBytes, options);
            JSONArray resultArray = res.getJSONArray("result");
            //��������json����
            if (resultArray != null) {
                //��ȡ������ĵ�һ�����
                JSONObject resultOne = resultArray.getJSONObject(0);
                JSONObject baike_info = resultOne.getJSONObject("baike_info");//��ȡ������Ϣ
                if (baike_info.has("description")) {
                    String description = baike_info.getString("description");//��ȡ��������еİٿƽ���
                    description = description.substring(0,60);//��ȡ������ʾ
                    description = description + "...";
                    return "���ͼƬ����"+description;
                } else {
                    String keyword = (String) resultOne.get("keyword");
                    return "���ͼƬ����"+keyword;
                }
            }
            if(resultArray == null){
                return "�ͺ�~��������������ԭ��"+res.toString(2);
            }
        } catch (Exception e) {
            System.out.println("������������һ�������Ƿ�ͨ��~");
            return "�ͺ�~��������������ԭ��"+res.toString(2);
        }
        return "�ޣ�";
    }

    /**
     * �����ļ�·����ȡbyte[] ����
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
