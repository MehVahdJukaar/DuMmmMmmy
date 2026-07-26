plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val moonlight_version: String by extra
val critical_strike_version: String by extra

dependencies {
    // Use the neoforge variant in common for compile-only access
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("maven.modrinth:critical-strike:${critical_strike_version}-neoforge")
}
