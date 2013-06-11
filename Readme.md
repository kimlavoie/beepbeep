BeepBeep: A Runtime Monitor and Trace Validator
===============================================

(User Manual, version 2013-04-23)

BeepBeep is a *runtime monitor*: it receives a stream of events produced
by some application or process, and constantly analyzes it against a
specification given beforehand. When the stream of events deviates from what
the specification stipulates, a signal is sent which can then be piped into
another program for further processing. It can also work in offline
mode and analyze a pre-recorded trace of events taken from a file.

This generic process can be put to numerous uses. Given log events from a
web server, BeepBeep can work as an intrusion detection system. When fed
debug output from a program under development, the tool can prove useful to
detect bugs.

Finally, BeepBeep can either be used as a stand-alone program running
from the command line, or as a Java library that can be directly integrated
into your application.

Table of contents                                                    {#toc}
-----------------

- [A Quick Example](#quickstart)
- [Compiling and installing BeepBeep](#install)
- [Events and Event Traces](#traces)
- [Passing Events to BeepBeep](#piping)
- [Writing Properties on Event Traces](#properties)
- [Triggering Actions](#triggers)
- [Command-Line Options](#cli)
- [Using the Trace Generator](#generator)
- [Some Applications](#applications)
- [About the Author](#about)

A Quick Example                                               {#quickstart}
---------------

Let us consider a simple program which, about once per second, generates an
event of the following form and sends it to the standard output:

    <message>
      <p>some integer value</p>
      <q>some integer value</q>
    </message>

In BeepBeep's `dist` folder, a program called TraceGenerator does exactly
that. To be convinced, call it on the command line by typing:

    java -jar TraceGenerator.jar

Observe that the program indeed generates events of that form (actually, the
TraceGenerator can do [many more things](#generator)); hit Ctrl-C to stop
the generator. Suppose now we want to be alerted whenever the parameter `p`
has a value greater than 7. In the example folder, the file called
`GreaterThan7.ltlfo` contains that specification. To detect it
automatically, we pipe the generator's output into the BeepBeep runtime
monitor by typing:

    java -jar TraceGenerator.jar | java -jar BeepBeep.jar Examples/GreaterThan7.ltlfo

This will produce an animated output that looks like this:

    Msgs |Last   |Total     |Max heap |Buffer   |G7
       9 |  1 ms |    12 ms |   56 MB |    0 MB |?          

The events can no longer be seen, but BeepBeep shows an event counter (Msgs)
that increments at the rate of one per second, indicating they are indeed
received and processed. At the end of the line, the "G7" field indicates the
state of the property to monitor. G7 is the shorthand name we gave in the
input file to identify our property (since BeepBeep can monitor multiple
properties at the same time). Right below, BeepBeep should show the value
"?" (meaning "inconclusive") as long as no event with parameter `p` greater
than 7 has been received, which will switch to "⊤" (meaning "true") upon
encountering the first event with `p` > 5. This is what you will observe if
you wait long enough.

The Example folder contains other examples of properties, whose syntax will
be described [later](#properties). To monitor many of them all at once,
simply put all the file names on the command line:

    java -jar TraceGenerator.jar | java -jar BeepBeep.jar \
      Examples/GreaterThan7.ltlfo \
      Examples/PinQ.ltlfo \
      Examples/TwiceTheSame.ltlfo \

Or in a shorter way:

    java -jar TraceGenerator.jar | java -jar BeepBeep.jar Examples/*.ltlfo

In this case, each property may be fulfilled or violated independently of
the others --hence each has its own state. BeepBeep's display will show the
shorthand name of each monitor and, directly below, the symbol representing
the state for this monitor:

    Msgs |Last   |Total     |Max heap |Buffer   |G7 PQ 2P
       9 |  1 ms |    12 ms |   56 MB |    0 MB |?  ⊤  ?   

The monitor names here are a bit (no, quite) cryptic to make them fit in the
terminal window, but we shall see that you can assign them a string of any
length.

This example was deliberately simple to illustrate the principle. In an
actual scenario, many things are likely to be different:

- Events will be received from a real running program, and have an
  arbitrarily complex structure containing many more parameters.
- These events may be written to an arbitrary named pipe instead of the
  standard output (or even read from a file).
- One may not be interested in looking at BeepBeep's output in the terminal,
  but rather pipe a trimmed down version of this output to another program
  for further processing.
- The properties to monitor will be much more complex. BeepBeep's
  [input language](#properties) is very expressive and can be used to
  represent intricate constraints between events. As an exemple, take a look
  at example file `PinQ.ltlfo`, whose contents represents the property:
  "no value appearing in parameter `p` may reappear in a subsequent event as
  the value of parameter `q`".

[Back to top](#toc)

Compiling and Installing BeepBeep                                {#install}
---------------------------------

A precompiled distribution of BeepBeep can be found on
[SourceForge](http://sourceforge.net/projects/beepbeep/); however this
distribution is not updated regularly. To obtain the latest version, it is
recommended that you download and compile the sources (the process is
simple).

First make sure you have the following installed:

- The Java Development Kit (JDK) to compile. BeepBeep was developed and
  tested on version 6 of the JDK, but it is probably safe to use any
  later version. Moreover, it most probably compiles on the JDK 5, although
  this was not tested.
- [Ant](http://ant.apache.org) to automate the compilation and build process

Download the sources for BeepBeep from
[SourceForge](http://sourceforge.net/projects/beepbeep/)
or clone the repository using Git:

    git clone git://beepbeep.git.sourceforge.net/gitroot/beepbeep/1.5

Compile the sources by simply typing:

    ant

This will produce a file called `BeepBeep.jar` in the `dist` subfolder.
This file is runnable and stand-alone, so it can be moved around to the
location of your choice.

[Back to top](#toc)

Events and Event Traces                                           {#traces}
-----------------------

The basic building block for BeepBeep is the *event*. An event is a piece of
data that is transmitted to the monitor at some moment in time. It can
represent various things depending on the context and has no predetermined
structure. For example, in the case of monitoring web server activity, one
could instruct the server to produce an event every time a page is requested
by a client. Similarly, when debugging a program under development, one
could insert into the code debugging instructions notifying of particular
function calls and arguments (similar to printing this information into a
console). This process of modifying a program to produce events to be sent
to a monitor is called *instrumentation*. It is up to the developer to
instrument the system to be monitored in a suitable way.

The data contained in an event is transmitted to BeepBeep as an XML string.
All parameters must be enclosed in a single element, by default named
`message` (this can be overridden through a [command-line parameter](#cli)).
This is the only constraint on events (up to a few other restrictions on the
XML syntax, see the code documentation for details); the number, name and
nesting of tags inside the `message` element is completely up to the user;
moreover, different events need not have the same structure at all. For
example, here is a possible event that would represent a user clicking on a
button in a GUI application:

    <message>
      <event-name>Click</event-name>
      <position>
        <x>34</x>
        <y>56</y>
      </position>
      <button>Left</button>
      <time>4909501</time>
      <selected-items>
        <id>4</id>
        <id>7</id>
        <id>12</id>
      </selected-items>
    </message>

Again, it is up to the person taking care of the instrumentation of the
system to be monitored to define a structure and a set of parameters that
is meaningful for that particular context.

[Back to top](#toc)

Passing Events to BeepBeep                                       {#piping}
--------------------------

There are three ways one can pass events to BeepBeep, which can be
specified using [command-line parameters](#cli). We shall show examples of
each mode; although these examples are written for Linux systems, the same
functionality can be achieved with other systems using a different syntax.
In all cases, BeepBeep expects its input to be encoded with UTF-8.

### By reading a file

The trace is completely recorded beforehand and written to some file.
BeepBeep reads the file and processes each event sequentially. Once the end
of the file is reached, the trace is considered finished and the monitor
exits.

A possible example of this scenario is:

    java -jar BeepBeep.jar -t Examples/trace-1.xml Examples/GreaterThan7.ltlfo

In this mode, BeepBeep rather acts as a "trace validator" or "log analyzer"
instead of a runtime monitor.

### By reading a pipe

In this scenario, one first creates some named pipe (e.g. using the `mkfifo`
command in Linux). The program to be monitored and BeepBeep are started
concurrently; the program writes events to the pipe as it executes, while
BeepBeep reads and fetches these events from the pipe at the same time.
Events are saved into a buffer on the monitor side; provided that this
buffer remains mostly empty, BeepBeep can then provide real-time feedback on
the state of the property to monitor.

A possible example of this scenario is:

    mkfifo /tmp/mypipe.fifo
    java -jar BeepBeep.jar -p /tmp/mypipe.fifo Examples/GreaterThan7.ltlfo

These two commands first create a named pipe and then start BeepBeep,
instructing it to wait for incoming events on that pipe. A simple way to
write events to the pipe is to use the `echo` command. In a *different*
command-line window, type:

    echo "<message><x>0</x></message>" > /tmp/mypipe.fifo

Observe that BeepBeep's message counter has incremented in the first
terminal, indicating the event was indeed processed.

BeepBeep buffers the characters it receives until it can make a complete
event out of it. You can see this by typing these two commands:

    echo "<message><x>0<" > /tmp/mypipe.fifo
    echo "/x></message>" > /tmp/mypipe.fifo

You shall notice that nothing happened in BeepBeep after the first command,
as no complete event could be formed from the received contents. An event,
however, could be completed after receiving the second string, and at this
point BeepBeep processed it and incremented its counter. (This string itself
could have contained the beginning of another event; this part of the
string would have been buffered in the same way.)

In the first scenario, the end of the file indicates the end of the trace.
However, in the present one, new events may always come; hence BeepBeep does
not exit when no new characters can be read from the pipe, and keeps polling
the pipe forever. To explicitly notify BeepBeep that no new events will come
(such as when the system to monitor has itself stopped), one must send the
End-Of-Transmission (EOT) character --that is, a single byte with value
0x04.

### By reading the standard input

This is just a special case of #2 where the output of some program is
redirected to BeepBeep's standard input, e.g.:

    java -jar TraceGenerator.jar | java -jar BeepBeep.jar Examples/GreaterThan7.ltlfo

As for the previous scenario, sending EOT will indicate the end of the
trace.

### By listening to some TCP connection on a given port

This feature allows one to monitor a system that does not even run on the
same computer as the monitor, and which sends its events through the
network. A possible way to test it is:

    java -jar BeepBeep.jar -k 55555 Examples/GreaterThan7.ltlfo

This starts BeepBeep, instructing it to open a socket and listen to TCP
port 55555 for incoming data. In a different command-line window, one can
then test sending data by running:

    nc localhost 55555

This will start the netcat command (in Linux systems). From now on,
everything that is typed in this window (followed by the Return key) will
be sent through a TCP connection on port 55555 to the destination host
(localhost in the present case). For example, try writing the following
event (follwed by a hit on the Return key), and observe BeepBeep's event
counter incrementing:

    <message><x>0</x></message>

If the monitor were residing on a different machine, one would replace
localhost by the IP address of that machine.

[Back to top](#toc)

Writing Properties on Event Traces                            {#properties}
----------------------------------

The properties to monitor in BeepBeep are expressed as formulas in an
extension of *Linear Temporal Logic* (LTL), called LTL-FO+. The semantics of
this logic is defined on a *trace* (i.e. a sequence) of XML events being
sent or received; the content of these events is accessible through
(simple) XPath expressions and can be stored into variables for future use.

### Accessing event contents

Data inside a event can be accessed using *first-order quantifiers*:

1. ∀x ∈ π means "for every x in π". Here, x is a variable, and π is a path
   expression used to fetch possible values for x. For example, if φ is a
   formula where x appears, then ∀x ∈ /tag1/tag2 : φ means: every value at
   the end of `/tag1/tag2` satisfies φ.

2. ∃x ∈ π means "some x in π". Hence, ∃x ∈ /tag1/tag2 : φ means: there
   exists some value at the end of `/tag1/tag2` which satisfies φ.

For example, consider the following XML message:

    <message>
      <action>login</action>
      <username>frank</username>
      <books-return>
        <book>123</book>
        <book>456</book>
        <book>789</book>
      </books-return>
      <books-borrow>
        <book>321</book>
        <book>456</book>
      </books-borrow>
    </message>

To express the fact that the `action` element of this event contains the
value `login`, one can write in LTL-FO+:

  ∃x ∈ /message/action : x = "login"

The previous formula says literally: in the current message, there exists a
value x at the end of the path `/message/action` such that x = "login".

LTL-FO+ provides all the familiar Boolean connectives: ∧ ("and"), ∨ ("or"),
¬ ("not"), → ("if then"), ⊕ ("xor"). They can be used to combine expressions
as usual. For example, to express the fact that the book with ID "444" is
not present in the current message, one can write:

  ∀x ∈ /message/books-return/book : (¬ (x = "444"))

This reads as: in the current message, every value x at the end of the path
`/message/books-return/book` is not equal to 444.

One can quantify multiple times to compare different values in the current
event. For example:

  ∀x ∈ /message/books-return/book : (∀y ∈ /message/books-borrow/book : ¬ (x = y))

In English: for every x at the end of `/message/books-return/book`, and
every y at the end of `/message/books-borrow/book`, we never have that x =
y; in other words, the returned books must be different from the borrowed
books. (This would be false in the previous message, as "456" appears in
both sides.)

### Sequences of events

Properties can then be defined on sequences of events using the following
LTL temporal operators (see also
[Wikipedia](http://en.wikipedia.org/wiki/Linear_Temporal_Logic)):

1. X means "in the next event". For example, if φ is a formula, X φ says
   that φ will be true in the next event.
2. G means "globally". For example, if φ is a formula, G φ says that φ is
   true in the current event, and will be true for all remaining events
   in the trace.
3. F means "eventually". Writing F φ says that φ is either true in the
   current event, or will become true for at least one event in the
   future.
4. U means "until". If φ and ψ are formulas, writing φ U ψ says that
   ψ will be true eventually, and in the meantime, φ is true for every
   event until ψ becomes true.

For example, in order to say that "a user must eventually log out", one can
write:

  F (∃x ∈ /message/action : x = "logout")

The previous formula says: eventually, some event in the trace will
fulfill the condition that some tag at the end of path `/action` will have
the value "logout".

To say that borrowed books and returned books are never the same, one can
take the formula shown previously, and enclose it within a G operator,
yielding:

  G (∀x ∈ /message/books-return/book : (∀y ∈ /message/books-borrow/book : ¬ (x = y)))

It is also possible for a variable to keep its value across be reused at a
later time in the trace. For example:

  G (∀x ∈ /message/books-borrow/book : (F (∀y ∈ /message/books-return/book : x = y)))

This formula says that globally, every book appearing in the section
`books-borrow` of some event must eventually reappear in some future
event in the `books-return` section. Here, the value of x is retrieved in
some message, and reused in a future event. This capability of freely
mixing temporal operators and quantifiers makes LTL-FO+ a very powerful
language to express constraints on sequences of XML events.

These operators can be nested or combined with traditional boolean
connectives to create compound statements.

### Path Expressions

Quantifiers fetch their values based on a path expression. This path
expression is written in a language called [XPath](http://en.wikipedia.org/wiki/XPath)
(or more precisely a subset of that language). Please refer to the code
documentation for details on the supported constructs.

It shall be noted in passing that quantified variables can reappear inside
path expressions of nested quantifiers. For example, one can write:

  ∀x ∈ /message/character/id : (∀y ∈ /message/character[id=$x]/action : (φ(x,y)))

where φ(x,y) is some expression involving x and y. On the following event:

    <message>
      <character>
        <id>0</id>
        <action>walk</action>
      </character>
      <character>
        <id>1</id>
        <action>wait</action>
      </character>
      <character>
        <id>2</id>
        <action>run</action>
      </character>
    </message>

the expression φ(x,y) will be evaluated only on the three pairs of values
(0,walk), (1,wait), (2,run) --i.e., the value of y depends on that already
chosen for x.

### Input File Format

BeepBeep reads the property to monitor from a file specified on the
[command line](#cli). The following syntax is accepted:

- Empty lines, or lines containing only space characters (tabs, etc.) are
  discarded
- Whitespace at the beginning of a line is discarded
- Lines beginning with # are comments and are discarded
- A formula may be fragmented across multiple lines; line breaks inside a
  formula are considered as spaces

The special characters used in the formulas (such as ∀, ∈, etc.) exist in
the UTF-8 character set and should be entered as such. Use a text editor
and a display font that supports UTF-8 to write them correctly. The C
operators (e.g. || for "or") are not recognized. For the sake of
convenience, the special characters are enumerated below so that they can
be copy-pasted into a file if needed:

- ∀ for all
- ∃ there exists
- ∈ in
- ∧ and
- ∨ or
- ¬ not
- → implies
- ⊕ xor

Beware: the ∧ ("and") is *not* the ^ ("hat") character.

Optionally, comment lines may contain *metadata declarations*, i.e.
key-value pairs that describe something about the property contained in the
file. The metadata must appear in the comment lines (those beginning with
a "#" symbol) and must be of the form:

    # @Param("some value");

where Param is some (user-defined) parameter name. Parameters may
span multiple lines, which still must each begin with the comment
symbol, as follows:

    # @Param("some value
    #     that spans multiple lines");

The "#" and extraneous spaces are removed on parsing. Currently BeepBeep
uses the parameter "Caption", if present, to display a name for each
monitor. Parameters @OnTrue and @OnFalse can be used to [trigger
actions](#triggers) when a property is fulfilled or violated.

[Back to top](#toc)

Triggering Actions                                              {#triggers}
------------------

While it is possible to simply read BeepBeep's output and react whenever a
property is fulfilled (its state switches from ? to ⊤) or violated (its
state switches from ? to ⊥), it is possible to instruct BeepBeep to run
commands by itself when this occurs. For this purpose, one uses two metadata
declarations:

- @OnTrue("..."): this will launch the command passed as an argument when
  the property becomes fulfilled
- @OnFalse("..."): this will launch the command passed as an argument when
  the property becomes violated

**Beware:** the path used in these values is relative the location of the
formula file.

### An example

In the following, we show how BeepBeep can be used to monitor the violation
of a property, and then trigger a script that pops up an alert window to the
user and saves a copy of the trace up to that point as proof of the
occurrence of the violation.

The first step is to write a property file and to define its @OnFalse
parameter. The file `TriggerExample.ltlfo` in the Examples folder is one
such file. Its @OnFalse parameter instructs BeepBeep to call the script
`ActionWhenFalse.sh`.

Then, we start BeepBeep, instructing it to take its events from named pipe
`/tmp/mypipe.fifo` and to mirror the received events back to the standard
output. We then trap the standard error and redirect it to a temporary file
called `/tmp/trace.xml`:

    java -jar BeepBeep.jar --mirror -p /tmp/mypipe.fifo \
      Examples/TriggerExample.ltlfo > /tmp/trace.xml

The script `ActionWhenFalse.sh`, written in Bash, contains the following
instructions:

    #! /bin/bash
    cp /tmp/trace.xml trace-XYZ.xml
    zenity --warning --text 'Property XYZ has just been violated! \
      A log has been saved in trace-XYZ.xml.'

The first line makes a copy of the event trace mirrored by BeepBeep up to
this moment into a new file. The second line uses
[Zenity](http://en.wikipedia.org/wiki/Zenity) to pop up an alert window.
To actually see the setup in action, it remains to feed events to the
running monitor, which can be done by launching the trace generator from a
different command-line window:

    java -jar TraceGenerator.jar > /tmp/mypipe.fifo

Obviously, the actual setup and commands to run are really up to the user's
imagination. You are encouraged to [contact the authors](#about) if you
think of inventive ways of using BeepBeep.

[Back to top](#toc)

Command-Line Options                                                 {#cli}
--------------------

Launch BeepBeep by typing:

    java -jar BeepBeep.jar <options> [file1 [file2 [file3] ...]]

The files file1, file2, etc. contain the properties to monitor. Wildcards
(e.g. `*.ltlfo`) are also accepted. Possible options:

`-p pipename`, `--pipe filename`
:  Fetch events by reading from a pipe called pipename

`-t filename`, `--trace filename`
:  Fetch events by reading the trace filename

`-k x`, `--socket x`
:  Fetch events by reading a socket on TCP port x

`--eventname x`
:  Set the name of the top-level tag for events to x (the default value is
   "message")

`--slowdown x`
:  Force BeepBeep to take a pause of x milliseconds after processing each
   event

`--verbosity x`
:  Set verbosity level to x (default: 1). If set to 0, BeepBeep will not
   display anything on the standard output (except if the `--stats` option
   is used)

`--stats`
:  At the end of the trace, make BeepBeep display statistics about its
   processing (CPU time, final verdict, memory use). If the `--verbosity`
   option is set to 0, only a comma-separated list of values is printed.

`--no-triggers`
:  Disable triggers that would be called (if defined) when a property
   becomes fulfilled or violated.

`--mirror`
:  Output events received back to the standard output

`--csv`
:  After every event processed, output statistics (CPU time, current
   verdict, buffer size, etc.) to the standard error in CSV format. The
   regular display is still printed on the standard output, so make sure to
   redirect stderr to avoid garbled text.

`--version`
:  Print version and build number

`--help`
:  Show the help text

If neither `-p`, `-k` nor `-t` is given, BeepBeep will fetch events from the
standard input.

[Back to top](#toc)

Using the Trace Generator                                      {#generator}
-------------------------

BeepBeep ships with a companion, stand-alone program called
`TraceGenerator.jar`, which can be used to simulate an event-producing
system (for example, to check that the message passing mechanism to the
monitor is correctly setup). The generator has been used in the [initial
example](#quickstart) to produce a regular stream of simple random events to
be sent to the monitor. However, it can also be run with the following
options:

    java -jar TraceGenerator.jar <options>

Possible options:

`-i x`, `--interval x`
:  Attempt to output events at the regular rate of one every x
   milliseconds. Defaults to 1000

`-e x`, `--events x`
:  Stop after outputting x events. Defaults to 1M.

`--generator name`
: Generates a sequence of events using a particular profile with given name.
  See below for an explanation of profiles.

`--parameters s`
: Some generators accept additional parameters that are passed through the
  string s

Possible generators:

- "Constant": generates the same constant event in a loop
- "FileFeeder": reads a trace from a file (whose name is passed in the
  `--parameters` option) and outputs the contents of that file one event at
  a time at the specified rate (useful for replaying a pre-recorded trace)
- "SimpleRandom": generates a trace of the form shown in the [initial
  example](#quickstart); this is the default if none if specified

[Back to top](#toc)

Some Applications                                           {#applications}
-----------------

Past versions of BeepBeep have been used, for example, for the runtime
monitoring of web service interactions. For more information:

- S. Hallé and R. Villemaire. (2012). Runtime Enforcement of Web Service
  Message Contracts with Data. In IEEE T. Services Computing, 5 (2),
  192-206. [Article](http://doi.ieeecomputersociety.org/10.1109/TSC.2011.10)

Stay tuned! More to come soon...

[Back to top](#toc)

About the Author                                                   {#about}
----------------

BeepBeep was developed by Sylvain Hallé, currently an Assistant Professor at
[Université du Québec à Chicoutimi, Canada](http://www.uqac.ca/) and head of
[LIF](http://lif.uqac.ca/), the Laboratory of Formal Computer Science
("Laboratoire d'informatique formelle"). For questions about BeepBeep,
please do not hesitate to contact the BeepBeep team at shalle@acm.org.

[Back to top](#toc)
