package ar.alopexmm.withholdingEarnings.repository

import ar.alopexmm.withholdingEarnings.model.Worker

interface WorkerRepository {
    fun dataWorker(): Worker
}