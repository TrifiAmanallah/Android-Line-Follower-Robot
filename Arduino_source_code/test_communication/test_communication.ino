#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#define  LED_PIN  13
AndroidAccessory acc("Manufacturer",
		"Model",
		"Description",
		"1.0",
		"http://yoursite.com",
                "0000000012345678");
void setup()
{
  // set communiation speed
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  acc.powerOn();
}
 
void loop()
{
  byte msg[3];
  if (acc.isConnected()) {
    int len = acc.read(msg, sizeof(msg), 1); // read data into msg variable
    if (len >0) {
      int commande=msg[0];
      if(commande==1) {
      int right_speed = msg[2];
      int left_speed= msg[3];
      Serial.println(right_speed);
      serial.println(left_speed);
                     }
      else Serial.println("invalid commande");               
    }
  } 
  else Serial.println("no message");
}
