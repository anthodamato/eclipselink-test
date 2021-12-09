package com.test.eclipselink.model.manytoone;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Department {

    @Id
    @SequenceGenerator(name = "SEQ_DEP", sequenceName = "SEQ_GEN_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DEP")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department")
    private Collection<Employee> employees = new HashSet<>();

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

    public Collection<Employee> getEmployees() {
	return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
	this.employees = employees;
    }
}
