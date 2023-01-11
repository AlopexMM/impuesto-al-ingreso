package ar.alopexmm.withholdingEarnings.controller
import ar.alopexmm.withholdingEarnings.model.Worker
import ar.alopexmm.withholdingEarnings.service.WorkerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profitretention/v1/api")
class WorkerController(private val service: WorkerService) {

    @GetMapping("/worker")
    fun retrieveWorker(): Worker {
        return service.dataWorker()
    }

    @PostMapping("/worker")
    fun calcRetention(@RequestBody worker: Worker): HashMap<String, Double> {
        return service.calcRetention(worker)
    }

}