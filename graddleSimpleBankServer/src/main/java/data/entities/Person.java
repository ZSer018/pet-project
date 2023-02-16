package data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Person {

    @Id
    private int id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    private String patronymic;

    @OneToMany(mappedBy = "personId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accountList;

    @OneToMany(mappedBy = "personId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Loan> loanList;

    @OneToMany(mappedBy = "personId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Guarantor> guarantorList;

    @OneToOne(mappedBy = "personId")
    private PersonData personData;





    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", patronymic='" + patronymic + '\'' +
                '}';
    }
}
