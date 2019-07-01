`define ALU_ADD    3'h0
`define ALU_SUB    3'h1

// palindrome datapath
module palindrome_circuit(front_ge_back, a_ne_b, load, select, base, ending, clk, reset);
	output front_ge_back, a_ne_b;
	input [4:0] base, ending;
	input load, select, clk, reset;

	reg [4:0]  front, back;

	always@(reset)
     	   if(reset == 1'b1)
	   begin 
	      front <= 0;
	      back <= 0;
           end

        always@(posedge clk)
        begin
          if((reset == 1'b0) && (load == 1'b1) && (select == 1'b0))
	  begin
	    front <= base;
  	    back <= ending;
          end
	else if ((reset == 1'b0) && (load == 1'b1) && (select == 1'b1))
	  begin
	    front <= front + 1;
            back <= back - 1;
	  end
        end

	wire front_lt_back;
	comparator #(5) compareCounters(front_lt_back, , front, back);
	assign front_ge_back = ~front_lt_back;

	wire [31:0] a, b;
	regfile rf(a, b, front, back, , , , clk, reset);

	comparator compareValues(, a_ne_b, a, b);

endmodule
