package com.furreverhome.Furrever_Home.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="shelter")
public class Shelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long capacity;

    private String contact;

    @Lob
    @Column(length = 100000)
    private String imageBase64;


    @Lob
    @Column(length = 100000)
    private String license;


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String address;

    private String city;

    private String country;

    private String zipcode;

    private boolean accepted;

    private boolean rejected;
}
