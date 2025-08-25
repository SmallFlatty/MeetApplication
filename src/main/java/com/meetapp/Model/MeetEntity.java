package com.meetapp.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "meetings")
@NoArgsConstructor

@Getter
@Setter
public class MeetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "starts_at")
    private Date start_at;

    @Column(name = "end_at")
    private Date end_at;

    @Column(name = "customer_name")
    private String customer_name;

    @Column(name = "created_by")
    private String created_by;

    @Column(name = "created_at")
    private Date created_at;


}
