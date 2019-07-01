module pipelined_machine(clk, reset);
    input        clk, reset;

    wire [31:0]  PC;
    wire [31:2]  next_PC, PC_plus4, PC_target;
    wire [31:0]  inst;

    wire [31:0]  imm = {{ 16{inst[15]} }, inst[15:0] };  // sign-extended immediate
    wire [4:0]   rs = inst[25:21];
    wire [4:0]   rt = inst[20:16];
    wire [4:0]   rd = inst[15:11];
    wire [5:0]   opcode = inst[31:26];
    wire [5:0]   funct = inst[5:0];

    wire [4:0]   wr_regnum;
    wire [2:0]   ALUOp;

    wire         RegWrite, BEQ, ALUSrc, MemRead, MemWrite, MemToReg, RegDst;
    wire         PCSrc, zero;
    wire [31:0]  rd1_data, rd2_data, B_data, alu_out_data, load_data, wr_data;

    // new wires
    wire [31:0]  new_inst, alu_out_data_new, rd2_data_new, rd1_data_Forward, rd2_data_Forward;
    wire [31:2] PC_plus4_new;
    wire [4:0] wr_regnum_new;
    wire [2:0] ALUOp_new;
    wire MemToReg_new, MemRead_new, MemWrite_new, RegWrite_new;

    wire ForwardA = (rs == wr_regnum) & (rs != 0) & RegWrite;
    wire ForwardB = (rt == wr_regnum) & (rt != 0)  & RegWrite;
    wire Stall =  (rt == wr_regnum | rs == wr_regnum) & (wr_regnum != 0) & MemRead;

    // forwarding muxes
    mux2v #(32) ForwardA_mux(rd1_data_Forward, rd1_data, alu_out_data, ForwardA);
    mux2v #(32) ForwardB_mux(rd2_data_Forward, rd2_data, alu_out_data, ForwardB);


    // DO NOT comment out or rename this module
    // or the test bench will break
    register #(30, 30'h100000) PC_reg(PC[31:2], next_PC[31:2], clk, /* enable */~Stall, reset);

    assign PC[1:0] = 2'b0;  // bottom bits hard coded to 00
    adder30 next_PC_adder(PC_plus4, PC[31:2], 30'h1);
    adder30 target_PC_adder(PC_target, PC_plus4_new, imm[29:0]);
    mux2v #(30) branch_mux(next_PC, PC_plus4, PC_target, PCSrc);
    assign PCSrc = BEQ & zero;

    // DO NOT comment out or rename this module
    // or the test bench will break
    instruction_memory imem(new_inst, PC[31:2]);

    mips_decode decode(ALUOp, RegWrite_new, BEQ, ALUSrc, MemRead_new, MemWrite_new, MemToReg_new, RegDst,
                      opcode, funct);

    // DO NOT comment out or rename this module
    // or the test bench will break
    regfile rf (rd1_data, rd2_data,
               rs, rt, wr_regnum, wr_data,
               RegWrite, clk, reset);

    mux2v #(32) imm_mux(B_data, rd2_data_Forward, imm, ALUSrc);
    alu32 alu(alu_out_data_new, zero, ALUOp, rd1_data_Forward, B_data);

    // DO NOT comment out or rename this module
    // or the test bench will break

    data_mem data_memory(load_data, alu_out_data, rd2_data_new, MemRead, MemWrite, clk, reset);

    mux2v #(32) wb_mux(wr_data, alu_out_data, load_data, MemToReg);
    mux2v #(5) rd_mux(wr_regnum_new, rt, rd, RegDst);

    // pipeline registers
    register #(32, ) pipe_inst(inst, new_inst, clk, ~Stall, reset | PCSrc);
    register #(32, ) pipe_alu_out(alu_out_data, alu_out_data_new, clk, 1'b1, reset | Stall);
    register #(32, ) pipe_rd2_data(rd2_data_new, rd2_data_Forward, clk, 1'b1, reset | Stall);
    register #(30, ) pipe_PC_plus4(PC_plus4_new, PC_plus4, clk, ~Stall, reset | PCSrc);
    register #(5, ) pipe_wr_regnum(wr_regnum, wr_regnum_new, clk, 1'b1, reset | Stall);
    register #(1, ) pipe_RegWrite(RegWrite, RegWrite_new, clk, 1'b1, reset | Stall);
    register #(1, ) pipe_MemRead(MemRead, MemRead_new, clk, 1'b1, reset | Stall);
    register #(1, ) pipe_MemWrite(MemWrite, MemWrite_new, clk, 1'b1, reset | Stall);
    register #(1, ) pipe_MemToReg(MemToReg, MemToReg_new, clk, 1'b1, reset | Stall);

endmodule // pipelined_machine
