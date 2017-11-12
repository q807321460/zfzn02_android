package com.jia.jdplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jia.znjj2.R;
import com.judian.support.jdplay.api.data.JdSong;
import com.judian.support.jdplay.api.data.resource.BCategory;
import com.judian.support.jdplay.sdk.JdPlayControlContract;
import com.judian.support.jdplay.sdk.JdPlayControlPresenter;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class JdMusicResourceActivity extends FragmentActivity implements CategoryFragment.CategoryFragmentListener,JdPlayControlContract.View,View.OnClickListener {
    private JdPlayControlPresenter mPresenter;

    private TextView mSongName;
    private ImageView mSongPic,mPlayBtn;
    private View mPlayBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jdplay_category_activity);
        initView();
    }

    private void initView(){
        mSongName = (TextView)findViewById(R.id.song_name);
        mSongPic = (ImageView)findViewById(R.id.song_img);
        mPlayBtn = (ImageView)findViewById(R.id.play_btn);
        mPlayBarView = findViewById(R.id.play_control_bar);
        mPlayBarView.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mPresenter = new JdPlayControlPresenter(this, this);


        CategoryFragment categoryFragment = new CategoryFragment();
        categoryFragment.setCategoryFragmentListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.jdplay_left_in,R.anim.jdplay_left_in,R.anim.jdplay_left_in,R.anim.jdplay_rigth_out);
        fragmentManager.beginTransaction().add(R.id.fragment_container, categoryFragment).commit();
    }

    @Override
    public void onItemClick(Object item, boolean last) {
        if (last) {
            Intent intent = new Intent();
            intent.setClass(JdMusicResourceActivity.this, JdPlayControlActivity.class);
            startActivity(intent);
        }
        else if (item instanceof BCategory){
            CategoryFragment categoryFragment = new CategoryFragment();
            Bundle args = new Bundle();
            BCategory bCategory = (BCategory)item;
            args.putParcelable("BCategory", bCategory);
            categoryFragment.setArguments(args);
            categoryFragment.setCategoryFragmentListener(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.jdplay_left_in,R.anim.jdplay_left_in,R.anim.jdplay_left_in,R.anim.jdplay_rigth_out);
            fragmentTransaction.add(R.id.fragment_container, categoryFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_btn:
                mPresenter.togglePlay();
                break;
            case R.id.play_control_bar:
                startJdPlayControlView();
                break;
        }
    }

    private void startJdPlayControlView() {
        Intent intent = new Intent();
        intent.setClass(JdMusicResourceActivity.this, JdPlayControlActivity.class);
        startActivity(intent);
    }

    @Override
    public void setVolume(int percent) {

    }

    @Override
    public void setSeekProgress(int percent) {

    }

    @Override
    public void setPosition(int position) {

    }

    @Override
    public void setDuration(int duration) {

    }

    @Override
    public void setPlayOrPause(boolean isPlay) {
        mPlayBtn.setImageResource(isPlay ? R.drawable.jdplay_pasue : R.drawable.jdplay_play);
    }

    @Override
    public void setPlayMode(String mode) {

    }

    @Override
    public void setSongName(String name) {
        mSongName.setText(name);
    }

    @Override
    public void setSingerName(String name) {

    }

    @Override
    public void setAlbumPic(String url) {
        if (url.equals("airplay")) {
            mSongPic.setImageResource(R.drawable.jdplay_logo_apple);
        }
        else {
            JdPlayImageUtils.getInstance().displayImage(url, mSongPic);
        }
    }

    @Override
    public void setPlaylist(List<JdSong> songs) {

    }

    @Override
    public void setPlaylistPosition(int position) {

    }
}
