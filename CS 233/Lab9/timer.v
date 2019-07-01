`define ALU_ADD    3'h0

module timer(TimerInterrupt, cycle, TimerAddress,
             data, address, MemRead, MemWrite, clock, reset);
    output        TimerInterrupt;
    output [31:0] cycle;
    output        TimerAddress;
    input  [31:0] data, address;
    input         MemRead, MemWrite, clock, reset;

    wire [31:0] q_cycle, d_cycle, q_inter;
    wire Write_time, Read_time, acknowledge;

    assign Write_time = (32'hffff001c == address) & MemWrite;
    assign Read_time = (32'hffff001c == address) & MemRead;
    assign acknowledge = (32'hffff006c == address) & MemWrite;
    assign TimerAddress = (32'hffff001c == address) | (32'hffff006c == address);

    wire w1 = q_cycle == q_inter;
    wire w2 = reset | acknowledge;

    register counter1(q_cycle, d_cycle, clock, 1'd1, reset);
    alu32 add1(d_cycle, , , `ALU_ADD, q_cycle, 32'd1);
    register #(, 32'hffffffff) interrupt_cycle(q_inter, data, clock, Write_time, reset);
    dffe  interupt_line(TimerInterrupt, 1'd1, clock, w1, w2);
    tristate trid(cycle, q_cycle, Read_time);

endmodule
