package com.cs.cs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "windows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Window {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "window_number", nullable = false)
    private Integer windowNumber;


}