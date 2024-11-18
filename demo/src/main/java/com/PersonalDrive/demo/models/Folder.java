package com.PersonalDrive.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="folders")
@ToString
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int id;

    @Getter @Setter @Column(name="name")
    private String name;

    @ManyToOne
    @Getter @Setter
    @JoinColumn(name = "parent_folder_id", nullable = true)
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL) @Getter @Setter
    private List<Folder> subFolders = new ArrayList<>();

    @ManyToOne
    @Getter @Setter
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL) @Getter @Setter
    private List<File> files = new ArrayList<>();

}
