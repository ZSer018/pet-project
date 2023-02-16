package data.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private int id;

    private Date datetime;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "sender_account_id")
    private Account senderAccountId;

    @OneToOne
    @JoinColumn(name = "receiver_account_id")
    private Account receiverAccountId;

    private int amount;

    @Column(name = "transaction_type")
    private int transactionType;

  //  alter table Transaction add constraint FKsrhhxkuajr9w0f3n3y6vvh55o foreign key (sender_account_id) references Account (account_num)

    private String transactionDataToStr(){
        String type = transactionType == 0? " receive": " send";
        String fromTo = transactionType == 0? "from "+senderAccountId.getPersonId(): "to "+receiverAccountId.getPersonId();
        return type + ", "+ fromTo;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", datetime=" + datetime +
                transactionDataToStr() +
               // ", senderAccountId=" + senderAccountId +
               // ", receiverAccountId=" + receiverAccountId +
                ", amount=" + amount +
               // ", transactionType=" + transactionTypeToStr() +
                '}';
    }
}


