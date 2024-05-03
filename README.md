# ChimpagneApp

Tired of the party planning chaos? Chimpagne streamlines it all in one application : easy guest invites, seamless location sharing, automated reminders, beverage coordination, real-time updates. Download now and throw stress-free parties. Life's too short for party planning headaches.


"Building an event organisation application” :

- Check-in: Ensures quick access to the event via QR code or invite link.
- Task management: To organise and track preparations effortlessly with reminders.
- Polls & voting: Facilitates group decisions.
- Shared budget & purchases: Simplifies collective expense control.
- Shared playlist: Creates a collaborative musical atmosphere using Spotify.  
- Group and private chat: Enables instant communication.
- Share the party/event with other people that aren’t in the organisation
- Research public parties that are close to your location 
- Party rating & review system ⭐⭐⭐⭐
- Party tag for what kind of party it is (this is set up by the organiser)
- Weather Info: Have a bar that indicates if and when it's going to rain (plus reminder)
- Permission Handling: One admin (creates event), multiple possible organisers (added later on) and event goers (who don’t have access to the organisation part of the event). 
- Car pooling : number of seats, departure time, destination, etc.. ,  “trust me I am not drunk”
- Organizing who can sleep on site with reservation 

# Architecture diagram

![alt text](arch.png)

# UI

## Design system

Material design has been unanimously choosen

## Sonar

https://sonarcloud.io/summary/overall?id=Chimpagne_ChimpagneApp

## Figma

https://www.figma.com/file/EfCQDVZTCt10o1LOw7R6H9/APP?type=design&node-id=0%3A1&mode=design&t=2499EcLDJihLwRIx-1

## Google Sheets
- [Team Standup](https://docs.google.com/spreadsheets/d/1Pcl_h6zzJgwICjcaRdEEALT_xQiP15N6UBF5lKIfa4Y/edit?usp=sharing)
- [Team Retrospective](https://docs.google.com/spreadsheets/d/13o0Ysau7RKnynANGpo9c38J0q14ie0_Pz-sv7EAwpT4/edit?usp=sharing)

## Scrum Masters and Owners
- Scrum Master Sprint 1 : Juan Bautista Iaconucci
- Scrum Master Sprint 2 : Clément
- Scrum Master Sprint 3 : Léa
- Scrum Master Sprint 4 : Arnaud
- Scrum Master Sprint 5: Gregory
- Scrum Master Sprint 6: Sacha
- Scrum Master Sprint 7: Léa

- Product owner Sprint 1 : The coaches
- Product owner Sprint 2 : Arnaud
- Product owner Sprint 3 : Gregory
- Product owner Sprint 4 : Sacha
- Product owner Sprint 5: Clément
- Product owner Sprint 6: Léa
- Product owner Sprint 7: Juan


## How to setup the project on your device

1. Clone the repo
2. Add this to secrets.properties (not local.properties and NOT secrets.default.properties)

    ```MAPS_API_KEY=YOUR_API_KEY_HERE```
