package com.laomachaguan.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Common.User_Detail2;
import com.laomachaguan.Common.ViewPagerActivity;
import com.laomachaguan.R;
import com.laomachaguan.TouGao.RecordVideoActivity;
import com.laomachaguan.TouGao.TouGaoGridAdapter;
import com.laomachaguan.TouGao.mDialog;
import com.laomachaguan.TouGao.mGridView;
import com.laomachaguan.TouGao.recordAudio;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.ImageUtil;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.LoadMoreListView;
import com.laomachaguan.View.mAudioManager;
import com.laomachaguan.View.mAudioView;
import com.laomachaguan.View.photoUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/12/24.
 */
public class Ask_Detail extends AppCompatActivity implements View.OnClickListener, LoadMoreListView.OnLoadMore, SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout updownLayout, total;
    private FrameLayout options;
    private ImageView updown;
    private static final int RES_CODE = 1111;
    private TextView content;
    private ListView listview;
    private mGridView grid1;
    private String user_friend, id;
    private boolean isInit = true;
    private ArrayList<HashMap<String, String>> list;
    private ArrayList<String> imglist;
    private chatAdapter adapter;
    private int page = 1;
    private int endPage = -1;
    private TextView title, time, user;
    private TouGaoGridAdapter gridAdapter;

    private boolean isFirst = true;
    //    private TextView t1;//加载数据的底部提示
//    private ProgressBar p1;//加载数据的底部进度
    private EditText PLText;
    private TextView fasong;
    private SharedPreferences sp1;
    private InputMethodManager imm;
    private File tmpFile;
    private ImageView audioImg;
    private TextView audioText;


    private static final int CHOOSEPICTUE = 2;//相册
    private static final int TAKEPICTURE = 1;//相机
    private static final int ChooseVideoAndAudio = 3;//视频
    private Uri pictureUri = null;
    private AlertDialog dialog;
    private static final String TAG = "TouGao";
    public String path;
    private HttpParams httpParams;
    private SharedPreferences sp;
    private int screenWidth;
    private int a = 0;
    private ImageView addFile;//添加附件按钮
    private LinearLayout addFileLayout;//添加附件的空间
    //选择的图片集合
    private ArrayList<String> mImages = new ArrayList<>();
    private TouGaoGridAdapter adpter;
    private GridView grid;
    private File titleFile, videoFile, audioFile;
    private PopupWindow p;
    private AlertDialog progressDialog;
    private static final int MAX_VIDEO_TIME = 30;
    //语音开始录制时间
    private long startTime;
    private Timer timer;
    private String voicePath;//输出地址
    private mDialog voicedialog;//录制语音时的提示dialog
    private ImageView img;//voicedialog中的图片
    private TextView text;//voicedialog中的文字
    private int times = 0;//计时
    private recordAudio ra;//录制语音对象
    private TextView t;//录音按钮


    private PopupWindow pp;
    private View view;
    //    private ScrollView scrollView;
    private SwipeRefreshLayout swip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_detail);
        user_friend = getIntent().getStringExtra("id");
        initView();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                getChat();
            }
        });

    }

    /**
     * 获取数据
     */
    private void getData() {
        ProgressUtil.show(this, "", "正在加载....");

        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("user_friend", user_friend);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Chat_List)
                .params("key", m.K())
                .params("msg", m.M()).execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                String data = null;
                try {
                    data = response.body().string();

                    Log.w(TAG, "onResponse: dta,,.,.,." + data);
                    if (!"".equals(data)) {
                        final HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                        if (map != null) {
                            if (map.get("image1") != null && !map.get("image1").equals("")) {
                                imglist.add(map.get("image1"));
                            }
                            if (map.get("image1") != null && !map.get("image2").equals("")) {
                                imglist.add(map.get("image2"));
                            }
                            if (map.get("image1") != null && !map.get("image3").equals("")) {
                                imglist.add(map.get("image3"));
                            }
                            gridAdapter.setmImgs(imglist);
                            gridAdapter.notifyDataSetChanged();
                            if ("".equals(map.get("title"))) {
                                title.setVisibility(View.GONE);
                            } else {
                                title.setText(map.get("title"));
                            }

                            content.setText(map.get("contents"));
                            time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
                            if (map.get("options") != null && !map.get("options").equals("")) {
                                if (map.get("options").endsWith(".mp4")) {
                                    //视频
                                    Log.w(TAG, "onBindViewHolder: 显示视频");
                                    JCVideoPlayerStandard j = new JCVideoPlayerStandard(Ask_Detail.this);
                                    LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dip2px(Ask_Detail.this, 180));
                                    l.gravity = Gravity.CENTER_HORIZONTAL;
                                    j.setLayoutParams(l);
                                    options.addView(j);
                                    j.setUp(map.get("options"), "  ");
                                    Glide.with(Ask_Detail.this).load(map.get("image1")).override(DimenUtils.dip2px(Ask_Detail.this, 180), DimenUtils.dip2px(Ask_Detail.this, 180)).fitCenter().into(j.thumbImageView);
                                    options.setVisibility(View.VISIBLE);
//                                    j.isFull=true;
                                } else if (map.get("options").endsWith(".mp3")) {
                                    //音频
                                    Log.w(TAG, "onBindViewHolder: 显示音频");
                                    options.removeAllViews();
                                    mAudioManager.release();
                                    final mAudioView mAudioView = new mAudioView(Ask_Detail.this);
                                    mAudioView.setOnImageClickListener(new mAudioView.onImageClickListener() {
                                        @Override
                                        public void onImageClick(final mAudioView v) {
                                            if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                                                mAudioManager.release();
                                                mAudioManager.getAudioView().setPlaying(false);
                                                mAudioManager.getAudioView().resetAnim();
                                                if (v == mAudioManager.getAudioView()) {
                                                    return;
                                                }
                                            }
                                            if (!mAudioView.isPlaying()) {
                                                Log.w(TAG, "onImageClick: 开始播放");
                                                mAudioManager.playSound(v, map.get("options"), new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        mAudioView.resetAnim();
                                                    }
                                                }, new MediaPlayer.OnPreparedListener() {
                                                    @Override
                                                    public void onPrepared(MediaPlayer mp) {
                                                        mAudioView.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                                    }
                                                });

                                            } else {
                                                Log.w(TAG, "onImageClick: 停止播放");
                                                mAudioManager.release();
                                            }

                                        }
                                    });
                                    options.addView(mAudioView);
                                }
                            } else {
                                options.setVisibility(View.GONE);
                            }
                            getChat();

                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });

    }

    private void getChat() {

        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(this));
            js.put("user_friend", user_friend);
            js.put("page", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Chat_List)
                .params("key", m.K())
                .params("msg", m.M()).execute(new AbsCallback<Object>() {
            @Override
            public Object parseNetworkResponse(Response response) throws Exception {
                return null;
            }

            @Override
            public void onSuccess(Object o, Call call, Response response) {

                try {
                    String dta = response.body().string();
                    if (!dta.equals("")) {
                        ArrayList<HashMap<String, String>> list = AnalyticalJSON.getList_zj(dta);
                        LogUtil.e("111");
                        if (list != null) {
                            LogUtil.e("222");
                            if (list.size() != 25) {
                                endPage = page;
                            }
                            if (adapter.list.size() == 0) {
                                LogUtil.e("333");
                                adapter.list = list;
                                for (HashMap<String, String> map : list) {
                                    adapter.Time.add("0");
                                }
                                adapter.notifyDataSetChanged();
                                listview.setSelection(adapter.list.size()-1);
                            } else {
                                LogUtil.e("444");
                                LogUtil.e("加载前数组：："+adapter.list+"   page::"+page);
                                adapter.list.addAll(0, list);
                                LogUtil.e("加载后数组：："+adapter.list+"   page::"+page);
                                for (HashMap<String, String> map : list) {
                                    adapter.Time.add(0,"0");
                                }
                                adapter.notifyDataSetChanged();
                                listview.setSelection(list.size()-1);
                            }


                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onAfter(@Nullable Object o, @Nullable Exception e) {
                super.onAfter(o, e);
                swip.setRefreshing(false);
            }


        });
    }

    private void initView() {
        // TODO: 2016/12/24 初始化
//        scrollView= (ScrollView) findViewById(R.id.scroll);
        swip = (SwipeRefreshLayout) findViewById(R.id.swip);
        swip.setOnRefreshListener(this);
        swip.setColorSchemeResources(R.color.main_color);
        view = LayoutInflater.from(this).inflate(R.layout.ask_popup, null);
        options = (FrameLayout) view.findViewById(R.id.zixun_item_option);
        content = (TextView) view.findViewById(R.id.zixun_item_content);
        grid1 = (mGridView) view.findViewById(R.id.zixun_item_iamges);
        gridAdapter = new TouGaoGridAdapter(this, imglist, false);
        grid1.setAdapter(gridAdapter);

        list = new ArrayList<>();
        imglist = new ArrayList<>();
        audioImg = (ImageView) findViewById(R.id.audio_toggle);
        audioImg.setOnClickListener(this);
        audioText = (TextView) findViewById(R.id.audio_text);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        PLText = (EditText) findViewById(R.id.zixun_item_input);
        sp = PreferenceUtil.getUserIncetance(this);
        fasong = (TextView) findViewById(R.id.zixun_item_fasong);


        updown = (ImageView) findViewById(R.id.zixun_item_img);
//        updownLayout = (LinearLayout) findViewById(R.id.updownLayout);
        total = (LinearLayout) findViewById(R.id.linearlayout_total);

        listview = (ListView) findViewById(R.id.zixun_item_plListview);
        adapter = new chatAdapter(Ask_Detail.this, list);

        listview.setAdapter(adapter);
        title = (TextView) findViewById(R.id.zixun_item_title);
        time = (TextView) findViewById(R.id.zixun_item_time);
//        t1 = (TextView) (listview.footer.findViewById(R.id.load_more_text));
//        p1 = (ProgressBar) (listview.footer.findViewById(R.id.load_more_bar));
        user = (TextView) findViewById(R.id.zixun_item_name);
        if (getIntent().getStringExtra("name") != null) {
            user.setText(getIntent().getStringExtra("name"));
        } else {
            user.setVisibility(View.GONE);
        }
        audioText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int p1 = PermissionChecker.checkSelfPermission(Ask_Detail.this, Manifest.permission.RECORD_AUDIO);
                int p2 = PermissionChecker.checkSelfPermission(Ask_Detail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (!(p1 == PackageManager.PERMISSION_GRANTED) || !(p2 == PackageManager.PERMISSION_GRANTED)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                0x00);
                    }
                    return false;
                } else {
                    ra = recordAudio.getInstance();
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.w(TAG, "onTouch: 按下   路径：" + voicePath);
                        startTime = System.currentTimeMillis();
                        voicedialog = new mDialog(Ask_Detail.this, R.style.MyDialog);
                        View view2 = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.alert_voice, null);
                        voicedialog.setView(view2);
                        voicedialog.show();
                        Window w = voicedialog.getWindow();
                        w.setGravity(Gravity.CENTER);
                        WindowManager.LayoutParams wl = w.getAttributes();
                        wl.width = DimenUtils.dip2px(mApplication.getInstance(), 200);
                        wl.height = DimenUtils.dip2px(mApplication.getInstance(), 200);
                        w.setDimAmount(0f);
                        w.setAttributes(wl);
                        img = (ImageView) view2.findViewById(R.id.alert_voice_image);
                        text = (TextView) view2.findViewById(R.id.alert_voice_text);
                        img.setImageResource(R.drawable.ic_settings_voice_white_48dp);
                        text.setText("语音录制中...");
                        audioText.setText("松开手指，结束录制");
//                        t.setBackgroundColor(Color.parseColor("#909b9b9b"));
                        //开始录制语音
                        voicePath = ra.getPath() + "/" + startTime + ".mp3";
                        ra.startRecord(voicePath);
                        timer = new Timer("voice");
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                times++;
                                Log.w(TAG, "handleMessage: times____>" + times);
                                mHandler.obtainMessage(0x00, text).sendToTarget();
                            }
                        }, 0, 1000);

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        //发送语音
                        Log.w(TAG, "onTouch: 抬起    路径：" + voicePath);
//                        t.setBackgroundColor(Color.WHITE);
                        if (times <= 1) {
                            Toast.makeText(Ask_Detail.this, "录制时间过短", Toast.LENGTH_SHORT).show();
                            if (ra.mRecorder != null && ra.isRecording) {
                                try {
                                    ra.stopRecord();
                                } catch (Exception e) {
                                    ra.mRecorder = null;
                                }
                                new File(voicePath).delete();
                            }

                        } else if (ra.mRecorder != null && ra.isRecording) {

                            ra.stopRecord();
                            uploadAudio();
//                            showInfoThenUploadFile(voicePath, times + "");
//                            t.setVisibility(View.GONE);
//                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                            p.dismiss();
                        }
                        audioText.setText("按  住  说  话");
                        if (voicedialog != null) voicedialog.dismiss();
                        startTime = 0;
                        times = 0;
                        if (timer != null)
                            timer.cancel();
                        timer = null;
                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        Log.w(TAG, "onTouch: cancel    路径：" + voicePath);
                        if (voicedialog != null && voicedialog.isShowing()) {
                            voicedialog.dismiss();
                        }
                        if (times <= 1) {
                            if (ra.mRecorder != null && ra.isRecording) {
                                try {
                                    ra.stopRecord();
                                } catch (Exception e) {
                                    ra.mRecorder = null;
                                }
                                new File(voicePath).delete();
                            }

                        } else if (ra.mRecorder != null && ra.isRecording) {
                            ra.stopRecord();
//                                            showInfoThenUploadFile(voicePath, times + "");
//                            audioText.setVisibility(View.GONE);
//                            view.findViewById(R.id.choose_layout).setVisibility(View.VISIBLE);
//                                            p.dismiss();
                        }
                        audioText.setText("按  住  说  话");
                        startTime = 0;
                        times = 0;
                        timer.cancel();
                        timer = null;
                    }

                    return true;
                }

            }
        });
    }

    /**
     * 上传录音并显示
     */
    private void uploadAudio() {

        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_id", PreferenceUtil.getUserId(Ask_Detail.this));
            js.put("user_friend", user_friend);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("发送语音：：" + js);
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Chat_Content_UpLoad)
                .params("key", m.K())
                .params("msg", m.M())
                .params("video", new File(voicePath))
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        if (response != null) {
                            try {
                                String data = response.body().string();
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(map.get("code"))) {
                                    String id = map.get("id");
                                    String audio = map.get("url");
                                    if (audio != null && !"".equals(audio)) {
                                        showAudio(id, audio);
                                        new File(voicePath).delete();
                                    }
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 显示音频
     */
    private void showAudio(String id, String audio) {
        final HashMap<String, String> map = new HashMap<>();
        String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
        final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
        String petname = sp.getString("pet_name", "");
        map.put("user_image", headurl);
        map.put("contents", "");
        map.put("pet_name", petname);
        map.put("time", time);
        map.put("id", id);
        map.put("user_id", sp.getString("user_id", ""));
        map.put("image", "");
        map.put("video", audio);
        listview.setFocusable(true);
        if (adapter.list.size() == 0) {
            adapter.list.add(map);
            adapter.Time.add("0");
            listview.setAdapter(adapter);

        } else {
            adapter.list.add(map);
            adapter.Time.add("0");
            adapter.notifyDataSetChanged();

        }
        listview.smoothScrollToPosition(adapter.list.size() - 1);
//        listview.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x00) {
                TextView t = (TextView) msg.obj;
                t.setText(" " + times + "'");
                if (times >= 60) {
                    ra.stopRecord();
                    Toast.makeText(Ask_Detail.this, "最长录制1分钟语音", Toast.LENGTH_SHORT).show();
                    audioText.setText("按  住  说  话");

                    // TODO: 2017/1/12
                    PLText.setVisibility(View.VISIBLE);
                    audioImg.setSelected(false);


                    uploadAudio();
                    if (voicedialog != null) voicedialog.dismiss();
                    voicedialog = null;
//                    showInfoThenUploadFile(voicePath, times + "");
                    times = 0;
                    if (timer != null) timer.cancel();
                    timer = null;
                    startTime = 0;
                }
            }
        }
    };





    @Override
    protected void onDestroy() {
        super.onDestroy();
        JCVideoPlayer.releaseAllVideos();
        mAudioManager.release();
        Intent intent=new Intent("chat");
        sendBroadcast(intent);
    }

    @Override
    public void onClick(final View v) {
        // TODO: 2016/12/24 点击事件
        switch (v.getId()) {
            case R.id.audio_toggle:
                if (audioText.getVisibility() == View.GONE) {
                    audioText.setVisibility(View.VISIBLE);
                    PLText.setVisibility(View.GONE);
                    v.setSelected(true);
                    audioText.setText("按  住  说  话");
                } else {
                    audioText.setVisibility(View.GONE);
                    PLText.setVisibility(View.VISIBLE);
                    v.setSelected(false);
                }
                break;
//            case R.id.zixun_item_img:
////                if (v.getTag() != null && ((boolean) v.getTag())) {
////                    content.setVisibility(View.GONE);
////                    grid1.setVisibility(View.GONE);
////                    options.setVisibility(View.GONE);
////                    v.setTag(false);
////                    ((ImageView) v).setImageResource(R.drawable.down);
////                    updownLayout.setBackgroundColor(Color.WHITE);
//////                    total.layout(0,v.getTop(),getResources().getDisplayMetrics().widthPixels,v.getBottom()+DimenUtils.dip2px(this,10));
////                } else {
////                    content.setVisibility(View.VISIBLE);
////                    grid1.setVisibility(View.VISIBLE);
////                    options.setVisibility(View.GONE);
////                    v.setTag(true);
////                    ((ImageView) v).setImageResource(R.drawable.up);
////                    updownLayout.setBackgroundColor(Color.TRANSPARENT);
////                }
//                if (pp == null) {
//                    pp = new PopupWindow(view);
//                    pp.setFocusable(true);
//                    pp.setOutsideTouchable(true);
//                    pp.setTouchable(true);
//                    ColorDrawable c = new ColorDrawable(Color.WHITE);
//                    pp.setAnimationStyle(R.style.popupwindow_fromTop);
//                    pp.setBackgroundDrawable(c);
//                    pp.setWidth(getResources().getDisplayMetrics().widthPixels);
//                    pp.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//                    pp.showAsDropDown(v, -50, -50);
//                    pp.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            ((ImageView) v).setImageResource(R.drawable.down);
//                            JCVideoPlayer.releaseAllVideos();
//                            mAudioManager.release();
//                        }
//                    });
//                } else {
//                    if (pp.isShowing()) pp.dismiss();
//                    pp.showAsDropDown(v, -50, 0);
//                }
//                ((ImageView) v).setImageResource(R.drawable.up);
//                break;
            case R.id.zixun_item_back:
                finish();
                break;
            case R.id.zixun_item_fasong:
                if (PLText.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "请输入评论", Toast.LENGTH_SHORT).show();
                    return;
                }

                v.setEnabled(false);
                ProgressUtil.show(this, "", "正在提交");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            final String content = PLText.getText().toString();

                            JSONObject js = new JSONObject();
                            try {
                                js.put("m_id", Constants.M_id);
                                js.put("user_id", PreferenceUtil.getUserId(Ask_Detail.this));
                                js.put("contents", content);
                                js.put("user_friend", user_friend);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LogUtil.e("发送文字：：" + js);
                            ApisSeUtil.M m = ApisSeUtil.i(js);
                            final String data = OkHttpUtils.post(Constants.Chat_Content_UpLoad)
                                    .params("key", m.K())
                                    .params("msg", m.M()).execute().body().string();
                            if (!data.equals("")) {
                                final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final HashMap<String, String> map = new HashMap<>();
                                            String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                            final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                            String petname = sp.getString("pet_name", "");
                                            map.put("user_image", headurl);
                                            map.put("contents", content);
                                            map.put("pet_name", petname);
                                            map.put("time", time);
                                            map.put("id", hashMap.get("id"));
                                            map.put("user_id", sp.getString("user_id", ""));
                                            map.put("image", "");
                                            map.put("video", "");
                                            if (adapter.list.size() == 0) {
                                                adapter.list.add(map);
                                                adapter.Time.add("0");
                                                listview.setAdapter(adapter);

                                            } else {
                                                adapter.list.add(map);
                                                adapter.Time.add("0");
                                                adapter.notifyDataSetChanged();

                                            }
                                            v.setEnabled(true);
                                            PLText.setText("");
//                                            imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);
                                            listview.smoothScrollToPosition(adapter.list.size() - 1);
//                                            listview.post(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//                                                }
//                                            });
                                            ProgressUtil.dismiss();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.setEnabled(true);
                                        ProgressUtil.dismiss();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.zixun_detail_more:
//
                //添加附件
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                View view = LayoutInflater.from(mApplication.getInstance()).inflate(R.layout.chat_bottom_dialog, null);
                builder.setView(view);
                builder.setCancelable(true);
                final Dialog dialog = builder.create();
                view.findViewById(R.id.video).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p1 = PermissionChecker.checkSelfPermission(Ask_Detail.this, Manifest.permission.CAMERA);
                        int p2 = PermissionChecker.checkSelfPermission(Ask_Detail.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        int p3 = PermissionChecker.checkSelfPermission(Ask_Detail.this, Manifest.permission.RECORD_AUDIO);
                        if (p1 != PackageManager.PERMISSION_GRANTED || p3 != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, TAKEPICTURE);
                                return;
                            }
                        }
                        Intent intent = new Intent(Ask_Detail.this, RecordVideoActivity.class);
                        startActivityForResult(intent, 000);
//                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                        if (intent.resolveActivity(getPackageManager()) != null) {
//                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
//                            videoFile = new File(FileUtils.TEMPPAH, System.currentTimeMillis() + ".mp4");
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
//                            startActivityForResult(intent, 6666);
//                        }
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.pic).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (tmpFile != null) {
                            tmpFile.delete();
                        }
                        photoUtil.choosePic(Ask_Detail.this, 0);
                    }
                });
                view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Window window = dialog.getWindow();
                window.setWindowAnimations(R.style.dialogWindowAnim);
                window.setBackgroundDrawableResource(R.color.vifrification);
                dialog.show();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data == null) {
                uri = photoUtil.uri;
            } else {
                uri = data.getData();
            }
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.pic_dialog, null);
            b.setView(view);
            final Dialog d = b.create();
            ImageView imageview = (ImageView) view.findViewById(R.id.photo);
            Glide.with(this).load(uri).asBitmap().override(DimenUtils.dip2px(this, 160), DimenUtils.dip2px(this, 160)).into(imageview);
            view.findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (d != null && d.isShowing()) d.dismiss();
                }
            });
            final Uri finalUri = uri;
            LogUtil.e("图片uri："+uri);

            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressUtil.show(Ask_Detail.this, "", "正在提交");
                    Tiny.getInstance().init(mApplication.getInstance());
                    Tiny.getInstance().source(ImageUtil.getImageAbsolutePath(Ask_Detail.this,finalUri)).asFile().compress(new FileCallback() {
                        @Override
                        public void callback(boolean isSuccess, String outfile) {
                            if(isSuccess){
                                tmpFile=new File(outfile);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            JSONObject js = new JSONObject();
                                            try {
                                                js.put("m_id", Constants.M_id);
                                                js.put("user_friend", user_friend);
                                                js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ApisSeUtil.M m = ApisSeUtil.i(js);
                                            final String data = OkHttpUtils.post(Constants.Chat_Content_UpLoad)

                                                    .params("image", tmpFile)
                                                    .params("key", m.K())
                                                    .params("msg", m.M()).execute().body().string();
                                            if (!data.equals("")) {
                                                Log.i(TAG, "run:      data------>" + data);
                                                final HashMap<String, String> hashMap = AnalyticalJSON.getHashMap(data);
                                                if (hashMap != null && "000".equals(hashMap.get("code"))) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            final HashMap<String, String> map = new HashMap<>();
                                                            String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
                                                            final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
                                                            String petname = sp.getString("pet_name", "");
                                                            map.put("user_image", headurl);
                                                            map.put("contents", "");
                                                            map.put("pet_name", petname);
                                                            map.put("time", time);
                                                            map.put("id", hashMap.get("id"));
                                                            map.put("user_id", sp.getString("user_id", ""));
                                                            map.put("image", hashMap.get("url"));
                                                            map.put("video", "");
                                                            listview.setFocusable(true);
                                                            if (adapter.list.size() == 0) {
                                                                adapter.list.add(map);
                                                                adapter.Time.add("0");
                                                                listview.setAdapter(adapter);

                                                            } else {
                                                                adapter.list.add(map);
                                                                adapter.Time.add("0");
                                                                adapter.notifyDataSetChanged();

                                                            }
                                                            listview.smoothScrollToPosition(adapter.list.size() - 1);

                                                            PLText.setText("");
                                                            imm.hideSoftInputFromWindow(PLText.getWindowToken(), 0);

                                                            d.dismiss();
                                                            ProgressUtil.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ProgressUtil.dismiss();
                                                            Toast.makeText(Ask_Detail.this, "提交失败，图片过大或网络异常", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ProgressUtil.dismiss();
                                                        Toast.makeText(Ask_Detail.this, "提交失败，图片过大或网络异常", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProgressUtil.dismiss();
                                                    Toast.makeText(Ask_Detail.this, "提交失败，图片过大或网络异常", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }else{
                                ToastUtil.showToastShort("图片压缩失败，请稍后重试");
                            }
                        }
                    });
//                    Bitmap bm = ImageUtil.getImageThumbnail(ImageUtil.getImageAbsolutePath(Ask_Detail.this, finalUri), ImageUtil.mWidth, ImageUtil.mHeight);
//                    try {
//                        tmpFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
//                        bm.compress(Bitmap.CompressFormat.JPEG, 60, new FileOutputStream(tmpFile));
//                        Log.w(TAG, "onActivityResult: 文件大小：" + tmpFile.length());
//                        bm.recycle();
//                        bm = null;
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.w(TAG, "onActivityResult: temfile++++" + tmpFile + "     advice_id:" + id);



                }
            });
            d.show();
            WindowManager.LayoutParams wl = d.getWindow().getAttributes();
            wl.gravity = Gravity.CENTER;
            d.getWindow().setAttributes(wl);
        } else if (requestCode == 6666 && resultCode == RESULT_OK) {
            Uri uri1 = data.getData();
            if (uri1 != null && !uri1.getPath().equals("")) {
                uploadVideo();
            }
            Log.w(TAG, "onActivityResult: " + data.getData());

        } else if (resultCode == RES_CODE) {
            String path = data.getStringExtra("path");
            videoFile = new File(path);

            uploadVideo();


        }


    }

    /**
     * 上传视频
     */
    private void uploadVideo() {

        JSONObject js = new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
            js.put("user_friend", user_friend);
            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m = ApisSeUtil.i(js);
        OkHttpUtils.post(Constants.Chat_Content_UpLoad)
                .params("key", m.K())
                .params("msg", m.M())
                .params("video", videoFile)
                .execute(new AbsCallback<Object>() {
                    @Override
                    public Object parseNetworkResponse(Response response) throws Exception {
                        return null;
                    }

                    @Override
                    public void onSuccess(Object o, Call call, Response response) {
                        if (response != null) {
                            try {
                                String data = response.body().string();
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data);
                                if (map != null && "000".equals(map.get("code"))) {
                                    String id = map.get("id");
                                    String video = map.get("url");
                                    if (video != null && !"".equals(video)) {
                                        showVideo(id, video);
                                    }
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(Ask_Detail.this,"","正在上传视频");
                    }

                    @Override
                    public void onAfter(@Nullable Object o, @Nullable Exception e) {
                        super.onAfter(o, e);
                        ProgressUtil.dismiss();
                    }
                });
    }

    private void showVideo(String id, String video) {
        final HashMap<String, String> map = new HashMap<>();
        String headurl = sp.getString("head_path", "").equals("") ? sp.getString("head_url", "") : sp.getString("head_path", "");
        final String time = TimeUtils.getStrTime(System.currentTimeMillis() + "");
        String petname = sp.getString("pet_name", "");
        map.put("user_image", headurl);
        map.put("contents", "");
        map.put("pet_name", petname);
        map.put("time", time);
        map.put("id", id);
        map.put("user_id", sp.getString("user_id", ""));
        map.put("image", "");
        map.put("video", video);
        listview.setFocusable(true);
        if (adapter.list.size() == 0) {
            adapter.list.add(map);
            adapter.Time.add("0");
            listview.setAdapter(adapter);

        } else {
            adapter.list.add(map);
            adapter.Time.add("0");
            adapter.notifyDataSetChanged();

        }
        listview.smoothScrollToPosition(adapter.list.size() - 1);
//        listview.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });

    }

    /**
     * 上拉加载
     */
    @Override
    public void loadMore() {

    }

    @Override
    public void onRefresh() {
        LogUtil.e("当前页面：："+page+"   最后一页：："+endPage);
        if (page != endPage) {
            page++;
            getChat();
        } else {
            swip.setRefreshing(false);
        }

    }


    public class chatAdapter extends BaseAdapter {
        private Activity a;
        private WeakReference<Activity> w;
        private ArrayList<HashMap<String, String>> list;
        private LayoutInflater inflater;
        private String concern_id, user_id;
        public ArrayList<String> Time;

        public chatAdapter(Activity a, ArrayList<HashMap<String, String>> list) {
            super();
            w = new WeakReference<Activity>(a);
            this.a = w.get();
            this.list = list;
            inflater = a.getLayoutInflater();
            Time = new ArrayList<>();
            for (HashMap<String, String> map : list) {
                String t = "0";
                Time.add(t);
            }
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).get("user_id").equals(PreferenceUtil.getUserIncetance(a).getString("user_id", ""))) {
                return 1;
            } else {
                return 0;
            }

        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            Holder holder = null;
            final HashMap<String, String> map = list.get(position);

            if (view == null) {
                holder = new Holder();
                if (getItemViewType(position) == 0) {
                    view = inflater.inflate(R.layout.chat_left, parent, false);
                } else {
                    view = inflater.inflate(R.layout.chat_right, parent, false);
                }
                holder.head = (AvatarImageView) view.findViewById(R.id.chat_head);
                holder.image = (ImageView) view.findViewById(R.id.chat_img);
                holder.name = (TextView) view.findViewById(R.id.chat_name);
                holder.msg = (TextView) view.findViewById(R.id.chat_msg);
                holder.tip = (ImageView) view.findViewById(R.id.chat_head_tip);
                holder.time = (TextView) view.findViewById(R.id.chat_time);
                holder.audio = (mAudioView) view.findViewById(R.id.audio);
                holder.player = (JCVideoPlayerStandard) view.findViewById(R.id.video);
                holder.headlayout= (RelativeLayout) view.findViewById(R.id.headLayout);
                view.setTag(holder);
            } else {
                holder = (Holder) view.getTag();
            }
            if(getItemViewType(position)==0){
                Glide.with(a).load(map.get("user_image")).override(DimenUtils.dip2px(a, 60), DimenUtils.dip2px(a, 60)).centerCrop().into(holder.head);
                holder.name.setText(map.get("pet_name"));
            }else{
                Glide.with(a).load(PreferenceUtil.getUserIncetance(a).getString("head_url","")).override(DimenUtils.dip2px(a, 60), DimenUtils.dip2px(a, 60)).centerCrop().into(holder.head);
                holder.name.setText(PreferenceUtil.getUserIncetance(a).getString("pet_name",""));
            }

            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
            holder.head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(a, User_Detail2.class);
                    intent.putExtra("id", map.get("user_id"));
                    intent.putExtra("add", false);
                    a.startActivity(intent);
                }
            });
            if (position != 0) {
                if (TimeUtils.dataOne(map.get("time")) - TimeUtils.dataOne(list.get(position - 1).get("time")) <= 180000) {
                    holder.time.setVisibility(View.GONE);
                } else {
                    holder.time.setVisibility(View.VISIBLE);
                }
            } else {
                holder.time.setVisibility(View.VISIBLE);
            }
            if (!map.get("contents").equals("")) {
                holder.player.setVisibility(View.GONE);
                holder.image.setVisibility(View.GONE);
                holder.msg.setVisibility(View.VISIBLE);
                holder.audio.setVisibility(View.GONE);
                holder.msg.setText(map.get("contents"));
                ViewGroup.LayoutParams vl=holder.headlayout.getLayoutParams();
                vl.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.headlayout.setLayoutParams(vl);
            } else if (!"".equals(map.get("image"))) {

                holder.player.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                holder.msg.setVisibility(View.GONE);
                holder.audio.setVisibility(View.GONE);
                Glide.with(a).load(map.get("image")).fitCenter().override(DimenUtils.dip2px(a, 180), DimenUtils.dip2px(a, 180)).into(holder.image);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> l = new ArrayList<String>();
                        l.add(map.get("image"));
                        Intent intent = new Intent(a, ViewPagerActivity.class);
                        intent.putStringArrayListExtra("array", l);
                        a.startActivity(intent);
                    }
                });
            } else if (!"".equals(map.get("video")) && map.get("video").endsWith("mp3")) {
                ViewGroup.LayoutParams vl=holder.headlayout.getLayoutParams();
                vl.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.headlayout.setLayoutParams(vl);
                holder.player.setVisibility(View.GONE);
                holder.audio.setTag(position);
                holder.msg.setVisibility(View.GONE);
                holder.image.setVisibility(View.GONE);
                holder.audio.setVisibility(View.VISIBLE);
                if (!Time.get(position).equals("0")) {
                    holder.audio.setTime(Integer.parseInt(Time.get(position)));
                } else {
                    holder.audio.setTime(0);
                }
                holder.audio.setOnImageClickListener(new mAudioView.onImageClickListener() {
                    @Override
                    public void onImageClick(final mAudioView v) {
                        if (mAudioManager.getAudioView() != null && mAudioManager.getAudioView().isPlaying()) {
                            mAudioManager.release();
                            mAudioManager.getAudioView().setPlaying(false);
                            mAudioManager.getAudioView().resetAnim();
                            if (v == mAudioManager.getAudioView()) {
                                return;
                            }
                        }
                        if (!v.isPlaying()) {
                            Log.w(TAG, "onImageClick: 开始播放");
                            mAudioManager.playSound(v, map.get("video"), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    v.resetAnim();
                                }
                            }, new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    LogUtil.w("语音tag：   " + v.getTag());
                                    v.setTime(mAudioManager.mMediaplayer.getDuration() / 1000);
                                    Time.set((Integer) v.getTag(), mAudioManager.mMediaplayer.getDuration() / 1000 + "");
                                }
                            });

                        } else {
                            Log.w(TAG, "onImageClick: 停止播放");
                            mAudioManager.release();
                        }

                    }
                });
            } else if (!"".equals(map.get("video")) && map.get("video").endsWith("mp4")) {
                ViewGroup.LayoutParams vl=holder.headlayout.getLayoutParams();
                vl.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.headlayout.setLayoutParams(vl);
                holder.msg.setVisibility(View.GONE);
                holder.player.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);
                holder.audio.setVisibility(View.GONE);
                holder.player.setUp(map.get("video"), "");
            }

            return view;
        }


        class Holder {
            AvatarImageView head;
            ImageView tip, image;
            TextView time, name, msg;
            mAudioView audio;
            JCVideoPlayerStandard player;
            RelativeLayout headlayout;
        }
    }

}
