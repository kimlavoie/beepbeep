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
package ca.uqac.info.monitor;

import ca.uqac.info.ltl.Atom;

public abstract class UnaryMonitor extends Monitor
{
  /**
   * The internal monitor for the inside property
   */
  protected Monitor m_phi;
  
  public UnaryMonitor(Monitor m)
  {
    super();
    m_phi = m;
  }
  
  public void setValue(Atom a, Atom v)
  {
    m_phi.setValue(a,  v);
  }
  
  @Override
  public void reset()
  {
	  super.reset();
	  m_phi.reset();
  }
}
