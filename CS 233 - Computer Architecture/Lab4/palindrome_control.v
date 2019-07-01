
module palindrome_control(palindrome, done, select, load, go, a_ne_b, front_ge_back, clock, reset);
	output load, select, palindrome, done;
	input go, a_ne_b, front_ge_back;
	input clock, reset;


	wire check_Palindrome, is_Palindrome, not_Palindrome, garbage, start;


	// next state logic
	wire check_Palindrome_next = (~ reset) & (start & (~ go)) | (check_Palindrome & (~ front_ge_back) & (~ a_ne_b));

	wire is_Palindrome_next = (~ reset) & (check_Palindrome & front_ge_back & (~ a_ne_b)) | (is_Palindrome & (~ go));

	wire not_Palindrome_next = (~ reset) & (check_Palindrome & (~ front_ge_back) & a_ne_b) | (not_Palindrome & (~ go));

	wire start_next = (~ reset) & ((garbage & go) | (start & go) | (is_Palindrome & go) | (not_Palindrome & go));

	wire garbage_next = (garbage & (~ go)) | reset;


	//DFFE
	dffe check_(check_Palindrome, check_Palindrome_next, clock, 1'b1, 1'b0);

	dffe is_(is_Palindrome, is_Palindrome_next, clock, 1'b1, 1'b0);

	dffe not_(not_Palindrome, not_Palindrome_next, clock, 1'b1, 1'b0);

	dffe start_(start, start_next, clock, 1'b1, 1'b0);

	dffe Garbage_(garbage, garbage_next, clock, 1'b1, 1'b0);


	//out
	assign palindrome = is_Palindrome ? 1 : 0;

	assign select = check_Palindrome ? 1 : 0;

	assign load = start | check_Palindrome ? 1 : 0 ;

	assign done = not_Palindrome | is_Palindrome ? 1 : 0;


endmodule // palindrome_control
