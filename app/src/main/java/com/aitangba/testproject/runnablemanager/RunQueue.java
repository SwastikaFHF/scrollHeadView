package com.aitangba.testproject.runnablemanager;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by fhf11991 on 2017/10/31.
 */

public class RunQueue {

    private final ArrayList<HandlerAction> mActions = new ArrayList<HandlerAction>();

    private View mRootView;

    public RunQueue(@NonNull View mRootView) {
        this.mRootView = mRootView;
    }

    void post(Runnable action) {
        postDelayed(action, 0);
    }

    void postDelayed(Runnable action, long delayMillis) {
        HandlerAction handlerAction = new HandlerAction();
        handlerAction.action = action;
        handlerAction.delay = delayMillis;

        synchronized (mActions) {
            mActions.add(handlerAction);
        }
    }

    void removeCallbacks(Runnable action) {
        final HandlerAction handlerAction = new HandlerAction();
        handlerAction.action = action;

        synchronized (mActions) {
            final ArrayList<HandlerAction> actions = mActions;

            while (actions.remove(handlerAction)) {
                // Keep going
            }
        }
    }

    void executeActions(Handler handler) {
        synchronized (mActions) {
            final ArrayList<HandlerAction> actions = mActions;
            final int count = actions.size();

            for (int i = 0; i < count; i++) {
                final HandlerAction handlerAction = actions.get(i);
                handler.postDelayed(handlerAction.action, handlerAction.delay);
            }

            actions.clear();
        }
    }

    private static class HandlerAction {
        Runnable action;
        long delay;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            HandlerAction that = (HandlerAction) o;
            return !(action != null ? !action.equals(that.action) : that.action != null);

        }

        @Override
        public int hashCode() {
            int result = action != null ? action.hashCode() : 0;
            result = 31 * result + (int) (delay ^ (delay >>> 32));
            return result;
        }
    }
}
