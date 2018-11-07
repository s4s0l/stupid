package com.madisp.stupid.context;

import com.madisp.stupid.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.madisp.stupid.context.VarContext.Type.CREATE_ON_SET_OR_GET;

/**
 * @author Marcin Wielgus
 */
public class NamedBlockContext extends BaseContext implements VisitorAcceptor {

    private final Map<String, Block> blocks = new HashMap<>();

    public Block addBlock(String name, Block b) {
        Block value = b.nameBlock(name);
        blocks.put(name, value);
        return value;
    }

    public Block addBlock(String name, String script) throws StupidRuntimeException {
        return addBlock(name, script, 100);
    }

    public Block addBlock(String name, String script, long maxBlockPrepareTimeMs) throws StupidRuntimeException {
        StackContext x = new StackContext();
        x.setDereferencer(new TimeTrackingDereferencer(1000 * 1000 * maxBlockPrepareTimeMs, 100));
        x.pushExecContext(new VarContext(CREATE_ON_SET_OR_GET));
        Expression expression = new ExpressionFactory().parseExpression("{" + script + "}");
        Object dereference = x.dereference(expression);
        if (dereference instanceof Block) {
            return addBlock(name, (Block) dereference);
        } else {
            throw new StupidRuntimeException("script has no block!");
        }
    }

    @Override
    public Object getFieldValue(Object root, String identifier) throws NoSuchFieldException {
        if (root == null) {
            Block block = blocks.get(identifier);
            if (block != null) {
                return block;
            }
        }
        return super.getFieldValue(root, identifier);
    }

    public Set<MethodSignature> getSupportedSignatures() {
        return blocks.values().stream()
                .map(Block::getMethodSignature)
                .collect(Collectors.toSet());
    }

    public Block getByName(String block) {
        return blocks.get(block);
    }


    @Override
    public <V> V acceptVisitor(ExpressionVisitor<V> visitor, V value) {
        V nextVal = value;
        for (Block block : blocks.values()) {
            nextVal = block.acceptVisitor(visitor, value);
        }
        return nextVal;
    }
}
