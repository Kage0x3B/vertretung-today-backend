package de.syscy.vertretungtoday.security.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@NamedQuery(name = "Account.findByUsername", query = "SELECT a FROM Account a WHERE LOWER(a.username) = LOWER(?1)")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String email;
	private String username;
	private String password;

	private Date creationTime;
	private boolean validated = false;

	@PrePersist
	public void prePersist() {
		creationTime = new Date();
	}
}