module blackbox_test;

    reg x, l, b;                           // these are inputs to "circuit under test"
                                          // use "reg" not "wire" so can assign a value
    wire w;                        // wires for the outputs of "circuit under test"

    blackbox box(w, x, l, b);  // the circuit under test

    initial begin                             // initial = run at beginning of simulation
                                          // begin/end = associate block with initial

        $dumpfile("blackbox_test.vcd");                  // name of dump file to create
        $dumpvars(0,blackbox_test);                 // record all signals of module "sc_test" and sub-modules
                                          // remember to change "sc_test" to the correct
                                          // module name when writing your own test benches

        // test all four input combinations
        // remember that 2 inputs means 2^2 = 4 combinations
        // 3 inputs would mean 2^3 = 8 combinations to test, and so on
        // this is very similar to the input columns of a truth table
        x = 0; l = 0; b = 0; # 10;             // set initial values and wait 10 time units
        x = 0; l = 0; b = 1; # 10;             // set initial values and wait 10 time units
        x = 0; l = 1; b = 0; # 10;             // set initial values and wait 10 time units
        x = 0; l = 1; b = 1; # 10;             // set initial values and wait 10 time units
        x = 1; l = 0; b = 0; # 10;             // set initial values and wait 10 time units
        x = 1; l = 0; b = 1; # 10;             // set initial values and wait 10 time units
        x = 1; l = 1; b = 0; # 10;             // set initial values and wait 10 time units
        x = 1; l = 1; b = 1; # 10;             // set initial values and wait 10 time units

        $finish;                              // end the simulation
    end

    initial
    $monitor("At time %2t, x = %d l = %d b = %d w = %d",
             $time, x, l, b, w);
endmodule //
