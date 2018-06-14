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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.laomachaguan.R;

import java.io.File;
import java.util.concurrent.TimeUnit;


public class ViewPagerFragment extends Fragment {

    private static final String BUNDLE_ASSET = "asset";

    private String asset;
    private ProgressBar progressBar;
    private String path;
    private int positon;
    public ViewPagerFragment(){

    }


    public void setAsset(String asset) {
        this.asset = asset;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.view_pager_page, container, false);

        positon=getArguments().getInt("i");
        Log.w(TAG, "onCreateView: 起始路径-=-=-="+asset +"页码：：："+positon);
//        if (savedInstanceState != null) {
//            if (asset == null && savedInstanceState.containsKey(BUNDLE_ASSET)) {
//                asset = savedInstanceState.getString(BUNDLE_ASSET);
//            }
//        }
        final SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) rootView.findViewById(R.id.zoom_imageView);
        if (asset != null) {

            progressBar = (ProgressBar) rootView.findViewById(R.id.zoom_progressBar);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (asset.startsWith("http")) {
                        try {
                            path=Glide.with(getActivity()).load(asset).downloadOnly(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                                    .get(10, TimeUnit.SECONDS).getPath();
                            Log.w(TAG, "onCreateView: 下载之后的路径" + path);
                            asset=path;
                            if(getActivity()!=null&&null!=((ViewPagerActivity) getActivity()).pathList){
                            ((ViewPagerActivity) getActivity()).pathList.set(positon,asset);}
                            Log.e(TAG, "run: 第"+positon+"页图片大小"+new File(asset).length()/1000+"kb");
                        } catch (Exception e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "加载超时", Toast.LENGTH_SHORT).show();
                                    imageView.setImage(ImageSource.resource(R.drawable.place_holder2));
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                    if(getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.w(TAG, "run: " );
                                imageView.setImage(ImageSource.uri(asset));
                                progressBar.setVisibility(View.GONE);
                            }catch (Exception e){
                                 Glide.with(getActivity()).load(asset).asBitmap().into(new ViewTarget<SubsamplingScaleImageView, Bitmap>(imageView) {
                                     @Override
                                     public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                         imageView.setImage(ImageSource.bitmap(resource));
                                     }
                                 });
                            }

                        }
                    });
                }
            }).start();


        }

        return rootView;
    }

    private static final String TAG = "ViewPagerFragment";

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        View rootView = getView();
//        Log.w(TAG, "onSaveInstanceState: 保存路径" );
//        if (rootView != null) {
//            outState.putString(BUNDLE_ASSET, asset);
//        }
//    }

}
