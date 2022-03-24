.data
x: 0
y: 0
.text
li $t0, 'a'
sw $t0, x
li $t1, 'g'
# The MIPS compiler doesn't haveinstructions to set a single floating point with a immediate value
# Then the following instructions are really important.
li $t1, 103
mtc1 $t1, $f0
cvt.s.w $f0, $f0
s.s $f0, y