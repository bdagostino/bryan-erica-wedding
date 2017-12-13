package net.ddns.buckeyeflash.models;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Guest {

    private static final int FIRST_NAME_LENGTH = 50;
    private static final int LAST_NAME_LENGTH = 50;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = FIRST_NAME_LENGTH)
    @NotNull
    @Size(min = 1, max = FIRST_NAME_LENGTH)
    private String firstName;

    @Column(nullable = false, length = LAST_NAME_LENGTH)
    @NotNull
    @Size(min = 1, max = LAST_NAME_LENGTH)
    private String lastName;

    private Boolean attendance;

    @OneToOne
    private Food food;

    private Boolean dietaryConcerns;

    private String dietaryComments;

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
        this.firstName = StringUtils.trim(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = StringUtils.trim(lastName);
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
        this.dietaryComments = StringUtils.trim(dietaryComments);
    }
}
