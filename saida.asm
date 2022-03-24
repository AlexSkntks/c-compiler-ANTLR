.data
x: 3
a: 2
y: 0
.text
 # assign 
li $t0, 3
sw $t0, x
 # assign 
li $t1, 2
sw $t1, a
 # assign 
 # times
 # sum
li $t2, 1
li $t3, 5
add $t4, $t3, $t2
li $t2, '3'
mult $t2, $t4
mflo $t3
sw $t3, y
