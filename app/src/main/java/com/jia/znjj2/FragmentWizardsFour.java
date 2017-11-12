package com.jia.znjj2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.jia.data.DataControl;
import com.jia.data.ElectricInfoData;
import com.jia.data.RoomData;
import com.jia.ir.FragmentWizardsTwo;
import com.jia.ir.db.ETDB;
import com.jia.ir.etclass.ETDevice;
import com.jia.ir.etclass.ETDeviceAIR;
import com.jia.ir.etclass.ETDeviceTV;
import com.jia.ir.etclass.ETKey;
import com.jia.ir.face.IBack;
import com.jia.ir.global.ETGlobal;
import com.jia.model.ETAirLocal;
import com.jia.model.ETKeyLocal;
import com.jia.util.NetworkUtil;
import com.jia.util.Util;

import java.util.ArrayList;

import et.song.device.DeviceType;
import et.song.jni.ir.ETIR;
import et.song.remote.face.IR;
import et.song.remote.face.IRKeyValue;
import et.song.remote.instance.AIR;

public class FragmentWizardsFour extends Fragment implements OnClickListener,IBack {
    private DataControl mDC;

	private AlertDialog mDialog = null;
	private TextView mTextName = null;
	private TextView mTextCount = null;
	private int mType;
	private int mRoomSequ;
	private int mRow;
	private String mName;
	private String electricCode;
	private int mKey = 0;
	private int mTotal = 0;
	private int mCount = 1;
	private int mIndex = 0;
	private IR mIR = null;

	private TextView mTextViewTemp;
	private TextView mTextViewModeAuto;
	private TextView mTextViewModeCool;
	private TextView mTextViewModeDrying;
	private TextView mTextViewModeWind;
	private TextView mTextViewModeWarm;
	private TextView mTextViewRate;
	private TextView mTextViewDir;
	private RecvReceiver mReceiver;
	private int electricIndex;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mDC = DataControl.getInstance();
        mType = this.getArguments().getInt("type");
		mRoomSequ = getArguments().getInt("group");
		mRow = getArguments().getInt("index");
		mName = getArguments().getString("name");
		electricCode = getArguments().getString("electricCode");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_wizards_four, container,
				false);

		mTextName = (TextView) view.findViewById(R.id.text_name);
		mTextName.setText(mName);
		mTextCount = (TextView) view.findViewById(R.id.text_count);
		AssetManager mgr = getActivity().getAssets();
		Typeface tf = Typeface.createFromAsset(mgr, "fonts/Clockopia.ttf");
		mTextCount.setTypeface(tf);

		TextView textTest = (TextView) view.findViewById(R.id.text_test);
		textTest.setOnClickListener(this);
		mIR = ETIR.Builder(mType);
		switch (mType) {
		case DeviceType.DEVICE_REMOTE_TV:
			mKey = IRKeyValue.KEY_TV_POWER;
			textTest.setText(R.string.str_other_power);
			break;
		case DeviceType.DEVICE_REMOTE_AIR:
			mKey = IRKeyValue.KEY_AIR_POWER;
			textTest.setText(R.string.str_other_power);
			break;
		}
		if (mIR != null) {
			try {
				mTotal = mIR.GetBrandCount(mRow);
				mTextCount.setText("(" + mCount + "/" + mTotal + ")");
				getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		mReceiver = new RecvReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ETGlobal.BROADCAST_APP_BACK);
		getActivity().registerReceiver(mReceiver, filter);
	}
	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mReceiver);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();


	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			byte[] keyValue = null;
			switch (msg.what) {
			case 0:
				try {
					if (mCount != mTotal) {
						mCount++;
						mIndex++;
					}
					mTextCount.setText("(" + mCount + "/" + mTotal + ")");
					getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
					mHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					if (mKey == 0)
						return;
					keyValue = mIR.Search(mRow, mIndex, mKey);
					if (mKey == IRKeyValue.KEY_AIR_POWER){
						AIR air = (AIR)mIR;
						if (air.GetPower() == 0x00){
							Log.i("Air Power", "Close");
							keyValue = mIR.Search(mRow, mIndex, mKey);
						}
					}
					if (keyValue == null)
						return;
					sendOrder(keyValue);
					ETGlobal.mTg.write(keyValue, keyValue.length);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					if (mCount != 1) {
						mCount--;
						mIndex--;
					}
					mTextCount.setText("(" + mCount + "/" + mTotal + ")");
					getActivity().setTitle(mName + "(" + mCount + "/" + mTotal + ")");
					mHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}

		}
	};

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int key = 0;
		byte[] keyValue = null;
		final LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View view = null;
		switch (arg0.getId()) {
		case R.id.text_up:	//按多了，上一组
			mHandler.sendEmptyMessage(2);
			break;
		case R.id.text_down:	//否，下一组
			mHandler.sendEmptyMessage(0);
			break;
		case R.id.text_again:	//再次发送该代码
			mHandler.sendEmptyMessage(1);
			break;
		case R.id.text_yes:		//是，下一步
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			switch (mType) {
				case DeviceType.DEVICE_REMOTE_TV:
					view = mInflater.inflate(R.layout.fragment_wizards_four_tv,
							null);
					TextView textViewTVMute = (TextView) view
							.findViewById(R.id.text_tv_mute);
					textViewTVMute.setOnClickListener(this);
					textViewTVMute
							.setBackgroundResource(R.drawable.ic_button_cast_selector);
					TextView textViewTVVolAdd = (TextView) view
							.findViewById(R.id.text_tv_voladd);
					textViewTVVolAdd.setOnClickListener(this);
					textViewTVVolAdd
							.setBackgroundResource(R.drawable.ic_button_cast_selector);
					TextView textViewTVVolSub = (TextView) view
							.findViewById(R.id.text_tv_volsub);
					textViewTVVolSub.setOnClickListener(this);
					textViewTVVolSub
							.setBackgroundResource(R.drawable.ic_button_cast_selector);
					break;


				case DeviceType.DEVICE_REMOTE_AIR:
					view = mInflater.inflate(R.layout.fragment_wizards_four_air,
							null);
					mTextViewModeAuto = (TextView) view
							.findViewById(R.id.text_air_mode_auto);
					mTextViewModeCool = (TextView) view
							.findViewById(R.id.text_air_mode_cool);
					mTextViewModeDrying = (TextView) view
							.findViewById(R.id.text_air_mode_drying);
					mTextViewModeWind = (TextView) view
							.findViewById(R.id.text_air_mode_wind);
					mTextViewModeWarm = (TextView) view
							.findViewById(R.id.text_air_mode_warm);

					mTextViewRate = (TextView) view.findViewById(R.id.text_air_rate);

					mTextViewDir = (TextView) view.findViewById(R.id.text_air_dir);

					mTextViewTemp = (TextView) view.findViewById(R.id.text_air_temp);
					AssetManager mgr = getActivity().getAssets();
					Typeface tf = Typeface.createFromAsset(mgr, "fonts/Clockopia.ttf");
					mTextViewTemp.setTypeface(tf);
					mTextViewTemp.setText("");

					TextView textViewPower = (TextView) view
							.findViewById(R.id.text_air_power);
					textViewPower.setOnClickListener(this);
					textViewPower.setBackgroundResource(R.drawable.ic_power_selector);

					TextView textViewTempAdd = (TextView) view
							.findViewById(R.id.text_air_tempadd);
					textViewTempAdd.setOnClickListener(this);
					textViewTempAdd.setBackgroundResource(R.drawable.ic_button_cast_selector);

					TextView textViewTempSub = (TextView) view
							.findViewById(R.id.text_air_tempsub);
					textViewTempSub.setOnClickListener(this);
					textViewTempSub.setBackgroundResource(R.drawable.ic_button_cast_selector);

					TextView textViewMode = (TextView) view
							.findViewById(R.id.text_air_mode);
					textViewMode.setOnClickListener(this);
					textViewMode.setBackgroundResource(R.drawable.ic_button_cast_selector);
					break;
			}
			TextView textViewFourNo = (TextView) view
					.findViewById(R.id.text_four_no);
			textViewFourNo.setOnClickListener(this);
			textViewFourNo
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textViewFourYes = (TextView) view
					.findViewById(R.id.text_four_yes);
			textViewFourYes.setOnClickListener(this);
			textViewFourYes
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.str_other_set)
					.setMessage(R.string.str_wizards_info_4_2).setView(view)
					.create();
			mDialog.show();
			break;


		case R.id.text_four_yes:	//是，存为遥控器
			view = mInflater.inflate(R.layout.dialog_set_name, null);
			final EditText name = (EditText) view.findViewById(R.id.edit_name);
			int len = mName.length() > 10? 10: mName.length();
			name.setText(mName.substring(0, len));
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher)
					.setView(view)
					.setPositiveButton(R.string.str_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
                                    RoomData.RoomDataInfo roomDataInfo = mDC.mAreaList.get(mRoomSequ);
									ETDevice device = null;
									switch (mType) {
									case DeviceType.DEVICE_REMOTE_TV:
										device = new ETDeviceTV(mRow, mIndex);
										device.SetName(name.getText()
												.toString());
										device.SetType(12);
										device.SetRes(0);
										device.SetGID(roomDataInfo.getRoomIndex());
										device.setmElectricCode(electricCode);
										electricIndex = mDC.mElectricData.getMaxElectricIndex(mDC.sAccountCode,mDC.sMasterCode)+1;
										device.setmElectricIndex(electricIndex);
										device.setmMasterCode(mDC.sMasterCode);
										device.setmRoomIndex(roomDataInfo.getRoomIndex());
										device.setmRoomSequ(mRoomSequ);
										device.Inster(ETDB
												.getInstance(getActivity()));

										break;



									case DeviceType.DEVICE_REMOTE_AIR:
										device = new ETDeviceAIR(mRow, mIndex);
										device.SetName(name.getText().toString());
										device.SetType(9);
										device.SetRes(7);
										device.SetGID(roomDataInfo.getRoomIndex());
										device.setmElectricCode(electricCode);
										electricIndex = mDC.mElectricData.getMaxElectricIndex(mDC.sAccountCode,mDC.sMasterCode)+1;
										device.setmElectricIndex(electricIndex);
										device.setmMasterCode(mDC.sMasterCode);
										device.setmRoomIndex(roomDataInfo.getRoomIndex());
										device.setmRoomSequ(mRoomSequ);
										((ETDeviceAIR)device).SetTemp(((AIR)mIR).GetTemp());
										((ETDeviceAIR)device).SetMode(((AIR)mIR).GetMode());
										((ETDeviceAIR)device).SetPower(((AIR)mIR).GetPower());
										((ETDeviceAIR)device).SetWindRate(((AIR)mIR).GetWindRate());
										((ETDeviceAIR)device).SetWindDir(((AIR)mIR).GetWindDir());
										((ETDeviceAIR)device).SetAutoWindDir(((AIR)mIR).GetAutoWindDir());
										((ETDeviceAIR)device).Inster(ETDB.getInstance(getActivity()));
										new AddETAirAsyncTask().execute();
										break;

									}
									new AddKeyAsyncTask().execute();


									getActivity().finish();

								}
							}).create();

			mDialog.setTitle(R.string.str_dialog_set_name_title);
			mDialog.show();

			break;
		/*具体的电器遥控测试，开始*/
		case R.id.text_tv_volsub:
			key = IRKeyValue.KEY_TV_VOLUME_OUT;
			break;
		case R.id.text_tv_voladd:
			key = IRKeyValue.KEY_TV_VOLUME_IN;
			break;
		case R.id.text_tv_mute:
			key = IRKeyValue.KEY_TV_MUTE;
			break;
		case R.id.text_air_power:
			key = IRKeyValue.KEY_AIR_POWER;
			break;
		case R.id.text_air_tempsub:
			key = IRKeyValue.KEY_AIR_TEMPERATURE_OUT;
			break;
		case R.id.text_air_tempadd:
			key = IRKeyValue.KEY_AIR_TEMPERATURE_IN;
			break;
		case R.id.text_air_mode:
			key = IRKeyValue.KEY_AIR_MODE;
			break;
		/*具体的遥控测试，结束*/

		case R.id.text_test:	//电源。。。
			mHandler.sendEmptyMessage(1);
		case R.id.text_four_no:		//否，上一步
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.cancel();
			}
			view = mInflater.inflate(R.layout.fragment_wizards_four_1, null);
			TextView textFourUp = (TextView) view.findViewById(R.id.text_up);
			textFourUp.setOnClickListener(this);
			textFourUp
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourDown = (TextView) view
					.findViewById(R.id.text_down);
			textFourDown.setOnClickListener(this);
			textFourDown
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			TextView textFourAgain = (TextView) view
					.findViewById(R.id.text_again);
			textFourAgain.setOnClickListener(this);
			textFourAgain
					.setBackgroundResource(R.drawable.ic_button_cast_selector);

			TextView textFourYes = (TextView) view.findViewById(R.id.text_yes);
			textFourYes.setOnClickListener(this);
			textFourYes
					.setBackgroundResource(R.drawable.ic_button_cast_selector);
			mDialog = new AlertDialog.Builder(getActivity())
					.setIcon(R.drawable.ic_launcher_l)
					.setTitle(R.string.str_other_set)
					.setMessage(R.string.str_wizards_info_4_1).setView(view)
					.create();
			mDialog.show();
			break;
		}


		boolean isSend = true;
		try {

			if (key == 0)
				return;
			keyValue = mIR.Search(mRow, mIndex, key);
			sendOrder(keyValue);
			if (keyValue == null)
				return;
			if (ETGlobal.mTg == null) {
				isSend = false;
			}
			int n = ETGlobal.mTg.write(keyValue, keyValue.length);
			//sendOrder(keyValue);
			if (n < 0) {
				isSend = false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mType == DeviceType.DEVICE_REMOTE_AIR)
		{
			F5();
		}

	}

	public void F5() {
		if (((AIR)mIR).GetPower() == 0x01) {
			switch (((AIR)mIR).GetMode()) {
			case 1:
				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_2);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 2:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_2);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);

				break;
			case 3:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_2);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 4:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_2);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
				break;
			case 5:

				mTextViewModeAuto
						.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
				mTextViewModeCool
						.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
				mTextViewModeDrying
						.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
				mTextViewModeWind
						.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
				mTextViewModeWarm
						.setBackgroundResource(R.drawable.ic_air_mode_warm_2);
				break;
			}

			switch (((AIR)mIR).GetWindRate()) {
			case 1:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_1);

				break;
			case 2:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_2);

				break;
			case 3:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_3);

				break;
			case 4:
				mTextViewRate.setBackgroundResource(R.drawable.ic_air_rate_4);

				break;
			}

			if (((AIR)mIR).GetAutoWindDir() == 0x01) {
				mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_1);
			} else {
				switch (((AIR)mIR).GetWindDir()) {
				case 1:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_2);
					break;
				case 2:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_3);
					break;
				case 3:
					mTextViewDir.setBackgroundResource(R.drawable.ic_air_dir_4);
					break;
				}
			}
			if (((AIR)mIR).GetMode() == 2 || ((AIR)mIR).GetMode() == 5) {
				mTextViewTemp.setText(Byte.valueOf(((AIR)mIR).GetTemp())
						.toString());
			} else {
				mTextViewTemp.setText("");
			}
		} else {

			mTextViewModeAuto
					.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewModeCool
					.setBackgroundResource(R.drawable.ic_air_mode_cold_1);
			mTextViewModeDrying
					.setBackgroundResource(R.drawable.ic_air_mode_drying_1);
			mTextViewModeWind
					.setBackgroundResource(R.drawable.ic_air_mode_wind_1);
			mTextViewModeWarm
					.setBackgroundResource(R.drawable.ic_air_mode_warm_1);
			mTextViewRate.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewDir.setBackgroundResource(R.drawable.ic_air_mode_auto_1);
			mTextViewTemp.setText("");
		}
	}

	@Override
	public void Back() {
		// TODO Auto-generated method stub
		this.getActivity().setTitle(R.string.str_wizards);
		FragmentWizardsTwo fragmentThree = new FragmentWizardsTwo();
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt("type", mType);
		args.putInt("group", mRoomSequ);
		fragmentThree.setArguments(args);
//		transaction.setCustomAnimations(R.anim.push_left_in,
//				R.anim.push_left_out, R.anim.push_left_in,
//				R.anim.push_left_out);
		transaction.replace(R.id.fragment_container, fragmentThree);
		// transactionBt.addToBackStack(null);
//		transaction
//				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.commit();
	}
	public class RecvReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ETGlobal.BROADCAST_APP_BACK)) {
				Back();
			}
		}
	}

	private String bytes2Order(byte[] bytes){
		String irCode = Util.byte2hex(bytes);
		System.out.println("红外码："+irCode);
		return irCode;
	}

	private void sendOrder(byte[] bytes){
		String irCode = bytes2Order(bytes);
		String length = Integer.toHexString(irCode.length());
		String irCount = (length.length()<2)?"0"+length:length;
		System.out.println("红外指令长度："+Integer.toHexString(irCode.length()));
		String irOrder = "<" + electricCode +"XM"+ irCount + irCode + "FF>";
		System.out.println("红外命令："+irOrder);
		NetworkUtil.out.println(irOrder);
	}

	class AddKeyAsyncTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			String sql = "SELECT * FROM ETKEY WHERE master_code = ? and electric_index = ?";
			try{
				Cursor cursor = ETDB.getInstance(getActivity()).queryData2Cursor(sql, new String[]{mDC.sMasterCode,""+electricIndex});
				int j = cursor.getCount();
				ArrayList<ETKeyLocal> locallist = new ArrayList<>();
				for (int i = 0; i < j; i++) {
					cursor.moveToPosition(i);
					ETKeyLocal etKeyLocal = new ETKeyLocal();
					//etKeyLocal.setId(cursor.getInt(cursor.getColumnIndex("id")));
					etKeyLocal.setMasterCode(cursor.getString(cursor.getColumnIndex("master_code")));
					etKeyLocal.setElectricIndex(cursor.getInt(cursor.getColumnIndex("electric_index")));
					etKeyLocal.setDid(cursor.getInt(cursor.getColumnIndex("did")));
					etKeyLocal.setKeyName(cursor.getString(cursor.getColumnIndex("key_name")));
					etKeyLocal.setKeyRes(cursor.getInt(cursor.getColumnIndex("key_res")));
					etKeyLocal.setKeyX(cursor.getFloat(cursor.getColumnIndex("key_x")));
					etKeyLocal.setKeyY(cursor.getFloat(cursor.getColumnIndex("key_y")));
					etKeyLocal.setKeyValue(cursor.getString(cursor.getColumnIndex("key_value")));
					etKeyLocal.setKeyKey(cursor.getInt(cursor.getColumnIndex("key_key")));
					etKeyLocal.setKeyBrandIndex(cursor.getInt(cursor.getColumnIndex("key_brandindex")));
					etKeyLocal.setKeyBrandPos(cursor.getInt(cursor.getColumnIndex("key_brandpos")));
					etKeyLocal.setKeyRow(cursor.getInt(cursor.getColumnIndex("key_row")));
					etKeyLocal.setKeyState(cursor.getInt(cursor.getColumnIndex("key_state")));
					locallist.add(etKeyLocal);
				}
				mDC.mWS.addETKeys(locallist);
			}catch (Exception e){
				e.printStackTrace();
			}

			return null;
		}
	}

	class AddETAirAsyncTask extends AsyncTask<Void,Void,Void>{
		@Override
		protected Void doInBackground(Void... params) {
			String sql = "SELECT * FROM ETAirDevice WHERE master_code = ? and electric_index = ?";
			try{
				Cursor cursor = ETDB.getInstance(getActivity()).queryData2Cursor(sql, new String[]{mDC.sMasterCode,""+electricIndex});
				if(cursor.getCount() == 1){
					cursor.moveToFirst();
					ETAirLocal etAirLocal = new ETAirLocal();
					etAirLocal.setMasterCode(cursor.getString(cursor.getColumnIndex("master_code")));
					etAirLocal.setElectricIndex(cursor.getInt(cursor.getColumnIndex("electric_index")));
					etAirLocal.setAirBrand(cursor.getInt(cursor.getColumnIndex("air_brand")));
					etAirLocal.setAirIndex(cursor.getInt(cursor.getColumnIndex("air_index")));
					etAirLocal.setAirTemp(cursor.getInt(cursor.getColumnIndex("air_temp")));
					etAirLocal.setAirRate(cursor.getInt(cursor.getColumnIndex("air_rate")));
					etAirLocal.setAirDir(cursor.getInt(cursor.getColumnIndex("air_dir")));
					etAirLocal.setAirAutoDir(cursor.getInt(cursor.getColumnIndex("air_auto_dir")));
					etAirLocal.setAirMode(cursor.getInt(cursor.getColumnIndex("air_mode")));
					etAirLocal.setAirPower(cursor.getInt(cursor.getColumnIndex("air_power")));
					mDC.mWS.addETAirDevice(etAirLocal);
				}


			}catch (Exception e){
				e.printStackTrace();
			}

			return null;
		}
	}
}
