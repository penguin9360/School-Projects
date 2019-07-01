# Disable make's irritating builtin rules
MAKEFLAGS += --no-builtin-rules
.SUFFIXES:


# EWS has issues with Clang 3.9 and the system linker, and clang gives better
# performance to the scalar code for some reason, so stick with g++ for now.
CPP = g++
CPPFLAGS = -Wall -g -O2 -msse -mfpmath=sse

CC = clang
CFLAGS = -Wall -g -O2 -std=c99 -fno-unroll-loops -ffast-math -fno-inline
VECTOR_FLAGS = -Rpass="loop-.*" -Rpass-missed=loop-vectorize -Rpass-analysis=loop-vectorize -Rpass-analysis=loop-vectorize
NO_VECTOR_FLAGS = -fno-vectorize

.PHONY: all
all:
	@echo Please run one of the following targets:
	@echo mv-mult, mandelbrot,
	@echo t1, t1-scalar, t1-vector,
	@echo t2, t2-scalar, t2-vector,
	@echo t3, t3-scalar, t3-vector,
	@echo t4, t4-scalar, t4-vector,
	@echo t5, t5-scalar, t5-vector,
	@echo t6, t6-scalar, t6-vector,
	@echo clean

mv-mult-main.o: mv-mult-main.cpp mv-mult.h
	$(CPP) $(CPPFLAGS) -c mv-mult-main.cpp

mv-mult.o: mv-mult.cpp mv-mult.h
	$(CPP) $(CPPFLAGS) -c mv-mult.cpp

mv-mult: mv-mult-main.o mv-mult.o
	$(CPP) $(CPPFLAGS) mv-mult-main.o mv-mult.o -o mv-mult

mandelbrot-main.o: mandelbrot-main.cpp mandelbrot.h bmp.h
	$(CPP) $(CPPFLAGS) -c mandelbrot-main.cpp

mandelbrot.o: mandelbrot.cpp mandelbrot.h
	$(CPP) $(CPPFLAGS) -c mandelbrot.cpp

bmp.o: bmp.cpp bmp.h
	$(CPP) $(CPPFLAGS) -c bmp.cpp

mandelbrot: mandelbrot-main.o mandelbrot.o bmp.o
	$(CPP) $(CPPFLAGS) mandelbrot-main.o mandelbrot.o bmp.o -o mandelbrot


# Check clang version against a minimum, for consistency.
MIN_CLANG := 3.9
CLANG_EXISTS := $(shell $(CC) -v > /dev/null 2>&1 && echo OK)
CLANG_VERSION := $(shell $(CC) -v 2>&1 | grep version | sed "s/.*version \([0-9]*\.[0-9]*\).*/\1/")

.PHONY: clang-check
clang-check:
ifndef CLANG_EXISTS
	@echo "Clang doesn't seem to be installed."
	@false
else ifndef CLANG_VERSION
	@echo "Unable to determine Clang version."
	@false
else ifneq ($(shell awk 'BEGIN { print ($(CLANG_VERSION) >= $(MIN_CLANG)) }'), 1)
	@echo "Your clang version is $(CLANG_VERSION), but the minimum required is $(MIN_CLANG). You may have forgotten to load the llvm/cs225 module."
	@false
else
	@true
endif

define SCLVEC
.PHONY: t$(1)
t$(1): t$(1)-scalar t$(1)-vector
	./t$(1)-scalar
	./t$(1)-vector

t$(1)-scalar: t$(1)-main.c clang-check
	$(CC) $(CFLAGS) $(NO_VECTOR_FLAGS) -DSCALAR t$(1)-main.c -o t$(1)-scalar

t$(1)-main.o: t$(1)-main.c clang-check
	$(CC) $(CFLAGS) $(NO_VECTOR_FLAGS) -c t$(1)-main.c

t$(1)-vector.o: t$(1).c clang-check
	$(CC) $(CFLAGS) $(VECTOR_FLAGS) -c t$(1).c -o t$(1)-vector.o

t$(1)-vector: t$(1)-main.o t$(1)-vector.o clang-check
	$(CC) $(CFLAGS) t$(1)-main.o t$(1)-vector.o -o t$(1)-vector
endef

$(foreach i,1 2 3 4 5 6,$(eval $(call SCLVEC,$(i))))

.PHONY: clean
clean:
	-rm -f mv-mult mandelbrot t*-scalar t*-vector *.o *.exe mandelbrot-*.bmp
