package com.jia.ezcamera.play;


/**
 * Created by ls on 15/2/14.
 */
public class StaticConstant {
	public static final String VIEW="view";
	public static final String ADAPTER="ADAPTER";
	public static final String DEVICE_ID = "devId";
    public static final String DEVICE_IP = "devIp";
    public static final String CHANNEL_ID = "channelId";
    public static final String FILE_PATH = "filePath";

    /**
     * �ɹ�����ֵ
     */
    public static final int RESULT_SUCESS=200;

    public static final String ITEM="item";
    public static final String CONNECTOR_WIFI_INFO="connectorWifiInfo";
    public static final String SSID="ssid";

    /**
     * ɾ��״̬     0:��     1:��
     */
    public static final int UNDELETE = 0;
    public static final int DELETING = 1;

    /**
     * ����¼���׺
     */
    public static final String VIDEO_SUFFIX = "_video.vveye";
    /**
     * ����¼��ͼƬ��׺
     */
    public static final String VIDEOPIC_SUFFIX = "_video.jpg";
    /**
     * ץ��ͼ���׺
     */
    public static final String PICTURE_SUFFIX = "_jpg.jpg";

    /**
     * �����豸�������ʱ��
     */
    public static final int SEARCH_TIME=3000;
    public static final int SEARCH_COUNT=30000;

    // 1=���߾�̬IP
    // 2=����DHCP
    // 3=���߾�̬IP()
    // 4=����DHCP
    public static int NETTYPE = 0;
    public static final int WIREDIP = NETTYPE+1;
    public static final int WIREDDHCP = WIREDIP+1;
    public static final int WIRELESSIP = WIREDDHCP+1;
    public static final int WIRELESSDHCP = WIRELESSIP+1;

    public static final int ACTIVITY_ADD_TO_SETAP=WIRELESSDHCP+1;
}
