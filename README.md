# motoresponder

Android application for automatical responding for motorbikers, cyclist or other people who can't talk/text during ride.

Motoresponder waits in background for incoming calls and SMSes.
It give then some time for you to respond yourself, and if you don't do this, app wake up and try to measure if you are riding fast enough to consider that you are riding on motorcycle.
Measurement is as cheap (in terms of battery) as possible.

So, if you have phone unlocked during receiving message or call, or you will respond manually, app ignores this incoming message/call.
Then app checks if sender is someone who should be responded automatically. By default, app will not respond to SMS premium or abroad numbers, or don't send more than one autorespond to one person until you will not send something to him manually.
If not, it try to measure if you are riding. Firstly using most cheap sensors, like proximity. They are checked for conditions usual for riding motorcycle, then more power consuming like accelerometer, and if phone is shaking like normal ride, finally GPS is used to measure your speed.

And if app will assume that you are riding, app will automatically respond with configurable message.

# Because I got very strong injury in motorcycle accident, project is postponed until ???????.