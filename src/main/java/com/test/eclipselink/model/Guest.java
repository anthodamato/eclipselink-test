package com.test.eclipselink.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@IdClass(GuestPk.class)
@Entity
public class Guest {
    @Id
    private long id;

    @Id
    @ManyToOne
    private GuestBooking guestBooking;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuestBooking getGuestBooking() {
        return guestBooking;
    }

    public void setGuestBooking(GuestBooking guestBooking) {
        this.guestBooking = guestBooking;
    }
}
