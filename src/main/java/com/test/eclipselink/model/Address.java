package com.test.eclipselink.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class Address {

    @Id
    @SequenceGenerator(name = "SEQ", sequenceName = "SEQ_GEN_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    private Long id;

    private String name;
    private String postcode;
    private boolean tt;

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

    public String getPostcode() {
	return postcode;
    }

    public void setPostcode(String postcode) {
	this.postcode = postcode;
    }

    public boolean isTt() {
	return tt;
    }

    public void setTt(boolean tt) {
	this.tt = tt;
    }

}
