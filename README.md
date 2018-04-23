
stupid [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://raw.githubusercontent.com/s4s0l/stupid/master/LICENSE)[![Run Status](https://api.shippable.com/projects/5ad3fd338b320e07000a5e69/badge?branch=master)](https://app.shippable.com/github/s4s0l/stupid)[![Coverage Badge](https://api.shippable.com/projects/5ad3fd338b320e07000a5e69/coverageBadge?branch=master)](https://app.shippable.com/github/s4s0l/stupid)[![GitHub release](https://img.shields.io/github/release/s4s0l/stupid/all.svg?style=plastic)](https://github.com/s4s0l/stupid/releases/latest)[![Bintray](https://img.shields.io/bintray/v/sasol-oss/maven/stupid.svg?style=plastic)](https://bintray.com/sasol-oss/maven/stupid)
======

A simple scripting language with a Java runtime and interoperability.

Usage
-----

Imports are omitted for brevity.

Compiling and evaluating an expression (essentially a glorified calculator):

```java
// create a new parser
ExpressionFactory parser = new ExpressionFactory();
// parse a string into an expression tree
Value expr = parser.parseExpression("3 + 5");
// evaluate the expression and print the result
System.out.println(expr.value(new BaseContext())); // prints "8"
```

Lets spice things up and use a context with some variables instead of the plain `BaseContext`.

```java
ExpressionFactory parser = new ExpressionFactory();
Value expr = parser.parseExpression("pi*r*r)");
Map<String, Object> vars = new HashMap<String, Object>();
vars.put("pi", Math.PI);
vars.put("r", 2.0f);
// prints "Area of a circle with r=2: 12.566370614359172"
System.out.println("Area of a circle with r=2: " + expr.value(new VarContext(vars)));
```

Lets also bind it to the `System.out` object so we can call the `println` method from the script:

```java
ExpressionFactory parser = new ExpressionFactory();
Value expr = parser.parseExpression("println('Area of a circle with r=2: ' + pi*r*r)");
// a stack context to keep both our vars and binding to System.out
StackContext ctx = new StackContext();
Map<String, Object> vars = new HashMap<String, Object>();
vars.put("pi", Math.PI);
vars.put("r", 2.0f);
ctx.pushExecContext(new VarContext(vars));
// push a reflection context that binds to system.out onto the stack
ctx.pushExecContext(new ReflectionContext(System.out));
// prints "Area of a circle with r=2: 12.566370614359172"
expr.value(ctx);
```

The Language
------------

Mostly really similar to Java. The grammar lives in `src/main/antlr4/Stupid.g4`. A short introduction is available in a [blogpost](http://madisp.com/stupid/2013/12/27/about-stupid.html).

License
-------

MIT License, see the LICENSE file.
