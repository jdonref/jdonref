CC=gcc
EXEC=levenstein
CFLAGS=-I/usr/local/src/postgresql-9.0.7/src/include/ -I/usr/local/src/postgresql-9.0.7/src/include/utils/
LDFLAGS=-shared

all: levenstein.so

levenstein.so : levenstein.c
	$(CC) $(CFLAGS) $(LDFLAGS) -o levenstein.so levenstein.c

clean:

delete: clean
	rm levenstein.so
