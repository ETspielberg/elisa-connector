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
libintel.elisa.callerid=<elisa caller id (vom hbz)>
libintel.elisa.secret=<elisa secret (vom hbz)>
libintel.elisa.userid.default=<elisa userid wenn kein Fach ausgewählt wurde>
libintel.eavs.email.default=<Standard-E-Mail, wenn der Eintrag in Elisa keinen Erfolg hatte>
libintel.eavs.email.from=<Die Adresse, die als Absender der E-Mails erscheint>
libintel.ebooks.email.default=<Die Adresse, an die E-Book-Anschaffungswünsche gesendet werden sollen>
```

Ist ein entsprechender Keystore vorhanden, können diese auch verschlüsselt abgelegt werden.

Der Elisa-Connector benötigt weiterhin einen Eureka-Server als Discovery-Service und einen Settings-Server, der die Zuordnung der userIDs zu den Fächer enthält.

Daten werden per POST an die Endpunkte gesendet. 
  