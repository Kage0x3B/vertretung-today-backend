package de.syscy.vertretungtoday.repository;

import de.syscy.vertretungtoday.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Eine Tabelle um die Benutzereinstellungen auf dem Server zu speichern
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, String> {

}