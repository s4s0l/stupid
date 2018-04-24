package com.madisp.stupid.context;

import com.madisp.stupid.ValidationContext;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Marcin Wielgus
 */
public class FixedMethodContext extends BaseContext implements ValidationContext {

    private final FixedMethodHandler[] methodHandlers;

    public FixedMethodContext(FixedMethodHandler... methodHandlers) {
        this.methodHandlers = Arrays.copyOf(methodHandlers, methodHandlers.length);
    }

    @Override
    public Object callMethod(Object root, String identifier, Object... args) throws NoSuchMethodException {
        validateCallMethod(root == null, identifier, args.length);
        FixedMethodHandler handler = Stream.of(methodHandlers)
                .filter(x -> x.getName().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("No method named " + identifier));


        Object[] finalArgs;
        if (root == null) {
            finalArgs = args;
        } else {
            finalArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, finalArgs, 1, args.length);
            finalArgs[0] = root;
        }
        return handler.handle(finalArgs);
    }

    @Override
    public boolean validateCallMethod(boolean root, String identifier, int argsCount) throws NoSuchMethodException {
        FixedMethodHandler handler = Stream.of(methodHandlers)
                .filter(x -> x.getName().equals(identifier))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("No method named " + identifier));
        if (handler.isRoot() != root) {
            throw new NoSuchMethodException("method named " + identifier + " was called in wrong context, should be root? " + handler.isRoot());
        }
        if (handler.getArgumentCount() != (argsCount + (root ? 0 : 1))) {
            throw new NoSuchMethodException("method named " + identifier + " has wrong number of args, expected " + handler.getArgumentCount() + ", but was " + argsCount);
        }
        return true;
    }

    public interface FixedMethodHandler {
        String getName();

        int getArgumentCount();

        boolean isRoot();

        Object handle(Object[] args);
    }

    public static class DefaultMethodHandle implements FixedMethodHandler {
        private final String name;
        private final boolean root;
        private final int argCount;
        private final Function<Object[], Object> handler;

        public DefaultMethodHandle(String name, boolean root, int argCount, Function<Object[], Object> handler) {
            this.name = name;
            this.root = root;
            this.argCount = argCount;
            this.handler = handler;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getArgumentCount() {
            return argCount;
        }

        @Override
        public boolean isRoot() {
            return root;
        }

        @Override
        public Object handle(Object[] args) {
            return handler.apply(args);
        }
    }

    public static <R> FixedMethodHandler root(String name, Supplier<R> handler) {
        return new DefaultMethodHandle(name, true, 0, x -> handler.get());
    }

    public static <A, R> FixedMethodHandler root(String name, Function<A, R> handler) {
        return new DefaultMethodHandle(name, true, 1, x -> handler.apply((A) x[0]));
    }

    public static <A, B, R> FixedMethodHandler root(String name, BiFunction<A, B, R> handler) {
        return new DefaultMethodHandle(name, true, 2, x -> handler.apply((A) x[0], (B) x[1]));
    }

    public static <O, R> FixedMethodHandler object(String name, Function<O, R> handler) {
        return new DefaultMethodHandle(name, false, 1, x -> handler.apply((O) x[0]));
    }

    public static <O, A, R> FixedMethodHandler object(String name, BiFunction<O, A, R> handler) {
        return new DefaultMethodHandle(name, false, 2, x -> handler.apply((O) x[0], (A) x[1]));
    }

}
