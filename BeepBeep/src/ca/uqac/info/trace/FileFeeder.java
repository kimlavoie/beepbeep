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
import java.io.*;

import ca.uqac.info.util.TokenBuffer;

public class FileFeeder extends MessageFeeder
{
  protected FileInputStream m_fis;
  protected InputStreamReader m_isr;
  protected BufferedReader m_br;
  protected TokenBuffer m_buffer;
  protected Queue<String> m_queue;
  protected static final int m_chunkSize = 16384;
  
  public FileFeeder(String filename)
  {
    m_buffer = new TokenBuffer("<message>", "</message>");
    m_queue = new LinkedList<String>();
    try
    {
      m_fis = new FileInputStream(new File(filename));
      m_isr = new InputStreamReader(m_fis, "UTF8");
      m_br = new BufferedReader(m_isr);
    }
    catch (FileNotFoundException fnfe)
    {
      m_br = null;
      return;
    } 
    catch (UnsupportedEncodingException e)
    {
      m_br = null;
      return;
    }
  }

  @Override
  public String next()
  {
    if (m_br == null)
      return null;
    return m_queue.poll();
  }
  
  @Override
  public boolean hasNext()
  {
    if (m_br == null)
      return false;
    if (!m_queue.isEmpty())
      return true;
    String token = "";
    int chars_read = 0;
    do
    {
      char[] cbuf = new char[m_chunkSize];
      try
      {
        chars_read = m_br.read(cbuf, 0, m_chunkSize);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      m_buffer.append(cbuf);
      token = m_buffer.nextToken();
    } while (token.isEmpty() && chars_read > 0);
    if (!token.isEmpty())
    {
      m_queue.add(token);
      return true;
    }
    return false;
  }
}
