package com.hanteo.hanteoproject.service;

public class Problem2 {

    public static void main(String[] args) {

        int[] coins = {2,5,3,6};
        int sum = 10;

        int result = coinChange(coins,sum);

        System.out.println(result == 5);
    }

    public static int coinChange(int[] coins, int sum) {
        int[] dp = new int[sum + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int i = coin; i <= sum; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[sum];
    }


}




