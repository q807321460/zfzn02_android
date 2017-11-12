package com.jia.jdplay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jia.znjj2.R;
import com.judian.support.jdplay.api.data.JdSong;
import com.judian.support.jdplay.sdk.JdPlayControlContract;
import com.judian.support.jdplay.sdk.JdPlayControlPresenter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class JdPlayControlActivity extends Activity implements View.OnClickListener, JdPlayControlContract.View {

    private static final String TAG = "JdPlayControlActivity";
    /* 界面 */
    private View mContainView;
    private AtomicBoolean isPlaying = new AtomicBoolean(false);
    /* 播放界面 */
    private ImageView mBackBtn;
    private ImageView mPlaylistBtn;
    private SeekBar mSeekBar;
    private SeekBar mVolumeBar;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private ImageView mPlayModeBtn;
    private ImageView mPrevBtn;
    private ImageView mPlayPauseBtn;
    private ImageView mNextBtn;
    private ImageView mDeviceListBtn;
    private TextView mSongNameText;
    private TextView mSingerNameText;
    private ImageView mAlbumPicImageView;
    private JdPlayPlaylistPopupWindow mPlayListPopWindow;

    private JdPlayControlPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.jdplay_control_view);
        setUpPlayView();

        mPresenter = new JdPlayControlPresenter(this, this);
    }

    /* 播放界面 */
    private void setUpPlayView() {
        mContainView = getWindow().getDecorView();
        mBackBtn = (ImageView) findViewById(R.id.activity_back);
        mPlaylistBtn = (ImageView) findViewById(R.id.playlist);
        mCurrentTime = (TextView) findViewById(R.id.current_time);
        mTotalTime = (TextView) findViewById(R.id.total_time);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mVolumeBar = (SeekBar) findViewById(R.id.volume_bar);
        mPrevBtn = (ImageView) findViewById(R.id.prev);
        mPlayPauseBtn = (ImageView) findViewById(R.id.play_pause);
        mNextBtn = (ImageView) findViewById(R.id.next);
        mSongNameText = (TextView) findViewById(R.id.song_name);
        mSingerNameText = (TextView) findViewById(R.id.singer_name);
        mAlbumPicImageView = (ImageView) findViewById(R.id.album_pic);
        mPlayModeBtn = (ImageView) findViewById(R.id.play_mode);
        mDeviceListBtn = (ImageView)findViewById(R.id.device_list);

        mPlayModeBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mPlaylistBtn.setOnClickListener(this);
        mPrevBtn.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mPlayPauseBtn.setOnClickListener(this);
        mDeviceListBtn.setOnClickListener(this);
        findViewById(R.id.jdplay_music_resource).setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                mPresenter.seekTo(bar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar mProgressBar) {
            }

            @Override
            public void onProgressChanged(SeekBar mProgressBar, int progress, boolean fromUser) {
            }
        });

        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                mPresenter.setVolume(bar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar mProgressBar) {
            }

            @Override
            public void onProgressChanged(SeekBar mProgressBar, int progress, boolean fromUser) {
            }
        });

        mPlayListPopWindow = new JdPlayPlaylistPopupWindow(this, new JdPlayPlaylistPopupWindow.JdPlaylistWindowListener() {
            @Override
            public void onItemClick(int pos) {
                mPresenter.playPlaylistWithPos(pos);
            }
        });
    }

    @Override
    public void setVolume(int percent) {
        mVolumeBar.setProgress(percent);
    }

    @Override
    public void setPosition(int position) {
        mCurrentTime.setText(formatTime(position));
    }

    @Override
    public void setSeekProgress(int percent) {
        mSeekBar.setProgress(percent);
    }

    @Override
    public void setDuration(int duration) {
        mTotalTime.setText(formatTime(duration));
    }

    @Override
    public void setPlayOrPause(boolean isPlay) {
        mPlayPauseBtn.setImageResource(isPlay ? R.drawable.jdplay_pasue : R.drawable.jdplay_play);
    }

    @Override
    public void setPlayMode(String mode) {
        if (mode.equals("REPEAT_ONE"))
            mPlayModeBtn.setImageResource(R.drawable.jdplay_repeat_one);
        else if (mode.equals("SHUFFLE"))
            mPlayModeBtn.setImageResource(R.drawable.jdplay_shuffle);
        else if (mode.equals("REPEAT_ALL"))
            mPlayModeBtn.setImageResource(R.drawable.jdplay_repeat_all);
    }

    @Override
    public void setSongName(String name) {
        mSongNameText.setText(name);
    }

    @Override
    public void setSingerName(String name) {
        mSingerNameText.setText(name);
    }

    @Override
    public void setAlbumPic(String url) {
        Log.v(TAG, "setAlbumPic:" + url);
        if (url.equals("airplay")) {
            mAlbumPicImageView.setImageResource(R.drawable.jdplay_logo_apple);
        }
        else {
            JdPlayImageUtils.getInstance().displayImage(url, mAlbumPicImageView);
        }
    }

    @Override
    public void setPlaylist(List<JdSong> songs) {
        mPlayListPopWindow.setPlayList(songs);
    }

    @Override
    public void setPlaylistPosition(int position) {
        mPlayListPopWindow.setPlaylistPosition(position);
    }

    private void startJdMusicResourceActivity() {
        Intent intent = new Intent();
        intent.setClass(JdPlayControlActivity.this, JdMusicResourceActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                mPresenter.togglePlay();
                break;
            case R.id.prev:
                mPresenter.prev();
                break;
            case R.id.next:
                mPresenter.next();
                break;
            case R.id.play_mode:
                mPresenter.changePlayMode();
                break;
            case R.id.playlist:
                mPresenter.getPlayList();
                mPlayListPopWindow.showAtLocation(mContainView, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.jdplay_music_resource:
                startJdMusicResourceActivity();
                break;
            case R.id.activity_back:
                finish();
                break;
        }
    }

    public String formatTime(long timeMS) {
        int tmp = (int) timeMS / 1000;
        int m = tmp / 60;
        int s = tmp % 60;
        return String.format("%02d", m) + ":" + String.format("%02d", s);
    }

}
