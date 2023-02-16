package data.entities;

import lombok.Data;

import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "Person_Login_Account")
public class PersonAccountLogin {

    @Id
    private String login;

    private String password;

    @OneToOne(mappedBy = "id")
    @JoinColumn(name = "person_id")
    private Person personId;

}
