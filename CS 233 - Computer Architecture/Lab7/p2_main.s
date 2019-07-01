.data

# syscall constants
PRINT_INT = 1
PRINT_CHAR = 11

.globl symbollist
symbollist: .ascii  "0123456789ABCDEFG"

board1:
.half      4 65535  1024    16 65535 65535   256    64  2048 32768   128 65535     8 16384 65535 65535 
.half  32768    64 65535  8192     8  2048 65535   512   256 16384 65535     2    16  4096 65535 65535 
.half     32   512 16384 65535 32768  4096 65535    16     1  1024    64     8 65535 65535  2048   256 
.half   4096     8   256  2048 65535 16384     1 65535    32   512  8192    16 65535 65535     4 32768 
.half  65535    32  4096 16384  2048 65535 65535 65535 65535     4   512 65535   128     2   256     8 
.half   2048     1 65535     4   256     8   512   128 65535     2  1024 65535    32    64 65535    16 
.half  65535   256     2 65535 65535 32768 16384     4     8  4096    32 65535  8192     1   512 65535 
.half    128  8192   512 65535  4096    64 65535    32    16     1  2048   256 65535 65535 16384 65535 
.half  65535 65535 65535   256    16    32  1024     2 65535  2048 16384    64  4096   512  8192     1 
.half      1  2048 65535     2   512 65535 65535     8  1024    16 65535 65535 65535    32    64 16384 
.half  16384  4096 65535 65535 65535   128 32768  2048 65535 65535     1    32     4    16     8 65535 
.half    512    16    64    32     1   256 65535 65535     2     8  4096     4 65535  2048 32768   128 
.half   8192 65535    16   512 16384     2 65535 32768     4 65535     8  2048     1 65535   128  4096 
.half    256     4 65535     1   128   512 65535  4096 65535    32     2  1024  2048 32768 65535  8192 
.half      2   128  2048 65535     4    16     8     1  4096 65535 65535 65535 16384  1024    32 65535 
.half  65535 16384    32  4096 65535  1024  2048   256 32768   128 65535     1   512     8     2     4 

board2:
.half      4     2  1024    16    32  8192   256    64  2048 32768   128  4096     8 16384     1   512 
.half  32768    64     1  8192     8  2048   128   512   256 16384     4     2    16  4096  1024    32 
.half     32   512 16384   128 32768  4096     4    16     1  1024    64     8     2  8192  2048   256 
.half   4096     8   256  2048     2 16384     1  1024    32   512  8192    16    64   128     4 32768 
.half   1024    32  4096 16384  2048     1    16  8192    64     4   512 32768   128     2   256     8 
.half   2048     1 32768     4   256     8   512   128  8192     2  1024 16384    32    64  4096    16 
.half     16   256     2    64  1024 32768 16384     4     8  4096    32   128  8192     1   512  2048 
.half    128  8192   512     8  4096    64     2    32    16     1  2048   256 32768     4 16384  1024 
.half      8 32768     4   256    16    32  1024     2   128  2048 16384    64  4096   512  8192     1 
.half      1  2048   128     2   512     4  4096     8  1024    16 32768  8192   256    32    64 16384 
.half  16384  4096  8192  1024    64   128 32768  2048   512   256     1    32     4    16     8     2 
.half    512    16    64    32     1   256  8192 16384     2     8  4096     4  1024  2048 32768   128 
.half   8192  1024    16   512 16384     2    32 32768     4    64     8  2048     1   256   128  4096 
.half    256     4     8     1   128   512    64  4096 16384    32     2  1024  2048 32768    16  8192 
.half      2   128  2048 32768     4    16     8     1  4096  8192   256   512 16384  1024    32    64 
.half     64 16384    32  4096  8192  1024  2048   256 32768   128    16     1   512     8     2     4 


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
#  this is a function that will test board_done and print_board
#

.globl main
main:
	sub	$sp, $sp, 4
	sw	$ra, 0($sp)		# save $ra on stack


	la	$a0, board2		# test board_done (true case)
	jal	board_done
	move	$a0, $v0
	jal	print_int_and_space	# this should print 1
	

	la	$a0, board1		# test board_done (false case)
	jal	board_done
	move	$a0, $v0
	jal	print_int_and_space	# this should print 0


	li	$v0, PRINT_CHAR		# print a newline
	li	$a0, '\n'
	syscall	


	la	$a0, board1		# test print_board
	jal	print_board

	# should print the following:
	# 3*B5**97CG8*4F**
	# G7*E4C*A9F*25D**
	# 6AF*GD*51B74**C9
	# D49C*F1*6AE5**3G
	# *6DFC****3A*8294
	# C1*394A8*2B*67*5
	# *92**GF34D6*E1A*
	# 8EA*D7*651C9**F*
	# ***956B2*CF7DAE1
	# 1C*2A**4B5***67F
	# FD***8GC**16354*
	# A57619**24D3*CG8
	# E*5AF2*G3*4C1*8D
	# 93*18A*D*62BCG*E
	# 28C*3541D***FB6*
	# *F6D*BC9G8*1A423


	lw	$ra, 0($sp)
	add	$sp, $sp, 4
	jr	$ra
