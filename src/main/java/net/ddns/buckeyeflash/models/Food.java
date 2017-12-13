package net.ddns.buckeyeflash.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

@Entity
public class Food {

    private static final int TYPE_LENGTH = 25;
    private static final int DESCRIPTION_LENGTH = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, length = TYPE_LENGTH)
    @NotNull
    @Size(min = 1, max = TYPE_LENGTH)
    private String type;

    @Column(nullable = false, length = DESCRIPTION_LENGTH)
    @NotNull
    @Size(min = 1, max = DESCRIPTION_LENGTH)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = StringUtils.trim(type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trim(description);
    }
}
