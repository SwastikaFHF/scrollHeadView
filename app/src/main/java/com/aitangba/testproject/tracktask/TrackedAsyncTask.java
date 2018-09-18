package com.aitangba.testproject.tracktask;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Modeled after {@link AsyncTask}; the basic usage is the same, with extra features:
 * - Bulk cancellation of multiple tasks.  This is mainly used by UI to cancel pending tasks
 * in onDestroy() or similar places.
 * - Instead of {@link AsyncTask#onPostExecute}, it has {@link #onSuccess(Object)} and {@link #onError(Object)}as the
 * regular {@link AsyncTask#onPostExecute} is a bit hard to predict when it'll be called and
 * when it won't, even cannot distinguish success and failed.
 * - Use DiscardOldestPolicy in PARALLEL_EXECUTOR to prevent from throwing RejectedExecutionException.
 * - Support SDK version from GINGERBREAD.
 */
@SuppressWarnings("hiding")
public abstract class TrackedAsyncTask<Params, Progress, Error, Result> {

    private final InnerTask<Params, Progress, Error, Result> mInnerTask;
    private View mRootView;

    /**
     * Construction with what create new instances cannot be canceled later.
     */
    public TrackedAsyncTask(View rootView) {
        mRootView = rootView;
        mInnerTask = new InnerTask<>(this);
    }

    protected View getRootView() {
        return mRootView;
    }

    private void unregisterSelf() {
        TaskTracked.getInstance().unregisterTask(this);
    }

    /**
     * @see AsyncTask#onPreExecute()
     */
    protected void onPreExecute() {
    }

    /**
     * @see AsyncTask#doInBackground
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * @see AsyncTask#onProgressUpdate
     */
    protected void onProgressUpdate(Progress value) {
    }

    /**
     * If any error occurred, {@link #onSuccess(Object)} will not be executed
     * and current task also been canceled.
     */
    protected void onError(Error error) {
    }

    /**
     * Similar to {@link AsyncTask#onPostExecute}, but this will never be executed if
     * {@link #cancel(boolean)} has been called before its execution even if
     * {@link #doInBackground(Object...)} has completed when cancelled.
     *
     * @see AsyncTask#onPostExecute
     */
    protected void onSuccess(Result result) {
    }

    /**
     * @see AsyncTask#cancel(boolean)
     */
    public void cancel(boolean mayInterruptIfRunning) {
        mInnerTask.cancel(mayInterruptIfRunning);
    }

    public void cancelWithoutCallback() {
        mInnerTask.cancelWithoutCallback();
    }

    /**
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mInnerTask.isCancelled();
    }

    /**
     * @see AsyncTask#onCancelled
     */
    protected void onCancelled() {
    }

    protected final void publishProgress(Progress... value) {
        mInnerTask.publishProgress(value);
    }

    protected final void publishError(Error... error) {
        mInnerTask.publishError(error);
    }

    @SafeVarargs
    protected final TrackedAsyncTask<Params, Progress, Error, Result> executeParallel(Params... params) {
        TaskTracked.getInstance().registerTask(mRootView, this);
        mInnerTask.executeParallel(params);
        return this;
    }

    @SafeVarargs
    protected final TrackedAsyncTask<Params, Progress, Error, Result> executeSerial(Params... params) {
        TaskTracked.getInstance().registerTask(mRootView, this);
        mInnerTask.executeSerial(params);
        return this;
    }

    private static class InnerTask<Params2, Progress2, Error2, Result2> extends AsyncTask<Params2, Progress2, Error2, Result2> {
        private final TrackedAsyncTask<Params2, Progress2, Error2, Result2> mOwner;

        public InnerTask(TrackedAsyncTask<Params2, Progress2, Error2, Result2> owner) {
            mOwner = owner;
        }

        @Override
        protected void onPreExecute() {
            mOwner.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Result2 doInBackground(Params2... params) {
            return mOwner.doInBackground(params);
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(Progress2... values) {
            mOwner.onProgressUpdate(values[0]);
        }

        @Override
        protected void onError(Error2[] values) {
            mOwner.unregisterSelf();
            if(ViewCompat.isAttachedToWindow(mOwner.mRootView)) {
                mOwner.onError(values[0]);
            }
        }

        @Override
        protected void onCancelled() {
            mOwner.unregisterSelf();
            // if error occurred task also will be canceled,
            // so onCancelled() will be called only when error never occurred in that task
            if(ViewCompat.isAttachedToWindow(mOwner.mRootView)) {
                mOwner.onCancelled();
            }
        }

        @Override
        public void onPostExecute(Result2 result) {
            mOwner.unregisterSelf();
            if(ViewCompat.isAttachedToWindow(mOwner.mRootView)) {
                mOwner.onSuccess(result);
            }
        }
    }
}
