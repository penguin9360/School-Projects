.PHONY: all clean palindrome_control register 
all:
	@echo Please use one of the make targets specified in the handout.


palindrome_circuit: palindrome_circuit_exe
	./$<

palindrome_circuit_exe: palindrome_circuit_tb.v palindrome_circuit.v register.v palindrome_lib.v
	iverilog -o $@ -Wall $^

palindrome_control: palindrome_control_exe
	./$<

palindrome_control_exe: palindrome_control_tb.v palindrome_control.v palindrome_circuit.v palindrome_lib.v register.v
	iverilog -o $@ -Wall $^

register: register_exe 
	./$<

register_exe: register.v register_tb.v 
	iverilog -o $@ -Wall $^

reg_writer_exe: reg_writer_tb.v reg_writer.v register.v palindrome_lib.v
	iverilog -o $@ -Wall $^

reg_writer: reg_writer_exe
	./$<
clean:
	rm -f *_exe *.vcd
