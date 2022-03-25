.data
a: 0
x: 0
z: 0
.text
 # assign 
# char to float
li $t0, 'a'
li $t0, 97
mtc1 $t0, $f0
cvt.s.w $f0, $f0
s.s $f0, a
 # assign 
# float to int
 # sum
lw $t1, a
# int to float
li $t0, 1
li $t0, 1
mtc1 $t0, $f2
cvt.s.w $f2, $f2
add.s $f3, $f2, $f1
li $t0, 98
sw $t0, x
 # assign 
lw $t1, x
sw $t1, z