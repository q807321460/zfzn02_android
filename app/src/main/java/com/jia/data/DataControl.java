package com.jia.data;

import android.content.Context;
import android.content.res.TypedArray;

import com.jia.connection.WebSocket;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class DataControl
{


    private static DataControl mDC = new DataControl();    //单例对象
    public static final int KEY_AIR_POWER = 48153;
    public static final int KEY_AIR_POWER_OFF = 48154;
    public static final int KEY_AIR_COLD_16 = 48116;
    public static final int KEY_AIR_COLD_18 = 48118;
    public static final int KEY_AIR_COLD_20 = 48120;
    public static final int KEY_AIR_COLD_22 = 48122;
    public static final int KEY_AIR_COLD_24 = 48124;
    public static final int KEY_AIR_COLD_25 = 48125;
    public static final int KEY_AIR_COLD_26 = 48126;
    public static final int KEY_AIR_COLD_28 = 48128;
    public static final int KEY_AIR_COLD_30 = 48130;
    public static final int KEY_AIR_HOT_18 = 48218;
    public static final int KEY_AIR_HOT_20 = 48220;
    public static final int KEY_AIR_HOT_22 = 48222;
    public static final int KEY_AIR_HOT_24 = 48224;
    public static final int KEY_AIR_HOT_26 = 48226;
    public static final int KEY_AIR_HOT_28 = 48228;
    public static Integer[] airLearnCode = {
            KEY_AIR_POWER,
            KEY_AIR_POWER_OFF,
            KEY_AIR_COLD_16,
            KEY_AIR_COLD_18,
            KEY_AIR_COLD_20,
            KEY_AIR_COLD_22,
            KEY_AIR_COLD_24,
            KEY_AIR_COLD_25,
            KEY_AIR_COLD_26,
            KEY_AIR_COLD_28,
            KEY_AIR_COLD_30,
            KEY_AIR_HOT_18,
            KEY_AIR_HOT_20,
            KEY_AIR_HOT_22,
            KEY_AIR_HOT_24,
            KEY_AIR_HOT_26,
            KEY_AIR_HOT_28,
    };
    public static final int KEY_TV_POWER = 47151;
    public static final int KEY_TV_HOME = 47152;
    public static final int KEY_TV_TVAV = 47153;
    public static final int KEY_TV_MENU = 47154;
    public static final int KEY_TV_BACK = 47155;
    public static final int KEY_TV_MUTE = 47156;
    public static final int KEY_TV_VOLADD = 47157;
    public static final int KEY_TV_VOLSUB = 47158;
    public static final int KEY_TV_UP = 47159;
    public static final int KEY_TV_LEFT = 47160;
    public static final int KEY_TV_OK = 47161;
    public static final int KEY_TV_RIGHT = 47162;
    public static final int KEY_TV_DOWN = 47163;
    public static final int KEY_TV_CH_SUB = 47164;
    public static final int KEY_TV_CH_ADD = 47165;
    public static Integer[] tvLearnCode = {
            KEY_TV_POWER,
            KEY_TV_HOME,
            KEY_TV_TVAV,
            KEY_TV_MENU,
            KEY_TV_BACK,
            KEY_TV_MUTE,
            KEY_TV_VOLADD,
            KEY_TV_VOLSUB,
            KEY_TV_UP,
            KEY_TV_LEFT,
            KEY_TV_OK,
            KEY_TV_RIGHT,
            KEY_TV_DOWN ,
            KEY_TV_CH_SUB,
            KEY_TV_CH_ADD,
    };

    public static String ELECTRIC_TYPE_SOCKET = "00";   //0
    public static String ELECTRIC_TYPE_SWIFT_ONE = "01";    // 1
    public static String ELECTRIC_TYPE_SWIFT_TWO = "02";   //2
    public static String ELECTRIC_TYPE_SWIFT_THREE = "03";   //3
    public static String ELECTRIC_TYPE_SWIFT_FOUR = "04";   //4
    public static String ELECTRIC_TYPE_LOCK = "05";   //5
    public static String ELECTRIC_TYPE_CURTAIN = "06";   //6
    public static String ELECTRIC_TYPE_WINDOW = "07";   //7
    public static String ELECTRIC_TYPE_CAMERA = "08";   //8
    public static String ELECTRIC_TYPE_AIR = "09";   //9
    public static String ELECTRIC_TYPE_SCENE_SWIFT = "0A";   //10
    public static String ELECTRIC_TYPE_VALVE = "0B";   //11
    public static String ELECTRIC_TYPE_TV = "09";   //12
    public static String ELECTRIC_TYPE_TEMP = "0DA1";   //13
    public static String ELECTRIC_TYPE_WATER = "0D73";   //14
    public static String ELECTRIC_TYPE_DOOR = "0D31";   //15
    public static String ELECTRIC_TYPE_GAS = "0D41";   //16
    public static String ELECTRIC_TYPE_WALL_IR = "0D21";   //17
    public static String ELECTRIC_TYPE_HORN = "0E";   //18
    public static String ELECTRIC_TYPE_SMOKE = "0D51";   //19
    public static String ELECTRIC_TYPE_CLOTHES = "0F";   //20
    public static String ELECTRIC_TYPE_AIR1= "09";   //21
    public static String ELECTRIC_TYPE_CENTER_AIR="0800"; //22
    public static String ELECTRIC_TYPE_NEWDOOR="1000";//23
    public static String ELECTRIC_TYPE_TV1 = "09";   //24
    //public String[] electricTypeCode = {"00","01","02","03","04","05","06","07","08","09","0A","0B","09","0D","0D","0D","0D"};
    public static String[] electricTypeCode = {
            ELECTRIC_TYPE_SOCKET,
            ELECTRIC_TYPE_SWIFT_ONE,
            ELECTRIC_TYPE_SWIFT_TWO,
            ELECTRIC_TYPE_SWIFT_THREE,
            ELECTRIC_TYPE_SWIFT_FOUR,
            ELECTRIC_TYPE_LOCK,
            ELECTRIC_TYPE_CURTAIN,
            ELECTRIC_TYPE_WINDOW,
            ELECTRIC_TYPE_CAMERA,
            ELECTRIC_TYPE_AIR,
            ELECTRIC_TYPE_SCENE_SWIFT,
            ELECTRIC_TYPE_VALVE,
            ELECTRIC_TYPE_TV,
            ELECTRIC_TYPE_TEMP,
            ELECTRIC_TYPE_WATER,
            ELECTRIC_TYPE_DOOR,
            ELECTRIC_TYPE_GAS,
            ELECTRIC_TYPE_WALL_IR,
            ELECTRIC_TYPE_HORN,
            ELECTRIC_TYPE_SMOKE,
            ELECTRIC_TYPE_CLOTHES,
            ELECTRIC_TYPE_AIR1,
            ELECTRIC_TYPE_CENTER_AIR,
            ELECTRIC_TYPE_NEWDOOR,
            ELECTRIC_TYPE_TV1
    };
    public char orderSign = 'X', addSign = 'Y' , stateSign = 'Z';
    public char orderClose = 'G', orderOpen = 'H', orderStop = 'I';
    public char stateClose = 'W', stateOpen = 'V', stateStop = 'U';
    public char airInside='C',airSet='S',airWindLow='L',airWindMid='K',airWindHigh='J',airCold='M',
                airHeat='N',airWind='O',airAuto='P';
    public String addleft = "000000";

    public boolean bUseWeb = true;     //是否远程
    public boolean bUserHard = true;       //是否有硬件（主节点）
    public String sVer1 = "1.0";    //软件版本号，此处不同必须更新才能使用
    public String sVer2 = "0";
    public String sVer3 = "005";    //软件版本号，此处不同仍能使用，不是必须更新
    //public String appVersion = "1.0.006";
    public AppInfo appInfo;


    public String sAccountCode;        //账户名字
    public String sLePhoneNumber;
    public String sUserIP;      //账户IP
    public int iUserPort = 8899;       //账户端口
    public String sMasterCode;
    public boolean bIsRemote = true;       //是否远程
    public boolean bIsSearchMaster =true;
    public boolean socketCrash = false;

    public String sUrlDir;

    public DBHelper mDB;      //操作数据库的对象
    public WebService mWS;
    public WebSocket mWST;
    public TypedArray mSceneTypeImages;
    public TypedArray mAreaTypeImages;
    public TypedArray mElectricTypeImages;

    public AccountData mAccountData; //账户对象
    public RoomData mAreaData;      //区域对象，提供对区域的操作
    public ElectricData mElectricData;      //电器对象，提供对电器的操作
    public ElectricSharedData mElectricSharedData;
    public UserData mUserData;    //用户对象，提供对用户的操作
    public SceneData mSceneData;
    public SceneElectricData mSceneElectricData;

    public AccountData.AccountDataInfo mAccount;
    public ArrayList<RoomData.RoomDataInfo> mAreaList = new ArrayList<>();
    public ArrayList<ElectricInfoData> mElectricList = new ArrayList<>();
    public ArrayList<ElectricInfoData> mSensorList = new ArrayList<>();
    public ArrayList<ElectricInfoData> mSceneSwiftList = new ArrayList<>();
    public ArrayList<UserData.UserDataInfo> mUserList = new ArrayList();
    public List<AccountData.AccountDataInfo> mSharedAccountList = new ArrayList();
    public List<SceneData.SceneDataInfo> mSceneList = new ArrayList<>();
    public volatile HashMap<String, String[]> mElectricState;

    /**
     * 私有化构造器，确保该类不能再外部实例化
     */
    private DataControl() {}

    /**
     * 初始化必要数据
     * @param paramContext
     */
    public void init(Context paramContext)
    {
        mElectricState = new HashMap<>();
        appInfo = new AppInfo();
        mDB = new DBHelper(paramContext);
        mAccountData = new AccountData();
        mAccount = mAccountData.new AccountDataInfo();
        mAreaData = new RoomData();
        mElectricData = new ElectricData();
        mElectricSharedData = new ElectricSharedData();
        mUserData = new UserData();
        mSceneData = new SceneData();
        mSceneElectricData = new SceneElectricData();
        mWS = new WebService();
        mWST=new WebSocket();
        mDC.mSceneTypeImages = paramContext.getResources().obtainTypedArray(R.array.scene_type_images);
        mDC.mAreaTypeImages = paramContext.getResources().obtainTypedArray(R.array.area_type_press_images);
        mDC.mElectricTypeImages = paramContext.getResources().obtainTypedArray(R.array.dev_type_images);

    }

    public static DataControl getInstance()
    {
        return mDC;
    }
}
