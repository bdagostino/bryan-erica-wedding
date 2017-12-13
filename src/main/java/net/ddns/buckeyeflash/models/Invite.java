package net.ddns.buckeyeflash.models;

import javax.persistence.*;
import java.util.List;

@Entity
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToMany
    private List<Guest> invitedGuests;

    @OneToMany
    private List<Guest> additionalGuests;

    private Integer maxAdditionalGuets;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Guest> getInvitedGuests() {
        return invitedGuests;
    }

    public void setInvitedGuests(List<Guest> invitedGuests) {
        this.invitedGuests = invitedGuests;
    }

    public List<Guest> getAdditionalGuests() {
        return additionalGuests;
    }

    public void setAdditionalGuests(List<Guest> additionalGuests) {
        this.additionalGuests = additionalGuests;
    }

    public Integer getMaxAdditionalGuets() {
        return maxAdditionalGuets;
    }

    public void setMaxAdditionalGuets(Integer maxAdditionalGuets) {
        this.maxAdditionalGuets = maxAdditionalGuets;
    }

}
