package ar.alopexmm.withholdingEarnings.model

import javax.persistence.*

@Entity(name = "deduccionespecialincrementada")
data class DeduccionEspecialIncrementada(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column
    val sueldoBruto: Double,
    @Column
    val deduccion: Double
) {
    private constructor() : this(0,0.0,0.0)
}
