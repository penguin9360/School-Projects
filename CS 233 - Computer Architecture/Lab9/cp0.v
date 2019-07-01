`define STATUS_REGISTER 5'd12
`define CAUSE_REGISTER  5'd13
`define EPC_REGISTER    5'd14

module cp0(rd_data, EPC, TakenInterrupt,
           wr_data, regnum, next_pc,
           MTC0, ERET, TimerInterrupt, clock, reset);
    output [31:0] rd_data;
    output [29:0] EPC;
    output        TakenInterrupt;
    input  [31:0] wr_data;
    input   [4:0] regnum;
    input  [29:0] next_pc;
    input         MTC0, ERET, TimerInterrupt, clock, reset;

    // your Verilog for coprocessor 0 goes here

    wire   [31:0] user_status, decoder_mtc0;
    wire   [29:0] EPC_IN;
    wire	 EXCEPTION_LEVEL;
    wire   [31:0] cause_register, status_register;
    wire   RST_OR_ERET;

    assign RST_OR_ERET = reset | ERET;
    assign cause_register = {16'b0, TimerInterrupt, 15'b0};
    assign status_register = {16'b0, user_status[15:8], 6'b0, EXCEPTION_LEVEL, user_status[0]};
    assign TakenInterrupt = (cause_register[15] & status_register[15]) & ((~ status_register[1]) & status_register[0]);

    decoder32 mtc0_decoder(decoder_mtc0, regnum, MTC0);

    register reg_usr_status(user_status, wr_data, clock, decoder_mtc0[12], reset);
    register #(30,) reg_EPC(EPC, EPC_IN, clock, decoder_mtc0[14] | TakenInterrupt, reset);

    dffe reg_EX_Level(EXCEPTION_LEVEL, 1'd1, clock, TakenInterrupt, RST_OR_ERET);

    mux2v #(30) mux_epc(EPC_IN, wr_data[31:2], next_pc, TakenInterrupt);
    mux32v mux_rdData(rd_data, 0,0,0,0,0,0,0,0,0,0,0,0, status_register, cause_register, {EPC, 2'b0}, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, regnum);

endmodule
