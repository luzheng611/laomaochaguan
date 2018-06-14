package com.laomachaguan.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.laomachaguan.Common.HeadToInfo;
import com.laomachaguan.Common.Home_Zixun_detail;
import com.laomachaguan.Common.TG_Mine;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.View.mAudioManager;
import com.laomachaguan.View.mAudioView;
import com.laomachaguan.View.mItemDeraction;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * 作者：因陀罗网 on 2017/6/5 15:40
 * 公司：成都因陀罗网络科技有限公司
 */

public class Zixun_Tougao extends Fragment implements View.OnClickListener, OnRefreshListener {
    private static final int PAGESIZE = 10;
    private int page = 1;
    private int endPage = 0;
    private boolean isLoadMore = false;
    private boolean isRefresh = false;
    private SwipeRefreshLayout swip;
    private RecyclerView recyclerView;
    private ImageView iv_tougao;
    private TGAdapter adapter;
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zixun_tougao, container, false);
        getActivity().registerReceiver(receiver,new IntentFilter("main"));
        swip = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        iv_tougao = (ImageView) view.findViewById(R.id.float_tougao);
        iv_tougao.setOnClickListener(this);
        adapter = new TGAdapter(getActivity(), new ArrayList<MyItem>());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.openLoadMore(PAGESIZE, true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (endPage != page) {
                    isLoadMore = true;
                    page++;
                    getData();
                }
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                Intent intent=new Intent(getActivity(), Home_Zixun_detail.class);
                intent.putExtra("id", ((MyItem) adapter.getData().get(i)).getId());
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
//        linearLayoutManager.setReverseLayout(true);

        recyclerView.addItemDecoration(new mItemDeraction(1, R.color.black));
        recyclerView.setAdapter(adapter);
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
        TextView textView = new TextView(getActivity());
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.loadnothing);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 150), DimenUtils.dip2px(getActivity(), 150) * d.getIntrinsicHeight() / d.getIntrinsicWidth());
        textView.setCompoundDrawables(null, d, null, null);
        textView.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 10));
        textView.setText("暂无圈子动态");
        textView.setGravity(Gravity.CENTER);
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vl.topMargin = DimenUtils.dip2px(getActivity(), 180);
        textView.setLayoutParams(vl);
        adapter.setEmptyView(textView);
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    /**
     * 加载数据
     */
    private void getData() {
        if (!Network.HttpTest(getActivity())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.e("run: 页码：" + page + "最后一页：" + endPage);
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("page", page);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m = ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.Zixun_List_IP)
                            .params("key", m.K())
                            .params("msg", m.M()).execute().body().string();
                    if (!TextUtils.isEmpty(data)&&!"null".equals(data)) {
                        final ArrayList<MyItem> list = (ArrayList<MyItem>) AnalyticalJSON.getItemEntitys(data);
                        LogUtil.e("run: list------>" + list);
                        if (list != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        adapter.setNewData(list);
                                        isRefresh = false;
                                        swip.setRefreshing(false);
                                    } else if (isLoadMore) {
                                        isLoadMore = false;
                                        if (list.size() < PAGESIZE) {
                                            ToastUtil.showToastShort("已经没有更多数据啦", Gravity.CENTER);
//                                endPage = page;
                                            adapter.notifyDataChangedAfterLoadMore(list, false);
                                        } else {
                                            adapter.notifyDataChangedAfterLoadMore(list, true);
                                        }
                                    }

                                    swip.setRefreshing(false);
                                }
                            });
                        }
                    }else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                swip.setRefreshing(false);
                                if(isRefresh){
                                    adapter.getData().clear();
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        });

                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swip.setRefreshing(false);
                        }
                    });
                    e.printStackTrace();
                }
            }

        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.float_tougao:
                if(LoginUtil.checkLogin(getActivity())){
                    Intent itent = new Intent(getActivity(), TG_Mine.class);
                    startActivity(itent);
                }

                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        endPage = -1;
        isRefresh = true;
        adapter.openLoadMore(PAGESIZE,true);
        getData();
    }

    public static class TGAdapter extends BaseMultiItemQuickAdapter<MyItem> {
        public static final int TEXT = 0;
        public static final int AUDIO = 2;
        public static final int VIDEO = 1;
        private Activity context;

        public TGAdapter(Activity context, List<MyItem> data) {
            super(data);
            WeakReference<Activity> w = new WeakReference<Activity>(context);
            this.context = w.get();
            addItemType(TEXT, R.layout.item_zixun_tougao_text);
            addItemType(AUDIO, R.layout.item_zixun_tougao_audio);
            addItemType(VIDEO, R.layout.item_zixun_tougao_video);
        }

        @Override
        protected void convert(final BaseViewHolder hoder, final MyItem bean) {
            switch (hoder.getItemViewType()) {
                case TEXT:
                    if(bean.getThumbUrl().equals("")){
                        hoder.setVisible(R.id.thumb,false);
                    }else{
                        hoder.setVisible(R.id.thumb,true);
                        Glide.with(context).load(bean.getThumbUrl())
                                .override(DimenUtils.dip2px(context, 100)
                                        , DimenUtils.dip2px(context,75))
                                .centerCrop().into((ImageView) hoder.getView(R.id.thumb));
                    }

                    break;
                case AUDIO:
                    ((mAudioView) hoder.getView(R.id.audio))
                            .setOnImageClickListener(new mAudioView.onImageClickListener() {
                                @Override
                                public void onImageClick(mAudioView v) {
                                    final mAudioView m = (mAudioView) v;
                                    if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                        mAudioManager.release();
                                        mAudioManager.getAudioView().setPlaying(false);
                                        mAudioManager.getAudioView().resetAnim();
                                        if (v == mAudioManager.getAudioView()) {
                                            return;
                                        }
                                    }
                                    if (!m.isPlaying()) {
                                        Log.w(TAG, "onImageClick: 开始播放");
                                        mAudioManager.playSound(m, bean.getOptions(), new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                m.resetAnim();
                                            }
                                        }, new MediaPlayer.OnPreparedListener() {
                                            @Override
                                            public void onPrepared(MediaPlayer mp) {
                                                m.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                            }
                                        });

                                    } else {
                                        Log.w(TAG, "onImageClick: 停止播放");
                                        mAudioManager.release();
                                    }
                                }
                            });
                    break;
                case VIDEO:
                    JCVideoPlayer.releaseAllVideos();
                    ((JCVideoPlayerStandard) hoder.getView(R.id.video)).setUp(bean.getOptions(), "  ");
                    RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) hoder.getView(R.id.video).getLayoutParams();
                    l.width=context.getResources().getDisplayMetrics().widthPixels;
                    l.height=context.getResources().getDisplayMetrics().widthPixels*9/16;
                    ((JCVideoPlayerStandard) hoder.getView(R.id.video)).setLayoutParams(l);
                        if(!bean.getThumbUrl().equals("")){
                            Glide.with(context).load(bean.getThumbUrl()).override(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().widthPixels*9/16)
                                .centerCrop()
                                .into(((JCVideoPlayerStandard) hoder.getView(R.id.video)).thumbImageView);
                    }else{
                        Glide.with(context).load(R.drawable.player_bg).override(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().widthPixels*9/16)
                                .centerCrop()
                                .into(((JCVideoPlayerStandard) hoder.getView(R.id.video)).thumbImageView);
                    }

                    ((JCVideoPlayerStandard) hoder.getView(R.id.video)).backButton.setVisibility(View.GONE);
                    ((JCVideoPlayerStandard) hoder.getView(R.id.video)).fullscreenButton.setVisibility(View.GONE);
                    break;
            }
           Glide.with(context).load(bean.getHeadUrl()).override(
                   DimenUtils.dip2px(context,40),DimenUtils.dip2px(context,40)
           ).centerCrop().into((ImageView) hoder.getView(R.id.head));
            hoder.setOnClickListener(R.id.head, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeadToInfo.goToUserDetail(context,bean.getUser_id());
                }
            });
            hoder.setOnClickListener(R.id.petName, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HeadToInfo.goToUserDetail(context,bean.getUser_id());
                }
            });
          hoder.setText(R.id.title,bean.getTitle())
                  .setText(R.id.petName,bean.getPetName())
                  .setText(R.id.time, TimeUtils.getTrueTimeStr(bean.getTime()))
                  .setText(R.id.ctr,bean.getCtr())
                  .setText(R.id.like,bean.getLike())
                  .setText(R.id.commet,bean.getComment());
            Glide.with(context).load(R.drawable.ctr_small).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable d=new BitmapDrawable(resource);
                    d.setBounds(0,0,DimenUtils.dip2px(context,15),DimenUtils.dip2px(context,15));

                    ((TextView) hoder.getView(R.id.ctr)).setCompoundDrawables(d,null,null,null);
                    ((TextView) hoder.getView(R.id.ctr)).setCompoundDrawablePadding(DimenUtils.dip2px(context,5));
                }
            });
            Glide.with(context).load(R.drawable.like_small).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable d=new BitmapDrawable(resource);
                    d.setBounds(0,0,DimenUtils.dip2px(context,15),DimenUtils.dip2px(context,15));
                    ((TextView) hoder.getView(R.id.like)).setCompoundDrawables(d,null,null,null);
                    ((TextView) hoder.getView(R.id.like)).setCompoundDrawablePadding(DimenUtils.dip2px(context,5));
                }
            });
            Glide.with(context).load(R.drawable.comment_small).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Drawable d=new BitmapDrawable(resource);
                    d.setBounds(0,0,DimenUtils.dip2px(context,15),DimenUtils.dip2px(context,15));
                    ((TextView) hoder.getView(R.id.commet)).setCompoundDrawables(d,null,null,null);
                    ((TextView) hoder.getView(R.id.commet)).setCompoundDrawablePadding(DimenUtils.dip2px(context,5));
                }
            });

        }
    }


    public static class MyItem extends MultiItemEntity {
        String headUrl;
        String petName;
        String time;
        String title;
        String thumbUrl;
        String ctr;
        String like;
        String id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        String user_id;
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public String getPetName() {
            return petName;
        }

        public void setPetName(String petName) {
            this.petName = petName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }

        public String getCtr() {
            return ctr;
        }

        public void setCtr(String ctr) {
            this.ctr = ctr;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        String comment;
        String options;
    }

}
