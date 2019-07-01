//implement your 32-bit ALU
module alu32(out, overflow, zero, negative, A, B, control);
    output [31:0] out;
    output        overflow, zero, negative;
    input  [31:0] A, B;
    input   [2:0] control;
    wire [32:0] wire_alu;
    assign wire_alu[0] = control[0];

    alu1 alu_0(out[0], wire_alu[1], A[0], B[0], wire_alu[0], control);
    alu1 alu_1(out[1], wire_alu[2], A[1], B[1], wire_alu[1], control);
    alu1 alu_2(out[2], wire_alu[3], A[2], B[2], wire_alu[2], control);
    alu1 alu_3(out[3], wire_alu[4], A[3], B[3], wire_alu[3], control);
    alu1 alu_4(out[4], wire_alu[5], A[4], B[4], wire_alu[4], control);
    alu1 alu_5(out[5], wire_alu[6], A[5], B[5], wire_alu[5], control);
    alu1 alu_6(out[6], wire_alu[7], A[6], B[6], wire_alu[6], control);
    alu1 alu_7(out[7], wire_alu[8], A[7], B[7], wire_alu[7], control);
    alu1 alu_8(out[8], wire_alu[9], A[8], B[8], wire_alu[8], control);
    alu1 alu_9(out[9], wire_alu[10], A[9], B[9], wire_alu[9], control);
    alu1 alu_10(out[10], wire_alu[11], A[10], B[10], wire_alu[10], control);
    alu1 alu_11(out[11], wire_alu[12], A[11], B[11], wire_alu[11], control);
    alu1 alu_12(out[12], wire_alu[13], A[12], B[12], wire_alu[12], control);
    alu1 alu_13(out[13], wire_alu[14], A[13], B[13], wire_alu[13], control);
    alu1 alu_14(out[14], wire_alu[15], A[14], B[14], wire_alu[14], control);
    alu1 alu_15(out[15], wire_alu[16], A[15], B[15], wire_alu[15], control);
    alu1 alu_16(out[16], wire_alu[17], A[16], B[16], wire_alu[16], control);
    alu1 alu_17(out[17], wire_alu[18], A[17], B[17], wire_alu[17], control);
    alu1 alu_18(out[18], wire_alu[19], A[18], B[18], wire_alu[18], control);
    alu1 alu_19(out[19], wire_alu[20], A[19], B[19], wire_alu[19], control);
    alu1 alu_20(out[20], wire_alu[21], A[20], B[20], wire_alu[20], control);
    alu1 alu_21(out[21], wire_alu[22], A[21], B[21], wire_alu[21], control);
    alu1 alu_22(out[22], wire_alu[23], A[22], B[22], wire_alu[22], control);
    alu1 alu_23(out[23], wire_alu[24], A[23], B[23], wire_alu[23], control);
    alu1 alu_24(out[24], wire_alu[25], A[24], B[24], wire_alu[24], control);
    alu1 alu_25(out[25], wire_alu[26], A[25], B[25], wire_alu[25], control);
    alu1 alu_26(out[26], wire_alu[27], A[26], B[26], wire_alu[26], control);
    alu1 alu_27(out[27], wire_alu[28], A[27], B[27], wire_alu[27], control);
    alu1 alu_28(out[28], wire_alu[29], A[28], B[28], wire_alu[28], control);
    alu1 alu_29(out[29], wire_alu[30], A[29], B[29], wire_alu[29], control);
    alu1 alu_30(out[30], wire_alu[31], A[30], B[30], wire_alu[30], control);
    alu1 alu_31(out[31], wire_alu[32], A[31], B[31], wire_alu[31], control);

    xor final(overflow, wire_alu[32], wire_alu[31]);
    assign negative = out[31]==1;
    assign zero = out[31:0]==32'b0;

endmodule 
