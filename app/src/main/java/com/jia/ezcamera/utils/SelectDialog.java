package com.jia.ezcamera.utils;

import java.util.ArrayList;



import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jia.znjj2.R;


public class SelectDialog extends AlertDialog{
	private TextView titleText;
	private String titleString;
	private ListView dlgList;
	private ArrayList<String> selectList;
	private DlgAdapter dlgAdapter;
	private OnItemClickListener listener;
	private Context mContext;
	private int curSelectPos = 0;
	public SelectDialog(Context context, int theme) {
	    super(context, theme);
	}

	public SelectDialog(Context context,String titleString,ArrayList<String> selectList,
			int curSelectPos , OnItemClickListener onItemClickListener) {
	    super(context);
	    mContext = context;
	    this.titleString = titleString;
	    this.selectList = selectList;
	    this.listener = onItemClickListener;
	    this.curSelectPos = curSelectPos;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dlg_select);
	    titleText = (TextView)findViewById(R.id.dlg_title_text);
	    titleText.setText(titleString);
	    dlgAdapter = new DlgAdapter();
	    dlgList = (ListView)findViewById(R.id.dlg_list);
	    dlgList.setAdapter(dlgAdapter);
	    dlgList.setOnItemClickListener(listener);
	    
	    Window window = getWindow();  
	    window.setGravity(Gravity.TOP);
	    LayoutParams params = window.getAttributes();
	    params.width = LayoutParams.MATCH_PARENT;
	    window.setAttributes(params);
	    
	}
	
	
	private class DlgAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return selectList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return selectList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;  
		    if (convertView == null) {  
		        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_dlg_select, null);  
		        holder = new ViewHolder();  
		        holder.selectText = (TextView) convertView.findViewById(R.id.list_text);  
		        holder.selectImage = (ImageView) convertView.findViewById(R.id.list_img);  
		        convertView.setTag(holder);  
		    }else{  
		        holder=(ViewHolder) convertView.getTag();  
		    }  
		    holder.selectText.setText(selectList.get(position));
		    if(curSelectPos!=position){
		    	holder.selectImage.setVisibility(View.INVISIBLE);
		    }else{
		    	holder.selectImage.setVisibility(View.VISIBLE);
		    }
		    
		    return convertView;
		}
		
		class ViewHolder {  
		  TextView selectText;  
		  ImageView selectImage;
		}  
		
	}
}
