/*
 * --------------------------------------------------------------------
 * RecordTrigger:	vSDKObjectMetadataSampleTrigger
 * Object:          vsdk_object_metadata_example__c
 * Author:			Veeva Vault Developer Support
 *---------------------------------------------------------------------
 * Description: Demonstrates the usage of ObjectMetadataService to fetch field and value types.
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.recordtriggers;

import com.veeva.vault.custom.udc.BusinessLogicService;
import com.veeva.vault.sdk.api.core.RequestContext;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.data.*;

import java.util.List;


/**
 * This class annotation (@RecordTriggerInfo) indicates that this class is a record trigger.
 * It specifies the object that this trigger will run on(vsdk_object_metadata_example__c), the events it will run on(BEFORE_UPDATE).
 */
@RecordTriggerInfo(object = "vsdk_object_metadata_example__c", events = {RecordEvent.BEFORE_UPDATE})
public class vSDKObjectMetadataSampleTrigger implements RecordTrigger {

    private final static String OBJECT_NAME = "vsdk_object_metadata_example__c";
    public void execute(RecordTriggerContext context) {
        // Get all the record changes
        List<RecordChange> recordChanges =  context.getRecordChanges();

        // Get an instance of our business logic service
        BusinessLogicService businessLogicService = ServiceLocator.locate(BusinessLogicService.class);

        String query = businessLogicService.buildObjectQuery(
                VaultCollections.asList("amount__c", "description__c"),
                OBJECT_NAME);

        boolean hasChanges = businessLogicService.hasChanges(
                VaultCollections.asList("status__v", "amount__c"),
                OBJECT_NAME,
                recordChanges);

    }

}
