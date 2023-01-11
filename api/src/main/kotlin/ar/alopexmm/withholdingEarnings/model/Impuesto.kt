package ar.alopexmm.withholdingEarnings.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "impuesto")
data class Impuesto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column
    val limite_inferior: Double,
    @Column
    val limite_superior: Double,
    @Column
    val cuota_fija: Double,
    @Column
    val alicuota: Int,
    @Column
    val sobre_exedente_de: Double
) {
    private constructor() : this(0,0.0,0.0,0.0,0,0.0)
}
