package xyz.secozzi.aniyomilocalmanager.di

import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.dsl.module

val SerializationModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    single {
        XML {
            defaultPolicy {
                ignoreUnknownChildren()
            }
            autoPolymorphic = true
            xmlDeclMode = XmlDeclMode.Charset
            indent = 2
            xmlVersion = XmlVersion.XML10
        }
    }
}
