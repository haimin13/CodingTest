// 3시간 25분
// 그냥 노가다 빡구현 문제. 할게 많아서 실수가 많았다. 영구적인 상태와 일시적인 상태를 구분하는 게 중요하다.
// 그리고 너무 다 int로 해놓으면 가독성 떨어져서 디버깅이 어렵다. 그러니까 여유있으면 클래스만들어서 관리하는 게 편하긴 하겠다.

import java.io.*;
import java.util.*;

public class Main {
    static boolean[][] visited;
    static int[][] map;
    static int[][] heat;
    static int[][] turtle;
    static int[][] volcano;
    static int n, m, k;
    static int[] dy = {0, 1, 0, -1};
    static int[] dx = {1, 0, -1, 0};

    public static void main(String[] args) throws Exception {
        // Please write your code here.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine().trim());
        StringBuilder sb = new StringBuilder();
        n = Integer.parseInt(st.nextToken()); // 맵 크기
        m = Integer.parseInt(st.nextToken()); // 거북이 수
        k = Integer.parseInt(st.nextToken()); // 화산 수
        // 맵 영구 상황 (빈칸, 산호초, 화석, 바다거북)
        // 맵 턴 상황 (열기)
        // 화산 상황 (임계점, 압력)

        // map 입력
        visited = new boolean[n][n];
        heat = new int[n][n];
        map = new int[n][n]; // 0: blank, 1: 산호초, 2: 바다 거북 or 화석
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine().trim());
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        // turtle 입력
        turtle = new int[m][]; //{y, x, isAlive, turn}
        for (int i = 0; i < m; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            turtle[i] = new int[]{y, x, 1, 1};
            map[y][x] = 2;
        }
        //volcano 입력
        volcano = new int[k][]; //{y, x, 임계치, 압력, 분출함}
        for (int i = 0; i < k; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int y = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int p = Integer.parseInt(st.nextToken());
            volcano[i] = new int[]{y, x, p, 0, 0};
        }

        for (int i = 0; i < 100; i++) {
            moveTurtle();
            if (checkDone()) break;
            increasePressure();
            erruptVolcano();
            fossilize();
            //printMap(heat, sb, i);
            resetHeat();
            //printMap(map, sb, i);
        }

        for (int[] t : turtle) {
            if (t[3] > 100 || (t[3] == 100 && (t[0] != n-1 || t[1] != n-1))) t[3] = -1;
            sb.append(t[3]).append('\n');
        }
        System.out.println(sb);
    }

    static void moveTurtle() {
        for (int i = 0; i < m; i++) {
            resetVisited();
            int[] t = turtle[i];
            //System.out.printf("%d번 거북이 위치 (%d, %d) isAlive: %d\n", i, t[0], t[1], t[2]);
            int isAlive = t[2];
            if (isAlive == 0) continue; // 죽었으면 스킵
            int startY = t[0];
            int startX = t[1];
            int[] firstMove = bfs(startY, startX);
            // 움직일 수 있는지 확인
            if (firstMove != null) {
                map[startY][startX] = 0;
                // 도착 여부 확인
                int firstY = firstMove[0];
                int firstX = firstMove[1];
                //System.out.printf("%d번 거북이 (%d, %d) -> (%d, %d)로 이동\n", i, startY, startX, firstY, firstX);
                t[0] = firstY;
                t[1] = firstX;
                if (firstY == n - 1 && firstX == n - 1) {
                   // System.out.println(i + "번 목적지 도착으로 사망");
                    t[2] = 0;
                    map[firstY][firstX] = 0;
                } else {
                    map[startY][startX] = 0;
                    map[firstY][firstX] = 2;
                    t[3] += 1;
                }

            } else {
                t[3] += 1;
            }
        }
    }

    static int[] bfs(int startY, int startX) {
        Queue<int[]> q = new ArrayDeque<>();
        visited[startY][startX] = true;
        q.offer(new int[]{startY, startX, -1, -1});
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int curY = cur[0];
            int curX = cur[1];
            int firstY = cur[2];
            int firstX = cur[3];
            if (curY == n - 1 && curX == n - 1) {
                q.clear();
                return new int[]{firstY, firstX};
            }
            
            for (int i = 0; i < 4; i++) {
                int ny = curY + dy[i];
                int nx = curX + dx[i];
                if (0 <= ny && ny < n && 0 <= nx && nx < n && !visited[ny][nx] && map[ny][nx] == 0) {
                    visited[ny][nx] = true;
                    if (firstY == -1) {
                        q.offer(new int[]{ny, nx, ny, nx});
                    } else {
                        q.offer(new int[]{ny, nx, firstY, firstX});
                    }
                }
            }
        }
        return null;
    }

    static void resetVisited() {
        for (int i = 0; i < n; i++) {
            Arrays.fill(visited[i], false);
        }
    }

    static boolean checkDone() {
        for (int[] t : turtle) {
            if (t[2] == 1) return false;
        }
        return true;
    }

    static void increasePressure() {
        for (int[] v: volcano) {
            v[3] += 10;
        }
    }

    static void erruptVolcano() {
        Queue<Integer> q = new ArrayDeque<>();
        checkActivation(q);
        while (!q.isEmpty()) {
            spreadHeat(q.poll());
            checkActivation(q);
        }
    }

    static void checkActivation(Queue<Integer> q) {
        for (int i = 0; i < k; i++) {
            int y = volcano[i][0];
            int x = volcano[i][1];
            //System.out.printf("%d번 화산 임계치: %d, 압력: %d, 열기: %d\n", i, volcano[i][2], volcano[i][3], heat[y][x]);
            if (volcano[i][4] == 0 && volcano[i][2] <= volcano[i][3] + heat[y][x]) {
                //System.out.printf("파이야!\n");
                volcano[i][4] = 1;
                q.offer(i);
            }
        }
    }

    static void spreadHeat(int num) {
        int[] v = volcano[num];
        int startY = v[0];
        int startX = v[1];
        int initial = v[2];

        heat[startY][startX] += initial;

        for (int i = 0; i < 4; i++) {
            int curHeat = initial;
            int y = startY;
            int x = startX;
            while(true) {
                curHeat = curHeat / 2;
                y = y + dy[i];
                x = x + dx[i];
                if (0 <= y && y < n && 0 <= x && x < n && curHeat > 0 && map[y][x] != 1) {
                    heat[y][x] += curHeat;
                } else {
                    break;
                }
            }
        }
    }
    
    static void fossilize() {
        for (int i = 0; i < m; i++) {
            int[] t = turtle[i];
            if (t[2] == 0) continue;
            int y = t[0];
            int x = t[1];

            if (heat[y][x] >= 20) {
                //System.out.println(i + "번 화석화!");
                t[2] = 0;
                t[3] = -1;
            }
        }
    }

    static void resetHeat() {
        for (int i = 0; i < n; i++) {
            Arrays.fill(heat[i], 0);
        }
        for (int[] v : volcano) {
            if (v[4] == 1) {
                v[3] = 0;
                v[4] = 0;
            }
        }
    }

    static void printMap(int[][] table, StringBuilder sb, int turn) {
        sb.append("Turn ").append(turn + 1).append('\n');
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(table[i][j]).append(' ');
            }
            sb.append('\n');
        }
        sb.append('\n');
        System.out.println(sb);
        sb.setLength(0);
    }
}
