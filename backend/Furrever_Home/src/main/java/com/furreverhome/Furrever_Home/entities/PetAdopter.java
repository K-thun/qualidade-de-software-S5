package com.furreverhome.Furrever_Home.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name="petadopter")
public class PetAdopter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String phone_number;

    private String address;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String city;

    private String country;

    private String zipcode;

}
