abstract class Reducer<State>{


  abstract public <State> reduce(<State> oldState, Action action);
  

}
