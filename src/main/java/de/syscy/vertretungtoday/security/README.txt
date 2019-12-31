Dieser ganze Ordner kümmert sich um die Authentifizierung mit der API mit Hilfe von JSON Web Tokens (oder auch JWT).
Ganz grob funktioniert das so:

- Client stellt eine Anfrage wenn der Benutzer sich einloggt mit Benutzername und Passwort
--> Server überprüft Benutzername und Passwort (in service.AuthenticationService) und erstellt ein JWT
    Dieser JWT enthält den Benutzernamen, Gültigkeitsdatum und eine Signatur um zu überprüfen ob der JWT valide ist oder modifiziert wurde
- Client bekommt JWT und speichert ihn Lokal für spätere Anfragen bei der REST API
=========
- Jede weitere Anfrage hat dann einen HTTP Header "Authentication", der das JWT enthält
- Server überprüft den JWT ob dieser gültig ist (Über die Signatur und das verbundene Gültigkeitsdatum)
-> Alle Anfragen an die API (außer die /auth/ Route, also einloggen usw natürlich) brauchen einen gültigen JWT, sonst wird mit einem Unauthorized Status geantwortet

<!!==!!==!!==!!==!!==!!>
Der Rest dieses Ordners enthält nicht viele Kommentare in den eigentlichen Dateien,
wenn eine noch genauere Beschreibung gewünscht ist kann ich diese aber ihnen noch geben.
<!!==!!==!!==!!==!!==!!>