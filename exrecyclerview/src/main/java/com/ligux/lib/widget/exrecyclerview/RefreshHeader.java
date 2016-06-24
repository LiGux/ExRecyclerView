package com.ligux.lib.widget.exrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

//import me.umei.library.R;

//import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView;

public class RefreshHeader extends LinearLayout {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_RELEASE_TO_REFRESH = 1;
    public static final int STATE_REFRESHING = 2;
    private static final int STATE_DONE = 3;
    private static final int STATE_FAIL = 4;

    private LinearLayout mContainer;
    //private ImageView mArrowImageView;
    private ProgressBar mProgressBar;
    private ImageView mRefreshView;
    private TextView mFailTextView;
    private int mState = STATE_NORMAL;

//    private TextView mHeaderTimeView;
//
//    private Animation mRotateUpAnim;
//    private Animation mRotateDownAnim;
//
//    private static final int ROTATE_ANIM_DURATION = 180;

    public int mMeasuredHeight;
    private int mMinVisiableHeight = 1;

    public RefreshHeader(Context context) {
        super(context);
        initView();
    }

    /**
     * @param context
     * @param attrs
     */
    public RefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.listview_header, null);

        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, mMinVisiableHeight));
        setGravity(Gravity.BOTTOM);

        //mArrowImageView = (ImageView)findViewById(R.id.listview_header_arrow);
        mRefreshView = (ImageView) findViewById(R.id.refresh_icon);
        mProgressBar = (ProgressBar) findViewById(R.id.refresh_progress);
        mFailTextView = (TextView) findViewById(R.id.fail_text);

        //init the progress view
//		mProgressBar = (SimpleViewSwitcher)findViewById(R.id.listview_header_progressbar);
//        AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(getContext());
//        progressView.setIndicatorColor(0xffB5B5B5);
//        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader);
//        mProgressBar.setView(progressView);


//		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
//		mRotateUpAnim.setFillAfter(true);
//		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
//				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
//		mRotateDownAnim.setFillAfter(true);

        //mHeaderTimeView = (TextView)findViewById(R.id.last_refresh_time);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

//    public void setProgressStyle(int style) {
//        if(style == ProgressStyle.SysProgress){
//            mProgressBar.setView(new ProgressBar(getContext(), null, android.R.attr.progressBarStyle));
//        }else{
//            AVLoadingIndicatorView progressView = new  AVLoadingIndicatorView(this.getContext());
//            progressView.setIndicatorColor(0xffB5B5B5);
//            progressView.setIndicatorId(style);
//            mProgressBar.setView(progressView);
//        }
//    }

//    public void setArrowImageView(int resid) {
//        mArrowImageView.setImageResource(resid);
//    }

    public void setState(int state) {
        if (state == mState) return;

//		if (state == STATE_REFRESHING) {	// 显示进度
//			mArrowImageView.clearAnimation();
//			mArrowImageView.setVisibility(View.INVISIBLE);
//			mProgressBar.setVisibility(View.VISIBLE);
//		} else if(state == STATE_DONE) {
//            mArrowImageView.setVisibility(View.INVISIBLE);
//            mProgressBar.setVisibility(View.INVISIBLE);
//        } else {	// 显示箭头图片
//			mArrowImageView.setVisibility(View.VISIBLE);
//			mProgressBar.setVisibility(View.INVISIBLE);
//		}

        switch (state) {
            case STATE_NORMAL:
                mProgressBar.setVisibility(View.GONE);
                mRefreshView.setVisibility(View.VISIBLE);
//                if (mState == STATE_RELEASE_TO_REFRESH) {
//                    mArrowImageView.startAnimation(mRotateDownAnim);
//                }
//                if (mState == STATE_REFRESHING) {
//                    mArrowImageView.clearAnimation();
//                }
                mFailTextView.setVisibility(View.GONE);
                break;
            case STATE_RELEASE_TO_REFRESH:
                mProgressBar.setVisibility(View.GONE);
                mRefreshView.setVisibility(View.VISIBLE);
//                if (mState != STATE_RELEASE_TO_REFRESH) {
                //mArrowImageView.clearAnimation();
                //mArrowImageView.startAnimation(mRotateUpAnim);
                mFailTextView.setVisibility(View.GONE);
//                }
                break;
            case STATE_REFRESHING:
                mProgressBar.setVisibility(View.VISIBLE);
                mRefreshView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.GONE);
                break;
            case STATE_DONE:
                mProgressBar.setVisibility(View.GONE);
                mRefreshView.setVisibility(View.VISIBLE);
                mFailTextView.setVisibility(View.GONE);
                break;
            case STATE_FAIL:
                mProgressBar.setVisibility(View.GONE);
                mRefreshView.setVisibility(View.GONE);
                mFailTextView.setVisibility(View.VISIBLE);
                break;
            default:
        }

        mState = state;
    }

    public int getState() {
        return mState;
    }


    public void refreshComplete() {
        //mHeaderTimeView.setText(friendlyTime(new Date()));
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    public void refreshFail() {
        //mHeaderTimeView.setText(friendlyTime(new Date()));
        setState(STATE_FAIL);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    private void setVisibleHeight(int height) {
        if (height < mMinVisiableHeight) {
            height = mMinVisiableHeight;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.height;
    }

    public int getOriginalHeight(){
        return mMeasuredHeight;
    }

    public int getMinVisiableHeight(){
        return mMinVisiableHeight;
    }
    public void onMove(float delta) {
        //LogUtils.e("onMove","heiht1 = "+getVisibleHeight()+",2 = "+delta);
        if (getVisibleHeight() > mMinVisiableHeight || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }


    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
//        if (height == 0) // not visible.
//            isOnRefresh = false;

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        int destHeight = mMinVisiableHeight; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    private void reset() {
        smoothScrollTo(mMinVisiableHeight);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    public void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

//    public static String friendlyTime(Date time) {
//        //获取time距离当前的秒数
//        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);
//
//        if (ct == 0) {
//            return "刚刚";
//        }
//
//        if (ct > 0 && ct < 60) {
//            return ct + "秒前";
//        }
//
//        if (ct >= 60 && ct < 3600) {
//            return Math.max(ct / 60, 1) + "分钟前";
//        }
//        if (ct >= 3600 && ct < 86400)
//            return ct / 3600 + "小时前";
//        if (ct >= 86400 && ct < 2592000) { //86400 * 30
//            int day = ct / 86400;
//            return day + "天前";
//        }
//        if (ct >= 2592000 && ct < 31104000) { //86400 * 30
//            return ct / 2592000 + "月前";
//        }
//        return ct / 31104000 + "年前";
//    }

}