package com.PersonalDrive.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "files")
@ToString
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter @Column(name = "id")
    private int id;

    @Getter @Setter @Column(name = "name")
    private String name;

    @Getter @Setter @ManyToOne @JoinColumn(name="user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = true)
    @Getter @Setter
    private Folder folder;

}
