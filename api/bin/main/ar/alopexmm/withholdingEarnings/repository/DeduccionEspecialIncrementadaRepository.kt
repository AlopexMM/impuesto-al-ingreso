package ar.alopexmm.withholdingEarnings.repository

import ar.alopexmm.withholdingEarnings.model.DeduccionEspecialIncrementada
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeduccionEspecialIncrementadaRepository: CrudRepository<DeduccionEspecialIncrementada, Int>