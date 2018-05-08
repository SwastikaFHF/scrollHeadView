package com.aitangba.testproject.tracktask;

import android.view.View;

import java.util.Iterator;
import java.util.LinkedList;

public class ViewTracked {

    private final LinkedList<ViewElement> mViewElements = new LinkedList<>();

    public static ViewTracked getInstance() {
        return SingletonHandler.instance;
    }

    public void registerTask(View view, TrackedAsyncTask task) {
        for(ViewElement viewElement : mViewElements) {
            if(viewElement.mView == view) {
                viewElement.add(task);
                return;
            }
        }

        ViewElement viewElement = new ViewElement(view);
        viewElement.add(task);
        mViewElements.add(viewElement);
    }

    public void unregisterTask(TrackedAsyncTask task) {
        for(ViewElement viewElement : mViewElements) {
            if(viewElement.mView == task.getRootView()) {
                viewElement.remove(task);
                return;
            }
        }
    }

    //内部类
    private static final class SingletonHandler{
        private static ViewTracked instance = new ViewTracked();
    }

    private class ViewElement implements View.OnAttachStateChangeListener {
        private View mView;
        private final LinkedList<TrackedAsyncTask<?, ?, ?, ?>> mTasks = new LinkedList<>();

        public ViewElement(View view) {
            mView = view;
            mView.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onViewAttachedToWindow(View v) {

        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            cancelAll();
            mViewElements.remove(this);
        }

        private void add(TrackedAsyncTask<?, ?, ?, ?> task) {
            synchronized (mTasks) {
                mTasks.add(task);
            }
        }

        private void remove(TrackedAsyncTask<?, ?, ?, ?> task) {
            synchronized (mTasks) {
                task.cancel(true);
                mTasks.remove(task);
            }
            if(mTasks.size() == 0) {
                mViewElements.remove(this);
            }
        }

        /**
         * Cancel all registered tasks.
         */
        public void cancelAll() {
            synchronized (mTasks) {
                Iterator<TrackedAsyncTask<?, ?, ?, ?>> iterator = mTasks.iterator();
                while (iterator.hasNext()) {
                    TrackedAsyncTask<?, ?, ?, ?> task = iterator.next();
                    task.cancelWithoutCallback();
                    iterator.remove();
                }
            }
        }
    }
}
