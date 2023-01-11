package ar.alopexmm.withholdingEarnings.service

import ar.alopexmm.withholdingEarnings.repository.WorkerRepository
import ar.alopexmm.withholdingEarnings.model.Worker
import ar.alopexmm.withholdingEarnings.repository.ConceptosDeduciblesRepository
import ar.alopexmm.withholdingEarnings.repository.DeduccionEspecialIncrementadaRepository
import ar.alopexmm.withholdingEarnings.repository.ImpuestoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WorkerService : WorkerRepository {

    @Autowired
    lateinit var conceptosDeduciblesDb: ConceptosDeduciblesRepository

    @Autowired
    lateinit var impuestosDb: ImpuestoRepository

    @Autowired
    lateinit var deduccionEspecialIncrementadaDb: DeduccionEspecialIncrementadaRepository

    override fun dataWorker(): Worker {
        return Worker(
            salario = 0.0,
            conyuge = false,
            hijo = 0,
            hijoHincapacitado = 0,
            monotributo = false,
            horasExtrasDiasLaborables = 0,
            horasExtrasDiasNoLaborables = 0,
            bonos = 0
        )
    }

    fun calcRetention(worker: Worker): HashMap<String, Double> {
        // Datos en conceptosDeduciblesDb
        val conceptosDeducibles = conceptosDeduciblesDb.findAll()

        // Datos en impuestosDb
        val impuestos = impuestosDb.findAll()

        // Datos en deduccionEspecialIncrementadaDb
        val deduccionesEspecialIncrementada = deduccionEspecialIncrementadaDb.findAll()

        // Datos en Worker
        val salario = worker.salario
        var conyuge = if (worker.conyuge) {conceptosDeducibles.filter { it.nombre == "conyuge" }[0].monto / 12} else { 0.0 }
        val hijo = conceptosDeducibles.filter {it.nombre == "hijo" }[0].monto / 12 * worker.hijo
        val hijoHincapacitado = conceptosDeducibles.filter {it.nombre == "hijoHincapacitado" }[0].monto / 12 * worker.hijoHincapacitado
        val monotributo = worker.monotributo
        // val horasExtrasDiasLaborales = worker.horasExtrasDiasLaborables
        // val horasExtrasDiasNoLaborables = worker.horasExtrasDiasNoLaborables
        // val bonos = worker.bonos

        // Este diccionario va a contener el resultado de los calculos
        val resultado : HashMap<String, Double> = HashMap<String, Double>()

        // Calculo de las deducciones
        val jubilacion = salario * 0.11
        val obraSocial = salario * 0.03
        val inssjp = salario * 0.03

        // Haberes luego de quitar jubilacion, obra social e inssjp
        var haberesGanados: Double = salario - jubilacion - obraSocial - inssjp
        // Haberes netos sujetos a impuestos
        var haberesNetosSujetosDeImpuestos = haberesGanados

        // Verificamos si corresponde o no calcular ganancias
        if ( salario < 330000.0) {
            resultado.put("sueldoNeto", haberesGanados)
            resultado.put("impuestoGanancias", 0.0)
            resultado.put("cargasDeFamilia", 0.0)
            resultado.put("deduccionEspecial", 0.0)
            resultado.put("deduccionEspecialIncrementada", 0.0)
            resultado.put("alicuotaFija", 0.0)
            resultado.put("alicuotaVariable", 0.0)
            resultado.put("gananciaNetaSujetaDeImpuestos", 0.0)
            resultado.put("sueldoBruto",salario)
            resultado.put("deduccionesGenerales",0.0)
            resultado.put("gananciaNoImponible", 0.0)


            return resultado
        }

        // Calculo de horas extras
        /*
        * TODO
        * Ver video de youtube para saber el correspondiente calculo segun el dia de la semana
        */

        // Calculo para el pago de bonos
        /*
        * TODO ver video de youtube
        */

        // Calcular el importe deducible por conyuge a cargo
        haberesNetosSujetosDeImpuestos -= conyuge

        // Calculo de importe deducible para hijos e hijos con discapacidad
        haberesNetosSujetosDeImpuestos -= hijo
        haberesNetosSujetosDeImpuestos -= hijoHincapacitado

        // Calculo de la deduccion especial incrementada
        val deduccionEspecialIncrementada: Double = deduccionesEspecialIncrementada.filter { it.sueldoBruto >= salario }[0].deduccion

        if (salario > 330000.0) {
            haberesNetosSujetosDeImpuestos -= deduccionEspecialIncrementada
        }

        // Quitamos el monto de deduccion especial por ser empleado siempre que monotributo sea false
        val deduccionEspecialApartadoDos = conceptosDeducibles.filter { it.nombre == "deduccionEspecialApartadoDos" }[0].monto / 12
        if (monotributo == false) {
            haberesNetosSujetosDeImpuestos -= deduccionEspecialApartadoDos
        }

        // Quitamos la ganancias no imponibles
        val gananciaNoImponible = conceptosDeducibles.filter { it.nombre == "gananciasNoImponible" }[0].monto / 12
        haberesNetosSujetosDeImpuestos -= gananciaNoImponible

        /*
        * Calculo para iigg fijo e iigg variable
        * Para esto tenemos que obtener el valor anual del salario que se encuentra sujeto a impuestos
        * Esto se consigue revisando la tabla de alicuotas que contine los montos fijos y variables que varian segun
        * el monto sujeto a impuestos del salario anualizado.
        * Otra deduccion que se sumo en 2021 es la tabla de deducciones especiales incrementada, esta revisa el rango en
        * el que se encuentra el sueldo promedio y asigna un valor que se resta al monto del sueldo sujeto a impuestos
        */

        // Buscamos las alicuotas correspondientes para llevar adelante el calculo
        val haberesNetosSujetosDeImpuestosAnual = haberesNetosSujetosDeImpuestos * 13
        val iiggFijo = impuestos.filter { it.limite_inferior < haberesNetosSujetosDeImpuestosAnual && it.limite_superior > haberesNetosSujetosDeImpuestosAnual }[0].cuota_fija / 12
        val iiggVariableAnual = impuestos.filter { it.limite_inferior < haberesNetosSujetosDeImpuestosAnual && it.limite_superior > haberesNetosSujetosDeImpuestosAnual }[0].sobre_exedente_de
        val alicuota = impuestos.filter { it.limite_inferior < haberesNetosSujetosDeImpuestosAnual && it.limite_superior > haberesNetosSujetosDeImpuestosAnual }[0].alicuota.toDouble()
        // Calculamos el monto de ganancia
        val ganancias = iiggFijo + ((haberesNetosSujetosDeImpuestosAnual - iiggVariableAnual) * (alicuota / 100) / 12)

        haberesGanados -= ganancias

        // Llenamo resultado con todos los datos
        // TODO pasar a string todas las variables que lleven porcentaje
        resultado.put("sueldoNeto", haberesGanados)
        resultado.put("impuestoGanancias", ganancias)
        resultado.put("cargasDeFamilia", (hijo + hijoHincapacitado + conyuge))
        resultado.put("deduccionEspecial", deduccionEspecialApartadoDos)
        resultado.put("deduccionEspecialIncrementada", deduccionEspecialIncrementada)
        resultado.put("alicuotaFija", iiggFijo)
        resultado.put("alicuotaVariable", alicuota)
        resultado.put("gananciaNetaSujetaDeImpuestos", haberesNetosSujetosDeImpuestos)
        resultado.put("sueldoBruto",salario)
        resultado.put("gananciaNoImponible",gananciaNoImponible)
        resultado.put("deduccionesGenerales", (jubilacion + obraSocial + inssjp))

        return resultado
    }
}