class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>(); // map--> key,value pair (here Integer -> Wrapper Class)

      for(int i=0; i<nums.length;i++){
          int complement = target - nums[i];
          if(map.containsKey(complement)){
              return new int[] {map.get(complement), i}; // get() method retrieves the value associated with a given key.
          }
          map.put(nums[i], i); // put() method adds a key-value pair to the map or updates the value if that key already exists.
      }

      return new int[] {};
    }
}