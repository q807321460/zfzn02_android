package com.jia.ir;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jia.ir.face.IBack;
import com.jia.ir.global.ETGlobal;
import com.jia.znjj2.FragmentWizardsFour;
import com.jia.znjj2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import et.song.device.DeviceType;

public class FragmentWizardsTwo extends Fragment implements OnClickListener,IBack {

	private SideBar mIndexBar;
	private ListView mListView = null;
	private BrandTask mBrandTask = null;
	private int mType;
	private int mGroupIndex;
	private String electricCode;
	private ProgressDialog mProgressDialog = null;
	private AdapterBrandList mAdapter = null;
	private RecvReceiver mReceiver;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mType = this.getArguments().getInt("type");
		mGroupIndex = getArguments().getInt("group");
		electricCode = getArguments().getString("electricCode");
		Log.i("GroupIndex", String.valueOf(mGroupIndex));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_wizards_two,
				container, false);
		mListView = (ListView) view.findViewById(android.R.id.list);
		mIndexBar = (SideBar) view.findViewById(R.id.sideBar);
		mIndexBar.setListView(mListView);
		TextView dialogText = (TextView) view.findViewById(R.id.text_az);
		mIndexBar.setTextView(dialogText);
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setMessage(getResources().getString(
				R.string.str_loading));
		mProgressDialog.setCanceledOnTouchOutside(false);
		if (mBrandTask == null
				|| mBrandTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			mBrandTask = new BrandTask();
			mBrandTask.execute();
		}
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				AdapterPYinItem item = (AdapterPYinItem) parent
						.getItemAtPosition(position);
				FragmentTransaction transaction = getActivity()
						.getSupportFragmentManager().beginTransaction();
				Fragment fragment = null;
				Bundle args = new Bundle();
				args.putInt("index", item.getPos());
				args.putString("name", item.getName());
				args.putInt("type", mType);
				args.putInt("group", mGroupIndex);
				args.putString("electricCode", electricCode);
				fragment = new FragmentWizardsFour();
				fragment.setArguments(args);
				transaction.replace(R.id.fragment_container, fragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});

		
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Nothing to see here.
		menu.clear();
	}
	@Override
	public void onStop() {
		super.onStop();
		if (!mBrandTask.isCancelled())
		{
			mBrandTask.cancel(true);
		}
		getActivity().unregisterReceiver(mReceiver);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("Home", "Home");
		switch (item.getItemId()) {
		case android.R.id.home:
			Back();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	class BrandTask extends AsyncTask<String, Void, Void> {
		ArrayList<AdapterPYinItem> items = new ArrayList<AdapterPYinItem>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.i("Type", String.valueOf(mType));
			switch (mType) {
			case DeviceType.DEVICE_REMOTE_TV:
				String tvs[] = getResources().getStringArray(
						R.array.strs_tv_brand);
				int iTV = 0;
				for (String name : tvs) {

					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhangcheng(changcheng)")) {
							pyin = "changcheng";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangfei(changfei)")) {
							pyin = "changfei";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangfeng(changfeng)")) {
							pyin = "changfeng";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhanghai(changhai)")) {
							pyin = "changhai";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhanghong(changhong)")) {
							pyin = "changhong";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iTV));
						iTV++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;

			case DeviceType.DEVICE_REMOTE_AIR:
				String airs[] = getResources().getStringArray(
						R.array.strs_air_brand);
				int iAIR = 0;
				for (String name:airs) {
					try {
						String pyin;
						// pyin = ETPyin.Pyin(name, ETPyin.ETPYIN_ALLLETTER);
						pyin = PingYinUtil.getPingYin(name);
						if (pyin.toLowerCase(Locale.getDefault()).equals(
								"zhanghong(changhong)")) {
							pyin = "changhong";
						} else if (pyin.toLowerCase(Locale.getDefault())
								.equals("zhangling(changling)")) {
							pyin = "changling";
						}
						Log.i("ETPYin", pyin);

						items.add(new AdapterPYinItem(name, pyin, iAIR));
						iAIR++;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.cancel();
			if (!items.isEmpty()) {
				Collections.sort(items, new PinyinComparator());
				mAdapter = new AdapterBrandList(getActivity(), items);
				mListView.setAdapter(mAdapter);
			}
		}
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction transaction = getActivity()
				.getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		// TODO Auto-generated method stub
	}
	
	@Override
	public void Back() {
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
}
