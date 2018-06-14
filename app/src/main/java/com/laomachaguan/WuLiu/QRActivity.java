package com.laomachaguan.WuLiu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.laomachaguan.R;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.StatusBarCompat;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * 作者：因陀罗网 on 2017/8/18 19:04
 * 公司：成都因陀罗网络科技有限公司
 */

public class QRActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private ZXingView zXingView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        setContentView(R.layout.qr_activity);
        zXingView= (ZXingView) findViewById(R.id.zxingview);
        zXingView.setDelegate(this);
        zXingView.changeToScanBarcodeStyle();//切换到条形码扫描
        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void vibrator() {
        //获取系统震动服务
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);

    } @Override
    protected void onStop() {
        zXingView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        zXingView.startCamera();
        zXingView.showScanRect();
        zXingView.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
//扫描成功后调用震动器
        vibrator();
        //显示扫描结果
        LogUtil.e(result);
        Intent intent=new Intent();
        intent.putExtra("code",result);
        setResult(0,intent);
        finish();
        //再次延时1.5秒后启动
//        zXingView.startSpot();

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toast.makeText(this, "相机打开失败", Toast.LENGTH_SHORT).show();
    }

//    @OnClick({R.id.btn_openligtht, R.id.btn_closeligtht, R.id.btn_photo,R.id.btn_openBarcode, R.id.btn_openQRcode})
//    public void onClick(View view) {
//        switch (view.getCode()) {
//            case R.id.btn_openligtht:
//                //打开闪关灯
//                zxingview.openFlashlight();
//                break;
//            case R.id.btn_closeligtht:
//                //关闭闪光灯
//                zxingview.closeFlashlight();
//                break;
//            case R.id.btn_photo:
//                //参数1 应用程序上下文
//                //参数2 拍照后图片保存的目录。如果传null表示没有拍照功能，如果不为null则具有拍照功能，
//                //参数3 图片选择张数的最大值
//                //参数4 当前已选中的图片路径集合，可以传null
//                //参数5 滚动列表时是否暂停加载图片
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
//                break;
//            case R.id.btn_openBarcode:
//                //切换到条形码扫描
//                zxingview.changeToScanBarcodeStyle();
//                break;
//            case R.id.btn_openQRcode:
//                //切换到二维码扫描
//                zxingview.changeToScanQRCodeStyle();
//                break;
//
//        }
//    }
}
