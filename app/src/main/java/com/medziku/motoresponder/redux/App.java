package com.medziku.motoresponder.redux;

import trikita.jedux.Action;
import trikita.jedux.Store;

public class AppStore extends {
        
        public AppStore(Store<Action, State> store){
                this.store=store;
        }
        
        
        public static AppStore createDefault(){
                return new AppStore(new Store<Action,State>(new Reducer(),                   Default.build()));
                
        }
       

        public  State dispatch(Action action) {
            return this.store.dispatch(action);
        }

        public  State getState() {
            return this.store.getState();
        }


    }
