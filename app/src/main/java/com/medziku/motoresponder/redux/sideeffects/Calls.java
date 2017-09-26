
public class Calls{
  public Calls(AppStore store){
    this.store=store;
  }
  
  public void start(){
    this.store.subscribe()
  }
}
