package com.jia.ezcamera.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.jia.ezcamera.play.FileUtils;
import com.jia.ezcamera.play.StaticConstant;
import com.jia.ezcamera.play.tool_file;
import com.jia.ezcamera.utils.CPlayParams;
import com.jia.ezcamera.utils.StringUtils;
import com.jia.znjj2.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import uk.co.senab.photoview.PhotoView;
import vv.ppview.PpviewClientInterface;
import vv.ppview.PpviewClientInterface.OnC2dEventCallback;
import vv.tool.gsonclass.item_c2devents;



/**
 * Created by ls on 15/1/29.
 */
public class ScanPicActivity extends Activity{
    private  static final String TAG=ScanPicActivity.class.getSimpleName();
    private static final int GET_PIC_CALLBACK=1;
    
    void doBack(){
        ScanPicActivity.this.finish();
    }
    ImageButton mBtnBack;
    TextView mTextTime;
    TextView mTextType;
    TextView mTextLong;
    ViewPagerFixed mViewpager;

    private void initView(){
    	mBtnBack = (ImageButton)findViewById(R.id.btn_return);
    	mBtnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	mTextTime = (TextView)findViewById(R.id.text_time);
    	mTextType = (TextView)findViewById(R.id.text_type);
    	mTextLong = (TextView)findViewById(R.id.text_long);
    	mViewpager = (ViewPagerFixed)findViewById(R.id.pager);
    }
    private Context myContext=null;
    private static playbackItem mItem = null;
    private CPlayParams cp = null;
    private long myConnector=0;
    private int myType=-1;
    PpviewClientInterface onvif_c2s = PpviewClientInterface.getInstance();
    tool_file tf;
    SamplePagerAdapter myAdapter=null;
    static ArrayList<showImgItem> listPath=null;
//    private ArrayList<View> viewList=null;
    private static int pageIndex=0;//��ʾviewpager�ĵڼ�ҳ
    private DelPicUtils delPicUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanpic);
        initView();
        myContext=this;
//        viewList=new ArrayList<View>();
        tf=tool_file.getInstance(myContext);
        delPicUtils = new DelPicUtils(tf.GetLocalAlarmPath(), 200);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).build();
            ImageLoader.getInstance().init(config);
        }
        Intent intent = getIntent();
        if (intent != null) {
            myConnector=intent.getLongExtra("connector",0);
            mItem = (playbackItem) intent.getSerializableExtra("item");
            cp = (CPlayParams) mItem.cam;
            myType=intent.getIntExtra("type",1);
        }
        listPath=new ArrayList<showImgItem>();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onvif_c2s.setOnC2dEventCallback(onC2dEventCallback);
        getPic();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mItem=null;
        super.onDestroy();
    }

    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int result=msg.arg1;
            switch (msg.what){
                case GET_PIC_CALLBACK:
                    int index=msg.arg2;
                    if (index>=0&&index<listPath.size()) {
                        listPath.get(index).imgPath= (String) msg.obj;
                        refreshAdapter();
                        ifNeedGetPic(++index);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void init(){
        myAdapter=new SamplePagerAdapter();
        mViewpager.setAdapter(myAdapter);
        mViewpager.setOnPageChangeListener(pageChangeListener);
        if (mItem!=null) {
            mTextTime.setText(myContext.getResources().getString(R.string.time_of_cam) + getPlayTime(mItem.event_time,TIME_TYPE2));
            mTextType.setText(myContext.getResources().getString(R.string.type_of_cam)+ StringUtils.getEventTypeString(myContext, mItem.event_type));
            mTextLong.setVisibility(View.GONE);
        }
    }

    private  static final String TIME_TYPE1="yyyyMMddHHmmss";
    private  static final String TIME_TYPE2="HH:mm:ss";
    private String getPlayTime(Long time,String type) {
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(type);
        simpleDateFormat2.setTimeZone(TimeZone.getDefault());
        String myTime = simpleDateFormat2.format(time * 1000);
        return myTime;
    }

    private void getPic(){
        boolean ifGetPic=true;
        if (mItem!=null) {
            for (int i=0;i<mItem.snap;i++) {
                String path=getPicName(i);
                showImgItem sii=new showImgItem();
                sii.index=i;
                if (FileUtils.isFileExist(path)){
                    sii.imgPath=path;
                }
                if (ifGetPic&& TextUtils.isEmpty(sii.imgPath)) {
                    ifGetPic=false;
                    c2dGetEventPic(i,path);
                }
                if (!listPath.contains(sii)) {
                    listPath.add(sii);
                }
            }
        }
    }

    private void ifNeedGetPic(int index){
        if (index<0||listPath==null||index>=listPath.size()){
            return;
        }
        showImgItem sii=listPath.get(index);
        if (sii.index==index&&TextUtils.isEmpty(sii.imgPath)){
            c2dGetEventPic(index,getPicName(index));
        }else{
            ifNeedGetPic(++index);
        }
    }

    private String getPicName(int index){
        return mItem==null?"":(tf.GetLocalAlarmPath()+mItem.eventId+index+".jpg");
    }

    private void c2dGetEventPic(int index,String fileName){
        if (myConnector!=0&&!TextUtils.isEmpty(fileName)) {
            Log.i(TAG,"c2dGetEventPic      mItem.eventId="+mItem.eventId+"     index="+index+"     fileName="+fileName);
        	delPicUtils.DelUnnecessaryPic();//ɾ��������ļ�
            onvif_c2s.c2d_getEventPic_fun(myConnector, myType, mItem.eventId, index, fileName);
        }
    }

    class showImgItem{
        public  int index;
        public String imgPath;
    }

    private void refreshAdapter(){
        myAdapter.notifyDataSetChanged();
        /*if (pageIndex>=0&&pageIndex<mViewpager.getChildCount()) {
            Log.i(TAG, "refreshAdapter    pageIndex=" + pageIndex);
            mViewpager.setCurrentItem(pageIndex);
        }*/
    }

    class SamplePagerAdapter extends PagerAdapter {
    	
    	private ArrayList<Boolean> isGetPicList;
    	
    	public SamplePagerAdapter() {
			// TODO Auto-generated constructor stub
    		isGetPicList = new ArrayList<Boolean>();
    		for(int i =0 ;i<mItem.snap;i++){
    			isGetPicList.add(false);
    		}
		}

        @Override
        public int getCount() {
            return mItem!=null?mItem.snap:0;
        }

        @Override
        public View instantiateItem(ViewGroup container,final int position) {
//            Log.i(TAG,"instantiateItem    position="+position);
            RelativeLayout rl=new RelativeLayout(container.getContext());
            rl.setTag(position);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            PhotoView photoView = new PhotoView(container.getContext());
            
           
            rl.addView(photoView,lp);
            ProgressBar pb=new ProgressBar(container.getContext());
            RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams(
                    64, 64);
            lpPb.addRule(RelativeLayout.CENTER_IN_PARENT);
            rl.addView(pb,lpPb);
            if (listPath!=null){
                int listSize=listPath.size();
                if (listSize>0&&position<listSize){
                    String filtP=listPath.get(position).imgPath;
                    if (FileUtils.isFileExist(filtP)){
                    	isGetPicList.set(position, true);
                        pb.setVisibility(View.GONE);
//                        Log.i(TAG,"instantiateItem    filtp="+filtP);
                        ImageLoader.getInstance().displayImage("file://"+filtP, photoView);
                        if(cp.fishEyeInfo.fishType!=0){
                       	 photoView.setOnDoubleTapListener(new OnDoubleTapListener() {
                				
                				@Override
                				public boolean onSingleTapConfirmed(MotionEvent e) {
                					// TODO Auto-generated method stub
                					return false;
                				}
                				
                				@Override
                				public boolean onDoubleTapEvent(MotionEvent e) {
                					// TODO Auto-generated method stub
                					Log.e("DEBUG", "onDoubleTapEvent");
                					String filtP=listPath.get(position).imgPath;
                					 Intent intent = new Intent();
                			            intent.setClass(myContext, FishEyePicActivity.class);
                			            intent.putExtra(StaticConstant.FILE_PATH, filtP);
                			            intent.putExtra("fisheyeinfo", cp.fishEyeInfo);
                			            myContext.startActivity(intent);
                					return false;
                				}
                				
                				@Override
                				public boolean onDoubleTap(MotionEvent e) {
                					// TODO Auto-generated method stub
                					return false;
                				}
                			});
                       }
                    }else{
                    	photoView.setOnDoubleTapListener(null);
                    }
                }
            }
//            photoView.setImageResource(R.drawable.pic_nopic);
            // Now just add PhotoView to ViewPager and return it
            container.addView(rl, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return rl;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view==o;
        }

        @Override
        public int getItemPosition(Object object) {
            //return  POSITION_NONE;
            /**
             * �����ǰҳû��ͼ����ˢ��ȫ�����統ǰҳ��ͼ����ˢ�µ�ǰҳ������ҳ��Ҫˢ��
             */
        	View view = (View)object;  
            if(pageIndex == (Integer)view.getTag()){  
            	if(isGetPicList.get(pageIndex)){
            		return POSITION_UNCHANGED;
            	}
                return POSITION_NONE;  
            }else{  
                return POSITION_NONE;    
            }  
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            pageIndex=i;
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
    
    OnC2dEventCallback onC2dEventCallback = new OnC2dEventCallback() {
		
    	public void on_c2d_getEventPicCallBack(int i, String s, int i2) {
            Log.i(TAG,"on_c2d_getEventPicCallBack     i="+i+"    is="+i2+"     s="+s);
            Message m=Message.obtain();
            m.what=GET_PIC_CALLBACK;
            m.arg1=i;
            m.arg2=i2;
            m.obj=s;
            myHandler.sendMessage(m);
        }
		
		@Override
		public void on_c2d_getEventLogCallBack(int i, ArrayList<item_c2devents> item_c2deventses) {
			// TODO Auto-generated method stub
			
		}
	};

    


}
