package org.motechproject.mds.testutil;

import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldInfo;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.FieldSetting;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeSetting;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.TypeHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;

/**
 * Utility class for constructing minimalist fields for testing field generation.
 */
public final class FieldTestHelper {

    public static Field field(Long id, String name, Class<?> typeClass) {
        return field(name, null, typeClass, null, id);
    }

    public static Field field(String name, Class<?> typeClass) {
        return field(name, typeClass, null);
    }

    public static Field field(String name, Class<?> typeClass, boolean readOnly) {
        return field(name, null, typeClass, null, null, readOnly);
    }

    public static Field field(String name, String displayName, Class<?> typeClass) {
        return field(name, displayName, typeClass, null, null);
    }

    public static Field field(String name, Class<?> typeClass, Object defaultVal) {
        return field(name, null, typeClass, defaultVal, null);
    }

    public static Field field(String name, String displayName, Class<?> typeClass, Object defaultVal, Long id) {
        return field(name, displayName, typeClass, defaultVal, id, false);
    }

    public static Field field(String name, Class<?> typeClass, boolean required, boolean exposedViaRest) {
        return field(name, typeClass, required, exposedViaRest, false);
    }

    public static Field field(String name, Class<?> typeClass, boolean required, boolean exposedViaRest, boolean autoGenerated) {
        Field field = field(name, name + " Display", typeClass);

        field.setRequired(required);
        field.setExposedViaRest(exposedViaRest);
        field.addMetadata(new FieldMetadata(field, Constants.Util.AUTO_GENERATED, String.valueOf(autoGenerated)));

        return field;
    }

    public static Field field(String name, String displayName, Class<?> typeClass, Object defaultVal, Long id, boolean readOnly) {
        Type type = new Type();
        // we only need the type
        type.setTypeClass(typeClass);

        Field field = new Field();
        // we only need the name, type and default value
        field.setName(name);
        field.setType(type);
        field.setDefaultValue(TypeHelper.format(defaultVal));
        field.setId(id);
        field.setDisplayName(displayName);
        field.setReadOnly(readOnly);

        return field;
    }

    public static Field fieldWithComboboxSettings(Entity entity, String name, String displayName, Class<?> typeClass, boolean allowsMultipleSelections,
                                                  boolean allowsUserSupplied, List<String> items) {
        Type type = new Type();

        type.setTypeClass(typeClass);
        type.setDisplayName(Constants.DisplayNames.COMBOBOX);

        Field field = new Field();

        field.setName(name);
        field.setDisplayName(displayName);
        field.setType(type);
        field.setEntity(entity);

        setAllowUserSupplied(field, allowsUserSupplied);
        setAllowMultipleSelections(field, allowsMultipleSelections);
        setComboboxValues(field, items);

        return field;
    }

    public static void setAllowUserSupplied(Field field, boolean value) {
        field.addSetting(new FieldSetting(field, new TypeSetting(Constants.Settings.ALLOW_USER_SUPPLIED),
                String.valueOf(value)));
    }

    public static void setAllowMultipleSelections(Field field, boolean value) {
        field.addSetting(new FieldSetting(field, new TypeSetting(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS),
                String.valueOf(value)));
    }

    public static void setComboboxValues(Field field, List<String> items) {
        field.addSetting(new FieldSetting(field, new TypeSetting(Constants.Settings.COMBOBOX_VALUES),
                TypeHelper.buildStringFromList(items)));
    }

    public static Object newVal(Class<?> clazz) throws IllegalAccessException, InstantiationException {
        if (Integer.class.equals(clazz)) {
            return 5;
        } else if (Long.class.equals(clazz)) {
            return 5L;
        } else if (Double.class.equals(clazz)) {
            return 2.1;
        } else if (String.class.equals(clazz)) {
            return "test";
        } else if (List.class.equals(clazz)) {
            return asList("3", "4", "5");
        } else if (Time.class.equals(clazz)) {
            return new Time(10, 54);
        } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
            return true;
        } else if (Locale.class.equals(clazz)) {
            return Locale.ENGLISH;
        } else if (LocalDate.class.equals(clazz)) {
            return LocalDate.now();
        } else if (LocalDateTime.class.equals(clazz)) {
            return LocalDateTime.now();
        } else {
            return clazz.newInstance();
        }
    }

    public static FieldDto fieldDto(String name, Class<?> clazz) {
        return fieldDto(name, clazz.getName());
    }

    public static FieldDto fieldDto(String name, String className) {
        return fieldDto(null, name, className, null, null);
    }

    public static FieldDto fieldDto(Long id, String name, Class clazz) {
        return fieldDto(id, name, clazz.getName(), null, null);
    }

    public static FieldDto requiredFieldDto(Long id, String name, Class clazz) {
        FieldDto field = fieldDto(id, name, clazz);
        field.getBasic().setRequired(true);
        return field;
    }

    public static FieldDto fieldDto(Long id, String name, String className,
                                    String displayName, Object defValue) {
        FieldDto fieldDto = new FieldDto();
        fieldDto.setType(new TypeDto(className, "", "", className));
        fieldDto.setBasic(new FieldBasicDto(displayName, name));
        fieldDto.getBasic().setDefaultValue(defValue);
        fieldDto.setId(id);
        return fieldDto;
    }

    public static FieldInfo fieldInfo(String name, Class typeClass, boolean required,
                                      boolean restExposed) {
        return fieldInfo(name, typeClass, required, restExposed, false);
    }

    public static FieldInfo fieldInfo(String name, Class typeClass, boolean required,
                                      boolean restExposed, boolean autoGenerated) {
        FieldInfo field = new FieldInfo();

        field.setName(name);
        field.setDisplayName(name + " disp");
        field.setRequired(required);
        field.setRestExposed(restExposed);
        field.setAutoGenerated(autoGenerated);

        field.getTypeInfo().setType(typeClass.getName());

        return field;
    }

    public static LookupFieldDto lookupFieldDto(String name) {
        return new LookupFieldDto(null, name, LookupFieldType.VALUE, null);
    }

    public static LookupFieldDto lookupFieldDto(String name, String operator) {
        return new LookupFieldDto(null, name, LookupFieldType.VALUE, operator);
    }

    public static LookupFieldDto lookupFieldDto(Long id, String name) {
        return new LookupFieldDto(id, name, LookupFieldType.VALUE);
    }

    public static LookupFieldDto lookupFieldDto(String name, LookupFieldType type) {
        return new LookupFieldDto(null, name, type);
    }

    public static List<LookupFieldDto> lookupFieldDtos(String... names) {
        List<LookupFieldDto> lookupFields = new ArrayList<>();
        for (String name : names) {
            lookupFields.add(new LookupFieldDto(null, name, LookupFieldType.VALUE));
        }
        return lookupFields;
    }

    public static FieldDto findByName(List<FieldDto> fields, String name) {
        for (FieldDto field : fields) {
            if (name.equals(field.getBasic().getName())) {
                return field;
            }
        }
        return null;
    }

    private FieldTestHelper() {
    }
}
