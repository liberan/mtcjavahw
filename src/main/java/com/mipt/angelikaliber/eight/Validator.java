package com.mipt.angelikaliber.eight;

import com.sun.jdi.ClassType;
import com.sun.source.tree.ClassTree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {
    public static class ValidationResult {
        private boolean isValid;
        private List<String> errors;

        public ValidationResult() {
            this.isValid = true;
            this.errors = new ArrayList<>();
        }

        public boolean isValid() {
            return isValid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
            this.isValid = false;
        }
    }

    public static ValidationResult validate(Object object) {
        final Class<?> aClass = object.getClass();
        final Field[] allFields = aClass.getDeclaredFields();
        ValidationResult result = new ValidationResult();

        for (Field field : allFields) {
            if (field.isAnnotationPresent(NotNull.class)) {
                try {
                    if(field.get(object) == null) {
                        result.addError(field.getAnnotation(NotNull.class).message());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            if (field.isAnnotationPresent(Size.class)) {
                Size a = field.getAnnotation(Size.class);
                String str = null;
                try {
                    str = (String) (field.get(object));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if(str == null || str.length() > a.max() || str.length() < a.min()) {
                    result.addError(a.message());
                }
            }

            if (field.isAnnotationPresent(Range.class)) {
                Range a = field.getAnnotation(Range.class);
                Integer b = null;
                try {
                    b = (Integer)(field.get(object));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (b == null || b > a.max() || b < a.min()) {
                    result.addError(a.message());
                }
            }

            if (field.isAnnotationPresent(Email.class)) {
                Email a = field.getAnnotation(Email.class);
                String str = null;
                try {
                    str = (String) (field.get(object));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                Pattern pattern = Pattern.compile("^([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.[a-zA-Z0-9_-]+)$", Pattern.CASE_INSENSITIVE);

                if(str == null || !pattern.matcher(str).find()) {
                    result.addError(a.message());
                }
            }

        }

        return result;
    }
}