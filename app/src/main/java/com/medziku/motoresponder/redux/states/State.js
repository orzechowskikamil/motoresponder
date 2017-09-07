class State {
   
  IncomingState incomingState; 
  SensorsState sensorsState;
   

}

class IncomingState{
 List<Incoming> incoming;
}

class Incoming{
String phoneNumber;
}

class IncomingMessage extends Incoming{
String message;
}

class IncomingCall extends Incoming{}


class SensorsState {
AccelerometerState accelerometerState;
}
