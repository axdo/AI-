/**
 * 案例：图像识别的AI女友
 */
public class MyGirlFriend {
    public static void main(String[] args){
        //1、创建一个图像识别的AI女友对象，AI女友会话窗口
        RobotChat r = new RobotChat();
        //2、创建AI智能女友图像大脑，并且设置到AI女友中
        ImageBrain a = new ImageBrain();
        r.setImageBrain(a);
        //3、创建AI智能女友语音增强大脑，并且设置到AI女友中
        SuperBrain s = new SuperBrain();
        r.setSuperBrain(s);
        //4、创建AI智能女友语音大脑，并且设置到AI女友中
        TalkBrain t = new TalkBrain();
        r.setTalkBrain(t);
    }
}