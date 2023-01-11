package ar.alopexmm.withholdingEarnings.repository

import ar.alopexmm.withholdingEarnings.model.ConceptosDeducibles
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConceptosDeduciblesRepository: CrudRepository<ConceptosDeducibles, Int>