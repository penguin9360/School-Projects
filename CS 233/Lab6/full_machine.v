// full_machine: execute a series of MIPS instructions from an instruction cache
//
// except (output) - set to 1 when an unrecognized instruction is to be executed.
// clock   (input) - the clock signal
// reset   (input) - set to 1 to set all registers to zero, set to 0 for normal execution.

module full_machine(except, clock, reset);
    output      except;
    input       clock, reset;

    wire [31:0] inst, out, PC, PC_tmp2, PC_tmp1,nextPC, rsData, rtData, rdData, B, slt_o, lui_o, mem_o, byte_, data_out, rsd, addm_, addm_alu_out;
    wire alu_src2, rd_src, negative, zero, slt, lui, addm, byte_we, word_we, mem_read, overflow, byte_load, writeenable;

    wire [31:0] branch_offset = {{14{inst[15]}}, inst[15:0], 2'b0};
    wire [31:0] lui_tmp = {inst[15:0], zero_[15:0]};
    wire [31:0] slt_revised = {zero_[31:1], (negative & (~ overflow)) | ((~ negative) & overflow)};
    wire [31:0] imm32 = {{16{inst[15]}}, inst[15:0]};
    wire [31:0] zero_ = 32'b0;
    wire [4:0] rdNum;
    wire [2:0] alu_op;
    wire [1:0] control_type;
    wire [7:0] data_select;

    // DO NOT comment out or rename this module
    // or the test bench will break
    register #(32) PC_reg(PC, nextPC, clock, 1, reset);

    // DO NOT comment out or rename this module
    // or the test bench will break
    instruction_memory im(inst, PC[31:2]);

    // DO NOT comment out or rename this module
    // or the test bench will break
    regfile rf (rsData, rtData,
                inst[25:21], inst[20:16], rdNum, rdData,
                writeenable, clock, reset);

    /* add other modules */

    // revise the sequence - except to mem_read
    mips_decode full_machine_decoder(alu_op, writeenable, rd_src, alu_src2, except,
                     control_type, mem_read, word_we, byte_we, byte_load, lui,
                     slt, addm, inst[31:26], inst[5:0], zero);

    // using existing modules
    alu32 pc_temp2_alu(PC_tmp2, , , , PC_tmp1, branch_offset, `ALU_ADD);
    alu32 addm_alu(addm_alu_out, , , , byte_, rtData, `ALU_ADD);
    alu32 pc_temp1_alu(PC_tmp1, , , , PC, 32'h04, `ALU_ADD);
    alu32 main_(out, overflow, zero, negative, rsData, B, alu_op);

    // other muxes
    mux2v mux_B(B, rtData, imm32, alu_src2);
    mux2v #(5) mux_rdNum(rdNum, inst[15:11], inst[20:16], rd_src);
    mux2v mux_slt(slt_o, out, slt_revised, slt);
    mux2v mux_lui(lui_o, mem_o, lui_tmp, lui);
    mux2v mux_mem(mem_o, slt_o, byte_, mem_read);
    mux2v mux_bout(byte_, data_out, {zero_[23:0], data_select}, byte_load);
    mux2v mux_rsd(rsd, out, rsData, addm);
    mux2v mux_addm(rdData, lui_o, addm_alu_out, addm);

    // PC & data muxes
    mux4v mux_jmp(nextPC, PC_tmp1, PC_tmp2, {PC[31:28], inst[25:0], 2'b0},
                  rsData, control_type);
    mux4v #(8) mux_b4out(data_select, data_out[7:0], data_out[15:8],
                         data_out[23:16], data_out[31:24], out[1:0]);

    data_mem data(data_out, rsd, rtData, word_we, byte_we, clock, reset);

endmodule // full_machine
