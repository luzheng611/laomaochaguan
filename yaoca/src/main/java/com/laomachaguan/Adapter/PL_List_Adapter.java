package com.laomachaguan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.laomachaguan.Common.HeadToInfo;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LoginUtil;
import com.laomachaguan.Utils.ScaleImageUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.TintUtil;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.Common.ZiXun_Detail;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Administrator on 2016/6/1.
 */
public class PL_List_Adapter extends BaseAdapter {
    public List<HashMap<String, String>> mlist;
    private int screenwidth;
    private Activity context;
    private static final String TAG = "PL_List_Adapter";
    public ArrayList<Boolean> flagList;
    ViewHolder holder = null;
    private SharedPreferences sp;
    private Drawable dianzan, dianzan1;
    private int mainColor;
    private onHuifuListener onHuifu;
    private boolean isHuifu = false;
    private boolean toDetail = false;

    public void setOnHuifuListener(onHuifuListener onhuifu) {
        this.onHuifu = onhuifu;
    }

    //    private int DZposition;
    //    app:layout_widthPercent="10%w"     用户头像列表图片宽高
//    app:layout_heightPercent="10%w"
    public PL_List_Adapter(Activity context) {
        this.mlist = new ArrayList<>();
        this.context = context;
        screenwidth = context.getResources().getDisplayMetrics().widthPixels;
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        flagList = new ArrayList<>();
        dianzan = ContextCompat.getDrawable(context, R.drawable.zan1);
        dianzan1 = ContextCompat.getDrawable(context, R.drawable.zan2);

        dianzan1.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
        dianzan.setBounds(0, 0, DimenUtils.dip2px(context, 15), DimenUtils.dip2px(context, 15));
        mainColor = ContextCompat.getColor(context, R.color.main_color);
    }

    public void setIsHuifu(boolean flag) {
        this.isHuifu = flag;
    }

    public void setToDetail(boolean flag) {
        this.toDetail = flag;
    }

    public void addList(List<HashMap<String, String>> list) {
        this.mlist = list;
        Boolean flag = false;
        for (int i = 0; i < mlist.size(); i++) {
            flagList.add(flag);
        }
    }

    @Override
    public int getCount() {
        return mlist.size() > 0 ? mlist.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.video_pinglun_item, parent, false);
            ImageView head = (ImageView) view.findViewById(R.id.PL_item_Head);
            TextView username = (TextView) view.findViewById(R.id.PL_item_Name);
            final TextView content = (TextView) view.findViewById(R.id.Pl_item_Content);
            TextView time = (TextView) view.findViewById(R.id.PL_item_time);
            TextView DZnum = (TextView) view.findViewById(R.id.Pl_item_DianZan_num);
            holder.huifu = (TextView) view.findViewById(R.id.PL_item_huifu);
            holder.huifu.setText(mApplication.ST("回复"));
            holder.huifuLayout = (LinearLayout) view.findViewById(R.id.pl_huifu_layout);
            holder.content = content;
            holder.head = head;
            holder.DZnum = DZnum;
            holder.time = time;
            holder.userName = username;
            view.setTag(holder);
            holder.head.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url= String .valueOf(view.getTag(R.id.image_url));
                    if(url!=null){
                        ScaleImageUtil.openBigIagmeMode((Activity) context,url);
                    }
                }
            });

        } else {
            holder = (ViewHolder) view.getTag();
        }
        final HashMap<String, String> bean = mlist.get(position);
        if (isHuifu) {
            holder.DZnum.setVisibility(View.GONE);
            holder.huifu.setVisibility(View.GONE);
        } else {
            if (toDetail) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
                        switch (bean.get("type")){
                            case "1":
                                intent.setClass(context, ZiXun_Detail.class);
                                break;
//                            case "4":
//                                intent.setClass(context, XuanzheActivity.class);
//                                break;
//                            case "5":
//                                intent.setClass(context, FundingDetailActivity.class);
//                                break;
//                            case "6":
//                                intent.setClass(context, activity_Detail.class);
//                                break;
                        }
                        intent.putExtra("id", bean.get("ct_id"));
                        context.startActivity(intent);
                    }
                });
            }else{
//                view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, PingLunActivity.class);
//                        intent.putExtra("id", bean.get("id"));
//                        intent.putExtra("content", bean.get("ct_contents"));
//                        intent.putExtra("pet_name", bean.get("pet_name"));
//                        intent.putExtra("user_image", bean.get("user_image"));
//                        intent.putExtra("num", bean.get("ct_ctr"));
//                        intent.putExtra("time", bean.get("ct_time"));
//                        intent.putExtra("realname", bean.get("realname"));
//                        intent.putExtra("isLike", flagList.get(position));
//                        context.startActivity(intent);
//                    }
//                });
            }




        }

        Glide.with(context).load(bean.get("user_image"))
                .bitmapTransform(new CropCircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .override(screenwidth / 10, screenwidth / 10)
                .into(holder.head);
        holder.head.setTag(R.id.image_url,bean.get("user_image"));
        holder.head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HeadToInfo.goToUserDetail(context,bean.get("user_id"));
            }
        });
        if (bean.get("id") == null) {
            holder.DZnum.setTag("");
        } else {
            holder.DZnum.setTag(bean.get("id"));
        }

        holder.userName.setText(bean.get("pet_name"));
        holder.content.setText(mApplication.ST(bean.get("ct_contents")));
        holder.DZnum.setText(bean.get("ct_ctr"));
        if (flagList.get(position)) {
            holder.DZnum.setCompoundDrawables(null, null, dianzan1, null);
            holder.DZnum.setTextColor(mainColor);
        } else {
            holder.DZnum.setCompoundDrawables(null, null, dianzan, null);
            holder.DZnum.setTextColor(Color.GRAY);
        }
        holder.time.setText(mApplication.ST(TimeUtils.getTrueTimeStr(bean.get("ct_time"))));
        holder.DZnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!new LoginUtil().checkLogin(context)) {
                    return;
                }
                ;
                if (v.getTag() == null || v.getTag().toString().equals("")) {
                    Toast.makeText(context,mApplication.ST("快去给其他人点赞吧") , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (flagList.get(position)) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js=new JSONObject();
                            try {
                                js.put("user_id", sp.getString("user_id", ""));
                                js.put("comment_id", v.getTag().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            String data1 = OkHttpUtils.post(Constants.PL_DZ_IP)
                                    .params("key", ApisSeUtil.getKey())
                                    .params("msg", ApisSeUtil.getMsg(js))
                                    .execute().body().string();
                            if (!data1.equals("")) {
                                final View childat = parent.getChildAt(position - ((ListView) parent).getFirstVisiblePosition());
                                final TextView dznum = (TextView) childat.findViewById(R.id.Pl_item_DianZan_num);
                                HashMap<String, String> map = AnalyticalJSON.getHashMap(data1);
                                if (map != null && map.get("code").equals("000")) {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) v).setCompoundDrawables(null, null, TintUtil.tintDrawable(R.drawable.zan2,R.color.main_color,15), null);
                                            if (dznum != null) {
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String d = (Integer.valueOf(dznum.getText().toString()) + 1) + "";
                                                        dznum.setText(d);
                                                        dznum.setTextColor(mainColor);
                                                        HashMap<String, String> map = mlist.get(position);
                                                        map.put("ct_ctr", d);
                                                        flagList.set(position, true);
                                                    }
                                                });

                                            }
                                        }
                                    });
                                } else {
                                    ((Activity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((TextView) v).setCompoundDrawables(null, null, TintUtil.tintDrawable(R.drawable.zan2,R.color.main_color,15), null);

                                            dznum.setTextColor(mainColor);
                                            Toast.makeText(context,mApplication.ST("你已经对该评论点过赞了") , Toast.LENGTH_SHORT).show();
                                            flagList.set(position, true);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        holder.huifuLayout.setTag(bean.get("id"));
        holder.huifu.setTag(holder.huifuLayout);
        holder.huifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout = (LinearLayout) v.getTag();
                onHuifu.onHuifuClicked(layout.getTag().toString(), position, layout, bean.get("pet_name"));
            }
        });
        if (bean.get("reply") != null && !"".equals(bean.get("reply"))) {
            final ArrayList<HashMap<String, String>> replay = AnalyticalJSON.getList_zj(bean.get("reply"));
            if (replay != null) {
                holder.huifuLayout.removeAllViews();
                for (final HashMap<String, String> map : replay) {
                    TextView textView = new TextView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, DimenUtils.dip2px(context, 5), 0, DimenUtils.dip2px(context, 5));
                    textView.setLayoutParams(layoutParams);
                    String pet_name = map.get("pet_name");
                    final String content = mApplication.ST(map.get("ct_contents"));
                    SpannableStringBuilder ssb = new SpannableStringBuilder(pet_name + ":" + content);
                    ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color)), 0, pet_name.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    textView.setText(ssb);
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    holder.huifuLayout.addView(textView);
                    if (holder.huifuLayout.getVisibility() == View.GONE) {
                        holder.huifuLayout.setVisibility(View.VISIBLE);
                    }
                }
                if (replay.size() >= 3) {
                    TextView textView = new TextView(context);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, DimenUtils.dip2px(context, 5), 0, DimenUtils.dip2px(context, 5));
                    textView.setLayoutParams(layoutParams);
                    textView.setText(mApplication.ST("查看更多回复"));
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PingLunActivity.class);
                            intent.putExtra("id", bean.get("id"));
                            intent.putExtra("content", bean.get("ct_contents"));
                            intent.putExtra("pet_name", bean.get("pet_name"));
                            intent.putExtra("user_image", bean.get("user_image"));
                            intent.putExtra("num", bean.get("ct_ctr"));
                            intent.putExtra("time", bean.get("ct_time"));
                            intent.putExtra("realname", bean.get("realname"));
                            intent.putExtra("isLike", flagList.get(position));
                            context.startActivity(intent);
                        }
                    });
                    holder.huifuLayout.addView(textView);
                } else {
                    if (holder.huifuLayout.getChildCount() >= 1) {
                        if (((TextView) holder.huifuLayout.getChildAt(holder.huifuLayout.getChildCount() - 1)).getText().toString().equals(mApplication.ST("查看更多回复"))) {
                            holder.huifuLayout.removeView(holder.huifuLayout.getChildAt(holder.huifuLayout.getChildCount() - 1));
                        }
                    } else {
                        holder.huifuLayout.setVisibility(View.GONE);
                    }

                }
            } else {
                holder.huifuLayout.removeAllViews();
                holder.huifuLayout.setVisibility(View.GONE);
            }
        } else {
            holder.huifuLayout.removeAllViews();
            holder.huifuLayout.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        ImageView head;
        TextView userName;
        TextView content;
        TextView time;
        TextView DZnum;
        TextView huifu;
        LinearLayout huifuLayout;
    }

    public interface onHuifuListener {
        void onHuifuClicked(String id, int p, View v, String name);
    }

}
