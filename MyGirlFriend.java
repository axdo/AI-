/**
 * ������ͼ��ʶ���AIŮ��
 */
public class MyGirlFriend {
    public static void main(String[] args){
        //1������һ��ͼ��ʶ���AIŮ�Ѷ���AIŮ�ѻỰ����
        RobotChat r = new RobotChat();
        //2������AI����Ů��ͼ����ԣ��������õ�AIŮ����
        ImageBrain a = new ImageBrain();
        r.setImageBrain(a);
        //3������AI����Ů��������ǿ���ԣ��������õ�AIŮ����
        SuperBrain s = new SuperBrain();
        r.setSuperBrain(s);
        //4������AI����Ů���������ԣ��������õ�AIŮ����
        TalkBrain t = new TalkBrain();
        r.setTalkBrain(t);
    }
}