package com.nextfaze.asyncdata;

/** Forwards fine-grained calls to {@link #onChange()} by default. */
public class SimpleDataObserver implements DataObserver {
    @Override
    public void onChange() {
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        onChange();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        onChange();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        onChange();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        onChange();
    }
}
