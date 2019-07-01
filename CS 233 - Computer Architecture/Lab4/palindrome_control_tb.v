module palindrome_control_test;
    reg       clock = 0;
    always #1 clock = !clock;

    reg go = 0;
    reg reset = 1;
    reg[4:0] base, ending;

    integer i;

    wire front_ge_back, a_ne_b; // palindrome_circuit's output
    wire palindrome, done, select, load; // palindrome_control's output
    palindrome_circuit circuit(front_ge_back, a_ne_b, load, select, base, ending, clock, reset);
    palindrome_control control(palindrome, done, select, load, go, a_ne_b, front_ge_back, clock, reset);

    initial begin
        $dumpfile("palindrome_control.vcd");
        $dumpvars(0, palindrome_control_test);
        #2      reset = 0;       // changed from #1 to #2 so that inputs don't transition with clock
        
	// First, lets give an initial value for all
	// registers equal to their 'index' in the register file
	for ( i = 0; i < 32; i = i + 1) 
		circuit.rf.r[i] <= i;
	

	// Test an even length palindrome
	circuit.rf.r[11] <= 32'h12344321;
	circuit.rf.r[12] <= 32'h00000000;
	circuit.rf.r[13] <= 32'h00000000;
	circuit.rf.r[14] <= 32'h12344321;
	# 2 base = 11; ending= 14; go = 1;
	# 20 go = 0;


	// Test an odd length palindrome
	circuit.rf.r[2] <=  32'hCAFEBABE;
	circuit.rf.r[3] <=  32'hFFFFFFFF;
	circuit.rf.r[4] <=  32'h0B3D1E55;
	circuit.rf.r[5] <=  32'hFFFFFFFF;
	circuit.rf.r[6] <=  32'hCAFEBABE;
	# 10 base = 2; ending= 6; go = 1;
	# 20 go = 0;

	// A failing test
	circuit.rf.r[7]  <= 32'h33333333;
	circuit.rf.r[8]  <= 32'hC001D0D3;
	circuit.rf.r[9]  <= 32'hFFFFFFFF;
	circuit.rf.r[10] <= 32'hBAB3D0D3;
	circuit.rf.r[11] <= 32'h33333333;
	# 10  base = 7; ending= 11; go = 1;
	# 20 go = 0;
        // Add your own testcases here!

        #10 $finish;
    end

endmodule
