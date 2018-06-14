package com.laomachaguan.Editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.even.mricheditor.ActionType;
import com.even.mricheditor.RichEditorAction;
import com.even.mricheditor.RichEditorCallback;
import com.even.mricheditor.ui.ActionImageView;
import com.laomachaguan.Editor.fragment.EditHyperlinkFragment;
import com.laomachaguan.Editor.fragment.EditTableFragment;
import com.laomachaguan.Editor.fragment.EditorMenuFragment;
import com.laomachaguan.Editor.interfaces.OnActionPerformListener;
import com.laomachaguan.Editor.keyboard.KeyboardHeightObserver;
import com.laomachaguan.Editor.keyboard.KeyboardHeightProvider;
import com.laomachaguan.Editor.keyboard.KeyboardUtils;
import com.laomachaguan.Editor.util.FileIOUtil;
import com.laomachaguan.R;
import com.laomachaguan.Utils.AnalyticalJSON;
import com.laomachaguan.Utils.ApisSeUtil;
import com.laomachaguan.Utils.Constants;
import com.laomachaguan.Utils.LogUtil;
import com.laomachaguan.Utils.Network;
import com.laomachaguan.Utils.ProgressUtil;
import com.laomachaguan.Utils.StatusBarCompat;
import com.laomachaguan.Utils.ToastUtil;
import com.laomachaguan.Utils.mApplication;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

@SuppressLint("SetJavaScriptEnabled")
public class RichEditorActivity extends AppCompatActivity
        implements KeyboardHeightObserver, View.OnClickListener {
    //    @BindView(R2.id.wv_container)
    WebView mWebView;
    //    @BindView(R2.id.fl_action)
    FrameLayout flAction;
    //    @BindView(R2.id.ll_action_bar_container)
    LinearLayout llActionBarContainer;

    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;
    private boolean isKeyboardShowing;
    private String htmlContent = "";
    private String newHtml = "";
    private RichEditorAction mRichEditorAction;
    private RichEditorCallback mRichEditorCallback;

    private EditorMenuFragment mEditorMenuFragment;

    private List<ActionType> mActionTypeList =
            Arrays.asList(ActionType.BOLD, ActionType.ITALIC, ActionType.UNDERLINE,
                    ActionType.STRIKETHROUGH, ActionType.SUBSCRIPT, ActionType.SUPERSCRIPT,
                    ActionType.NORMAL, ActionType.H1, ActionType.H2, ActionType.H3, ActionType.H4,
                    ActionType.H5, ActionType.H6, ActionType.INDENT, ActionType.OUTDENT,
                    ActionType.JUSTIFY_LEFT, ActionType.JUSTIFY_CENTER, ActionType.JUSTIFY_RIGHT,
                    ActionType.JUSTIFY_FULL, ActionType.ORDERED, ActionType.UNORDERED, ActionType.LINE,
                    ActionType.BLOCK_CODE, ActionType.BLOCK_QUOTE, ActionType.CODE_VIEW);

    private List<Integer> mActionTypeIconList =
            Arrays.asList(R.drawable.ic_format_bold, R.drawable.ic_format_italic,
                    R.drawable.ic_format_underlined, R.drawable.ic_format_strikethrough,
                    R.drawable.ic_format_subscript, R.drawable.ic_format_superscript,
                    R.drawable.ic_format_para, R.drawable.ic_format_h1, R.drawable.ic_format_h2,
                    R.drawable.ic_format_h3, R.drawable.ic_format_h4, R.drawable.ic_format_h5,
                    R.drawable.ic_format_h6, R.drawable.ic_format_indent_decrease,
                    R.drawable.ic_format_indent_increase, R.drawable.ic_format_align_left,
                    R.drawable.ic_format_align_center, R.drawable.ic_format_align_right,
                    R.drawable.ic_format_align_justify, R.drawable.ic_format_list_numbered,
                    R.drawable.ic_format_list_bulleted, R.drawable.ic_line, R.drawable.ic_code_block,
                    R.drawable.ic_format_quote, R.drawable.ic_code_review);

    private static final int REQUEST_CODE_CHOOSE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_editor);
//        ButterKnife.bind(this);
        StatusBarCompat.compat(this, Color.parseColor("#2a292f"));
        initImageLoader();
        initView();

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9,
                getResources().getDisplayMetrics());
        for (int i = 0, size = mActionTypeList.size(); i < size; i++) {
            final ActionImageView actionImageView = new ActionImageView(this);
            actionImageView.setLayoutParams(new LinearLayout.LayoutParams(width, width));
            actionImageView.setPadding(padding, padding, padding, padding);
            actionImageView.setActionType(mActionTypeList.get(i));
            actionImageView.setTag(mActionTypeList.get(i));
            actionImageView.setActivatedColor(R.color.colorAccent);
            actionImageView.setDeactivatedColor(R.color.tintColor);
            actionImageView.setRichEditorAction(mRichEditorAction);
            actionImageView.setBackgroundResource(R.drawable.btn_colored_material);
            actionImageView.setImageResource(mActionTypeIconList.get(i));
            actionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionImageView.command();
                }
            });
            llActionBarContainer.addView(actionImageView);
        }

        mEditorMenuFragment = new EditorMenuFragment();
        mEditorMenuFragment.setActionClickListener(new MOnActionPerformListener(mRichEditorAction));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.fl_action, mEditorMenuFragment, EditorMenuFragment.class.getName())
                .commit();
    }

    private void imgReset() {
        LogUtil.e("js");
        mWebView.loadUrl("javascript:(function(){" +
                        "var table=document.getElementsByTagName('table');" +
                        "for(var i=0;i<table.length;i++){" +
                        "var t=table[i];" +
                        "t.style.width='100%';" +
                        "t.style.margin='auto';" +
                        "t.style.display='block';" +
                        "}" +
//                        "var objs = document.getElementsByTagName('img'); " +
//                        "for(var i=0;i<objs.length;i++)  " +
//                        "{"
//                        + "var img = objs[i];   " +
//                        "img.style.maxWidth = '100%'; " +
//                        "var w=img.style.width;" +
//                        "if(w > '50%') {" +
//                        "img.style.width='100%';}" +
//                        "img.style.height = 'auto'; " +
//                        "img.style.marginBottom=10;" +
//                        "img.style.marginTop=10;" +
//                        "img.style.marginLeft='auto';" +
//                        "img.style.marginRight='auto';" +
//                        "img.style.display='block';" +
//                        "}" +
                        "var obj1=document.getElementsByTagName('section');" +
                        "for(var i=0;i<obj1.length;i++)  " +
                        "{"
                        + "var sec = obj1[i];  " +
                        "sec.style.maxWidth = '100%'; " +
                        "var w1=sec.style.width;" +
                        "if(w1>'50%'){" +
                        "w1='100%';" +
                        "}" +
//                "sec.style.height = 'auto';" +
                        "}" +
                        "})()"
        );
    }

    /**
     * ImageLoader for insert Image
     */
    private void initImageLoader() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(false);
        imagePicker.setSaveRectangle(true);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(256);
        imagePicker.setOutPutY(256);
    }

    private void initView() {
        htmlContent = getIntent().getStringExtra("html");
        LogUtil.e("加载已有html：："+htmlContent);
        mWebView = (WebView) findViewById(R.id.wv_container);
        flAction = (FrameLayout) findViewById(R.id.fl_action);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.commit).setOnClickListener(this);
        llActionBarContainer = (LinearLayout) findViewById(R.id.ll_action_bar_container);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressUtil.show(RichEditorActivity.this, "", "请稍等");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.e("网页加载完毕");
                imgReset();
                ProgressUtil.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.e("重载网页");
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mRichEditorCallback = new MRichEditorCallback();
        mWebView.addJavascriptInterface(mRichEditorCallback, "MRichEditor");
        mWebView.loadUrl("file:///android_asset/richEditor.html");
        mRichEditorAction = new RichEditorAction(mWebView);

        keyboardHeightProvider = new KeyboardHeightProvider(this);
        findViewById(R.id.fl_container).post(new Runnable() {
            @Override
            public void run() {
                keyboardHeightProvider.start();
            }
        });
        setOnClick();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_action) {
            if (flAction.getVisibility() == View.VISIBLE) {
                flAction.setVisibility(View.GONE);
            } else {
                if (isKeyboardShowing) {
                    KeyboardUtils.hideSoftInput(RichEditorActivity.this);
                }
                flAction.setVisibility(View.VISIBLE);
            }
        } else if (v.getId() == R.id.back) {
            finish();
        } else if (v.getId() == R.id.commit) {
            mRichEditorAction.refreshHtml(mRichEditorCallback);//为html赋值
            ProgressUtil.show(RichEditorActivity.this, "", "请稍等");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long begin=System.currentTimeMillis()/1000;
                    while (newHtml.equals("")) {
                        LogUtil.e("修改过后的html:    " + mRichEditorCallback.getHtml());
                        newHtml = mRichEditorCallback.getHtml()==null?"":mRichEditorCallback.getHtml();
                        if (System.currentTimeMillis()/1000-begin >= 3) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressUtil.dismiss();
                                    ToastUtil.showToastShort("保存失败，请稍后重试");
                                }
                            });
                            break;
                        }
                    }
                    if (!newHtml.equals("")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ProgressUtil.dismiss();
                                ToastUtil.showToastShort("请记得提交更新噢");
                            }
                        });
                        Intent intent = new Intent();
                        intent.putExtra("html", newHtml);
                        setResult(111, intent);
                        finish();

                        newHtml = "";
                    }
                }
            }).start();


        } else if (v.getId() == R.id.iv_action_undo) {
            mRichEditorAction.undo();
        } else if (v.getId() == R.id.iv_action_redo) {
            mRichEditorAction.redo();
        } else if (v.getId() == R.id.iv_action_txt_color) {
            mRichEditorAction.foreColor("blue");
        } else if (v.getId() == R.id.iv_action_txt_bg_color) {
            mRichEditorAction.backColor("red");
        } else if (v.getId() == R.id.iv_action_line_height) {
            mRichEditorAction.lineHeight(20);
        } else if (v.getId() == R.id.iv_action_insert_image) {
            Intent intent = new Intent(this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE);
        } else if (v.getId() == R.id.iv_action_insert_link) {
            KeyboardUtils.hideSoftInput(RichEditorActivity.this);
            EditHyperlinkFragment fragment = new EditHyperlinkFragment();
            fragment.setOnHyperlinkListener(new EditHyperlinkFragment.OnHyperlinkListener() {
                @Override
                public void onHyperlinkOK(String address, String text) {
                    mRichEditorAction.createLink(text, address);
                }
            });
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
                    .commit();
        } else if (v.getId() == R.id.iv_action_table) {
            KeyboardUtils.hideSoftInput(RichEditorActivity.this);
            EditTableFragment fragment = new EditTableFragment();
            fragment.setOnTableListener(new EditTableFragment.OnTableListener() {
                @Override
                public void onTableOK(int rows, int cols) {
                    mRichEditorAction.insertTable(rows, cols);
                }
            });
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
                    .commit();
        }
    }


    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            LogUtil.e("加载进度：：；" + newProgress);
            if (newProgress == 100) {
                if (!TextUtils.isEmpty(htmlContent)) {
                    mRichEditorAction.insertHtml(htmlContent);
//                    String s=
//                            "<p><a href=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c03f776b0.png\"><img src=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c03f776b0.png\" alt=\"\"></a><a href=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c03b0c97b.png\"><img src=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c03b0c97b.png\" alt=\"\"></a>" +
//                                    "<a href=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c0693a8de.png\"><img src=\"http://yintolo.net/public/uploads/image/2017-10-17/59e5c0693a8de.png\" alt=\"\"></a></p>";
//                    mRichEditorAction.insertHtml(s);
                }
                KeyboardUtils.showSoftInput(RichEditorActivity.this);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

    }

    private void setOnClick() {
        findViewById(R.id.iv_action).setOnClickListener(this);
        findViewById(R.id.iv_action_undo).setOnClickListener(this);
        findViewById(R.id.iv_action_redo).setOnClickListener(this);
        findViewById(R.id.iv_action_txt_color).setOnClickListener(this);
        findViewById(R.id.iv_action_txt_bg_color).setOnClickListener(this);
        findViewById(R.id.iv_action_line_height).setOnClickListener(this);
        findViewById(R.id.iv_action_insert_image).setOnClickListener(this);
        findViewById(R.id.iv_action_insert_link).setOnClickListener(this);
        findViewById(R.id.iv_action_table).setOnClickListener(this);

    }
//    @OnClick(R2.id.iv_action) void onClickAction() {
//        if (flAction.getVisibility() == View.VISIBLE) {
//            flAction.setVisibility(View.GONE);
//        } else {
//            if (isKeyboardShowing) {
//                KeyboardUtils.hideSoftInput(RichEditorActivity.this);
//            }
//            flAction.setVisibility(View.VISIBLE);
//        }
//    }

//    @OnClick(R2.id.iv_action_undo) void onClickUndo() {
//        mRichEditorAction.undo();
//    }

//    @OnClick(R2.id.iv_action_redo) void onClickRedo() {
//        mRichEditorAction.redo();
//    }

//    @OnClick(R2.id.iv_action_txt_color) void onClickTextColor() {
//        mRichEditorAction.foreColor("blue");
//    }

//    @OnClick(R2.id.iv_action_txt_bg_color) void onClickHighlight() {
//        mRichEditorAction.backColor("red");
//    }

//    @OnClick(R2.id.iv_action_line_height) void onClickLineHeight() {
//        mRichEditorAction.lineHeight(20);
//    }

//    @OnClick(R2.id.iv_action_insert_image) void onClickInsertImage() {
//        Intent intent = new Intent(this, ImageGridActivity.class);
//        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS
                && data != null
                && requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<ImageItem> images =
                    (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null && !images.isEmpty()) {

                //1.Insert the Base64 String (Base64.NO_WRAP)
                ImageItem imageItem = images.get(0);
//                mRichEditorAction.insertImageData(imageItem.name,
//                        encodeFileToBase64Binary(imageItem.path));
                ProgressUtil.show(RichEditorActivity.this,"","图片处理中");
                Tiny.getInstance().init(mApplication.getInstance());
                Tiny.getInstance().source(imageItem.path)
                        .asFile().compress(new FileCallback() {
                    @Override
                    public void callback(boolean isSuccess, String outfile) {
                        if(isSuccess){
                            pushFile(outfile);
                        }else{
                            ProgressUtil.dismiss();
                            ToastUtil.showToastShort("图片处理失败，请稍后重试");
                        }
                    }
                });

                //2.Insert the ImageUrl
                //mRichEditorAction.insertImageUrl(
                //    "https://avatars0.githubusercontent.com/u/5581118?v=4&u=b7ea903e397678b3675e2a15b0b6d0944f6f129e&s=400");
            }
        }
    }

    private void pushFile(String path) {
        if(!Network.HttpTest(this)){
            ProgressUtil.dismiss();
            return;
        }
        JSONObject js=new JSONObject();
        try {
            js.put("m_id", Constants.M_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ApisSeUtil.M m=ApisSeUtil.i(js);
        LogUtil.e("上传图片：：："+js);
        OkHttpUtils.post(Constants.ImageUp).params("key",m.K())
                .params("msg",m.M())
                .params("image",new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        HashMap<String,String> map= AnalyticalJSON.getHashMap(s);
                        if(map!=null){
                            if("000".equals(map.get("code"))){
                                mRichEditorAction.insertImageUrl(
                                    map.get("url"));
                            }
                        }else{
                            ToastUtil.showToastShort("图片处理失败，请稍后重试");
                        }
                    }

                    @Override
                    public void onAfter(@Nullable String s, @Nullable Exception e) {
                        super.onAfter(s, e);
                        ProgressUtil.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ToastUtil.showToastShort("图片处理失败，请稍后重试");
                    }
                });
    }

    private static String encodeFileToBase64Binary(String filePath) {
        byte[] bytes = FileIOUtil.readFile2BytesByStream(filePath);
        byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
        return new String(encoded);
    }

//    @OnClick(R2.id.iv_action_insert_link) void onClickInsertLink() {
//        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
//        EditHyperlinkFragment fragment = new EditHyperlinkFragment();
//        fragment.setOnHyperlinkListener(new EditHyperlinkFragment.OnHyperlinkListener() {
//            @Override public void onHyperlinkOK(String address, String text) {
//                mRichEditorAction.createLink(text, address);
//            }
//        });
//        getSupportFragmentManager().beginTransaction()
//            .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
//            .commit();
//    }

//    @OnClick(R2.id.iv_action_table) void onClickInsertTable() {
//        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
//        EditTableFragment fragment = new EditTableFragment();
//        fragment.setOnTableListener(new EditTableFragment.OnTableListener() {
//            @Override public void onTableOK(int rows, int cols) {
//                mRichEditorAction.insertTable(rows, cols);
//            }
//        });
//        getSupportFragmentManager().beginTransaction()
//            .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
//            .commit();
//    }

    @Override
    public void onBackPressed() {
        findViewById(R.id.back).performClick();

    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        if (flAction.getVisibility() == View.INVISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            flAction.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = flAction.getLayoutParams();
            params.height = height;
            flAction.setLayoutParams(params);
        } else if (flAction.getVisibility() != View.VISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    class MRichEditorCallback extends RichEditorCallback {

        @Override
        public void notifyFontStyleChange(ActionType type, final String value) {
            ActionImageView actionImageView =
                    (ActionImageView) llActionBarContainer.findViewWithTag(type);
            if (actionImageView != null) {
                actionImageView.notifyFontStyleChange(type, value);
            }

            if (mEditorMenuFragment != null) {
                mEditorMenuFragment.updateActionStates(type, value);
            }
        }

    }

    public class MOnActionPerformListener implements OnActionPerformListener {
        private RichEditorAction mRichEditorAction;

        public MOnActionPerformListener(RichEditorAction mRichEditorAction) {
            this.mRichEditorAction = mRichEditorAction;
        }

        @Override
        public void onActionPerform(ActionType type, Object... values) {
            if (mRichEditorAction == null) {
                return;
            }

            String value = null;
            if (values != null && values.length > 0) {
                value = (String) values[0];
            }

            switch (type) {
                case SIZE:
                    mRichEditorAction.fontSize(Double.valueOf(value));
                    break;
                case LINE_HEIGHT:
                    mRichEditorAction.lineHeight(Double.valueOf(value));
                    break;
                case FORE_COLOR:
                    mRichEditorAction.foreColor(value);
                    break;
                case BACK_COLOR:
                    mRichEditorAction.backColor(value);
                    break;
                case FAMILY:
                    mRichEditorAction.fontName(value);
                    break;
                case IMAGE:
//                    onClickInsertImage();
                    findViewById(R.id.iv_action_insert_image).performClick();
                    break;
                case LINK:
//                    onClickInsertLink();
                    findViewById(R.id.iv_action_insert_link).performClick();
                    break;
                case TABLE:
//                    onClickInsertTable();
                    findViewById(R.id.iv_action_table).performClick();
                    break;
                case BOLD:
                case ITALIC:
                case UNDERLINE:
                case SUBSCRIPT:
                case SUPERSCRIPT:
                case STRIKETHROUGH:
                case JUSTIFY_LEFT:
                case JUSTIFY_CENTER:
                case JUSTIFY_RIGHT:
                case JUSTIFY_FULL:
                case CODE_VIEW:
                case ORDERED:
                case UNORDERED:
                case INDENT:
                case OUTDENT:
                case BLOCK_QUOTE:
                case BLOCK_CODE:
                case NORMAL:
                case H1:
                case H2:
                case H3:
                case H4:
                case H5:
                case H6:
                case LINE:
                    ActionImageView actionImageView =
                            (ActionImageView) llActionBarContainer.findViewWithTag(type);
                    if (actionImageView != null) {
                        actionImageView.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
