package com.madisp.stupid.context;

import com.madisp.stupid.Block;
import com.madisp.stupid.ExecContext;
import com.madisp.stupid.MethodSignature;

import java.util.*;

/**
 * StackContext is a LIFO-stack container of {@link ExecContext} instances.
 * StackContext tries to forward the methods to the underlying context by starting
 * from the most recently added one. If a NoSuch* exception is thrown (e.g. no field)
 * then the next ExecContext is tried and so on.
 * This enables one to mix-and-match various context in a fairly predictible manner.
 * <p>
 * Some neat tricks:
 * <p>
 * Add a {@link VarContext} with the type CREATE_ON_SET_OR_GET before adding another context
 * on top. This will allow stupid scripts to define their own variables while still enabling
 * access to some other context, a {@link ReflectionContext} to access a Java API for instance.
 * <p>
 * The push/pop is used by a {@link Block} to set the block arguments when yielding.
 */
public class StackContext extends BaseContext {
    private final Deque<ExecContext> stack = new LinkedList<>();

    /**
     * Push a new ExecContext on top of the stack
     */
    public void pushExecContext(ExecContext ctx) {
        stack.push(ctx);
    }


    @Override
    public Set<MethodSignature> getSupportedSignatures() {
        Set<MethodSignature> ret = new HashSet<>();
        for (ExecContext aStack : stack) {
            ret.addAll(aStack.getSupportedSignatures());
        }
        return ret;
    }

    /**
     * Pop the last ExecContext (in a LIFO fashion, e.g., the most recently pushed item).
     */
    public void popExecContext() {
        stack.pop();
    }

    @Override
    public Object getFieldValue(Object root, String identifier) {
        for (ExecContext aStack : stack) {
            try {
                return aStack.getFieldValue(root, identifier);
            } catch (NoSuchFieldException e) {
                // ignore, jump to next scope
            }
        }
        return null;
    }

    @Override
    public Object setFieldValue(Object root, String identifier, Object value) {
        Iterator<ExecContext> iter = stack.descendingIterator();
        while (iter.hasNext()) {
            try {
                return iter.next().setFieldValue(root, identifier, value);
            } catch (NoSuchFieldException e) {
                // ignore, jump to next scope
            }
        }
        return null;
    }

    @Override
    public Object callMethod(Object root, String identifier, Object... args) throws NoSuchMethodException {
        Object fieldValue = getFieldValue(root, identifier);
        if (fieldValue != null) {
            fieldValue = dereference(fieldValue);
        }
        if (fieldValue instanceof Block) {
            return ((Block) fieldValue).yield(this, args);
        } else {
            return callMethodInt(root, identifier, args);
        }
    }

    private Object callMethodInt(Object root, String identifier, Object[] args) throws NoSuchMethodException {
        Iterator<ExecContext> iter = stack.descendingIterator();
        while (iter.hasNext()) {
            try {
                return iter.next().callMethod(dereference(root), identifier, args);
            } catch (NoSuchMethodException e) {
                // ignore, jump to next scope
            }
        }
        throw new NoSuchMethodException(identifier);
    }

    @Override
    public Object apply(Object base, Object[] args) throws NoSuchMethodException {
        if (base instanceof Block) {
            return ((Block) base).yield(this, args);
        }
        Iterator<ExecContext> iter = stack.descendingIterator();
        while (iter.hasNext()) {
            try {
                return iter.next().apply(base, args);
            } catch (NoSuchMethodException e) {
                // ignore, jump to next scope
            }
        }
        throw new NoSuchMethodException("apply");
    }

    @Override
    public Object getResource(String pckg, String type, String name) {
        for (ExecContext aStack : stack) {
            try {
                return aStack.getResource(pckg, type, name);
            } catch (NoSuchFieldException e) {
                // ignore, jump to next scope
            }
        }
        return null;
    }
}
