package com.test.eclipselink.model;

import javax.persistence.*;

@Entity
public class GenericList {
    @Id
    @SequenceGenerator(name = "SEQ", sequenceName = "SEQ_GEN_SEQUENCE_GL", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    private Long id;

    private String name1;

    private String name2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

}
