module full_adder(sum, cout, a, b, cin);
    output sum, cout;
    input  a, b, cin;
    wire   partial_s, partial_c1, partial_c2;

    xor x0(partial_s, a, b);
    xor x1(sum, partial_s, cin);
    and a0(partial_c1, a, b);
    and a1(partial_c2, partial_s, cin);
    or  o1(cout, partial_c1, partial_c2);
endmodule // full_adder

`define ALU_ADD    3'h2
`define ALU_SUB    3'h3
`define ALU_AND    3'h4
`define ALU_OR     3'h5
`define ALU_NOR    3'h6
`define ALU_XOR    3'h7

// 01x -> arithmetic, 1xx -> logic
module alu1(out, cout, A, B, cin, control);
    output      out, cout;
    input       A, B, cin;
    input [2:0] control;
    wire w1, w2, w3;

    // add code here!!!
    xor x1(w3, B, control[0]);
    full_adder adder1(w1, cout, A, w3, cin);
    logicunit lu1(w2, A, B, control[1:0]);
    mux2 mux_alu(out, w1, w2, control[2]);
endmodule // a
