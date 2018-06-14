package com.laomachaguan.Adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.laomachaguan.Fragment.MineManager;
import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;
import com.laomachaguan.Utils.mApplication;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/5/10.
 */

public class Mine_GridAdapter extends BaseItemDraggableAdapter<HashMap<String,Object>> {
    public Mine_GridAdapter(List<HashMap<String, Object>> data) {
        super(R.layout.item_mine, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, HashMap<String, Object> map) {

        holder.setText(R.id.text, mApplication.ST(map.get(MineManager.text).toString()));
        Glide.with(mApplication.getInstance()).load(map.get(MineManager.img))
                .override(DimenUtils.dip2px(mApplication.getInstance(),40),DimenUtils.dip2px(mApplication.getInstance(),40))
                .placeholder(R.drawable.invite)
                .fitCenter().into((ImageView) holder.getView(R.id.image));
    }


}
