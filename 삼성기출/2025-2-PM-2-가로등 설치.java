// 3시간 50
// 이중 TreeMap으로 우선순위큐 만들었다.
// 그런데 내부 TreeMap의 key를 왼쪽 가로등의 위치로 했어야 하는데 가로등의 인덱스로 해버려서 거리가 같은게 여러개 일 때 오답이 나왔다.
/*
1. TreeMap
   - Map.Entry<Integer, Integer> entry = map.firstEntry();
   - 비어 있을 때 firstEntry(), pollFirstEntry()는 null 반환
   - firstKey()는 비어 있으면 예외 발생
   - new TreeMap<>(Comparator.reverseOrder()) : key 내림차순
   - 중첩 TreeMap을 사용하면 바깥쪽과 안쪽의 정렬 기준을 따로 설정 가능

   - Map 인터페이스에는 firstEntry(), pollFirstEntry()가 없음
   - 해당 메서드를 사용하려면 TreeMap 타입으로 선언해야 함
   - Map<Integer, Integer> map = new TreeMap<>(); 처럼 선언하면 map.firstEntry() 호출 불가

   - value가 null인 경우와 key 자체가 없는 경우를 구분하려면 containsKey() 사용
   - int 같은 기본 자료형은 null이 될 수 없으므로 null 비교 시 주의

   - list.get(index).pos = 10; 리스트 안에 있는 실제 객체의 필드가 변경됨
   - int와 double을 함께 사용하면 결과 타입이 double이 될 수 있음
   - 양의 정수의 나눗셈 올림: (a + b - 1) / b
   
10. TreeMap의 지연 삭제(Lazy Deletion)
    - 삭제된 원소를 자료구조에서 즉시 모두 제거하지 않고, 실제로 사용할 때 유효한 원소인지 검사하는 방식
    - 예: list.get(key).isAlive && list.get(value).isAlive -> 죽은 가로등이 포함된 구간은 건너뛰거나 제거
*/

import java.io.*;
import java.util.*;

public class Main {
    static class Light {
        boolean isAlive = true;
        int pos;
        int prev;
        int next;

        Light(int pos, int prev, int next) {
            this.pos = pos;
            this.prev = prev;
            this.next = next;
        }
    }
    static List<Light> list = new ArrayList<>();
    static TreeMap<Integer, TreeMap<Integer,Integer>> map = new TreeMap<>(Comparator.reverseOrder());

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        StringBuilder sb = new StringBuilder();
        list.add(new Light(0, 0, 0));
        list.get(0).isAlive = false;
        int Q = Integer.parseInt(br.readLine().trim());
        int N = 0, M = 0;
        int firstPos = 0;
        int lastPos = 0;
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine());
            int task = st.nextToken().charAt(0) - '0';
            if (task == 1) {
                N = Integer.parseInt(st.nextToken());
                M = Integer.parseInt(st.nextToken());
                for (int j = 1; j <= M; j++) {
                    int L = Integer.parseInt(st.nextToken());
                    if (j < M) {
                        list.add(new Light(L, j - 1, j + 1));
                    } else {
                        list.add(new Light(L, j - 1, 0));
                    }
                    if (j > 1) {
                        addDistance(j-1, j);
                    }
                    if (j == 1) firstPos = L;
                    if (j == M) lastPos = L;
                }
            }
            else if (task == 2) {
                int prev = 0;
                int next = 0;
                while (!map.isEmpty()) {
                    int distance = map.firstEntry().getKey();
                    TreeMap<Integer, Integer> innerMap = map.firstEntry().getValue();
                    while(!innerMap.isEmpty()) {
                        Map.Entry<Integer, Integer> entry = innerMap.pollFirstEntry();
                        int prevPos = entry.getKey();
                        int prevIdx = entry.getValue();
                        Light prevLight = list.get(prevIdx);
                        Light nextLight = list.get(prevLight.next);
                        if (prevLight.isAlive && nextLight.isAlive && (distance == nextLight.pos - prevPos)) {
                            prev = prevIdx;
                            next = prevLight.next;
                            break;
                        }
                    }
                    if (prev == 0) {
                        map.pollFirstEntry();
                    } else {
                        break;
                    }
                }
                M++;
                int newPos = (list.get(prev).pos + list.get(next).pos + 1) / 2;
                list.add(new Light(newPos, prev, next));
                list.get(prev).next = M;
                list.get(next).prev = M;
                addDistance(prev, M);
                addDistance(M, next);
            }
            else if (task == 3) {
                int D = Integer.parseInt(st.nextToken());
                Light target = list.get(D);
                target.isAlive = false;
                int prev = target.prev;
                int next = target.next;
                list.get(prev).next = next;
                list.get(next).prev = prev;
                addDistance(prev, next);
                if (target.prev == 0) firstPos = list.get(next).pos;
                if (target.next == 0) lastPos = list.get(prev).pos;
            }
            else if (task == 4) {
                int prev = 0;
                int next = 0;
                while (!map.isEmpty()) {
                    int distance = map.firstEntry().getKey();
                    TreeMap<Integer, Integer> innerMap = map.firstEntry().getValue();
                    while(!innerMap.isEmpty()) {
                        Map.Entry<Integer, Integer> entry = innerMap.firstEntry();
                        int prevPos = entry.getKey();
                        int prevIdx = entry.getValue();
                        Light prevLight = list.get(prevIdx);
                        Light nextLight = list.get(prevLight.next);
                        if (prevLight.isAlive && nextLight.isAlive && (distance == nextLight.pos - prevPos)) {
                            prev = prevIdx;
                            next = prevLight.next;
                            break;
                        } else {
                            innerMap.pollFirstEntry();
                        }
                    }
                    if (prev == 0) {
                        map.pollFirstEntry();
                    } else {
                        break;
                    }
                }
                int betweenDistance = list.get(next).pos - list.get(prev).pos;
                int leftDistance = firstPos - 1;
                int rightDistance = N - lastPos;
                //sb.append(betweenDistance).append(' ').append(leftDistance).append(' ').append(rightDistance).append('\n');
                int r = Math.max(leftDistance * 2, Math.max(rightDistance * 2, betweenDistance));
                sb.append(r).append('\n');
            }
        }
        System.out.println(sb);
    }
    static void addDistance(int prev, int next) {
        int prevPos = list.get(prev).pos;
        int distance = list.get(next).pos - prevPos;
        
        TreeMap<Integer, Integer> innerMap = map.get(distance);
        if (innerMap == null) {
            innerMap = new TreeMap<>();
            innerMap.put(prevPos, prev);
            map.put(distance, innerMap);
        } else {
            innerMap.put(prevPos, prev);
        }
    }
}
