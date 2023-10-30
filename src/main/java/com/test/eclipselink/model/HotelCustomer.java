package com.test.eclipselink.model;

import javax.persistence.*;

@Entity
public class HotelCustomer {
    @Id
    @SequenceGenerator(name = "SEQ", sequenceName = "SEQ_GEN_SEQUENCE_HC", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
