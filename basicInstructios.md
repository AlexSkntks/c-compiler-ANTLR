# Instruções MIPS a serem usadas


Coloca em rt o conteúdo da variável
    
    lw rt, label

--- 

Coloca o valor value no registrador
    
    li rt, value

---

Coloca o a soma de r2 e r3 em r1

    add r1, r2, r3

---
Coloca o conteúdo do registrador r1 na posição de memória representado por label
    
    sw r1, label

---

Coloca o endereço da variável no registrador

    la r1, label

---

Carrega o valor do registrador (float) para a memória

        s.s rt, label
        s.s rt, position

---

Carrega o valor da memória para o registrador (float)

        l.s rt, label
        l.s rt, position

---

Move o valor de rt para um registrador de ponto flutuante
        
        mct1 rt, floatRegister

---

Converte o valor inteiro do floatRegister1 e coloca em floatRegister0

        cvt.s.w floatRegister0 floatRegister1