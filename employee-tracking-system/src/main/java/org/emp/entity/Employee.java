package org.emp.entity;

//import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;
//import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

@Entity
public class Employee extends PanacheEntity {

    private String name;
    private String emailId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }



    // Constructors, getters, setters, etc.


}
