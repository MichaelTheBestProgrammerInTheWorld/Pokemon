package com.michaelmagdy.pokemon

import android.location.Location

class Pokemon {

    var name:String ?= null
    var desc:String ?= null
    var image:Int ?= null
    var power:Int ?= null
    var location:Location ?= null
    var isCatched:Boolean ?= false

    constructor(
        name: String?,
        desc: String?,
        image: Int?,
        power: Int?,
        lat: Double?,
        long: Double?
    ) {
        this.name = name
        this.desc = desc
        this.image = image
        this.power = power
        this.location = Location(name)
        this.location!!.latitude = lat!!
        this.location!!.longitude = long!!
    }
}