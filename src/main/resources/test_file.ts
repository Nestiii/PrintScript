// @ts-nocheck
let a: number = 2.0;
const b: string  = "hola";


print(a);
print (b);

a = 3;
print (a);
{
    let e: string = 'inside block';
    print (e);

}

if(a >= 3.0) {
    print("Entro");
} else {
    print("No Entro");
}
