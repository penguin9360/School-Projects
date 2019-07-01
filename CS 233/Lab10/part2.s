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

RIGHT_WALL_SENSOR 			= 0xffff0054
PICK_TREASURE           = 0xffff00e0
TREASURE_MAP            = 0xffff0058

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
#    short x;
#    short y;
#    int points;
#};
#
#struct spim_treasure_map
#{
#    unsigned length;
#    struct spim_treasure treasures[50];
#};
#

.data
#REQUEST_PUZZLE returns an int array of length 128

.align 4
#
#Put any other static memory you need here
#
dfs0:      					.word 0:128   							# not actually used, placeholder for loop
soln:								.word 0:1

puzzle_flag:    	 	.word 0:1
new_treasure_map:   .word 0:256           			# assume new treasure map costs 1024 bytes

.text

request_puzzles_begin:
		li		$t5, 0																# t5 = counter for request_puzzles loop
		j			request_puzzles

request_puzzles:
		bge		$t5, 8,	main_cont											# branch to main_cont
		la    $t6, dfs0
		sw    $t6, REQUEST_PUZZLE($0)								# wait for puzzle to be ready
		j			check_puzzle_ready

request_puzzles_cont:
		sub		$sp, $sp, 12													# setting up stack frame for dfs call
		sw 		$ra, 0($sp)
		sw 		$t0, 4($sp)
		sw 		$t1, 8($sp)

		la    $t0, puzzle_flag											# reset puzzle flag to 0
		sw		$0, 0($t0)

		la	  $a0, dfs0															# prepare to call dfs
		li		$a1, 1
		li		$a2, 1
		jal		dfs

		la    $t0, soln
		sw		$v0, 0($t0)														# write the solution to "soln"
		sw		$t0, SUBMIT_SOLUTION

		add   $t5, $t5, 1														# t5 counter increment

		lw		$t1, 8($sp)
		lw		$t0, 4($sp)
		lw 		$ra, 0($sp)														# restores stack frame from dfs
		add		$sp, $sp, 12

		j			request_puzzles

check_puzzle_ready:															# continuously check if puzzle is ready
		#la    $t0, puzzle_flag
		lw		$t1, puzzle_flag
		beq   $t1, 1, request_puzzles_cont
		j			check_puzzle_ready

.globl dfs
dfs:																						# copied solutions for dfs
		sub		$sp, $sp, 16
		sw 		$ra, 0($sp)
		sw		$s0, 4($sp)
		sw		$s1, 8($sp)
		sw		$s2, 12($sp)
		move 	$s0, $a0
		move 	$s1, $a1
		move	$s2, $a2

_dfs_base_case_one:
        blt     $s1, 127, _dfs_base_case_two
        li      $v0, -1
        j _dfs_return

_dfs_base_case_two:
		mul			$t1, $s1, 4
		add			$t2, $s0, $t1
    lw      $t1, 0($t2)
    bne     $t1, $s2, _dfs_ret_one
    li      $v0, 0
		j _dfs_return

_dfs_ret_one:
		mul		$a1, $s1, 2
		jal 	dfs
		blt		$v0, 0, _dfs_ret_two
		addi	$v0, 1
		j _dfs_return

_dfs_ret_two:
    mul		$a1, $s1, 2
		addi	$a1, 1
		jal 	dfs
		blt		$v0, 0, _dfs_return
		addi	$v0, 1
		j _dfs_return

_dfs_return:
		lw 		$ra, 0($sp)
		lw		$s0, 4($sp)
		lw		$s1, 8($sp)
		lw		$s2, 12($sp)
		add		$sp, $sp, 16
    jr    $ra

go_east:
		li              $t6, 0
		sw              $t6, ANGLE
		li              $t6, 1
		sw              $t6, ANGLE_CONTROL
		j								move_continue
go_west:
		li              $t6, 180
		sw              $t6, ANGLE
		li              $t6, 1
		sw              $t6, ANGLE_CONTROL
		j								move_continue
go_south:
		li              $t6, 90
		sw              $t6, ANGLE
		li              $t6, 1
		sw              $t6, ANGLE_CONTROL
		j								move_continue
go_north:
		li              $t6, 270
		sw              $t6, ANGLE
		li              $t6, 1
		sw              $t6, ANGLE_CONTROL
		j								move_continue

move_continue:															  # moves one cell per time
		li			$t1, 10
		sw			$t1, VELOCITY($0)
		li			$t0, 0														# t0 = instruction cycle counter

		move_forward:
			add		$t0, $t0, 1
			blt   $t0, 3300, move_forward						# t0 += half of 10000 instruction cycle, value could be adjusted
			sw		$0, VELOCITY($0)									# reset velocity to 0
			jr		$ra

main:

	la			$t1, new_treasure_map
	sw			$t1, TREASURE_MAP($0)								# initializes treasure map

	li      $t0, TIMER_INT_MASK       				  # enable timer interrupt
	or      $t0, $t0, BONK_INT_MASK   					# enable bonk interrupt
	or			$t0, $t0, REQUEST_PUZZLE_INT_MASK		# enable request puzzle interrupt
	or      $t0, $t0, 1        								  # enable global interrupt
	mtc0    $t0, $12

	sw			$0, VELOCITY($0)										# set default velocity to 0

	j				request_puzzles

main_cont:

	lw			$v0, TIMER($0)											# read from time interrupt
	add     $v0, $v0, 10000
	sw			$v0, TIMER($0)
	j				paths

paths:																				# hardcode the path
	sub     $sp, $sp, 4													# setup the stack frame for moving function calls
	sw 			$ra, 0($sp)



	la      $t2, PICK_TREASURE									# picks the 1st treasure right under the spawn point
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal			go_west
	jal			go_west
	jal			go_south
	jal			go_east
	jal			go_east
	la      $t2, PICK_TREASURE									# picks the 2nd treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal 		go_south
	jal     go_east
	jal			go_north
	la      $t2, PICK_TREASURE									# picks the 3rd treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal     go_east
	jal			go_north
	jal			go_west
	la      $t2, PICK_TREASURE									# picks the 4th treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal			go_north
	jal			go_north
	jal     go_east
	jal			go_north
	jal     go_north
	jal     go_east
	jal     go_east
	jal     go_east
	jal     go_east
	jal     go_east
	la      $t2, PICK_TREASURE									# picks the 5th treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal			go_west
	jal			go_west
	jal			go_south
	jal			go_east
	jal			go_east
	la      $t2, PICK_TREASURE									# picks the 6th treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal 		go_south
	jal     go_east
	jal			go_north
	la      $t2, PICK_TREASURE									# picks the 7th treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	jal     go_east
	jal			go_north
	jal			go_west
	la      $t2, PICK_TREASURE									# picks the 8th treasure
	sw			$t0, 0($t2)													# store random stuff....by definition

	lw			$ra, 0($sp)													# restore the stack frame from moving function calls
	add     $sp, $sp, 4

	j				final_infinite_loop

final_infinite_loop:													# just keep spimbot from exiting...
	j				final_infinite_loop

.kdata
chunkIH:    .space 28
non_intrpt_str:    .asciiz "Non-interrupt exception\n"
unhandled_str:    .asciiz "Unhandled interrupt type\n"
.ktext 0x80000180
interrupt_handler:
.set noat
        move      $k1, $at        # Save $at
.set at
        la        $k0, chunkIH
        sw        $a0, 0($k0)        # Get some free registers
        sw        $v0, 4($k0)        # by storing them to a global variable
        sw        $t0, 8($k0)
        sw        $t1, 12($k0)
        sw        $t2, 16($k0)
        sw        $t3, 20($k0)

        mfc0      $k0, $13             # Get Cause register
        srl       $a0, $k0, 2
        and       $a0, $a0, 0xf        # ExcCode field
        bne       $a0, 0, non_intrpt



interrupt_dispatch:            # Interrupt:
    mfc0       $k0, $13        # Get Cause register, again
    beq        $k0, 0, done        # handled all outstanding interrupts

    and        $a0, $k0, BONK_INT_MASK    # is there a bonk interrupt?
    bne        $a0, 0, bonk_interrupt

    and        $a0, $k0, TIMER_INT_MASK    # is there a timer interrupt?
    bne        $a0, 0, timer_interrupt

		and 		   $a0, $k0, REQUEST_PUZZLE_INT_MASK
		bne 			 $a0, 0, request_puzzle_interrupt

    li         $v0, PRINT_STRING    # Unhandled interrupt types
    la         $a0, unhandled_str
    syscall
    j    done

bonk_interrupt:
    #Fill in your code here
		sw      $a1, BONK_ACK($zero)
		li      $t6, 180
		sw      $t6, ANGLE($zero)
		sw      $0, ANGLE_CONTROL
		li      $t5, 10
		sw      $t5, VELOCITY          			# VELOCITY = 10

    j       interrupt_dispatch

request_puzzle_interrupt:
	#Fill in your code here
		sw 			$a1, REQUEST_PUZZLE_ACK($0)   # acknowledge interrupt
		la      $t1, puzzle_flag
		li			$t0, 1
		sw			$t0, 0($t1)
		j       interrupt_dispatch

timer_interrupt:
    #Fill in your code here
		sw		 	$a1, TIMER_ACK($zero)		 # acknowledge interrupt
		lw			$v0, TIMER($zero)
		add			$v0, $v0, 10000
		sw			$v0, TIMER($zero)
		j        interrupt_dispatch    # see if other interrupts are waiting

non_intrpt:                # was some non-interrupt
    li        $v0, PRINT_STRING
    la        $a0, non_intrpt_str
    syscall                # print out an error message
    # fall through to done

done:
    la      $k0, chunkIH
    lw      $a0, 0($k0)        # Restore saved registers
    lw      $v0, 4($k0)
		lw      $t0, 8($k0)
    lw      $t1, 12($k0)
    lw      $t2, 16($k0)
    lw      $t3, 20($k0)
.set noat
    move    $at, $k1        # Restore $at
.set at
    eret
