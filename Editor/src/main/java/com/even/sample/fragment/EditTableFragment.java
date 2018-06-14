package com.even.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.even.sample.R;
import com.even.sample.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Edit Table Fragment
 * Created by even.wu on 10/8/17.
 */

public class EditTableFragment extends Fragment {
    @BindView(R2.id.et_rows) EditText etRows;
    @BindView(R2.id.et_cols) EditText etCols;

    private OnTableListener mOnTableListener;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_table, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R2.id.iv_back) void onClickBack() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    @OnClick(R2.id.btn_ok) void onClickOK() {
        if (mOnTableListener != null) {
            mOnTableListener.onTableOK(Integer.valueOf(etRows.getText().toString()),
                Integer.valueOf(etCols.getText().toString()));
            onClickBack();
        }
    }

    public void setOnTableListener(OnTableListener mOnTableListener) {
        this.mOnTableListener = mOnTableListener;
    }

    public interface OnTableListener {
        void onTableOK(int rows, int cols);
    }
}
