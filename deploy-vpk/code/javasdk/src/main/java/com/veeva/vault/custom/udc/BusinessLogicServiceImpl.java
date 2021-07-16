package com.veeva.vault.custom.udc;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;

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


    //  buildObjectQuery checks if field types and constructs a VQL query for them
    public String buildObjectQuery(List<String> fieldNames, String objectName) {
        LogService logService = ServiceLocator.locate(LogService.class);

       List<ObjectField> queryFields = getFields(fieldNames, objectName);
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");

        //  Iterate over fields
        Iterator<ObjectField> fieldIterator = queryFields.iterator();
        while (fieldIterator.hasNext()) {

            ObjectField currentChangedField = fieldIterator.next();
            // If the field type is long text, use the long text method to get the full value
            if (currentChangedField.getFieldType() == ObjectFieldType.LONGTEXT) {
                queryBuilder.append("LONGTEXT(").append(currentChangedField.getName()).append(")");
            } else {
                queryBuilder.append(currentChangedField.getName());
            }
            if (fieldIterator.hasNext()) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" FROM ").append(objectName);
        logService.info(queryBuilder.toString());
        return queryBuilder.toString();
    }


    // hasChanges checks if field values have changed and returns true if they have
    public boolean hasChanges(List<String> fieldNames, String objectName, List<RecordChange> recordChanges) {
        LogService logService = ServiceLocator.locate(LogService.class);
        boolean hasChanges = false;

        for (RecordChange recordChange : recordChanges) {
            List<ObjectField> changeFields = getFields(fieldNames, objectName);

            for (ObjectField field : changeFields) {

                logService.info("Data Change Field: " + field.getLabel());

                String fieldName = field.getName();
                ValueType fieldValueType = field.getValueType();

                // If new values are not same as old values, values have changed
                // Notify user that values have changed
                if (!compareFields(fieldName, fieldValueType, recordChange)) {
                    logService.debug("Field Updated: " + field.getLabel());
                    hasChanges = true;
                }

            }
        }
        return hasChanges;
    }
}