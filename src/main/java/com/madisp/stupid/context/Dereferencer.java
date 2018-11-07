package com.madisp.stupid.context;

import com.madisp.stupid.ExecContext;
import com.madisp.stupid.StupidRuntimeException;
import com.madisp.stupid.Value;

/**
 * Performs dereference operation.
 * Subclassing can give listener like capabilities top perform
 * some action on every dereference.
 *
 * @author Marcin Wielgus
 */
public class Dereferencer {

    public <T> T dereference(Value<T> value, ExecContext context) throws StupidRuntimeException {
        return value.value(context);
    }

}
