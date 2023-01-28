package com.patrykandpatrick.liftapp.json.di

import com.patrykandpatryk.liftapp.domain.model.StringResource
import com.patrykandpatryk.liftapp.domain.serialization.PolymorphicEnumSerializer
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.PercentageUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.functionality.database.string.BodyStringResource
import com.patrykandpatryk.liftapp.functionality.database.string.ExerciseStringResource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import javax.inject.Singleton
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Module
@InstallIn(SingletonComponent::class)
interface JsonModule {

    companion object {

        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            serializersModule = SerializersModule {

                polymorphic(ValueUnit::class) {
                    subclass(MassUnit::class, PolymorphicEnumSerializer(MassUnit.serializer()))
                    subclass(LongDistanceUnit::class, PolymorphicEnumSerializer(LongDistanceUnit.serializer()))
                    subclass(MediumDistanceUnit::class, PolymorphicEnumSerializer(MediumDistanceUnit.serializer()))
                    subclass(ShortDistanceUnit::class, PolymorphicEnumSerializer(ShortDistanceUnit.serializer()))
                    subclass(PercentageUnit::class)
                }

                polymorphic(StringResource::class) {
                    subclass(
                        ExerciseStringResource::class,
                        PolymorphicEnumSerializer(ExerciseStringResource.serializer()),
                    )
                    subclass(
                        BodyStringResource::class,
                        PolymorphicEnumSerializer(BodyStringResource.serializer()),
                    )
                }
            }
        }
    }
}
