package com.example.frisrplus

object servicesDataManager {

    val services = mutableListOf<Services>()

    init {
        createServices()
    }

    fun createServices() {

        services.add(Services("Barn klippning", "100kr."))
        services.add(Services("Pensionär klippning", "100kr."))
        services.add(Services("Vuxen hår klippning", " från 100kr."))
        services.add(Services("Vuxen skägg klippning", "från 70kr."))
        services.add(Services("Hår+Skägg klippning", "från 150kr."))
    }
}