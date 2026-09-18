package com.veeva.vault.custom.udc;

import com.veeva.vault.sdk.api.core.UserDefinedService;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.data.RecordChange;
import com.veeva.vault.sdk.api.query.Query;

import java.util.List;

@UserDefinedServiceInfo
public interface BusinessLogicService extends UserDefinedService {

    Query buildObjectQuery(List<String> fieldNames, String objectName);
    boolean hasChanges(List<String> fieldNames, String objectName, List<RecordChange> recordChanges);
}