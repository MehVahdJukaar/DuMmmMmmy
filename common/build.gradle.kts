plugins {
    id("com.possible-triangle.common")
}

common {
    accessWidener()
}

val moonlight_version: String by extra
val critical_strike_version: String by extra

dependencies {
    // Use the neoforge variant in common for compile-only access.
    // @jar ignores moonlight's module metadata. Without it gradle picks the access transformer variant here and
    // the whole api goes missing from the classpath
    modCompileOnly("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}@jar")
    accessTransformers("net.mehvahdjukaar:moonlight-neoforge:${moonlight_version}")

    modCompileOnly("maven.modrinth:critical-strike:${critical_strike_version}-neoforge")
}
