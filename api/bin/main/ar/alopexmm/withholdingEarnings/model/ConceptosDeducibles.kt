package ar.alopexmm.withholdingEarnings.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "conceptosdeducibles")
data class ConceptosDeducibles(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,
    @Column
    val nombre: String,
    @Column
    val monto: Double
) {
    private  constructor() : this(0,"",0.0)
}
