package com.madisp.stupid;

import com.madisp.stupid.context.StackContext;
import com.madisp.stupid.context.VarContext;
import com.madisp.stupid.expr.StatementListExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A block is basically an expression with some arguments that can be
 * evaluated later. A block is not a closure however, e.g., it doesn't
 * have a local scope.
 */
public class Block implements VisitorAcceptor {
    private final StatementListExpression body;
    private final String[] varNames;
    private final String blockName;

    public Block(StatementListExpression body, String[] varNames, String blockName) {
        this.body = body;
        this.varNames = Arrays.copyOf(varNames, varNames.length);
        this.blockName = blockName;
    }

    /**
     * Create a new block with named arguments
     *
     * @param varNames list of argument names
     * @param body     expression that is to be evaluated
     */
    public Block(String[] varNames, StatementListExpression body) {
        this(body, varNames, "<<anonymous block>>");
    }

    public Block nameBlock(String blockName) {
        return new Block(this.body, this.varNames, blockName);
    }

    public MethodSignature getMethodSignature() {
        return new MethodSignature(blockName, varNames.length, true);
    }

    public <V> V acceptVisitor(ExpressionVisitor<V> visitor, V value) {
        return ((Expression<?>) body).acceptVisitor(visitor, value);
    }

    /**
     * Evaluate the body of a block with given arguments. The length
     * of arguments must match the length of given names in the constructor.
     *
     * @param ctx  The context wherein to evaluate the block
     * @param args argument values (must match the same length and order as given
     *             in the constructor)
     * @return The evaluated value of the block
     */
    public Object yield(ExecContext ctx, Object... args) throws StupidRuntimeException {
        if (varNames.length != args.length) {
            throw new StupidRuntimeException("Expected " + varNames.length + " arguments but got " + args.length + " in " + getBlockName() + " code block");
        }
        Map<String, Object> argMap = new HashMap<>();
        for (int i = 0; i < varNames.length; i++) {
            argMap.put(varNames[i], args[i]);
        }
        // wrap our block arguments over the underlying context
        StackContext withArgs = new StackContext();
        withArgs.setDereferencer(ctx.getDereferencer());
        withArgs.pushExecContext(ctx); // the underlying context
        withArgs.pushExecContext(new VarContext(Collections.unmodifiableMap(argMap))); // args
        return body.value(withArgs);
    }

    public String getBlockName() {
        return "<<anonymous block>>";
    }
}
