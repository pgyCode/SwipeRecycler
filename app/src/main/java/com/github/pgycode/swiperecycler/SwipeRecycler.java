package com.github.pgycode.swiperecycler;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeRecycler extends RecyclerView {

    //移动距离
    public int distance = 0;

    //是否在拖动
    public boolean isSwipe = false;

    //是否需要向右
    private boolean toRight = false;

    //是否需要向左
    private boolean toLeft = false;

    //是否在滚动
    private boolean isSroll = false;

    //开始的x
    private float startx;

    //开始的y
    private float starty;

    //现在的
    private int add = -1;

    //多出尺寸
    private int exSize;

    private LinearLayoutManager layoutManager;
    private Context context;

    public SwipeRecycler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        this.context = context;

        //确认滚动停止
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_IDLE){
                    isSroll = false;
                }
            }
        });
    }


    /**
     * 返回 false 不作处理 但是也不禁止它上传
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE){
            int movex = (int) (e.getX() - startx);
            int movey = (int) (e.getY() - starty);

            //滑动范围过小，不作用于view
            if (Math.abs(movey) < 50 && Math.abs(movex) < 50) {
                return false;
            }
            if (isSroll){
                return super.onTouchEvent(e);
            }
            if (isSwipe && toLeft){
                distance = movex;
                move();
            }

            if (isSwipe && toRight){
                distance = -exSize + movex;
                move();
            }
            return false;
        }
        if (isSwipe){
            return false;
        }
        return super.onTouchEvent(e);
    }

    private void move(){
        if (distance > 0){
            distance = 0;
        } if (distance < -exSize){
            distance = -exSize;
        }
        if (add != -1) {
            View view = findViewHolderForAdapterPosition(add).itemView;
            setPaddingLeft(view, distance);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isSwipe || isSroll){
            return true;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        /**
         * 按下事件
         * 只记录按下之后的坐标
         */
        if (e.getAction() == MotionEvent.ACTION_DOWN){
            startx = e.getX();
            starty = e.getY();
        }

        /**
         * 移动事件
         */
        else if (e.getAction() == MotionEvent.ACTION_MOVE){
            int movex = (int) (e.getX() - startx);
            int movey = (int) (e.getY() - starty);

            if (!isSwipe && !isSroll && Math.abs(movex) > 50){
                isSwipe = true;
                //向右 在外面
                if (movex > 0 && distance < 0){
                    toRight = true;
                    toLeft = false;
                }
                //向左 在里面
                else if (movex < 0 && distance == 0){
                    int temp = getPosition(e);
                    if (temp != -1){
                        add = temp;
                        exSize = findViewHolderForAdapterPosition(add).itemView.getWidth() - getWidth();
                        toRight = false;
                        toLeft = true;
                    }
                }
                //向左 在外面
                else if (movex < 0 && distance < 0){
                    int temp = getPosition(e);
                    if (temp != -1 && temp != add) {
                        playLastBack();
                        add = temp;
                        exSize = findViewHolderForAdapterPosition(add).itemView.getWidth() - getWidth();
                        toRight = false;
                        toLeft = true;
                    }
                } else {
                    toRight = false;
                    toLeft = false;
                }
            } else if (!isSroll && !isSwipe && Math.abs(movey) > 50){
                isSroll = true;
                closeEx();
            }
            System.out.println(isSroll + " " + isSwipe + " " + toLeft + " " + toRight + " " + distance + " " + movex + " " + movey);
        } else  {
            if (toRight){
                if (distance > -2 * exSize / 3){
                    playBack(distance, 0);
                } else {
                    playBack(distance, -exSize);
                }
            }
            if (toLeft){
                if (distance < -exSize / 3){
                    playBack(distance, -exSize);
                } else {
                    playBack(distance, 0);
                }
            }
            else{
                initData();
            }
        }
        return super.dispatchTouchEvent(e);
    }

    private void playBack(int start, final int end){
        final ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(Math.abs(end - start) / 3);
        final View view = findViewHolderForAdapterPosition(add).itemView;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (add != -1) {
                    setPaddingLeft(view, (Integer) valueAnimator.getAnimatedValue());
                    distance = (int) valueAnimator.getAnimatedValue();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (end == 0){
                    initData();
                    add = -1;
                } else {
                    initData();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }


    public void playLastBack(){
        final View view = findViewHolderForAdapterPosition(add).itemView;
        if (view != null) {
            final ValueAnimator animator = ValueAnimator.ofInt(-exSize, 0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setPaddingLeft(view, (Integer) valueAnimator.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    private int getPosition(MotionEvent e){
        int f = layoutManager.findFirstVisibleItemPosition();
        int l = layoutManager.findLastVisibleItemPosition();
        for (int i = f; i <= l; i++){
            View view = findViewHolderForAdapterPosition(i).itemView;
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            if (e.getY() < location[1] && e.getY() > location[1] - view.getHeight() && e.getX() > location[0] && e.getX() < location[0] + view.getWidth()) {
                return i;
            }
        }
        return -1;
    }

    private void initData(){
        isSwipe = false;
        startx = 0;
        starty = 0;
        toRight = false;
        toLeft = false;
    }


    public void closeEx(){
        ViewHolder viewHolder = findViewHolderForAdapterPosition(add);
        if (viewHolder != null){
            View view = viewHolder.itemView;
            add = -1;
            distance = 0;
            view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        }
    }


    public int getScreenWidth(){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /*
     * dp转换成px
     */
    public int dp2px(int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }


    private void setPaddingLeft(View view, int left){
        view.setPadding(left, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    }
}
