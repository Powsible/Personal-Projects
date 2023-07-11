/*
TIMER_STOPWATCH
AUTHOR Christian Joel M. Ventura
VERSION 2023-3-26
PURPOSE: Program a multifunction shield that can act as a stopwatch and timer
*/

#include <TimerOne.h>
#include <Wire.h>
#include <MultiFuncShield.h>
#define LM35 A4;

enum possibleStates{
  STOPWATCH, // Short release button 1 to set stopwatch. Short release button 3 to start. Long release button 3 to cancel.
  TIMER, // Short release button 2 to set timer. Short release button 3 to start. Long release button 3 to cancel.
  // When menu, short release button 1 to decrement seconds, long release to decrement minutes. Vice versa with increment for button 2.
  NONE, // User still needs to choose between the two above
  SHOW_SECONDS_MINUTES,
  SHOW_MINUTES_HOURS,
  SHOW_DECISECONDS_SECONDS
};

byte displayedMenu = NONE; 
byte condition = NONE;
byte timeDisplay = SHOW_SECONDS_MINUTES;
byte button;
boolean isPaused = false; 

int hours = 0;
int minutes = 0;
int seconds = 0;
int deciseconds = 0;

int blinkCount = 0;
boolean isOn = false; // When blinking

char LED1 = 10; // Stopwatch on, stopwatch menu blinking, not in it off
char LED2 = 11; // Timer on, timer menu blinking, not in it off
char LED3 = 12; // Resume on, pause blinking, not active off

void setup() {
  Timer1.initialize();
  MFS.initialize(&Timer1);
  MFS.write(0);
  Serial.begin(9600);
}

void loop() {
  button = MFS.getButton();

  if (condition == NONE){
    MFS.write(minutes*100 + seconds);
    if (displayedMenu == NONE){ // User still choosing
      digitalWrite(LED1, HIGH);
      digitalWrite(LED2, HIGH);
      digitalWrite(LED3, HIGH);
      blinkCount = 0;
      isOn = false;
      
      condition = NONE;
      if (button == BUTTON_1_SHORT_RELEASE){
        displayedMenu = STOPWATCH;
      } 
      if (button == BUTTON_2_SHORT_RELEASE){
        displayedMenu = TIMER;
      } 
    
    } else if (displayedMenu == STOPWATCH) { // Stopwatch
      blinkLED(LED1);

      // Starting time
      configureIncrementingButtonsInMenu(LED1);
    
    } else { // Timer
      blinkLED(LED2);

      // Starting time
      configureIncrementingButtonsInMenu(LED2);
    }
  } else {
    displayedMenu = NONE; // Go back to options after countdown stops

    if (condition == TIMER and deciseconds == 0 and seconds == 0 and hours == 0 and minutes == 0){ // TIMER RUNS OUT!
      boolean exitTimer = false;
      while (not exitTimer){ // User needs to enter one of the buttons
        MFS.beep(5, 5, 3); // RING!
        delay(100);
        
        byte button = MFS.getButton();
        if (button == BUTTON_1_SHORT_RELEASE or button == BUTTON_2_SHORT_RELEASE or button == BUTTON_3_SHORT_RELEASE){
          exitTimer = true;
        }
      }

      resetTime();
      condition = NONE; // Reset Condition
    }
    
    configureButtonsInActiveCondition();
    configureDisplay();
      
    if (!isPaused){ 
      digitalWrite(LED3, LOW);
      delay(100);
      if (condition == STOPWATCH){
        deciseconds++; // Stopwatch increment
      } else {
        deciseconds--; // Timer decrement
      }
    } else {
      // Blink LED
      blinkLED(LED3);
    }
  }
  
  configureTime();
}

void resetTime(){
  deciseconds = 0;
  seconds = 0;
  minutes = 0;
  hours = 0;
}

void blinkLED(int LED){ // Blink LED
  delay(100);
  blinkCount++;
  if (blinkCount > 2){ // Blinks every 4 decaseconds
    blinkCount = 0;
    isOn = !isOn;
    if (isOn){
      digitalWrite(LED, LOW);
    } else {
      digitalWrite(LED, HIGH);
    }
  }
}

void configureIncrementingButtonsInMenu(int LED){
  if (button == BUTTON_1_SHORT_RELEASE){
    seconds--;
  }
      
  if (button == BUTTON_1_LONG_RELEASE){
    minutes--;
  }

  if (button == BUTTON_2_SHORT_RELEASE){
    seconds++;
  }

  if (button == BUTTON_2_LONG_RELEASE){
    minutes++;
  }

  if (button == BUTTON_3_LONG_RELEASE){ // Go back to options
    displayedMenu = NONE;
    resetTime();
  }

  if (button == BUTTON_3_SHORT_RELEASE and (!(deciseconds == 0 and seconds == 0 and minutes == 0 and hours == 0) or displayedMenu == STOPWATCH)){ // Activate only if the timer is not set to 0
    condition = displayedMenu;
    isPaused = false;
    digitalWrite(LED, LOW); // Turn on LED on the corresponding condition
  }
}

void configureTime(){ // Manage time display and time intervals
  if (deciseconds > 9){
    seconds++;
    deciseconds = 0;
  } 
  
  if (deciseconds < 0){
    if (seconds > 0 or minutes > 0 or hours > 0){
      seconds--;
      deciseconds = 9;
    } else {
      deciseconds = 0;
    }
  }
  
  if (seconds > 59){
    minutes++;
    seconds = 0;
  } 
  
  if (seconds < 0){
    if (minutes > 0 or hours > 0){
      minutes--;
      seconds = 59;
    } else {
      seconds = 0;
    }
  }

  if (minutes > 59){
    hours++;
    minutes = 0;
  } 
  
  if (minutes < 0){
    if (hours > 0){
      hours--;
      minutes = 59;
    } else {
      minutes = 0;
    }
  }

  if (hours > 99){ // Sorry, the digital LED can only display up to 4 digits
    hours = 0;
  } 
}

void configureDisplay(){
  if (timeDisplay == SHOW_SECONDS_MINUTES){
    MFS.write(minutes*100 + seconds);
  }
  if (timeDisplay == SHOW_MINUTES_HOURS){
    MFS.write(hours*100 + minutes);
  } 
  if (timeDisplay == SHOW_DECISECONDS_SECONDS){
    MFS.write(seconds*100 + deciseconds*10);
  }
}

void configureButtonsInActiveCondition(){
  if (button == BUTTON_3_LONG_RELEASE){ // User cancels the countdown, return to menu
    condition = NONE;
    resetTime();
  } 

  if (button == BUTTON_3_SHORT_RELEASE){ // User wants to pause the countdown
    isPaused = !isPaused;
  }

  if (button == BUTTON_1_SHORT_RELEASE){ 
    timeDisplay = SHOW_SECONDS_MINUTES;
  }
    
  if (button == BUTTON_2_SHORT_RELEASE){ 
    timeDisplay = SHOW_MINUTES_HOURS;
  }
    
  if (button == BUTTON_2_LONG_RELEASE){
    timeDisplay = SHOW_DECISECONDS_SECONDS;
  }
}
