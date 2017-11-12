package com.jia.jdplay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jia.znjj2.R;
import com.judian.support.jdplay.api.data.JdSong;

import java.util.ArrayList;
import java.util.List;


public class JdPlayPlaylistPopupWindow extends PopupWindow implements OnItemClickListener {
	private Context mContext;
	private View mRootView;
    private View mContain;
	private TextView mPlaylistTitle;
	private TextView mClose;
	private ListView mPlayListView;
	private PlayListAdapter mPlayListAdapter;
    private int mJdPlaylistPosition;
    private List<JdSong> mJdPlaylist = new ArrayList<JdSong>();
    private JdPlaylistWindowListener mJdPlaylistWindowListener;

    public interface JdPlaylistWindowListener {
        void onItemClick(int pos);
    }

	public JdPlayPlaylistPopupWindow(Context context, JdPlaylistWindowListener listener) {
		this.mContext = context;
        mJdPlaylistWindowListener = listener;
		LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.jdplay_popup_playlist_layout, null);
		mContain = mRootView.findViewById(R.id.container);
		mPlaylistTitle = (TextView)mRootView.findViewById(R.id.play_list_title);
		mPlayListView = (ListView)mRootView.findViewById(R.id.device_list);
		mPlayListView.setOnItemClickListener(this);
		mClose = (TextView)mRootView.findViewById(R.id.close);
		mClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setContentView(mRootView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);
        mRootView.setBackgroundColor(0x70000000);
        mRootView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mContain.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        mPlayListAdapter = new PlayListAdapter();
        mPlayListView.setAdapter(mPlayListAdapter);
	}

    private void refreshTitle(){
        mPlaylistTitle.setText(mContext.getString(R.string.play_list_title, mJdPlaylist.size()));
    }

	public void setPlayList(List<JdSong> songs){
        mJdPlaylist.clear();
        if (songs != null) {
            mJdPlaylist.addAll(songs);
        }
        mPlayListAdapter.notifyDataSetChanged();
	}

    public void setPlaylistPosition(int position) {
        mJdPlaylistPosition = position;
        mPlayListAdapter.notifyDataSetChanged();
    }

	@Override
	public final void dismiss() {

		Animation am = AnimationUtils.loadAnimation(mContext,
                R.anim.jdplay_sween_bottom_down);
		am.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        JdPlayPlaylistPopupWindow.super.dismiss();
                    }
                });
            }
        });
		Animation am2 = AnimationUtils.loadAnimation(mContext, R.anim.jdplay_fade_out);
		mRootView.startAnimation(am2);
		mContain.startAnimation(am);
	}
    
    @Override
    public final void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        mContain.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.jdplay_sween_bottom_up));
        Animation am2 = AnimationUtils.loadAnimation(mContext, R.anim.jdplay_fade_in);
        mRootView.startAnimation(am2);
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
        mJdPlaylistWindowListener.onItemClick(position);
        mPlayListAdapter.notifyDataSetChanged();
	}

    //----------- playlist view -------------------

    class viewHolder {
        public TextView songName;
        public TextView songSinger;
        public TextView songIndex;
    }

    class PlayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int count = mJdPlaylist.size();
            refreshTitle();
            return count;
        }

        @Override
        public Object getItem(int arg0) {
            return mJdPlaylist.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int pos, View v, ViewGroup arg2) {
            viewHolder holder = null;
            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.jdplay_popup_playlist_item, null);
                holder = new viewHolder();
                holder.songName = (TextView) v.findViewById(R.id.song_name);
                holder.songSinger = (TextView) v.findViewById(R.id.song_singer);
                holder.songIndex = (TextView) v.findViewById(R.id.song_index);
                v.setTag(holder);
            } else {
                holder = (viewHolder) v.getTag();
            }
            JdSong song = mJdPlaylist.get(pos);
            holder.songIndex.setText(String.valueOf(pos+1));
            holder.songName.setText(song.song_name);
            holder.songSinger.setText(TextUtils.isEmpty(song.singers) ? "" : "— " + song.singers);

            //设置当前播放的歌曲的颜色
            int color = ContextCompat.getColor(mContext, R.color.jdplay_color_popup_item_title_color);
            if(mJdPlaylistPosition == pos) {
                color = Color.rgb(255, 0, 0);
            }
            holder.songIndex.setTextColor(color);
            holder.songName.setTextColor(color);
            holder.songSinger.setTextColor(color);

            return v;
        }
    }
}
