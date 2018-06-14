package com.laomachaguan.TouGao.Photo;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.laomachaguan.R;
import com.laomachaguan.Utils.DimenUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 */
public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder> {

    private ListView mListDir;
    private List_Dir_Adapter adapter;
    private int checkedPositin=0;
    public ListImageDirPopupWindow(int width, int height,
                                   List<ImageFloder> datas, View convertView)
    {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews()
    {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        adapter=new List_Dir_Adapter(context,mDatas,checkedPositin);
//        mListDir.setAdapter(new CommonAdapter<ImageFloder>(context, mDatas,
//                R.layout.list_dir_item)
//        {
//            @Override
//            public void convert(ViewHolder helper, ImageFloder item)
//            {
//                helper.setText(R.id.id_dir_item_name, item.getName());
//                helper.setImageByUrl(R.id.id_dir_item_image,
//                        item.getFirstImagePath());
//                helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
//            }
//        });
        mListDir.setAdapter(adapter);

    }

    public interface OnImageDirSelected
    {
        void selected(ImageFloder floder, int position);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
    {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents()
    {
        mListDir.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                if(checkedPositin==position){
                    return;
                }else{
                    parent.getAdapter().getView(checkedPositin,view,parent).findViewById(R.id.img_list_dir_item_check).setVisibility(View.GONE);
                    checkedPositin=position;
                    adapter.setChecked(checkedPositin);
                }
                ImageView imageView= (ImageView) view.findViewById(R.id.img_list_dir_item_check);
                Glide.with(context).load(R.drawable.icon_data_select).override(DimenUtils.dip2px(context,25),DimenUtils.dip2px(context,25))
                        .into(imageView);

                if (mImageDirSelected != null)
                {
                    mImageDirSelected.selected(mDatas.get(position),position);
                }
            }
        });
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params)
    {
        // TODO Auto-generated method stub
    }
}
