#set terminal pdf
set title "Processing time per event"
set xlabel "Event no"
set ylabel "Time (ms)"
set datafile separator ","
#set key below
unset key
set grid
#set logscale xy
plot "< sort -g data.csv" using 1:4 with lines
