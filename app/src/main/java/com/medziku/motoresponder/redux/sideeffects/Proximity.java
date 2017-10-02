class Proximity{

Proximity(Store store,Context context){
this.store=store;
this.proximityUtility=new ProximityUtility(context)
}

start(){ this.proximityUtility.listenToProximity(

);

}
