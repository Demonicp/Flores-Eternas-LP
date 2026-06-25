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
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
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

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Persona cliente;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
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

    @Column(name = "pago_token", unique = true)
    private String pagoToken;

    @Column(name = "payu_reference_code")
    private String payuReferenceCode;

    @Column(name = "payu_transaction_id")
    private String payuTransactionId;

    @Column(name = "payu_estado")
    private String payuEstado;
}
