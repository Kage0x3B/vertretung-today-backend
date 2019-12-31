Das Event System hilft das Programm etwas modularer zu halten, ich versuche mit Events und den zugehörigen Listenern
einzelne Programmteile aufzuteilen.
Bis jetzt gibt es erst zwei Events (jeweils mit zugehöriger Publisher Klasse, macht man bei Spring anscheinend so),
eins wenn Dateien von Moodle heruntergeladen wurden und diese dann weiter verarbeitet werden sollen zu
internen Daten Repräsentationen und ein weiteres Event wenn nach dem weiterverarbeiten diese Daten
abgespeichert werden können.