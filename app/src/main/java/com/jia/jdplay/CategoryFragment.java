package com.jia.jdplay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jia.znjj2.R;
import com.judian.support.jdplay.api.data.resource.BCategory;
import com.judian.support.jdplay.api.data.resource.EglSong;
import com.judian.support.jdplay.sdk.JdMusicResourceContract;
import com.judian.support.jdplay.sdk.JdMusicResourcePresenter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/10.
 */

public class CategoryFragment extends BaseFragment implements AdapterView.OnItemClickListener,JdMusicResourceContract.View {
    private static final String TAG = "CategoryFragment";
    private View mRootView;
    private TextView mErrorTip,mTitle;
    private ImageView mClose;
    private PullToRefreshListView mListView;
    private CategoryAdapter mCategoryAdpter;
    private ProgressBar progressBar;
    private View mLoadingView;
    private List<Object> mObjects;
    private boolean mLastLevel;
    private Bundle mArgs;
    private JdMusicResourcePresenter mPresenter;
    private CategoryFragmentListener mCategoryFragmentListener;

    private Object category;

    private static final int MSG_LOAD_SUCCESS = 1;
    private static final int MSG_LOAD_FAIL = 2;
    private static final int MSG_SHOW_TOAST = 3;

    public interface CategoryFragmentListener{
        void onItemClick(Object item, boolean last);
    }

    private Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOAD_FAIL:
                    mErrorTip.setText("加载失败:" + (String)msg.obj);
                    progressBar.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                    mListView.onRefreshComplete();
                    break;
                case MSG_LOAD_SUCCESS:
                    mLoadingView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    List<Object> objs = (List<Object>)msg.obj;
                    if (mObjects.size()>0){
                        mObjects.addAll(mObjects.size(),objs);
                    }else{
                        mObjects.addAll(objs);
                    }
                    mCategoryAdpter.notifyDataSetChanged();
                    mListView.onRefreshComplete();
                    mListView.setMode(msg.arg1==1?PullToRefreshBase.Mode.PULL_FROM_END:PullToRefreshBase.Mode.DISABLED);
                    break;
                case MSG_SHOW_TOAST:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    mListView.onRefreshComplete();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.jdplay_category_view,null);
        mListView = (PullToRefreshListView) mRootView.findViewById(R.id.list_view);
        mLoadingView = mRootView.findViewById(R.id.loading_view);
        mTitle = (TextView) mRootView.findViewById(R.id.title);
        mClose = (ImageView)mRootView.findViewById(R.id.activity_back);
        mErrorTip = (TextView)mRootView.findViewById(R.id.tips);
        progressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
        mObjects = new ArrayList<>();
        mCategoryAdpter = new CategoryAdapter();
        ListView listView = mListView.getRefreshableView();
        listView.setAdapter(mCategoryAdpter);
        mListView.setOnItemClickListener(this);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.getMusicResourceMore();
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fr = getActivity().getSupportFragmentManager();
                if (fr.getBackStackEntryCount()>0){
                    fr .popBackStack();
                }else{
                    getActivity().finish();
                }
            }
        });

        mArgs = getArguments();
        mPresenter = new JdMusicResourcePresenter(getActivity().getApplicationContext(),this);
        category = mArgs == null ? null : ((BCategory)mArgs.getParcelable("BCategory"));
        mTitle.setText(mArgs == null ? getString(R.string.resource_lib) : ((BCategory)mArgs.getParcelable("BCategory")).getName());
        //动画完成后再加载
        mHanlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.getMusicResource(mArgs == null ? null : (BCategory)mArgs.getParcelable("BCategory"));
            }
        },400);
        return mRootView;
    }

    @Override
    public void setMusicResource(List<?> objs, boolean last, boolean loadMore) {
        Log.e(TAG, "setMusicResource  loadMore: " + loadMore+"   objs:"+objs.size());
        if (objs==null||objs.size()==0){
            if (mObjects.size()==0){
                mHanlder.sendMessage(mHanlder.obtainMessage(MSG_LOAD_FAIL, getString(R.string.data_empty)));
            }else{
                mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SHOW_TOAST, getString(R.string.no_more_data)));
            }
            return;
        }
        mLastLevel = last;
        Message message = new Message();
        message.what = MSG_LOAD_SUCCESS;
        message.obj = objs;
        message.arg1 = loadMore?1:0;
        mHanlder.sendMessage(message);
    }

    @Override
    public void onGetMusicResourceFail(int erroCode, String errMsg) {
        Log.e(TAG, "onGetMusicResourceFail: " + erroCode + " " + errMsg);
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_LOAD_FAIL, "" + erroCode));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object item = mObjects.get((int)id);
        if (item instanceof EglSong) {
            List<EglSong> songs = new ArrayList<>();
            for(Object obj : mObjects) {
                songs.add((EglSong) obj);
            }
            mPresenter.play(songs, position);
        }
        else if (item instanceof BCategory){
            BCategory bc = (BCategory)item;
            if (bc.getNextType() == BCategory.NextType.PlaySongList || bc.getNextType() == BCategory.NextType.PlaySong) {
                mPresenter.play(bc);
            }
        }
        mCategoryFragmentListener.onItemClick(item, mLastLevel);
    }

    public void setCategoryFragmentListener(CategoryFragmentListener listener) {
        mCategoryFragmentListener = listener;
    }

    private class CategoryAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mObjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mObjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.jdplay_category_list_item,null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.next.setVisibility(mLastLevel ? View.INVISIBLE : View.VISIBLE);

            Object obj = mObjects.get(position);
            if (obj instanceof BCategory){
                BCategory bCategory = (BCategory)obj;
                viewHolder.categoryName.setText(bCategory.getName());
                if (!TextUtils.isEmpty(bCategory.getImagePath())){
                    ImageLoader.getInstance().displayImage(bCategory.getImagePath(),viewHolder.categoryImg);
                }
            }
            else if (obj instanceof EglSong) {
                EglSong song = (EglSong)obj;
                viewHolder.categoryName.setText(song.getName());
                if (!TextUtils.isEmpty(song.getImgPath())){
                    ImageLoader.getInstance().displayImage(song.getImgPath(),viewHolder.categoryImg);
                }
            }

            return convertView;
        }
    }

    class ViewHolder {
        public TextView categoryName;
        public ImageView categoryImg;
        public ImageView next;
        public ViewHolder(View view){
            categoryName = (TextView) view.findViewById(R.id.category_name);
            categoryImg = (ImageView) view.findViewById(R.id.category_img);
            next = (ImageView) view.findViewById(R.id.next);
        }
    }
}
