package ml.ml.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Single place to store all field names with any specific info like generic formats and patters.
 * Follow FIELD_NAME_... convention for field names.
 */
public class Fields {

    public static final String FIELD_NAME_EMAIL = "email";
    public static final String FIELD_NAME_SUPPLIER_NAME = "supplier_name";
    public static final String FIELD_NAME_PRICE = "price";
    public static final String FIELD_NAME_PRODUCT = "product";
    public static final String FIELD_NAME_INVOICE_NUMBER = "invoice_number";
    public static final String FIELD_NAME_QUANTITY = "quantity";
    public static final String FIELD_NAME_INVOICE_DATE = "invoice_date";
    public static final String FIELD_NAME_TOTAL_AMOUNT = "total_amount";
    public static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";;
    public static final String DATE_FORMAT = "MM/dd/yyyy";

    /**
     * Get all field that follow proposed convention.
     *
     * @return List of field names without convention prefix.
     */
    public List<String> getFieldsNames() {
        List<String> fieldsNames = new ArrayList<>();
        Field[] allFields = Fields.class.getDeclaredFields();
        for (Field field : allFields) {
            String name = field.getName();
            if (name.startsWith("FIELD_NAME"))
                fieldsNames.add(name.substring(11));
        }
        return fieldsNames;
    }
}
