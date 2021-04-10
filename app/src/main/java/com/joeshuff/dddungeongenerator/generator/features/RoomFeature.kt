package com.joeshuff.dddungeongenerator.generator.features

interface RoomFeature {
    fun getPageForMoreInfo(): Int
    fun getFeatureName(): String
    fun getFeatureDescription(): String
}