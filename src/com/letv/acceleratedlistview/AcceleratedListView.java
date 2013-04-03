package com.letv.acceleratedlistview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

public class AcceleratedListView extends ListView {
    private long startTime = 0L;
    private long endTime = 0L;
    private boolean isOnGainFocus;
    private FastScrollState mFastScrollState;
    private FastScrollListener mFastScrollListener;
    private FastScrollMode mFastScrollMode;

    enum FastScrollState {
        None, Start, Stop
    }

    enum FastScrollMode {
        Normal, // normal speed
        LinearTime, // speedup when time passed
        LinearTime15X // 1.5x Linear speed
    }

    public AcceleratedListView(Context context) {
        super(context, null);
        mFastScrollState = FastScrollState.None;
        mFastScrollMode = FastScrollMode.Normal;
        isOnGainFocus = false;
    }

    public AcceleratedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFastScrollState = FastScrollState.None;
        mFastScrollMode = FastScrollMode.Normal;
        isOnGainFocus = false;
    }

    public AcceleratedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFastScrollState = FastScrollState.None;
        mFastScrollMode = FastScrollMode.Normal;
        isOnGainFocus = false;
    }

    public void registerFastScrollListener(FastScrollListener fastScrollListener) {
        mFastScrollListener = fastScrollListener;
    }

    public void unRegisterFastScrollListener() {
        mFastScrollListener = null;
    }

    public void setFastScrollMode(FastScrollMode mode) {
        mFastScrollMode = mode;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            isOnGainFocus = true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("BTTest", "dispatchKeyEvent event action = " + event.getAction()
                + " event keycode = " + event.getKeyCode());
        View focusedView = getFocusedChild();
        boolean focused = (focusedView != null) || isFocused();
        if (focused
                && event.getAction() == KeyEvent.ACTION_DOWN
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event
                        .getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)) {
            // ... and our focused child didn't handle it
            // ... give it to ourselves so we can scroll if necessary
            int repeatCount = event.getRepeatCount();
            if(0 == repeatCount || isOnGainFocus){
                startTime = System.currentTimeMillis();
                isOnGainFocus = false;
            } else if (repeatCount > 1) {
                endTime = System.currentTimeMillis();
                long timePassed = 0L;
                long slidePcs = 0L;

                // The key, calculate the factor
                switch (mFastScrollMode) {
                case Normal:
                    slidePcs = 1;
                    break;
                case LinearTime:
                    timePassed = (long) ((endTime - startTime) / 1000);
                    slidePcs = (timePassed > 0 ? timePassed : timePassed + 1);
                    break;
                case LinearTime15X:
                    timePassed = (long) ((endTime - startTime) / 1000 * 2);
                    slidePcs = (timePassed > 0 ? timePassed : timePassed + 1);
                    break;
                default:
                    break;
                }

                Log.d("BTTest", "slide " + slidePcs + " items");
                if (slidePcs > 1 && mFastScrollState != FastScrollState.Start) {
                    Log.d("BTTest", "start fastscroll");
                    mFastScrollState = FastScrollState.Start;
                    if (null != mFastScrollListener) {
                        mFastScrollListener.onFastScrollStart();
                    }
                }

                if (slidePcs > 1) {
                    for (int i = 0; i < slidePcs; i++) {
                        super.onKeyDown(event.getKeyCode(), event);
                    }
                }
            }

        } else if (event.getAction() == KeyEvent.ACTION_UP
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event
                        .getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)) {
            if (mFastScrollState == FastScrollState.Start) {
                mFastScrollState = FastScrollState.Stop;
                Log.d("BTTest", "stop fastscroll");
                if (null != mFastScrollListener) {
                    mFastScrollListener.onFastScrollStop();
                }
            }
        }
        // Dispatch in the normal way
        return super.dispatchKeyEvent(event);
    }

    interface FastScrollListener {
        public void onFastScrollStart();

        public void onFastScrollStop();
    }
}
