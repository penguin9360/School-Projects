.text

## bool has_single_bit_set(unsigned value) {  // returns 1 if a single bit is set
##   if (value == 0) {
##     return 0;   // has no bits set
##   }
##   if (value & (value - 1)) {
##     return 0;   // has more than one bit set
##   }
##   return 1;
## }

.globl has_single_bit_set

has_single_bit_set:

	beq  $a0, $0, RET0
	li  $t1, 1
	sub  $t0, $a0, $t1 # value - 1
	and  $t0, $a0, $t0 # value & (value - 1)
	bne  $t0, $zero, RET0 # value & (value - 1) == 1 -> return 0
	j RET1

RET0:

	li  $v0, 0
	jr	$ra

RET1:

	li  $v0, 1
	jr  $ra

## unsigned get_lowest_set_bit(unsigned value) {
##   for (int i = 0 ; i < 16 ; ++ i) {
##     if (value & (1 << i)) {          # test if the i'th bit position is set
##       return i;                      # if so, return i
##     }
##   }
##   return 0;
## }

.globl get_lowest_set_bit
get_lowest_set_bit:

	li  $t0, 0 # i = 0
	li  $t1, 16 # t1 = 16
	li  $t2, 1

LOOP:

		bge  $t0, $t1, RE0
		sll  $t3, $t2, $t0 # 1 << i
		and  $t4, $a0, $t3 # value & (1 << i)
		bne  $t4, $zero, REI
		add  $t0, $t0, 1 # ++i
		j LOOP
RE0:
		li  $v0, 0
		jr	$ra

REI:
		move  $v0, $t0
		jr	$ra
