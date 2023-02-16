package data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Loan {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person personId;

    private int amount;

    @Column(name = "date")
    private Date dateOfAccepting;

    private double rate;

    private int period;

    @Column(name = "repaid_amount")
    private int repaidAmount;

    @Column(name = "is_repaid")
    private boolean isRepaid;

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", amount=" + amount +
                ", dateOfAccepting=" + dateOfAccepting +
                ", rate=" + rate +
                ", period=" + period +
                ", repaidAmount=" + repaidAmount +
                ", isRepaid=" + isRepaid +
                '}';
    }
}

