/*
        BeepBeep, an LTL-FO+ runtime monitor with XML events
        Copyright (C) 2008-2013 Sylvain Hallé

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.info.trace;

import java.util.*;

public class PinguLevel extends MessageFeeder
{
  protected int m_numPingus;
  protected int m_width = 100;
  protected Random m_random;
  
  protected Set<Pingu> m_pingus;
  
  public PinguLevel(String param_string)
  {
    super();
    m_random = new Random();
    m_numPingus = Integer.parseInt(param_string);
    m_pingus = new HashSet<Pingu>();
    m_pingus.add(new Pingu(-1, 0, 0, Pingu.Action.BLOCKER));
    m_pingus.add(new Pingu(-2, m_width, 0, Pingu.Action.BLOCKER));
    for (int i = 0; i < m_numPingus; i++)
    {
      Pingu p = new Pingu(i);
      p.m_x = m_random.nextInt(m_width - 4) + 2;
      m_pingus.add(p);
    }
  }

  @Override
  public String next()
  {
    movePingus();
    StringBuilder out = new StringBuilder();
    out.append("<message>\n");
    out.append(" <characters>\n");
    for (Pingu p : m_pingus)
    {
      out.append("  <character>\n");
      out.append("   <id>").append(p.m_id).append("</id>\n");
      out.append("   <status>").append(p.m_action).append("</status>\n");
      out.append("   <position>\n");
      out.append("    <x>").append(p.m_x).append("</x>\n");
      out.append("    <y>").append(p.m_y).append("</y>\n");
      out.append("   </position>\n");
      out.append("   <velocity>\n");
      out.append("    <x>").append(p.m_velocityX).append("</x>\n");
      out.append("    <y>").append(p.m_velocityY).append("</y>\n");
      out.append("   </velocity>\n");
      out.append("  </character>\n");
    }
    out.append(" </characters>\n");
    out.append("</message>\n");
    return out.toString();
  }
  
  protected void movePingus()
  {
    Set<Pingu> new_pingus = new HashSet<Pingu>();
    for (Pingu p : m_pingus)
    {
      Pingu new_p = new Pingu(p);
      new_p.move(m_pingus);
      new_pingus.add(new_p);
    }
    m_pingus = new_pingus;
  }
  
  protected static class Pingu
  {
    protected static final Random m_random = new Random();
    protected static final int m_pinguRadius = 5;
    protected static final Map<String,Boolean> m_bugJumpers = new HashMap<String,Boolean>();
    public enum Action {WALKER, BLOCKER, DEAD};
    int m_x = 0;
    int m_y = 0;
    int m_velocityX = 1;
    int m_velocityY = 0;
    int m_id = 0;
    Action m_action;

    public Pingu()
    {
      super();
    }
    
    public Pingu(int id)
    {
      this();
      m_id = id;
      m_x = 0;
      m_y = 0;
      m_action = Action.WALKER;
    }
    
    public Pingu(int id, int x, int y, Action a)
    {
      this(id);
      m_x = x;
      m_y = y;
      m_action = a;
    }
    
    public Pingu(Pingu p)
    {
      this(p.m_id);
      m_x = p.m_x;
      m_y = p.m_y;
      m_velocityX = p.m_velocityX;
      m_velocityY = p.m_velocityY;
      m_action = p.m_action;
    }
    
    public static void addBug(String name)
    {
      m_bugJumpers.put(name, true);
    }
    
    public static boolean hasBug(String name)
    {
      if (m_bugJumpers.containsKey(name))
        return m_bugJumpers.get(name);
      return false;
    }
    
    @Override
    public int hashCode()
    {
      return m_id;
    }
    
    @Override
    public boolean equals(Object o)
    {
      if (!(o instanceof Pingu))
        return false;
      return (equals((Pingu)o));
    }
    
    public boolean equals(Pingu p)
    {
      return p.m_id == m_id;
    }
    
    public void move(Set<Pingu> old_pingus)
    {
      // A blocker: we don't move
      if (m_action == Action.BLOCKER)
        return;
      
      // Spontaneous walker freeze
      if (m_random.nextBoolean() && hasBug("Walker freeze"))
      {
        return;
      }
      
      // Increment by velocity
      m_x += m_velocityX;
      m_y += m_velocityY;
      // Check if blocker with same position
      for (Pingu p : old_pingus)
      {
        if (p.m_action == Action.BLOCKER && at(p))
        {
          m_velocityX *= -1;
          m_velocityY *= -1;
        }
      }
    }
    
    @Override
    public String toString()
    {
      return "[" + m_id + " (" + m_x + "," + m_y + ")]"; 
    }
    
    public boolean at(Pingu p)
    {
      return Math.abs(p.m_x - m_x) < m_pinguRadius &&
          Math.abs(p.m_y - m_y) < m_pinguRadius;
    }
  }

}
