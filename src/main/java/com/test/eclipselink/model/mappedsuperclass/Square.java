package com.test.eclipselink.model.mappedsuperclass;

import javax.persistence.Entity;

@Entity
public class Square extends Shape {

	public Square() {
		super();
		this.sides = 4;
	}

}
