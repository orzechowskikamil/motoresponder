package com.medziku.motoresponder;

import android.location.Location;

/**
 * Created by medziku on 22.09.15.
 */
public interface LocationChangedCallback {
    void onLocationChange(Location location);
}
