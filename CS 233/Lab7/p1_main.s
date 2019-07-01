# syscall constants
PRINT_INT = 1
PRINT_CHAR = 11

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
#  this will test has_single_bit_set and get_lowest_set_bit
#

.globl main
main:
	sub	$sp, $sp, 4
	sw	$ra, 0($sp)		# save $ra on stack

	
	li	$a0, 0x010		# test has_single_bit_set (true case)
	jal	has_single_bit_set
	move	$a0, $v0
	jal	print_int_and_space	# this should print 1


	li	$a0, 0x10b		# test has_single_bit_set (false case)
	jal	has_single_bit_set
	move	$a0, $v0
	jal	print_int_and_space	# this should print 0


	li	$a0, 0x010		# test get_lowest_set_bit 
	jal	get_lowest_set_bit
	move	$a0, $v0
	jal	print_int_and_space	# this should print 4


	li	$a0, 0x008		# test get_lowest_set_bit 
	jal	get_lowest_set_bit
	move	$a0, $v0
	jal	print_int_and_space	# this should print 3


	lw	$ra, 0($sp)
	add	$sp, $sp, 4
	jr	$ra
