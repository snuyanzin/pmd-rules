package ru.nuyanzin.pmd.rules.java;

import java.util.Collection;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidWrongSLF4JSignatureUsage extends AbstractJavaRule {
  private static final String CURLY_BRACES_PATTERN = "{}";

  @Override
  public Object visit(ASTName node, Object data) {
    try {
      if (insideCatch(node) && isRuleViolate(node)) {
        addViolation(data, node);
      }
      return data;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private boolean isRuleViolate(ASTName node) {
    final String methodName = node.getImage();
    if (methodName == null) {
      System.out.println(
          "Please check the rule as node "
              + node + " and image is " + methodName);
      return false;
    }

    if (methodName.endsWith(".trace")
        || methodName.endsWith(".debug")
        || methodName.endsWith(".info")
        || methodName.endsWith(".warn")
        || methodName.endsWith(".error")
        || methodName.endsWith(".fatal")) {
      Collection<String> catchVariables = node
          .getNthParent(7)
          .findDescendantsOfType(ASTVariableDeclaratorId.class)
          .stream()
          .map(AbstractNode::getImage).collect(Collectors.toSet());

      final ASTArguments arguments =
          (ASTArguments) node.getNthParent(2).jjtGetChild(1).jjtGetChild(0);
      int argumentCount = arguments.getArgumentCount();
      if (argumentCount <= 1) {
        return true;
      }
      int curlyBracesCounter = 0;
      for (int i = 0; i < argumentCount; i++) {
        final AbstractJavaTypeNode expression =
            (AbstractJavaTypeNode) arguments
                .jjtGetChild(0).jjtGetChild(i).jjtGetChild(0);
        if (expression instanceof ASTPrimaryExpression) {
          final ASTPrimaryPrefix prefix =
              (ASTPrimaryPrefix) expression.jjtGetChild(0);
          // to prevent NPE on the next line
          if (!prefix.usesThisModifier() && !prefix.usesSuperModifier()) {
            Node prefixChild = prefix.jjtGetChild(0);

            if (prefixChild instanceof ASTLiteral) {
              if (i == argumentCount - 1
                  && !catchVariables.contains(
                      ((ASTLiteral) prefixChild).getEscapedStringLiteral())) {
                return true;
              }
              curlyBracesCounter += countCurlyBraces((ASTLiteral) prefixChild);
            } else if (prefixChild instanceof ASTAdditiveExpression) {
              curlyBracesCounter +=
                  countCurlyBraces((ASTAdditiveExpression) prefixChild);
            }
          }
        } else if (expression instanceof ASTAdditiveExpression) {
          curlyBracesCounter +=
              countCurlyBraces((ASTAdditiveExpression) expression);
        }
      }
      return curlyBracesCounter > 0 && argumentCount <= curlyBracesCounter + 1;
    }
    return false;
  }

  private boolean insideCatch(ASTName node) {
    Node n = node.jjtGetParent();
    while (n != null) {
      if (n instanceof ASTCatchStatement) {
        return true;
      } else if (n instanceof ASTForInit) {

       // init part is not technically inside the loop.
       // Skip parent ASTForStatement but continue higher
       // up to detect nested loops

        n = n.jjtGetParent();
      }
      n = n.jjtGetParent();
    }
    return false;
  }

  private int countCurlyBraces(ASTLiteral literal) {
    String str = literal.getEscapedStringLiteral();
    return str == null
        ? 0
        : (str.length() - str.replace(CURLY_BRACES_PATTERN, "").length())
            / CURLY_BRACES_PATTERN.length();
  }

  private int countCurlyBraces(ASTAdditiveExpression additiveExpression) {
    int curlyBracesCounter = 0;
    for (int j = 0; j < additiveExpression.jjtGetNumChildren(); j++) {
      final ASTPrimaryPrefix primaryPrefix =
          (ASTPrimaryPrefix) additiveExpression.jjtGetChild(j).jjtGetChild(0);
      // to prevent NPE on the next line
      if (!primaryPrefix.usesThisModifier()
          && !primaryPrefix.usesSuperModifier()) {
        Node primaryPrefixChild = primaryPrefix.jjtGetChild(0);
        if (primaryPrefixChild instanceof ASTLiteral) {
          curlyBracesCounter +=
              countCurlyBraces((ASTLiteral) primaryPrefix.jjtGetChild(0));
        } else if (primaryPrefixChild instanceof ASTExpression) {
          final Node primaryPrefixChildChild =
              primaryPrefixChild.jjtGetChild(0);
          if (primaryPrefixChildChild instanceof ASTAdditiveExpression) {
            curlyBracesCounter += countCurlyBraces(
                (ASTAdditiveExpression) primaryPrefixChildChild);
          }
        }
      }
    }
    return curlyBracesCounter;
  }
}
