package data.entities;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "Person_data")
@Entity
@Data
public class PersonData {

    @Id
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person personId;

    private Date birthday;

    @Column(name = "passport_series")
    private String passportSeries;

    @Column(name = "passport_num")
    private int passportNum;

    @Column(name = "issued_by")
    private String issuedBy;

    private String address;


    @Override
    public String toString() {
        return "PersonData{" +
                "birthday=" + birthday +
                ", passportSeries='" + passportSeries + '\'' +
                ", passportNum=" + passportNum +
                ", issuedBy='" + issuedBy + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
