/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: FuncoesAuxiliares
* Funcao...........: Codifica e decodifica bits, transformas arrays em strings, enquadra os bits, a parte mais "calculo" ou "reutilizavel" do programa
*************************************************************** */
package utils;

import java.util.ArrayList;
import java.util.Arrays;

import controller.TelaPrincipalController;

public class FuncoesAuxiliares {
  /**************************************************************
  * Metodo: arrayToString
  * Funcao: transforma o array de bits em uma string para ser apresentada na caixa de texto
  * @param int[] array | bits
  * @return String builder | string do array de bits
  * ********************************************************* */
  public String arrayToString(int[] array)
  {
    StringBuilder builder = new StringBuilder(); //Cria o builder da string
    //Loop para transformar os bits do array em string
    for(int bit : array)
    {
      builder.append(bit);
    }
    return builder.toString();
  } // Fim do metodo

  /**************************************************************
  * Metodo: arrayListToArrayInt
  * Funcao: transforma o arrayList em um array de int
  * @param int[] array | bits
  * @return int[] array | array int
  * ********************************************************* */
  public int[] arrayListToArrayInt(ArrayList<Integer> list)
  {
    int[] array = new int[list.size()]; //declara o array int que a funcao vai retornar
    //Loop para preencher o array
    for(int i = 0; i < list.size(); i++)
    {
      array[i] = list.get(i);
    }

    return array;
  } // Fim do metodo

  /**************************************************************
  * Metodo: addAll
  * Funcao: transforma o arrayList em um array de int
  * @param ArrayList<Integer> list | lista que vamos pegar os elementos
  * @param int[] array | bits
  * @return void
  * ********************************************************* */
  public void addAll(ArrayList<Integer> list, int[] array)
  {
    for(int bit : array)
    {
      list.add(bit);
    }
  } // Fim do metodo


  /**************************************************************
  * Metodo: codificacaoBinaria
  * Funcao: envia a mensagem (em bits) codificada em binario para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] bits | a mensagem eh igual aos bits em binario 
  * ********************************************************* */
  public int[] codificacaoBinaria(int[] bits) {
    return bits; // Em binario ja eh igual aos bits
  } // Fim do metodo

  /**************************************************************
  * Metodo: codificacaoManchester
  * Funcao: envia a mensagem (em bits) codificada em manchester para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] manchester | a mensagem codificada em manchester
  * ********************************************************* */
  public int[] codificacaoManchester(int[] bits)
  {
  // A lista eh mais flexivel para tamanhos variaveis
    ArrayList<Integer> manchester = new ArrayList<>();
    //Loop para codificar atualizado com o enquadramento
    for (int bit : bits) {
      switch (bit) {
        case 0: // Regra normal para o bit 0
          manchester.add(0);
          manchester.add(1);
          break;
        case 1: // Regra normal para o bit 1
          manchester.add(1);
          manchester.add(0);
          break;
        case 2: // Violacao de inicio
          manchester.add(0);
          manchester.add(0); // Sinal baixo-baixo (ilegal)
          break;
        case 3: // Violacao de fim
          manchester.add(1);
          manchester.add(1); // Sinal alto-alto (ilegal)
          break;
      }
    }
    // Converte o ArrayList de volta para um array de int
    return arrayListToArrayInt(manchester); 
  } // Fim do metodo

  /**************************************************************
  * Metodo: codificacaoManchesterDiferencial
  * Funcao: envia a mensagem (em bits) codificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] codificacaoManchesterDiferencial(int[] bits)
  {
    ArrayList<Integer> diferencial = new ArrayList<>(); // Cria o array para armazenar a mensagem codificada em manchester diferencial
    int nivelSinalAtual = 1; // Nivel de sinal que comecamos
    //Loop para codificar em manchester diferencial
    for(int bit: bits)
    {
      switch(bit)
      {
        case 0:
          // Para o bit 0, ha uma transicao no inicio do periodo
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          // Transicao obrigatoria no meio do periodo
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          break;
        case 1:
          // Para o bit 1, NAO ha transicao no inicio do periodo
          diferencial.add(nivelSinalAtual);
          // Transicao obrigatoria no meio do periodo
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          break;
        case 2: // Por causa da violacao fisica
          // Forca o sinal ilegal baixo-baixo
          diferencial.add(0);
          diferencial.add(0);
          nivelSinalAtual = 0;
          break;
        case 3:
          //Logica inversa ao 2
          diferencial.add(1);
          diferencial.add(1);
          nivelSinalAtual = 1;
          break;
      }
    }

    return arrayListToArrayInt(diferencial); 
  } // Fim do metodo

  /**************************************************************
  * Metodo: decodificacaoBinaria
  * Funcao: envia a mensagem (em bits) decodificada em binario para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] bits | a mensagem eh igual aos bits em binario 
  * ********************************************************* */
  public int[] decodificacaoBinaria(int[] bits) {
    return bits; // Em binario ja eh igual aos bits
  } // Fim do metodo

  /**************************************************************
  * Metodo: decodificacaoManchester
  * Funcao: envia a mensagem (em bits) decodificada em manchester para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] manchester | a mensagem codificada em manchester
  * ********************************************************* */
  public int[] decodificacaoManchester(int[] bits)
  {
    ArrayList<Integer> bitsDecodificados = new ArrayList<>();
        
        // Processa os bits em pares
        for (int i = 0; i < bits.length; i += 2) {
            int bit1 = bits[i];
            int bit2 = bits[i + 1];

            if (bit1 == 0 && bit2 == 1) {
                bitsDecodificados.add(0); // Transicao baixo-alto -> bit 0
            } else if (bit1 == 1 && bit2 == 0) {
                bitsDecodificados.add(1); // Transicao alto-baixo -> bit 1
            } else if (bit1 == 0 && bit2 == 0) {
                bitsDecodificados.add(2); // Marcador de inicio
            } else if (bit1 == 1 && bit2 == 1) {
                bitsDecodificados.add(3); // Marcador de fim
            }
        }
        return arrayListToArrayInt(bitsDecodificados);
  } // Fim do metodo

  /**************************************************************
  * Metodo: decodificacaoDiferencial
  * Funcao: envia a mensagem (em bits) decodificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] decodificacaoDiferencial(int[] bits)
  {
    ArrayList<Integer> diferencial = new ArrayList<>();
    int ultimoNivelDeSinal = 1; //mesmo nivel de sinal anterior
    //Loop para decodificar os bits
    for(int i = 0; i < bits.length; i += 2)
    {
      int primeiroSinal = bits[i];
      int segundoSinal = bits[i+1];
      // Se o nivel do sinal no inicio do bit for diferente do nivel no final do bit anterior, foi um 0
      if(primeiroSinal == segundoSinal) 
      {
        if(primeiroSinal == 0)
        {
          diferencial.add(2);
        }
        else
        {
          diferencial.add(3);
        }
      }
      else 
      {
        if(primeiroSinal != ultimoNivelDeSinal) diferencial.add(0);
        else diferencial.add(1);
      }
      // O ultimo nivel deste bit eh o segundo sinal do par
      ultimoNivelDeSinal = segundoSinal;
    }
    return arrayListToArrayInt(diferencial);
  } // Fim do metodo

  /**************************************************************
  * Metodo: contagemCaracteres
  * Funcao: envia a mensagem (em bits) enquadrada por contagem de caracteres para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] caracteres | a mensagem enquadradada em contagem de caracteres
  * ********************************************************* */
  public int[] contagemCaracteres(int[] bits)
  {
    int numeroBytes = bits.length / 8; // Conta quantos bytes a mensagem tem
    String binarioDoTamanho= String.format("%8s", Integer.toBinaryString(numeroBytes)).replace(' ', '0'); // Converte o numero de bytes para um array de 8 bits

    int[] cabecalho = new int[8]; //Cria um array para conseguirmos enquadrar os bits
    //Loop para enquadrar
    for(int i = 0; i < 8; i++)
    {
      cabecalho[i] = Character.getNumericValue(binarioDoTamanho.charAt(i));
    }

    int[] caracteres = new int[bits.length + 8]; // Cria o array que vai ser retornado pela funcao
    System.arraycopy(cabecalho, 0, caracteres, 0, 8);
    System.arraycopy(bits, 0, caracteres, 8, bits.length);

    return caracteres; //retorna o quadro enquadrado
  } // Fim do metodo

  /**************************************************************
  * Metodo: insercaoBytes
  * Funcao: envia a mensagem (em bits) enquadrada por insercao de bytes para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] insercaoBytes | a mensagem enquadradada em insercao de bytes
  * ********************************************************* */
  public int[] insercaoBytes(int[] bits)
  {
    ArrayList<Integer> quadroComStuffing = new ArrayList<>(); // Cria o quadro com stuffing
    int[] FLAG = {0,1,1,1,1,1,1,0}; // Cria a FLAG 0x7E
    int[] ESC = {0,1,1,1,1,1,0,1}; // Cria a ESC 0x7D

    addAll(quadroComStuffing, FLAG); // Adiciona a FLAG no inicio

    //Loop para enquadrar
    for(int i = 0; i < bits.length; i += 8)
    {
      int[] chunk = Arrays.copyOfRange(bits, i, i + 8);
      if(Arrays.equals(chunk, FLAG) || Arrays.equals(chunk, ESC))
      {
        addAll(quadroComStuffing, ESC); // Insere o byte de escape
      }
      addAll(quadroComStuffing, chunk); // Adiciona o byte original
    }
    addAll(quadroComStuffing, FLAG); // Adiciona a FLAG no final

    return arrayListToArrayInt(quadroComStuffing);
  } // Fim do metodo

  /**************************************************************
  * Metodo: insercaoBits
  * Funcao: envia a mensagem (em bits) enquadrada por insercao de bits para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] insercaoBytes | a mensagem enquadradada em insercao de bits
  * ********************************************************* */
  public int[] insercaoBits(int[] bits)
  {
    ArrayList<Integer> quadroComStuffing = new ArrayList<>(); // Cria o quadro com stuffing
    int[] FLAG = {0,1,1,1,1,1,1,0}; // Cria a FLAG 0x7E
    int contadorDeUns = 0; // Cria um contador para 1's

    addAll(quadroComStuffing, FLAG); // Adiciona a FLAG no inicio

    //Loop para enquadrar
    for(int bit : bits)
    {
      quadroComStuffing.add(bit);
      if(bit == 1)
      {
        contadorDeUns++;
        if(contadorDeUns == 5)
        {
          quadroComStuffing.add(0); //Adiciona o bit 0 (stuffing)
          contadorDeUns = 0; // Zera o contador
        }
      }
      else 
      {
        contadorDeUns = 0;
      }
    }

    addAll(quadroComStuffing, FLAG); // Adiciona a FLAG no final

    return arrayListToArrayInt(quadroComStuffing);
  } // Fim do metodo

  /**************************************************************
  * Metodo: violacaoFisica
  * Funcao: envia a mensagem (em bits) enquadrada por Violacao da camada fisica para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] violacao | a mensagem enquadradada em Violacao da camada fisica
  * ********************************************************* */
  public int[] violacaoFisica(int[] bits)
  {
    // 2 = sinal J
    // 3 = sinal K
    int[] violacao =new int[bits.length + 2]; // Cria o array que vamos retornar
    violacao[0] = 2; // Marcador de inicio
    System.arraycopy(bits, 0, violacao, 1, bits.length);
    
    violacao[violacao.length - 1] = 3; // Marcador de fim
    return violacao;
  } // FIm do Metodo

  /**************************************************************
  * Metodo: desenquadroViolacaoFisica
  * Funcao: envia a mensagem (em bits) desenquadrada por Violacao da camada fisica para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] violacao | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroViolacaoFisica(int[] quadroEnquadrado)
  {
    //Verificando a violacao da camada fisica
    if(quadroEnquadrado.length >= 2 && quadroEnquadrado[0] == 2 && quadroEnquadrado[quadroEnquadrado.length - 1] == 3)
    {
      int[] violacao = new int[quadroEnquadrado.length - 2];
      System.arraycopy(quadroEnquadrado, 1, violacao, 0, violacao.length);
      return violacao;
    }
    // Caso nao detecte nada, houve um erro
    return new int[0];
  } // Fim do metodo

  /**************************************************************
  * Metodo: desenquadroContagemCaracteres
  * Funcao: envia a mensagem (em bits) desenquadrada por Contagem de Caracteres para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroContagemCaracteres(int[] quadroEnquadrado)
  {
    //le os primeiros 8 bits para saber o tamanho
    int[] cabecalho = Arrays.copyOfRange(quadroEnquadrado, 0, 8);
    StringBuilder binarioDoTamanho = new StringBuilder();
    // Percorre e transforma o tamanho em binario
    for(int bit : cabecalho)
    {
      binarioDoTamanho.append(bit); // Forma a string
    }

    int tamanho = Integer.parseInt(binarioDoTamanho.toString(), 2); // Retorna o tamanho
    return Arrays.copyOfRange(quadroEnquadrado, 8, 8 + (tamanho * 8)); // Retorna o quadro desenquadrado
  } // Fim do metodo

  /**************************************************************
  * Metodo: desenquadroInsercaoBits
  * Funcao: envia a mensagem (em bits) desenquadrada por Insercao de bits para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] quadroSemStuffing | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroInsercaoBits(int[] quadroEnquadrado)
  {
    ArrayList<Integer> quadroSemStuffing = new ArrayList<>(); // Declara o array que vai armazenar o quadro de bits desenquadrado
    int contadorDeUns = 0;
    // Ignora a FLAG inicial e inicia o Loop para desenquadrar
    for(int i = 8; i < quadroEnquadrado.length - 8; i++)
    {
      int bit = quadroEnquadrado[i];
      if(contadorDeUns == 5 & bit == 0)
      {
        contadorDeUns = 0; //eh um bit de stuffing, entao ignoramos
      }
      else
      {
        quadroSemStuffing.add(bit);
        if(bit == 1) contadorDeUns++;
        else contadorDeUns = 0;
      }
    }

    return arrayListToArrayInt(quadroSemStuffing);
  } // Fim do metodo

  /**************************************************************
  * Metodo: desenquadroInsercaoBytes
  * Funcao: envia a mensagem (em bits) desenquadrada por Insercao de bytes para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] quadroSemStuffing | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroInsercaoBytes(int[] quadroEnquadrado)
  {
    ArrayList<Integer> quadroSemStuffing = new ArrayList<>(); // Declara o array que vai armazenar o quadro de bits desenquadrado
    int[] FLAG = {0,1,1,1,1,1,1,0}; // FLAG reversa
    int[] ESC = {0,1,1,1,1,1,0,1}; // ESC reverso

    // Ignora a FLAG inicial e final
    for(int i = 8; i < quadroEnquadrado.length - 8; i += 8)
    {
      int[] chunk = Arrays.copyOfRange(quadroEnquadrado, i, i + 8); // Cria o chunk
      //verifica se o chunk é um ESC
      if(Arrays.equals(chunk, ESC))
      {
        i += 8;
        int[] proximoChunk = Arrays.copyOfRange(quadroEnquadrado, i, i + 8);
        addAll(quadroSemStuffing, proximoChunk);
      }
      else
      {
        addAll(quadroSemStuffing, chunk);
      }
    }

    return arrayListToArrayInt(quadroSemStuffing);
  } // Fim do metodo

  /**************************************************************
  * Metodo: paridadePar
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadePar(int[] quadro)
  {
    int[] par = new int[quadro.length + 1];
    int contadorDeUns = 0; // Cria um contador para verificarmos quantos 1 tem
    //Loop para contar os bits 1 do quadro original
    for(int bit : quadro)
    {
      //Se for 1, aumenta o contador
      if(bit == 1)
      {
        contadorDeUns++;
      }
    }

    System.arraycopy(quadro, 0, par, 0, quadro.length);

    // Se a contagem for impar ou par, muda bit de paridade
    if(contadorDeUns % 2 != 0)
    {
      par[quadro.length] = 1;
    }
    else
    {
      par[quadro.length] = 0;
    }
    return par;

  } // Fim do metodo

  /**************************************************************
  * Metodo: paridadeParVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeParVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    int[] par = new int[quadro.length - 1];
    int contadorDeUns = 0; // Cria um contador para verificarmos quantos 1 tem
    //Loop para contar os bits 1 do quadro original
    for(int bit : quadro)
    {
      //Se for 1, aumenta o contador
      if(bit == 1)
      {
        contadorDeUns++;
      }
    }

    if(contadorDeUns % 2 == 0)
    {
      par = Arrays.copyOfRange(quadro, 0, quadro.length - 1); // Remove o bit de paridade
      return par;
    }
    else
    {
      return null; // Descarta o fluxo
    }
  } // Fim do metodo

  /**************************************************************
  * Metodo: paridadeImpar
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeImpar(int[] quadro)
  {
  int[] impar = new int[quadro.length + 1];
  int contadorDeUns = 0; // Cria um contador para verificarmos quantos 1 tem
  //Loop para contar os bits 1 do quadro original
  for(int bit : quadro)
  {
    //Se for 1, aumenta o contador
    if(bit == 1)
    {
      contadorDeUns++;
    }
  }
  System.arraycopy(quadro, 0, impar, 0, quadro.length);

  // Se a contagem for impar ou par, muda bit de paridade
  if(contadorDeUns % 2 != 0)
  {
    impar[quadro.length] = 0;
  }
  else
  {
    impar[quadro.length] = 1;
  }
  return impar;
  } // Fim do metodo

  /**************************************************************
  * Metodo: paridadeImparVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeImparVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    int[] impar = new int[quadro.length - 1];
    int contadorDeUns = 0; // Cria um contador para verificarmos quantos 1 tem
    //Loop para contar os bits 1 do quadro original
    for(int bit : quadro)
    {
      //Se for 1, aumenta o contador
      if(bit == 1)
      {
        contadorDeUns++;
      }
    }

    if(contadorDeUns % 2 != 0)
    {
      impar = Arrays.copyOfRange(quadro, 0, quadro.length - 1); // Remove o bit de paridade
      return impar;
    }
    else
    {
      return null; // Descarta o fluxo
    }
  } // Fim do metodo

  /**************************************************************
  * Metodo: crc
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] crc(int[] quadro)
  {
    int[] crc = quadro;
    return crc;
  } // Fim do metodo

  /**************************************************************
  * Metodo: crcVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] crcVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    int[] crc = quadro;
    return crc;
  } // Fim do metodo
  
  /**************************************************************
  * Metodo: hamming
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] hamming(int[] quadro)
  {
    int[] hamming = quadro;
    return hamming;
  } // Fim do metodo

  /**************************************************************
  * Metodo: hammingVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] hammingVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    int[] hamming = quadro;
    return hamming;
  } // Fim do metodo
}