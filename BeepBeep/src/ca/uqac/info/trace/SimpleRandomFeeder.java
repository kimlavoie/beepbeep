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

import java.util.Random;

public class SimpleRandomFeeder extends MessageFeeder
{
  protected static final Random m_random = new Random();
  
  public SimpleRandomFeeder()
  {
    super();
  }
  
  @Override
  public String next()
  {
    return "<message>\n  <x>" + m_random.nextInt(10) + "</x>\n</message>\n";
  }
}
