###Obligatorisk øvelse 4

**Deloppgave 1:** 

Gruppen har blitt enige om at Lars er testansvarlig. 

Tester: 

Gruppen valgte å gå for både automatiske og manuelle tester. De manuelle testene ble gjennomført av testansvarlig ved å kjøre applikasjonen og styre roboten på kartet ved hjelp av midlertidige funksjoner.


Et problem som oppstod ved start var at spillbrettet ikke ble vist fullstendig. Dette kunne fikses ved å dra kartet til en annen posisjon med musen.


Test 1:

Første testen var å trykke på kortstokken i høyre hjørne for å dele ut kort.


Dette fungerte, så neste test var å starte programmet på nytt et par ganger for å sikre at det ble delt ut forskjellige kort hver gang.   


Test 2:

Plassere fem kort i kortleserene ved å dra dem med musen.


Test 3:

Trykke på ‘E’ tasten for å kjøre kommandoen til de valgte bevegelses kortene for å sikre at roboten utfører de rette bevegelsene. Dette er en midlertidig testfunksjon.


Test 4:

Bruke piltastene på tastaturet for å utføre kommandoer på roboten. Dette er en testfunksjon. Navigering til et flag.


Det er lagt til en enkel System.out.print her for å vise at systemet har registrert at en bestemt spiller har besøkt et bestemt flag. “Flag 2 registered by Player-3”

Test 5:

Navigere til en skiftenøkkel med piltastene.

Det spilles av en lydeffekt når roboten besøker et felt som inneholder en skiftenøkkel. Dette gjør at man vet at roboten har besøkt et felt som inneholder en skiftenøkkel.

Test 6:

La roboten dø. I koden har gruppen lagt til en testfunksjon som lar roboten dø ved å trykke på “K”. Roboten forsvinner da fra kartet etter at det gjøres en liten animasjon med dens tekstur og blir gjenopplivet på det sist besøkte flagget/skiftenøkkelen.


Test 7:

Navigere til et hull i kartet med piltastene.


Her dør roboten i et hull og blir gjenopplivet på sin opprinnelige startposisjon eller på sist besøkte flagg eller skiftenøkkel. Dette er midlertidig.

Test 8:

Ved kjøring av programmet skrives det ut informasjon om kortene spillerne får tildelt og om dimensjonene til spillebrettet i terminalen.


Test 9:

Navigere til alle flag som er plassert på kartet for å vinne.


Prosjektmetodikken til gruppen er den samme som tidligere brukt i prosjektarbeidet. Vi bruker fortsatt project board på GitHub, noe som fungerer bra for gruppen. Den siste uken har gruppen møttes flere ganger for å jobbe sammen utenom de planlagte møtene, dette var nødvendig for å nå målene for denne innleveringen. Dette har ført til en høyere progresjon og gruppen er innstilt på å opprettholde den samme hyppigheten framover. 

Gruppedynamikken og kommunikasjonen i gruppen er veldig god, ingenting har endret seg på disse områdene siden forrige obligatoriske innlevering. 

I retroperspektiv så har ikke gruppen klart å arbeide så jevnt som ønsket etter forrige innlevering, dette førte til mye arbeid veldig tett opp mot innleveringsfristen. Det er flere grunner til dette, vi har en del sykdom i gruppen og det er mye arbeid i andre fag som også må gjøres.

Gruppen klarte ikke å nå alle målene som ønsket for denne innleveringen. I retroperspektiv var målene litt ambisiøse i forhold til gruppens forventninger av hva som krevdes for å nå målene. 

Basert på vurdering av arbeidet til gruppen er forbedringsområdene de samme som ved forrige obligatoriske innlevering: 
- Hyppigheten på arbeid med oppgaver kan økes for å oppnå en jevnere arbeidsflyt.
- Gruppen kan møtes oftere for å jobbe med oppgavene sammen. Dette vil føre til bedre innsikt i prosjektets helhet og problemstillinger kan diskuteres og løses bedre, noe som igjen vil føre til en høyere kvalitet på arbeidet. 
- Gruppen er ikke så flink til å dele problemer som oppstår underveis i arbeidet med resten av medlemmene. Dette er noe som kan sinke effektiviteten for utførelsen av arbeidet og gir en redusert oversikt over prosjektets helhet. 

Gruppen har drevet litt kompetanseoverføring i form av parprogrammering på deler av kode-basen på et område hvor en i paret for eksempel ikke er så godt kjent, og ved å la gruppemedlemmene som hittil ikke har programmert så mye få prøve seg på å implementere noen nødvendige metoder.


**Deloppgave 2:** 

For denne obligen valgte vi følgende MVP’er:

- Man må kunne vinne spillet spillet ved å besøke siste flagg (fullføre et spill)
  Fordi dette er veldig grunnleggende i spillet og bør gå fort å implementere.

- Skademekanismer (spilleren får færre kort ved skade)
  Dette er fordi vi vil begynne å få til grafikk, lyd og den grunnleggende logikken som skal brukes videre i koden.

- Spillmekanismer for å skyte andre spillere innen rekkevidde med laser som peker rett frem
  Enkelt å lage, kult å se på.

- Game over etter 3 tapte liv
  Også veldig grunnleggende, og lett å få på plass.

- Plassere flagg selv før spillet starter
  For å kunne teste ut litt ulike brett. 



Av disse har gruppen fått til følgende MVP-krav:

- Man må kunne vinne spillet spillet ved å besøke siste flagg (fullføre et spill).

- Game over etter 3 tapte liv

Møtereferat

13.03.19
Alle til stede

-Diskuterte implementering av vegger, logisk og grafisk
-Jobbet med individuelle oppgaver

Neste steg:
Få i gang enkle runder, begynne med implementasjon av “utfør kort”, implementere kort, implementere laser, lage et system for animasjon, vegg .

20.03.19
Alle til stede, minus Martin

Vi jobbet bare med prosjektet og diskuterte litt implementasjon. 

26.03.19
Alle til stede, minus Eirik

Kom fram til hvilke av MVPene vi ville implementere til fredag.
Utnevnte Lars til testmester med terningkast.

27.03.19
Alle til stede, minus Tellev

Implementerte liv/helse til robot. Jobbet med dokumetering av manuelle tester.
Jobbet med et nytt rendringssystem for kortene for å kunne tegne prioriteten på kort.

28.03.19
Alle til stede, minus Eirik

Jobbet med liv/helse/deaths.
