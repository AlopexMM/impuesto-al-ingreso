package ar.alopexmm.withholdingEarnings.repository

import ar.alopexmm.withholdingEarnings.model.Impuesto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ImpuestoRepository : CrudRepository<Impuesto, Int>