package xyz.secozzi.aniyomilocalmanager.di

import com.github.k1rakishou.fsaf.FileManager
import org.koin.dsl.module

val FileManagerModule = module {
    single { FileManager(get()) }
}
