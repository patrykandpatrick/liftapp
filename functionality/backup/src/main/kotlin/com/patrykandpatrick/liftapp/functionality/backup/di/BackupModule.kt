package com.patrykandpatrick.liftapp.functionality.backup.di

import com.patrykandpatrick.liftapp.domain.backup.AutoBackupScheduler
import com.patrykandpatrick.liftapp.domain.backup.ExportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.ExportRoutineUseCase
import com.patrykandpatrick.liftapp.domain.backup.GetDirectoryNameUseCase
import com.patrykandpatrick.liftapp.domain.backup.GetShareableLocationUseCase
import com.patrykandpatrick.liftapp.domain.backup.ImportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.PersistDirectoryAccessUseCase
import com.patrykandpatrick.liftapp.domain.backup.ReadBackupContentsUseCase
import com.patrykandpatrick.liftapp.functionality.backup.BackupRepository
import com.patrykandpatrick.liftapp.functionality.backup.work.BackupScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BackupModule {

    @Binds fun bindExportBackupUseCase(repository: BackupRepository): ExportBackupUseCase

    @Binds fun bindExportRoutineUseCase(repository: BackupRepository): ExportRoutineUseCase

    @Binds fun bindImportBackupUseCase(repository: BackupRepository): ImportBackupUseCase

    @Binds
    fun bindReadBackupContentsUseCase(repository: BackupRepository): ReadBackupContentsUseCase

    @Binds fun bindGetDirectoryNameUseCase(repository: BackupRepository): GetDirectoryNameUseCase

    @Binds
    fun bindGetShareableLocationUseCase(repository: BackupRepository): GetShareableLocationUseCase

    @Binds
    fun bindPersistDirectoryAccessUseCase(
        repository: BackupRepository
    ): PersistDirectoryAccessUseCase

    @Binds fun bindAutoBackupScheduler(scheduler: BackupScheduler): AutoBackupScheduler
}
