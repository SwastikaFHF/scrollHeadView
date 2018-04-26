package com.aitangba.testproject.tracktask;

import java.util.Iterator;
import java.util.LinkedList;

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

    private final Tracker mTracker;
    private final InnerTask<Params, Progress, Error, Result> mInnerTask;
    private Object mTag;

    /**
     * Construction with what create new instances can be canceled by Tracker.
     *
     * @param tracker can retrieve instance like : <p>
     *                TrackedAsyncTask.Tracke mTracke = new TrackedAsyncTask.Tracke();
     */
    public TrackedAsyncTask(Tracker tracker) {
        mTracker = tracker;
        if (mTracker != null) {
            mTracker.add(this);
        }
        mInnerTask = new InnerTask<>(this);
    }

    /**
     * Construction with what create new instances cannot be canceled later.
     */
    public TrackedAsyncTask() {
        mTracker = null;
        mInnerTask = new InnerTask<>(this);
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    private void unregisterSelf() {
        if (mTracker != null) {
            mTracker.remove(this);
        }
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
    private final TrackedAsyncTask<Params, Progress, Error, Result> executeParallel(Params... params) {
        if (mTracker == null) {
            throw new IllegalStateException();
        }
        mInnerTask.executeParallel(params);
        return this;
    }

    @SafeVarargs
    private final TrackedAsyncTask<Params, Progress, Error, Result> executeSerial(Params... params) {
        if (mTracker == null) {
            throw new IllegalStateException();
        }
        mInnerTask.executeSerial(params);
        return this;
    }

    /**
     * Tracks {@link TrackedAsyncTask}.
     * <p>
     * Call {@link #cancelAll()} to cancel all tasks registered.
     */
    public static class Tracker {
        private final LinkedList<TrackedAsyncTask<?, ?, ?, ?>> mTasks = new LinkedList<>();

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
        }

        /**
         * Cancel all registered tasks.
         */
        public void cancelAll() {
            removeByTag(null);
        }

        public void removeByTag(Object obj) {
            synchronized (mTasks) {
                Iterator<TrackedAsyncTask<?, ?, ?, ?>> iterator = mTasks.iterator();
                while (iterator.hasNext()) {
                    TrackedAsyncTask<?, ?, ?, ?> task = iterator.next();
                    if (obj == null || obj.equals(task.mTag)) {
                        task.cancelWithoutCallback();
                        iterator.remove();
                    }
                }
            }
        }

        /**
         * Return remaining tasks count which still not been executed.
         */
        public int getRemainingTaskCount() {
            return mTasks.size();
        }

        /**
         * Check specified task whether still not been executed.
         */
        public boolean containsTask(TrackedAsyncTask<?, ?, ?, ?> task) {
            return mTasks.contains(task);
        }
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
            mOwner.onError(values[0]);
        }

        @Override
        protected void onCancelled() {
            mOwner.unregisterSelf();
            // if error occurred task also will be canceled,
            // so onCancelled() will be called only when error never occurred in that task
            mOwner.onCancelled();
        }

        @Override
        public void onPostExecute(Result2 result) {
            mOwner.unregisterSelf();
            mOwner.onSuccess(result);
        }
    }
}
