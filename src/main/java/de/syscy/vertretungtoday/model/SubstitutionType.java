package de.syscy.vertretungtoday.model;

import de.syscy.vertretungtoday.util.Util;

public enum SubstitutionType {
	SUBSTITUTION("Vertretung", "Vert", "retung"),
	CANCELLED("fällt aus", "fällt", "aus"),
	SWITCH("Tausch"),
	MOVED("Verlegung", "Verl", "egung"),
	ROOM_CHANGE("Raumänd", "Raum"),
	INDEPENDENT_WORKING("Stillarbeit", "Still"),
	OTHER;

	private final String[] searchStrings;

	SubstitutionType(String... searchStrings) {
		this.searchStrings = searchStrings != null ? searchStrings : new String[0];

		for(int i = 0; i < this.searchStrings.length; i++) {
			this.searchStrings[i] = Util.cleanString(searchStrings[i]);
		}
	}

	public static SubstitutionType parseType(String typeString) {
		typeString = Util.cleanString(typeString);

		for(SubstitutionType substitutionType : values()) {
			for(String searchString : substitutionType.searchStrings) {
				if(typeString.contains(searchString)) {
					return substitutionType;
				}
			}
		}

		return OTHER;
	}
}