package net.ddns.buckeyeflash.models;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "invitation", targetEntity = Guest.class, orphanRemoval = true)
    @Column(name = "guest_list")
    private List<Guest> guestList;

    @Column(name = "max_guests")
    @NotNull
    private Integer maxGuests;

    @Column(name = "invitation_code")
    @NotNull
    private String invitationCode;

    @Transient
    private Integer removalIndex;

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

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Integer getRemovalIndex() {
        return removalIndex;
    }

    public void setRemovalIndex(Integer removalIndex) {
        this.removalIndex = removalIndex;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }
}