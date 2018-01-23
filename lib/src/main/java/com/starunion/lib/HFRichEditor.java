package com.starunion.lib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Discription:帖子富文本编辑器
 *
 * Created by sz on 2018/1/22.
 */

public class HFRichEditor extends LinearLayout {
    Context mContext;
    //记录最近获取焦点的EditText
    EditText mLastEditText;
    ImageLoaderInterface mImageLoaderInterface;

    public HFRichEditor(Context context) {
        this(context, null);
    }

    public HFRichEditor(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HFRichEditor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOrientation(VERTICAL);
        mImageLoaderInterface = new GlideLoader();
        initViews();
    }

    private void initViews() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        EditText firstEditText = createEditText("");
        mLastEditText = firstEditText;
        addView(firstEditText, params);
    }

    /**
     * 设置图片加载方案
     * @param mImageLoaderInterface
     */
    public void setImageLoaderInterface(ImageLoaderInterface mImageLoaderInterface) {
        this.mImageLoaderInterface = mImageLoaderInterface;
    }

    /**
     * 根据内容创建文本编辑器
     *
     * @param content 内容
     * @return
     */
    private EditText createEditText(String content) {
        final EditText editText = (EditText) LayoutInflater.from(mContext).inflate(R.layout.edittext_richeditor, null);
        editText.setText(content);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus){
                    mLastEditText = (EditText) view;
                }
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction()){
                    if (editText.getSelectionStart() == 0){
                        int index = indexOfChild(view);
                        if (index > 0){
                            View beforeView = getChildAt(index -1);
                            if (beforeView instanceof EditText){
                                //合并两个EditText
                                mergeEditor((EditText) beforeView, editText);
                            }
                        }
                    }
                }
                return false;
            }

        });
        return editText;
    }

    /**
     * 合并两个编辑器
     * @param beforeView    前一个View
     * @param currentView   当前的View
     */
    private void mergeEditor(EditText beforeView, EditText currentView) {
        String preStr = beforeView.getText().toString();
        String suffixStr = currentView.getText().toString();
        beforeView.setText(preStr+suffixStr);
        beforeView.setSelection(preStr.length());
        removeView(currentView);
    }


    /**
     * 插入一张本地图片
     *
     * @param imagePath
     */
    public void insertImageView(String imagePath) {
        Log.d("HFRichEditor", "mLastEditText == getFocusChild :"+(mLastEditText == getFocusedChild()));
        //第一步分割焦点字符串
        String lastEditStr = mLastEditText.getText().toString();
        int cursorIndex = mLastEditText.getSelectionStart();
        String preStr = lastEditStr.substring(0, cursorIndex);
        String suffixStr = lastEditStr.substring(cursorIndex);
        mLastEditText.setText(preStr);
        //第二步创建ImageView
        int lastEditIndex = indexOfChild(mLastEditText);
        addEditTextAtIndex(lastEditIndex+1,suffixStr);
        addImageViewAtIndex(lastEditIndex+1, imagePath);

    }

    /**
     * 在index位置添加一个EditText
     *
     * @param targetIndex
     * @param suffixStr
     */
    private void addEditTextAtIndex(int targetIndex, String suffixStr) {
        //将之前的edittext变成wrap_content,将之后添加的变为match_parent
        fixLastEditHeight();
        //添加一个如果在最后就是match_parent不是就是wrap_content的EditText
        EditText editText = createEditText(suffixStr);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (targetIndex >= getChildCount()){
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        addView(editText, targetIndex, params);
        mLastEditText = editText;
        mLastEditText.requestFocus();
        mLastEditText.setSelection(0);
    }

    /**
     * 修正之前Edit的高度
     */
    private void fixLastEditHeight() {
        if (mLastEditText != null){
            ViewGroup.LayoutParams params = mLastEditText.getLayoutParams();
            if (params != null){
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mLastEditText.setLayoutParams(params);
            }
        }
    }

    /**
     * 在lastEditIndex创建一个ImageView
     *
     * @param targetIndex
     */
    private void addImageViewAtIndex(int targetIndex, String imagePath) {
        View imageContent = LayoutInflater.from(mContext).inflate(R.layout.rl_image_richeditor, null);
        ImageView reImageView = imageContent.findViewById(R.id.iv_image_richeditor);
        final Button redeleteBtn = imageContent.findViewById(R.id.btn_delete_richeditor);

        if (mImageLoaderInterface != null){
            mImageLoaderInterface = new GlideLoader();
            mImageLoaderInterface.getImageBitmapByPath(reImageView, imagePath);
        }

        redeleteBtn.setTag(imageContent);

        redeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //1、删除控件
                removeView((View) redeleteBtn.getTag());
                //2、删除Image数组对应的位置
            }
        });

        addView(imageContent, targetIndex);
    }


}
