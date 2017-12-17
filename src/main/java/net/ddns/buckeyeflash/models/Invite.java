package net.ddns.buckeyeflash.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invite")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invite", targetEntity = Guest.class)
    @Column(name = "guest_list")
    private List<Guest> guestList;

    @Column(name = "max_guests")
    private Integer maxAdditionalGuests;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Guest> getGuestList() {
        if (this.guestList == null) {
            this.guestList = new ArrayList<>();
        }
        return guestList;
    }

    public void setGuestList(List<Guest> guestList) {
        this.guestList = guestList;
    }

    public Integer getMaxAdditionalGuests() {
        return maxAdditionalGuests;
    }

    public void setMaxAdditionalGuests(Integer maxAdditionalGuests) {
        this.maxAdditionalGuests = maxAdditionalGuests;
    }
}