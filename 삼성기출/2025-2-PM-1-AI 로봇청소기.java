// 2시간 40분
// 일단 상좌우하 순서로 BFS큐에 넣으면 처음 찾는애가 최단경로인 애 중에 최상단 최좌측에 있을 것이라는 가정은 틀렸다.
// 상좌우하 순서는 지역적인 이웃 탐색 순서일 뿐이고, 최상단·최좌측 우선순위를 전역적으로 보장하는 정렬 규칙은 아니야.
// 그거 뺴고 알고리즘은 잘 짰음. 그런데 청소 기준 정할 때 물건인 칸 구분하고, 먼지 양이 아니라 "청소할 수 있는 먼지의 양"이다.
// 맨 처음에는 그거 고려했는데, 일부 테케에서 빼도 통과되길래 뺐더니 그것 때문에 나중에 찾은 테케가 틀려버림.
import java.io.*;
import java.util.*;

public class Main {
    static int[][] map;
    static int[][] spreadMap;
    static int[][] cleanerMap;
    static int[][] cleaner;
    static int[] dy = {-1, 0, 0, 1};
    static int[] dx = {0, -1, 1, 0};
    static int[] ry = {0, 1, 0, -1};
    static int[] rx = {1, 0, -1, 0};
    static int N;
    static int K;
    static int L;
    static final int INF = 1_000_000;
    static final int CLEAN_MAX = 20;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());
        L = Integer.parseInt(st.nextToken());

        map = new int[N][N];
        cleaner = new int[K][2];
        cleanerMap = new int[N][N];
        spreadMap = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            cleaner[i][0] = r-1;
            cleaner[i][1] = c-1;
            cleanerMap[r-1][c-1] = i + 1;
        }
        for (int i = 0; i < L; i++) {
            // System.out.println("Start");
            // printMap(map);
            // printMap(cleanerMap);
            move();
            // System.out.println("after move");
            // printMap(cleanerMap);
            clean();
            // System.out.println("after clean");
            // printMap(map);
            addDust();
            // System.out.println("after add dust");
            // printMap(map);
            spreadDust();
            // System.out.println("after spread");
            // printMap(map);
            int result = countDust();
            sb.append(result).append('\n');
            if (result == 0) break;
        }
        System.out.println(sb);
    }
    static void move() {
        Queue<int[]> q = new ArrayDeque<>();
        for (int i = 0; i < K; i++) {
            int preY = cleaner[i][0];
            int preX = cleaner[i][1];
            if (map[preY][preX] > 0) continue; // 현재 위치에 먼지 있음. 이동 X
            boolean[][] visited = new boolean[N][N];

            int curY = preY;
            int curX = preX;
            int minY = preY;
            int minX = preX;
            int minDepth = INF;
            visited[curY][curX] = true;
            q.offer(new int[]{curY, curX, 0});
            while (!q.isEmpty()) {
                int[] cur = q.poll();
                curY = cur[0];
                curX = cur[1];
                int depth = cur[2];
                if (map[curY][curX] > 0) {
                    if (depth < minDepth || depth == minDepth && (curY < minY || (curY == minY && curX < minX))) {
                        minY = curY;
                        minX = curX;
                        minDepth = depth;
                    }
                }
                if (minDepth < INF) continue;
                for (int j = 0; j < 4; j++) {
                    int ny = curY + dy[j];
                    int nx = curX + dx[j];
                    if (0 <= ny && ny < N && 0 <= nx && nx < N 
                        && !visited[ny][nx] && map[ny][nx] != -1 && cleanerMap[ny][nx] == 0) {
                        visited[ny][nx] = true;
                        q.offer(new int[]{ny, nx, depth + 1});
                    }
                }
            }
            cleaner[i][0] = minY;
            cleaner[i][1] = minX;
            cleanerMap[preY][preX] = 0;
            cleanerMap[minY][minX] = i + 1;
        }
    }
    
    static void clean() {
        for (int i = 0; i < K; i++) {
            int curY = cleaner[i][0];
            int curX = cleaner[i][1];
            int right = (curX + 1 < N) ? Math.max(0, Math.min(CLEAN_MAX, map[curY][curX + 1])) : 0;
            int down = (curY + 1 < N) ? Math.max(0, Math.min(CLEAN_MAX, map[curY + 1][curX])) : 0;
            int left = (curX - 1 >= 0) ? Math.max(0, Math.min(CLEAN_MAX, map[curY][curX - 1])) : 0;
            int up = (curY - 1 >= 0) ? Math.max(0, Math.min(CLEAN_MAX, map[curY - 1][curX])) : 0;
            int[] sum = new int[]{up + right + down, right+down+left, down+left+up, left+up+right};
            // System.out.printf("[%d, %d, %d, %d]\n", sum[0], sum[1], sum[2], sum[3]);
            int dir = 0; int max = sum[0];
            for (int j = 1; j < 4; j++) {
                if (sum[j] > max) {
                    dir = j;
                    max = sum[j];
                }
            }
            // System.out.printf("청소기 %d번 방향 선택: %d\n", i + 1, dir);
            map[curY][curX] = Math.max(0, map[curY][curX] - CLEAN_MAX);
            for (int j = -1; j < 2; j++) {
                int d = dir + j;
                if (d < 0) d = 3;
                else if (d > 3) d = 0;
                int ny = curY + ry[d];
                int nx = curX + rx[d];
                if (0 <= ny && ny < N && 0 <= nx && nx < N && map[ny][nx] > 0) {
                    map[ny][nx] = Math.max(0, map[ny][nx] - CLEAN_MAX);
                }
            }
            
        }
    }

    static void addDust() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] > 0) {
                    map[i][j] += 5;
                }
            }
        }
    }

    static void spreadDust() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] == 0) {
                    int dust = 0;
                    for (int k = 0; k < 4; k++) {
                        int ny = i + ry[k];
                        int nx = j + rx[k];
                        if (0 <= ny && ny < N && 0 <= nx && nx < N && map[ny][nx] > 0) {
                            dust += map[ny][nx];
                        }
                    }
                    spreadMap[i][j] += dust / 10;
                }
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] == 0) {
                    map[i][j] += spreadMap[i][j];
                    spreadMap[i][j] = 0;
                }
            }
        }
    }

    static int countDust() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int dust = map[i][j];
                if (dust > 0) result += dust;
            }
        }
        return result;
    }

    static void printMap(int[][] map) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%d ", map[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
