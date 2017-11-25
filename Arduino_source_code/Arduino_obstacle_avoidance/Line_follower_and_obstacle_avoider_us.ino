/* Written by Amanallah Trifi:
 * Git: https://github.com/TrifiAmanallah/ 
 *
 * This the source code for an android based Line follower
 * and obstacle avoider Robot.
 *
 * The Line following is performed by an android app using image 
 * processing and PID controle.
 *
 * The obstacle avoidance is performed by an arduino ADK connected 
 * to an Ultrasonic sensor
 * 
 */
 
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <NewPing.h>
#include <Servo.h>  
#define TRIGGER_PIN  7
#define ECHO_PIN     6
#define VCC 5
#define VCC_2 4
#define MAX_DISTANCE 200
#define temps_rotation_90_deg 900
#define  MOTEUR_DROITE_PIN_LOW  12
#define  MOTEUR_GAUCHE_PIN_LOW  11
#define  MOTEUR_DROITE_PIN_HIGH  9
#define  MOTEUR_GAUCHE_PIN_HIGH  10
#define  LED_PIN  13

int distance_obstacle=50;
boolean test_start_avoidance=false;
long Moy=0;
int i=0;
boolean test_phase_1=false;
boolean test_phase_2=false;
boolean test_phase_3=false;
boolean test_phase_4=false;
boolean test_phase_5=false;
unsigned long temps;
Servo myservo; 
NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE);
AndroidAccessory acc("Manufacturer",
		"Model",
		"Description",
		"1.0",
		"http://yoursite.com",
                "0000000012345678");
void setup()
{
  //SETUP COMMUNICATION
  Serial.begin(115200);
  acc.powerOn();
  temps=millis();
  myservo.attach(3);
  myservo.write(80);
  //SETUP OUTPUTS
  pinMode(LED_PIN, OUTPUT);
  pinMode(VCC, OUTPUT);
   pinMode(VCC_2, OUTPUT);
  pinMode(MOTEUR_DROITE_PIN_LOW, OUTPUT);
  pinMode(MOTEUR_GAUCHE_PIN_LOW, OUTPUT);
  pinMode(MOTEUR_DROITE_PIN_HIGH, OUTPUT);
  pinMode(MOTEUR_GAUCHE_PIN_HIGH , OUTPUT);
  //INISIALIZATION
  digitalWrite(LED_PIN, LOW); 
  digitalWrite(VCC,HIGH); 
  digitalWrite(VCC_2,HIGH); 
  digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
  digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
  digitalWrite(MOTEUR_DROITE_PIN_HIGH, LOW);
  digitalWrite(MOTEUR_GAUCHE_PIN_HIGH, LOW);

}
void loop()
{
byte msg[2]; 
if (acc.isConnected()) {
	int len = acc.read(msg, sizeof(msg),1); // read data into msg variable
    if (len >0) {
     
    if(i<5) {
		int uS = sonar.ping();
		int distance=uS / US_ROUNDTRIP_CM; Serial.println(distance);
		Moy=Moy+distance;
		i=i+1;
		if(distance==0) {Moy=0;i=5;}
	}
    else {
		Moy=Moy/5;Serial.print("moyenne=");Serial.println(Moy);
		if (Moy>0&&Moy<distance_obstacle) test_start_avoidance=true;
		i=0;Moy=0;
	}         
     
	if(!test_start_avoidance) {                           
		digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
		analogWrite(MOTEUR_DROITE_PIN_HIGH,255);
		digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
		analogWrite(MOTEUR_GAUCHE_PIN_HIGH,255);
	} 
	else if((test_start_avoidance||test_phase_1)&&!test_phase_2) {
		//Serial.println("OBSTACLE!!");
		digitalWrite(LED_PIN, HIGH);
		myservo.write(0);
		temps=millis();
		do { 
			digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
			analogWrite(MOTEUR_DROITE_PIN_HIGH,255);
			digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
			analogWrite(MOTEUR_GAUCHE_PIN_HIGH,0);             
			test_phase_1=true;
		}while(millis()-temps<temps_rotation_90_deg);
		test_phase_1=false;
		test_phase_2=true; 
	}
    else if(test_phase_2) {
		myservo.write(0);
		temps=millis();
		do { 
			digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
			analogWrite(MOTEUR_DROITE_PIN_HIGH,255);
			digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
			analogWrite(MOTEUR_GAUCHE_PIN_HIGH,255);
			test_phase_2=true;                
		}while(!test_start_avoidance&&millis()-temps<900);
		test_phase_2=false; 
		test_phase_3=true;
	}  
	else if(test_phase_3) {
		myservo.write(0);
		temps=millis();
		do { 
			digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
			analogWrite(MOTEUR_DROITE_PIN_HIGH,0);
			digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
			analogWrite(MOTEUR_GAUCHE_PIN_HIGH,255);
			test_phase_3=true;                
		}while(millis()-temps<temps_rotation_90_deg);
		test_phase_3=false; 
		test_phase_4=true;
	}
    else if(test_phase_4) {                                    
		temps=millis();
		do { 
			digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
			analogWrite(MOTEUR_DROITE_PIN_HIGH,255);
			digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
			analogWrite(MOTEUR_GAUCHE_PIN_HIGH,255);
			test_phase_4=true;                
		}while(millis()-temps<1000);
		test_phase_4=false; 
		test_phase_5=true;
	}
    else if(test_phase_5) {
		myservo.write(80);
		temps=millis();
		do { 
			digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
			analogWrite(MOTEUR_DROITE_PIN_HIGH,255);
			digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
			analogWrite(MOTEUR_GAUCHE_PIN_HIGH,0);
			test_phase_5=true;             
		}while(millis()-temps<temps_rotation_90_deg);
		test_phase_5=false;
		test_start_avoidance=false;
	}                                     
    else{
		digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
		analogWrite(MOTEUR_DROITE_PIN_HIGH,0);
		digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
		analogWrite(MOTEUR_GAUCHE_PIN_HIGH,0);
	}                              
  }
}
}
