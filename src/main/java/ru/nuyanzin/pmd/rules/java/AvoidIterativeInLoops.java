package ru.nuyanzin.pmd.rules.java;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidIterativeInLoops extends AbstractJavaRule {
  private static final Set<String> FORBIDDEN_METHODS =
      ResourceFileReader.readFromFile("/BulkAPIRule/exactMatching");
  private static final Set<String> FORBIDDEN_METHODS_END_WITH =
      ResourceFileReader.readFromFile("/BulkAPIRule/endWithMatching");

  @Override
  public Object visit(ASTName node, Object data) {
    try {
      if (insideLoop(node) && isRuleViolate(node)) {
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
      System.out.println("Please check the rule as node "
          + node + " and image is " + methodName);
      return false;
    }

    return FORBIDDEN_METHODS.contains(methodName)
        || FORBIDDEN_METHODS_END_WITH.stream().anyMatch(methodName::endsWith);
  }

  private boolean insideLoop(ASTName node) {
    Node n = node.jjtGetParent();
    while (n != null) {
      if (n instanceof ASTDoStatement
          || n instanceof ASTWhileStatement
          || n instanceof ASTForStatement) {
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
}
