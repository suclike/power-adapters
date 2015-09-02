package com.nextfaze.asyncdata;

import android.support.annotation.UiThread;
import lombok.NonNull;

import java.util.Iterator;

/**
 * Provides access to an asynchronously loaded list of elements.
 * <h3>Loading</h3>
 * <p>
 * A {@code Data} instance may be in a loading state, which it broadcasts to interested observers so they can present
 * loading indicators.
 * </p>
 * <h3>Errors</h3>
 * <p>
 * It also broadcasts errors it encounters while loading.
 * </p>
 * <h3>Showing</h3>
 * <p>
 * It may also be in a shown or hidden state. When hidden, the data may opt to release any internal resources. This way
 * the user does not need to necessarily free them manually. It's not recommended that resources be freed immediately
 * upon being hidden, however, because often it is only hidden temporarily. Release them after a short delay instead.
 * </p>
 * <h3>Threading</h3>
 * <p>
 * In general this class is not thread-safe. It's intended to be accessed from the UI thread only.
 * <h3>Notifications</h3>
 * Change notifications must be dispatched BEFORE the other notifications.
 * </p>
 */
public interface Data<T> extends Iterable<T> {

    /** Flag indicating the intent to present information in a user interface. */
    int FLAG_PRESENTATION = 1;

    int UNKNOWN = -1;

    /**
     * Retrieve the element at the specified position. Equivalent to calling {@link #get(int, int)} without any flags.
     * @param position The position at which to retrieve the value.
     * @return The value at the specified position, never {@code null}.
     * @throws RuntimeException If the element is out of bounds or can't be retrieved.
     */
    @UiThread
    @NonNull
    T get(int position);

    /**
     * Retrieve the element at the specified position.
     * <p/>The return value for a given {@code position} must never change:
     * <ul>
     * <li>Outside the UI thread</li>
     * <li>Without a corresponding change notification</li>
     * </ul>
     * @param position The position at which to retrieve the value.
     * @param flags Bit field containing flags with extra information about this request for an element.
     * @return The value at the specified position, never {@code null}.
     * @throws RuntimeException If the element is out of bounds or can't be retrieved.
     */
    @UiThread
    @NonNull
    T get(int position, int flags);

    /**
     * The number of elements in this data instance.
     * <p/>The return value must never change:
     * <ul>
     * <li>Outside the UI thread</li>
     * <li>Without a corresponding change notification</li>
     * </ul>
     * @return The number of elements, always {@code >= 0}.
     */
    @UiThread
    int size();

    /**
     * Indicates how many more elements are available to be loaded relative to the current data set. Implementations
     * can
     * override this if they wish to convey an incomplete data set to observers.
     * <p/>The return value must never change:
     * <ul>
     * <li>Outside the UI thread</li>
     * <li>Without a corresponding available notification</li>
     * </ul>
     * @return {@link #UNKNOWN} if the value is unknown, {@link Integer#MAX_VALUE} if it's known there are more elements
     * available, but not how many. {@code 0} indicates no more elements are available to be loaded.
     */
    @UiThread
    int available();

    /** Simply returns if {@link #size()} {@code == 0}. The same threading constraints apply. */
    @UiThread
    boolean isEmpty();

    /** If {@code true}, indicates the data is currently loading more elements. */
    @UiThread
    boolean isLoading();

    /** Marks existing elements as invalid, such that they will be reloaded next time the data is shown. */
    @UiThread
    void invalidate();

    /** Reloads the elements without clearing them first. */
    @UiThread
    void refresh();

    /** Clears then refreshes the elements. */
    @UiThread
    void reload();

    /** Close this instance. Other methods should not called after this. */
    @UiThread
    void close();

    /**
     * Notify this data instance that is currently visibility presented to the user. This cue might be used to start
     * loading elements, or to refresh existing ones.
     */
    @UiThread
    void notifyShown();

    /**
     * Notify this data instance that is no longer presented to the user. This cue might be used to cancel any active
     * loading operations.
     * <p/>Note that it's common for the data to be in a "hidden" state for a very short interval,
     * such as during a configuration change, so any cancelation should probably occur after a short delay. {@code 3}
     * seconds should suffice.
     */
    @UiThread
    void notifyHidden();

    @UiThread
    void registerLoadingObserver(@NonNull LoadingObserver loadingObserver);

    @UiThread
    void unregisterLoadingObserver(@NonNull LoadingObserver loadingObserver);

    @UiThread
    void registerErrorObserver(@NonNull ErrorObserver errorObserver);

    @UiThread
    void unregisterErrorObserver(@NonNull ErrorObserver errorObserver);

    @UiThread
    void registerDataObserver(@NonNull DataObserver dataObserver);

    @UiThread
    void unregisterDataObserver(@NonNull DataObserver dataObserver);

    @UiThread
    void registerAvailableObserver(@NonNull AvailableObserver availableObserver);

    @UiThread
    void unregisterAvailableObserver(@NonNull AvailableObserver availableObserver);

    @UiThread
    @Override
    Iterator<T> iterator();
}
