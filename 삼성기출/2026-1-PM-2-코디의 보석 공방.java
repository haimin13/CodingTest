// 3시간 10분
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
/*
 * [회고]
 *
 * 1. 자료구조 선택
 * - 보석은 추가된 순서로 삭제해야 하므로 ArrayList에 모든 보석을 저장했다.
 * - 삭제된 보석도 인덱스가 유지되어야 하기 때문에 실제로 삭제하지 않고 v = -1로 표시했다.
 * - 5번 쿼리에서는 무게별 보석 개수가 필요하고 정렬된 무게가 필요하므로 TreeMap<Integer, Integer>을 사용했다.
 *
 * 2. 4번 쿼리 - 0/1 Knapsack
 * - 각 보석은 한 번만 사용할 수 있으므로 0/1 Knapsack으로 해결했다.
 * - DP를 1차원으로 만들고, 같은 보석을 중복해서 사용하는 것을 방지하기 위해
 *   무게를 K에서 weight 방향으로 감소시키면서 갱신했다.
 *
 * 3. 5번 쿼리 - 처음 접근
 * - 처음에는 TreeMap의 정렬된 key를 배열로 만든 뒤,
 *   각 무게 a에 대해 [a-D, a+D] 범위를 이분 탐색으로 찾았다.
 * - Prefix Sum을 이용하여 해당 범위에 존재하는 보석 개수를 O(1)에 구했다.
 * - 자기 자신이 포함되므로 count에서 1을 빼고,
 *   각 보석을 기준으로 쌍을 세었기 때문에 마지막에 2로 나눴다.
 *
 * 4. TLE와 투 포인터
 * - 이분 탐색 방식은 각 무게마다 lowerBound와 upperBound를 수행하므로
 *   5번 쿼리 하나당 O(G log G)이 걸린다.
 * - 5번 쿼리가 최대 3000번이므로 더 효율적인 방법을 고민했다.
 * - 무게 배열이 정렬되어 있고 a가 증가하면 a-D와 a+D도 증가하므로,
 *   범위의 시작과 끝을 가리키는 포인터 역시 뒤로 돌아갈 필요가 없다.
 * - 따라서 lowerBound와 upperBound를 투 포인터로 바꾸어
 *   5번 쿼리를 O(G)에 처리할 수 있었다.
 *
 * 5. 투 포인터에서의 실수
 * - 처음에는 lowerBound와 upperBound를 for문 내부에서 0으로 초기화했다.
 * - 이 경우 각 원소마다 포인터가 다시 처음부터 이동하므로 O(G^2)이 되어 TLE가 발생했다.
 * - 포인터를 for문 밖에서 한 번만 초기화하고 계속 증가시키도록 수정하면서
 *   전체 이동 횟수가 최대 G번으로 제한되어 O(G)이 되었다.
 *
 * 6. 배운 점
 * - "while문이 for문 안에 있다 = 무조건 O(N^2)"은 아니다.
 * - 포인터가 뒤로 가지 않고 전체 과정에서 최대 N번만 이동한다면
 *   중첩된 while문이 있어도 O(N)이 될 수 있다.
 * - 같은 문제라도 이분 탐색을 반복하는 것보다,
 *   탐색 대상의 경계가 단조롭게 이동하는지 확인하면 투 포인터로 최적화할 수 있다.
 * - 또한 시간복잡도뿐만 아니라 실제 Java에서는 TreeMap, Integer 등의 자료구조
 *   오버헤드도 존재하므로 문제의 제한과 실제 데이터 크기를 함께 고려해야 한다.
 *
 * 7. 다른 풀이를 보고 추가로 배운 점
 * - 정렬된 배열에서는 현재 원소보다 오른쪽에 있는 원소만 탐색하면
 *   각 쌍을 한 번만 셀 수 있다.
 * - 따라서 기존처럼 모든 방향의 쌍을 센 뒤 2로 나눌 필요가 없다.
 * - 현재 위치를 i, 조건을 만족하는 오른쪽 범위의 끝을 flag로 두면
 *   flag - i - 1로 현재 원소를 제외한 유효한 쌍의 개수를 바로 구할 수 있다.
 */
