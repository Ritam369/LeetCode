class Solution {
    public int majorityElement(int[] nums) {
        // Using Boyer-Moore Voting Algorithm

        /*
        majorityElement(nums):
            candidate = None
            count = 0
            for num in nums:
                if count == 0:
                    candidate = num
                    count += 1
                elif num == candidate:
                    count += 1
                else:
                    count -= 1
            return candidate   
        */

        int candidate=nums[0], count=0;
        for(int i=0;i<=nums.length-1;i++){
            if(count==0){
                candidate=nums[i];
                count++;
            }
            else if(nums[i]==candidate)
                count++;
            else
                count--;
        }
        return candidate;
    }
}