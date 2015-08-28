package com.nextfaze.poweradapters.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.nextfaze.asyncdata.Data;
import com.nextfaze.asyncdata.IncrementalArrayData;
import com.nextfaze.poweradapters.EmptyAdapterBuilder;
import com.nextfaze.poweradapters.FooterAdapterBuilder;
import com.nextfaze.poweradapters.HeaderAdapterBuilder;
import com.nextfaze.poweradapters.Holder;
import com.nextfaze.poweradapters.LoadingAdapterBuilder;
import com.nextfaze.poweradapters.PowerAdapter;
import com.nextfaze.poweradapters.asyncdata.DataBindingAdapter;
import com.nextfaze.poweradapters.asyncdata.DataEmptyDelegate;
import com.nextfaze.poweradapters.asyncdata.DataLoadingDelegate;
import com.nextfaze.poweradapters.binding.Binder;
import com.nextfaze.poweradapters.binding.BinderWrapper;
import com.nextfaze.poweradapters.binding.Mapper;
import com.nextfaze.poweradapters.binding.PolymorphicMapperBuilder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.nextfaze.poweradapters.PowerAdapters.concat;
import static com.nextfaze.poweradapters.recyclerview.RecyclerPowerAdapters.toRecyclerAdapter;

public class ConcatFragment extends Fragment {

    private static final int ADAPTER_COUNT = 100;

    @NonNull
    private final List<Pair<Data<?>, PowerAdapter>> mPairs = new ArrayList<>();

    @Bind(R.id.recycler)
    RecyclerView mRecyclerView;

    public ConcatFragment() {
        Random random = new Random();
        for (int i = 0; i < ADAPTER_COUNT; i++) {
            NewsIncrementalData data = new NewsIncrementalData(random.nextInt(10), 3);
            ColoredBinder binder = new ColoredBinder(random.nextInt());
            mPairs.add(createPair(data, binder));
        }
    }

    @NonNull
    private Pair<Data<?>, PowerAdapter> createPair(@NonNull final IncrementalArrayData<?> data,
                                                   @NonNull Binder newsItemBinder) {
        Binder removeItemBinder = new BinderWrapper(newsItemBinder) {
            @Override
            public void bindView(@NonNull final Object item, @NonNull View v, @NonNull Holder holder) {
                super.bindView(item, v, holder);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.remove(item);
                    }
                });
            }
        };
        Mapper mapper = new PolymorphicMapperBuilder()
                .bind(NewsItem.class, removeItemBinder)
                .build();
        PowerAdapter adapter = new DataBindingAdapter(data, mapper);

        adapter = new HeaderAdapterBuilder()
                .addResource(R.layout.news_header_item)
                .emptyPolicy(HeaderAdapterBuilder.EmptyPolicy.HIDE)
                .build(adapter);

        adapter = new LoadingAdapterBuilder()
                .resource(R.layout.list_loading_item)
                .build(adapter, new DataLoadingDelegate(data));

        data.setLookAheadRowCount(-1);
        LoadNextAdapter loadNextAdapter = new LoadNextAdapter(adapter, data, R.layout.list_load_next_item);
        loadNextAdapter.setOnClickListener(new LoadNextAdapter.OnLoadNextClickListener() {
            @Override
            public void onClick() {
                data.loadNext();
            }
        });
        adapter = loadNextAdapter;

        adapter = new FooterAdapterBuilder()
                .addResource(R.layout.news_footer_item)
                .emptyPolicy(FooterAdapterBuilder.EmptyPolicy.HIDE)
                .build(adapter);

        adapter = new EmptyAdapterBuilder()
                .resource(R.layout.list_empty_item)
                .build(adapter, new DataEmptyDelegate(data));

        return new Pair<Data<?>, PowerAdapter>(data, adapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        for (Pair<Data<?>, PowerAdapter> pair : mPairs) {
            pair.first.close();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_recycler, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        List<PowerAdapter> adapters = FluentIterable.from(mPairs)
                .transform(new Function<Pair<Data<?>, PowerAdapter>, PowerAdapter>() {
                    @Override
                    public PowerAdapter apply(Pair<Data<?>, PowerAdapter> pair) {
                        return pair.second;
                    }
                })
                .toList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(toRecyclerAdapter(concat(adapters)));
    }

    /**
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Pair<Data<?>, PowerAdapter> pair : mPairs) {
            pair.first.notifyShown();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Pair<Data<?>, PowerAdapter> pair : mPairs) {
            pair.first.notifyHidden();
        }
    }

    static final class ColoredBinder extends NewsItemBinder {

        private final int mColor;

        ColoredBinder(int color) {
            mColor = color;
        }

        @Override
        protected void bind(@NonNull NewsItem newsItem, @NonNull TextView v, @NonNull Holder holder) {
            super.bind(newsItem, v, holder);
            v.setBackgroundColor(mColor);
        }
    }
}
