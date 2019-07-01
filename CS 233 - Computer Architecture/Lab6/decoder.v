// mips_decode: a decoder for MIPS arithmetic instructions
//
// alu_op       (output) - control signal to be sent to the ALU
// writeenable  (output) - should a new value be captured by the register file
// rd_src       (output) - should the destination register be rd (0) or rt (1)
// alu_src2     (output) - should the 2nd ALU source be a register (0) or an immediate (1)
// except       (output) - set to 1 when we don't recognize an opdcode & funct combination
// control_type (output) - 00 = fallthrough, 01 = branch_target, 10 = jump_target, 11 = jump_register
// mem_read     (output) - the register value written is coming from the memory
// word_we      (output) - we're writing a word's worth of data
// byte_we      (output) - we're only writing a byte's worth of data
// byte_load    (output) - we're doing a byte load
// lui          (output) - the instruction is a lui
// slt          (output) - the instruction is an slt
// addm         (output) - the instruction is an addm
// opcode        (input) - the opcode field from the instruction
// funct         (input) - the function field from the instruction
// zero          (input) - from the ALU


module mips_decode(alu_op, writeenable, rd_src, alu_src2, except, control_type,
                   mem_read, word_we, byte_we, byte_load, lui, slt, addm,
                   opcode, funct, zero);
    output [2:0] alu_op;
    output       writeenable, rd_src, alu_src2, except;
    output [1:0] control_type;
    output       mem_read, word_we, byte_we, byte_load, lui, slt, addm;
    input  [5:0] opcode, funct;
    input        zero;

    //normal
    wire bne = opcode == `OP_BNE;
    wire beq = opcode == `OP_BEQ;
    wire j = opcode == `OP_J;
    wire lw = opcode == `OP_LW;
    wire lbu = opcode == `OP_LBU;
    wire sw = opcode == `OP_SW;
    wire sb = opcode == `OP_SB;
    wire lui = opcode == `OP_LUI;

    //`OP_OTHER
    wire jr = (opcode == `OP_OTHER0) & (funct == `OP0_JR);
    wire slt = (opcode == `OP_OTHER0) & (funct == `OP0_SLT);
    wire addm = (opcode == `OP_OTHER0) & (funct == `OP0_ADDM);

    //normal
    wire addi_ = (opcode == 6'h08);
    wire xori_ = opcode == 6'h0e;
    wire andi_ = opcode == 6'h0c;
    wire ori_ = (opcode == 6'h0d);

    //`OP_OTHER0
    wire and_ = (opcode == `OP_OTHER0) & (funct == 6'h24);
    wire or_ = (opcode == `OP_OTHER0) & (funct == 6'h25);
    wire xor_ = (opcode == `OP_OTHER0) & (funct == 6'h26);
    wire nor_ =  (opcode == `OP_OTHER0) & (funct == 6'h27);
    wire add_ = (opcode == `OP_OTHER0) & (funct == 6'h20);
    wire sub_ = (opcode == `OP_OTHER0) & (funct == 6'h22);

    assign control_type[0] = (beq & zero) | jr | (bne & ~zero);
    assign control_type[1] = jr | j;

    assign alu_op[2] = and_  | andi_  | or_  | nor_ | xor_ | ori_ | xori_;
    assign alu_op[1] = slt | lw | lbu | sw | sb | beq | bne | addm | add_ | sub_ | nor_ | xor_ | addi_ | xori_;
    assign alu_op[0] = slt | beq | bne| sub_ | or_  | xor_ | ori_ | xori_;



    assign rd_src = (lui | lw | lbu | addi_ | andi_ | ori_ | xori_);
    assign writeenable =  lui | slt | lw | lbu | addm | add_ | addi_ | sub_ | and_ | andi_ | or_ | ori_ |
                          xor_ | xori_ | nor_;

    assign except = ~(bne | beq | j | lw | lbu | sw | sb | lui |
                      jr | slt | addm |
                      addi_ | xori_ | andi_ | ori_ |
                      add_ | sub_ | and_ | or_ | xor_ | nor_);

    assign alu_src2 = (lw | lbu | sw | sb | addi_ | andi_ | ori_ | xori_);

    assign byte_we = sb;
    assign byte_load = lbu;
    assign mem_read = addm | lw | lbu;
    assign word_we = sw;

endmodule // mips_decode
