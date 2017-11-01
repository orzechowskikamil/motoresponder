package com.medziku.motoresponder.redux.sideeffects;

import com.medziku.motoresponder.BuildConfig;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.sideeffects.base.SubscribedContextSideEffect;
import trikita.jedux.Action;

public class PackageName extends SubscribedContextSideEffect{
    @Override
    public void onStoreChanged() {
        if (this.store.getState().applicationPackageName()==null) {
            this.store.dispatch(new Action(Actions.AppBuild.PACKAGE_NAME, getApplicationPackageName()));
        }
    }

    @Override
    protected void afterStart() {

    }

    @Override
    protected void beforeStop() {

    }

    private String getApplicationPackageName() {
        return BuildConfig.APPLICATION_ID;
    }
}
