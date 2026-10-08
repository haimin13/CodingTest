import java.io.*;
import java.util.*;

public class Main {
    // 이동: 상 우 하 좌
    static int[] dy = {-1, 0, 1, 0};
    static int[] dx = {0, 1, 0, -1};
    static int[] rotate = {0, -1, 1, 2};
    static boolean[][] visited;
    static boolean[][] tempVisited;
    static int[][] map;
    static Queue<int[]> q;
    static int left;
    static int INF = 1_000_000_000;

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder();
        StringTokenizer st;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        st = new StringTokenizer(br.readLine());
        int n = Integer.parseInt(st.nextToken());
        int r = Integer.parseInt(st.nextToken());
        int c = Integer.parseInt(st.nextToken());
        int d = Integer.parseInt(st.nextToken());
        if (d == 1) d = 0;
        else if (d == 4) d = 1;

        left = n*n;
        map = new int[n][n];
        visited = new boolean[n][n];
        tempVisited = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                int entry = Integer.parseInt(st.nextToken());
                map[i][j] = entry;
                if (entry == 1) left--;
            }
        }
        int y = r - 1;
        int x = c - 1;
        visited[y][x] = true;
        sb.append(y+1).append(' ').append(x+1).append('\n');
        for (int i = 0; i < left - 1; i++) {
            int prevY = y;
            int prevX = x;
            for (int j = 0; j < 4; j++) {
                int tempDir = d + rotate[j];
                if (tempDir >= 4) tempDir = tempDir % 4;
                else if (tempDir == -1) tempDir = 3;

                int ny = y + dy[tempDir];
                int nx = x + dx[tempDir];
                if (0 <= ny && ny < n && 0 <= nx && nx < n && map[ny][nx] == 0 && !visited[ny][nx]) {
                    y = ny;
                    x = nx;
                    d = tempDir;
                    break;
                }
            }
            if (prevY == y && prevX == x) {
                q = new ArrayDeque<>();
                for (int j = 0; j < n; j++) {
                    Arrays.fill(tempVisited[j], false);
                }
                tempVisited[y][x] = true;
                q.offer(new int[]{y, x, -1, 0});
                // BFS 목적
                // 가장 가까운 칸중에 제일 위쪽 -> 왼쪽에 있는 거 찾기
                // 거기에 가는 경로 중에 선택할 때 좌,하,우,상 순서로 찾기.
                // 이미 그렇게 찾고 있고 이동방향도 저장하니까 찾은것중에 위쪽 왼쪽에 있는 것만 하면 됨.
                int shortest = INF;
                while (!q.isEmpty()) {
                    int[] cur = q.poll();
                    int curY = cur[0]; int curX = cur[1]; int curDir = cur[2]; int depth = cur[3];
                    if (!visited[curY][curX]) {
                        if (depth < shortest || (depth == shortest && (y > curY || (y == curY && x > curX)))) {
                            shortest = depth;
                            y = curY;
                            x = curX;
                            d = curDir;
                        }
                    }
                    if (shortest < INF) continue; // 이미 최단거리 찾음. 큐에 새로 추가 안함.
                    for (int j = 3; j >=0; j--) { // 좌 하 우 상 순서
                        int ny = curY + dy[j];
                        int nx = curX + dx[j];
                        if (0 <= ny && ny < n && 0 <= nx && nx < n && map[ny][nx] == 0 && !tempVisited[ny][nx]) {
                            tempVisited[ny][nx] = true;
                            q.offer(new int[]{ny, nx, j, depth + 1});
                        }
                    }
                }
            }
            visited[y][x] = true;
            sb.append(y+1).append(' ').append(x+1).append('\n');
        }
        System.out.println(sb);
    }
}
