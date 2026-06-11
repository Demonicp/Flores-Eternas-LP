package flores.eternas.backend.model;

import flores.eternas.backend.model.enums.Estado;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "id_pedido"))
public class Pedido extends AbstractEntity {

    @Column(name = "total_pedido")
    private BigDecimal totalPedido;

    @Column(name = "direccion_entrega")
    private String direccionEntrega;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Persona cliente;

    @ManyToOne
    @JoinColumn(name = "id_pago")
    private MetodoPago metodoPago;

    @Column(name = "monto_pagado")
    private BigDecimal montoPagado;

    @Column(name = "tipo_pedido")
    private String tipoPedido;

    @Column(name = "email_cliente")
    private String emailCliente;

    @Enumerated(EnumType.STRING)
    private Estado estado;
}
