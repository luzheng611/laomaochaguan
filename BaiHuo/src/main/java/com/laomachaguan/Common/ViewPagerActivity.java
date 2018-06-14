/*
Copyright 2014 David Morrissey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.laomachaguan.Common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laomachaguan.R;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewPagerActivity extends FragmentActivity implements View.OnLongClickListener, View.OnClickListener {

    private static final String TAG = "ViewPagerActivity";
    private ImageView back;
    private TextView num, download;
    private ViewPager page;
    public static ArrayList<String> pathList;
    public  int currentPosition=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);
        final PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        page = (ViewPager) findViewById(R.id.zoom_pager);
        back = (ImageView) findViewById(R.id.zoom_back);
        back.setOnClickListener(this);
        pathList=new ArrayList<>();
        String image=getIntent().getStringExtra("url");
        if(image!=null&&!image.equals("")){
            try {
                JSONArray j=new JSONArray(image);
                for (int i=0;i<j.length();i++){
                    pathList.add(j.getJSONObject(i).getString("url"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ArrayList l=getIntent().getStringArrayListExtra("array");
        if(l!=null){
            if(l.contains("add")){
                l.remove("add");
            }
           currentPosition=getIntent().getIntExtra("position",-1);
            pathList=l;
        }
        num = (TextView) findViewById(R.id.zoom_num);
        if(currentPosition!=-1)num.setText((currentPosition+1)+"/"+pathList.size());
        download = (TextView) findViewById(R.id.zoom_download);
        download.setOnClickListener(this);
        page.setAdapter(pagerAdapter);
        page.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (num != null && pathList != null)
                    num.setText((position+1) + "/" + pathList.size());
                if(!pathList.get(position).startsWith("http")){
                    download.setText("原图("+new File(pathList.get(position)).length()/1000+"kb)");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(currentPosition!=-1)page.setCurrentItem(currentPosition);
    }

    @Override
    public void onBackPressed() {
        if (page.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            page.setCurrentItem(page.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zoom_back:
                finish();
                break;
            case R.id.zoom_download:
                int p=page.getCurrentItem();
                String path=pathList.get(p);
                if(path.startsWith("http")){
                    Glide.with(this).load(path).downloadOnly(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                            try {
                                String path= Constants.ExternalCacheDir+"download/";
                                File f=new File(path);
                                if(!f.exists()){
                                    f.mkdirs();
                                }
                                File f0=new File(f,System.currentTimeMillis()/1000+".jpg");
                                FileUtils.copyFileUsingFileChannels(resource,f0);
                                Toast.makeText(ViewPagerActivity.this, "文件已下载至"+path+"，本次下载使用"+(f0.length())/1000+"kb流量", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    try {
                        String path1= Constants.ExternalCacheDir+"download/";
                        File f=new File(path1);
                        if(!f.exists()){
                            f.mkdirs();
                        }
                        File f0=new File(f,System.currentTimeMillis()/1000+".jpg");
                        File file1=new File(path);
                        FileUtils.copyFileUsingFileChannels(file1,f0);
                        Toast.makeText(ViewPagerActivity.this, "文件已下载至"+path1+"，本次下载使用"+(f0.length())/1000+"kb流量", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ViewPagerFragment fragment = new ViewPagerFragment();
            Bundle b=new Bundle();
            b.putInt("i",position);
            fragment.setArguments(b);
            fragment.setAsset(pathList.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return pathList == null ? 0 : pathList.size();
        }
    }


}
