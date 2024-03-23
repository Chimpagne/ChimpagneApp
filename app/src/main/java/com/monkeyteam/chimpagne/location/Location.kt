package com.monkeyteam.chimpagne.model.location

data class Location(
    val name: String,
    val latitude: Double,
    val longitude: Double,
){
    constructor() :
        this(
            name = "",
            latitude = 0.0,
            longitude = 0.0,
        )

    constructor(
        name: String
    ) : this(
        name = name,
        latitude = 0.0,
        longitude = 0.0,
    )
}
