package com.test.eclipselink.model.mappedsuperclass;

import javax.persistence.*;

@MappedSuperclass
public class Shape {
    @Id
    @SequenceGenerator(name = "SEQ", sequenceName = "SEQ_GEN_SEQUENCE_SH", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
    private Long id;

    private Integer area;
    protected Integer sides;

    public Long getId() {
        return id;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getSides() {
        return sides;
    }

}
