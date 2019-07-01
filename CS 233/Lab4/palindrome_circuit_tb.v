module palindrome_circuit_test;
    reg       clock = 0;
    always #1 clock = !clock;


    reg [4:0] base, ending;
    reg sel = 0, ld = 0, reset = 1;
    
    integer i;
	
    wire front_ge_back, a_ne_b;
    palindrome_circuit circuit(front_ge_back, a_ne_b, ld, sel, base, ending, clock, reset);

    initial begin
        $dumpfile("palindrome_circuit.vcd");
        $dumpvars(0, palindrome_circuit_test);
        #4 reset = 0;

	// First, lets give an initial value for all
	// registers equal to their 'index' in the register file
	
	for ( i = 0; i < 32; i = i + 1) 
		circuit.rf.r[i] <= i;

	// Test an even length palindrome
	circuit.rf.r[11] <= 32'h12344321;
	circuit.rf.r[12] <= 32'h00000000;
	circuit.rf.r[13] <= 32'h00000000;
	circuit.rf.r[14] <= 32'h12344321;
	# 2 base = 11; ending = 14; ld = 1; sel = 0;
	# 10 sel = 1; 


	// Test an odd length palindrome
	circuit.rf.r[2] <=  32'hCAFEBABE;
	circuit.rf.r[3] <=  32'hFFFFFFFF;
	circuit.rf.r[4] <=  32'h0B3D1E55;
	circuit.rf.r[5] <=  32'hFFFFFFFF;
	circuit.rf.r[6] <=  32'hCAFEBABE;
	# 2 base = 2; ending= 6; ld = 1; sel = 0;
	# 10 sel = 1;

	// A failing test
	circuit.rf.r[7]  <= 32'h33333333;
	circuit.rf.r[8]  <= 32'hC001D0D3;
	circuit.rf.r[9]  <= 32'hFFFFFFFF;
	circuit.rf.r[10] <= 32'hBAB3D0D3;
	circuit.rf.r[11] <= 32'h33333333;
	#2 base = 7; ending= 11; ld = 1; sel = 0;
	# 10 sel = 1;
        // Add your own testcases here!


        #10 $finish;
    end

    endmodule
