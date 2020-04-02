# elisa-connector

Der Elisa-Connector bietet eine Schnittstelle, um Anschffungswünsche direkt in das [ELi:SA-System](https://elisa.hbz-nrw.de/) des [Hochschulbibiothekszentrums](https://www.hbz-nrw.de/) abzulegen. 
Daten können dabei an drei Endpunkte gesendet werden. 
Über die Einbindung in die Lib:Intel-Plattform lassen sich die Zugriffsrechte steuern und eine Bestellmöglichkeit in verschiedene Dienste integrieren.

Die Konfiguration erfolgt über einen abgesichert Spring Cloud Config-Server, dessen Zugangsdaten als Umgebungsvariabelen
```
export LIBINTEL_CONFIG_USERNAME="<username>"
export LIBINTEL_CONFIG_PASSWORD="<password>"
```
festgelegt werden. In dem Config-Server müssen folgende Einstellungen hinterlegt werden:

```
libintel:
  elisa:
    callerid=<elisa caller id (vom hbz)>
    secret=<elisa secret (vom hbz)>
    userid
      :default=<elisa userid wenn kein Fach ausgewählt wurde>
  eavs:
    email:
      default=<Standard-E-Mail, wenn der Eintrag in Elisa keinen Erfolg hatte>
      from=<Die Adresse, die als Absender der E-Mails erscheint>
  ebooks:
    email:
      default=<Die Adresse, an die E-Book-Anschaffungswünsche gesendet werden sollen>
```

Für das Versenden der E-Mails ist die Konfiguration eines Mail-Services nötig: 

```
sprving:
  mail:
    host: <mailout server>
    port: <mailout port
    username: <username>
    password: <password>
    defaultEncoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

Soll der Service über Spring Security abgesichert werden, so müssen für diesen Service weiterhin ein Nutzer definiert werden:

```
spring:
  security:
    user:
      name: ${libintel_system_username:thedoctor}
      password: ${libintel_system_password:Tard15} 
```

Ist ein entsprechender Keystore vorhanden, können diese Properties auch verschlüsselt abgelegt werden.

Der Elisa-Connector benötigt weiterhin einen Eureka-Server als Discovery-Service und einen Settings-Server, der die Zuordnung der userIDs zu den Fächer enthält.

Die Anschaffungsvorschläge werden als JSON-Payload per POST an folgende Endpunkte gesendet:

* _/receiveEavLecturer_ : Für einen elektronischen Anschaffungsvorsschlag eines Lehrenden (frei)  
* _/receiveEavUser_ : Für einen Anschaffungsvorschlag eines Studierenden/Externen (frei)
* _/receiveEav_ : Für einen allgemeine Anschaffungsvorschlag (frei)
* _/sendToElisa_ : Direktempfang ELi:SA konformer Daten aus einem anderen System (Anmeldung erforderlich)
 
Das JSON-Format sieht dabei eine flache Struktur der Daten (mit Datentyp) vor. In allen Fälölen werden folgende Eigenschaften benötigt:

```
{
  "isbn": <ISBN, string>,
  "title": <Buchtitel, string>,
  "contributor": <Autor/Herausgeber, string>,
  "edition": <Auflage, string>,
  "publisher": <Verlag, string>,
  "year": <Jahr, string>,
  "price": <Preis, string>,
  "subjectarea": <Fächer-Code, string>,
  "comment": <Kommentar, string>,
  "name": <Name des Vorschlagenden, string>,
  "libraryaccountNumber": <Bibliotheksaccountnummer des Vorschlagenden, string>,
  "emailAddress": <E-Mail-Adresse des Vorschlagenden, string>,
  "ebookDesired": <E-Book-Wunsch, boolean>
}    
```
Für Anschaffungsvorschläge von Lehrenden kommen noch folgende Felder hinzu:
```
{
  "directToStock":<Keine Vormerkung gewünscht, boolean>,
  "personalAccount": <Vormerkung für den persönlichen Ausweis, boolean>,
  "happAccount": <Vormerkung für den Handapparat, boolean>,
  "semAppAccount": <Vormerkung für einen Semesterapparat, boolean>,
  "semAppNumber": <Semesterapparatsnummer, string>,
  "source": <Quelle der Literaturangabe>
}    
```
und für Studierende/Externe:
```
{
  "response": <Benachrichtigungswunsch über Kaufentscheidung, boolean>,
  "essen": <Anschaffung für Essen>,
  "duisburg": <Anschaffung für Duisburg>,
  "requestPlace": <Ort für die Vormerkung, string>,
  "source": <Quelle der Literaturangabe>
}    
```





