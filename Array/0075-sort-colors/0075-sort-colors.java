class Solution {
    public void sortColors(int[] nums) {
        // nums: array of integers where 0=red, 1=white, 2=blue
        // Sort the array in-place to be in the order of red, white, and blue

        // Dutch National Flag Algorithm - 3-pointer approach
        // 0=red, 1=white, 2=blue
        // 3 pointers low, mid & high
        // low fight for 0 ; mid fight for 1 ; high fight for 2

        int low=0, mid=0, high=nums.length-1;
        while(mid<=high){
            if(nums[mid]==1){
                mid++;
            }
            else if(nums[mid]==0){
                int temp = nums[mid];
                nums[mid] = nums[low];
                nums[low] = temp;

                low++;
                mid++;
            }
            else{
                int temp = nums[mid];
                nums[mid] = nums[high];
                nums[high] = temp;
                
                high--; // As 2 will be at end so high starts from the end and count will be reversely 
            }
        }
    }
}