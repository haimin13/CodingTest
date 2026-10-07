// 첫시도: 시간초과
// 잘한점 1: 향수 추가, 제거할 때 TreeMap으로 받아서 O(log N) 시간으로 향도순 오름차순 정렬유지
// 잘한점 2: TreeMap value로 그 향도를 가진 향수의 수를 저장함.
// 잘못한점 1: blend할 때 perfume 배열을 돌면서 확인했음 -> map에서 그냥 지금 있는 애들만 보면 됨.
// 잘못한점 2: compose에서 dfs로 조합 전부 찾고나서 경우의 수 계산하려고 했음. 근데 이게 O(N^3)이라서 N(서로 다른 향도)이 1000정도로 크면 무조건 시간초과 남.
// 잘못한점 3: dfs에서 이미 조합 유일하게 한번씩만 찾는데 그걸 굳이 set에다가 저장하려고 해서 낭비함.
// 개선한점 1: compose에서 조합을 전부 구할 필요 없이 첫 두개 정하고 나머지 조건 만족하는 map의 최소 인덱스 찾는 방식으로 바꿈. 마지막 하나 찾는 걸 이분탐색 + 누적합으로 최종적으로 O(N^2 logN)으로 할수 있게 됨.
// 개선한점 2: 마지막 최소인덱스 찾는 거를 전부 탐색하면 똑같이 O(N^3)이니까 이분탐색으로 찾음.
// 개선한점 3: 최소인덱스 구하고 나서 그것보다 향도가 큰 향료 개수를 구해야하는데 이걸 순회로 찾으면 또 O(N^3)꼴 나니까 누적합 배열을 compose 처음 호출할 때 만들어 놓고 O(1)로 찾을 수 있게 함.
// 배운점 1: 문제 풀기 전에 시간 제한, 메모리 제한, 입력사이즈를 고려해서 알고리즘의 대략적인 시간 복잡도를 계산해보고 구현하면 좋을 것이다.
// 배운점 2: INF정할 때 Integer.MAX_VALUE로 하면 오버플로우 날 수있으니까 수정가능한 값에는 쓰지말고 그냥 적당히 큰 수 쓰자.
// 배운점 3: 조합을 직접 생성하지 않고 조건을 역으로 생각하기. 여러 원소의 조합을 직접 생성하기보다 일부 원소를 고정하고
// 나머지 원소가 만족해야 하는 조건을 구하면 탐색량을 크게 줄일 수 있다.
// 개선할점 1: blend 풀때는 정렬없어도 되긴 한다. TreeMap의 정렬 특성을 쓰지 않고도 해결할 수 있는 방법을 생각해 볼 수 있음.
// 개선할점 2: 사실 perfume 객체를 추가될 때마다 만들 필요없겠다. 같은 향도를 가진 향료가 몇개인지를 저장하기만 하면 될듯.
// 개선할점 3: 문제의 연산 빈도에 따라 TreeMap처럼 정렬 상태를 유지하는 자료구조와 HashMap처럼 조회를 빠르게 하는 자료구조 중 무엇이 적절한지 판단하는 연습이 필요함. 이번에는 아다리로 맞춤.
import java.util.*;
import java.io.*;


public class Main {

    static class Perfume {
        int grade;
        boolean discarded;

        Perfume(int grade) {
            this.grade = grade;
            this.discarded = false;
        }
    }


    static List<Perfume> perfumes;
    static StringBuilder sb = new StringBuilder();
    static StringTokenizer st;
    static Map<Integer, Integer> map;
    static Integer[] arr;
    static List<Integer> combo = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int Q = Integer.parseInt(br.readLine());
        perfumes = new ArrayList<>();
        perfumes.add(new Perfume(0));
        perfumes.get(0).discarded = true;
        map = new TreeMap<>();
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine().trim());
            int task = Integer.parseInt(st.nextToken());
            int n = Integer.parseInt(st.nextToken());
            int result = -1;
            switch (task) {
                case 1: 
                    prepare(n, st);
                    break;
                case 2:
                    add(n);
                    break;
                case 3: 
                    result = discard(n);
                    sb.append(result).append('\n');
                    break;
                case 4:
                    result = blend(n);
                    sb.append(result).append('\n');
                    break;
                case 5:
                    result = compose(n);
                    sb.append(result).append('\n');
                    break;
                default:
                    break;
            }
        }
        System.out.println(sb);
    }

    static void prepare(int n, StringTokenizer st) {
        for (int i = 0; i < n; i++) {
            int grade = Integer.parseInt(st.nextToken());
            perfumes.add(new Perfume(grade));
            map.merge(grade, 1, Integer::sum);
        }
    }
    static void add(int v) {
        perfumes.add(new Perfume(v));
        map.merge(v, 1, Integer::sum);
    }
    static int discard(int idx) {
        if (idx < 0 || idx >= perfumes.size()) {
            // 없음
            return -1;
        }
        Perfume p = perfumes.get(idx);
        if (p.discarded) {
            // 이미 폐기됨
            return -1;
        } else {
            p.discarded = true;
            map.compute(p.grade, (k, v) -> {
                if (v == 1) return null;
                return v - 1;
            });
            return p.grade;
        }
    }
    static int blend(int k) {
        if (map.isEmpty()) return -1;
        int[] dp = new int[k+1];
        int INF = 1_000_000_000;
        for (int i = 1; i <= k; i++) {
            dp[i] = INF;
        }
        for (int g : map.keySet()) {
            if (g > k) break;
            for (int i = g; i <= k; i++) {
                if (dp[i-g] != INF) {
                    dp[i] = Math.min(dp[i], dp[i-g] + 1);
                }
            }
        }
        return dp[k] == INF ? -1 : dp[k];
    }
    static int compose(int k) {
        int result = 0;
        if (map.isEmpty()) return result;
        
        arr = map.keySet().toArray(new Integer[0]);
        int[] suffix = new int[arr.length];
        suffix[arr.length - 1] = map.get(arr[arr.length - 1]);
        for (int i = arr.length - 2; i >= 0; i--) {
            suffix[i] = suffix[i + 1] + map.get(arr[i]);
        }
        for (int a : arr) {
            for (int b : arr) {
                int c = k - (a + b);
                int minIdx = binarySearch(c);
                if (minIdx == -1) continue;
                int cCount = suffix[minIdx];
                result += map.get(a) * map.get(b) * cCount;
            }
        }
        return result;
    }
    static int binarySearch(int require) {
        int start = 0;
        int end = arr.length - 1;
        int minIdx = -1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if (arr[mid] >= require) {
                minIdx = mid;
                end = mid - 1;
            } else start = mid + 1;
        }
        return minIdx;
    }
}