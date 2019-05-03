### Obligatorisk innlevering 5

**Deloppgave 1** 

En viktig erfaring teamet har tilegnet seg er at arbeidsoppgaver ofte tar lengre tid enn planlagt. Dette er noe teamet har gjort en innsats for å gjøre noe med ved å starte arbeidet tidlig etter at obligatorisk kravliste har blitt publisert. Gruppen har også erfart at dette har en tendens til å skli litt ut etter 1 ukes tid så det er fortsatt en vei å gå for å nå optimal arbeidsflyt. 

Gruppedynamikken har vært veldig god gjennom hele prosjektet. Arbeidsfordelingen har vært fornuftig og fleksibel hvor arbeidsoppgavene har blitt fordelt basert på kompetanse. Samtidig har gruppemedlemmene fått prøve seg på arbeidsoppgaver hvor man ikke har hatt så mye kompetanse, her som regel sammen med en som har kompetanse innenfor området som arbeidsoppgaven dekker. Det har generelt sett gjennom prosjektet vært veldig lav terskel for å spørre om hjelp fra resten av gruppen. 

Gruppen har ikke gjort så mange justeringer gjennom prosjektet med unntak av overgang fra single player til multiplayer. Da ble det gjort noen justeringer i en del forskjellige klasser for å få multiplayer til å fungere. Den største justeringen har vært endringer i animasjon, dette var en stor overgang i prosjektet, det har fungert utmerket etter justeringen og er det vi er mest fornøyd med. 
Om prosjektet skulle holdt på lengre kunne gruppen tenkt seg å legge til flere brett i spillet for å gi det større variasjon og kompleksitet. 

Gruppen har lært at god arbeidsflyt, løpende dialog og møte opp til avtaler er veldig viktig for å ha en god progresjon i prosjektet. 

Liste over implementerte krav: 

Man må kunne spille en komplett runde   JA  
Man må kunne vinne spillet spillet ved å besøke siste flagg (fullføre et spill)   JA  
Det skal være lasere på brettet   JA  
Det skal være hull på brettet  JA  
Skademekanismer (spilleren får færre kort ved skade) NEI    
Spillmekanismer for å skyte andre spillere innen rekkevidde med laser som peker rett frem  JA  
Fungerende samlebånd på brettet som flytter robotene   JA  
Fungerende gyroer på brettet som flytter robotene   JA  
Game over etter 3 tapte liv   JA  
Multiplayer over LAN eller Internet   JA  
Feilhåndtering ved disconnect. (Spillet skal i hvertfall ikke kræsje)   NEI  
Power down NEI    
Samlebånd som går i dobbelt tempo   JA  
Spille mot AI (single-player-mode), evt spill mot random-roboter  JA  



Som en del av denne leveransen skal dere legge ved en liste (og kort beskrivelse?) over kjente feil og svakheter i produktet. 
   
    - Dersom man velger å låse inn kortene man vil bruke uten å velge noen den gitte runden krasjer spillet ved feilmelding "Krasj1"
    - Dersom man blir frakoblet fra spillet kobler man ikke til igjen.
    - 

Dere må dokumentere hvordan prosjektet bygger, testes og kjøres, slik at det er lett å teste koden. Under vurdering kommer koden også til å brukertestes. 

    - Generell dokumentasjon av metoder og klasser gjennom prosjekt mappen.
    
    - Spillet kjøres gjennom Main der man velger gamemode(Være host, joine en annen host eller spille mot AI).
      !Kan også kjøres gjennom gameServerTest dersom det skulle oppstå problemer med main.
    - Gjennom prosjektet har vi brukt ulike måter å teste produktet på,
      dette innebærer både Junit tester og manuell testing. 
      Manuell testing har blitt brukt for å sjekke etter eventuelle bugs som oppstår ved fremtvunget adferd av spiller. Ved hjelp av dette har vi funnet bugs som ikke oppstod ved normal spilling. Dette var i hovedsak ting som oppstod ved “spamming” av enkelte kommandoer og hyppige gjentakelser av enkelte handlinger.
      Junit testene våres går på mer “tekniske” løsninger, slik som multiplayer, at commands utfører de riktige handlingene, hvordan kortene "lages"nfungerer og at vegger fungerer som de skal blant annet.

Dokumentér også hvordan testene skal kjøres. 
    
    - Testene kjøres som Junit tester og kan kjøres individuelt. Resultatene fra kjøring av enkelte tester er dokumentert med screenshots i Deliverables mappen.


Kodekvalitet og testdekning vektlegges. Merk at testene dere skriver skal brukes i produktet. 

Legg også ved et klassediagram som viser de viktige delene av koden. Tilpass klassediagrammet slik at det gir leseren mest mulig informasjon (feks Intellij kan tilpasse klassediagram som genereres).
    
    - Klassediagrammet ligger ved i "Deliverables" folderen i prosjektet. 




#Møter:


02.04.19
Martin og Tellev til stede

Implementerte mock-up klasser av laserskytere og rullebånd.

03.04.19
Alle til stede


Fordelte ansvarsområder til hele gruppa og fant ut mer nøye hvordan reglene fungerer.

09.04.19
Jonas og Tellev til stede


Lagde basic runder og en del refaktorering slik at programmet skulle bli mer intuitivt.

10.04.19
Alle til stede, minus Martin

Alle jobbet med hvert sitt. Jonas og Tellev med runder, Eirik og Lars med brett, grafisk.

**Etter dette har vi ikke hatt offisielle “møter” men har jobbet mer sammen i mindre grupper, og hyppigere enn før.**
