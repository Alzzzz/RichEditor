package com.starunion.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Discription:帖子富文本编辑器
 * <p>
 * Created by sz on 2018/1/22.
 */

public class HFRichEditor extends LinearLayout {
    private static final String TAG = HFRichEditor.class.getSimpleName();
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_PIC = 1;
    public static final int TYPE_VIDEO = 2;

    Context mContext;
    //记录最近获取焦点的EditText
    EditText mLastEditText;
    //记录最底端的EditText,用于控制高度
    EditText mBottomEditText;
    ImageLoaderInterface mImageLoaderInterface;
    HFRichEditorListener hfRichEditorListener;
    int totalTextNum = 0;
    //最大长度
    int maxLength = 0;
    float textSize;
    int textColor;
    //用于图片url和控件绑定
    List<String> picsList = new ArrayList<>();

    String mVideoPath = "";
    ImageView ivCoverVideo;
    private Bitmap videoCoverBtm;
    EditText firstEditText;
    String hintStr;

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
        initViews(attrs);
    }

    private void initViews(AttributeSet attrs) {

        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.HFRichEditor);
        hintStr = a.getString(R.styleable.HFRichEditor_hint);
        textSize = a.getDimension(R.styleable.HFRichEditor_textSize, 15f);
        textColor = a.getColor(R.styleable.HFRichEditor_textColor, getResources().getColor(R.color.black32));
        maxLength = a.getInt(R.styleable.HFRichEditor_maxLength, 120);
        a.recycle();

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        firstEditText = createEditText("");
        firstEditText.setHint(hintStr);
        firstEditText.setTextSize(textSize);
        firstEditText.setTextColor(textColor);
        mLastEditText = firstEditText;
        mBottomEditText = firstEditText;
        addView(firstEditText, params);
    }

    /**
     * 设置默认提示
     * @param hintStr
     */
    public void setHintStr(String hintStr){
        this.hintStr = hintStr;
        if (!TextUtils.isEmpty(firstEditText.getHint().toString())){
            firstEditText.setHint(hintStr);
        }
    }
    /**
     * 设置能输入的最大字数
     *
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 设置图片加载方案
     *
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
    private EditText createEditText(CharSequence content) {
        final EditText editText = (EditText) LayoutInflater.from(mContext).inflate(R.layout.edittext_richeditor, null);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    mLastEditText = (EditText) view;
                }
                hfRichEditorListener.onFocusChange(view, focus);
            }
        });
        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hfRichEditorListener.onFocusChange(v, true);
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    if (editText.getSelectionStart() == 0) {
                        int index = indexOfChild(view);
                        if (index > 0) {
                            View beforeView = getChildAt(index - 1);
                            if (beforeView instanceof EditText) {
                                //合并两个EditText
                                mergeEditor((EditText) beforeView, editText);
                                if (index >= getChildCount() - 1) {//最后一个View
                                    ViewGroup.LayoutParams params = beforeView.getLayoutParams();
                                    if (params == null) {
                                        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                    } else {
                                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                                    }
                                    beforeView.setLayoutParams(params);
                                    mBottomEditText = (EditText) beforeView;
                                }
                            }
                        }
                    }
                }
                return false;
            }

        });
        editText.addTextChangedListener(new HFRichEditorTextWatch(editText));
        editText.setTextSize(textSize);
        editText.setTextColor(textColor);
        editText.setText(content);
        editText.setTag(R.id.tag_edit_type, TYPE_TEXT);
        return editText;
    }

    /**
     * RichEditor Text监听
     */
    public class HFRichEditorTextWatch implements TextWatcher {
        CharSequence beforeStr = "";
        EditText mEditText;

        /**
         * 传递一个TargetEditText
         *
         * @param editText
         */
        public HFRichEditorTextWatch(EditText editText) {
            this.mEditText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.d(TAG, "beforeTextChanged: s=" + s + ",start=" + start + ",count=" + count + ",after=" + after);
            beforeStr = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d(TAG, "onTextChanged: s=" + s + ",start=" + start + ",before=" + before + ",count=" + count);
            //1、判断是否小于总数
            totalTextNum += (count - before);
            if (totalTextNum > maxLength) {
                //方式一：保证表情完整
//                //1、获取超过的文本，并Emoji格式化
//                CharSequence replaceStr = s.subSequence(start, start+count);
//                //2、截取从start到start+totalTextNum-maxLength的文本，并保证最后一个表情不被截取
//                replaceStr = HFEmojiUtiles.cutstring(replaceStr.toString(), start+totalTextNum-maxLength);

                //方式二：保证完整输入
                SpannableStringBuilder ssb = new SpannableStringBuilder();
                CharSequence replaceStr = s.subSequence(start, start + count - totalTextNum + maxLength);
                CharSequence tempPreStr = beforeStr.subSequence(0, start);
                CharSequence tempSuffixStr = beforeStr.subSequence(start, beforeStr.length());
                CharSequence resultStr = ssb.append(tempPreStr).append(replaceStr).append(tempSuffixStr);
                //先取消监听再添加监听
                mEditText.removeTextChangedListener(this);
                mEditText.setText(resultStr);
                mEditText.setSelection(start + replaceStr.length());
                mEditText.addTextChangedListener(this);
                totalTextNum = maxLength;
            }

            if (totalTextNum == 0
                    && picsList.size() == 0
                    && TextUtils.isEmpty(mVideoPath)
                    && firstEditText != null) {
                firstEditText.setHint(hintStr);
            }

            if (hfRichEditorListener != null) {
                hfRichEditorListener.onTextCountChanged(totalTextNum);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: s" + s);
        }
    }

    /**
     * 合并两个编辑器
     *
     * @param beforeView  前一个View
     * @param currentView 当前的View
     */
    private void mergeEditor(EditText beforeView, EditText currentView) {
        int preStrLenght = beforeView.getText().length();
        beforeView.setText(beforeView.getText().append(currentView.getText()));
        currentView.setText("");
        beforeView.requestFocus();
        beforeView.setSelection(preStrLenght);
        removeView(currentView);
    }


    /**
     * 插入一张本地图片
     *
     * @param imagePath
     */
    public void insertImageView(String imagePath) {
        Log.d("HFRichEditor", "mLastEditText == getFocusChild :" + (mLastEditText == getFocusedChild()));
        //第一步分割焦点字符串
        CharSequence lastEditStr = mLastEditText.getText();
        int cursorIndex = mLastEditText.getSelectionStart();
        CharSequence preStr = lastEditStr.subSequence(0,cursorIndex);
        CharSequence suffixStr = lastEditStr.subSequence(cursorIndex, lastEditStr.length());
        mLastEditText.setText(preStr);
        //第二步创建ImageView
        int lastEditIndex = indexOfChild(mLastEditText);
        addEditTextAtIndex(lastEditIndex + 1, suffixStr);
        addImageViewAtIndex(lastEditIndex + 1, imagePath);

    }

    /**
     * 在lastEditIndex创建一个ImageView
     *
     * @param targetIndex
     */
    private synchronized void addImageViewAtIndex(int targetIndex, String imagePath) {
        View imageContent = LayoutInflater.from(mContext).inflate(R.layout.rl_image_richeditor, null);
        ImageView reImageView = imageContent.findViewById(R.id.iv_image_richeditor);
        final ImageView redeleteBtn = imageContent.findViewById(R.id.iv_delete_richeditor);
        View maskView = imageContent.findViewById(R.id.mv_image_richeditor);
        reImageView.setEnabled(false);
        if (mImageLoaderInterface != null) {
            mImageLoaderInterface = new GlideLoader();
            mImageLoaderInterface.loadBitmapIntoImageView(reImageView, imagePath, maskView);
        }

        imageContent.setTag(R.id.tag_edit_type, TYPE_PIC);
        imageContent.setTag(R.id.tag_pic_url, imagePath);
        picsList.add(imagePath);

        redeleteBtn.setTag(imageContent);

        redeleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //1、删除控件
                View rootView = (View) redeleteBtn.getTag();
                String url = (String) rootView.getTag(R.id.tag_pic_url);
                //判断图片之后是否还有edittext有内容、有就不删除，没有就删除之前edittext
                deleteTextUntilIndex(indexOfChild(rootView));
                removeView(rootView);
                //2、删除Image数组对应的位置
                boolean success = picsList.remove(url);
                Log.d(TAG, "onClick: delete success=" + success);
                hfRichEditorListener.onImgSizeChanged(picsList.size());
                if (picsList.size() == 0
                        && TextUtils.isEmpty(mVideoPath)
                        && TextUtils.isEmpty(getAbstractContent())) {
                    //如果没有图片和没有视频了
                    firstEditText.setHint(hintStr);
                }
            }
        });
        addView(imageContent, targetIndex);
        firstEditText.setHint("");
        Log.d(TAG, "addImageViewAtIndex: picsList.size()=" + picsList.size());
        hfRichEditorListener.onImgSizeChanged(picsList.size());
    }

    /**
     * 如果index之后的EditText都是空的情况下
     * 删除index前一个EditText，倒叙
     *
     * @param index
     */
    private synchronized void deleteTextUntilIndex(int index) {
        String tempStr = "";
        for (int i = getChildCount(); i >= index - 1 && i >= 0; i--) {
            if (getChildAt(i) instanceof EditText) {
                tempStr += ((EditText) getChildAt(i)).getText().toString();
            }
        }
        //之后再也没有字符串
        if (TextUtils.isEmpty(tempStr)) {
            if (index == 1) {
                //如果要删除第0个
                EditText nextEidtor = getNextEditText(0);
                if (nextEidtor != null) {
                    firstEditText = nextEidtor;
                    removeViewAt(index - 1);
                }
            } else {
                removeViewAt(index - 1);
            }
        }
    }

    /**
     * 获取下一个EditText
     *
     * @param index
     * @return
     */
    private EditText getNextEditText(int index) {
        for (int i = index + 1; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof EditText) {
                return (EditText) getChildAt(i);
            }
        }
        return null;
    }

    /**
     * 插入视频View
     *
     * @param videoPath
     * @param coverPath
     * @param videoLength
     */
    public void insertVideoView(String videoPath, String coverPath, String videoLength, EditVideoController mEditVideoController) {
        //第一步分割焦点字符串
        CharSequence lastEditStr = mLastEditText.getText();
        int cursorIndex = mLastEditText.getSelectionStart();
        CharSequence preStr = lastEditStr.subSequence(0,cursorIndex);
        CharSequence suffixStr = lastEditStr.subSequence(cursorIndex, lastEditStr.length());
        mLastEditText.setText(preStr);
        //第二步创建VideoView
        int lastEditIndex = indexOfChild(mLastEditText);
        addEditTextAtIndex(lastEditIndex + 1, suffixStr);
        addVideoViewAtIndex(lastEditIndex + 1, videoPath, coverPath, videoLength, mEditVideoController);
    }

    /**
     * 在index处添加一个video控件
     *
     * @param index
     * @param videoPath
     * @param coverPath
     * @param videoLength
     */
    private synchronized void addVideoViewAtIndex(int index, final String videoPath, final String coverPath,
                                                  final String videoLength, final EditVideoController mEditVideoController) {
        View videoContent = LayoutInflater.from(mContext).inflate(R.layout.layout_video_clubedit, null);
        ivCoverVideo = (ImageView) videoContent.findViewById(R.id.iv_cover_video);
        final ImageView ivDeleteVideo = (ImageView) videoContent.findViewById(R.id.iv_delete_video);
        ImageView ivStartVideo = (ImageView) videoContent.findViewById(R.id.iv_start_video);
        TextView tvChangeCover = (TextView) videoContent.findViewById(R.id.tv_changecover_video);

        ViewGroup.LayoutParams params = videoContent.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(mContext, 205));
        } else {
            params.height = DisplayUtil.dip2px(mContext, 205);
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        videoContent.setLayoutParams(params);

        if (mImageLoaderInterface != null) {
            mImageLoaderInterface = new GlideLoader();
            mImageLoaderInterface.loadBitmapIntoVideoView(ivCoverVideo, coverPath);
        }

        this.mVideoPath = videoPath;
        videoContent.setTag(R.id.tag_edit_type, TYPE_VIDEO);
        videoContent.setTag(R.id.tag_pic_url, videoPath);
        videoContent.setTag(R.id.tag_video_cover, coverPath);
        videoContent.setTag(R.id.tag_video_length, videoLength);

        ivDeleteVideo.setTag(videoContent);
        ivDeleteVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //1、删除控件
                View rootView = (View) ivDeleteVideo.getTag();
                String videoPath = (String) rootView.getTag(R.id.tag_pic_url);
                deleteTextUntilIndex(indexOfChild(rootView));
                removeView(rootView);
                //2、删除Image数组对应的位置
                if (!TextUtils.isEmpty(mVideoPath) && mVideoPath.equals(videoPath)) {
                    mVideoPath = "";
                    if (picsList.size() == 0
                            && TextUtils.isEmpty(getAbstractContent())) {
                        //如果没有图片和没有视频了
                        firstEditText.setHint(hintStr);
                    }
                    Log.d(TAG, "onClick: delete success=" + true);
                } else {
                    Log.d(TAG, "onClick: delete success=" + false);
                }

                hfRichEditorListener.onVideoSizeChanged(TextUtils.isEmpty(mVideoPath) ? 0 : 1);
            }
        });

        ivStartVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditVideoController != null) {
                    mEditVideoController.start(videoPath, coverPath, videoLength);
                }
            }
        });

        tvChangeCover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditVideoController != null) {
                    mEditVideoController.onClickChangeCover(videoPath, videoLength);
                }
            }
        });

        addView(videoContent, index);

        firstEditText.setHint("");
        hfRichEditorListener.onVideoSizeChanged(TextUtils.isEmpty(mVideoPath) ? 0 : 1);
    }

    /**
     * 在index位置添加一个EditText
     *
     * @param targetIndex
     * @param suffixStr
     */
    private void addEditTextAtIndex(int targetIndex, CharSequence suffixStr) {
        //添加一个如果在最后就是match_parent不是就是wrap_content的EditText
        EditText editText = createEditText(suffixStr);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (targetIndex >= getChildCount()) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            //将之前的edittext变成wrap_content,将之后添加的变为match_parent
            fixLastEditHeight();
            mBottomEditText = editText;
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
        //这个只可能是最后一个，其他的就算修改也无所谓
        if (mBottomEditText != null) {
            ViewGroup.LayoutParams params = mBottomEditText.getLayoutParams();
            if (params != null) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mBottomEditText.setLayoutParams(params);
            }
        }
    }

    /**
     * 获取最近focus的EditText
     *
     * @return
     */
    public EditText getLastFocusEdit() {
        return mLastEditText;
    }

    /**
     * 设置富文本输入监听
     *
     * @param listener
     */
    public void setHfRichEditorListener(HFRichEditorListener listener) {
        this.hfRichEditorListener = listener;
    }

    /**
     * 获取当前图片的列表，用于上传
     */
    public List<String> getImageList() {
        return picsList;
    }


    /**
     * 更换视频封面
     *
     * @param bitmap
     */
    public void changeVideoCover(Bitmap bitmap) {
        if (ivCoverVideo != null) {
            ivCoverVideo.setImageBitmap(bitmap);
            videoCoverBtm = bitmap;
        }
    }

    /**
     * 获取文章摘要
     *
     * @return
     */
    public String getAbstractContent() {
        String articalAbstract = "";
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof EditText) {
                String editStr = ((EditText) getChildAt(i)).getText().toString();
                if (!TextUtils.isEmpty(editStr)) {
                    articalAbstract = editStr;
                    break;
                }
            }
        }
        if (articalAbstract.length() > 200){
            articalAbstract = articalAbstract.substring(0, 200);
        }
        return articalAbstract;
    }

    /**
     * 获取所有内容Text
     *
     * @return 内容集合
     */
    public List<EditContentBean> getDraftInfo() {
        List<EditContentBean> contentBeans = new ArrayList<>();
        //遍歷所有Image的Url和对应的text的Index
        for (int i = 0; i < getChildCount(); i++) {
            EditContentBean mEditContentBean = new EditContentBean();
            if (getChildAt(i).getTag(R.id.tag_edit_type) != null) {
                switch ((int) getChildAt(i).getTag(R.id.tag_edit_type)) {
                    case TYPE_TEXT:
                        if (getChildAt(i) instanceof EditText) {
                            mEditContentBean.setContent(((EditText) getChildAt(i)).getText().toString());
                            mEditContentBean.setIndex(i);
                            mEditContentBean.setType(TYPE_TEXT);
                            contentBeans.add(mEditContentBean);
                        }
                        break;
                    case TYPE_PIC:
                        if (getChildAt(i).getTag(R.id.tag_pic_url) != null){
                            mEditContentBean.setIndex(i);
                            mEditContentBean.setFilePath((String) getChildAt(i).getTag(R.id.tag_pic_url));
                            mEditContentBean.setType(TYPE_PIC);
                            contentBeans.add(mEditContentBean);
                        }
                        break;
                    case TYPE_VIDEO:
                        if (getChildAt(i).getTag(R.id.tag_pic_url) != null){
                            mEditContentBean.setIndex(i);
                            mEditContentBean.setFilePath((String) getChildAt(i).getTag(R.id.tag_pic_url));
                            String coverPath = FileUtil.getInstance().saveCorver(videoCoverBtm);
                            if (!TextUtils.isEmpty(coverPath)){
                                mEditContentBean.setCoverPath(coverPath);
                            } else {
                                mEditContentBean.setCoverPath((String) getChildAt(i).getTag(R.id.tag_video_cover));
                            }
                            mEditContentBean.setVideoLength((String) getChildAt(i).getTag(R.id.tag_video_length));
                            mEditContentBean.setType(TYPE_VIDEO);
                            contentBeans.add(mEditContentBean);
                        }
                        break;
                }
            } else if (getChildAt(i) instanceof EditText) {
                mEditContentBean.setContent(((EditText) getChildAt(i)).getText().toString());
                mEditContentBean.setIndex(i);
                contentBeans.add(mEditContentBean);
            }
        }
        return contentBeans;
    }

    /**
     * 将草稿设置进来
     *
     * @param editContentBeans
     */
    public void setDraft(List<EditContentBean> editContentBeans, EditVideoController mEditVideoController){
        if (editContentBeans == null){
            return;
        }
        for (int index = 0; index < editContentBeans.size(); index++){
            EditContentBean mEditContentBean = editContentBeans.get(index);
            switch (mEditContentBean.getType()){
                case TYPE_TEXT:
                    String mSpannableString = mEditContentBean.getContent();
                    if (index == 0){
                        firstEditText.setText(mSpannableString);
                    } else {
                        addEditTextAtIndex(index, mSpannableString);
                    }
                    break;
                case TYPE_PIC:
                    if (FileUtil.getInstance().isExist(mEditContentBean.getFilePath())){
                        addImageViewAtIndex(index, mEditContentBean.getFilePath());
                    }
                    break;
                case TYPE_VIDEO:
                    if (FileUtil.getInstance().isExist(mEditContentBean.getFilePath())){
                        addVideoViewAtIndex(index, mEditContentBean.getFilePath(),
                                mEditContentBean.getCoverPath(), mEditContentBean.getVideoLength(), mEditVideoController);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 是否为空
     */
    public boolean isEmpty(){
        if ((picsList == null || picsList.size() == 0)
                && (TextUtils.isEmpty(mVideoPath))){
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) instanceof EditText) {
                    String editStr = ((EditText) getChildAt(i)).getText().toString();
                    if (!TextUtils.isEmpty(editStr.trim())) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }


}
