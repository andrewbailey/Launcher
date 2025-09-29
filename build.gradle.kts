// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.spotless) apply true
}

spotless {
    format("global") {
        target("**/*")
        targetExclude("**/*.webp", "**/*.png", "**/*.jar", "**/*.bat")
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
    }
    kotlin {
        target("**/*.kt", "**/*.gradle.kts")
        ktlint(libs.versions.ktlint.get())
            .customRuleSets(
                listOf(
                    libs.ktlint.compose.get().toString(),
                ),
            )
    }
    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/", ".idea/**", ".run/**")
        prettier(
            mapOf(
                "prettier" to libs.versions.prettier.get(),
                "@prettier/plugin-xml" to libs.versions.prettierXml.get(),
            ),
        ).config(
            mapOf(
                "parser" to "xml",
                "plugins" to listOf("@prettier/plugin-xml"),
                "tabWidth" to 4,
                "bracketSameLine" to true,
                "xmlSelfClosingSpace" to false,
            ),
        )
    }
    format("md") {
        target("**/*.md")
        prettier().config(mapOf("parser" to "markdown"))
    }
    yaml {
        target("**/*.yaml")
        jackson()
    }
}
