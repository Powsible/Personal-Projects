/*
CLOCK_WITH_ALARM
AUTHOR Christian Joel M. Ventura
VERSION 2023-3-28
PURPOSE: Program a multifunction shield to act as a digital alarm clock
*/

#include <TimerOne.h>
#include <Wire.h>
#include <MultiFuncShield.h>
#define LM35 A4;

int minutes = 27; 
int hours = 19;
int seconds = 27;
int deciseconds = 0;

boolean displayMinuteHour = true; 
boolean display24HourClock = false;
boolean displaySecondDecisecond = false;

byte button;

boolean alarmState = false; // Alarm menu
boolean alarmBlink = true; // Used for blinking LED to show the user is in alarm menu
int alarmBlinkCount = 0;
int alarmMinutes = 0;
int alarmHours = 0;
boolean isAlarm = false; // If alarm is active
char inAlarm = 10; // Blinking LED indicating in alarm menu
char ledAlarm = 11; // Led indicating alarm is active
char ledAlarmBeep = 12; // Led indicating alarm rings


void setup() {
  Timer1.initialize();
  MFS.initialize(&Timer1);
  MFS.write(0);
  Serial.begin(9600);
  pinMode(ledAlarm, OUTPUT);
  pinMode(ledAlarmBeep, OUTPUT);
}


void loop(){
  delay(100);
  deciseconds++;

  button = MFS.getButton(); // Gather input from user

  configureUserButtons();
  
  configureClockTime();
  
  configureDisplay();
}


void configureClockTime(){ // Organize time between hours, minutes, and seconds
  if (deciseconds > 9){
    seconds++;
    deciseconds = 0;
  }
  
  if (seconds > 59){
    minutes++;
    seconds = 0;
  }

  if (minutes > 59){
    hours++;
    minutes = 0;
  }

  if (hours > 23){
    hours = 0;
  }

  if (alarmMinutes > 59){
    alarmHours++;
    alarmMinutes = 0;
  }

  if (alarmMinutes < 0){
    alarmHours--;
    alarmMinutes = 59;
  }

  if (alarmHours > 23){
    alarmHours = 0;
  }

  if (alarmHours < 0){
    alarmHours = 23;
  }
}


void configureUserButtons(){ // Analyze user inputs
  if (button == BUTTON_3_SHORT_RELEASE){ // Go to alarm menu
    alarmState = !alarmState;
  }

  if (isAlarm and alarmMinutes == minutes and alarmHours == hours){ // Turn off alarm going off using any of the following buttons
    if (button == BUTTON_1_SHORT_RELEASE or button == BUTTON_2_SHORT_RELEASE or button == BUTTON_3_SHORT_RELEASE){
      isAlarm = !isAlarm;
    }
  } else if (!alarmState){ // Display different time formats and change time by minutes and hours
    digitalWrite(inAlarm, LOW);
    if (button == BUTTON_1_SHORT_RELEASE){
      displayMinuteHour = !displayMinuteHour;
      display24HourClock = false;
      displaySecondDecisecond = false;
    }

    if (button == BUTTON_1_LONG_RELEASE){
      if (displayMinuteHour){
         display24HourClock = true;
      } else {
         displaySecondDecisecond = true;
      }
    }
    
    if (button == BUTTON_2_SHORT_RELEASE){
      minutes++;
    }

    if (button == BUTTON_2_LONG_RELEASE){
      hours++;
    }
  } else { // Modify or set alarm
    if (button == BUTTON_3_LONG_RELEASE){
      alarmState = !alarmState;
      isAlarm = !isAlarm;
    }
    if (button == BUTTON_1_SHORT_RELEASE){
      alarmMinutes--;
    }

    if (button == BUTTON_1_LONG_RELEASE){
      alarmHours--;
    }
    if (button == BUTTON_2_SHORT_RELEASE){
      alarmMinutes++;
    }

    if (button == BUTTON_2_LONG_RELEASE){
      alarmHours++;
    }
  }
}


void configureDisplay(){ // Manage Display
  if (!alarmState){ // Display normal time
    if (displayMinuteHour){
      if (!display24HourClock){ // Display 24-hour clock
        int hourCheck = hours;
        if (hourCheck > 12){
          hourCheck -= 12;
        }
        if (hourCheck == 0){
          hourCheck = 12; // 00:00 is 12:00 AM
        }
        MFS.write(hourCheck*100 + minutes);
      } else { // Display twelve-hour clock
        MFS.write(hours*100 + minutes);
      }
    } else { // Display minutes, seconds, and deciseconds of the clock
      if (!displaySecondDecisecond){
        MFS.write(minutes*100 + seconds);
      } else {
        MFS.write(seconds*100 + deciseconds*10);
      }
    }
  } else { // Display alarm menu
    MFS.write(alarmHours*100 + alarmMinutes);
    alarmBlinkCount++;
    if (alarmBlinkCount > 2){ // Blinks every 4 decaseconds
      alarmBlinkCount = 0;
      alarmBlink = !alarmBlink;
    }
    if (alarmBlink){
      digitalWrite(inAlarm, LOW);
    } else {
      digitalWrite(inAlarm, HIGH);
    }
  }

  if (isAlarm){ // Alarm is on
    digitalWrite(ledAlarm, LOW);
    if (alarmMinutes == minutes and alarmHours == hours){ // Alarm rings
      MFS.beep(5, 5, 3);
      digitalWrite(ledAlarmBeep, LOW);
    } else {
      digitalWrite(ledAlarmBeep, HIGH);
    }
  } else {
    digitalWrite(ledAlarm, HIGH);
    digitalWrite(ledAlarmBeep, HIGH);
  }
}
