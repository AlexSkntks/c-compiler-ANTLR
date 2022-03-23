.data
x: 0
y: 0
z: 0

.text

li $t1, 1
sw $t1, x # x = 1;

# y =  x + 2

lw $t1, x		 # t1 <- x
li $t2, 2		 # t2 <- 2
add $t3, $t1, $t2	 # t3 <- x + 2
sw $t3, y		 # y <- 3

# z = x + y

lw $t1, x 		# t1 <- x
lw $t2, y 		# t2 <- y
add $t3, $t1, $t2 	# t3 <- x+y = 4
sw $t3, z 		# z <- 4