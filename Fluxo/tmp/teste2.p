{Programa teste} 
program teste; 
 
{Declaracao de variaveis globais:} 
 
var 
      valor1, b, aux: integer; 
      valor: real; 
      valor3, valor12 : integer; 
 
{Procedimento compara:} 
procedure compara(alfa, beta, teta: boolean ; zeta, gamma: integer); 
	var aux1, aux2: integer; 
	begin 
		aux1:=zeta-gamma; 
		aux2 := gamma - zeta; 
		if aux1 > aux2 then 
			alfa := true 
		; 
		if aux1 < aux2 then 
			aux1 := aux2 + aux1 
		else 
			aux := 4235 
	end 
; 
{ 
$ 
{Procedimento inicializa:} 
procedure inicializa; 
	begin 
		valor1 := 0; 
		b := 0; 
		aux := 0 
	end 
; 
 
{Comando composto main:} 
begin 
	inicializa; 
	valor1 := 10; 
   { valor3 := valor1*(2*valor1) valor2 valor1 *; 
    valor2 := valor3 / (valor1 * 3) + 4.5;{deve dar erro por causa desse ponto e virgula} 
    if valor1 >= valor1 then 
		valor1 := valor1 

    else 
		valor1 := valor1 ;

compara (true, false, true, valor1, valor1) 
end 
{Final do programa:} 
.