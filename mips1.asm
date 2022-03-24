.data
x: 0
y: 0
z: 0
.text
li $t0, 'a'
sw $t0, x
li $t1, 1
li $t2, 3
add $t3, $t2, $t1
# The MIPS compiler doesn't haveinstructions to set a single floating point with a immediate value
# Then the following instructions are really important.
li $t1, 4
mtc1 $t1, $f0
cvt.s.w $f0, $f0
s.s $f0, y
lw $t1, y
mfc1 $t1, $f1
sw $t1, z