/*
 * com.example.ballsort.domain.model
 * Sonsuz ölçeklenebilir Saraylar & Hükümdarlar (Royal Pacing) seviye ve tema modeli.
 */
package com.example.ballsort.domain.model

enum class LevelTier {
    NORMAL,
    HARD,       // 🟣 Zor Bölüm (% 10 == 5)
    SUPER_HARD, // 🔴 Süper Zor Bölüm (% 10 == 9)
    BOSS,       // 👑 Hükümdar / Vezir Düellosu (% 10 == 0)
}

data class LevelPacing(
    val levelId: Int,
    val tier: LevelTier,
    val eraName: String,
    val bossTitle: String?,
    val coinMultiplier: Int,
    val isEraFinal: Boolean,
)

object PalaceTheme {

    private val ERA_NAMES = listOf(
        "Şah Sarayı",       // 1 - 100
        "Sultan Divanı",     // 101 - 200
        "Han Otağı",        // 201 - 300
        "Halife Köşkü",     // 301 - 400
        "Hakan Meydanı",    // 401 - 500
        "Vezir Kasrı",      // 501 - 600
        "Padişah Otağı",    // 601 - 700
        "Beglerbegi Sarayı" // 701+
    )

    private val ERA_1_BOSSES = listOf(
        "Saray Nakkaşı",
        "Hekimbaşı",
        "Sipahi Beyi",
        "Divan Katibi",
        "Baş Mimar",
        "Hazinedar",
        "Baş Pehlivan",
        "Acem Veziri",
        "Müneccimbaşı",
        "Kudretli Şah", // 100. Bölüm
    )

    private val ERA_2_BOSSES = listOf(
        "Çavuşbaşı",
        "Bostancıbaşı",
        "Yeniçeri Ağası",
        "Kaptan-ı Derya",
        "Defterdar",
        "Nişancı",
        "Kazasker",
        "Şeyhülislam",
        "Vezir-i Azam",
        "Cihan Sultanı", // 200. Bölüm
    )

    private val ERA_3_BOSSES = listOf(
        "Toygun Beyi",
        "Tarkan",
        "Atabey",
        "Buyruk",
        "Otacı",
        "Yalvaç",
        "Bahadır",
        "Yabgu",
        "Şad",
        "Ulu Kağan", // 300. Bölüm
    )

    fun getPacing(levelId: Int): LevelPacing {
        if (levelId <= 0) {
            return LevelPacing(
                levelId = levelId,
                tier = LevelTier.NORMAL,
                eraName = "Özel Meydan Okuma",
                bossTitle = null,
                coinMultiplier = 1,
                isEraFinal = false,
            )
        }

        val safeId = levelId.coerceAtLeast(1)
        val cycle = ((safeId - 1) % 10) + 1
        val eraIndex = (safeId - 1) / 100
        val eraName = if (eraIndex < ERA_NAMES.size) ERA_NAMES[eraIndex] else "Hükümdar Çağı ${eraIndex + 1}"
        val isEraFinal = safeId % 100 == 0

        val tier = when (cycle) {
            5 -> LevelTier.HARD
            9 -> LevelTier.SUPER_HARD
            10 -> LevelTier.BOSS
            else -> LevelTier.NORMAL
        }

        val bossTitle = if (tier == LevelTier.BOSS) {
            val bossStep = (((safeId - 1) % 100) / 10).coerceIn(0, 9)
            when (eraIndex) {
                0 -> ERA_1_BOSSES[bossStep]
                1 -> ERA_2_BOSSES[bossStep]
                2 -> ERA_3_BOSSES[bossStep]
                else -> if (isEraFinal) "Büyük Hükümdar" else "Saray Muhafızı"
            }
        } else {
            null
        }

        val coinMultiplier = when (tier) {
            LevelTier.NORMAL -> 1
            LevelTier.HARD -> 2
            LevelTier.SUPER_HARD -> 3
            LevelTier.BOSS -> 5 // Sandık ödülüyle birlikte gelir
        }

        return LevelPacing(
            levelId = safeId,
            tier = tier,
            eraName = eraName,
            bossTitle = bossTitle,
            coinMultiplier = coinMultiplier,
            isEraFinal = isEraFinal,
        )
    }
}
