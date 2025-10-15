package com.meetapp.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name ="Users_Status")
@NoArgsConstructor

@Getter
@Setter
public class UserStatusEntity {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="User_Id")
    private long userId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "status")
    private String status;

}
