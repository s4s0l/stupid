package com.madisp.stupid.context;

import com.madisp.stupid.ValidationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marcin Wielgus
 */
public class ComposedValidationContext implements ValidationContext {

    private final ValidationContext[] allContexts;

    public ComposedValidationContext(ValidationContext ... allContexts) {
        this.allContexts = Arrays.copyOf(allContexts, allContexts.length);
    }

    @Override
    public boolean validateGetFieldValue(boolean root, String identifier) throws NoSuchFieldComplex {
        List<NoSuchFieldException> allExceptions = new ArrayList<>();
        boolean anyTrue = false;
        for (ValidationContext context : allContexts) {
            try {
                if (context.validateGetFieldValue(root, identifier)) {
                    anyTrue = true;
                }
            } catch (NoSuchFieldException e) {
                allExceptions.add(e);
            }
        }
        if (!allExceptions.isEmpty()) {
            throw new NoSuchFieldComplex(identifier, allExceptions);
        }
        return anyTrue;
    }

    @Override
    public boolean validateSetFieldValue(boolean root, String identifier) throws NoSuchFieldComplex {
        List<NoSuchFieldException> allExceptions = new ArrayList<>();
        boolean anyTrue = false;
        for (ValidationContext context : allContexts) {
            try {
                if (context.validateSetFieldValue(root, identifier)) {
                    anyTrue = true;
                }
            } catch (NoSuchFieldException e) {
                allExceptions.add(e);
            }
        }
        if (!allExceptions.isEmpty()) {
            throw new NoSuchFieldComplex(identifier, allExceptions);
        }
        return anyTrue;
    }

    @Override
    public boolean validateCallMethod(boolean root, String identifier, int argsCount) throws NoSuchMethodComplex {
        List<NoSuchMethodException> allExceptions = new ArrayList<>();
        boolean anyTrue = false;
        for (ValidationContext context : allContexts) {
            try {
                if (context.validateCallMethod(root, identifier, argsCount)) {
                    anyTrue = true;
                }
            } catch (NoSuchMethodException e) {
                allExceptions.add(e);
            }
        }
        if (!allExceptions.isEmpty()) {
            throw new NoSuchMethodComplex(identifier, allExceptions);
        }
        return anyTrue;
    }


    public static class NoSuchFieldComplex extends NoSuchFieldException {
        private final List<NoSuchFieldException> wrapped;

        NoSuchFieldComplex(String fieldName, List<NoSuchFieldException> wrapped) {
            super(getMessage(fieldName, wrapped));
            this.wrapped = wrapped;
        }

        private static String getMessage(String fieldName, List<NoSuchFieldException> wrapped) {
            String collect = wrapped.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));
            return "Field named " + fieldName + " is inaccessible due to [" + collect + "]";
        }

        public List<NoSuchFieldException> getWrapped() {
            return Collections.unmodifiableList(wrapped);
        }
    }

    public static class NoSuchMethodComplex extends NoSuchMethodException {
        private final List<NoSuchMethodException> wrapped;

        NoSuchMethodComplex(String fieldName, List<NoSuchMethodException> wrapped) {
            super(getMessage(fieldName, wrapped));
            this.wrapped = wrapped;
        }

        private static String getMessage(String fieldName, List<NoSuchMethodException> wrapped) {
            String collect = wrapped.stream().map(Throwable::getMessage).collect(Collectors.joining(", "));
            return "Field named " + fieldName + " is inaccessible due to [" + collect + "]";
        }

        public List<NoSuchMethodException> getWrapped() {
            return Collections.unmodifiableList(wrapped);
        }
    }
}
