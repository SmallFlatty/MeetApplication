package com.meetapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "users")
@NoArgsConstructor

@Setter
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "role")
    private String role;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL)
    private List<MeetEntity> meetEntities;

}
