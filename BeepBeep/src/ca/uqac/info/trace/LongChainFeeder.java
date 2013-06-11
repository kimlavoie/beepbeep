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

public class LongChainFeeder extends MessageFeeder
{
  protected int m_n;
  protected int m_curN;
  
  public LongChainFeeder(String param_string)
  {
    m_n = Integer.parseInt(param_string);
    m_curN = 0;
  }

  @Override
  public String next()
  {
    StringBuilder out = new StringBuilder();
    out.append("<message>\n");
    if (m_curN < m_n)
    {
      out.append("  <p>").append(m_curN).append("</p>\n");
      m_curN++;
    }
    else
    {
      for (int i = 0; i < m_n; i++)
      {
        out.append("  <q>").append(i).append("</q>\n");        
      }
      m_curN = 0;
    }
    out.append("</message>\n");
    return out.toString();
  }

}
