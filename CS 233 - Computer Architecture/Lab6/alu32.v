`define ALU_ADD    3'h2
`define ALU_SUB    3'h3
`define ALU_AND    3'h4
`define ALU_OR     3'h5
`define ALU_NOR    3'h6
`define ALU_XOR    3'h7

////
//// mips_ALU: Performs all arithmetic and logical operations
////
//// out (output) - Final result
//// inA (input)  - Operand modified by the operation
//// inB (input)  - Operand used (in arithmetic ops) to modify inA
//// control (input) - Selects which operation is to be performed
////
module alu32(out, overflow, zero, negative, inA, inB, control);
    output [31:0] out;
    output        overflow, zero, negative;
    input  [31:0] inA, inB;
    input   [2:0] control;

    wire   [32:0] inAE = { inA[31], inA };
    wire   [32:0] inBE = { inB[31], inB };
    wire   [32:0] outE = (({33{(control == `ALU_AND)}} &  (inAE & inBE)) |
                          ({33{(control == `ALU_OR)}}  &  (inAE | inBE)) |
                          ({33{(control == `ALU_XOR)}} &  (inAE ^ inBE)) |
                          ({33{(control == `ALU_NOR)}} & ~(inAE | inBE)) |
                          ({33{(control == `ALU_ADD)}} &  (inAE + inBE)) |
                          ({33{(control == `ALU_SUB)}} &  (inAE - inBE)));

    assign out = outE[31:0];
    assign zero = (out[31:18] == 14'b0) & (out[17:1] == 17'd0) & ~out[0];
    xor  x1(negative, out[31], 1'b0);
    xor  x2(overflow, out[31], outE[32]);
   
endmodule

   
