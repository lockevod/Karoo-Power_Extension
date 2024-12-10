package com.enderthor.kpower.data

import kotlin.math.atan
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.pow

import timber.log.Timber
import kotlin.math.exp


class CyclingWattageEstimator(
    private val gravity: Double = 9.80665,
    private val slope:  Double,
    private val totalMass: Double,
    private val rollingResistanceCoefficient: Double,
    private val dragCoefficient: Double,
    private val frontalArea: Double,
    private val speed:  Double,
    private val windSpeed: Double,
    private val powerLoss: Double,
    private val elevation: Double,
) {

    fun calculateCyclingWattage(): Double {

        val gravityForce = calculateGravityForce()
        val rollingResistanceForce = calculateRollingResistanceForce()
        val aerodynamicDragForce = calculateAerodynamicDragForce()
        val estimatedPower = ((gravityForce + rollingResistanceForce + aerodynamicDragForce) * speed * (1 - powerLoss).pow(-1))

        Timber.d("Data for cycling calculation: totalMass is $totalMass, gravity is $gravity, dragCoefficient is $dragCoefficient, windSpeed is $windSpeed, powerLoss is $powerLoss, rollingResistanceCoefficient is $rollingResistanceCoefficient,  speed is $speed, slope is $slope")
        Timber.d("Force cycling calculation: gravityForce is $gravityForce, rollingResistance is $rollingResistanceForce,aerodynamicDrag is $aerodynamicDragForce")
        Timber.d("Cycling Estimated Power is $estimatedPower")

        return estimatedPower
    }

    private fun calculateGravityForce(): Double {
        return gravity * sin(atan(slope)) * totalMass
    }

    private fun calculateRollingResistanceForce(): Double {
        return gravity * cos(atan(slope)) * totalMass * rollingResistanceCoefficient
    }

    private fun calculateAerodynamicDragForce(): Double {
        val airDensity = 1.225 * exp(-0.00011856 * elevation)
        Timber.d("IN aeroForce dragCoefficient is $dragCoefficient, frontalArea is $frontalArea, airDensity is $airDensity, windSpeed is $windSpeed, speed is $speed, elevation is $elevation")
        return (0.5 * dragCoefficient * frontalArea * airDensity * (speed - windSpeed).pow(2))   // windspeed is subtracted because it is a headwind
    }
}