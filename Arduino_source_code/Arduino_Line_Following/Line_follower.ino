/* Written by Amanallah Trifi:
 * Git: https://github.com/TrifiAmanallah/ 
 *
 * This the source code for an android based Line follower
 * and obstacle avoider Robot.
 *
 * The Line following is performed by an android app using image 
 * processing and PID controle.
 * 
 */
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define  MOTEUR_DROITE_PIN_LOW  12
#define  MOTEUR_GAUCHE_PIN_LOW  11
#define  MOTEUR_DROITE_PIN_HIGH  9
#define  MOTEUR_GAUCHE_PIN_HIGH  10
#define  LED_PIN  13
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
  //SETUP OUTPUTS
  pinMode(LED_PIN, OUTPUT);
  pinMode(MOTEUR_DROITE_PIN_LOW, OUTPUT);
  pinMode(MOTEUR_GAUCHE_PIN_LOW, OUTPUT);
  pinMode(MOTEUR_DROITE_PIN_HIGH, OUTPUT);
  pinMode(MOTEUR_GAUCHE_PIN_HIGH , OUTPUT);
  //INISIALIZATION
  digitalWrite(LED_PIN, LOW); 
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
     
      if(msg[0]==0x1)         {byte right_speed=msg[1];
                               digitalWrite(MOTEUR_DROITE_PIN_LOW, LOW);
                               analogWrite(MOTEUR_DROITE_PIN_HIGH,right_speed);
                               //Serial.println(right_speed);
                              }
      else if (msg[0]==0x2)   {byte left_speed=msg[1];
                               digitalWrite(MOTEUR_GAUCHE_PIN_LOW, LOW);
                               analogWrite(MOTEUR_GAUCHE_PIN_HIGH,left_speed);
                               //Serial.println(left_speed);
                             } 
                }
  }
}
