package com.api.greetings.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Greeting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    private String text;
    private String erstelltAm;
    private String erstelltVon;
    private String geaendertAm;
    private String geaendertVon;

    public String toString() {
        return String.format(
                "Greeting[id=%d, text='%s', erstelltAm='%s', erstelltVon='%s', geaendertAm='%s', geaendertVon='%s']",
                id, text, erstelltAm, erstelltVon, geaendertAm, geaendertVon);
    }

}

