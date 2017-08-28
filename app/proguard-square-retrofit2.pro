# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-dontnote retrofit2.Platform

-keep class retrofit2.** { *; }

-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}