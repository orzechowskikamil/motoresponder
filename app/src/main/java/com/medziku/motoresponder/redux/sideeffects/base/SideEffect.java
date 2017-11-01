package com.medziku.motoresponder.redux.sideeffects.base;

import android.content.Context;
import com.medziku.motoresponder.redux.Store;

/**
 * This interface should be fulfilled by any Android side effect
 */
public interface SideEffect {
    void start(Context context, Store store);
    void stop();
}
