package net.ddns.buckeyeflash.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "guest")
public class Guest {

    private static final int FIRST_NAME_LENGTH = 50;
    private static final int LAST_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "first_name", nullable = false, length = FIRST_NAME_LENGTH)
    @NotNull
    @Size(min = 1, max = FIRST_NAME_LENGTH)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = LAST_NAME_LENGTH)
    @NotNull
    @Size(min = 1, max = LAST_NAME_LENGTH)
    private String lastName;

    @Column(name = "attendance")
    private Boolean attendance;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = Food.class)
    @JoinColumn(name = "food_id")
    private Food food;

    @Column(name = "dietary_concerns")
    private Boolean dietaryConcerns;

    @Column(name = "dietary_comments")
    private String dietaryComments;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Invitation.class)
    @JoinColumn(name = "invite_id", nullable = false)
    private Invitation invitation;

    private Boolean invitedPerson = Boolean.FALSE;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public Boolean getInvitedPerson() {
        return invitedPerson;
    }

    public void setInvitedPerson(Boolean invitedPerson) {
        this.invitedPerson = invitedPerson;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.trim();
    }

    public Boolean getAttendance() {
        return attendance;
    }

    public void setAttendance(Boolean attendance) {
        this.attendance = attendance;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

    public Boolean getDietaryConcerns() {
        return dietaryConcerns;
    }

    public void setDietaryConcerns(Boolean dietaryConcerns) {
        this.dietaryConcerns = dietaryConcerns;
    }

    public String getDietaryComments() {
        return dietaryComments;
    }

    public void setDietaryComments(String dietaryComments) {
        this.dietaryComments = dietaryComments;
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }
}
