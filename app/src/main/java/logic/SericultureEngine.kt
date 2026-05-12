package com.reshmenamma.app.logic

import com.reshmenamma.app.data.entities.InstarStage

data class ClimateRange(
    val minTemp: Double,
    val maxTemp: Double,
    val minHumidity: Double,
    val maxHumidity: Double
)

data class ClimateAdvice(
    val status: ClimateStatus,
    val message: String,
    val actions: List<String>
)

enum class ClimateStatus {
    SAFE, CAUTION, DANGER
}

class SericultureEngine {

    companion object {
        // Ideal ranges for each instar stage
        private val stageRequirements = mapOf(
            InstarStage.STAGE_1 to ClimateRange(25.0, 28.0, 80.0, 90.0),
            InstarStage.STAGE_2 to ClimateRange(24.0, 27.0, 75.0, 85.0),
            InstarStage.STAGE_3 to ClimateRange(23.0, 26.0, 70.0, 80.0),
            InstarStage.STAGE_4 to ClimateRange(22.0, 25.0, 70.0, 80.0),
            InstarStage.STAGE_5 to ClimateRange(21.0, 24.0, 65.0, 75.0)
        )

        fun analyzeClimate(
            instarStage: InstarStage,
            temperature: Double,
            humidity: Double
        ): ClimateAdvice {
            val range = stageRequirements[instarStage] ?: stageRequirements[InstarStage.STAGE_1]!!

            val tempStatus = when {
                temperature in range.minTemp..range.maxTemp -> ClimateStatus.SAFE
                temperature in (range.minTemp - 2)..(range.minTemp - 0.1) -> ClimateStatus.CAUTION
                temperature in (range.maxTemp + 0.1)..(range.maxTemp + 2) -> ClimateStatus.CAUTION
                else -> ClimateStatus.DANGER
            }

            val humidityStatus = when {
                humidity in range.minHumidity..range.maxHumidity -> ClimateStatus.SAFE
                humidity in (range.minHumidity - 10)..(range.minHumidity - 0.1) -> ClimateStatus.CAUTION
                humidity in (range.maxHumidity + 0.1)..(range.maxHumidity + 10) -> ClimateStatus.CAUTION
                else -> ClimateStatus.DANGER
            }

            val overallStatus = if (tempStatus == ClimateStatus.DANGER || humidityStatus == ClimateStatus.DANGER) {
                ClimateStatus.DANGER
            } else if (tempStatus == ClimateStatus.CAUTION || humidityStatus == ClimateStatus.CAUTION) {
                ClimateStatus.CAUTION
            } else {
                ClimateStatus.SAFE
            }

            val actions = mutableListOf<String>()
            val message = buildString {
                append("Instar Stage ${instarStage.stage}: ${instarStage.stageName}\n\n")

                // Temperature analysis
                when {
                    temperature > range.maxTemp + 2 -> {
                        append("⚠️ CRITICAL: Temperature is dangerously high ($temperature°C)!\n")
                        actions.add("IMMEDIATE: Open all windows and doors")
                        actions.add("Spread wet gunny bags on the floor")
                        actions.add("Use exhaust fans if available")
                        actions.add("Sprinkle water on the roof")
                    }
                    temperature > range.maxTemp -> {
                        append("⚠️ Temperature is above optimal ($temperature°C)\n")
                        actions.add("Open windows for ventilation")
                        actions.add("Hang wet gunny bags near windows")
                        actions.add("Reduce density of silkworms if possible")
                    }
                    temperature < range.minTemp - 2 -> {
                        append("⚠️ CRITICAL: Temperature is dangerously low ($temperature°C)!\n")
                        actions.add("IMMEDIATE: Close all windows and doors")
                        actions.add("Use heating lamps or bulbs")
                        actions.add("Cover rearing trays with paper")
                        actions.add("Add straw insulation around trays")
                    }
                    temperature < range.minTemp -> {
                        append("⚠️ Temperature is below optimal ($temperature°C)\n")
                        actions.add("Close windows partially")
                        actions.add("Use 100W bulbs for extra warmth")
                        actions.add("Keep trays covered")
                    }
                    else -> {
                        append("✅ Temperature is within ideal range ($temperature°C)\n")
                    }
                }

                append("\n")

                // Humidity analysis
                when {
                    humidity > range.maxHumidity + 10 -> {
                        append("⚠️ CRITICAL: Humidity is too high ($humidity%)!\n")
                        actions.add("Increase ventilation immediately")
                        actions.add("Use dry lime powder near trays")
                        actions.add("Reduce water on floor")
                    }
                    humidity > range.maxHumidity -> {
                        append("⚠️ Humidity is above optimal ($humidity%)\n")
                        actions.add("Improve air circulation")
                        actions.add("Reduce water spraying")
                    }
                    humidity < range.minHumidity - 10 -> {
                        append("⚠️ CRITICAL: Humidity is too low ($humidity%)!\n")
                        actions.add("Spray water immediately on floors and walls")
                        actions.add("Place water containers around rearing house")
                        actions.add("Cover trays with wet cloths")
                    }
                    humidity < range.minHumidity -> {
                        append("⚠️ Humidity is below optimal ($humidity%)\n")
                        actions.add("Spray water on floor lightly")
                        actions.add("Hang wet cloths near trays")
                    }
                    else -> {
                        append("✅ Humidity is within ideal range ($humidity%)\n")
                    }
                }

                if (overallStatus == ClimateStatus.SAFE) {
                    append("\n🟢 All conditions are optimal! Continue current care routine.")
                } else if (overallStatus == ClimateStatus.CAUTION) {
                    append("\n🟡 Conditions need attention. Take corrective action soon.")
                } else {
                    append("\n🔴 URGENT: Conditions are critical! Take immediate action to prevent crop loss!")
                }
            }

            return ClimateAdvice(overallStatus, message, actions)
        }

        fun getIdealRange(instarStage: InstarStage): ClimateRange {
            return stageRequirements[instarStage] ?: stageRequirements[InstarStage.STAGE_1]!!
        }
    }
}
