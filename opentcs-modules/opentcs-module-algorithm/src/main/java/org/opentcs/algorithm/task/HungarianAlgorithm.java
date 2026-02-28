package org.opentcs.algorithm.task;

/**
 * 匈牙利算法 - 用于任务分配
 */
public class HungarianAlgorithm {

    /**
     * 解决分配问题
     * @param costMatrix 成本矩阵，其中costMatrix[i][j]表示第i个工人完成第j个任务的成本
     * @return 分配结果，result[i]表示第i个工人分配到的任务索引
     */
    public int[] solve(int[][] costMatrix) {
        int n = costMatrix.length;
        int m = costMatrix[0].length;
        int[] u = new int[n + 1];
        int[] v = new int[m + 1];
        int[] p = new int[m + 1];
        int[] way = new int[m + 1];

        for (int i = 1; i <= n; i++) {
            p[0] = i;
            int[] minv = new int[m + 1];
            for (int j = 1; j <= m; j++) {
                minv[j] = costMatrix[i - 1][j - 1];
            }
            boolean[] used = new boolean[m + 1];
            int j0 = 0;
            do {
                used[j0] = true;
                int i0 = p[j0];
                int delta = Integer.MAX_VALUE;
                int j1 = 0;
                for (int j = 1; j <= m; j++) {
                    if (!used[j]) {
                        int cur = costMatrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }
                for (int j = 0; j <= m; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }

        int[] result = new int[n];
        for (int j = 1; j <= m; j++) {
            if (p[j] != 0) {
                result[p[j] - 1] = j - 1;
            }
        }
        return result;
    }

    /**
     * 计算分配的总成本
     * @param costMatrix 成本矩阵
     * @param assignment 分配结果
     * @return 总成本
     */
    public int calculateCost(int[][] costMatrix, int[] assignment) {
        int totalCost = 0;
        for (int i = 0; i < assignment.length; i++) {
            if (assignment[i] != -1) {
                totalCost += costMatrix[i][assignment[i]];
            }
        }
        return totalCost;
    }
}
