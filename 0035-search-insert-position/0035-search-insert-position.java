class Solution {
    public int searchInsert(int[] nums, int target) {
        int first=0, last=nums.length-1;
        while(first<=last){
            int mid = (first+last)/2;
            if(nums[mid]==target)
                return mid;
            else if(target<nums[mid])
                last=mid-1;
            else
                first=mid+1;
        }
        return first;
    }
}