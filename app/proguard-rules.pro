-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class androidx.room.RoomDatabase_Impl
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-dontwarn kotlin.Metadata
