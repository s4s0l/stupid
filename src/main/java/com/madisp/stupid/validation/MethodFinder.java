package com.madisp.stupid.validation;

import com.madisp.stupid.ExpressionVisitor;
import com.madisp.stupid.MethodSignature;
import com.madisp.stupid.Value;
import com.madisp.stupid.VisitorAcceptor;
import com.madisp.stupid.expr.CallExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marcin Wielgus
 */
public class MethodFinder {

    public static Set<MethodSignature> findRequiredMethods(Value<?> expr) {
        if (expr instanceof VisitorAcceptor) {
            return findRequiredMethods((VisitorAcceptor) expr);
        } else {
            return Collections.emptySet();
        }
    }

    public static Set<MethodSignature> findRequiredMethods(VisitorAcceptor expr) {
        return expr.acceptVisitor(new FinderVisitor(), new HashSet<>());
    }

    private static class FinderVisitor implements ExpressionVisitor<Set<MethodSignature>> {
        @Override
        public Set<MethodSignature> onCall(Set<MethodSignature> initValue, CallExpression expr) {
            boolean root = expr.getBase() == null;
            initValue.add(new MethodSignature(expr.getIdentifier(), expr.getArgs().length, root));
            return ExpressionVisitor.super.onCall(initValue, expr);
        }
    }
}