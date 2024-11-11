package com.PersonalDrive.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name="users")
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private int id;

    @Getter @Setter @Column(name = "name")
    private String name;

    @Getter @Setter @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<File> files;



}
