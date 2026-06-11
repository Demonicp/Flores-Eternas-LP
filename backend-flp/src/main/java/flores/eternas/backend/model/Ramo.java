package flores.eternas.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name = "ramo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_ramo"))
public class Ramo extends AbstractEntity {

    @Column(name = "precio_ramo")
    private BigDecimal precioRamo;

    @Column(name = "nombre_ramo")
    private String nombreRamo;

    @Column(name = "descripcion_corta")
    private String descripcionCorta;

    @Column(name = "descripcion_ramo")
    private String descripcionRamo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "disponible")
    private Boolean disponible = true;

    @Lob
    @Column(name = "foto_ramo")
    private String fotoRamo;

    @OneToMany(mappedBy = "ramo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRamo> detallesRamo;

    @ManyToOne
    @JoinColumn(name = "id_categoria_ramo")
    private CategoriaRamo categoriaRamo;
}
