package com.trendsnatch.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    private double precio;
    private int stock;
    private String categoria;
    private String urlVideoFuente;
    private String urlImagenReferencia;
    private String redSocialOrigen;
    private boolean visible = true;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion = new Date();
}