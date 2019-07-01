// arith_machine: execute a series of arithmetic instructions from an instruction cache
//
// except (output) - set to 1 when an unrecognized instruction is to be executed.
// clock  (input)  - the clock signal
// reset  (input)  - set to 1 to set all registers to zero, set to 0 for normal execution.

module arith_machine(except, clock, reset);
    output      except;
    input       clock, reset;

    wire [31:0] inst, PC, nextPC, rsData, rtData, B, imm32, rdData;
    wire [4:0] rdNum;
    wire rd_src, rdWriteEnable, alu_src2;
    wire [2:0] alu_op;

    wire enable = 1'b1;


    assign imm32 = {{16{inst[15]}}, inst[15:0]};

    // DO NOT comment out or rename this module
    // or the test bench will break
    register #(32) PC_reg(PC, nextPC, clock, enable, reset);

    // DO NOT comment out or rename this module
    // or the test bench will break
    instruction_memory im(inst, PC[31:2]);

    // DO NOT comment out or rename this module
    // or the test bench will break
    regfile rf (rsData, rtData, inst[25:21], inst[20:16], rdNum, rdData, rdWriteEnable, clock, reset);

    /* add other modules */
    mips_decode decoder(alu_op, rd_src, alu_src2, rdWriteEnable, except, inst[31:26], inst[5:0]);
    mux2v mux_rf(B, rtData, imm32, alu_src2);
    mux2v #(5) mux_im(rdNum, inst[15:11], inst[20:16], rd_src);
    alu32 alu_pc(nextPC, , , , PC, 32'h04, 3'b010);
    alu32 alu_rf(rdData, , , , rsData, B, alu_op);







endmodule // arith_machine
