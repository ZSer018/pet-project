package data.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Guarantor {

    @Id
    @Column(name = "id")
    private int id;

    @JoinColumn(name = "person_id")
    @ManyToOne
    private Person personId;

    @JoinColumn(name = "guarantor_id")
    @OneToOne
    private Person guarantorId;


    @Override
    public String toString() {
        return "Guarantor{" +
                "id=" + id +
                ", guarantorId=" + guarantorId +
                '}';
    }
}
