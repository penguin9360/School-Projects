.text

## bool
## rule1(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
##   bool changed = false;
##   for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
##     for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
##       unsigned value = board[i][j];
##       if (has_single_bit_set(value)) {
##         for (int k = 0 ; k < GRID_SQUARED ; ++ k) {
##           // ELIMINATE from row
##           if (k != j) {
##             if (board[i][k] & value) {
##               board[i][k] &= ~value;
##               changed = true;
##             }
##           }
##           // ELIMINATE from column
##           if (k != i) {
##             if (board[k][j] & value) {
##               board[k][j] &= ~value;
##               changed = true;
##             }
##           }
##         }
##
##         // elimnate from square
##         int ii = get_square_begin(i);
##         int jj = get_square_begin(j);
##         for (int k = ii ; k < ii + GRIDSIZE ; ++ k) {
##           for (int l = jj ; l < jj + GRIDSIZE ; ++ l) {
##             if ((k == i) && (l == j)) {
##               continue;
##             }
##             if (board[k][l] & value) {
##               board[k][l] &= ~value;
##               changed = true;
##             }
##           }
##         }
##       }
##     }
##   }
##   return changed;
## }

.globl rule1
rule1:
	sub	 	$sp, $sp, 36
	sw 	 	$ra, 0($sp)
	sw		$s0, 4($sp)						 # ii
	sw		$s1, 8($sp)						 # jj
	sw		$s2, 12($sp)					 # bool changed
	sw		$s3, 16($sp)					 # i
	sw  	$s4, 20($sp)					 # j
	sw  	$s5, 24($sp)					 # holds a0
	sw    $s6, 28($sp) 					 # k, used in 2 loops
	sw    $s7, 32($sp)					 # value

	li  	$t5, 16        	    	 # t5 = GRID_SQUARED = 16, later reset to 4
	li 		$s2, 0								 # s2 = "changed", initialized to false
	li  	$s3, 0								 # s3 = i
	li  	$s4, 0							   # s4 = j
	move  $s5, $a0          	 	 # holds the initial a0

	LOOP_OUT:
		li   $t5, 16							 # hard code t5 to hold 16
		bge  $s3, 16, RET_CHANGE
		li   $s4, 0           							 # j = 0
		j		 LOOP_IN_j											 # enter j loop

		LOOP_IN_j:
			li  	$t5, 16        	    				 # t5 restores to 16
			li    $s6, 0											 # k = 0
			bge  	$s4, 16, break_i						 # if j > 16 => break and increment i

			mul		$t0, $s3, 16								 # i*16
			add		$t0, $t0, $s4	   					 	 # (i*16)+j
			sll		$t0, $t0, 1		  	 					 # ((i*16)+j)*2
			add		$s7, $s5, $t0     					 # s7 holds the address of "value"
			lhu		$a0, 0($s7)     						 # dereference - a0 now holds "value"
			move 	$s7, $a0
			jal		has_single_bit_set

			beq   $v0, $zero, break_j 				 # if (has_single_bit_set(value)) == 0 -> break_j
			j			LOOP_IN_k										 # enter k loop

			LOOP_IN_k:
				bge		$s6, 16, ELIMINATE_SQ		 # k >= 16 => break to ELIMINATE_SQ
				beq		$s6, $s4, BOARD_K_J  			 # if k = j => go to BOARD_K_J

				move  $a0, $s5									 # restores initial $a0
				mul		$t0, $s3, 16							 # i*16
				add		$t0, $t0, $s6	   					 # (i*16)+k
				sll		$t0, $t0, 1		  					 # ((i*16)+k)*2
				add		$a0, $a0, $t0   				   # &board[i][k] - finding address
				lhu		$t0, 0($a0)								 # board[i][k]
				and   $t1, $t0, $s7							 # board[i][k] & value

				beq		$t1, $zero, BOARD_K_J			 # if (board[i][k] & value) == 0 => BOARD_K_J
				not   $t2, $s7          				 # ~value
				and   $t3, $t0, $t2     				 # board[i][k] & ~value
				sh		$t3, 0($a0)								 # board[i][k] &= ~value;
				li    $s2, 1										 # changed = true
				j			BOARD_K_J

				BOARD_K_J:
					beq   $s6, $s3, break_k		   	 # if k = i, break and increment k
					move  $a0, $s5								 # restores initial $a0
					mul		$t0, $s6, 16						 # k*16
					add		$t0, $t0, $s4	   				 # (k*16)+j
					sll		$t0, $t0, 1		  				 # ((k*16)+j)*2
					add		$a0, $a0, $t0   				 # &board[k][j] - finding address
					lhu		$t0, 0($a0)							 # *board[k][j]
					and   $t1, $t0, $s7						 # board[k][j] & value

					beq		$t1, $zero, break_k		   # if (board[k][j] & value) = 0 -> break_k
					not   $t2, $s7          			 # ~value
					and   $t3, $t0, $t2     			 # board[k][j] & ~value
					sh		$t3, 0($a0)							 # board[k][j] &= ~value;
					li    $s2, 1									 # changed = true
					j			break_k									 # finishing a cycle of loop_in_k

				break_k:
					add   $s6, $s6, 1     				 # ++k
					j		  LOOP_IN_k

			ELIMINATE_SQ:
				move   $a0, $s3									 # a0 = i
				jal    get_square_begin          # get_square_begin(i)
				move   $s0, $v0                  # s0 = ii

				move   $a0, $s4									 # a0 = j
				jal    get_square_begin					 # get_square_begin(j)
				move   $s1, $v0									 # s1 = jj


				li      $t5, 4								 # t5 = GRIDSIZE = 4, reused
				add			$t7, $s0, 4					 # t7 now holds ii + GRIDSIZE
				add			$t8, $s1, 4					 # t8 now holds jj + GRIDSIZE
				move    $s6, $s0							 # k = ii, reused

				j			 LOOP_IN_II

				LOOP_IN_II:

					move		$t6, $s1							 # t6 = l, initialized to jj
					bge			$s6, $t7, break_j      # jump back to j loop if k >= ii + GRIDSIZE
					j				LOOP_IN_JJ						 # enters LOOP_JJ

					LOOP_IN_JJ:
						bge		$t6, $t8, break_ii
						bne   $s6, $s3, BOARD_K_L			   # if k!= i => go to BOARD_K_L
						bne   $t6, $s4, BOARD_K_L  			 # if l!= j => go to BOARD_K_L
						j			break_jj								 	 # "continue" statement

						BOARD_K_L:
							move  $a0, $s5								 # restores initial $a0
							mul		$t0, $s6, 16						 # k*16
							add		$t0, $t0, $t6	   				 # (k*16)+l
							sll		$t0, $t0, 1		  				 # ((k*16)+l)*2
							add		$a0, $a0, $t0   				 # &board[k][l] - finding address
							lhu		$t0, 0($a0)							 # *board[k][l]
							and   $t1, $t0, $s7						 # board[k][l] & value

							beq		$t1, $zero, break_jj	   # if (board[k][l] & value) = 0 => break and increment JJ
							not   $t2, $s7          			 # ~value
							and   $t3, $t0, $t2     			 # board[k][l] & ~value
							sh		$t3, 0($a0)							 # board[k][l] &= ~value;
							li    $s2, 1									 # changed = true
							j			break_jj								 # go back to loop jj

					break_jj:
						add				$t6, $t6, 1					 	 # increment l before exiting
						j  LOOP_IN_JJ

				break_ii:
					add     $s6, $s6, 1								 # increment k before exiting
					j		LOOP_IN_II

			break_j:
				add  $s4, $s4, 1      							 # ++j
				j    LOOP_IN_j

		break_i:
			add  $s3, $s3, 1      								 # ++i
			j  LOOP_OUT



RET_CHANGE:

	move 	$v0, $s2

	lw    $s7, 32($sp)
	lw    $s6, 28($sp)
	lw    $s5, 24($sp)
	lw    $s4, 20($sp)
	lw    $s3, 16($sp)
	lw    $s2, 12($sp)
	lw  	$s1, 8($sp)
	lw  	$s0, 4($sp)
	lw  	$ra, 0($sp)
	add   $sp, $sp, 36

	jr    $ra
