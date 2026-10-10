// 1시간 27분
// unload 판정을 잘못생각했다. 행 아래로 순회하면서 처음발견하는 택배를 그냥 후보에 넣었는데, 다른 행에서 다른 택배에 막혀있을 수 있다는 점을 고려해야 한다.
// 그래서 그냥 택배 수도 100개로 적으니까 다 순회했다.
// map.get(key) -> value 반환
// map.remove(key) -> key,value 쌍 삭제

import java.io.*;
import java.util.*;

public class Main {
    static class Package {
        int x;
        int y;
        int w;
        int h;
        Package (int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    static Map<Integer, Package> map;
    static int N;
    static int[][] storage;
    static final int INF = 1_000_000;
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();
        st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());
        storage = new int[N][N];
        map = new HashMap<>();
        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int k = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            map.put(k, new Package(c-1, 0, w, h));

            Package p = map.get(k);
            for (int row = p.y; row < p.y + p.h; row++) {
                for (int col = p.x; col < p.x + p.w; col++) {
                    storage[row][col] = k;
                }
            }
            // printStorage();
            fall(k);
            // printStorage();
        }
        for (int i = 0; i < M; i++) {
            int k = unload(i);
            // System.out.printf("off %d\n", k);
            // printStorage();
            sb.append(k).append('\n');
            int fallDistance = 1;
            while (fallDistance > 0) {
                fallDistance = 0;
                for (int key : map.keySet()) {
                    int d = fall(key);
                    fallDistance += d;
                    // if (d > 0) {
                    //     System.out.printf("drop %d\n", key);
                    //     printStorage();
                    // }
                }
            }
        }
        System.out.println(sb);
    }

    static int fall(int k) {
        Package p = map.get(k);
        int fallDistance = 0;
        for (int row = p.y + p.h; row < N; row++) {
            boolean obstacle = false;
            for (int col = p.x; col < p.x + p.w ; col++) {
                if (storage[row][col] != 0) {
                    obstacle = true;
                    break;
                }
            }
            if (!obstacle) {
                fallDistance++;
            } else {
                break;
            }
        }
        if (fallDistance > 0) {
            for (int row = p.y; row < p.y + p.h; row++) {
                for (int col = p.x; col < p.x + p.w; col++) {
                    storage[row][col] = 0;
                }
            }
            p.y = p.y + fallDistance;
            for (int row = p.y; row < p.y + p.h; row++) {
                for (int col = p.x; col < p.x + p.w; col++) {
                    storage[row][col] = k;
                }
            }
        }
        return fallDistance;
    }

    static int unload(int dir) {
        int target = INF;
        for (int key : map.keySet()) {
            Package p = map.get(key);
            boolean loadable = true;
            for (int row = p.y; row < p.y + p.h; row++) {
                if (dir % 2 != 0) {
                    for (int col = p.x + p.w; col < N; col++) {
                        if (storage[row][col] != 0) {
                            loadable = false;
                            break;
                        }
                    }
                } else {
                    for (int col = p.x - 1; col >= 0; col--) {
                       if (storage[row][col] != 0) {
                            loadable = false;
                            break;
                        }
                    }
                }
                if (!loadable) break;
            }
            if (loadable) {
                target = Math.min(target, key);
            }
        }
        Package p = map.get(target);
        if (p == null) System.out.println(target);
        else {
            for (int row = p.y; row < p.y + p.h; row++) {
                for (int col = p.x; col < p.x + p.w; col++) {
                    storage[row][col] = 0;
                }
            }
            map.remove(target);
        }
        return target;
    }
    static void printStorage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< N; i++) {
            for (int j = 0; j < N; j++) {
                sb.append(storage[i][j]).append(' ');
            }
            sb.append('\n');
        }
        sb.append('\n');
        System.out.println(sb);
    }
}
