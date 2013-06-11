import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import ca.uqac.info.ltl.*;
import ca.uqac.info.ltl.Operator.ParseException;
import ca.uqac.info.monitor.*;

/**
 * 
 */

/**
 * @author sylvain
 *
 */
public class MonitorTest
{
  
  /**
   * Check if G monitor returns inconclusive when everything is OK
   */
  @Test
  public void testGInconclusive()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("G (/message/p = 0)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.INCONCLUSIVE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if G monitor returns false upon violation
   */
  @Test
  public void testGFalse2()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("G (/message/p = 0)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if G monitor returns false upon violation
   */
  @Test
  public void testGFalse1()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("G (/message/p = 0)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if F monitor returns true when everything is OK
   */
  @Test
  public void testFTrue1()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("F (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if F monitor returns false upon violation
   */
  @Test
  public void testFTrue2()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("F (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if F monitor returns inconclusive
   */
  @Test
  public void testFInconclusive()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("F (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.INCONCLUSIVE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if X monitor returns true
   */
  @Test
  public void testXTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("X (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if X monitor returns true
   */
  @Test
  public void testXFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("X (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if U monitor returns true
   */
  @Test
  public void testUTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("(/message/p = 0) U (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if U monitor returns false
   */
  @Test
  public void testUFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("(/message/p = 0) U (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>2</p></message>"));
    le.add(new XPathEvent("<message><p>1</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check if U monitor returns inconclusive
   */
  @Test
  public void testUInconclusive()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("(/message/p = 0) U (/message/p = 1)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    le.add(new XPathEvent("<message><p>0</p></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.INCONCLUSIVE)
      fail("Wrong verdict");
  }
  
  /**
   * Check universal quantifier
   */
  @Test
  public void testForAllTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (¬ (x = 0))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    le.add(new XPathEvent("<message><p>0</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check universal quantifier
   */
  @Test
  public void testForAllFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (¬ (x = 0))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check existential quantifier
   */
  @Test
  public void testExistsTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∃ x ∈ message/p : (x = 0)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check existential quantifier
   */
  @Test
  public void testExistsFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∃ x ∈ message/p : (x = 0)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testEqualityTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("0 = 0");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testEqualityFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("0 = 1");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testGreaterThanTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("bbc > ab");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testGreaterThanFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("ab > bbc");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testGreaterThanNumericTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("01.4 > 1.3");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check equality
   */
  @Test
  public void testGreaterThanNumericFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("1.3 > 1.4");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check contant true
   */
  @Test
  public void testTrueTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("⊤");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check negation
   */
  @Test
  public void testNegationFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("¬ (⊤)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  /**
   * Check negation
   */
  @Test
  public void testNegationTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("¬ (⊥)");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  /**
   * Check contant true
   */
  @Test
  public void testFalseFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("⊥");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  @Test
  public void testTwoQuantifiersFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (∃ y ∈ message/q : (x = y))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>2</p><p>3</p><q>0</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  @Test
  public void testTwoQuantifiersTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (∃ y ∈ message/q : (x = y))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q><q>3</q><q>1</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  @Test
  public void testQuantifierPredicateTrue()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (∀ y ∈ message/q[v=$x]/r : (x = y))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q><v>1</v><r>1</r></q><q>3</q><q>1</q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.TRUE)
      fail("Wrong verdict");
  }
  
  @Test
  public void testQuantifierPredicateFalse()
  {
    Monitor.Verdict verd = Monitor.Verdict.INCONCLUSIVE;
    Monitor m = createMonitor("∀ x ∈ message/p : (∀ y ∈ message/q[v=$x]/r : (x = y))");
    if (m == null)
      fail("Parse exception");
    List<Event> le = new LinkedList<Event>();
    le.add(new XPathEvent("<message><p>1</p><p>0</p><q><v>1</v><r>0</r></q></message>"));
    le.add(new XPathEvent("<message><p>1</p><p>0</p><p>3</p><q>0</q></message>"));
    for (Event e : le)
    {
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        fail("Monitor exception");
      }
      verd = m.getVerdict();
    }
    if (verd != Monitor.Verdict.FALSE)
      fail("Wrong verdict");
  }
  
  protected static Monitor createMonitor(String formula)
  {
    Operator o = null;
    try
    {
      o = Operator.parseFromString(formula);
    }
    catch (ParseException e)
    {
      return null;
    }
    MonitorFactory mf = new MonitorFactory();
    o.accept(mf);
    Monitor m = mf.getMonitor();
    return m;
  }

}
