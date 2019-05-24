package ru.nuyanzin.pmd.rules.java.tests;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.testframework.RuleTst;
import net.sourceforge.pmd.testframework.TestDescriptor;

public class AvoidIterativeInLoopsTest extends RuleTst {
  private Rule rule;

  @Before
  public void setUp() {
    rule = findRule("java-BulkAPIRule", "BulkAPIRule");
  }

  @Test
  public void testAcceptance() {
    System.out.println(rule);
    System.out.println(rule.getMessage());
    System.out.println(rule.getExamples());
    runTest(new TestDescriptor(TEST1, "NOPMD should work", 0, rule));
    runTest(new TestDescriptor(
        TEST2, "Should fail without exclude marker", 1, rule));
  }

  public AvoidIterativeInLoopsTest() {
       /* try {
            ruleSet = (ruleSetFactory).createRuleSet(AvoidIterativeInLoops.c);
        } catch (RuleSetNotFoundException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex);
        }*/

  }

  private static final String TEST1 =
      "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x; //NOPMD "
          + PMD.EOL + " } " + PMD.EOL + "}";

  private static final String TEST2 =
      "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x;"
          + PMD.EOL + " } " + PMD.EOL + "}";
}
