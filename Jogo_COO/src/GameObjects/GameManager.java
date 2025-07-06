package GameObjects;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import GameObjects.Colliders.CollideWithPlayer;
import GameObjects.SpaceShips.Enemies.Enemy;
import GameObjects.SpaceShips.Enemies.Enemy1;
import GameObjects.SpaceShips.Enemies.Enemy2;
import GameObjects.SpaceShips.Enemies.Bosses.Boss1;

public class GameManager {
    int proximoEvento = 0;
    List<String[]> eventosDaFase = new ArrayList<>();

    private static List<String[]> carregarConfiguracoes(String arquivoFase) throws IOException {
        List<String[]> eventos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoFase))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] partes = linha.split(" ");
                eventos.add(partes);
            }
        }
        return eventos;
    }
    
    

    public void loadLevel(String fasePath){
        try{
            eventosDaFase = carregarConfiguracoes(fasePath); // Carrega os eventos da fase a partir do arquivo
            System.out.println("Fase carregada com sucesso: " + fasePath);
        }catch (IOException e){
            System.out.println("Erro ao carregar a fase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void checkLevel(List<Enemy1> enemies1, 
                    List<Enemy2> enemies2, 
                    List<Boss1> bosses1, 
                    List<Enemy> enemies, 
                    List<CollideWithPlayer> colideComPlayer)
    {
        System.out.println(eventosDaFase.size() + " eventos na fase.");
            // Processa os eventos até o próximo evento que ainda não ocorreu
        while (proximoEvento < eventosDaFase.size()) {
            String[] evento = eventosDaFase.get(proximoEvento);
            String tipo = evento[0];
            int tempo = Integer.parseInt(evento[2]);

            if (System.currentTimeMillis() < tempo) break;

            if ("INIMIGO".equalsIgnoreCase(tipo)) {
                int tipoInimigo = Integer.parseInt(evento[1]);
                double x = Double.parseDouble(evento[3]);
                double y = Double.parseDouble(evento[4]);

                if (tipoInimigo == 1) {
                    Enemy1 e = new Enemy1(x, y, 0.2, (3 * Math.PI) / 2, 0.0);
                    e.setNextShoot(System.currentTimeMillis() + 500);
                    enemies.add(e);
                    enemies1.add(e);
                    colideComPlayer.add(e);
                } else if (tipoInimigo == 2) {
                    Enemy2 e = new Enemy2(x, y, 0.42, (3 * Math.PI) / 2, 0.0);
                    enemies.add(e);
                    enemies2.add(e);
                    colideComPlayer.add(e);
                }
            } else if ("CHEFE".equalsIgnoreCase(tipo)) {
                int tipoChefe = Integer.parseInt(evento[1]);
                int vida = Integer.parseInt(evento[2]);
                double x = Double.parseDouble(evento[4]);
                double y = Double.parseDouble(evento[5]);

                if (tipoChefe == 1) {
                    Boss1 boss = new Boss1(x, y, 0.15, (3 * Math.PI) / 2, 0.0);
                    boss.setVida(vida); // Assumindo método setVida existe
                    enemies.add(boss);
                    bosses1.add(boss);
                    colideComPlayer.add(boss);
                }
            }

            proximoEvento++;
        }
    }
}
