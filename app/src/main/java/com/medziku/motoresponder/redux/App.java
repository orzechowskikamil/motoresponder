package com.medziku.motoresponder.redux;

import trikita.jedux.Action;
import trikita.jedux.Store;

/**
 * Created by Kamil on 25.09.2017.
 */

public class App {

        private static App instance;

        private Store<Action, State> store;

        public static State dispatch(Action action) {
            return instance.store.dispatch(action);
        }

        public static State getState() {
            return instance.store.getState();
        }


        public void onCreate() {
            App.instance = this;



            this.store = new Store<Action,State>(new Reducer(),
                    Default.build());

            this.store.subscribe(Anvil::render);
        }
    }
