/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author Emanuel
 */
public class PersistenceManager {

    private static String filename = "highway_data";

    public static Simulator loadSimulator(String filename, boolean isEditorMode) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            
                int number = Integer.parseInt(in.readLine());
                Section[] road = new Section[number + 2];
                for (int i = 0; i < road.length; i++) {
                    Section section = new Section(i == 0 || i == 11);
                    road[i] = section;
                }



            }  catch (Exception e) {
            return loadDefaults(isEditorMode);
        }
        return loadDefaults(isEditorMode);
    }

    //Caso o ficheiro n�o exista, deve ser criada uma rede de estradas com as caracter�sticas apresentadas na tabela 1.
    //    comprimento da estrada 10
    //    posi��es das fontes 1; 4; 5
    //    per�odo de emiss�o de autom�veis nas fontes 2; 4; 5
    //    estrada direita
    //Tabela 1: Caracter�sticas da rede de estradas quando � omitido o nome do ficheiro.
    private static Simulator loadDefaults(boolean isEditorMode) {
        Section[] road = new Section[12];
        for (int i = 0; i < 12; i++) {
            Section section = new Section(i == 0 || i == 11);
            road[i] = section;
        }
        ArrayList<Source> sources = new ArrayList();
        Source source = new Source(1, 2, road[1]);
        sources.add(source);
        source = new Source(4, 4, road[4]);
        sources.add(source);
        source = new Source(5, 5, road[5]);
        sources.add(source);

        Simulator simulator = new Simulator(isEditorMode);
        simulator.setRoad(road);
        simulator.setSources(sources);

        return simulator;
    }

    public static void saveSimulator() {
    }
}
