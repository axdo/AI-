import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * AI女友聊天窗口界面
 * 聊天窗口：
 * 上
 * 中，右侧
 * 下
 *
 * 初始化聊天界面
 * 聊天上传图片的按钮
 * 初始化监听器
 */
public class RobotChat extends JFrame implements ActionListener,MouseListener {


    //顶部区域【上】
    private JPanel topPanel;//面板
    private JLabel jbl_touxiang,jbl_friendname;//头像,好友名称
    private JButton exit, min;//最小化和关闭按钮
    //-----------------
    //聊天内容显示面板【中】
    private JTextPane msgPane;
    //右边区域【右边】
    private JPanel rightPanel;//右边面板
    private CardLayout cardLayout;//卡片布局
    //默认右边面板显示一张图,点击查询聊天记录按钮切换到聊天记录面板
    private JLabel imageLabel = new JLabel(new ImageIcon("image/dialogimage/hh.png"));
    private JTextPane panel_Record;//聊天记录显示面板
    //-----------------
    //底部区域【下】
    private JPanel bottomPanel;//面板
    //消息输入区上方4个功能按钮，表情，发送图片，截图 ，查看消息记录
    private JButton biaoqingBtn, sendImageBtn, jietuBtn,MsgRecordBtn;
    private JTextPane inputMsgArea;//消息输入区
    private JButton sendBtn, closeBtn;//消息输入区下方，关闭和发送按钮
    //-----------------
    private String windowName = "我的AI女友：小乔";//窗口名称
    private static String myName = "雄哥";//窗口名称
    private String newline = "\n";//换行

    //图像大脑
    private ImageBrain imageBrain;
    //智能对话大脑
    private TalkBrain talkBrain ;
    //增强大脑
    private SuperBrain superBrain;


    public static void main(String[] args) {
        new RobotChat();
    }

    public RobotChat() {
        initChat();//初始化聊天AI女友
    }
    public RobotChat(String windowName) {
        this.windowName = windowName;
        initChat();//初始化聊天AI女友
    }
    public RobotChat(String robotName, String myName) {
        this.windowName = robotName;
        this.myName = myName;
        initChat();//初始化聊天AI女友
    }
    /**
     * 初始化聊天AI女友
     * 1、加载顶部面板面板【上】
     * 2、中部聊天内容显示部分【中】
     * 3、加载底部面板【下】
     * 4、注册鼠标监听事件
     * 5、窗口信息
     */
    public void initChat(){
        //设置窗口居中，null == 屏幕居中
        //setLocationRelativeTo(null);
        //获取窗口容器
        Container container = this.getContentPane();
        container.setLayout(null);//设置布局
        //1、加载顶部面板面板【上】
        loadTopPanel(container);
        //2、中部聊天内容显示部分【中】
        msgPane = new JTextPane();
        JScrollPane scrollPane_Msg = new JScrollPane(msgPane);
        scrollPane_Msg.setBounds(0, 92, 446, 270);
        container.add(scrollPane_Msg);
        //加载右边面板(图片和聊天记录)
        loadRightPanel(container);
        //3、加载底部面板【下】
        loadBottomPanel(container);
        //4、注册鼠标监听事件
        registerListener();
        //5、窗口信息
        this.setIconImage(new ImageIcon("image/dialogimage/Q.png").getImage());//修改窗体默认图标
        this.setSize(728, 553);//设置窗体大小
        this.setUndecorated(true);//去掉自带装饰框
        this.setVisible(true);//设置窗体可见

        showMessage("亲亲亲，我是小乔；美貌与智慧加身,才华与妩媚齐飞。" +newline+
                "我可以聊天、陪你笑、逗你开心，是"+myName+"忠实的小迷妹。" +newline+
                "小乔：上知天文,下知地理,中晓人和；明阴阳,懂八卦,晓奇门遁甲。" ,false);//AI女友：小乔，欢迎语
    }

    /**
     * 注册鼠标监听事件，拖拽效果
     */
    private boolean isDragged = false;//鼠标拖拽窗口标志
    private Point frameLocation;//记录鼠标点击位置
    //注册鼠标监听器
    private void registerListener() {
        //注册鼠标事件监听器
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //鼠标释放
                isDragged = false;
                //光标恢复
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                //鼠标按下
                //获取鼠标相对窗体位置
                frameLocation = new Point(e.getX(),e.getY());
                isDragged = true;
                //光标改为移动形式
                if(e.getY() < 92)
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        });
        //注册鼠标事件监听器
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //指定范围内点击鼠标可拖拽
                if(e.getY() < 92){
                    //如果是鼠标拖拽移动
                    if(isDragged) {
                        Point loc = new Point(getLocation().x+e.getX()-frameLocation.x,
                                getLocation().y+e.getY()-frameLocation.y);
                        //保证鼠标相对窗体位置不变,实现拖动
                        setLocation(loc);
                    }
                }
            }
        });
    }
    /**
     * 点击按钮事件
     * 发送图片
     *
     * 发送文字
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //发送图片
        if(e.getSource() == sendImageBtn){
            if (imageBrain == null){
                //显示信息
                JOptionPane.showMessageDialog(this,"AI智能小女友，缺少【看图大脑】");
                return ;
            }
            //显示文件上传
            Map imageContent = showFileOpenDialog(this,imageLabel,imageBrain);
            //构造一个图片消息识别后的内容
            //显示信息
            String description = (String) imageContent.get(ImageType.DESCRIPTION);
            File file = (File) imageContent.get(ImageType.FILE);

            showMessage(file,true);//我显示图片

            showMessage(description,false);//AI女友显示内容

        }
        //发送消息内容
        if (e.getSource() == sendBtn) {
            String content = inputMsgArea.getText();
            if(StringUtils.isNoneBlank(content)){
                System.out.println("发送成功");

                String rsp = null;//对话结果信息
                //先走增强大脑，再去聊天大脑，否则增强不管用了
                //增强大脑
                if (superBrain == null){
                    //显示信息
                    JOptionPane.showMessageDialog(this,"AI智能小女友，缺少【增强大脑】");
                    return ;
                }
                rsp = superBrain.solve(content);

                //智能会话大脑
                if(StringUtils.isBlank(rsp)){
                    if (talkBrain == null){
                        //显示信息
                        JOptionPane.showMessageDialog(this,"AI智能小女友，缺少【智能会话大脑】");
                        return ;
                    }
                    rsp= talkBrain.solve(content);
                }
                //兜底回复
                if(StringUtils.isBlank(rsp)){
                    rsp="宝宝累了，休息一会儿再陪您聊天~";
                }
                showMessage(content,true);//我显示内容

                showMessage(rsp,false);//AI女友回应内容
                inputMsgArea.setText("");
            }else{
                JOptionPane.showMessageDialog(this,"亲爱的，你想说什么？");
            }
        }
    }

    /**
     * 显示内容
     * @param msg
     * @param fromSelf true 是我，false是AI女友
     */
    public void showMessage(String msg, boolean fromSelf) {
        showMessage(msgPane, msg, fromSelf);//先显示到聊天内容面板
        showMessage(panel_Record, msg, fromSelf);//再显示到聊天记录面板
    }

    /**
     * 显示图片
     * @param file
     * @param fromSelf true 是我，false是AI女友
     */
    public void showMessage(File file, boolean fromSelf) {
        showMessage(msgPane, file, fromSelf);//先显示到聊天内容面板
        showMessage(panel_Record, file, fromSelf);//再显示到聊天记录面板
    }

    /**
     * 将消息内容显示到指定面板
     * @param jtp
     * @param msg
     * @param fromSelf
     */
    public void showMessage(JTextPane jtp,String msg, boolean fromSelf) {

        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        //设置显示格式
        //SimpleAttributeSet attrset = new SimpleAttributeSet();

        StyleConstants.setFontFamily(def, "仿宋");
        StyleConstants.setFontSize(def,14);

        StyledDocument doc = jtp.getStyledDocument();
        String info = null;
        try {
            //标题样式
            Style s = doc.addStyle("name", def);
            StyleConstants.setForeground(s, Color.MAGENTA);
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);//设置居中显示
            //内容样式
            s = doc.addStyle("msg", def);
            StyleConstants.setFontSize(s,16);
            StyleConstants.setForeground(s, Color.BLACK);
            if(fromSelf){//发出去的消息内容

                info = myName+"："+newline;//自己账号：紫色
                doc.insertString(doc.getLength(), info, doc.getStyle("name"));
                info = msg+" "+newline;//发送内容：黑色
                doc.insertString(doc.getLength(), info, doc.getStyle("msg"));
            }else{//接收到的消息内容
                info = windowName+newline;
                doc.insertString(doc.getLength(), info, doc.getStyle("name"));

                info = msg+" "+newline;//发送内容：蓝色
                doc.insertString(doc.getLength(), info, doc.getStyle("msg"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将图片显示到指定面板
     * @param jtp
     * @param imgfile
     * @param fromSelf
     */
    public void showMessage(JTextPane jtp, File imgfile, boolean fromSelf) {
        //获取默认样式
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyledDocument doc = jtp.getStyledDocument();

        Style regular = doc.addStyle("regular", def);
        //设置显示格式
        StyleConstants.setFontFamily(regular, "仿宋");
        StyleConstants.setFontSize(regular,14);
        String info = null;
        try {
            if(fromSelf){//发出去的消息内容

                Style s = doc.addStyle("name", regular);
                StyleConstants.setForeground(s, Color.MAGENTA);

                s = doc.addStyle("icon", regular);
                ImageIcon imageIcon = new ImageIcon(imgfile.getAbsolutePath());
                StyleConstants.setAlignment(s,StyleConstants.ALIGN_CENTER);//设置居中显示
                StyleConstants.setIcon(s,imageIcon);//设置显示图片

                info = myName+"："+newline;//自己账号：紫色
                doc.insertString(doc.getLength(), info,  doc.getStyle("name"));

                doc.insertString(doc.getLength(), " "+newline, doc.getStyle("icon"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        URL imgURL = RobotChat.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("找不到文件 " + path);
            return null;
        }
    }

    /**
     * 加载顶部聊天面板
     * @param container 窗口容器
     */
    public JPanel loadTopPanel(Container container) {
        topPanel = new JPanel();
        topPanel.setBounds(0, 0, 729, 92);
        topPanel.setLayout(null);
        //添加顶部面板
        container.add(topPanel);
        //左上角灰色头像
        jbl_touxiang = new JLabel(new ImageIcon("image/dialogimage/huisetouxiang.png"));
        jbl_touxiang.setBounds(10, 10, 42, 42);
        topPanel.add(jbl_touxiang);
        //头像右方正在聊天的对方姓名
        jbl_friendname = new JLabel(windowName);
        jbl_friendname.setBounds(62, 21, 105, 20);
        topPanel.add(jbl_friendname);
        //右上角最小化按钮
        min = new JButton(new ImageIcon ("image/dialogimage/min.png"));
        min.addActionListener(e -> this.setExtendedState(JFrame.ICONIFIED));//设置当前窗口最小化
        min.setBounds(668, 0, 30, 30);
        topPanel.add(min);
        //右上角关闭按钮
        exit = new JButton(new ImageIcon ("image/dialogimage/exit.jpg"));
        exit.addActionListener(e -> this.dispose());//设置当前窗户退出
        exit.setBounds(698, 0, 30, 30);
        topPanel.add(exit);
        //设置顶部面板背景色
        topPanel.setBackground(new Color(22, 154, 228));
        return topPanel;
    }
    /**
     * 右边区域
     * @param container 窗口容器
     */
    public  JPanel loadRightPanel(Container container) {
        //右边面板
        rightPanel = new JPanel();
        //卡片布局
        cardLayout = new CardLayout(2,2);
        rightPanel.setLayout(cardLayout);
        rightPanel.setBounds(444, 91, 285, 418);
        //添加右边面板
        container.add(rightPanel);
        //显示聊天记录面板
        panel_Record = new JTextPane();
        panel_Record.setText("-----------------------------记录--------------------------\n\n");
        JScrollPane scrollPane_Record = new JScrollPane(panel_Record);
        scrollPane_Record.setBounds(2, 2, 411, 410);
        //添加到右边面板
        rightPanel.add(imageLabel);
        rightPanel.add(scrollPane_Record);
        return rightPanel;
    }

    /**
     * 底部区域
     * @param container 聊天窗口容器
     */
    public JPanel loadBottomPanel(Container container) {
        bottomPanel = new JPanel();
        bottomPanel.setBounds(0, 370, 446, 170);
        bottomPanel.setLayout(null);
        //添加底部面板
        container.add(bottomPanel);
        //内容输入区
        inputMsgArea = new JTextPane();
        inputMsgArea.setBounds(0, 34, 446, 105);
        //添加到底部面板
        bottomPanel.add(inputMsgArea);
        //文本输入区上方功能 按钮
        //功能按钮1；表情
        biaoqingBtn = new JButton(new ImageIcon("image/dialogimage/biaoqing.png"));
        biaoqingBtn.setBounds(10, 0, 30, 30);
        bottomPanel.add(biaoqingBtn);
        //功能按钮2 发送图片 按钮
        sendImageBtn = new JButton(new ImageIcon("image/dialogimage/sendimage.jpg"));
        sendImageBtn.addActionListener(this);
        sendImageBtn.setBounds(45, 0, 30, 30);
        bottomPanel.add(sendImageBtn);
        //功能按钮3 截图 按钮
        jietuBtn = new JButton(new ImageIcon("image/dialogimage/jietu.jpg"));
        jietuBtn.setBounds(80, 0, 30, 30);
        bottomPanel.add(jietuBtn);
        //查询聊天记录 按钮
        MsgRecordBtn = new JButton(new ImageIcon("image/dialogimage/recorde.png"));
        MsgRecordBtn.addActionListener(e-> {
            System.out.println("点击查找聊天记录");
            cardLayout.next(rightPanel);
        });
        MsgRecordBtn.setBounds(350, 0, 96, 30);
        bottomPanel.add(MsgRecordBtn);
        //消息关闭 按钮
        closeBtn = new JButton(new ImageIcon("image/dialogimage/close.jpg"));
        closeBtn.setBounds(290, 145, 64, 24);
        closeBtn.addActionListener(e -> this.dispose());
        bottomPanel.add(closeBtn);
        //消息发送 按钮
        sendBtn = new JButton(new ImageIcon("image/dialogimage/send.jpg"));
        sendBtn.addActionListener(this);// TODO 屏蔽发送按钮，待实现
        sendBtn.setBounds(381, 145, 64, 24);
        bottomPanel.add(sendBtn);

        return bottomPanel;
    }
    /**
     * 显示图片选择器弹出框
     * @param chat 聊天窗口对象
     * @param imageLabel 右侧图片显示标签
     * @param imageBrain
     */
    public Map showFileOpenDialog(Component chat , JLabel imageLabel, ImageBrain imageBrain) {

        Map mp = new HashMap();
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));// 设置默认显示的文件夹为当前文件夹
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setMultiSelectionEnabled(false);// 设置是否允许多选
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）
        int result = fileChooser.showOpenDialog(chat);
        // 如果点击了"确定", 则将选择文件传递到腾讯云接口，解析图片中的内容
        if (result == JFileChooser.APPROVE_OPTION) {
            //获取选中文件
            File file = fileChooser.getSelectedFile();
            //设置选中文件到右侧面板
            //imageLabel.setIcon(new ImageIcon(file.getAbsolutePath()));
            // 调用接口，分析图片中人物

            String description = imageBrain.analysisImage(file);

            mp.put(ImageType.FILE,file);
            mp.put(ImageType.DESCRIPTION,description);
            if (description != null)
                return mp;
        }
        mp.put(ImageType.DESCRIPTION,"请您选择需要识别图片！");
        return mp;
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}


    public ImageBrain getImageBrain() {
        return imageBrain;
    }

    public void setImageBrain(ImageBrain imageBrain) {
        this.imageBrain = imageBrain;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public TalkBrain getTalkBrain() {
        return talkBrain;
    }

    public void setTalkBrain(TalkBrain talkBrain) {
        this.talkBrain = talkBrain;
    }

    public SuperBrain getSuperBrain() {
        return superBrain;
    }

    public void setSuperBrain(SuperBrain superBrain) {
        this.superBrain = superBrain;
    }
}

enum ImageType {
    FILE,DESCRIPTION
}