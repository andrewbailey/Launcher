package dev.andrewbailey.launcher.inject

import dev.zacsweers.metro.Qualifier

@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE,
)
@Qualifier
annotation class GlobalBackgroundScope