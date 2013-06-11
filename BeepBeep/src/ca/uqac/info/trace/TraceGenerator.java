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

import org.apache.commons.cli.*;

public class TraceGenerator
{
  /**
   * Return codes
   */
  public static final int ERR_OK = 0;
  public static final int ERR_PARSE = 2;
  public static final int ERR_IO = 3;
  public static final int ERR_ARGUMENTS = 4;
  public static final int ERR_RUNTIME = 6;
  
  public static void main(String[] args)
  {
    // Parse command line arguments
    Options options = setupOptions();
    CommandLine c_line = setupCommandLine(args, options);
    int verbosity = 0, max_events = 1000000;
    
    if (c_line.hasOption("h"))
    {
      showUsage(options);
      System.exit(ERR_OK);
    }
    String generator_name = "simplerandom";
    if (c_line.hasOption("g"))
    {
      generator_name = c_line.getOptionValue("generator");
    }
    long time_per_msg = 1000;
    if (c_line.hasOption("i"))
    {
      time_per_msg = Integer.parseInt(c_line.getOptionValue("i"));
    }
    if (c_line.hasOption("verbosity"))
    {
      verbosity = Integer.parseInt(c_line.getOptionValue("verbosity"));
    }
    if (c_line.hasOption("e"))
    {
      max_events = Integer.parseInt(c_line.getOptionValue("e"));
    }
    String feeder_params = "";
    if (c_line.hasOption("parameters"))
    {
      feeder_params = c_line.getOptionValue("parameters");
    }
    MessageFeeder mf = instantiateFeeder(generator_name, feeder_params);
    if (mf == null)
    {
      System.err.println("Unrecognized feeder name");
      System.exit(ERR_ARGUMENTS);
    }
    // Trap Ctrl-C to send EOT before shutting down
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
         char EOT = 4;
         System.out.print(EOT);
      }
    });
    for (int num_events = 0; mf.hasNext() && num_events < max_events; num_events++)
    {
      long time_begin = System.nanoTime();
      String message = mf.next();
      StringBuilder out = new StringBuilder();
      out.append(message);
      System.out.print(out.toString());
      long time_end = System.nanoTime();
      long duration = time_per_msg - (long) ((time_end - time_begin) / 1000000f);
      if (duration > 0)
      {
        try
        {
          Thread.sleep(duration);
        }
        catch (InterruptedException e)
        {
        }
      }
      if (verbosity > 0)
      {
        System.err.print("\r" + num_events + " " + duration + "/" + time_per_msg);
      }
    }
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
        .withLongOpt("interval")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Generate a new message every x milliseconds")
            .create("i");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("verbosity")
        .withArgName("x")
        .hasArg()
        .withDescription(
            "Set verbosity level to x (0 = nothing)")
            .create();
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("generator")
        .withArgName("name")
        .hasArg()
        .withDescription(
            "Use generator name")
            .create("g");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("events")
        .withArgName("n")
        .hasArg()
        .withDescription(
            "Stop after generating n events")
            .create("e");
    options.addOption(opt);
    opt = OptionBuilder
        .withLongOpt("parameters")
        .withArgName("s")
        .hasArg()
        .withDescription(
            "Pass parameter string s to generator")
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
    hf.printHelp("java -jar TraceGenerator.jar [options]", options);
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
    catch (org.apache.commons.cli.ParseException exp)
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
    System.out.println("A simple event trace generator");
  } 
  
  public static MessageFeeder instantiateFeeder(String name, String params)
  {
    MessageFeeder mf = null;
    if (name.compareToIgnoreCase("constant") == 0)
      mf = new ConstantFeeder();
    else if (name.compareToIgnoreCase("longchain") == 0)
      mf = new LongChainFeeder(params);
    else if (name.compareToIgnoreCase("pingulevel") == 0)
      mf = new PinguLevel(params);
    else if (name.compareToIgnoreCase("filefeeder") == 0)
      mf = new FileFeeder(params);
    else if (name.compareToIgnoreCase("simplerandom") == 0)
      mf = new SimpleRandomFeeder();
    return mf;
  }
  
}
