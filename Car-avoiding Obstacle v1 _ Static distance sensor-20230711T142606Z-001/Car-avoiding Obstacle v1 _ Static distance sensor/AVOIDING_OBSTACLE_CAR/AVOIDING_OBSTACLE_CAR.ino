#include <Wire.h>
int leftWheel = 10;
int rightWheel = 11;
int trig = 12; // Trigger
int echo = 13; // Echo
long duration, cm, inches;
boolean LEFT = true;
int cooldown = 0;
int nextDistance;
int previousDistance = 10;

void setup() {
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  pinMode(leftWheel, OUTPUT);
  pinMode(rightWheel, OUTPUT);

}

void loop() {
  cooldown++;
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);
  pinMode(echo, INPUT);
  
  duration = pulseIn(echo, HIGH);
  cm = (duration/2) / 29.1; 
  nextDistance = cm;
  
  if (cm > 50){ // LOW IS TO MOVE, HIGH IS TO STATIONARY
    digitalWrite(rightWheel, HIGH);
    digitalWrite(leftWheel, HIGH);
  } else if (cm > 10) {
    
    // SET WHICH DIRECTION IS BETTER!!!!!!~!
    previousDistance = nextDistance;
    
    if (nextDistance < previousDistance and cooldown > 1000){ 
      LEFT = !LEFT;
      cooldown = 0;
    }
    
    if (LEFT){
      digitalWrite(rightWheel, LOW);
      digitalWrite(leftWheel, HIGH);
    } else {
      digitalWrite(rightWheel, HIGH);
      digitalWrite(leftWheel, LOW);
    }
    
    
  } else {
    digitalWrite(rightWheel, LOW);
    digitalWrite(leftWheel, LOW);
  }
}
