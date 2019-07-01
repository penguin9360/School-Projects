.globl get_square_begin
get_square_begin:
	# round down to the nearest multiple of 4
	div	$v0, $a0, 4
	mul	$v0, $v0, 4
	jr	$ra


# UNTIL THE SOLUTIONS ARE RELEASED, YOU SHOULD COPY OVER YOUR VERSION FROM LAB 7
# (feel free to copy over the solution afterwards)
.globl has_single_bit_set
has_single_bit_set:
	beq	$a0, 0, hsbs_ret_zero	# return 0 if value == 0
	sub	$a1, $a0, 1
	and	$a1, $a0, $a1
	bne	$a1, 0, hsbs_ret_zero	# return 0 if (value & (value - 1)) == 0
	li	$v0, 1
	jr	$ra
hsbs_ret_zero:
	li	$v0, 0
	jr	$ra


# UNTIL THE SOLUTIONS ARE RELEASED, YOU SHOULD COPY OVER YOUR VERSION FROM LAB 7
# (feel free to copy over the solution afterwards)
.globl get_lowest_set_bit
get_lowest_set_bit:
	li	$v0, 0			# i
	li	$t1, 1

glsb_loop:
	sll	$t2, $t1, $v0		# (1 << i)
	and	$t2, $t2, $a0		# (value & (1 << i))
	bne	$t2, $0, glsb_done
	add	$v0, $v0, 1
	blt	$v0, 16, glsb_loop	# repeat if (i < 16)

	li	$v0, 0			# return 0
glsb_done:
	jr	$ra


# UNTIL THE SOLUTIONS ARE RELEASED, YOU SHOULD COPY OVER YOUR VERSION FROM LAB 7
# (feel free to copy over the solution afterwards)
.globl print_board
print_board:
	sub	$sp, $sp, 20
	sw	$ra, 0($sp)		# save $ra and free up 4 $s registers for
	sw	$s0, 4($sp)		# i
	sw	$s1, 8($sp)		# j
	sw	$s2, 12($sp)		# the function argument
	sw	$s3, 16($sp)		# the computed pointer (which is used for 2 calls)
	move	$s2, $a0

	li	$s0, 0			# i
pb_loop1:
	li	$s1, 0			# j
pb_loop2:
	mul	$t0, $s0, 16		# i*16
	add	$t0, $t0, $s1		# (i*16)+j
	sll	$t0, $t0, 1		# ((i*16)+j)*2
	add	$s3, $s2, $t0
	lhu	$a0, 0($s3)
	jal	has_single_bit_set
	beq	$v0, 0, pb_star		# if it has more than one bit set, jump
	lhu	$a0, 0($s3)
	jal	get_lowest_set_bit	#
	add	$v0, $v0, 1		# $v0 = num
	la	$t0, symbollist
	add	$a0, $v0, $t0		# &symbollist[num]
	lb	$a0, 0($a0)		#  symbollist[num]
	li	$v0, 11
	syscall
	j	pb_cont

pb_star:
	li	$v0, 11			# print a "*"
	li	$a0, '*'
	syscall

pb_cont:
	add	$s1, $s1, 1		# j++
	blt	$s1, 16, pb_loop2

	li	$v0, 11			# at the end of a line, print a newline char.
	li	$a0, '\n'
	syscall

	add	$s0, $s0, 1		# i++
	blt	$s0, 16, pb_loop1

	lw	$ra, 0($sp)		# restore registers and return
	lw	$s0, 4($sp)
	lw	$s1, 8($sp)
	lw	$s2, 12($sp)
	lw	$s3, 16($sp)
	add	$sp, $sp, 20
	jr	$ra
