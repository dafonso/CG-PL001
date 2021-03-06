/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Emanuel
 */
public class PersistenceManager {

    private static String filename = "highway_data_dft";
    private static BufferedReader in;
    private static BufferedWriter out;

    public static Simulator loadSimulator(String filename, boolean isEditorMode) {
        try {
            in = new BufferedReader(new FileReader(filename));
            PersistenceManager.filename = filename;
            ArrayList<Section> road = new ArrayList();
            int roadLength = Integer.parseInt(in.readLine()) + 2;
            float x = 0;
            float y = 0;
            float z = 0;
            String[] vecStr;
            for (int i = 0; i < roadLength; i++) {
                Section section = new Section(i == 0 || i == roadLength - 1, i == 1);
                if (!section.isAuxiliar()) {
                    section.setOriginX(x);
                    section.setOriginY(y);
                    section.setOriginZ(z);
                    z += 10;
                    vecStr = in.readLine().split(" ");
                    section.setAngle(Float.parseFloat(vecStr[0]));
                    if (Integer.parseInt(vecStr[1]) != -1) {
                        section.setSource(createSource(Integer.parseInt(vecStr[1]), section));
                    }
                }
                road.add(i, section);
            }
            Simulator simulator = new Simulator(isEditorMode);
            simulator.setRoad(road);
            in.close();
            return simulator;
        } catch (Exception e) {
            System.out.println("Foi lido o ficheiro de default!!");
            return loadDefaults(isEditorMode);
        }
    }

    private static ArrayList<Section> createRoad(int segmentsNumber) {
        ArrayList<Section> road = new ArrayList();
        float x = 0;
        float y = 0;
        float z = 0;
        for (int i = 0; i < segmentsNumber + 2; i++) {
            Section section = new Section(i == 0 || i == segmentsNumber + 1, i == 1);
            if (!section.isAuxiliar()) {
                section.setOriginX(x);
                section.setOriginY(y);
                section.setOriginZ(z);
                z += 10;
            }
            road.add(i, section);
        }
        return road;
    }

    public static Source createSource(int period, Section section) {
        Source source = new Source(period);
        source.setOriginX(section.getOriginX() - 3.5f);
        source.setOriginY(section.getOriginY());
        source.setOriginZ(section.getOriginZ() + 2.0f);
        source.setOn(true);
        return source;
    }

    //Caso o ficheiro n�o exista, deve ser criada uma rede de estradas com as caracter�sticas apresentadas na tabela 1.
    //    comprimento da estrada 10
    //    posi��es das fontes 1; 4; 5
    //    per�odo de emiss�o de autom�veis nas fontes 2; 4; 5
    //    estrada direita
    //Tabela 1: Caracter�sticas da rede de estradas quando � omitido o nome do ficheiro.
    private static Simulator loadDefaults(boolean isEditorMode) {
        ArrayList<Section> road = createRoad(10);
        Source s1 = createSource(2, road.get(1));
        road.get(1).setSource(s1);
        Source s2 = createSource(4, road.get(4));
        road.get(4).setSource(s2);
        Source s3 = createSource(5, road.get(5));
        road.get(5).setSource(s3);
        Simulator simulator = new Simulator(isEditorMode);
        simulator.setRoad(road);
        return simulator;
    }

    public static void saveSimulator(ArrayList<Section> road) {
        try {
            out = new BufferedWriter(new FileWriter(new File(PersistenceManager.filename)));
            StringBuilder sb = new StringBuilder();
            sb.append(road.size() - 2).append("\n");
            out.write(sb.toString());
            for (Section s : road) {
                if (!s.isAuxiliar()) {
                    sb = new StringBuilder();
                    sb.append(s.getAngle());//guarda �ngulodo segmento
                    sb.append(" ");
                    if (s.hasSource()) {
                        sb.append(s.getSource().getPeriod());//guarda periodo da fonte, se existir
                    } else {
                        sb.append(-1);
                    }
                    sb.append("\n");
                    out.write(sb.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao gravar o ficheiro!!");
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                System.out.println("Ocorreu um erro ao gravar o ficheiro!!");
            }
        }
    }
}
