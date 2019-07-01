# syscall constants
PRINT_INT = 1
PRINT_CHAR = 11

.data
	tree1:	.word	0:127
	tree2:	.word	0:127
	tree3:	.word	0:127
.text

# print int and space ##################################################
#
# argument $a0: number to print

print_int_and_space:
	li	$v0, PRINT_INT	# load the syscall option for printing ints
	syscall			# print the number

	li   	$a0, ' '       	# print a black space
	li	$v0, PRINT_CHAR	# load the syscall option for printing chars
	syscall			# print the char
	
	jr	$ra		# return to the calling procedure

# main function ########################################################
#
#  this is a function that will test dfs
#

.globl main
main:
	sub	$sp, $sp, 4
	sw	$ra, 0($sp)	# save $ra on stack

	la	$t0, tree1
	li	$t1, 1
	sw	$t1, 4($t0)
	
	la	$t0, tree2
	sw	$t1, 124($t0)
	
	la	$t0, tree3
	sw	$t1, 28($t0)
	sw	$t1, 256($t0)

 	la	$a0, tree1     
		li $a1, 1
		li $a2, 1
		jal	dfs
		move	$a0, $v0
		jal	print_int_and_space            # this should print 0

  	la	$a0, tree2     
		li $a1, 1
		li $a2, 1
		jal	dfs
		move	$a0, $v0
		jal	print_int_and_space            # this should print 4

  	la	$a0, tree3     
		li $a1, 1
		li $a2, 1
		jal	dfs
		move	$a0, $v0
		jal	print_int_and_space            # this should print 6


	lw	$ra, 0($sp)
	add	$sp, $sp, 4
	jr	$ra


