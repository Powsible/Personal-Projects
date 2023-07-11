/*
OBSTACLE_AVOIDING_CAR
AUTHOR Christian Joel M. Ventura
VERSION 2023-5-29
PURPOSE: Program an Arduino UNO to simulate a car that avoids obstacles.
*/

#include <LiquidCrystal_I2C.h>
#include <Wire.h>
#include <Servo.h>

int rightWheelSpeed = 4, rightWheel2 = 5, rightWheel1 = 9;
int leftWheel1 = 11, leftWheel2 = 10, leftWheelSpeed = 12;
int trig = 7, echo = 8;
int servo1Pin = 2, servo2Pin = 13;
Servo servo1, servo2;
LiquidCrystal_I2C lcd(0x27, 16, 2);

void setup() {
  pinMode(rightWheel1, OUTPUT);
  pinMode(rightWheel2, OUTPUT);
  pinMode(rightWheelSpeed, OUTPUT);
  pinMode(leftWheel1, OUTPUT);
  pinMode(leftWheel2, OUTPUT);
  pinMode(leftWheelSpeed, OUTPUT);

  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);

  servo1.attach(servo1Pin);
  servo2.attach(servo2Pin);

  lcd.init();
  lcd.backlight();

  analogWrite(leftWheelSpeed, 255);
  analogWrite(rightWheelSpeed, 255);
}

void loop() {
  int distance = getDistance();
  lcd.print("Distance: " + String(distance) + " cm");

  if (distance > 10){
    moveStraight(true);
  } else {
    moveStraight(false);
    delay(1000);
    stop();

    int rightDistance = getDirectionDistance(true);
    lcd.clear();
    lcd.print("Right: " + String(rightDistance) + " cm");
    delay(100);
    int leftDistance = getDirectionDistance(false);
    lcd.clear();
    lcd.print("Left: " + String(leftDistance) + " cm");
    
    if (leftDistance > rightDistance){
      leftTurn(true);
    } else {
      rightTurn(true);
    }

    delay(1000);
  }

  delay(100);
  lcd.clear();
}

int getDirectionDistance(boolean isRight){
  int direction = isRight ? 0 : 90;
  servo1.write(48 + direction);
  delay(200);
  servo1.write(90);
  delay(200);

  // Prevent calculating distance with too high value unless it actually is by counting
  int dist = getDistance();
  int count = 0;
  while (dist > 1000 && count < 10){
    dist = getDistance();
    count++;
  }
  
  delay(200);
  servo1.write(138 - direction);
  delay(200);
  servo1.write(90);

  return dist;
}

int getDistance(){
  digitalWrite(trig, LOW);
  delayMicroseconds(2);
  digitalWrite(trig, HIGH);
  delayMicroseconds(10);
  digitalWrite(trig, LOW);
  pinMode(echo, INPUT);

  return pulseIn(echo, HIGH) / 29 / 2;
}

void moveStraight(boolean isForward){
  leftEngine(isForward, false);
  rightEngine(isForward, false);
}

void stop(){
  leftEngine(false, true);
  rightEngine(false, true);
}

void leftTurn(boolean isForward){
  leftEngine(isForward, false);
  rightEngine(false, true);
}

void rightTurn(boolean isForward){
  leftEngine(false, true);
  rightEngine(isForward, false);
}

void leftEngine(boolean isForward, boolean stopEngine){
  if (!stopEngine){
    if (isForward){
      digitalWrite(leftWheel1, HIGH);
      digitalWrite(leftWheel2, LOW);
    } else {
      digitalWrite(leftWheel1, LOW);
      digitalWrite(leftWheel2, HIGH);
    }
  } else {
    digitalWrite(leftWheel1, LOW);
    digitalWrite(leftWheel2, LOW);
  }
}

void rightEngine(boolean isForward, boolean stopEngine){
  if (!stopEngine){
    if (isForward){
      digitalWrite(rightWheel1, HIGH);
      digitalWrite(rightWheel2, LOW);
    } else {
      digitalWrite(rightWheel1, LOW);
      digitalWrite(rightWheel2, HIGH);
    }
  } else {
    digitalWrite(rightWheel1, LOW);
      digitalWrite(rightWheel2, LOW);
  }
}



