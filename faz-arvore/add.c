int test_func(int a, int b){
    int c = a + b;
    return c;
}

int main(){
    int x = 2.34;
    float z = test_func(1, 2) + x;
    return 0;
}