package com.ln.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.WrapperListAdapter;

import com.ln.base.R;
import com.ln.base.activity.BaseActivity;
import com.ln.base.network.Config;
import com.ln.base.network.ListViewUiHandler;
import com.ln.base.network.PageRspInterface;
import com.ln.base.network.RequestUiHandler;
import com.ln.base.tool.DimenUtils;
import com.ln.base.tool.ViewAnimator;

public class ListView extends android.widget.ListView implements OnScrollListener, ListViewUiHandler {

    public static final int SCROLL_DOWN = 0x0;
    public static final int SCROLL_UP = 0x1;
    private View mEmptyView;
    private String mEmptyViewTips;
    private Drawable mEmptyViewIcon;
    private String mHintText;
    private Paint mHintTextPaint;
    private float mHintTextX, mHintTextY;
    private PullRefreshIndicator mPullRefreshIndicator;
    private OnPullRefreshListener mPullRefreshListener;
    private int mTouchSlop;
    private int mPullOffset;
    private float mPrevDownY;
    private boolean mPreventClick;
    private boolean mIsRefreshing;
    private boolean mIsShowFirstLoading;
    private View mTopTipView;
    private ViewAnimator mTopTipViewAnimator;
    private RequestUiHandler firstLoadUiHandler;
    private LoadMoreIndicator mLoadMoreIndicator;
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mHasMore, mIsLoading;
    private OnScrollListener mScrollListener;
    private OnScrollPlusListener mScrollPlusListener;
    private int mFirstVisibleItem;
    private android.widget.ListAdapter mListAdapter;

    public ListView(Context context) {
        this(context, null);
    }

    public ListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    @SuppressLint("NewApi")
    public ListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListView, defStyle, 0);
        mHintText = typedArray.getString(R.styleable.ListView_android_hint);
        if (mHintText != null) {
            mHintTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mHintTextPaint.setTextAlign(Align.CENTER);
            mHintTextPaint.setColor(typedArray.getColor(R.styleable.ListView_android_textColorHint, Color.BLACK));
            mHintTextPaint.setTextSize(typedArray.getDimensionPixelSize(R.styleable.ListView_textSizeHint,
                    DimenUtils.sp2px(context, 16)));
        }
        mEmptyViewTips = typedArray.getString(R.styleable.ListView_emptyViewTip);
        mEmptyViewIcon = typedArray.getDrawable(R.styleable.ListView_emptyViewIcon);

        typedArray.recycle();

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        super.setOnScrollListener(this);
        if (Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHintTextPaint != null) {
            final FontMetrics fontMetrics = mHintTextPaint.getFontMetrics();
            final float offset = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            mHintTextX = w >> 1;
            mHintTextY = (h >> 1) + offset;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (getAdapter() == null) {
            return;
        }
        if (getAdapter().getCount() > (getHeaderViewsCount() + getFooterViewsCount())) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.GONE);
            }
        } else {
            if (mPullOffset > mTouchSlop || isRefreshing()) {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.GONE);
                }
            } else {
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(View.VISIBLE);
                } else if (mHintText != null) {
                    canvas.drawText(mHintText, mHintTextX, mHintTextY, mHintTextPaint);
                }
            }
        }
    }

    @Override
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    public void setEmptyViewRes(int resIdOfTips, int resIdOfIcon) {
        setEmptyViewRes(getContext().getString(resIdOfTips), resIdOfIcon);
    }

    public void setEmptyViewRes(String tips, int resIdOfIcon) {
        mEmptyViewTips = tips;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEmptyViewIcon = getContext().getDrawable(resIdOfIcon);
        } else {
            mEmptyViewIcon = getContext().getResources().getDrawable(resIdOfIcon);
        }
    }

    public void setFirstLoadUiHandler(RequestUiHandler uiHandler) {
        this.firstLoadUiHandler = uiHandler;
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        invalidate();
    }

    public void setFirstLoadIndicator() {
        setPullRefreshEnabled(new DefaultPullRefreshIndicator(getContext()), null, true);
    }

    public void setFirstLoadIndicator(PullRefreshIndicator pullRefreshIndicator) {
        setPullRefreshEnabled(pullRefreshIndicator, null, true);
    }

    /**
     * 该方法会调用addHeaderView方法，存在兼容性问题，在4.3以前，必须要先addHeaderView，之后再调用setAdapter，否则会crash；
     * 暂时对该方法做了兼容处理，不过在使用时也尽量保证在setAdapter之前调用；
     *
     * @param listener
     */
    public void setPullRefreshEnabled(OnPullRefreshListener listener) {
//		setPullRefreshEnabled(new DefaultPullRefreshIndicator(getContext()), listener, false);
        android.widget.ListAdapter tempBaseAdapter = null;
        if (getAdapter() != null) {
            tempBaseAdapter = getAdapter();
            setAdapter(null);
        }
        setPullRefreshEnabled(new DefaultPullRefreshIndicator(getContext()), listener, true);
        if (tempBaseAdapter != null) {
            setAdapter(tempBaseAdapter);
        }
    }

    public void setPullRefreshEnabled(OnPullRefreshListener listener, boolean isShowFirstLoading) {
        setPullRefreshEnabled(new DefaultPullRefreshIndicator(getContext()), listener, isShowFirstLoading);
    }

    public void setPullRefreshEnabled(PullRefreshIndicator pullRefreshIndicator, OnPullRefreshListener listener) {
//		setPullRefreshEnabled(pullRefreshIndicator, listener, false);
        setPullRefreshEnabled(pullRefreshIndicator, listener, true);
    }

    public void setTopTipView(View topTipView, ViewAnimator viewAnimator) {
        mTopTipView = topTipView;
        mTopTipViewAnimator = viewAnimator;
    }

    public View getTopTipView() {
        return mTopTipView;
    }

    public void setTopTipView(View topTipView) {
        setTopTipView(topTipView, null);
    }

    public void setPullRefreshEnabled(PullRefreshIndicator pullRefreshIndicator, OnPullRefreshListener listener,
                                      boolean isShowFirstLoading) {
        if (pullRefreshIndicator != null) {
            if (mPullRefreshIndicator != null) {
                removeHeaderView(mPullRefreshIndicator);
            }
            mPullRefreshIndicator = pullRefreshIndicator;
            addHeaderView(mPullRefreshIndicator, null, false);
        }
        mPullRefreshListener = listener;
        mIsShowFirstLoading = isShowFirstLoading;

        if (getContext() instanceof BaseActivity) {
            setEmptyView(((BaseActivity) getContext()).getLoadingView());
        }
    }

    public void setLoadMoreEnabled(OnLoadMoreListener listener) {
        setLoadMoreEnabled(new DefaultLoadMoreIndicator(getContext()), listener);
    }

    public void setLoadMoreEnabled(LoadMoreIndicator loadMoreIndicator, OnLoadMoreListener listener) {
        if (loadMoreIndicator != null && listener != null) {
            mLoadMoreIndicator = loadMoreIndicator;
            mLoadMoreListener = listener;
            addFooterView(mLoadMoreIndicator, null, false);
        }
    }

    // 如果下拉刷新已经完成
    public void onPullRefreshComplete(boolean isSuccess, String msg) {
        mIsRefreshing = false;
        if (mPullRefreshIndicator != null)
            mPullRefreshIndicator.onRefreshComplete(isSuccess, msg);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListener = listener;
    }

    public void setOnScrollPlusListener(OnScrollPlusListener listener) {
        mScrollPlusListener = listener;
    }

    // 如果加载更多已经完成
    public void onLoadMoreComplete(boolean hasMore) {
        onLoadMoreComplete(hasMore, null);
    }

    private void onLoadMoreComplete(boolean hasMore, String msg) {
        mIsLoading = false;
        mHasMore = hasMore;
        if (mLoadMoreIndicator != null)
            mLoadMoreIndicator.onLoadMoreComplete();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pullRefreshAble()) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mPrevDownY = event.getY();
                    mPullOffset = 0;
                    mPreventClick = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float eventY = event.getY();
                    float yDiff = eventY - mPrevDownY;
                    mPullOffset += yDiff > 0 ? yDiff / 2 : yDiff;
                    if (mPullOffset < 0) {
                        mPullOffset = 0;
                    }
                    mPrevDownY = eventY;
                    mPullRefreshIndicator.onPulling(mPullOffset);
                    if (mPreventClick) {
                        if (mPullOffset > 0) {
                            return true;
                        }
                    } else if (mPullOffset >= mTouchSlop) {
                        mPreventClick = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (mPullRefreshIndicator.onRelease()) {
                        mIsRefreshing = true;
                        mPullRefreshListener.onPullRefresh();
                    }
                    mPullOffset = 0;
            }
        }
        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        }
    }

    private boolean pullRefreshAble() {
        return mPullRefreshListener != null
                && !mIsRefreshing
                && !mIsLoading
                && (getChildCount() == 0 || (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= getPaddingTop()));
    }

    @Override
    public void setAdapter(android.widget.ListAdapter adapter) {
        mListAdapter = adapter;
        super.setAdapter(adapter);
    }

    /**
     * Returns the adapter passed to {@link #setAdapter(android.widget.ListAdapter)} but
     * might be a {@link WrapperListAdapter} if the caller ListView is extends this ListView.
     *
     * @return The original adapter of this ListView,but might be a null if not set
     * @see #setAdapter(android.widget.ListAdapter)
     */
    public android.widget.ListAdapter getOriginalAdapter() {
        return mListAdapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        if (mLoadMoreListener != null && !mIsRefreshing && !mIsLoading && mHasMore
                && (firstVisibleItem + visibleItemCount == totalItemCount)
                && (totalItemCount > getHeaderViewsCount() + getFooterViewsCount())) {
            mIsLoading = true;
            mLoadMoreIndicator.onLoadMoreStart();
            mLoadMoreListener.onLoadMore();
        }
        if (mScrollPlusListener != null && mFirstVisibleItem != firstVisibleItem) {
            if (firstVisibleItem > 0 && firstVisibleItem > mFirstVisibleItem) {
                mScrollPlusListener.onScrolled(SCROLL_DOWN, mFirstVisibleItem, firstVisibleItem);
                if (mTopTipViewAnimator != null) {
                    mTopTipViewAnimator.showView();
                } else if (mTopTipView != null) {
                    mTopTipView.setVisibility(VISIBLE);
                }
            } else {
                mScrollPlusListener.onScrolled(SCROLL_UP, mFirstVisibleItem, firstVisibleItem);
                if (mTopTipViewAnimator != null) {
                    mTopTipViewAnimator.hideView();
                } else if (mTopTipView != null) {
                    mTopTipView.setVisibility(GONE);
                }
            }
            mFirstVisibleItem = firstVisibleItem;
        }
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public void onStart(String hint) {
        if ((getCount() - getHeaderViewsCount() - getFooterViewsCount()) == 0) {
            /**
             *firstLoadUiHandler has high priority
             */
            if (firstLoadUiHandler != null) {
                firstLoadUiHandler.onStart(hint);
            } else {
                /**
                 * when the list children is empty，if onStart() triggered by
                 * manual(pull)，isRefreshing() = true
                 */
                if (!isRefreshing() && mIsShowFirstLoading) {
                    mIsRefreshing = true;
                    mPullRefreshIndicator.onLoading();
                }
            }
        }
    }

    @Override
    public void onError(int errCode, String errMsg) {
        /**
         *firstLoadUiHandler has high priority
         */
        if ((getCount() - getHeaderViewsCount() - getFooterViewsCount()) == 0 && firstLoadUiHandler != null) {
            firstLoadUiHandler.onError(errCode, errMsg);
            return;
        }
        //on refresh data
        if (isRefreshing()) {
            onPullRefreshComplete(false, errMsg);
            if (mEmptyView != null) {
                //update empty view res
                if (mEmptyView instanceof LoadingView) {
                    ((LoadingView) mEmptyView).show(errMsg, R.drawable.im_error);
                }
            } else {
                //if has not empty,show toast
                if (getContext() instanceof BaseActivity) {
                    ((BaseActivity) getContext()).getToastDialog().showToast(errMsg);
                }
            }
        }
        //on load more data
        else {
            onLoadMoreComplete(true, errMsg);
            if (getContext() instanceof BaseActivity) {
                ((BaseActivity) getContext()).getToastDialog().showToast(errMsg);
            }
        }
    }

    @Override
    public void onSuccess() {
        /**
         *firstLoadUiHandler has high priority
         */
        if (firstLoadUiHandler != null && (getCount() - getHeaderViewsCount() - getFooterViewsCount()) == 0) {
            firstLoadUiHandler.onSuccess();
            return;
        }
        if (isRefreshing()) {
            onPullRefreshComplete(true, null);
            //update empty view res
            if (mEmptyView != null && mEmptyView instanceof LoadingView) {
                ((LoadingView) mEmptyView).show(mEmptyViewTips, mEmptyViewIcon);
            }
        }
    }

    @Override
    public void onRspSuccess(PageRspInterface<?> rsp, int pageNum, int pageSize) {
        android.widget.ListAdapter adapter = getOriginalAdapter();
        // normal case,the adapter is not a WrapperListAdapter object
        while (adapter instanceof WrapperListAdapter) {
            adapter = ((WrapperListAdapter) adapter).getWrappedAdapter();
        }
        if (adapter instanceof ListAdapter) {
            @SuppressWarnings("unchecked")
            ListAdapter<View, ?> listAdapter = (ListAdapter<View, ?>) adapter;
            if (pageNum == Config.LIST_REQ_PAGE_START_NUM)// refresh
            {
                listAdapter.setDataList(rsp.getList());

            } else
            // load more
            {
                listAdapter.addDataList(rsp.getList());
            }
        }
        onLoadMoreComplete(rsp.getList() != null && rsp.getList().size() == pageSize);
    }

    public interface OnPullRefreshListener {
        void onPullRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnScrollPlusListener {
        /**
         * </br>
         * <ul>
         * <li><b>preFirstVisibleItem</b> may equal <b>curFirstVisibleItem</b></li>
         * <li>if <b>state == SCROLL_DOWN</b>,means scroll down; if <b>state == SCROLL_UP</b>,means scroll up;</li>
         * <ul/>
         **/
        void onScrolled(int state, int preFirstVisibleItem, int curFirstVisibleItem);
    }
}