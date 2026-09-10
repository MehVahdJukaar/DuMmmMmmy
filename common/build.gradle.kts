plugins {
    id("com.possible-triangle.common")
}

common {
    //pinned so the build doesn't need to hit maven.neoforged.net to list versions
    neoformVersion = "26.1.2-1"
    accessWidener()
}

val moonlight_version: String by extra
val critical_strike_version: String by extra

dependencies {
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}@jar")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("maven.modrinth:critical-strike:${critical_strike_version}-neoforge")
}
