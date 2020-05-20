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
 * AIŮ�����촰�ڽ���
 * ���촰�ڣ�
 * ��
 * �У��Ҳ�
 * ��
 *
 * ��ʼ���������
 * �����ϴ�ͼƬ�İ�ť
 * ��ʼ��������
 */
public class RobotChat extends JFrame implements ActionListener,MouseListener {


    //���������ϡ�
    private JPanel topPanel;//���
    private JLabel jbl_touxiang,jbl_friendname;//ͷ��,��������
    private JButton exit, min;//��С���͹رհ�ť
    //-----------------
    //����������ʾ��塾�С�
    private JTextPane msgPane;
    //�ұ������ұߡ�
    private JPanel rightPanel;//�ұ����
    private CardLayout cardLayout;//��Ƭ����
    //Ĭ���ұ������ʾһ��ͼ,�����ѯ�����¼��ť�л��������¼���
    private JLabel imageLabel = new JLabel(new ImageIcon("image/dialogimage/hh.png"));
    private JTextPane panel_Record;//�����¼��ʾ���
    //-----------------
    //�ײ������¡�
    private JPanel bottomPanel;//���
    //��Ϣ�������Ϸ�4�����ܰ�ť�����飬����ͼƬ����ͼ ���鿴��Ϣ��¼
    private JButton biaoqingBtn, sendImageBtn, jietuBtn,MsgRecordBtn;
    private JTextPane inputMsgArea;//��Ϣ������
    private JButton sendBtn, closeBtn;//��Ϣ�������·����رպͷ��Ͱ�ť
    //-----------------
    private String windowName = "�ҵ�AIŮ�ѣ�С��";//��������
    private static String myName = "�۸�";//��������
    private String newline = "\n";//����

    //ͼ�����
    private ImageBrain imageBrain;
    //���ܶԻ�����
    private TalkBrain talkBrain ;
    //��ǿ����
    private SuperBrain superBrain;


    public static void main(String[] args) {
        new RobotChat();
    }

    public RobotChat() {
        initChat();//��ʼ������AIŮ��
    }
    public RobotChat(String windowName) {
        this.windowName = windowName;
        initChat();//��ʼ������AIŮ��
    }
    public RobotChat(String robotName, String myName) {
        this.windowName = robotName;
        this.myName = myName;
        initChat();//��ʼ������AIŮ��
    }
    /**
     * ��ʼ������AIŮ��
     * 1�����ض��������塾�ϡ�
     * 2���в�����������ʾ���֡��С�
     * 3�����صײ���塾�¡�
     * 4��ע���������¼�
     * 5��������Ϣ
     */
    public void initChat(){
        //���ô��ھ��У�null == ��Ļ����
        //setLocationRelativeTo(null);
        //��ȡ��������
        Container container = this.getContentPane();
        container.setLayout(null);//���ò���
        //1�����ض��������塾�ϡ�
        loadTopPanel(container);
        //2���в�����������ʾ���֡��С�
        msgPane = new JTextPane();
        JScrollPane scrollPane_Msg = new JScrollPane(msgPane);
        scrollPane_Msg.setBounds(0, 92, 446, 270);
        container.add(scrollPane_Msg);
        //�����ұ����(ͼƬ�������¼)
        loadRightPanel(container);
        //3�����صײ���塾�¡�
        loadBottomPanel(container);
        //4��ע���������¼�
        registerListener();
        //5��������Ϣ
        this.setIconImage(new ImageIcon("image/dialogimage/Q.png").getImage());//�޸Ĵ���Ĭ��ͼ��
        this.setSize(728, 553);//���ô����С
        this.setUndecorated(true);//ȥ���Դ�װ�ο�
        this.setVisible(true);//���ô���ɼ�

        showMessage("�����ף�����С�ǣ���ò���ǻۼ���,�Ż���������ɡ�" +newline+
                "�ҿ������졢����Ц�����㿪�ģ���"+myName+"��ʵ��С���á�" +newline+
                "С�ǣ���֪����,��֪����,�����˺ͣ�������,������,�����Ŷݼס�" ,false);//AIŮ�ѣ�С�ǣ���ӭ��
    }

    /**
     * ע���������¼�����קЧ��
     */
    private boolean isDragged = false;//�����ק���ڱ�־
    private Point frameLocation;//��¼�����λ��
    //ע����������
    private void registerListener() {
        //ע������¼�������
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                //����ͷ�
                isDragged = false;
                //���ָ�
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            @Override
            public void mousePressed(MouseEvent e) {
                //��갴��
                //��ȡ�����Դ���λ��
                frameLocation = new Point(e.getX(),e.getY());
                isDragged = true;
                //����Ϊ�ƶ���ʽ
                if(e.getY() < 92)
                    setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }
        });
        //ע������¼�������
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //ָ����Χ�ڵ��������ק
                if(e.getY() < 92){
                    //����������ק�ƶ�
                    if(isDragged) {
                        Point loc = new Point(getLocation().x+e.getX()-frameLocation.x,
                                getLocation().y+e.getY()-frameLocation.y);
                        //��֤�����Դ���λ�ò���,ʵ���϶�
                        setLocation(loc);
                    }
                }
            }
        });
    }
    /**
     * �����ť�¼�
     * ����ͼƬ
     *
     * ��������
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //����ͼƬ
        if(e.getSource() == sendImageBtn){
            if (imageBrain == null){
                //��ʾ��Ϣ
                JOptionPane.showMessageDialog(this,"AI����СŮ�ѣ�ȱ�١���ͼ���ԡ�");
                return ;
            }
            //��ʾ�ļ��ϴ�
            Map imageContent = showFileOpenDialog(this,imageLabel,imageBrain);
            //����һ��ͼƬ��Ϣʶ��������
            //��ʾ��Ϣ
            String description = (String) imageContent.get(ImageType.DESCRIPTION);
            File file = (File) imageContent.get(ImageType.FILE);

            showMessage(file,true);//����ʾͼƬ

            showMessage(description,false);//AIŮ����ʾ����

        }
        //������Ϣ����
        if (e.getSource() == sendBtn) {
            String content = inputMsgArea.getText();
            if(StringUtils.isNoneBlank(content)){
                System.out.println("���ͳɹ�");

                String rsp = null;//�Ի������Ϣ
                //������ǿ���ԣ���ȥ������ԣ�������ǿ��������
                //��ǿ����
                if (superBrain == null){
                    //��ʾ��Ϣ
                    JOptionPane.showMessageDialog(this,"AI����СŮ�ѣ�ȱ�١���ǿ���ԡ�");
                    return ;
                }
                rsp = superBrain.solve(content);

                //���ܻỰ����
                if(StringUtils.isBlank(rsp)){
                    if (talkBrain == null){
                        //��ʾ��Ϣ
                        JOptionPane.showMessageDialog(this,"AI����СŮ�ѣ�ȱ�١����ܻỰ���ԡ�");
                        return ;
                    }
                    rsp= talkBrain.solve(content);
                }
                //���׻ظ�
                if(StringUtils.isBlank(rsp)){
                    rsp="�������ˣ���Ϣһ�������������~";
                }
                showMessage(content,true);//����ʾ����

                showMessage(rsp,false);//AIŮ�ѻ�Ӧ����
                inputMsgArea.setText("");
            }else{
                JOptionPane.showMessageDialog(this,"�װ��ģ�����˵ʲô��");
            }
        }
    }

    /**
     * ��ʾ����
     * @param msg
     * @param fromSelf true ���ң�false��AIŮ��
     */
    public void showMessage(String msg, boolean fromSelf) {
        showMessage(msgPane, msg, fromSelf);//����ʾ�������������
        showMessage(panel_Record, msg, fromSelf);//����ʾ�������¼���
    }

    /**
     * ��ʾͼƬ
     * @param file
     * @param fromSelf true ���ң�false��AIŮ��
     */
    public void showMessage(File file, boolean fromSelf) {
        showMessage(msgPane, file, fromSelf);//����ʾ�������������
        showMessage(panel_Record, file, fromSelf);//����ʾ�������¼���
    }

    /**
     * ����Ϣ������ʾ��ָ�����
     * @param jtp
     * @param msg
     * @param fromSelf
     */
    public void showMessage(JTextPane jtp,String msg, boolean fromSelf) {

        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        //������ʾ��ʽ
        //SimpleAttributeSet attrset = new SimpleAttributeSet();

        StyleConstants.setFontFamily(def, "����");
        StyleConstants.setFontSize(def,14);

        StyledDocument doc = jtp.getStyledDocument();
        String info = null;
        try {
            //������ʽ
            Style s = doc.addStyle("name", def);
            StyleConstants.setForeground(s, Color.MAGENTA);
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);//���þ�����ʾ
            //������ʽ
            s = doc.addStyle("msg", def);
            StyleConstants.setFontSize(s,16);
            StyleConstants.setForeground(s, Color.BLACK);
            if(fromSelf){//����ȥ����Ϣ����

                info = myName+"��"+newline;//�Լ��˺ţ���ɫ
                doc.insertString(doc.getLength(), info, doc.getStyle("name"));
                info = msg+" "+newline;//�������ݣ���ɫ
                doc.insertString(doc.getLength(), info, doc.getStyle("msg"));
            }else{//���յ�����Ϣ����
                info = windowName+newline;
                doc.insertString(doc.getLength(), info, doc.getStyle("name"));

                info = msg+" "+newline;//�������ݣ���ɫ
                doc.insertString(doc.getLength(), info, doc.getStyle("msg"));
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    /**
     * ��ͼƬ��ʾ��ָ�����
     * @param jtp
     * @param imgfile
     * @param fromSelf
     */
    public void showMessage(JTextPane jtp, File imgfile, boolean fromSelf) {
        //��ȡĬ����ʽ
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyledDocument doc = jtp.getStyledDocument();

        Style regular = doc.addStyle("regular", def);
        //������ʾ��ʽ
        StyleConstants.setFontFamily(regular, "����");
        StyleConstants.setFontSize(regular,14);
        String info = null;
        try {
            if(fromSelf){//����ȥ����Ϣ����

                Style s = doc.addStyle("name", regular);
                StyleConstants.setForeground(s, Color.MAGENTA);

                s = doc.addStyle("icon", regular);
                ImageIcon imageIcon = new ImageIcon(imgfile.getAbsolutePath());
                StyleConstants.setAlignment(s,StyleConstants.ALIGN_CENTER);//���þ�����ʾ
                StyleConstants.setIcon(s,imageIcon);//������ʾͼƬ

                info = myName+"��"+newline;//�Լ��˺ţ���ɫ
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
            System.err.println("�Ҳ����ļ� " + path);
            return null;
        }
    }

    /**
     * ���ض����������
     * @param container ��������
     */
    public JPanel loadTopPanel(Container container) {
        topPanel = new JPanel();
        topPanel.setBounds(0, 0, 729, 92);
        topPanel.setLayout(null);
        //��Ӷ������
        container.add(topPanel);
        //���Ͻǻ�ɫͷ��
        jbl_touxiang = new JLabel(new ImageIcon("image/dialogimage/huisetouxiang.png"));
        jbl_touxiang.setBounds(10, 10, 42, 42);
        topPanel.add(jbl_touxiang);
        //ͷ���ҷ���������ĶԷ�����
        jbl_friendname = new JLabel(windowName);
        jbl_friendname.setBounds(62, 21, 105, 20);
        topPanel.add(jbl_friendname);
        //���Ͻ���С����ť
        min = new JButton(new ImageIcon ("image/dialogimage/min.png"));
        min.addActionListener(e -> this.setExtendedState(JFrame.ICONIFIED));//���õ�ǰ������С��
        min.setBounds(668, 0, 30, 30);
        topPanel.add(min);
        //���Ͻǹرհ�ť
        exit = new JButton(new ImageIcon ("image/dialogimage/exit.jpg"));
        exit.addActionListener(e -> this.dispose());//���õ�ǰ�����˳�
        exit.setBounds(698, 0, 30, 30);
        topPanel.add(exit);
        //���ö�����屳��ɫ
        topPanel.setBackground(new Color(22, 154, 228));
        return topPanel;
    }
    /**
     * �ұ�����
     * @param container ��������
     */
    public  JPanel loadRightPanel(Container container) {
        //�ұ����
        rightPanel = new JPanel();
        //��Ƭ����
        cardLayout = new CardLayout(2,2);
        rightPanel.setLayout(cardLayout);
        rightPanel.setBounds(444, 91, 285, 418);
        //����ұ����
        container.add(rightPanel);
        //��ʾ�����¼���
        panel_Record = new JTextPane();
        panel_Record.setText("-----------------------------��¼--------------------------\n\n");
        JScrollPane scrollPane_Record = new JScrollPane(panel_Record);
        scrollPane_Record.setBounds(2, 2, 411, 410);
        //��ӵ��ұ����
        rightPanel.add(imageLabel);
        rightPanel.add(scrollPane_Record);
        return rightPanel;
    }

    /**
     * �ײ�����
     * @param container ���촰������
     */
    public JPanel loadBottomPanel(Container container) {
        bottomPanel = new JPanel();
        bottomPanel.setBounds(0, 370, 446, 170);
        bottomPanel.setLayout(null);
        //��ӵײ����
        container.add(bottomPanel);
        //����������
        inputMsgArea = new JTextPane();
        inputMsgArea.setBounds(0, 34, 446, 105);
        //��ӵ��ײ����
        bottomPanel.add(inputMsgArea);
        //�ı��������Ϸ����� ��ť
        //���ܰ�ť1������
        biaoqingBtn = new JButton(new ImageIcon("image/dialogimage/biaoqing.png"));
        biaoqingBtn.setBounds(10, 0, 30, 30);
        bottomPanel.add(biaoqingBtn);
        //���ܰ�ť2 ����ͼƬ ��ť
        sendImageBtn = new JButton(new ImageIcon("image/dialogimage/sendimage.jpg"));
        sendImageBtn.addActionListener(this);
        sendImageBtn.setBounds(45, 0, 30, 30);
        bottomPanel.add(sendImageBtn);
        //���ܰ�ť3 ��ͼ ��ť
        jietuBtn = new JButton(new ImageIcon("image/dialogimage/jietu.jpg"));
        jietuBtn.setBounds(80, 0, 30, 30);
        bottomPanel.add(jietuBtn);
        //��ѯ�����¼ ��ť
        MsgRecordBtn = new JButton(new ImageIcon("image/dialogimage/recorde.png"));
        MsgRecordBtn.addActionListener(e-> {
            System.out.println("������������¼");
            cardLayout.next(rightPanel);
        });
        MsgRecordBtn.setBounds(350, 0, 96, 30);
        bottomPanel.add(MsgRecordBtn);
        //��Ϣ�ر� ��ť
        closeBtn = new JButton(new ImageIcon("image/dialogimage/close.jpg"));
        closeBtn.setBounds(290, 145, 64, 24);
        closeBtn.addActionListener(e -> this.dispose());
        bottomPanel.add(closeBtn);
        //��Ϣ���� ��ť
        sendBtn = new JButton(new ImageIcon("image/dialogimage/send.jpg"));
        sendBtn.addActionListener(this);// TODO ���η��Ͱ�ť����ʵ��
        sendBtn.setBounds(381, 145, 64, 24);
        bottomPanel.add(sendBtn);

        return bottomPanel;
    }
    /**
     * ��ʾͼƬѡ����������
     * @param chat ���촰�ڶ���
     * @param imageLabel �Ҳ�ͼƬ��ʾ��ǩ
     * @param imageBrain
     */
    public Map showFileOpenDialog(Component chat , JLabel imageLabel, ImageBrain imageBrain) {

        Map mp = new HashMap();
        // ����һ��Ĭ�ϵ��ļ�ѡȡ��
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));// ����Ĭ����ʾ���ļ���Ϊ��ǰ�ļ���
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// �����ļ�ѡ���ģʽ��ֻѡ�ļ���ֻѡ�ļ��С��ļ����ļ�����ѡ��
        fileChooser.setMultiSelectionEnabled(false);// �����Ƿ������ѡ
        // ����Ĭ��ʹ�õ��ļ�������
        fileChooser.setFileFilter(new FileNameExtensionFilter("image(*.jpg, *.png, *.gif)", "jpg", "png", "gif"));
        // ���ļ�ѡ����߳̽�������, ֱ��ѡ��򱻹رգ�
        int result = fileChooser.showOpenDialog(chat);
        // ��������"ȷ��", ��ѡ���ļ����ݵ���Ѷ�ƽӿڣ�����ͼƬ�е�����
        if (result == JFileChooser.APPROVE_OPTION) {
            //��ȡѡ���ļ�
            File file = fileChooser.getSelectedFile();
            //����ѡ���ļ����Ҳ����
            //imageLabel.setIcon(new ImageIcon(file.getAbsolutePath()));
            // ���ýӿڣ�����ͼƬ������

            String description = imageBrain.analysisImage(file);

            mp.put(ImageType.FILE,file);
            mp.put(ImageType.DESCRIPTION,description);
            if (description != null)
                return mp;
        }
        mp.put(ImageType.DESCRIPTION,"����ѡ����Ҫʶ��ͼƬ��");
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