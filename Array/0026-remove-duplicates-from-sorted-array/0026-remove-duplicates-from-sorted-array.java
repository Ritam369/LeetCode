class Solution {
    public int removeDuplicates(int[] nums) {
        // 2-pointer method
        int i,j;
        for(i=0,j=1; j<nums.length;j++){
            if(nums[i]!=nums[j]){
                i++;
                nums[i]=nums[j];
            }
        }
        return i+1;
    }
}