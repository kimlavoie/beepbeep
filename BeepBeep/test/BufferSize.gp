#set terminal pdf
set title "Buffer size"
set xlabel "Event no"
set ylabel "Size (B)"
set datafile separator ","
#set key below
unset key
set grid
#set logscale xy
plot "< sort -g data.csv" using 1:5 with lines
