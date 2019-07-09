package net.gridtech.machine.model.property.entityClass

import net.gridtech.core.data.INodeClass
import net.gridtech.core.util.parse
import net.gridtech.machine.model.IBaseProperty
import net.gridtech.machine.model.entityClass.DisplayClass


class DisplayClientVersionDescription(private val displayClass: DisplayClass)
    : IBaseProperty<DisplayClientVersion, INodeClass>({ parse(it.description) }, DisplayClientVersion.empty()) {
    fun updateDisplayClientVersion(displayClientVersion: DisplayClientVersion) {
        displayClass.updateDescription(displayClientVersion)
    }
}


data class DisplayClientVersion(
        var version: String,
        var name: String,
        var location: String
) {
    companion object {
        fun empty() = DisplayClientVersion("", "", "")
    }
}