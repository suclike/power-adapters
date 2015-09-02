package com.nextfaze.asyncdata;

import android.support.annotation.CallSuper;
import lombok.NonNull;

public class DataWrapper<T> extends AbstractData<T> {

    @NonNull
    private final Data<? extends T> mData;

    @NonNull
    private final DataObserver mDataObserver = new SimpleDataObserver() {
        @Override
        public void onChange() {
            notifyDataChanged();
        }
    };

    @NonNull
    private final LoadingObserver mLoadingObserver = new LoadingObserver() {
        @Override
        public void onLoadingChange() {
            notifyLoadingChanged();
        }
    };

    @NonNull
    private final ErrorObserver mErrorObserver = new ErrorObserver() {
        @Override
        public void onError(@NonNull Throwable e) {
            notifyError(e);
        }
    };

    private final boolean mTakeOwnership;

    public DataWrapper(@NonNull Data<? extends T> data) {
        this(data, true);
    }

    public DataWrapper(@NonNull Data<? extends T> data, boolean takeOwnership) {
        mData = data;
        mTakeOwnership = takeOwnership;
        mData.registerDataObserver(mDataObserver);
        mData.registerLoadingObserver(mLoadingObserver);
        mData.registerErrorObserver(mErrorObserver);
    }

    @CallSuper
    @Override
    protected void onClose() throws Throwable {
        mData.unregisterDataObserver(mDataObserver);
        mData.unregisterLoadingObserver(mLoadingObserver);
        mData.unregisterErrorObserver(mErrorObserver);
        if (mTakeOwnership) {
            mData.close();
        }
    }

    @NonNull
    @Override
    public T get(int position, int flags) {
        return mData.get(position, flags);
    }

    @Override
    public int size() {
        return mData.size();
    }

    @Override
    public int available() {
        return mData.available();
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public boolean isLoading() {
        return mData.isLoading();
    }

    @Override
    public void invalidate() {
        mData.invalidate();
    }

    @Override
    public void refresh() {
        mData.refresh();
    }

    @Override
    public void reload() {
        mData.reload();
    }

    @CallSuper
    @Override
    protected void onShown(long millisHidden) {
        mData.notifyShown();
    }

    @CallSuper
    @Override
    protected void onHidden(long millisShown) {
        mData.notifyHidden();
    }
}
