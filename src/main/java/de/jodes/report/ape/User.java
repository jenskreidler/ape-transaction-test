package de.jodes.report.ape;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class User {

    @Id
    @GeneratedValue(generator = "userSequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userSequence", sequenceName = "user_seq", allocationSize = 1)
    @Column(updatable = false)
    private Long id;

    @Column
    private String login;

    public User() {
        // default
    }

    public User(String login) {
        this.login = login;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
