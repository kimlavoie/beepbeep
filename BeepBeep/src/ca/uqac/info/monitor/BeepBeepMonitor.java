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

import ca.uqac.info.ltl.Operator;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.info.util.FileReadWrite;
import ca.uqac.info.util.PipeReader;

import org.apache.commons.cli.*;

public class BeepBeepMonitor
{
  /**
   * Return codes
   */
  public static final int ERR_OK = 0;
  public static final int ERR_PARSE = 2;
  public static final int ERR_IO = 3;
  public static final int ERR_ARGUMENTS = 4;
  public static final int ERR_RUNTIME = 6;

  /**
   * Build string to identify versions
   */
  protected static final String BUILD_STRING = "20130506";

  public static void main(String[] args)
  {
    int verbosity = 1, slowdown = 0, tcp_port = 0;
    boolean show_stats = false, to_stdout = false;
    String trace_filename = "", pipe_filename = "", event_name = "message";
    final MonitorFactory mf = new MonitorFactory();
    
    // In case we open a socket
    ServerSocket m_serverSocket = null;
    Socket m_connection = null;

    // Parse command line arguments
    Options options = setupOptions();
    CommandLine c_line = setupCommandLine(args, options);
    assert c_line != null;
    if (c_line.hasOption("verbosity"))
    {
      verbosity = Integer.parseInt(c_line.getOptionValue("verbosity"));
    }
    if (verbosity > 0)
    {
      showHeader();
    }
    if (c_line.hasOption("version"))
    {
      System.err.println("(C) 2008-2013 Sylvain Hallé et al., Université du Québec à Chicoutimi");
      System.err.println("This program comes with ABSOLUTELY NO WARRANTY.");
      System.err.println("This is a free software, and you are welcome to redistribute it");
      System.err.println("under certain conditions. See the file COPYING for details.\n");
      System.exit(ERR_OK);
    }
    if (c_line.hasOption("h"))
    {
      showUsage(options);
      System.exit(ERR_OK);
    }
    if (c_line.hasOption("version"))
    {
      System.exit(ERR_OK);
    }
    if (c_line.hasOption("slowdown"))
    {
      slowdown = Integer.parseInt(c_line.getOptionValue("slowdown"));
      if (verbosity > 0)
        System.err.println("Slowdown factor: " + slowdown + " ms");
    }
    if (c_line.hasOption("stats"))
    {
      show_stats = true;
    }
    if (c_line.hasOption("csv"))
    {
      // Will output data in CSV format to stdout
      to_stdout = true;
    }
    if (c_line.hasOption("eventname"))
    {
      // Set event name
      event_name = c_line.getOptionValue("eventname");
    }
    if (c_line.hasOption("t"))
    {
      // Read events from a trace
      trace_filename = c_line.getOptionValue("t");
    }
    if (c_line.hasOption("p"))
    {
      // Read events from a pipe
      pipe_filename = c_line.getOptionValue("p");
    }
    if (c_line.hasOption("k"))
    {
      // Read events from a TCP port
      tcp_port = Integer.parseInt(c_line.getOptionValue("k"));
    }
    if (!trace_filename.isEmpty() && !pipe_filename.isEmpty())
    {
      System.err.println("ERROR: you must specify at most one of trace file or named pipe");
      showUsage(options);
      System.exit(ERR_ARGUMENTS);
    }
    @SuppressWarnings("unchecked")
    List<String> remaining_args = c_line.getArgList();
    if (remaining_args.isEmpty())
    {
      System.err.println("ERROR: no input formula specified");
      showUsage(options);
      System.exit(ERR_ARGUMENTS);
    }
    // Instantiate the event notifier
    boolean notify = (verbosity > 0);
    EventNotifier en = new EventNotifier(notify);
    en.m_slowdown = slowdown;
    en.m_csvToStdout = to_stdout;
    // Create one monitor for each input file and add it to the notifier 
    for (String formula_filename : remaining_args)
    {
      try
      {
        String formula_contents = FileReadWrite.readFile(formula_filename);
        Operator op = Operator.parseFromString(formula_contents);
        op.accept(mf);
        Monitor mon = mf.getMonitor();
        Map<String,String> metadata = getMetadata(formula_contents);
        metadata.put("Filename", formula_filename);
        en.addMonitor(mon, metadata);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        System.exit(ERR_IO);
      }
      catch (Operator.ParseException e)
      {
        System.err.println("Error parsing input formula");
        System.exit(ERR_PARSE);
      }
    }

    // Read trace and iterate
    // Opens file
    PipeReader pr = null;
    try
    {
      if (!pipe_filename.isEmpty())
      {
        // We tell the pipe reader we read a pipe
        File f = new File(pipe_filename);
        if (verbosity > 0)
          System.err.println("Reading from pipe named " + f.getName());
        pr = new PipeReader(new FileInputStream(f), en, false);
      }
      else if (!trace_filename.isEmpty())
      {
        // We tell the pipe reader we read a regular file
        File f = new File(trace_filename);
        if (verbosity > 0)
          System.err.println("Reading from file " + f.getName());
        pr = new PipeReader(new FileInputStream(f), en, true);
      }
      else if (tcp_port > 0)
      {
        // We tell the pipe reader we read from a socket
        if (verbosity > 0)
          System.err.println("Reading from TCP port " + tcp_port);
        m_serverSocket = new ServerSocket(tcp_port);
        m_connection = m_serverSocket.accept();
        pr = new PipeReader(m_connection.getInputStream(), en, false);
      }
      else
      {
        // We tell the pipe reader we read from standard input
        if (verbosity > 0)
          System.err.println("Reading from standard input");
        pr = new PipeReader(System.in, en, false);
      }      
    }
    catch (FileNotFoundException ex)
    {
      // We print both trace and pipe since one of them must be empty
      System.err.println("ERROR: file not found " + trace_filename + pipe_filename);
      System.exit(ERR_IO);
    }
    catch (IOException e)
    {
      // Caused by socket error
      e.printStackTrace();
      System.exit(ERR_IO);
    }
    pr.setSeparator("<" + event_name + ">", "</" + event_name + ">");
    
    // Check parameters for the event notifier
    if (c_line.hasOption("no-trigger"))
    {
      en.m_notifyOnVerdict = false;
    }
    else
    {
      en.m_notifyOnVerdict = true;
    }
    if (c_line.hasOption("mirror"))
    {
      en.m_mirrorEventsOnStdout = true;
    }
    
    // Start event notifier
    en.reset();
    Thread th = new Thread(pr);
    long clock_start = System.nanoTime();
    th.start();
    try
    {
      th.join(); // Wait for thread to finish
    }
    catch (InterruptedException e1)
    {
      // Thread is finished
    }
    if (tcp_port > 0 && m_serverSocket != null)
    {
      // We opened a socket; now we close it
      try
      {
        m_serverSocket.close();
      }
      catch (IOException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    long clock_end = System.nanoTime();
    int ret_code = pr.getReturnCode();
    switch (ret_code)
    {
    case PipeReader.ERR_EOF:
      if (verbosity > 0)
        System.err.println("\nEnd of file reached");
      break;
    case PipeReader.ERR_EOT:
      if (verbosity > 0)
        System.err.println("\nEOT received on pipe: closing");
      break;
    case PipeReader.ERR_OK:
      // Do nothing
      break;
    default:
      // An error
      System.err.println("Runtime error");
      System.exit(ERR_RUNTIME);
      break;
    }
    if (show_stats)
    {
      if (verbosity > 0)
      {
        System.out.println("Messages:   " + en.m_numEvents);
        System.out.println("Time:       " + (int) (en.m_totalTime / 1000000f) + " ms");
        System.out.println("Clock time: " + (int) ((clock_end - clock_start) / 1000000f) + " ms");
        System.out.println("Max heap:   " + (int) (en.heapSize / 1048576f) + " MB");
      }
      else
      {
        // If stats are asked but verbosity = 0, only show time value
        // (both monitor and wall clock) 
        System.out.print((int) (en.m_totalTime / 1000000f));
        System.out.print(",");
        System.out.print((int) ((clock_end - clock_start) / 1000000f));
      }
    }
    System.exit(ERR_OK);
  }

  /**
   * Sets up the options for the command line parser
   * @return The options
   */
  @SuppressWarnings("static-access")
  private static Options setupOptions()
  {
    Options options = new Options();
    Option opt;
    opt = OptionBuilder
        .withLongOpt("help")
        .withDescription(
            "Display command line usage")
            .create("h");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("trace")
        .withArgName("filename")
        .hasArg()
        .withDescription(
            "Read trace from filename")
            .create("t");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("pipe")
        .withArgName("filename")
        .hasArg()
        .withDescription(
            "Read trace from named pipe filename")
            .create("p");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("socket")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Read trace from TCP port x")
            .create("k");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("version")
        .withDescription(
            "Show version")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("csv")
        .withDescription(
            "Write stats in CSV format to the standard error")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("no-trigger")
        .withDescription(
            "Disable triggers for violation/fulfillment")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("mirror")
        .withDescription(
            "Mirror events received to the standard error")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("verbosity")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Verbose messages with level x")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("slowdown")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Add idle time of x ms between each event")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("eventname")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Set event name to x (default: \"message\")")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("stats")
        .withDescription(
            "Show statistics about monitor processing")
            .create();
    options.addOption(opt);
    return options;
  }

  /**
   * Show the benchmark's usage
   * @param options The options created for the command line parser
   */
  private static void showUsage(Options options)
  {
    HelpFormatter hf = new HelpFormatter();
    hf.printHelp("java -jar BeepBeep.jar [options] [file1 [file2 [file3] ...]]", options);
  }

  /**
   * Sets up the command line parser
   * @param args The command line arguments passed to the class' {@link main}
   * method
   * @param options The command line options to be used by the parser
   * @return The object that parsed the command line parameters
   */
  private static CommandLine setupCommandLine(String[] args, Options options)
  {
    CommandLineParser parser = new PosixParser();
    CommandLine c_line = null;
    try
    {
      // parse the command line arguments
      c_line = parser.parse(options, args);
    }
    catch (ParseException exp)
    {
      // oops, something went wrong
      System.err.println("ERROR: " + exp.getMessage() + "\n");
      //HelpFormatter hf = new HelpFormatter();
      //hf.printHelp(t_gen.getAppName() + " [options]", options);
      System.exit(ERR_ARGUMENTS);    
    }
    return c_line;
  }

  public static void showHeader()
  {
    System.err.println("BeepBeep, a runtime monitor for LTL-FO+ with XML events");
    System.err.println("Version 1.6.2b, build " + BUILD_STRING);
  }  
  
  /**
   * Parses optional metadata that can be found in a formula's input file.
   * The metadata must appear in the comment lines (those beginning with
   * a "#" symbol) and must be of the form:
   * <pre>
   * # @Param("some value");
   * </pre>
   * where Param is some (user-defined) parameter name. Parameters may
   * span multiple lines, which still must each begin with the comment
   * symbol, as follows:
   * <pre>
   * # @Param("some value
   * #     that spans multiple lines");
   * </pre>
   * The "#" and extraneous spaces are removed on parsing. Currently BeepBeep
   * uses the parameter "Caption", if present, to display a name for each
   * monitor. All other parameters are presently ignored.
   * @param file_contents The string contents of the formula file
   * @return A map associating parameters to values
   */
  public static Map<String,String> getMetadata(String fileContents)
  {
    Map<String,String> out_map = new HashMap<String,String>();
    StringBuilder comment_contents = new StringBuilder();
    Pattern pat = Pattern.compile("^\\s*?#(.*?)$", Pattern.MULTILINE);
    Matcher mat = pat.matcher(fileContents);
    while (mat.find())
    {
      String line = mat.group(1).trim();
      comment_contents.append(line).append(" ");
    }
    pat = Pattern.compile("@(\\w+?)\\((.*?)\\);");
    mat = pat.matcher(comment_contents);
    while (mat.find())
    {
      String key = mat.group(1);
      String val = mat.group(2).trim();
      if (val.startsWith("\"") && val.endsWith("\""))
      {
        // Trim double quotes if any
        val = val.substring(1, val.length() - 1);
      }
      out_map.put(key, val);
    }
    return out_map;
  }
}
