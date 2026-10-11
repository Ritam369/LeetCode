class Solution {
    public void reverse(int[] nums, int start, int end){
        for(;start<=end;start++,end--){
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
        }
    }
    public void rotate(int[] nums, int k) {
        int turn = k % nums.length;
        reverse(nums, 0, nums.length-1);
        reverse(nums, 0, turn-1);
        reverse(nums, turn, nums.length-1);
    }
}