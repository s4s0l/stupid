package com.madisp.stupid;

import com.madisp.stupid.expr.AssignExpression;
import com.madisp.stupid.expr.CallExpression;
import com.madisp.stupid.expr.VarExpression;

/**
 * @author Marcin Wielgus
 */
public interface ExpressionVisitor<T> {

    default T onExpression(T initValue, Expression<?> expr) {
        return visitChildren(initValue, expr);
    }

    default T onAssign(T initValue, AssignExpression expr) {
        return visitChildren(initValue, expr);
    }

    default T onCall(T initValue, CallExpression expr) {
        return visitChildren(initValue, expr);
    }

    default T onVarExpression(T initValue, VarExpression expr) {
        return visitChildren(initValue, expr);
    }

    default T visitChildren(T initValue, Expression<?> expr) {
        T nextVal = initValue;
        if (expr.children() != null) {
            for (Expression<?> expression : expr.children()) {
                if (expression != null) {
                    nextVal = expression.acceptVisitor(this, nextVal);
                }
            }
        }
        return nextVal;
    }


}
