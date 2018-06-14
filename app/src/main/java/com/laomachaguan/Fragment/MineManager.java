package com.laomachaguan.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.laomachaguan.Adapter.Mine_GridAdapter;
import com.laomachaguan.R;
import com.laomachaguan.Utils.FileUtils;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.PreferenceUtil;
import com.laomachaguan.Utils.Verification;
import com.laomachaguan.Utils.mApplication;
import com.laomachaguan.View.mItemDeraction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class MineManager {
    public static final String img = "mine_image";
    public static final String text = "mine_text";
    public static final String CACHE_NAME = "mine_cache";
    public static final String CACHE_VERSON = "cache_verson";


    //    private ArrayList<Integer> imageList;
//    private ArrayList<String> titles;
    private ArrayList<HashMap<String, Object>> maps;

    private Mine_GridAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;
//    public ArrayList<Integer> getImageList() {
//        return imageList;
//    }
//
//    public ArrayList<String> getTitles() {
//        return titles;
//    }

    public List<HashMap<String, Object>> getMaps() {
        return maps;
    }

    public void notifyDataChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public MineManager(Context context, RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
        this.context = context;
        initMine();

    }
    public  static void clear(Context context){
        File cacheVerson=new File(context.getExternalCacheDir()+ File.separator+CACHE_VERSON);
        if(cacheVerson!=null&&cacheVerson.exists()){
            FileUtils.deleteFile(cacheVerson);
        }
        File cacheMap=new File(context.getExternalCacheDir()+ File.separator+CACHE_NAME);
        if(cacheMap!=null&&cacheMap.exists()){
            FileUtils.deleteFile(cacheMap);
        }

    }
    public void initMine() {
        String status=PreferenceUtil.getUserIncetance(context).getString("role","");
//        isAdmin = PreferenceUtil.getUserIncetance(context).getString("role", "").equals("3") ? true : false;
        maps = FileUtils.getStorageMapEntities(mApplication.getInstance(), CACHE_NAME);

        if (FileUtils.getStorageIntEntities(mApplication.getInstance(), CACHE_VERSON) == null || FileUtils.getStorageIntEntities(mApplication.getInstance(), CACHE_VERSON).get(0) != Verification.getVersionCode(mApplication.getInstance())) {
            maps = null;
        }
        if (maps == null) {
            maps = new ArrayList<>();
            String text[] = null;
            int img[] = null;

            if (status.equals("3")) {//管理员页面
                LogUtil.e("管理员登录");
                text = mApplication.getInstance().getResources().getStringArray(R.array.mine_admin);
                img = new int[]
                        {
                                R.drawable.haoyou_icon,
                                R.drawable.xiaoxi_icon,
                                R.drawable.tongzhi_icon,
                                R.drawable.yonghu_icon,
                                R.drawable.jifen_icon,
                                R.drawable.shoucang_icon,
                                R.drawable.dingdan_icon,
                                R.drawable.bangzhu_icon,
                                R.drawable.setting_icon,
                                R.drawable.qiehuanzhanghao_icon,

                        };
            } else if(status.equals("2")){//代理商页面
                LogUtil.e("代理商登录");
                text = mApplication.getInstance().getResources().getStringArray(R.array.mine_agent);
                img = new int[]
                        {
                                R.drawable.wodedianpu_icon,
                                R.drawable.haoyou_icon,
                                R.drawable.xiaoxi_icon,
                                R.drawable.tongzhi_icon,
                                R.drawable.pingtuan_icon,
                                R.drawable.jifen_icon,
                                R.drawable.shoucang_icon,
                                R.drawable.dingdan_icon,
                                R.drawable.bangzhu_icon,
                                R.drawable.setting_icon,
                                R.drawable.qiehuanzhanghao_icon,


                        };
            } else {//普通用户页面
                LogUtil.e("普通用户登录");
                text = mApplication.getInstance().getResources().getStringArray(R.array.mine_user);
                img = new int[]
                        {
                                R.drawable.woyaokaidian_iocn,
                                R.drawable.haoyou_icon,
                                R.drawable.xiaoxi_icon,
                                R.drawable.tongzhi_icon,
                                R.drawable.pingtuan_icon,
                                R.drawable.jifen_icon,
                                R.drawable.shoucang_icon,
                                R.drawable.dingdan_icon,
                                R.drawable.bangzhu_icon,
                                R.drawable.setting_icon,
                                R.drawable.qiehuanzhanghao_icon,


                        };
            }


            for (int i = 0; i < img.length; i++) {
                LogUtil.e("img::::" + img[i] + "  text::::" + text[i]);
                HashMap<String, Object> map = new HashMap<>();
                map.put(MineManager.img, img[i]);
                map.put(MineManager.text, text[i]);
                maps.add(map);
            }
        }
        LogUtil.e(maps + "  @#@#@#@#@~!!@!~~");
        if (adapter == null) {
            adapter = new Mine_GridAdapter(maps);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
            mItemDeraction mItemDeraction = new mItemDeraction(1, Color.parseColor("#f2f2f2"));
            recyclerView.addItemDecoration(mItemDeraction);
            ItemDragAndSwipeCallback swipeCallback = new ItemDragAndSwipeCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            adapter.enableDragItem(itemTouchHelper);
            adapter.setOnItemDragListener(new OnItemDragListener() {
                @Override
                public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int i) {
                    LogUtil.e("起始：：" + i);
                    LogUtil.e("     onItemDragStart    " + maps);
                }

                @Override
                public void onItemDragMoving(RecyclerView.ViewHolder viewHolder, int i, RecyclerView.ViewHolder viewHolder1, int i1) {
                    LogUtil.e("拖动：：" + i + "     交换:::" + i1);

                }

                @Override
                public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int i) {
                    LogUtil.e("最后位置：：" + i);
                    LogUtil.e("onItemDragEnd      " + maps);
                }
            });

        } else {
            adapter.setNewData(maps);
        }
        recyclerView.setAdapter(adapter);

    }

    public void setOnitemClickListener(BaseQuickAdapter.OnRecyclerViewItemClickListener listener) {
        adapter.setOnRecyclerViewItemClickListener(listener);
    }

    public void saveMySetting() {

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(Verification.getVersionCode(context));
        FileUtils.saveStorage2SDCard(mApplication.getInstance(), arrayList, CACHE_VERSON);
        FileUtils.saveStorage2SDCard(mApplication.getInstance(), maps, CACHE_NAME);
    }

    public void chageRedPoint() {
        for (int i = 0; i < maps.size(); i++) {
            if (maps.get(i).get(text).equals("会员中心")) {
                recyclerView.getChildAt(i).findViewById(R.id.badge).setVisibility(View.VISIBLE);
                break;
            }

        }

    }


}
