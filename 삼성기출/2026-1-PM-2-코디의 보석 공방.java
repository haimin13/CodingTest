import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        StringTokenizer st;
        Map<Integer, Integer> map = new TreeMap<>();
        List<int[]> list = new ArrayList<>();
        int gemNum = 0;

        int Q = Integer.parseInt(br.readLine());
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            int task = Integer.parseInt(st.nextToken());
            if (task == 1) {
                int N = Integer.parseInt(st.nextToken());
                for (int j = 0; j < N; j++) {
                    int w = Integer.parseInt(st.nextToken());
                    int v = Integer.parseInt(st.nextToken());
                    list.add(new int[]{w, v});
                    map.merge(w, 1, Integer::sum);
                    gemNum++;
                }

            } else if (task == 2) {
                int w = Integer.parseInt(st.nextToken());
                int v = Integer.parseInt(st.nextToken());
                list.add(new int[]{w, v});
                map.merge(w, 1, Integer::sum);
                gemNum++;

            } else if (task == 3) {
                int idx = Integer.parseInt(st.nextToken());
                idx--;
                if (0 > idx || idx >= list.size()) sb.append(-1).append('\n');
                else {
                    int[] gem = list.get(idx);
                    int w = gem[0];
                    int v = gem[1];
                    if (v != -1) {
                        gem[1] = -1;
                        map.compute(w, (key, value) -> {
                            if (value == 1) return null;
                            return value - 1;
                        });
                        gemNum--;
                    }
                    sb.append(v).append('\n');
                }

            } else if (task == 4) {
                // 실제로 있는 보석 리스트 만들고
                // 0-1knapsack 문제
                int K = Integer.parseInt(st.nextToken());
                int[] dp = new int[K+1];
                for (int[] gem : list) {
                    int w = gem[0];
                    int v = gem[1];
                    if (v != -1 && w <= K) {
                        for (int j = K; j >= w; j--) {
                            dp[j] = Math.max(dp[j], dp[j-w] + v);
                        }
                    }
                }
                sb.append(dp[K]).append('\n');

            } else if (task == 5) {
                int D = Integer.parseInt(st.nextToken());
                int result = 0;

                Integer[] arr = map.keySet().toArray(new Integer[0]);
                int len = arr.length;
                int[] prefix = new int[len + 1];
                int[] counts = new int[len];
                for (int j = 0; j < len; j++) {
                    prefix[j + 1] = prefix[j] + map.get(arr[j]);
                    counts[j] = map.get(arr[j]);
                }
                int lowerBound = 0; int upperBound = 0;
                for (int j = 0; j < len; j++) {
                    int a = arr[j];
                    int L = a - D;
                    int R = a + D;
                    while (lowerBound < len && arr[lowerBound] < L) {
                        lowerBound++;
                    }
                    while (upperBound < len && arr[upperBound] <= R) {
                        upperBound++;
                    }
                    int count = prefix[upperBound] - prefix[lowerBound];
                    count--;
                    result += count * counts[j];
                }
                result = result / 2;
                sb.append(result).append('\n');
            }
        }
        System.out.println(sb);
    }
}
