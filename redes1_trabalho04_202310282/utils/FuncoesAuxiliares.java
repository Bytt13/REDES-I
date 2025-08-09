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
  // Flag para identificar um quadro de ACK. Uma sequencia de bits improvavel de ocorrer em dados normais.
  private static final int[] ACK_FLAG = {1,0,1,0,1,0,1,0};

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
    if (isQuadroAck(bits)) return bits; // ACKs nao sao codificados
    return bits;
  }

  /**************************************************************
  * Metodo: codificacaoManchester
  * Funcao: envia a mensagem (em bits) codificada em manchester para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] manchester | a mensagem codificada em manchester
  * ********************************************************* */
  public int[] codificacaoManchester(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao codificados

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
  }

  /**************************************************************
  * Metodo: codificacaoManchesterDiferencial
  * Funcao: envia a mensagem (em bits) codificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] codificacaoManchesterDiferencial(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao codificados

    ArrayList<Integer> diferencial = new ArrayList<>();
    int nivelSinalAtual = 1;
    for(int bit: bits)
    {
      switch(bit)
      {
        case 0:
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          break;
        case 1:
          diferencial.add(nivelSinalAtual);
          nivelSinalAtual = 1 - nivelSinalAtual;
          diferencial.add(nivelSinalAtual);
          break;
        case 2: diferencial.add(0); diferencial.add(0); nivelSinalAtual = 0; break;
        case 3: diferencial.add(1); diferencial.add(1); nivelSinalAtual = 1; break;
      }
    }
    return arrayListToArrayInt(diferencial);
  }

  /**************************************************************
  * Metodo: decodificacaoBinaria
  * Funcao: envia a mensagem (em bits) decodificada em binario para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] bits | a mensagem eh igual aos bits em binario
  * ********************************************************* */
  public int[] decodificacaoBinaria(int[] bits) {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao decodificados
    return bits;
  }

  /**************************************************************
  * Metodo: decodificacaoManchester
  * Funcao: envia a mensagem (em bits) decodificada em manchester para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] manchester | a mensagem codificada em manchester
  * ********************************************************* */
  public int[] decodificacaoManchester(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao decodificados

    ArrayList<Integer> bitsDecodificados = new ArrayList<>();
    for (int i = 0; i < bits.length; i += 2) {
        if (i + 1 >= bits.length) break;
        int bit1 = bits[i];
        int bit2 = bits[i + 1];

        if (bit1 == 0 && bit2 == 1) bitsDecodificados.add(0);
        else if (bit1 == 1 && bit2 == 0) bitsDecodificados.add(1);
        else if (bit1 == 0 && bit2 == 0) bitsDecodificados.add(2);
        else if (bit1 == 1 && bit2 == 1) bitsDecodificados.add(3);
    }
    return arrayListToArrayInt(bitsDecodificados);
  }

  /**************************************************************
  * Metodo: decodificacaoDiferencial
  * Funcao: envia a mensagem (em bits) decodificada em manchester diferencial para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] diferencial | a mensagem codificada em manchester diferencial
  * ********************************************************* */
  public int[] decodificacaoDiferencial(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao decodificados

    ArrayList<Integer> diferencial = new ArrayList<>();
    int ultimoNivelDeSinal = 1;
    for(int i = 0; i < bits.length; i += 2)
    {
      if (i + 1 >= bits.length) break;
      int primeiroSinal = bits[i];
      int segundoSinal = bits[i+1];
      if(primeiroSinal == segundoSinal)
      {
        if(primeiroSinal == 0) diferencial.add(2);
        else diferencial.add(3);
      }
      else
      {
        if(primeiroSinal != ultimoNivelDeSinal) diferencial.add(0);
        else diferencial.add(1);
      }
      ultimoNivelDeSinal = segundoSinal;
    }
    return arrayListToArrayInt(diferencial);
  }

  /**************************************************************
  * Metodo: contagemCaracteres
  * Funcao: envia a mensagem (em bits) enquadrada por contagem de caracteres para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] caracteres | a mensagem enquadradada em contagem de caracteres
  * ********************************************************* */
  public int[] contagemCaracteres(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao enquadrados
    int numeroBits = bits.length;
    String binarioDoTamanho = String.format("%16s", Integer.toBinaryString(numeroBits)).replace(' ', '0');
    int[] cabecalho = new int[16];
    for(int i = 0; i < 16; i++) {
      cabecalho[i] = Character.getNumericValue(binarioDoTamanho.charAt(i));
    }
    int[] caracteres = new int[bits.length + 16];
    System.arraycopy(cabecalho, 0, caracteres, 0, 16);
    System.arraycopy(bits, 0, caracteres, 16, bits.length);
    return caracteres;
  }

  /**************************************************************
  * Metodo: insercaoBytes
  * Funcao: envia a mensagem (em bits) enquadrada por insercao de bytes para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] insercaoBytes | a mensagem enquadradada em insercao de bytes
  * ********************************************************* */
  public int[] insercaoBytes(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao enquadrados
    ArrayList<Integer> quadroComStuffing = new ArrayList<>();
    int[] FLAG = {0,1,1,1,1,1,1,0};
    int[] ESC = {0,1,1,1,1,1,0,1};
    addAll(quadroComStuffing, FLAG);
    for(int i = 0; i < bits.length; i += 8) {
      int[] chunk = Arrays.copyOfRange(bits, i, Math.min(i + 8, bits.length));
      if(Arrays.equals(chunk, FLAG) || (chunk.length == 8 && Arrays.equals(chunk, ESC))) {
        addAll(quadroComStuffing, ESC);
      }
      addAll(quadroComStuffing, chunk);
    }
    addAll(quadroComStuffing, FLAG);
    return arrayListToArrayInt(quadroComStuffing);
  }

  /**************************************************************
  * Metodo: insercaoBits
  * Funcao: envia a mensagem (em bits) enquadrada por insercao de bits para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] insercaoBytes | a mensagem enquadradada em insercao de bits
  * ********************************************************* */
  public int[] insercaoBits(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao enquadrados
    ArrayList<Integer> quadroComStuffing = new ArrayList<>();
    int[] FLAG = {0,1,1,1,1,1,1,0};
    int contadorDeUns = 0;
    addAll(quadroComStuffing, FLAG);
    for(int bit : bits) {
      quadroComStuffing.add(bit);
      if(bit == 1) {
        contadorDeUns++;
        if(contadorDeUns == 5) {
          quadroComStuffing.add(0);
          contadorDeUns = 0;
        }
      } else {
        contadorDeUns = 0;
      }
    }
    addAll(quadroComStuffing, FLAG);
    return arrayListToArrayInt(quadroComStuffing);
  }

  /**************************************************************
  * Metodo: violacaoFisica
  * Funcao: envia a mensagem (em bits) enquadrada por Violacao da camada fisica para a proxima camada
  * @param int[] bits | mensagem recebida (em bits)
  * @return int[] violacao | a mensagem enquadradada em Violacao da camada fisica
  * ********************************************************* */
  public int[] violacaoFisica(int[] bits)
  {
    if (isQuadroAck(bits)) return bits; // ACKs nao sao enquadrados
    int[] violacao =new int[bits.length + 2];
    violacao[0] = 2; // Marcador de inicio (J)
    System.arraycopy(bits, 0, violacao, 1, bits.length);
    violacao[violacao.length - 1] = 3; // Marcador de fim (K)
    return violacao;
  }

  /**************************************************************
  * Metodo: desenquadroViolacaoFisica
  * Funcao: envia a mensagem (em bits) desenquadrada por Violacao da camada fisica para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] violacao | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroViolacaoFisica(int[] quadroEnquadrado)
  {
    if (isQuadroAck(quadroEnquadrado)) return quadroEnquadrado; // ACKs nao sao desenquadrados
    if(quadroEnquadrado.length >= 2 && quadroEnquadrado[0] == 2 && quadroEnquadrado[quadroEnquadrado.length - 1] == 3) {
      return Arrays.copyOfRange(quadroEnquadrado, 1, quadroEnquadrado.length - 1);
    }
    return quadroEnquadrado;
  }

  /**************************************************************
  * Metodo: desenquadroContagemCaracteres
  * Funcao: envia a mensagem (em bits) desenquadrada por Contagem de Caracteres para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroContagemCaracteres(int[] quadroEnquadrado)
  {
    if (isQuadroAck(quadroEnquadrado)) return quadroEnquadrado; // ACKs nao sao desenquadrados
    if (quadroEnquadrado.length < 16) return quadroEnquadrado;

    int[] cabecalho = Arrays.copyOfRange(quadroEnquadrado, 0, 16);
    StringBuilder binarioDoTamanho = new StringBuilder();
    for(int bit : cabecalho) {
      binarioDoTamanho.append(bit);
    }
    int tamanhoEmBits = Integer.parseInt(binarioDoTamanho.toString(), 2);
    if (quadroEnquadrado.length < 16 + tamanhoEmBits) return new int[0];
    return Arrays.copyOfRange(quadroEnquadrado, 16, 16 + tamanhoEmBits);
  }

  /**************************************************************
  * Metodo: desenquadroInsercaoBits
  * Funcao: envia a mensagem (em bits) desenquadrada por Insercao de bits para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] quadroSemStuffing | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroInsercaoBits(int[] quadroEnquadrado)
  {
    if (isQuadroAck(quadroEnquadrado)) return quadroEnquadrado; // ACKs nao sao desenquadrados
    ArrayList<Integer> quadroSemStuffing = new ArrayList<>();
    int[] FLAG = {0,1,1,1,1,1,1,0};
    if (quadroEnquadrado.length < 16 || !Arrays.equals(Arrays.copyOfRange(quadroEnquadrado, 0, 8), FLAG) || !Arrays.equals(Arrays.copyOfRange(quadroEnquadrado, quadroEnquadrado.length - 8, quadroEnquadrado.length), FLAG)) {
        return quadroEnquadrado;
    }
    int contadorDeUns = 0;
    for(int i = 8; i < quadroEnquadrado.length - 8; i++) {
      int bit = quadroEnquadrado[i];
      if(contadorDeUns == 5 && bit == 0) {
        contadorDeUns = 0;
      } else {
        quadroSemStuffing.add(bit);
        if(bit == 1) contadorDeUns++;
        else contadorDeUns = 0;
      }
    }
    return arrayListToArrayInt(quadroSemStuffing);
  }

  /**************************************************************
  * Metodo: desenquadroInsercaoBytes
  * Funcao: envia a mensagem (em bits) desenquadrada por Insercao de bytes para a proxima camada
  * @param int[] quadroEnquadrado | mensagem recebida (em bits)
  * @return int[] quadroSemStuffing | a mensagem deenquadradada
  * ********************************************************* */
  public int[] desenquadroInsercaoBytes(int[] quadroEnquadrado)
  {
    if (isQuadroAck(quadroEnquadrado)) return quadroEnquadrado; // ACKs nao sao desenquadrados
    ArrayList<Integer> quadroSemStuffing = new ArrayList<>();
    int[] FLAG = {0,1,1,1,1,1,1,0};
    int[] ESC = {0,1,1,1,1,1,0,1};
    if (quadroEnquadrado.length < 16 || !Arrays.equals(Arrays.copyOfRange(quadroEnquadrado, 0, 8), FLAG) || !Arrays.equals(Arrays.copyOfRange(quadroEnquadrado, quadroEnquadrado.length - 8, quadroEnquadrado.length), FLAG)) {
        return quadroEnquadrado;
    }
    for(int i = 8; i < quadroEnquadrado.length - 8; i += 8) {
      int[] chunk = Arrays.copyOfRange(quadroEnquadrado, i, i + 8);
      if(Arrays.equals(chunk, ESC)) {
        i += 8;
        if (i + 8 <= quadroEnquadrado.length - 8) {
            int[] proximoChunk = Arrays.copyOfRange(quadroEnquadrado, i, i + 8);
            addAll(quadroSemStuffing, proximoChunk);
        } else {
             return new int[0];
        }
      } else {
        addAll(quadroSemStuffing, chunk);
      }
    }
    return arrayListToArrayInt(quadroSemStuffing);
  }

  /**************************************************************
  * Metodo: paridadePar
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadePar(int[] quadro)
  {
    int[] par = new int[quadro.length + 1];
    int contadorDeUns = 0;
    for(int bit : quadro) {
      if(bit == 1) contadorDeUns++;
    }
    System.arraycopy(quadro, 0, par, 0, quadro.length);
    par[quadro.length] = (contadorDeUns % 2 != 0) ? 1 : 0;
    return par;
  }

  /**************************************************************
  * Metodo: paridadeParVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeParVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    if (quadro == null || quadro.length < 1) return null;
    int contadorDeUns = 0;
    for(int bit : quadro) {
      if(bit == 1) contadorDeUns++;
    }
    if(contadorDeUns % 2 == 0) {
      return Arrays.copyOfRange(quadro, 0, quadro.length - 1);
    } else {
      return null;
    }
  }

  /**************************************************************
  * Metodo: paridadeImpar
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeImpar(int[] quadro)
  {
    int[] impar = new int[quadro.length + 1];
    int contadorDeUns = 0;
    for(int bit : quadro) {
      if(bit == 1) contadorDeUns++;
    }
    System.arraycopy(quadro, 0, impar, 0, quadro.length);
    impar[quadro.length] = (contadorDeUns % 2 != 0) ? 0 : 1;
    return impar;
  }

  /**************************************************************
  * Metodo: paridadeImparVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] paridadeImparVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    if (quadro == null || quadro.length < 1) return null;
    int contadorDeUns = 0;
    for(int bit : quadro) {
      if(bit == 1) contadorDeUns++;
    }
    if(contadorDeUns % 2 != 0) {
      return Arrays.copyOfRange(quadro, 0, quadro.length - 1);
    } else {
      return null;
    }
  }

  /**************************************************************
  * Metodo: crc
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] crc(int[] quadro)
  {
    int[] polinomioGerador = {1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,0,1,1,1};
    int tamanhoDados = quadro.length;
    int tamanhoPolinomio = polinomioGerador.length;
    int[] quadroEstendido = new int[tamanhoDados + tamanhoPolinomio - 1];
    System.arraycopy(quadro, 0, quadroEstendido, 0, tamanhoDados);
    int[] resto = Arrays.copyOf(quadroEstendido, quadroEstendido.length);
    for (int i = 0; i < tamanhoDados; i++) {
        if (resto[i] == 1) {
            for (int j = 0; j < tamanhoPolinomio; j++) {
                resto[i + j] = resto[i + j] ^ polinomioGerador[j];
            }
        }
    }
    int[] quadroComCRC = Arrays.copyOf(quadro, tamanhoDados + tamanhoPolinomio - 1);
    System.arraycopy(resto, tamanhoDados, quadroComCRC, tamanhoDados, tamanhoPolinomio - 1);
    return quadroComCRC;
  }

  /**************************************************************
  * Metodo: crcVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] crcVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    if (quadro == null || quadro.length < 33) return null;
    int[] polinomioGerador = {1,0,0,0,0,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,1,1,1,0,1,1,0,1,1,0,1,1,1};
    int tamanhoQuadro = quadro.length;
    int tamanhoPolinomio = polinomioGerador.length;
    int tamanhoDados = tamanhoQuadro - (tamanhoPolinomio - 1);
    int[] resto = Arrays.copyOf(quadro, tamanhoQuadro);
    for (int i = 0; i < tamanhoDados; i++) {
        if (resto[i] == 1) {
            for (int j = 0; j < tamanhoPolinomio; j++) {
                resto[i + j] = resto[i + j] ^ polinomioGerador[j];
            }
        }
    }
    boolean erroDetectado = false;
    for (int i = tamanhoDados; i < tamanhoQuadro; i++) {
        if (resto[i] != 0) {
            erroDetectado = true;
            break;
        }
    }
    if (!erroDetectado) {
        return Arrays.copyOfRange(quadro, 0, tamanhoDados);
    } else {
        return null;
    }
  }

  /**************************************************************
  * Metodo: hamming
  * Funcao: envia a mensagem (em bits) controlada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] hamming(int[] quadro)
  {
    int m = quadro.length;
    int r = 1;
    while(Math.pow(2, r) < m + r + 1) r++;
    int n = m + r;
    int[] hamming = new int[n];
    int ponteiro = 0;
    for(int i = 0; i < n; i++) {
      int pos = i + 1;
      boolean potenciaDeDois = pos > 0 && (pos & (pos - 1)) == 0;
      if(!potenciaDeDois) {
        if(ponteiro < m) hamming[i] = quadro[ponteiro++];
      }
    }
    for(int i = 0; i < r; i++) {
      int paridadePos = (int) Math.pow(2, i);
      int xor = 0;
      for(int j = 0; j < n; j++) {
        int pos = j + 1;
        if(((pos >> i) & 1) == 1) {
          if(pos != paridadePos) xor ^= hamming[j];
        }
      }
      hamming[paridadePos - 1] = xor;
    }
    return hamming;
  }

  /**************************************************************
  * Metodo: hammingVerificacao
  * Funcao: envia a mensagem (em bits) verificada
  * @param int[] quadro | mensagem recebida (em bits)
  * @return int[] quadroControlado | a mensagem deenquadradada
  * ********************************************************* */
  public int[] hammingVerificacao(int[] quadro, TelaPrincipalController controller)
  {
    if (quadro == null || quadro.length < 1) return null;
    int n = quadro.length;
    int r = 0;
    while(Math.pow(2, r) < n + 1) r++;
    int posicaoErro = 0;
    for(int i = 0; i < r; i++) {
      int p_pos = (int) Math.pow(2, i);
      int xor = 0;
      for(int j = 0; j < n; j++) {
        if ((((j + 1) >> i) & 1) == 1) xor ^= quadro[j];
      }
      if (xor != 0) posicaoErro += p_pos;
    }
    if (posicaoErro != 0) {
        int indiceErro = posicaoErro - 1;
        if (indiceErro < n) quadro[indiceErro] = 1 - quadro[indiceErro];
    }
    ArrayList<Integer> dadosOriginais = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        int pos = i + 1;
        boolean isPowerOfTwo = (pos > 0) && ((pos & (pos - 1)) == 0);
        if (!isPowerOfTwo) dadosOriginais.add(quadro[i]);
    }
    return arrayListToArrayInt(dadosOriginais);
  }

  /**************************************************************
  * Metodo: adicionarNumeroDeSequencia
  * Funcao: Adiciona um numero de sequencia de 8 bits no início de um quadro.
  * @param int[] quadro | O quadro de dados.
  * @param int numeroSequencia | O número a ser adicionado.
  * @return int[] quadroComSequencia | O quadro com o número de sequência.
  * ********************************************************* */
  public int[] adicionarNumeroDeSequencia(int[] quadro, int numeroSequencia) {
      String binarioSequencia = String.format("%8s", Integer.toBinaryString(numeroSequencia)).replace(' ', '0');
      int[] quadroComSequencia = new int[quadro.length + 8];
      for (int i = 0; i < 8; i++) {
          quadroComSequencia[i] = Character.getNumericValue(binarioSequencia.charAt(i));
      }
      System.arraycopy(quadro, 0, quadroComSequencia, 8, quadro.length);
      return quadroComSequencia;
  }

  /**************************************************************
  * Metodo: extrairNumeroDeSequencia
  * Funcao: Extrai o numero de sequencia de 8 bits do inicio de um quadro.
  * @param int[] quadroComSequencia | O quadro com a sequencia.
  * @return int numeroSequencia | O numero de sequencia extraido.
  * ********************************************************* */
  public int extrairNumeroDeSequencia(int[] quadroComSequencia) {
      if (quadroComSequencia == null || quadroComSequencia.length < 8) return -1;
      StringBuilder binarioSequencia = new StringBuilder();
      for (int i = 0; i < 8; i++) {
          binarioSequencia.append(quadroComSequencia[i]);
      }
      return Integer.parseInt(binarioSequencia.toString(), 2);
  }

  /**************************************************************
  * Metodo: extrairDados
  * Funcao: Extrai os dados de um quadro, removendo o numero de sequencia.
  * @param int[] quadroComSequencia | O quadro completo.
  * @return int[] | Apenas os dados do quadro.
  * ********************************************************* */
  public int[] extrairDados(int[] quadroComSequencia) {
      if (quadroComSequencia == null || quadroComSequencia.length < 8) return new int[0];
      return Arrays.copyOfRange(quadroComSequencia, 8, quadroComSequencia.length);
  }

  /**************************************************************
  * Metodo: criarQuadroAck
  * Funcao: Cria um quadro de ACK com uma flag e o numero de sequencia.
  * @param int numeroSequencia | O numero do PROXIMO quadro esperado.
  * @return int[] quadroAck | O quadro de ACK.
  * ********************************************************* */
  public int[] criarQuadroAck(int numeroSequencia) {
      String binarioSequencia = String.format("%8s", Integer.toBinaryString(numeroSequencia)).replace(' ', '0');
      int[] quadroAck = new int[16];
      System.arraycopy(ACK_FLAG, 0, quadroAck, 0, 8);
      for (int i = 0; i < 8; i++) {
          quadroAck[8 + i] = Character.getNumericValue(binarioSequencia.charAt(i));
      }
      return quadroAck;
  }

  /**************************************************************
  * Metodo: isQuadroAck
  * Funcao: Verifica se um quadro e um quadro de ACK pela sua flag.
  * @param int[] quadro | O quadro a ser verificado.
  * @return boolean | True se for um ACK, false caso contrario.
  * ********************************************************* */
  public boolean isQuadroAck(int[] quadro) {
      if (quadro == null || quadro.length != 16) return false;
      int[] flagDoQuadro = Arrays.copyOfRange(quadro, 0, 8);
      return Arrays.equals(flagDoQuadro, ACK_FLAG);
  }

  /**************************************************************
  * Metodo: extrairNumeroDoAck
  * Funcao: Extrai o numero de sequencia de um quadro de ACK.
  * @param int[] quadroAck | O quadro de ACK.
  * @return int | O numero de sequencia confirmado.
  * ********************************************************* */
  public int extrairNumeroDoAck(int[] quadroAck) {
      if (!isQuadroAck(quadroAck)) return -1;
      int[] numeroBinario = Arrays.copyOfRange(quadroAck, 8, 16);
      StringBuilder builder = new StringBuilder();
      for (int bit : numeroBinario) builder.append(bit);
      return Integer.parseInt(builder.toString(), 2);
  }
}