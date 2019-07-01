.data
# syscall constants
PRINT_STRING            = 4
PRINT_CHAR              = 11
PRINT_INT               = 1

# memory-mapped I/O
VELOCITY                = 0xffff0010
ANGLE                   = 0xffff0014
ANGLE_CONTROL           = 0xffff0018

BOT_X                   = 0xffff0020
BOT_Y                   = 0xffff0024

TIMER                   = 0xffff001c

RIGHT_WALL_SENSOR       = 0xffff0054
PICK_TREASURE           = 0xffff00e0
TREASURE_MAP            = 0xffff0058
MAZE_MAP                = 0xffff0050

REQUEST_PUZZLE          = 0xffff00d0
SUBMIT_SOLUTION         = 0xffff00d4

BONK_INT_MASK           = 0x1000
BONK_ACK                = 0xffff0060

TIMER_INT_MASK          = 0x8000
TIMER_ACK               = 0xffff006c

REQUEST_PUZZLE_INT_MASK = 0x800
REQUEST_PUZZLE_ACK      = 0xffff00d8


# struct spim_treasure
#{
#   short x;
#   short y;
#   int points;
#};
#
#struct spim_treasure_map
#{
#   unsigned length;
#   struct spim_treasure treasures[50];
#};
.data
    .align 2
    new_treasure_map:       .word 0:256
    dfs0:                   .word 0:128
    puzzle_flag:            .word 0:1
    soln:                   .word 0:1


#Insert whatever static memory you need here

.text

##############################


get_square_begin:
    # round down to the nearest multiple of 4
    div $v0, $a0, 4
    mul $v0, $v0, 4
    jr  $ra


has_single_bit_set:
    beq $a0, $0, return0
    sub $t0, $a0, 1 # t0 = value - 1
    and $t1, $a0, $t0 # t1 = value & t0
    bnez $t1, return0
    add $t2, $0, 1 # t2 = 1
    move $v0, $t2
    jr $ra

return0:
    move $v0, $0

    jr $ra

get_lowest_set_bit:
    li $t0, 0 # i = 0
    add $t1, $0, 1 # t1 = 1

forloop:
    sll $t2, $t1, $t0 # t2 = 1<<i
    and $t3, $a0, $t2 # t3 = value & t2
    bnez $t3, returni
    add $t0, $t0, 1
    blt $t0, 16, forloop
    move $v0, $0
    jr $ra

returni:
    move $v0, $t0
    jr $ra

print_board:
    sub $sp, $sp, 32        #stk
    sw $ra, 0($sp)          #ra
    sw $s0, 4($sp)          #s0
    sw $s1, 8($sp)          #s1
    sw $s2, 12($sp)         #s2
    sw $s3, 16($sp)         #s3
    sw $s4, 20($sp)         #s4
    sw $s5, 24($sp)         #s5
    sw $s6, 28($sp)         #s6


    li $s0, 0 # i = 0
    li $s1, 0 # j = 0
    move $s2, $a0

forloopii:
    li $t0, 16 # t0 = 16
    ble $t0, $s0, Exit2
    add $s0, $s0, 1 # ++i
    li $s1, 0 # reset j = 0
    j forloopjj

forloopjj:
    li $t0, 16 # t0 = 16
    beq $s1, 16, ifloop2

endif2:
    ble $t0, $s1, forloopii
    addi $t1, $s0, -1 # t1 = i
    mul $t1, $t1, $t0 # t1 = 16 * i
    add $t2, $t1, $s1 # t2 = t1 + j
    mul $t3, $t2, 2 # t3 = t2 * 4
    add $t4, $s2, $t3 # t4 = address of board[i][j]
    lhu $s3, 0($t4) # s3 = value = board[i][j]
    li $s4, '*' # s4 = '*'
    move $a0, $s3
    jal has_single_bit_set
    move $t5, $v0 # t5 = has_single_bit_set(value)
    bnez $t5, ifloop

endif:
    move $a0, $s4
    li $v0, 11 # for the 11th syscall
    syscall

    add $s1, $s1, 1
    j forloopjj

ifloop:
    move    $a0, $s3
    jal     get_lowest_set_bit
    move    $t6, $v0 # t6 = get_lowest_set_bit(value)
    add     $s5, $t6, 1 # s5 = num = t6 + 1
    la      $s6, symbollist # s6 = symbollist
    add     $t6, $s6, $s5 # t6 = s5 + s6
    lb      $s4, 0($t6) # t7 = c
    j       endif

ifloop2:
    li $a0, '\n'
    li $v0, 11 # for the 11th syscall
    syscall
    j endif2

Exit2:
    lw $ra, 0($sp)  #ra
    lw $s0, 4($sp) #s0
    lw $s1, 8($sp) #s1
    lw $s2, 12($sp) #s1
    lw $s3, 16($sp) #s3
    lw $s4, 20($sp) #s4
    lw $s5, 24($sp) #s5
    lw $s6, 28($sp) #s6
    add $sp, $sp, 32
    jr  $ra

##############################

request_puzzles_begin:
        li      $s5, 0                              # t5 = counter for request_puzzles loop
        j       request_puzzles

request_puzzles:
        bge     $s5, 1, main_cont                   # branch to main_cont
        la      $t6, dfs0
        sw      $t6, REQUEST_PUZZLE($0)             # wait for puzzle to be ready
        j       check_puzzle_ready

request_puzzles_cont:
        #sub    $sp, $sp, 12                        # setting up stack frame for dfs call
        #sw     $ra, 0($sp)
        #sw     $t0, 4($sp)
        #sw     $t1, 8($sp)

        la      $t0, puzzle_flag                    # reset puzzle flag to 0
        sw      $0, 0($t0)
        j       solve_puzzle

solve_puzzle:

        la      $a0, dfs0                           # solve the puzzle whenever return value != 0
        jal     rule1
        la      $a0, dfs0

        bne     $v0, 0, solve_puzzle
        j       submit_puzzle

submit_puzzle:
        #la     $t0, soln
        #sw     $a0, 0($t0)                         # write the solution to "soln"
        sw      $a0, SUBMIT_SOLUTION

        add   $s5, $s5, 1                           # t5 counter increment

        #lw     $t1, 8($sp)
        #lw     $t0, 4($sp)
    # lw    $ra, 0($sp)                         # restores stack frame from dfs
    # add   $sp, $sp, 12

        j           request_puzzles

check_puzzle_ready:                                 # continuously check if puzzle is ready
        #la $t0, puzzle_flag
        lw      $t1, puzzle_flag
        beq   $t1, 1, request_puzzles_cont
        j           check_puzzle_ready


    ###############################


main:

    li          $t0, TIMER_INT_MASK                     # enable timer interrupt
    or          $t0, $t0, BONK_INT_MASK                 # enable bonk interrupt
    or          $t0, $t0, REQUEST_PUZZLE_INT_MASK       # enable request puzzle interrupt
    or          $t0, $t0, 1                             # enable global interrupt
    mtc0        $t0, $12

    sw          $0, VELOCITY($0)                        # set default velocity to 0

    lw          $v0, TIMER($zero)
    add         $v0, $v0, 1
    sw          $v0, TIMER($zero)

    j           request_puzzles_begin

main_cont:
    # stop bot
    li          $t9, 0
    sw          $t9, VELOCITY($0)

    li          $t0, TIMER_INT_MASK                     # enable timer interrupt
    or          $t0, $t0, BONK_INT_MASK                 # enable bonk interrupt
    or          $t0, $t0, 1                             # enable global interrupt
    mtc0        $t0, $12

    la          $s7, new_treasure_map                   # get addr of the treasure struct
    sw          $s7, TREASURE_MAP($0)                   # update map
    add         $s7, $s7, 4                             # address of the 0th spim_treasure struct

    li          $s6, 0                                  # set s6 as a treasure counter

    lw          $v0, TIMER($zero)
    add         $v0, $v0, 10000
    sw          $v0, TIMER($zero)

    j           detect_treasure

detect_treasure:
    li          $t9, 0
    sw          $t9, VELOCITY($0)

    bge         $s6, 32,request_puzzles_begin                     # max number of treasures is 50 (currently testing it with 32)
    move        $t1, $s7                                # & spim_treasure[0].i
    lhu         $t7, 0($t1)                             # t7 now holds the 0th treasure's i
    add         $t1, $t1, 2                             # & spim_treasure[0].j
    lhu         $t8, 0($t1)                             # t8 now holds the 0th treasure's j

    la          $t2, BOT_X
    lw          $t2, 0($t2)                             # t2 = BOT_X

    la          $t3, BOT_Y
    lw          $t3, 0($t3)                             # t3 = BOT_Y

    li          $t4, 10

    div         $t2, $t4                                # BOT_X / 10
    mflo        $t2

    div         $t3, $t4                                # BOT_Y / 10
    mflo        $t3

    bne         $t2, $t7, increment_s7                  # BOT_X == j
    bne         $t3, $t8, increment_s7                  # BOT_Y == i
    j    pick_treasure
increment_s7:
    add         $s7, $s7, 8                             # address of the next treasure struct
    add         $s6, $s6, 1                             # treasure counter += 1
    j           detect_treasure

pick_treasure:
    la          $t4, PICK_TREASURE
    sw          $t3, 0($t4)

    li          $t9, 10
    sw          $t9, VELOCITY

    j           request_puzzles_begin

    #RET:
        #jr          $ra

############################
board_address:
    mul    $v0, $a1, 16      # i*16
    add    $v0, $v0, $a2     # (i*16)+j
    sll    $v0, $v0, 1       # ((i*9)+j)*2
    add    $v0, $a0, $v0
    jr    $ra

.globl rule1
rule1:
    sub    $sp, $sp, 32
    sw    $ra, 0($sp)        # save $ra and free up 7 $s registers for
    sw    $s0, 4($sp)        # i
    sw    $s1, 8($sp)        # j
    sw    $s2, 12($sp)       # board
    sw    $s3, 16($sp)       # value
    sw    $s4, 20($sp)       # k
    sw    $s5, 24($sp)       # changed
    sw    $s6, 28($sp)       # temp
    move    $s2, $a0     # store the board base address
    li    $s5, 0         # changed = false

    li    $s0, 0         # i = 0
r1_loop1:
    li    $s1, 0         # j = 0
r1_loop2:
    move    $a0, $s2     # board
    move     $a1, $s0        # i
    move    $a2, $s1     # j
    jal    board_address
    lhu    $s3, 0($v0)       # value = board[i][j]
    move    $a0, $s3
    jal    has_single_bit_set
    beq    $v0, 0, r1_loop2_bot    # if not a singleton, we can go onto the next iteration

    li    $s4, 0         # k = 0
r1_loop3:
    beq    $s4, $s1, r1_skip_row    # skip if (k == j)
    move    $a0, $s2     # board
    move     $a1, $s0        # i
    move    $a2, $s4     # k
    jal    board_address
    lhu    $t0, 0($v0)       # board[i][k]
    and    $t1, $t0, $s3
    beq    $t1, 0, r1_skip_row
    not    $t1, $s3
    and    $t1, $t0, $t1
    sh    $t1, 0($v0)        # board[i][k] = board[i][k] & ~value
    li    $s5, 1         # changed = true

r1_skip_row:
    beq    $s4, $s0, r1_skip_col    # skip if (k == i)
    move    $a0, $s2     # board
    move     $a1, $s4        # k
    move    $a2, $s1     # j
    jal    board_address
    lhu    $t0, 0($v0)       # board[k][j]
    and    $t1, $t0, $s3
    beq    $t1, 0, r1_skip_col
    not    $t1, $s3
    and    $t1, $t0, $t1
    sh    $t1, 0($v0)        # board[k][j] = board[k][j] & ~value
    li    $s5, 1         # changed = true

r1_skip_col:
    add    $s4, $s4, 1       # k ++
    blt    $s4, 16, r1_loop3

    ## doubly nested loop
    move    $a0, $s0     # i
    jal    get_square_begin
    move    $s6, $v0     # ii
    move    $a0, $s1     # j
    jal    get_square_begin    # jj

    move     $t0, $s6        # k = ii
    add    $t1, $t0, 4       # ii + GRIDSIZE
    add     $s6, $v0, 4      # jj + GRIDSIZE

r1_loop4_outer:
    sub    $t2, $s6, 4       # l = jj  (= jj + GRIDSIZE - GRIDSIZE)

r1_loop4_inner:
    bne    $t0, $s0, r1_loop4_1
    beq    $t2, $s1, r1_loop4_bot

r1_loop4_1:
    mul    $v0, $t0, 16      # k*16
    add    $v0, $v0, $t2     # (k*16)+l
    sll    $v0, $v0, 1       # ((k*16)+l)*2
    add    $v0, $s2, $v0     # &board[k][l]
    lhu    $v1, 0($v0)       # board[k][l]
     and    $t3, $v1, $s3        # board[k][l] & value
    beq    $t3, 0, r1_loop4_bot

    not    $t3, $s3
    and    $v1, $v1, $t3
    sh    $v1, 0($v0)        # board[k][l] = board[k][l] & ~value
    li    $s5, 1         # changed = true

r1_loop4_bot:
    add    $t2, $t2, 1       # l++
    blt    $t2, $s6, r1_loop4_inner

    add    $t0, $t0, 1       # k++
    blt    $t0, $t1, r1_loop4_outer


r1_loop2_bot:
    add    $s1, $s1, 1       # j ++
    blt    $s1, 16, r1_loop2

    add    $s0, $s0, 1       # i ++
    blt    $s0, 16, r1_loop1

    move    $v0, $s5     # return changed
    lw    $ra, 0($sp)        # restore registers and return
    lw    $s0, 4($sp)
    lw    $s1, 8($sp)
    lw    $s2, 12($sp)
    lw    $s3, 16($sp)
    lw    $s4, 20($sp)
    lw    $s5, 24($sp)
    lw    $s6, 28($sp)
    add    $sp, $sp, 32
    jr    $ra

############################

# Kernel Text
.kdata
chunkIH:    .space 40
non_intrpt_str: .asciiz "Non-interrupt exception\n"
unhandled_str:  .asciiz "Unhandled interrupt type\n"
.ktext 0x80000180
interrupt_handler:
.set noat
        move    $k1, $at        # Save $at
.set at
        la      $k0, chunkIH
        sw      $a0, 0($k0)     # Get some free registers
        sw      $v0, 4($k0)     # by storing them to a global variable

        sw      $t0, 8($k0)
        sw      $t1, 12($k0)
        sw      $t2, 16($k0)
        sw      $t3, 20($k0)
        sw      $a1, 24($k0)
        sw      $t9, 28($k0)
        sw      $t6, 32($k0)



        mfc0    $k0, $13            # Get Cause register
        srl     $a0, $k0, 2
        and     $a0, $a0, 0xf       # ExcCode field
        bne     $a0, 0, non_intrpt



interrupt_dispatch:         # Interrupt:
        mfc0    $k0, $13        # Get Cause register, again
        beq     $k0, 0, done        # handled all outstanding interrupts

        and     $a0, $k0, BONK_INT_MASK # is there a bonk interrupt?
        bne     $a0, 0, bonk_interrupt

        and     $a0, $k0, TIMER_INT_MASK    # is there a timer interrupt?
        bne     $a0, 0, timer_interrupt

        and     $a0, $k0, REQUEST_PUZZLE_INT_MASK
        bne     $a0, 0, request_puzzle_interrupt

        li      $v0, PRINT_STRING   # Unhandled interrupt types
        la      $a0, unhandled_str
        syscall
        j   done

bonk_interrupt:
        sw      $a1, BONK_ACK($zero)
        li      $t6, 180
        sw      $t6, ANGLE($zero)
        sw      $0, ANGLE_CONTROL
        li      $t9, 10
        sw      $t9, VELOCITY # VELOCITY = 10
        j       interrupt_dispatch  # see if other interrupts are waiting

request_puzzle_interrupt:
    #Fill in your code here
        sw      $a1, REQUEST_PUZZLE_ACK($0)   # acknowledge interrupt
        la      $t1, puzzle_flag
        li      $t0, 1
        sw      $t0, 0($t1)
        j       interrupt_dispatch

timer_interrupt:
    #Fill in your code here

        sw      $a1, TIMER_ACK($zero)       # acknowledge interrupt
        j       check_right


        ##########################
        check_right:
            li          $t9, 10
            sw          $t9, VELOCITY
            lw          $t1, RIGHT_WALL_SENSOR($0)              # t1 = curr_right_wall
            beq         $t1, 1, wall_on_right
            beq         $t1, 0, no_wall

        wall_on_right:
            li          $t9, 10
            sw          $t9, VELOCITY
            lw          $t1, RIGHT_WALL_SENSOR($0)
            beq         $t1, 0, no_wall
            beq         $t1, 1, proceed

        no_wall:
            li          $t6, 90                                 # turn 90 degrees relative
            sw          $t6, ANGLE($0)
            sw          $0, ANGLE_CONTROL($0)
            j           proceed

        proceed:
            li          $t9, 10
            sw          $t9, VELOCITY
            lw          $t1, RIGHT_WALL_SENSOR($0)
            #j         detect_treasure

            beq         $t1, 0, next_timer

        next_timer:
            lw          $v0, TIMER($zero)
            add         $v0, $v0, 7000
            sw          $v0, TIMER($zero)
            j           interrupt_dispatch

        ##########################

non_intrpt:             # was some non-interrupt
        li      $v0, PRINT_STRING
        la      $a0, non_intrpt_str
        syscall             # print out an error message
        # fall through to done

done:
        la      $k0, chunkIH
        lw      $a0, 0($k0)     # Restore saved registers
        lw      $v0, 4($k0)
        lw      $t0, 8($k0)
        lw      $t1, 12($k0)
        lw      $t2, 16($k0)
        lw      $t3, 20($k0)
        lw      $a1, 24($k0)
        lw      $t9, 28($k0)
        lw      $t6, 32($k0)

.set noat
        move    $at, $k1        # Restore $at
.set at
        eret
