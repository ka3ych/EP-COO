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
import GameObjects.SpaceShips.Enemies.Bosses.Boss;
import GameObjects.SpaceShips.Enemies.Bosses.Boss1;
import GameObjects.SpaceShips.Enemies.Bosses.Boss2;
import GameObjects.SpaceShips.Enemies.Bosses.Boss3;

final public class GameManager {
    protected static long deltaTime = System.currentTimeMillis();
    protected static int proximoEvento = 0;
    protected static long nextEnemy2 = 0;
    protected static int enemy2_count = 0; // Contador para controlar a formação do inimigo 2 

    protected static List<String[]> eventosDaFase = new ArrayList<>();

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
    
    

    public static void loadLevel(String fasePath){
        try{
            eventosDaFase = carregarConfiguracoes(fasePath); // Carrega os eventos da fase a partir do arquivo
            System.out.println("Fase carregada com sucesso: " + fasePath);
            proximoEvento = 0; // Reseta o próximo evento
        }catch (IOException e){
            System.out.println("Erro ao carregar a fase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void checkLevel(List<Enemy1> enemies1, 
                    List<Enemy2> enemies2, 
                    List<Enemy> enemies, 
                    List<Boss1> bosses1, 
                    List<Boss2> bosses2, 
                    List<Boss3> bosses3,
                    List<Boss> bosses, 
                    List<CollideWithPlayer> colideComPlayer)
    {
        //System.out.println(eventosDaFase.size() + " eventos na fase.");
            // Processa os eventos até o próximo evento que ainda não ocorreu
        while (proximoEvento < eventosDaFase.size()) {
            String[] evento = eventosDaFase.get(proximoEvento);
            String tipo = evento[0];
            long tempo;
    
            if (tipo.equalsIgnoreCase("CHEFE")) {
                tempo = (long) Integer.parseInt(evento[3]); // Para chefes, o tempo é o terceiro elemento
            }
            else{
                tempo = (long) Integer.parseInt(evento[2]); // Para inimigos, o tempo é o segundo elemento
            }

            if (System.currentTimeMillis()-deltaTime < tempo){
                //System.out.println("Próximo evento ainda não ocorreu: " + tempo);
                return;
            }
            if (tipo.equalsIgnoreCase("INIMIGO")) {
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
                    
                    // ***** AQUI É ONDE O INIMIGO 2 É CRIADO *****
                    if(System.currentTimeMillis() < nextEnemy2)return;

                    Enemy2 e = new Enemy2(x, y, 0.42, (3 * Math.PI) / 2, 0.0);
                    enemies.add(e);
                    enemies2.add(e);
                    colideComPlayer.add(e);

                    enemy2_count++;
                    
                    // Controle de formação
                    if(enemy2_count < 10){
                        nextEnemy2 = System.currentTimeMillis() + 120;
                        //System.out.println("Inimigo 2 criado: " + enemy2_count);
                        return;
                    }
                    else{
                        enemy2_count = 0;
                    }
                }
            } else if (tipo.equalsIgnoreCase("CHEFE")) {
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
                else if (tipoChefe == 2) {
                    Boss2 boss = new Boss2(x, y, 0.15, (3 * Math.PI) / 2, 0.0);
                    boss.setVida(vida); // Assumindo método setVida existe
                    enemies.add(boss);
                    bosses2.add(boss);
                    colideComPlayer.add(boss);
                }
                else if (tipoChefe == 3) {
                    Boss3 boss = new Boss3(x, y, 0.15, (3 * Math.PI) / 2, 0.0);
                    boss.setVida(vida); // Assumindo método setVida existe
                    enemies.add(boss);
                    bosses3.add(boss);
                    colideComPlayer.add(boss);
                }
            }

            proximoEvento++;
            deltaTime = System.currentTimeMillis(); // Atualiza o tempo do último evento processado
        }
    }
}
