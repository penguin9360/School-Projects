// 00 -> AND, 01 -> OR, 10 -> NOR, 11 -> XOR
module logicunit(out, A, B, control);
    output      out;
    input       A, B;
    input [1:0] control;
    wire a, o, n, x;

    and a1(a, A, B);
    or o1(o, A, B);
    nor n1(n, A, B);
    xor x1(x, A, B);

    mux4 mux_logicunit(out, a, o, n, x, control);

endmodule // logicunit
