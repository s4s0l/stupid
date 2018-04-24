package com.madisp.stupid;

import com.madisp.stupid.expr.AndExpression;
import com.madisp.stupid.expr.ApplyExpression;
import com.madisp.stupid.expr.AssignExpression;
import com.madisp.stupid.expr.BlockExpression;
import com.madisp.stupid.expr.CallExpression;
import com.madisp.stupid.expr.ComparisonExpression;
import com.madisp.stupid.expr.ConstantExpression;
import com.madisp.stupid.expr.DivisionExpression;
import com.madisp.stupid.expr.EqualsExpression;
import com.madisp.stupid.expr.MinusExpression;
import com.madisp.stupid.expr.MultiplicationExpression;
import com.madisp.stupid.expr.NegateExpression;
import com.madisp.stupid.expr.NotExpression;
import com.madisp.stupid.expr.OrExpression;
import com.madisp.stupid.expr.PlusExpression;
import com.madisp.stupid.expr.ResourceExpression;
import com.madisp.stupid.expr.StatementListExpression;
import com.madisp.stupid.expr.TernaryExpression;
import com.madisp.stupid.expr.VarExpression;
import com.madisp.stupid.gen.StupidBaseVisitor;
import com.madisp.stupid.gen.StupidLexer;
import com.madisp.stupid.gen.StupidParser;
import com.madisp.stupid.util.CharSupport;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ExpressionFactory is used to parse a String into an Expression tree.
 * Currently the underlying implementation uses ANTLR4, but this may change
 * in the future.
 * <p>
 * Note: Only the parseExpression(...) method is considered to be a part
 * of the public API.
 */
public class ExpressionFactory extends StupidBaseVisitor<Expression> {

    /**
     * Tokenize, lex and visit an expression represented by a String.
     * The root node of the resulting expression tree will be returned.
     *
     * @param expression A stupid expression
     * @return The root node of the resulting expression tree
     */
    public Expression parseExpression(String expression) {
        CodePointCharStream codePointCharStream = CharStreams.fromString(expression);
        StupidLexer lexer = new StupidLexer(codePointCharStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        StupidParser parser = new StupidParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        return visitProg(parser.prog());
    }

    @Override
    public Expression visitProg( StupidParser.ProgContext ctx) {
        List<Expression> statements = new ArrayList<>();
        StupidParser.StatementsContext xs = ctx.statements();
        while (xs != null) {
            statements.add(visitExpr(xs.expr()));
            xs = xs.statements();
        }
        return new StatementListExpression(statements);
    }

    @Override
    public Expression visitExpr( StupidParser.ExprContext ctx) {
        if (ctx.value() != null) {
            return visitValue(ctx.value());
        }
        if (ctx.AND() != null) {
            return new AndExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.MINUS() != null && ctx.left != null) {
            return new MinusExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.PLUS() != null) {
            return new PlusExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.OR() != null) {
            return new OrExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.STAR() != null) {
            return new MultiplicationExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.SLASH() != null) {
            return new DivisionExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.LPAREN() != null) {
            if (ctx.DOT() != null) {
                // apply, not value
                StupidParser.ArgslistContext args = ctx.argslist();
                List<Expression> argsList = new ArrayList<>();
                while (args != null) {
                    argsList.add(visitExpr(args.expr()));
                    args = args.argslist();
                }
                return new ApplyExpression(visitExpr(ctx.left), argsList.toArray(new Expression[0]));
            }
            return visitExpr(ctx.center);
        } else if (ctx.BANG() != null) {
            return new NotExpression(visitExpr(ctx.center));
        } else if (ctx.MINUS() != null) {
            return new NegateExpression(visitExpr(ctx.center));
        } else if (ctx.EQUALS() != null && ctx.EQUALS().size() == 2) {
            return new EqualsExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.LANGLE() != null) {
            return new ComparisonExpression(visitExpr(ctx.left), visitExpr(ctx.right));
        } else if (ctx.RANGLE() != null) {
            return new ComparisonExpression(visitExpr(ctx.right), visitExpr(ctx.left));
        } else if (ctx.var() != null) {
            Expression base = null;
            if (ctx.DOT() != null) {
                base = visitExpr(ctx.left);
            }
            return new VarExpression(base, ctx.var().IDENTIFIER().getText());
        } else if (ctx.call() != null) {
            StupidParser.ArgslistContext args = ctx.call().argslist();
            List<Expression> argsList = new ArrayList<>();
            while (args != null) {
                argsList.add(visitExpr(args.expr()));
                args = args.argslist();
            }
            Expression base = null;
            if (ctx.DOT() != null) {
                base = visitExpr(ctx.left);
            }
            return new CallExpression(base, ctx.call().IDENTIFIER().getText(),
                    argsList.toArray(new Expression[0]));
        } else if (ctx.ASK() != null) {
            return new TernaryExpression(visitExpr(ctx.left), visitExpr(ctx.center), visitExpr(ctx.right));
        } else if (ctx.assign() != null) {
            Expression base = null;
            if (ctx.DOT() != null) {
                base = visitExpr(ctx.left);
            }
            return new AssignExpression(base, ctx.assign().IDENTIFIER().getText(), visitExpr(ctx.assign().expr()));
        }
        return super.visitExpr(ctx);
    }

    @Override
    public Expression visitValue(StupidParser.ValueContext ctx) {
        if (ctx.bool() != null) {
            return visitBool(ctx.bool());
        } else if (ctx.str() != null) {
            return visitStr(ctx.str());
        } else if (ctx.num() != null) {
            return visitNum(ctx.num());
        } else if (ctx.nil() != null) {
            return visitNil(ctx.nil());
        } else if (ctx.block() != null) {
            return visitBlock(ctx.block());
        } else if (ctx.resource() != null) {
            return visitResource(ctx.resource());
        }
        return super.visitValue(ctx);
    }

    @Override
    public Expression visitBool(StupidParser.BoolContext ctx) {
        if (ctx.TRUE() != null) {
            return new ConstantExpression(true);
        } else if (ctx.FALSE() != null) {
            return new ConstantExpression(false);
        }
        return super.visitBool(ctx);
    }

    @Override
    public Expression visitStr(StupidParser.StrContext ctx) {
        return new ConstantExpression(CharSupport.getStringFromGrammarStringLiteral(ctx.getText()));
    }

    @Override
    public Expression visitNum(StupidParser.NumContext ctx) {
        if (ctx.INT() != null) {
            return new ConstantExpression(Integer.parseInt(ctx.getText()));
        } else if (ctx.DOUBLE() != null) {
            return new ConstantExpression(Double.parseDouble(ctx.getText()));
        }
        return super.visitNum(ctx);
    }

    @Override
    public Expression visitNil(StupidParser.NilContext ctx) {
        return new ConstantExpression(null);
    }

    @Override
    public Expression visitBlock(StupidParser.BlockContext ctx) {
        StupidParser.VarlistContext vars = ctx.varlist();
        List<String> varsList = new ArrayList<>();
        while (vars != null) {
            varsList.add(vars.IDENTIFIER().getText());
            vars = vars.varlist();
        }

        List<Expression> statements = new ArrayList<>();
        StupidParser.StatementsContext xs = ctx.statements();
        while (xs != null) {
            statements.add(visitExpr(xs.expr()));
            xs = xs.statements();
        }

        return new BlockExpression(varsList.toArray(new String[0]), new StatementListExpression(statements));
    }

    @Override
    public Expression visitResource(StupidParser.ResourceContext ctx) {
        if (ctx.NULL() != null) {
            return new ConstantExpression(0);
        }
        String pckg = null;
        if (ctx.pckg() != null) {
            pckg = ctx.pckg().getText();
        }
        String type = ctx.type.getText();
        String name = ctx.name.getText();
        return new ResourceExpression(pckg, type, name);
    }
}
