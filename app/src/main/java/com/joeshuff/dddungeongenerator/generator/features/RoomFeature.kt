package com.joeshuff.dddungeongenerator.generator.features

abstract class RoomFeature {
    abstract fun getPageForMoreInfo(): Int
    abstract fun getFeatureName(): String
    abstract fun getFeatureDescription(): String
}