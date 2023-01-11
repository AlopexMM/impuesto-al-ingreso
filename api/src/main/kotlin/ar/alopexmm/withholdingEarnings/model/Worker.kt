package ar.alopexmm.withholdingEarnings.model

data class Worker (
    val salario: Double,
    val conyuge: Boolean,
    val hijo: Int,
    val hijoHincapacitado: Int,
    val monotributo: Boolean,
    val horasExtrasDiasLaborables: Int,
    val horasExtrasDiasNoLaborables: Int,
    val bonos: Int,
    )