module alu1_test;
    // exhaustively test your 1-bit ALU implementation by adapting mux4_tb.v
    reg A = 0;
    always #1 A = !A;
    reg B = 0;
    always #2 B = !B;
    reg cin = 0;
    always #4 cin = !cin;
    reg[2:0] control = 0;
    output out, cout;

    initial begin
	$dumpfile("alu1.vcd");
	$dumpvars(0, alu1_test);

	      # 8 control = 1;
        # 8 control = 2;
        # 8 control = 3;
        # 8 control = 4;
        # 8 control = 5;
        # 8 control = 6;
        # 8 control = 7;
        $finish;

    end

    alu1 alu_test(out, cout, A, B, cin, control);

    initial begin
	   $monitor("out:%d cout:%d A:%d B:%d cin:%d time:%d \n", out, cout, A, B, cin, $time);
    end

endmodule
