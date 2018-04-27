package com.madisp.stupid;

import java.util.Objects;

/**
 * @author Marcin Wielgus
 */
public class MethodSignature {
    private final String name;
    private final int argumentCount;
    private final boolean root;

    public MethodSignature(String name, int argumentCount, boolean root) {
        this.name = name;
        this.argumentCount = argumentCount;
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public int getArgumentCount() {
        return argumentCount;
    }

    public boolean isRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodSignature that = (MethodSignature) o;
        return argumentCount == that.argumentCount &&
                root == that.root &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, argumentCount, root);
    }
}
