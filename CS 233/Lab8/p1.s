.text

##int
##dfs(int* tree, int i, int input) {
##	if (i >= 127) {
##		return -1;
##	}
##	if (input == tree[i]) {
##		return 0;
##	}
##
##	int ret = DFS(tree, 2 * i, input);
##	if (ret >= 0) {
##		return ret + 1;
##	}
##	ret = DFS(tree, 2 * i + 1, input);
##	if (ret >= 0) {
##		return ret + 1;
##	}
##	return ret;
##}

.globl dfs

dfs:

	sub 	$sp, $sp, 16
	sw  	$ra, 0($sp)
	sw  	$s0, 4($sp) 					 # stores tree[i]
	sw  	$s1, 8($sp)						 # i
	sw  	$s2, 12($sp)					 # temp for dereferening tree

	move  $s1, $a1     					 # stores initial $a1(i) in $s1
	move  $s0, $a0    					 # stores $a0 in $s0

	blt 	$a1, 127, next         # if i < 127 => next
	li    $v0, -1					    	 # return -1
	j     RET

	next:
		mul   $t1, $a1, 4            # i * 4, offset
		add   $t1, $t1, $a0					 # a0 + 4*a1 => i
		lw    $t2, 0($t1) 					 # *tree[i]
		bne   $t2, $a2, dfs_recurse	 # tree[i] != input => start recursion
		li    $v0, 0								 # return 0
		j     RET

dfs_recurse:

	mul   $a1, $a1, 2 					 # 2 * i, tree->left
	jal 	dfs										 # start the 1st recursion
	blt   $v0, 0, recur_next1    # finishes the 1st recursion
	add   $v0, $v0, 1
	j     RET

	recur_next1:
		move  $a0, $s0							 # restores a0
		move  $a1, $s1							 # restores a1
		mul   $a1, $a1, 2 					 # 2 * i
		add   $a1, $a1, 1  					 # 2 * i + 1, tree->right
		jal		dfs										 # starts the 2nd recursion
		blt   $v0, 0, RET            # if ret < 0, return ret
		add   $v0, $v0, 1		         # return ret + 1
		j     RET

RET:
	lw    $s2, 12($sp)
	lw  	$s1, 8($sp)
	lw  	$s0, 4($sp)
	lw  	$ra, 0($sp)
	add   $sp, $sp, 16
	jr    $ra
