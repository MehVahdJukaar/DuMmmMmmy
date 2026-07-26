plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

val moonlight_version: String by extra
val critical_strike_version: String by extra
val codecui_version: String by extra

dependencies {
    modImplementation("net.mehvahdjukaar:moonlight-fabric:${moonlight_version}")
    modImplementation("net.mehvahdjukaar:codecui-fabric:${codecui_version}")

    modCompileOnly("maven.modrinth:critical-strike:${critical_strike_version}-fabric")

    modCompileOnly("com.terraformersmc:modmenu:4.0.6") {
        exclude(module = "fabric-api")
    }
}
