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

.data
#
#Put any static memory you need here
#



.text
main:
	#Fill in your code here
	li      $t0, TIMER_INT_MASK       	  # enable timer interrupt
	or      $t0, $t0, BONK_INT_MASK   		# enable bonk interrupt
	or      $t0, $t0, 1        					  # enable global interrupt
	mtc0    $t0, $12

	lw			$v0, TIMER($0)
	add     $v0, $v0, 1000
	sw			$v0, TIMER($0)

	li			$a0, 10
	sw			$a0, VELOCITY($0)
	j				infinite


	infinite:
		la 			$t2, BOT_X
		lw 			$t2, 0($t2)									# t2 = BOT_X
		la 			$t3, BOT_Y
		lw 			$t3, 0($t3)									# t3 = BOT_Y

		bge			$t2, 300, x_greater_than_300		# branch for x >= 300

		j				check_right

		x_greater_than_300:
			bge			$t3, 300, RET								# check if y >= 300. if so, return
			j				check_right

	check_right:
		lw			$t1, RIGHT_WALL_SENSOR($0)				# t1 = curr_right_wall
		beq 		$t1, 1, wall_on_right
		beq 		$t1, 0, no_wall

	wall_on_right:
		lw			$t1, RIGHT_WALL_SENSOR($0)
		beq			$t1, 0, no_wall
		beq			$t1, 1, wall_on_right

	no_wall:
		li      $t6, 90											# turn 90 degrees relative
		sw      $t6, ANGLE($0)
		sw      $0, ANGLE_CONTROL($0)
		j       proceed

	proceed:
		lw			$t1, RIGHT_WALL_SENSOR($0)
		beq			$t1, 0, proceed
		j				wall_on_right

	RET:
		jr			$ra

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

    li        $v0, PRINT_STRING    # Unhandled interrupt types
    la        $a0, unhandled_str
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
