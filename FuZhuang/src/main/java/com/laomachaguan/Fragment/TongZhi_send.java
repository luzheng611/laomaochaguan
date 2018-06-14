package com.laomachaguan.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.TimeUtils;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/18.
 */
public class TongZhi_send extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private ImageView tip;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private SharedPreferences sp;
    private mAdapter SCadapter;
    private ArrayList<HashMap<String, String>> list;
    private static final String TAG = "TongZhi_get";
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.tongzhi_fragment,container,false);
        initView();
        getData();
        return view;
    }



    /**
     * 获取数据
     */
    private void getData() {
        if (sp == null) {

        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);

                        js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    String data = OkHttpUtils.post(Constants.TongzhiList_Ip).tag(TAG)
                            .params("key",m.K())
                            .params("msg",m.M())
                            .execute().body().string();
                    if (!data.equals("")) {
                        Log.w(TAG, "run: news_+-=-getData-=-=-=" + data);
                        HashMap<String,String> map=AnalyticalJSON.getHashMap(data);
                        if(map!=null){
                            list = AnalyticalJSON.getList_zj(map.get("friendqq"));
                        }
                    }
                    tip.post(new Runnable() {
                        @Override
                        public void run() {
                            if (SCadapter == null) {
                                SCadapter = new mAdapter(getActivity(),list);
                            }
                            if (list != null) {
                                if (list.size() == 0) {
                                    if (tip != null && tip.getVisibility() != View.VISIBLE) {
                                        ToastUtil.showToastShort("您还没有发起过好友请求");
                                        tip.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    if (SCadapter != null && listView != null && tip != null) {
                                        SCadapter.addList(list);
                                        listView.setAdapter(SCadapter);
                                    }
                                    tip.setVisibility(View.GONE);
                                }
                            }
                            if(swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(false);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shoucang_tip:
                onRefresh();
                break;
        }
    }

    public static class mAdapter extends BaseAdapter {

        public ArrayList<HashMap<String, String>> list;
        private Context context;
        private Drawable d0,d1,d2;
        public mAdapter(Context context, ArrayList<HashMap<String, String>> list) {
            super();
            this.context = context;
            this.list = list;
            d0= ContextCompat.getDrawable(context,R.drawable.guanzhu);
            d0.setBounds(0,0, DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25));
            d1= ContextCompat.getDrawable(context,R.drawable.guanzhu_gray);
            d1.setBounds(0,0,DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25));
            d2= ContextCompat.getDrawable(context,R.drawable.delete);
            d2.setBounds(0,0,DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25));
        }

        public void addList(ArrayList<HashMap<String, String>> list) {
            this.list = list;
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
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            viewHolder holder = null;
            final HashMap<String ,String > map=list.get(position);
            if (view == null) {
                holder = new viewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.tongzhi_item, parent, false);
                holder.head = (AvatarImageView) view.findViewById(R.id.tongzhi_item_head);
                holder.name = (TextView) view.findViewById(R.id.tongzhi_item_name);
                holder.agree = (TextView) view.findViewById(R.id.tongzhi_tongyi);
                holder.diny = (TextView) view.findViewById(R.id.tongzhi_diny);
                holder.tip= (ImageView) view.findViewById(R.id.tongzhi_head_tip);
                holder.time= (TextView) view.findViewById(R.id.tongzhi_item_time);
                holder.msg= (TextView) view.findViewById(R.id.tongzhi_item_msg);
                holder.delete= (TextView) view.findViewById(R.id.delete);
                holder.job= (TextView) view.findViewById(R.id.tongzhi_item_job);
                view.setTag(holder);

            } else {
                holder = (viewHolder) view.getTag();
            }

            Glide.with(context).load(map.get("user_image")).override(DimenUtils.dip2px(context,60),DimenUtils.dip2px(context,60)).into(holder.head);
            holder.name.setText(map.get("pet_name"));
            holder.name.setTag(map.get("id"));
            holder.msg.setText(map.get("msg"));
            holder.time.setText(TimeUtils.getTrueTimeStr(map.get("time")));
            holder.agree.setVisibility(View.VISIBLE);
            holder.diny.setVisibility(View.VISIBLE);
                if(map.get("status").equals("1")){
                    holder.agree.setVisibility(View.GONE);
                    holder.diny.setText("对方未查看");
                    holder.diny.setTextColor(Color.GRAY);
                    holder.diny.setCompoundDrawables(null,d1,null,null);
                }else if(map.get("status").equals("2")){
                    holder.diny.setVisibility(View.GONE);
                    holder.agree.setText("对方已同意");
                }else{
                    holder.agree.setVisibility(View.GONE);
                    holder.diny.setText("对方已拒绝");
                    holder.diny.setBackgroundColor(Color.RED);
                    holder.diny.setCompoundDrawables(null,d2,null,null);
                }
//            else if(PreferenceUtil.getUserIncetance(context).getString("user_id","").equals(map.get("concern_id"))){// TODO: 2016/12/23 收到通知
//
//                if(map.get("status").equals("2")){
//                    holder.agree.setText("已同意");
//                    holder.diny.setVisibility(View.GONE);
//                    holder.agree.setCompoundDrawables(null,d0,null,null);
//                }else if(map.get("status").equals("1")){
//                    holder.diny.setCompoundDrawables(null,d2,null,null);
//                    holder.agree.setCompoundDrawables(null,d0,null,null);
//                    holder.agree.setTag(holder.diny);
//                    holder.diny.setTag(holder.agree);
//                    holder.agree.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(final View v) {
//                            OkHttpUtils.post(Constants.little_agree_diny_IP).params("key",Constants.safeKey)
//                                    .params("concern_id",map.get("user_id")).params("user_id",map.get("concern_id"))
//                                    .params("id",map.get("id"))
//                                    .params("type","1").execute(new AbsCallback<Object>() {
//                                @Override
//                                public Object parseNetworkResponse(Response response) throws Exception {
//                                    return null;
//                                }
//
//                                @Override
//                                public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
//                                    try {
//                                        String data=response.body().string();
//                                        if(!data.equals("")){
//                                            HashMap<String,String >map=AnalyticalJSON.getHashMap(data);
//                                            if(map!=null){
//                                                if("000".equals(map.get("code"))){
//                                                    Toast.makeText(context, "已同意对方请求", Toast.LENGTH_SHORT).show();
//                                                    v.setVisibility(View.GONE);
//                                                    ((TextView) v.getTag()).setText("已同意");
//                                                    ((TextView) v.getTag()).setCompoundDrawables(null,d0,null,null);
//                                                    ((TextView) v.getTag()).setClickable(false);
//                                                    Intent intent=new Intent("main");
//                                                    context.sendBroadcast(intent);
//                                                }
//                                            }else{
//                                                Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }else{
//                                            Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } catch (Exception e) {
//                                        Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    });
//                    holder.diny.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(final View v) {
//                            OkHttpUtils.post(Constants.little_agree_diny_IP).params("key",Constants.safeKey)
//                                    .params("concern_id",map.get("concern_id")).params("user_id",map.get("user_id"))
//                                    .params("type","1").execute(new AbsCallback<Object>() {
//                                @Override
//                                public Object parseNetworkResponse(Response response) throws Exception {
//                                    return null;
//                                }
//
//                                @Override
//                                public void onResponse(boolean isFromCache, Object o, Request request, @Nullable Response response) {
//                                    try {
//                                        String data=response.body().string();
//                                        if(!data.equals("")){
//                                            HashMap<String,String >map=AnalyticalJSON.getHashMap(data);
//                                            if(map!=null){
//                                                if("001".equals(map.get("code"))){
//                                                    Toast.makeText(context, "已拒绝对方请求", Toast.LENGTH_SHORT).show();
//                                                    ((TextView) v).setText("已拒绝");
//                                                    v.setBackgroundColor(Color.RED);
//                                                    ((TextView) v.getTag()).setVisibility(View.GONE);
//                                                }
//                                            }else{
//                                                Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }else{
//                                            Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        }
//                                    } catch (Exception e) {
//                                        Toast.makeText(context, "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    });
//                }else{
//                    holder.diny.setText("已拒绝");
//                    holder.diny.setCompoundDrawables(null,d2,null,null);
//                    holder.agree.setVisibility(View.GONE);
//                }
//
//            }
            holder.delete.setTag(position);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.w(TAG, "onClick: 通知的id："+v );
                    final int pos= (int) v.getTag();
                    JSONObject js = new JSONObject();
                    try {
                        js.put("m_id", Constants.M_id);
                        js.put("id",map.get("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.TongzhiDelete_Ip)
                            .params("key",m.K())
                            .params("msg",m.M()).execute(new AbsCallback<Object>() {
                        @Override
                        public Object parseNetworkResponse(Response response) throws Exception {
                            return null;
                        }

                        @Override
                        public void onSuccess(Object o, Call call, Response response) {
                            try {
                                String data=response.body().string();
                                if(!data.equals("")){
                                    HashMap<String ,String >map=AnalyticalJSON.getHashMap(data);
                                    if(map!=null&&"000".equals(map.get("code"))){
                                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                        list.remove(pos);
                                        notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(context, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }


                    });
                }
            });


            return view;
        }

        static class viewHolder {
            AvatarImageView head;
            ImageView tip;
            TextView name,agree,diny,time,msg,job,delete;


        }
    }
    private void initView() {
        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        tip = (ImageView)view. findViewById(R.id.shoucang_tip);
        tip.setOnClickListener(this);
        swipeRefreshLayout = (SwipeRefreshLayout)view. findViewById(R.id.swip);
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);
        swipeRefreshLayout.setOnRefreshListener(this);
        list = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.shoucang_listview);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity()).setItems(new String[]{"移除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String v= (String) view.findViewById(R.id.tongzhi_item_name).getTag();
                        Log.w(TAG, "onClick: 通知的id："+v );
                        JSONObject js = new JSONObject();
                        try {
                            js.put("m_id", Constants.M_id);
                            js.put("id",v);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ApisSeUtil.M m=ApisSeUtil.i(js);
                        OkHttpUtils.post(Constants.TongzhiDelete_Ip)
                                .params("key",m.K())
                                .params("msg",m.M()).execute(new AbsCallback<Object>() {
                            @Override
                            public Object parseNetworkResponse(Response response) throws Exception {
                                return null;
                            }

                            @Override
                            public void onSuccess(Object o, Call call, Response response) {
                                try {
                                    String data=response.body().string();
                                    if(!data.equals("")){
                                        HashMap<String ,String >map=AnalyticalJSON.getHashMap(data);
                                        if(map!=null&&"000".equals(map.get("code"))){
                                            Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                            SCadapter.list.remove(position);
                                            SCadapter.notifyDataSetChanged();
                                        }else{
                                            Toast.makeText(getActivity(), "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        Toast.makeText(getActivity(), "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }


                        });
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }



    @Override
    public void onRefresh() {
        tip.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });
    }
}
