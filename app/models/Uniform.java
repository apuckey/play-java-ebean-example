package models;

import io.ebean.annotation.History;
import play.data.validation.Constraints;

import javax.persistence.Entity;

/**
 * Computer entity managed by Ebean
 */
@Entity
@History
public class Uniform extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Constraints.Required
    public String name;

    
}

