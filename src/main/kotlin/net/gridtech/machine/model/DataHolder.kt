package net.gridtech.machine.model

import net.gridtech.core.Bootstrap
import net.gridtech.core.data.IField
import net.gridtech.core.data.IFieldValue
import net.gridtech.core.data.INode
import net.gridtech.core.data.INodeClass

class DataHolder(bootstrap: Bootstrap) {
    private val nodeClassPublisher = bootstrap.dataPublisher<INodeClass>(bootstrap.nodeClassService.serviceName)
    private val fieldPublisher = bootstrap.dataPublisher<IField>(bootstrap.fieldService.serviceName)
    private val nodePublisher = bootstrap.dataPublisher<INode>(bootstrap.nodeService.serviceName)
    private val fieldValuePublisher = bootstrap.dataPublisher<IFieldValue>(bootstrap.fieldValueService.serviceName)

    init {

        nodeClassPublisher.connect()
        fieldPublisher.connect()
        nodePublisher.connect()
        fieldValuePublisher.connect()
    }
}