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

import ca.uqac.info.util.PipeCallback;
import java.io.*;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

class EventNotifier implements PipeCallback<String>
{
  /*package*/ Vector<Monitor> m_monitors;
  /*package*/ Vector<Monitor.Verdict> m_verdicts;
  /*package*/ Vector<Map<String,String>> m_metadatas;
  public int m_numEvents = 0;
  public boolean m_notifyOnEvents = false;
  public boolean m_notifyOnVerdict = true;
  public boolean m_mirrorEventsOnStdout = false;
  public boolean m_csvToStdout = false;
  public long m_totalTime = 0;
  public long heapSize = 0;
  public int m_slowdown = 0;
  public String trace = new String();		// Modified by Kim Lavoie
  private BugReporterMantisWS report = new BugReporterMantisWS(); // Added by Kim Lavoie

  public EventNotifier()
  {
    m_monitors = new Vector<Monitor>();
    m_verdicts = new Vector<Monitor.Verdict>();
    m_metadatas = new Vector<Map<String,String>>();
  }
  
  public EventNotifier(boolean notifyOnEvents)
  {
    this();
    m_notifyOnEvents = notifyOnEvents;
  }

  public void addMonitor(Monitor w)
  {
    addMonitor(w, new HashMap<String,String>());
  }
  
  public void addMonitor(Monitor w, Map<String,String> metadata)
  {
    m_monitors.add(w);
    m_verdicts.add(Monitor.Verdict.INCONCLUSIVE);
    m_metadatas.add(metadata);
  }

  public int eventCount()
  {
    return m_numEvents;
  }

  @Override
  public void notify(String token, long buffer_size) throws CallbackException
  {
    m_numEvents++;
    trace += token;		// Modified by Kim Lavoie     
 
    //System.out.println(ESC_HOME + ESC_CLEARLINE + "Event received");
    if (m_mirrorEventsOnStdout)
    {
      System.out.print(token);
    }
    // Update all monitors
    Event e = new XPathEvent(token);
    long processing_time = 0;
    for (int i = 0; i < m_monitors.size(); i++)
    {
      long clock_start = System.nanoTime();
      Monitor m = m_monitors.elementAt(i);
      Monitor.Verdict old_out = m_verdicts.elementAt(i);
      try
      {
        m.processEvent(e);
      }
      catch (MonitorException ex)
      {
        throw new CallbackException(ex.getMessage());
      }
      Monitor.Verdict new_out = m.getVerdict();
      if (m_slowdown > 0)
      {
        try
        {
          // We force the monitor to slow down by sleeping N ms
          Thread.sleep(m_slowdown);
        }
        catch (InterruptedException ie)
        {
          // TODO Auto-generated catch block
          ie.printStackTrace();
        }
      }
      long clock_end = System.nanoTime();
      processing_time = clock_end - clock_start;
      m_totalTime += processing_time;
      m_verdicts.set(i, m.getVerdict());
      if (old_out != new_out && m_notifyOnVerdict)
      {
        Map<String,String> metadata = m_metadatas.elementAt(i);
        String command = null;
        if (new_out == Monitor.Verdict.TRUE)
        {
	  command = metadata.get("OnTrue");
	  // Modified by Kim Lavoie
	  // New metadata "ReportOn" help decide when to report bugs 
	  if(metadata.get("ReportOn").toLowerCase().equals("true") || metadata.get("ReportOn").toLowerCase().equals("both"));
	  {
		//Added by Raphael Laguerre
		//Test the add of a bug to Mantis via its web service interface
		report.sendReport(metadata.get("Filename"), metadata.get("Description"), trace);	
	  }
	  //
	}
        if (new_out == Monitor.Verdict.FALSE)
        {
	  command = metadata.get("OnFalse");
	  // Modified by Kim Lavoie
	  // New metadata "ReportOn" help decide when to report bugs 
	  if(metadata.get("ReportOn").toLowerCase().equals("false") || metadata.get("ReportOn").toLowerCase().equals("both"));
	  {
		report.sendReport(metadata.get("Filename"), metadata.get("Description"), trace);
	  }
	  //
	}
        if (command != null)
        {
          try
          {
            File f = new File(metadata.get("Filename"));
            String absolute_path = f.getAbsolutePath();
            String s_dir = absolute_path.substring(0, absolute_path.lastIndexOf(File.separator));
            File dir = new File(s_dir);
            Runtime.getRuntime().exec("./" + command, null, dir);
          }
          catch (IOException ioe)
          {
            // TODO Auto-generated catch block
            ioe.printStackTrace();
          }
        }
      }
    }
    heapSize = Math.max(heapSize, Runtime.getRuntime().totalMemory());
    if (m_notifyOnEvents)
    {
      Formatter format = new Formatter();
      format.format("\r%4d |%3d ms |%6d ms |%5d MB |%5d MB |%s          ", m_numEvents, (int) (processing_time / 1000000f), (int) (m_totalTime / 1000000f), (int) (heapSize / 1048576f), (int) (buffer_size / 1048576f), formatVerdicts());
      System.err.print(format.toString());
      //System.out.println(token);
    }
    if (m_csvToStdout)
    {
      Formatter format = new Formatter();
      format.format("%d,%d,%d,%d,%d,%s", m_numEvents, (int) (processing_time / 1000000f), (int) (m_totalTime / 1000000f), (int) (heapSize / 1048576f), buffer_size, m_verdicts);
      System.out.println(format.toString());
    }
  }

  public void reset()
  {
    m_numEvents = 0;
    m_totalTime = 0;
    for (int i = 0; i < m_monitors.size(); i++)
    {
      Monitor m = m_monitors.elementAt(i);
      m.reset();
      m_verdicts.setElementAt(Monitor.Verdict.INCONCLUSIVE, i);
    }
    if (m_notifyOnEvents)
    {
      printHeader();
    }
  }

  public void reset(int i)
  {
    m_numEvents = 0;
    Monitor m = m_monitors.elementAt(i);
    m.reset();
    m_verdicts.setElementAt(Monitor.Verdict.INCONCLUSIVE, i);		
  }
  
  protected void printHeader()
  {
    System.err.print("\nMsgs |Last   |Total     |Max heap |Buffer   |");
    for (Map<String,String> metadata : m_metadatas)
    {
      String caption = metadata.get("Caption");
      if (caption == null || caption.isEmpty())
        System.err.print("· ");
      else
        System.err.print(caption + " ");  
    }
    System.err.println(" ");
  }
  
  protected String formatVerdicts()
  {
    StringBuilder out = new StringBuilder();
    Iterator<Monitor.Verdict> iv = m_verdicts.iterator();
    Iterator<Map<String,String>> is = m_metadatas.iterator();
    while (iv.hasNext() && is.hasNext())
    {
      Map<String,String> metadata = is.next();
      Monitor.Verdict v = iv.next();
      String caption = metadata.get("Caption");
      if (caption == null)
        caption = "";
      if (v == Monitor.Verdict.INCONCLUSIVE)
        out.append("?");
      else if (v == Monitor.Verdict.TRUE)
        out.append("⊤");
      else if (v == Monitor.Verdict.FALSE)
        out.append("⊥");
      // We just pad the output with spaces to align with the length of the monitor's caption
      int length = caption.length();
      if (length == 0)
        length = 1;
      String space_pad = new String(new char[length]).replace("\0", " ");
      out.append(space_pad);
    }
    return out.toString();
  }
}
