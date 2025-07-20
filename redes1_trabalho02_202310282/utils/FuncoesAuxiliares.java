/***************************************************************** 
Autor..............: Lucas de Menezes Chaves
* Matricula........: 202310282
* Inicio...........: 
* Ultima alteracao.: 
* Nome.............: FuncoesAuxiliares
* Funcao...........: Codifica e decodifica bits, e transformas arrays em strings
*************************************************************** */
package utils;

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
    int[] manchester = new int[bits.length * 2]; // Cria um array para armazenar a mensagem codificada
    //Loop para realizar a codificacao
    for(int i = 0; i < bits.length; i++)
    {
      //O bit 0 eh representado por uma transicao de baixo para alto (01)
      if(bits[i] == 0) 
      {
        manchester[i * 2] = 0;
        manchester[i * 2 + 1] = 1;
      }
      // O bit 1 eh representado por uma transicao de alto para baixo (10)
      else
      {
        manchester[i * 2] = 1;
        manchester[i * 2 + 1] = 0;
      }
    }
    return manchester;
  } // Fim do metodo

  /**************************************************************
  * Metodo: codificacaoManchesterDiferencial
  * Funcao: envia a mensagem (em bits) codificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] codificacaoManchesterDiferencial(int[] bits)
  {
    int[] diferencial = new int[bits.length * 2]; // Cria o array para armazenar a mensagem codificada em manchester diferencial
    int nivelSinalAtual = 1; // Nivel de sinal que comecamos
    //Loop para codificar em manchester diferencial
    for(int i = 0; i < bits.length; i++)
    {
      if(bits[i] == 0) nivelSinalAtual = 1 - nivelSinalAtual; // Inverte o sinal no bit 0, no bit 1 nao ocorre transicao
      diferencial[i * 2] = nivelSinalAtual;
      nivelSinalAtual = 1 - nivelSinalAtual; // Inverte para segunda metade
      diferencial[i * 2 + 1] = nivelSinalAtual;
    }

    return diferencial; 
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
    int[] manchester = new int[bits.length / 2]; // Cria um array para armazenar a mensagem codificada
    //Loop para realizar a codificacao
    for(int i = 0; i < manchester.length; i++)
    {
      //se o par de bits for 01, o bit original eh 0, se for 10, eh 1
      if(bits[i * 2] == 0 && bits[i * 2 + 1] == 1) manchester[i] = 0;
      else manchester[i] = 1;
    }
    return manchester;
  } // Fim do metodo

  /**************************************************************
  * Metodo: decodificacaoDiferencial
  * Funcao: envia a mensagem (em bits) decodificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] decodificacaoDiferencial(int[] bits)
  {
    int[] diferencial = new int[bits.length / 2];
    int ultimoNivelDeSinal = 1; //mesmo nivel de sinal anterior
    //Loop para decodificar os bits
    for(int i = 0; i < diferencial.length; i++)
    {
      int primeiroNivelDeSinal = bits[i * 2];
      // Se o nivel do sinal no inicio do bit for diferente do nivel no final do bit anterior, foi um 0
      if(primeiroNivelDeSinal != ultimoNivelDeSinal) diferencial[i] = 0;
      else diferencial[i] = 1;
      // O ultimo nivel deste bit eh o segundo sinal do par
      ultimoNivelDeSinal = bits[i * 2 + 1];
    }
    return diferencial;
  }
}
