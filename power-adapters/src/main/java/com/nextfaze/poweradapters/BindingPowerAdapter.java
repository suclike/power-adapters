package com.nextfaze.poweradapters;

import android.view.View;
import android.view.ViewGroup;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.IdentityHashMap;

@Accessors(prefix = "m")
public abstract class BindingPowerAdapter extends AbstractPowerAdapter {

    @NonNull
    private final ArrayList<Binder> mBinders;

    @NonNull
    private final IdentityHashMap<Binder, Integer> mIndexes = new IdentityHashMap<Binder, Integer>();

    @NonNull
    private final Mapper mMapper;

    public BindingPowerAdapter(@NonNull Mapper mapper) {
        mBinders = new ArrayList<Binder>(mapper.getAllBinders());
        mMapper = mapper;
    }

    @NonNull
    protected abstract Object getItem(int position);

    @NonNull
    @Override
    public final View newView(@NonNull ViewGroup parent, int itemViewType) {
        Binder binder = mBinders.get(itemViewType - super.getViewTypeCount());
        return binder.newView(parent);
    }

    @Override
    public final void bindView(@NonNull View view, int position) {
        Object item = getItem(position);
        Binder binder = mMapper.getBinder(item, position);
        if (binder != null) {
            binder.bindView(item, view, position);
        }
    }

    @Override
    public final int getViewTypeCount() {
        return super.getViewTypeCount() + mBinders.size();
    }

    @Override
    public final int getItemViewType(int position) {
        Object item = getItem(position);
        Binder binder = mMapper.getBinder(item, position);
        if (binder != null) {
            // Cache index of each binder to avoid linear search each time.
            Integer index = mIndexes.get(binder);
            if (index == null) {
                index = mBinders.indexOf(binder);
                mIndexes.put(binder, index);
            }
            // Offset type by inner type count, otherwise we could get collisions.
            return super.getViewTypeCount() + index;
        }
        throw new AssertionError();
    }
}
