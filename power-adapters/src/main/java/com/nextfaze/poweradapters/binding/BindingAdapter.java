package com.nextfaze.poweradapters.binding;

import android.view.View;
import android.view.ViewGroup;
import com.nextfaze.poweradapters.Container;
import com.nextfaze.poweradapters.Holder;
import com.nextfaze.poweradapters.PowerAdapter;
import lombok.NonNull;

public abstract class BindingAdapter<T> extends PowerAdapter {

    @NonNull
    private final BindingEngine<T> mEngine;

    protected BindingAdapter(@NonNull Mapper mapper) {
        mEngine = new BindingEngine<>(mapper, new ItemAccessor<T>() {
            @NonNull
            @Override
            public T get(int position) {
                return getItem(position);
            }
        });
    }

    @NonNull
    protected abstract T getItem(int position);

    @NonNull
    @Override
    public final View newView(@NonNull ViewGroup parent, @NonNull Object viewType) {
        return mEngine.newView(parent, viewType);
    }

    @Override
    public final void bindView(@NonNull Container container, @NonNull View view, @NonNull Holder holder) {
        mEngine.bindView(container, view, holder);
    }

    @NonNull
    @Override
    public final Object getItemViewType(int position) {
        return mEngine.getItemViewType(position);
    }

    @Override
    public final boolean isEnabled(int position) {
        return mEngine.isEnabled(position);
    }

    @Override
    public final long getItemId(int position) {
        return mEngine.getItemId(position);
    }

    @Override
    public final boolean hasStableIds() {
        return mEngine.hasStableIds();
    }
}
