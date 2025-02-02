package com.dag.productservice.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Price extends V0 {
    @Column
    String currency;
    @Column
    double price;
}
