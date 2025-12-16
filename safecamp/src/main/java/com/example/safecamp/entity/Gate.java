package com.example.safecamp.entity;

import com.example.safecamp.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="gates")
public class Gate extends BaseEntity{
    @Column(nullable=false, unique =true)
    private String name;

    @Column
    private String location;
}
