/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

/**
 *
 * @author Emanuel
 */
public class PersistenceManager {

    private static String filename = "highway_data";

    public static Simulator loadSimulator(String filename) {
        return null;
    }

    //Caso o ficheiro n�o exista, deve ser criada uma rede de estradas com as caracter�sticas apresentadas na tabela 1.
    //    comprimento da estrada 10
    //    posi��es das fontes 1; 4; 5
    //    per�odo de emiss�o de autom�veis nas fontes 2; 4; 5
    //    estrada direita
    //Tabela 1: Caracter�sticas da rede de estradas quando � omitido o nome do ficheiro.
    private static Simulator loadDefaults() {
        return null;
    }

    public static void saveSimulator() {

    }

}
