package com.madisp.stupid;

/**
 * @author Marcin Wielgus
 */
public interface ValidationContext {


    /**
     * @return false means this validator does not want to participate in validation, true when validation was ok
     */
    default boolean validateGetFieldValue(boolean root, String identifier) throws NoSuchFieldException {
        return false;
    }

    /**
     * @return false means this validator does not want to participate in validation, true when validation was ok
     */
    default boolean validateSetFieldValue(boolean root, String identifier) throws NoSuchFieldException {
        return false;
    }

    /**
     * @return false means this validator does not want to participate in validation, true when validation was ok
     */
    default boolean validateCallMethod(boolean root, String identifier, int argsCount) throws NoSuchMethodException {
        return false;
    }


}
