.text

## bool
## board_done(unsigned short board[16][16]) {
##   for (int i = 0 ; i < 16 ; ++ i) {
##     for (int j = 0 ; j < 16 ; ++ j) {
##       if (!has_single_bit_set(board[i][j])) {
##         return false;
##       }
##     }
##   }
##   return true;
## }

.globl board_done
board_done:
	sub $sp, $sp, 24
	sw  $ra, 0($sp)
	sw  $s0, 4($sp) # holds value 16
	sw  $s1, 8($sp) #  i
	sw  $s2, 12($sp) #  j
	sw  $s3, 16($sp) # temp used for array calculation
	sw  $s4, 20($sp) # stores a0

	li  $s0, 16 # holds value 16
	li  $s1, 0  # i = 0

	LOOP:

		bge  $s1, $s0, RET
		li  $s2, 0  # j = 0

		LOOP_INNER:

			bge  $s2, $s0, LOOP
			lhu  $s4, 0($a0) # stores a0

			mul  $s3, $s1, 16  # i*N
			add  $s3, $s3, $s2 # (i*N)+j
			mul  $s3, $s3, 2  # (i*N)+j * sizeof(elem), size = 2
			add  $a0, $a0, $s3 # A[0][0] + (i*N)+j * sizeof(elem)

			jal  has_single_bit_set
			move  $a0, $s4  # restores the initial value of a0

			beq  $v0, $zero, REF
			add  $s2, $s2, 1
			j LOOP_INNER

	add  $s1, $s1, 1
	j LOOP

	RET:
		lw  $ra, 0($sp)
		lw  $s0, 4($sp)
		lw  $s1, 8($sp)
		lw  $s2, 12($sp)
		lw  $s3, 16($sp)
		lw  $s4, 20($sp)
		add $sp, $sp, 24
		li  $v0, 1
		jr	$ra

	REF:
		lw  $ra, 0($sp)
		lw  $s0, 4($sp)
		lw  $s1, 8($sp)
		lw  $s2, 12($sp)
		lw  $s3, 16($sp)
		lw  $s4, 20($sp)
		add $sp, $sp, 24
		li  $v0, 0
		jr  $ra


## void
## print_board(unsigned short board[16][16]) {
##   for (int i = 0 ; i < 16 ; ++ i) {
##     for (int j = 0 ; j < 16 ; ++ j) {
##       int value = board[i][j];
##       char c = '*';
##       if (has_single_bit_set(value)) {
##         int num = get_lowest_set_bit(value) + 1;
##         c = symbollist[num];
##       }
##       putchar(c);
##     }
##     putchar('\n');
##   }
## }


.globl print_board
print_board:

	sub $sp, $sp, 40
	sw  $ra, 0($sp)
	sw  $s0, 4($sp) # holds value 16
	sw  $s1, 8($sp) #  i
	sw  $s2, 12($sp) #  j
	sw  $s3, 16($sp) # temp used for array calculation
	sw  $s4, 20($sp) # stores initial a0
	sw  $s5, 24($sp) # char c

	li  $s0, 16 # holds value 16
	li  $s1, 0  # i = 0

	LOOP_2:
		bge  $s1, $s0, RET_2
		li  $s2, 0  # j = 0

		LOOP_INNER_2:

			bge  $s2, $s0, LOOP_2
			move $s4, $a0 # stores a0

			# board[i][j]
			mul  $s3, $s1, 16  # i*N
			add  $s3, $s3, $s2 # (i*N)+j
			mul  $s3, $s3, 2  # (i*N)+j * sizeof(elem), size = 2
			add  $a0, $a0, $s3 # A[0][0] + (i*N)+j * sizeof(elem)
			move  $t0, $a0  # stores calculated a0
			sw  $t0, 28($sp)

			# char c = '*'
			li  $s5, '*'

			jal  has_single_bit_set
			move  $a0, $t0  # restore a0 to calculated value

			move $t1, $v0
			sw  $t1, 32($sp) # stores return val of has_single_bit_set

			beq $t1, $zero, PUT_C
			move $a0, $t0 # get calculated value of a0
			jal  get_lowest_set_bit
			add $t2, $v0, 1 # get_lowest_set_bit(value) + 1
			sw  $t2, 36($sp) # int num = get_lowest_set_bit(value) + 1;

			la  $t3, symbollist
			add $t3, $t3, $t2 # symbolist[num]
			lb  $s5, 0($t3)

			add  $s2, $s2, 1
			j LOOP_INNER_2

	# putchar('\n')
	li  $a0, '\n'
	li  $v0, 11
	syscall

	add  $s1, $s1, 1
	move $a0, $s4
	j LOOP_2

PUT_C:
	move  $a0, $s5
	li  $v0, 11
	syscall

RET_2:
	lw  $ra, 0($sp)
	lw  $s0, 4($sp)
	lw  $s1, 8($sp)
	lw  $s2, 12($sp)
	lw  $s3, 16($sp)
	lw  $s4, 20($sp)
	lw  $s5, 24($sp)
	add $sp, $sp, 40
