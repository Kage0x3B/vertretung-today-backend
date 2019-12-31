package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.SubstitutionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Eine Tabelle mit *allen* Vertretungsplan Einträgen, jeweils ein Einträg für jede Reihe in den Vertretungsplan Tabellen auf Moodle
@Repository
public interface SubstitutionInfoRepository extends JpaRepository<SubstitutionEntry, String> {
	// Diese Methoden werden automatisch generiert und dabei wird aus dem Namen Informationen um eine SQL Abfrage zu generieren extrahiert,
	// deswegen die komischen, teilweise langen, Namen.
	// ---
	// Beispiel:
	// Aus "findAllByDay" nimmt es
	// * find -> es soll eine SELECT ... FROM ... Anfrage werden
	// * All -> es wird ein oder mehr Einträge erwartet, also kein "... LIMIT 1" soll benutzt werden
	// * ByDay -> es wird eine WHERE-clause generiert mit dem ersten Parameter der Methode also:
	//            "WHERE day = ?" (wobei ? durch den Parameter der Methode ersetzt wird)
	List<SubstitutionEntry> findAllByDay(LocalDate day);

	List<SubstitutionEntry> findAllByDayAfter(LocalDate day);

	List<SubstitutionEntry> findAllByDayAndGrade(LocalDate day, int grade);

	// Das hier generiert z.B. ungefähr dieses SQL:
	// "SELECT * FROM substitution_info WHERE day = ? AND grade = ?"
	// und die ? werden durch die Parameter ersetzt
	List<SubstitutionEntry> findAllByDayAfterAndGrade(LocalDate day, int grade);
}