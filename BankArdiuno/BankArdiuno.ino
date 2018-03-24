#include <Keypad.h>
#include <SPI.h>
#include <MFRC522.h>
 
#define SS_PIN 10
#define RST_PIN 9

MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.
  
const byte ROWS = 4;
const byte COLS = 4;
  
char keys[ROWS][COLS]={
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
  
byte rowPins[ROWS] = {5,4,3,2}; //digitale inputs voor rijen
byte colPins[COLS] = {A0,8,7,6}; //digitale input kolommen
  
String prevContent;  
//instantie maken van Keypad
Keypad keypad = Keypad(makeKeymap(keys),colPins,rowPins,ROWS,COLS);
void setup(){
  Serial.begin(9600);
  SPI.begin();      // Initiate  SPI bus
  mfrc522.PCD_Init();   // Initiate MFRC522

 // Serial.println("Begin");
}

void loop() {
  
  // Select one of the cards
  mfrc522.PICC_ReadCardSerial();

   //Show UID on serial monitor
  //Serial.print("UID tag :");
  String content= "";
  byte letter;
  if(mfrc522.PICC_IsNewCardPresent())
  {
      for (byte i = 0; i < mfrc522.uid.size; i++) 
      {
         //Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
         //Serial.print(mfrc522.uid.uidByte[i], HEX);
         content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
         content.concat(String(mfrc522.uid.uidByte[i], HEX));
      }
  }
  if(content != "" && content != prevContent)
  {
      String command = "card: ";
      command.concat(content);
      Serial.println(command);
      prevContent = content;
  }

  //keypad
  char key = keypad.getKey();
  if(key){
    prevContent = "";
    Serial.print("pin:");
    Serial.println(key);
  }
}

