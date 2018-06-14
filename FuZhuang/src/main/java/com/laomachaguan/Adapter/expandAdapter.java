package com.laomachaguan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.laomachaguan.Common.User_Detail2;
import com.laomachaguan.Fragment.Ask_Detail;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.PreferenceUtil;
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
 * Created by Administrator on 2016/12/28.
 */
public class expandAdapter extends BaseExpandableListAdapter {
    public static  final  int Fans=1;
    public static  final  int Focus=2;

    public ArrayList<HashMap<String, String>> list;
    public ArrayList<HashMap<String, String>> groups;
    private Activity context;
    private static final String TAG = "expandAdapter";
    private Drawable sixin,ziliao;
    private int type;
    private int currentPos=-1;

    public expandAdapter(Activity context, ArrayList<HashMap<String, String>> list, int type) {
        super();
        // 初始化数据
        this.context = context;
        this.list = list;
        this.groups=list;
//        dongtai= ContextCompat.getDrawable(context, R.drawable.dongtai);
        sixin= ContextCompat.getDrawable(context,R.drawable.sixin1 );
        ziliao= ContextCompat.getDrawable(context,R.drawable.ziliao);
//        dongtai.setBounds(0,0, DimenUtils.dip2px(context,20),DimenUtils.dip2px(context,20));
        sixin.setBounds(0,0,DimenUtils.dip2px(context,20),DimenUtils.dip2px(context,20));
        ziliao.setBounds(0,0,DimenUtils.dip2px(context,20),DimenUtils.dip2px(context,20));
        this.type=type;

    }
    public  void addList(ArrayList<HashMap<String ,String >>l){
        this.list=l;
    }


    @Override
    public int getGroupCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;

    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = list.get(groupPosition).get("id");
        return key;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int position, final boolean isExpanded, View view, final ViewGroup parent) {

//        if(type==Focus){
//            if(view==null){
//                view=LayoutInflater.from(context).inflate(R.layout.user_list_item2,null);
//            }
//            final HashMap<String ,String >map=list.get(position);
//            AvatarImageView head = (AvatarImageView) view.findViewById(R.id.user_list_item_head);
//            TextView name = (TextView) view.findViewById(R.id.user_list_item_name);
//            TextView sign = (TextView) view.findViewById(R.id.user_list_item_sign);
//            TextView job = (TextView) view.findViewById(R.id.user_list_item_job);
//            TextView beizu = (TextView) view.findViewById(R.id.beizhu);
//            TextView guanzhu = (TextView) view.findViewById(R.id.user_list_item_guanzhu);
//            ImageView tip = (ImageView) view.findViewById(R.id.user_list_item_tip);
//            ImageView focus = (ImageView) view.findViewById(R.id.user_focus);
//            final LinearLayout content= (LinearLayout) view.findViewById(R.id.content);
//            TextView delete= (TextView) view.findViewById(R.id.delete);
//            content.setTag(position);
//            content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    LogUtil.w("当前位置："+currentPos);
//                    int pos= (int) v.getTag();
//                    if(currentPos!=-1){
//                        if(((ExpandableListView) parent).isGroupExpanded(currentPos))((ExpandableListView) parent).collapseGroup(currentPos);
//                    }else if(currentPos==pos){
//                        if(((ExpandableListView) parent).isGroupExpanded(currentPos))((ExpandableListView) parent).collapseGroup(currentPos);
//                        if(!(((ExpandableListView) parent).isGroupExpanded(currentPos)))((ExpandableListView) parent).expandGroup(currentPos,true);
//                        return;
//                    }
//                    if(!(((ExpandableListView) parent).isGroupExpanded(pos)))((ExpandableListView) parent).expandGroup(pos,true);
//                    currentPos=pos;
//                }
//            });
////            if(position==0){
////                ((ExpandableListView) parent).expandGroup(position,true);
////                currentPos=position;
////            }
//            Glide.with(context).load(list.get(position).get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 60)
//                    , DimenUtils.dip2px(mApplication.getInstance(), 60)).into(head);
//            name.setText(list.get(position).get("pet_name"));
//            name.setTag(list.get(position).get("id"));
//            LogUtil.w("备注：：：：："+map.get("bz_name"));
//            if((map.get("bz_name")!=null&&!map.get("bz_name").equals("")&&!map.get("bz_name").equals("null"))){
//                beizu.setText("备注:"+map.get("bz_name"));
//            }else{
//                beizu.setText("");
//            }
//            sign.setText(list.get(position).get("signature").equals("") ? "这个人很懒，还没有签名噢" : list.get(position).get("signature"));
//            if ("1".equals(map.get("realname"))) {
//                job.setBackgroundResource(R.drawable.button1_shape_enabled);
//                job.setText("未认证");
//                tip.setVisibility(View.GONE);
//            } else {
//                job.setText(map.get("job"));
//                tip.setVisibility(View.VISIBLE);
//            }
//            if(null==map.get("bz_focus")||"".equals(map.get("bz_focus"))||map.get("bz_focus").equals("null")){
//                focus.setSelected(false);
//            }else{
//                focus.setSelected(true);
//            }
//            focus.setTag(position);
//            focus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    ConfirmDialog.get(context,v.isSelected()?"是否取消特别关注?":"是否添加特别关注?",
//                            v.isSelected()?"取消特别关注后您将无法再首页动态获取他(她)的动态":"添加关注后您首页将加载他(她)的动态","",false).setOnCommitListener(v.isSelected()?"确认取消":"确认添加", new ConfirmDialog.OnmDialogCommitListener() {
//                        @Override
//                        public void onCommit(AlertDialog dialog, View parent, View view, String msg) {
//                                dialog.dismiss();
//                                AddDeleteFocus(map.get("id"),v);// TODO: 2017/1/13 添加取消特别关注
//                        }
//                    }).setOnCancleListener("点错了", new ConfirmDialog.OnmDialogCancleListener() {
//                        @Override
//                        public void onCancle(AlertDialog dialog, View v) {
//                                dialog.dismiss();
//                        }
//                    }).show();
//
//                }
//            });
//            delete.setText("取消关注");
//            guanzhu.setTextColor(Color.GRAY);
//            guanzhu.setVisibility(View.GONE);
//            delete.setTag(position);
//            delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    AlertDialog.Builder b=new AlertDialog.Builder(context).setPositiveButton("确认取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(final DialogInterface dialog, int which) {
//                            ProgressUtil.show(context,"","正在提交操作，请稍等");
//                            final int pos= (int) v.getTag();
//                            JSONObject js=new JSONObject();
//
//                            try {  js.put("concern_id", map.get("id"));
//                                js.put("type", "1");
//                                js.put("m_id", Constants.M_id);
//                                js.put("pet_name", PreferenceUtil.getUserIncetance(context).getString("pet_name", ""));
//                                js.put("user_id", PreferenceUtil.getUserIncetance(context).getString("user_id", ""));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            ApisSeUtil.M m=ApisSeUtil.i(js);
//                            OkHttpUtils.post(Constants.little_fans_focus_delete__IP)
//                                    .params("key",m.K())
//                                    .params("msg",m.M())
//                                    .execute(new AbsCallback<Object>() {
//                                        @Override
//                                        public Object parseNetworkResponse(Response response) throws Exception {
//                                            return null;
//                                        }
//
//                                        @Override
//                                        public void onSuccess(Object o, Call call, Response response) {
//                                            try {
//                                                String data=response.body().string();
//                                                if(!data.isEmpty()){
//                                                    HashMap<String ,String >map= AnalyticalJSON.getHashMap(data);
//                                                    if("000".equals(map.get("code"))){
//                                                        ((Activity) context).runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                list.remove(pos);
//                                                                notifyDataSetChanged();
//                                                                dialog.dismiss();
//                                                                ProgressUtil.dismiss();
//                                                                Intent intent=new Intent("main");
//                                                                context.sendBroadcast(intent);
//
//                                                            }
//                                                        });
//                                                    }else{
//                                                        ((Activity) context).runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                Toast.makeText(context, "删除操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                                                ProgressUtil.dismiss();
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                            } catch (Exception e) {
//                                                ((Activity) context).runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Toast.makeText(context, "删除操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                                        ProgressUtil.dismiss();
//                                                    }
//                                                });
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//
//                                    });
//                        }
//                    }).setNegativeButton("点错了", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    }).setTitle("确认取消关注该用户吗？");
//                    b.create().show();
//
//
//                }
//            });
//        }else if(type==Fans){
            if(view==null){
                view=LayoutInflater.from(context).inflate(R.layout.user_list_item2,null);
            }
            final HashMap<String ,String >map=list.get(position);
            AvatarImageView head = (AvatarImageView) view.findViewById(R.id.user_list_item_head);
            TextView name = (TextView) view.findViewById(R.id.user_list_item_name);
            TextView sign = (TextView) view.findViewById(R.id.user_list_item_sign);
            TextView job = (TextView) view.findViewById(R.id.user_list_item_job);
            TextView beizu = (TextView) view.findViewById(R.id.beizhu);
            TextView guanzhu = (TextView) view.findViewById(R.id.user_list_item_guanzhu);
            ImageView tip = (ImageView) view.findViewById(R.id.user_list_item_tip);
            ImageView focus = (ImageView) view.findViewById(R.id.user_focus);
            final LinearLayout content= (LinearLayout) view.findViewById(R.id.content);
            TextView delete= (TextView) view.findViewById(R.id.delete);
            content.setTag(position);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.w("当前位置："+currentPos);
                    if(currentPos!=-1){
                        if(((ExpandableListView) parent).isGroupExpanded(currentPos))
                        ((ExpandableListView) parent).collapseGroup(currentPos);
                    }
                    int pos= (int) v.getTag();
                    if(!((ExpandableListView) parent).isGroupExpanded(currentPos))((ExpandableListView) parent).expandGroup(pos,true);
                    currentPos=pos;
                }
            });

            Glide.with(context).load(list.get(position).get("user_image")).override(DimenUtils.dip2px(mApplication.getInstance(), 60)
                    , DimenUtils.dip2px(mApplication.getInstance(), 60)).into(head);
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, User_Detail2.class);
                intent.putExtra("id",map.get("id"));
                intent.putExtra("add",false);
                context.startActivity(intent);
            }
        });
            name.setText(list.get(position).get("pet_name"));
            name.setTag(list.get(position).get("id"));


//            sign.setText(list.get(position).get("signature").equals("") ? "暂无个性签名" : list.get(position).get("signature"));





           delete.setText("删除好友");
            delete.setTag(position);
           delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int pos= (int) v.getTag();
                    JSONObject js=new JSONObject();

                    try {
                        js.put("user_id",PreferenceUtil.getUserIncetance(context).getString("user_id",""));
                        js.put("user_friend",map.get("id"));
//                        js.put("type","2");
                        js.put("m_id",Constants.M_id);
//                        js.put("pet_name",PreferenceUtil.getUserIncetance(context).getString("pet_name", ""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LogUtil.e("删除好友：："+js);
                    ApisSeUtil.M m=ApisSeUtil.i(js);
                    OkHttpUtils.post(Constants.FridendDelete)
                            .params("key",m.K())
                            .params("msg",m.M())

                            .execute(new AbsCallback<Object>() {
                                @Override
                                public Object parseNetworkResponse(Response response) throws Exception {
                                    return null;
                                }

                                @Override
                                public void onSuccess(Object o, Call call, Response response) {
                                    try {
                                        String data=response.body().string();
                                        if(!data.isEmpty()){
                                            HashMap<String ,String >map=AnalyticalJSON.getHashMap(data);
                                            if("000".equals(map.get("code"))){
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showToastShort("删除好友成功");
                                                        list.remove(pos);
                                                        notifyDataSetChanged();

                                                    }
                                                });
                                            }else{
                                                ((Activity) context).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(context, "删除操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(context, "删除操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                }


                            });

                }
            });
//        }



        return view;
    }

    @Override
    public View getChildView(int possition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String key =list.get(possition).get("id");
        final String userName=list.get(possition).get("pet_name");
        final String title=list.get(possition).get("user_title");
        final String realname=list.get(possition).get("realname");
        final String bzName=list.get(possition).get("bz_name");
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_child, null);
        }

//        TextView dongtai = (TextView) convertView
//                .findViewById(R.id.dongtai);
//        dongtai.setCompoundDrawables(null,this.dongtai,null,null);
        TextView sixin = (TextView) convertView
                .findViewById(R.id.sixin);
        sixin.setCompoundDrawables(null,this.sixin,null,null);
        TextView ziliao = (TextView) convertView
                .findViewById(R.id.kanzixliao);
        ziliao.setCompoundDrawables(null,this.ziliao,null,null);
//        dongtai.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent=new Intent(context, ZiXun_List.class);
////                intent.putExtra("id",key);
////                intent.putExtra("name",userName);
////                intent.putExtra("title",title);
////                context.startActivity(intent);
//            }
//        });
        sixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Ask_Detail.class);
                intent.putExtra("id",key);
                context.startActivity(intent);
            }
        });
        ziliao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, User_Detail2.class);
                intent.putExtra("id",key);
                intent.putExtra("add",false);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


//    public void AddDeleteFocus(String concern_id, final View v){
//        int pos= (int) v.getTag();
//        final boolean isSelected=v.isSelected();
//        JSONObject js = new JSONObject();
//        LogUtil.e("关注的ID：："+concern_id);
//        try {
//            js.put("m_id", Constants.M_id);
//            js.put("concern_id",concern_id);
//            js.put("type",isSelected?"2":"1");
//            js.put("user_id", PreferenceUtil.getUserIncetance(mApplication.getInstance()).getString("user_id", ""));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ApisSeUtil.M m=ApisSeUtil.i(js);
//        LogUtil.e("js:::;;"+js);
//        OkHttpUtils.post(Constants.little_tebieGuanzhu__IP).tag(context)
//                .params("key",m.K())
//                .params("msg",m.M())
//                .execute(new AbsCallback<Object>() {
//                    @Override
//                    public Object parseNetworkResponse(Response response) throws Exception {
//                        return null;
//                    }
//
//                    @Override
//                    public void onSuccess(Object o, Call call, Response response) {
//                        if(response!=null){
//                            try {
//                                String data=response.body().string();
//                                if(!"".equals(data)){
//                                    HashMap<String ,String >map=AnalyticalJSON.getHashMap(data);
//                                    if(map!=null&&"000".equals(map.get("code"))){
//                                        context.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if(isSelected){
//                                                    v.setSelected(false);
//                                                    Toast.makeText(context, "已取消特别关注", Toast.LENGTH_SHORT).show();
//                                                } else{
//                                                    v.setSelected(true);
//                                                    Toast.makeText(context, "已添加特别关注", Toast.LENGTH_SHORT).show();
//                                                }
//                                                Intent intent=new Intent("bz");
//                                                context.sendBroadcast(intent);
//                                            }
//                                        });
//
//                                    }else{
//                                        context.runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(context, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }
//                            } catch (Exception e) {
//                                context.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(context, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//    }
}
