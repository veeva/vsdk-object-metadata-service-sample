package com.veeva.vault.custom.udc;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;
import com.veeva.vault.sdk.api.query.Query;
import com.veeva.vault.sdk.api.query.QueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;


@UserDefinedServiceInfo
public class BusinessLogicServiceImpl implements BusinessLogicService {

   // getFields demonstrates how to use the object metadata service to get specified fields
   public List<ObjectField> getFields(List<String> fieldNames, String objectName) {
       // Initialize service
       ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);

       // Get ObjectFieldCollectionRequest Builder
       ObjectFieldRequest.Builder objectFieldRequestBuilder = objectMetadataService.newFieldRequestBuilder();

       List<ObjectField> objectFields = VaultCollections.newList();

       for(String fieldName: fieldNames) {
           // Object Field Request
           ObjectFieldRequest objectFieldRequest = objectFieldRequestBuilder
                   .withObjectName(objectName)
                   .withFieldName(fieldName)
                   .build();
           ObjectField field = objectMetadataService.getField(objectFieldRequest);
           objectFields.add(field);
       }

       return objectFields;

   }

   // compareFields demonstrates how to use the ValueType interface to get the field value and compare
   public boolean compareFields(String fieldName, ValueType fieldValueType, RecordChange recordChange) {
       if (ValueType.BOOLEAN.equals(fieldValueType)) {
           Boolean newValue =  recordChange.getNew().getValue(fieldName, ValueType.BOOLEAN);
           Boolean oldValue =  recordChange.getOld().getValue(fieldName, ValueType.BOOLEAN);
           return newValue == oldValue;
       } else if (ValueType.DATE.equals(fieldValueType)) {
           LocalDate newDate = recordChange.getNew().getValue(fieldName, ValueType.DATE);
           LocalDate oldDate = recordChange.getOld().getValue(fieldName, ValueType.DATE);
           // compareTo returns 0 if equal
           return newDate.compareTo(oldDate) == 0;
       } else if (ValueType.DATETIME.equals(fieldValueType)) {
           ZonedDateTime newDateTime = recordChange.getNew().getValue(fieldName, ValueType.DATETIME);
           ZonedDateTime oldDateTime = recordChange.getOld().getValue(fieldName, ValueType.DATETIME);
           return newDateTime.toInstant().compareTo(oldDateTime.toInstant()) == 0;
       } else if (ValueType.PICKLIST_VALUES.equals(fieldValueType)) {
           List<String> newPicklistValues = recordChange.getNew().getValue(fieldName, ValueType.PICKLIST_VALUES);
           List<String> oldPicklistValues = recordChange.getOld().getValue(fieldName, ValueType.PICKLIST_VALUES);

           Collections.sort(newPicklistValues);
           Collections.sort(oldPicklistValues);
           return newPicklistValues.equals(oldPicklistValues);
       } else if (ValueType.REFERENCES.equals(fieldValueType)) {
           List<String> newReferences = recordChange.getNew().getValue(fieldName, ValueType.REFERENCES);
           List<String> oldReferences =  recordChange.getOld().getValue(fieldName, ValueType.REFERENCES);

           Collections.sort(newReferences);
           Collections.sort(oldReferences);
           return newReferences.equals(oldReferences);
       } else if (ValueType.STRING.equals(fieldValueType)) {
           String newString = recordChange.getNew().getValue(fieldName, ValueType.STRING);
           String oldString = recordChange.getOld().getValue(fieldName, ValueType.STRING);
           return newString.equals(oldString);
       } else if (ValueType.NUMBER.equals(fieldValueType)) {
           BigDecimal newNumber = recordChange.getNew().getValue(fieldName, ValueType.NUMBER);
           BigDecimal oldNumber = recordChange.getOld().getValue(fieldName, ValueType.NUMBER);
           return newNumber.compareTo(oldNumber) == 0;
       }
       return false;
   }


    //  buildObjectQuery checks field types and constructs a VQL query using object name and fields.
    //  The query is built with QueryService.newQueryBuilder() instead of assembling a raw VQL String.
    public Query buildObjectQuery(List<String> fieldNames, String objectName) {
        LogService logService = ServiceLocator.locate(LogService.class);
        QueryService queryService = ServiceLocator.locate(QueryService.class);

        List<ObjectField> queryFields = getFields(fieldNames, objectName);

        //  Build the SELECT field list. Long text fields must be wrapped in the LONGTEXT() function
        //  to retrieve their full value.
        List<String> selectFields = VaultCollections.newList();
        for (ObjectField currentChangedField : queryFields) {
            if (currentChangedField.getFieldType() == ObjectFieldType.LONGTEXT) {
                selectFields.add("LONGTEXT(" + currentChangedField.getName() + ")");
            } else {
                selectFields.add(currentChangedField.getName());
            }
        }

        Query query = queryService.newQueryBuilder()
                .withSelect(selectFields)
                .withFrom(objectName)
                .build();
        logService.info("Built query on {} selecting {}", objectName, selectFields);
        return query;
    }


    // hasChanges checks if field values have changed and returns true if they have
    public boolean hasChanges(List<String> fieldNames, String objectName, List<RecordChange> recordChanges) {
        LogService logService = ServiceLocator.locate(LogService.class);
        boolean hasChanges = false;

        for (RecordChange recordChange : recordChanges) {
            List<ObjectField> changeFields = getFields(fieldNames, objectName);

            for (ObjectField field : changeFields) {

                logService.info("Data Change Field: {}", field.getLabel());

                String fieldName = field.getName();
                ValueType fieldValueType = field.getValueType();

                // If new values are not same as old values, values have changed
                // Notify user that values have changed
                if (!compareFields(fieldName, fieldValueType, recordChange)) {
                    logService.debug("Field Updated: {}", field.getLabel());
                    hasChanges = true;
                }

            }
        }
        return hasChanges;
    }
}