package com.jia.znjj2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jia.camera.business.Business;
import com.jia.camera.business.entity.ChannelInfo;
import com.jia.camera.mediaplay.MediaPlayActivity;
import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.widget.MyGridView;

import java.util.List;

public class AreaElectricFragment extends Fragment {
	private ImageView mIvAreaImg;
	private MyGridView mGvElectricList;
	private ImageView mIvAreaEdit;
	private TextView mTvAreaEdit;
	private DataControl mDC;
	private TypedArray mTaElectricImages;
	private View view;
	int mPosition;
	BaseAdapter adapter;

	private static final String ARG_POSITION = "position";

	private int roomPosition;
	private BrdcstReceiver receiver;

	private List<ChannelInfo> mChannelInfoList;

	public static AreaElectricFragment newInstance(int position) {
		AreaElectricFragment f = new AreaElectricFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		roomPosition = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.area_content, container, false);
		initView(view);
		upDateGridView();
		addListener();

		/*设置接收后台的广播消息*/
		receiver = new BrdcstReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MY_RECEIVER");
		getActivity().registerReceiver(receiver, filter);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView(View view){
		mDC = DataControl.getInstance();
		mIvAreaImg = (ImageView) view.findViewById(R.id.area_electric_fragment_area_image);
		mGvElectricList = (MyGridView) view.findViewById(R.id.area_electric_fragment_electric_list);
		mIvAreaEdit = (ImageView) view.findViewById(R.id.area_electric_fragment_edit_img);
		mTvAreaEdit = (TextView) view.findViewById(R.id.area_electric_fragment_edit_text);

		if (roomPosition > mDC.mAreaList.size() - 1) {
			roomPosition = 0;
		}

		mIvAreaImg.setBackgroundResource(mDC.mAreaTypeImages.getResourceId(mDC.mAreaList.get(roomPosition).getRoomImg(),0));

		mTaElectricImages = getResources().obtainTypedArray(R.array.dev_type_images);

	}

	public void upDateGridView() {
		adapter = new BaseAdapter() {
			@Override
			public int getCount() {
				if (mDC.mUserList.isEmpty() || mDC.mAreaList.isEmpty())
					return 0;

				if (roomPosition > mDC.mAreaList.size() - 1) {
					roomPosition = 0;
				}

				return (mDC.mUserList.get(0).getIsAdmin() == 1)
						?(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size() + 1)
						:(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size());
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder viewHolder;
				if(convertView == null){
					viewHolder = new ViewHolder();
					//LayoutInflater inflater = LayoutInflater.from(getContext());
					//convertView = inflater.inflate(R.layout.electric_list_item, null, false);
					convertView = View.inflate((Activity) getContext(),
							R.layout.electric_list_item, null);
					viewHolder.electricrl = (RelativeLayout) convertView.findViewById(R.id.electric_list_item_rl);
					viewHolder.electricll = (LinearLayout) convertView.findViewById(R.id.electric_list_item_ll);
					viewHolder.electricImg = (ImageView) convertView.findViewById(R.id.electric_list_item_img);
					viewHolder.electricName = (TextView) convertView.findViewById(R.id.electric_list_item_name);
					viewHolder.delete = (ImageView) convertView.findViewById(R.id.electric_list_item_delete);
					viewHolder.delete.setVisibility(View.GONE);
					convertView.setTag(viewHolder);
					//viewHolder.update();
				}else {
					viewHolder = (ViewHolder) convertView.getTag();
				}
				DisplayMetrics loacalDisplayMetrics = new DisplayMetrics();
				getActivity().getWindowManager().getDefaultDisplay().getMetrics(loacalDisplayMetrics);
				//System.out.println("$$$$$$$$$$$"+loacalDisplayMetrics);
				int i = (int)(loacalDisplayMetrics.heightPixels/loacalDisplayMetrics.density);

				if (position < mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size()) {
					ElectricInfoData electricInfoData = new ElectricInfoData();
					for (ElectricInfoData ele : mDC.mAreaList.get(roomPosition).getmElectricInfoDataList()) {
						if (ele.getElectricSequ()==position) {
							electricInfoData = ele;
							break;
						}
					}

					int electricType = electricInfoData.getElectricType();
					String electricState = mDC.mElectricState.get(electricInfoData.getElectricCode())[0];
					if(electricType == 0){
						if(electricState.equals("ZV")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_socket_on1);
						}else {
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_socket_close);
						}
					}else if(electricType == 1){
						if(electricState.equals("Z1")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift1_on);
						}else {
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift1);
						}
					}else if (electricType == 2){
						if (electricInfoData.getOrderInfo().equals("01")){
							if (electricState.equals("Z1") || electricState.equals("Z3")) {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_left_on);
							} else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_left);
							}
						}else if(electricInfoData.getOrderInfo().equals("02")){
							if (electricState.equals("Z2") || electricState.equals("Z3")) {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_right_on);
							} else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift2_right);
							}
						}
					}else if(electricType == 3){
						if (electricInfoData.getOrderInfo().equals("01")){
							if(electricState.equals("Z1") || electricState.equals("Z3") || electricState.equals("Z5") || electricState.equals("Z7")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_left_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_left);
							}
						}else if(electricInfoData.getOrderInfo().equals("02")){
							if(electricState.equals("Z2") || electricState.equals("Z3") || electricState.equals("Z6") || electricState.equals("Z7")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_center_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_center);
							}

						}else if(electricInfoData.getOrderInfo().equals("03")){
							if(electricState.equals("Z4") || electricState.equals("Z5") || electricState.equals("Z6") || electricState.equals("Z7")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_right_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift3_right);
							}
						}
					}else if (electricType == 4){
						if (electricInfoData.getOrderInfo().equals("01")){
							if(electricState.equals("Z1") || electricState.equals("Z3") || electricState.equals("Z5") || electricState.equals("Z7")
									|| electricState.equals("Z9") || electricState.equals("ZB") || electricState.equals("ZD") || electricState.equals("ZF")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left1_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left1);
							}
						}else if(electricInfoData.getOrderInfo().equals("02")){
							if(electricState.equals("Z3") || electricState.equals("Z4") || electricState.equals("Z6") || electricState.equals("Z7")
									|| electricState.equals("ZA") || electricState.equals("ZB") || electricState.equals("ZE") || electricState.equals("ZF")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left2_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left2);
							}

						}else if(electricInfoData.getOrderInfo().equals("03")){
							if(electricState.equals("Z4") || electricState.equals("Z5") || electricState.equals("Z6") || electricState.equals("Z7")
									|| electricState.equals("ZC") || electricState.equals("ZD") || electricState.equals("ZE") || electricState.equals("ZF")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right2_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right2);
							}

						}else if(electricInfoData.getOrderInfo().equals("04")){
							if(electricState.equals("Z8") || electricState.equals("Z9") || electricState.equals("ZA") || electricState.equals("ZB")
									|| electricState.equals("ZC") || electricState.equals("ZD") || electricState.equals("ZE") || electricState.equals("ZF")){
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right1_on);
							}else {
								viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right1);
							}

						}
					}else if (electricType == 10){
						if (electricInfoData.getOrderInfo().equals("01")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left1);
						}else if(electricInfoData.getOrderInfo().equals("02")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_left2);
						}else if(electricInfoData.getOrderInfo().equals("03")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right2);
						}else if(electricInfoData.getOrderInfo().equals("04")){
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_swift4_right1);
						}
					}else if(electricType==11) {
						if (electricState.equals("ZV")) {
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_armon);
						} else if (electricState.equals("ZW") || electricState.equals("ZU")) {
							viewHolder.electricImg.setBackgroundResource(R.drawable.electric_type_arm_close1);
						}

					}
					else
					{
						viewHolder.electricImg.setBackgroundResource(mTaElectricImages.getResourceId(electricType,0));
					}
					//viewHolder.electricImg.setVisibility(View.GONE);

					viewHolder.delete.setBackgroundResource(R.drawable.electric_delete);
					viewHolder.electricName.setText(mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricName());
					ViewGroup.LayoutParams layoutParams = viewHolder.electricImg.getLayoutParams();
					layoutParams.height = i/6;
					layoutParams.width = i/6;
				}else {
					//管理员本地才能添加
					if(mDC.mUserList.get(0).getIsAdmin() == 1 && mDC.bIsRemote == false){
						viewHolder.electricImg.setBackgroundResource(R.drawable.add);
						viewHolder.electricImg.setLayoutParams(new LinearLayout.LayoutParams(i / 6, i / 6));
					}
				}

				if (i <= 800)
				{
					viewHolder.electricName.setTextSize(17.0F);
				}
				else
				{
					viewHolder.electricName.setTextSize(20.0F);
				}
				viewHolder.electricName.setTag(position);
				viewHolder.electricImg.setTag(convertView);



				return convertView;
			}
		};

		mGvElectricList.setAdapter(adapter);



	}

	public void addListener() {
		mGvElectricList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent;
				if (position < mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().size()) {
					ElectricInfoData electricInfoData = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position);
					int i = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricType();
					switch (i) {
						case 0:
						case 1:
						case 2:
						case 3:
						case 4:
							intent = new Intent(getContext(), SwiftOne.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 10:
							intent = new Intent(getContext(), SwiftScene.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 5:
							intent = new Intent(getContext(), DoorDetail.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 6:
						case 7:
						case 11:
							intent = new Intent(getContext(), CurtainDetail.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
//						case 7:
//							intent = new Intent(getContext(), WindowDetail.class);
//							break;
						case 8:
							loadChannelList(position);
							mPosition=position;
							break;
						case 9:
							//空调，需要先判断空调是否已经对码，若对码，则选择进入控制页面，否则进入学习/码库页面
							intent = new Intent(getContext(), AirActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 12:
							intent = new Intent(getContext(), TvActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 13:
						case 14:
						case 16:
						case 17:
						case 19:
							intent = new Intent(getContext(), SensorActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 15:
							intent = new Intent(getContext(), DoorActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 18:
							intent = new Intent(getContext(), HornActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 20:
							intent = new Intent(getContext(), ClothesHanger.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 21:
							intent = new Intent(getContext(),AirLearnActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 22:
							intent = new Intent(getContext(),AirCenterActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 23:
							intent = new Intent(getContext(),Newdoor.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 24:
							intent = new Intent(getContext(),TvLearnActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						case 25:
							intent = new Intent(getContext(),AirCenterMoreActivity.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
						default:
							intent = new Intent(getContext(), ElectricDetail.class);
							intent.putExtra("roomSequ",roomPosition);
							intent.putExtra("electricSequ", position);
							mPosition=position;
							startActivity(intent);
							break;
					}
					//intent.putExtra("areaIndex", iAreaIndex);
//					intent.putExtra("roomSequ",roomPosition);
//					intent.putExtra("electricSequ", position);
//					startActivity(intent);
				} else {
					//if(mDC.bIsRemote){    //实际
					if(false){      //本地测试
						Toast.makeText(getContext(), "远程不能添加电器", Toast.LENGTH_LONG).show();
					}else {
						intent = new Intent(getContext(), ElectricAdd.class);
						intent.putExtra("roomSequ",roomPosition);
						startActivity(intent);
					}
				}

			}
		});

		mIvAreaImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
		mTvAreaEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoAreaEdit();
			}
		});
	}

	private void goToAirStudyOrLib(int type){
		Intent intent = new Intent(getContext(),IRWizardsActivity.class);
		intent.putExtra("roomPosition", roomPosition);
		intent.putExtra("electricType", type);
		startActivity(intent);
	}
	private void gotoAreaEdit(){
		Intent intent = new Intent(getContext(),AreaEdit.class);
		intent.putExtra("roomPosition", roomPosition);
		startActivity(intent);
	}





	class ViewHolder{
		public RelativeLayout electricrl;
		public LinearLayout electricll;
		public ImageView electricImg;
		public TextView electricName;
		public ImageView delete;
		public void update() {
			// 精确计算GridView的item高度
			electricName.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {
						public void onGlobalLayout() {
							int position = (Integer) electricName.getTag();
							// 这里是保证同一行的item高度是相同的！！也就是同一行是齐整的 height相等
							if (position > 0 && position % 3 == 1) {
								View v = (View) electricImg.getTag();
								int height = v.getHeight();

								View v1 = mGvElectricList.getChildAt(position - 1);
								int height1 = v1.getHeight();
								//System.out.println("height:"+height + "  height1:"+height1+"   height2"+height2);
								int maxHeight = Math.max(height1,height);
								// 得到同一行的最后一个item和前一个item想比较，把谁的height大，就把两者中
								// height小的item的高度设定为height较大的item的高度一致，也就是保证同一
								// 行高度相等即可
								v.setLayoutParams(new GridView.LayoutParams(
										GridView.LayoutParams.MATCH_PARENT,
										maxHeight));
								v1.setLayoutParams(new GridView.LayoutParams(
										GridView.LayoutParams.MATCH_PARENT,
										maxHeight));
							}else if (position > 0 && position % 3 == 2) {
								View v = (View) electricImg.getTag();
								int height = v.getHeight();

								View v1 = mGvElectricList.getChildAt(position - 1);
								int height1 = v1.getHeight();
								View v2 = mGvElectricList.getChildAt(position - 2);
								int height2 = v2.getHeight();
								//System.out.println("height:"+height + "  height1:"+height1+"   height2"+height2);
								int maxHeight = Math.max(Math.max(height1,height2),height);
								int minHegith = Math.min(Math.min(height1,height2),height);
								// 得到同一行的最后一个item和前一个item想比较，把谁的height大，就把两者中
								// height小的item的高度设定为height较大的item的高度一致，也就是保证同一
								// 行高度相等即可
								v.setLayoutParams(new GridView.LayoutParams(
											GridView.LayoutParams.MATCH_PARENT,
											maxHeight));
								v1.setLayoutParams(new GridView.LayoutParams(
										GridView.LayoutParams.MATCH_PARENT,
										maxHeight));
								v2.setLayoutParams(new GridView.LayoutParams(
										GridView.LayoutParams.MATCH_PARENT,
										maxHeight));

							}
						}
					});
		}
	}

	/**
	 *广播：接受后台的service发送的广播
	 */
	private  class BrdcstReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI();
		}
	}
	private void updateUI(){
		//System.out.println("AreaElectricFragment updateUI");
		upDateGridView();
	}

	private void loadChannelList(final int position) {
		// 初始化数据
		Business.getInstance().getChannelList(new Handler()	{
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				Business.RetObject retObject = (Business.RetObject) msg.obj;
				if (msg.what == 0) {
					mChannelInfoList = (List<ChannelInfo>) retObject.resp;
					if(mChannelInfoList != null && mChannelInfoList.size() > 0){
						toMediaPlayAcitvity(position);
					} else{
						Toast.makeText(getActivity(), "没有设备", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(getActivity(), retObject.mMsg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private String getUuid(int position){
		String electricCode = mDC.mAreaList.get(roomPosition).getmElectricInfoDataList().get(position).getElectricCode();
		for (ChannelInfo info: mChannelInfoList ) {
			if (info.getDeviceCode() != null && info.getDeviceCode().equals(electricCode)) {
				mDC.DeviceCode=info.getDeviceCode();
				mDC.DeviceIndex=info.getIndex();
				return info.getUuid();
			}
		}
		return null;
	}

	private void toMediaPlayAcitvity(int position){
		mDC.UUID=getUuid(position);
		// 启动实时视频
		Intent intent = new Intent(getActivity(), MediaPlayActivity.class);
		intent.putExtra("UUID", getUuid(position));
		intent.putExtra("TYPE", MediaPlayActivity.IS_VIDEO_ONLINE);
		intent.putExtra("MEDIA_TITLE", R.string.live_play_name);
		startActivityForResult(intent, 0);
	}

}