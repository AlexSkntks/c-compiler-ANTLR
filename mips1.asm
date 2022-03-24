.data
x: 3
y: 0
.text
 # assign 
li $t0, 3
sw $t0, x
 # assign 
 # times
 # sum
li $t1, 1
li $t2, 5
add $t3, $t2, $t1
li $t1, '3'
mult $t1, $t3
mflo $t2
sw $t2, y