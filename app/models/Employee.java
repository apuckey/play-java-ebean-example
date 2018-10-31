package models;

import io.ebean.annotation.History;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;

/**
 * Computer entity managed by Ebean
 */
@Entity
@History
public class Employee extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Constraints.Required
    public String name;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Uniform> uniforms;
}

