// mips_decode: a decoder for MIPS arithmetic instructions
//
// alu_op      (output) - control signal to be sent to the ALU
// rd_src      (output) - should the destination register be rd (0) or rt (1)
// alu_src2    (output) - should the 2nd ALU source be a register (0) or an immediate (1)
// writeenable (output) - should a new value be captured by the register file
// except      (output) - set to 1 when the opcode/funct combination is unrecognized
// opcode      (input)  - the opcode field from the instruction
// funct       (input)  - the function field from the instruction
//

`define ALU_ADD    3'h2
`define ALU_SUB    3'h3
`define ALU_AND    3'h4
`define ALU_OR     3'h5
`define ALU_NOR    3'h6
`define ALU_XOR    3'h7

module mips_decode(alu_op, rd_src, alu_src2, writeenable, except, opcode, funct);

    output [2:0] alu_op;
    output       rd_src, alu_src2, writeenable, except;
    input  [5:0] opcode, funct;

    // note from lab:
    // use inputs to represent output
    // use the new representation
    // check the alu_op assignment

    module mips_decode(alu_op, rd_src, alu_src2, writeenable, except, opcode, funct);
    output [2:0] alu_op;
    output       rd_src, alu_src2, writeenable, except;
    input  [5:0] opcode, funct;
    wire temp = opcode == `OP_OTHER0;
    assign alu_op = (temp&& (funct == `OP0_ADD)) ? `ALU_ADD:
                    (temp&& (funct == `OP0_SUB)) ? `ALU_SUB:
                    (temp&& (funct == `OP0_AND)) ? `ALU_AND:
                    (temp&& (funct == `OP0_OR)) ? `ALU_OR:
                    (temp&& (funct == `OP0_NOR)) ? `ALU_NOR:
                    (temp&& (funct == `OP0_XOR))? `ALU_XOR:
                    (opcode==`OP_ADDI) ? `ALU_ADD:
                    (opcode==`OP_ANDI) ? `ALU_AND:
                    (opcode==`OP_ORI) ? `ALU_OR:
                    (opcode==`OP_XORI) ? `ALU_XOR:
                    3'b000;
    assign except = (alu_op == 3'b000);
    assign alu_src2 = ~(opcode==`OP_OTHER0);
    assign rd_src = ~(opcode==`OP_OTHER0);
    assign writeenable = (except == 0);

endmodule // mips_decode
