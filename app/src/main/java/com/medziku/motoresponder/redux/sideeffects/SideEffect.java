package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import com.medziku.motoresponder.redux.Store;

public interface SideEffect {
    void start(Context context, Store store);
    void stop();
}
