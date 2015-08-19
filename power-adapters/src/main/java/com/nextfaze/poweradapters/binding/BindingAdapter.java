package com.nextfaze.poweradapters.binding;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.nextfaze.poweradapters.AbstractPowerAdapter;
import com.nextfaze.poweradapters.Holder;
import com.nextfaze.poweradapters.ViewType;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Accessors(prefix = "m")
public abstract class BindingAdapter extends AbstractPowerAdapter {

    @NonNull
    private final Map<ViewType, Binder> mBinders;

    @NonNull
    private final Map<Binder, ViewType> mViewTypes;

    @NonNull
    private final Mapper mMapper;

    public BindingAdapter(@NonNull Mapper mapper) {
        Collection<? extends Binder> allBinders = mapper.getAllBinders();
        mBinders = new HashMap<>(allBinders.size());
        mViewTypes = new HashMap<>(allBinders.size());
        for (Binder binder : allBinders) {
            ViewType viewType = new ViewType();
            mBinders.put(viewType, binder);
            mViewTypes.put(binder, viewType);
        }
        mMapper = mapper;
    }

    @NonNull
    protected abstract Object getItem(int position);

    @NonNull
    @Override
    public final View newView(@NonNull ViewGroup parent, @NonNull ViewType viewType) {
        Binder binder = mBinders.get(viewType);
        return binder.newView(parent);
    }

    @Override
    public final void bindView(@NonNull View view, @NonNull Holder holder) {
        int position = holder.getPosition();
        Object item = getItem(position);
        Binder binder = mMapper.getBinder(item, position);
        if (binder != null) {
            binder.bindView(item, view, holder);
        }
    }

    @NonNull
    @Override
    public final ViewType getItemViewType(int position) {
        Object item = getItem(position);
        Binder binder = mMapper.getBinder(item, position);
        assertBinder(binder, position, item);
        return mViewTypes.get(binder);
    }

    @Override
    public final boolean isEnabled(int position) {
        Object item = getItem(position);
        Binder binder = mMapper.getBinder(item, position);
        assertBinder(binder, position, item);
        return binder.isEnabled(position);
    }

    private void assertBinder(@Nullable Binder binder, int position, Object item) {
        if (binder == null) {
            throw new AssertionError("No binder for position " + position + ", item " + item);
        }
    }
}
