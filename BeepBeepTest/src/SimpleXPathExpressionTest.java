import static org.junit.Assert.*;

import org.junit.Test;
import ca.uqac.info.simplexpath.*;
import java.util.*;

public class SimpleXPathExpressionTest
{

  @Test
  public void testPath()
  {
    Element e = Element.parse("<message><p>0</p><p>1</p><q><r>9</r><s>10</s></q><p>2</p></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/p");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e);
    if (le.size() != 3)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && has1 && has2))
      fail("Wrong elements in answer");
  }
  
  @Test
  public void testMultiPath()
  {
    Element e = Element.parse("<message><q><p>0</p><p>1</p></q><q><r>9</r><s>10</s></q><q><p>2</p></q></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/q/p");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e);
    if (le.size() != 3)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && has1 && has2))
      fail("Wrong elements in answer");
  }
  
  @Test
  public void testPredicate()
  {
    Element e = Element.parse("<message><q><p>0</p><p>1</p></q><q><r>9</r><s>10</s></q><q><p>2</p></q></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/q[p=0]/p");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e);
    if (le.size() != 2)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && has1 && !has2))
      fail("Wrong elements in answer");
  }
  
  @Test
  public void testLongPredicate()
  {
    Element e = Element.parse("<message><q><p><w><s>1</s></w></p><r>0</r><p><w><s>0</s></w></p></q><q><p><w><s>1</s></w></p><r>1</r></q><q><p>2</p></q></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/q[p/w/s=0]/r");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e);
    if (le.size() != 1)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && !has1 && !has2))
      fail("Wrong elements in answer");
  }
  
  @Test
  public void testMultiPredicate()
  {
    Element e = Element.parse("<message><q><p>0</p><p>1</p></q><q><p>0</p><r>0</r><s>10</s></q><q><p>2</p></q></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/q[s=10][p=0]/r");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e);
    if (le.size() != 1)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && !has1 && !has2))
      fail("Wrong elements in answer");
  }
  
  @Test
  public void testResolver()
  {
    Element e = Element.parse("<message><q><p>0</p><p>1</p></q><q><p>0</p><r>0</r><s>10</s></q><q><p>2</p></q></message>");
    SimpleXPathExpression exp = SimpleXPathExpression.parse("/message/q[s=$x][p=$y]/r");
    Map<String,String> variables = new HashMap<String,String>();
    variables.put("x", "10");
    variables.put("y", "0");
    boolean has0 = false, has1 = false, has2 = false;
    List<Element> le = exp.evaluate(e, variables);
    if (le.size() != 1)
      fail("Wrong number of elements in answer");
    for (Element el : le)
    {
      if (el.toString().compareTo("0") == 0)
        has0 = true;
      if (el.toString().compareTo("1") == 0)
        has1 = true;
      if (el.toString().compareTo("2") == 0)
        has2 = true;
    }
    if (!(has0 && !has1 && !has2))
      fail("Wrong elements in answer");
  }

}
