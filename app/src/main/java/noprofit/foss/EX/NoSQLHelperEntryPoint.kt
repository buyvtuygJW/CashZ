package noprofit.foss.EX

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import noprofit.foss.NOSQL.NoSQLHelper

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NoSQLHelperEntryPoint {
    fun noSQLHelper(): NoSQLHelper
}