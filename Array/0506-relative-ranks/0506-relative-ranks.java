class Solution {
    public String[] findRelativeRanks(int[] score) {
        int n = score.length;
        Integer[] indices = new Integer[n]; 
        //We are going to sort based on the indices not the score;
        //as we need to keep the order intact
        //we can determine each athlete's rank without losing the original order.

        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, (a, b) -> Integer.compare(score[b], score[a])); //sorting indices in descending order using custom comparataor
        String[] answer = new String[n];
        
        for (int i = 0; i < n; i++) {
            int index = indices[i];

            if (i == 0) {
                answer[index] = "Gold Medal";
            } else if (i == 1) {
                answer[index] = "Silver Medal";
            } else if (i == 2) {
                answer[index] = "Bronze Medal";
            } else {
                answer[index] = String.valueOf(i + 1);
            }
        }

        return answer;
    }
}