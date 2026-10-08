class Solution {
    public int[] intersection(int[] nums1, int[] nums2) {
        // We will use HashSet as it can store unique elements only
        /*
        set1 ← set(nums1)
        result is also set()
        for y in nums2: if y in set1: result.add(y)
        return list(result)
        */

        // Use HashSet to store unique elements from nums1
        Set<Integer> set1 = new HashSet<>();
        for(int num : nums1){
            set1.add(num);
        }

        // Taking another set to store the intersection
        Set<Integer> result = new HashSet<>();
        for(int num: nums2){
            if(set1.contains(num))
                result.add(num);
        }

        // As the return type is int[] so converting result set to int array
        int[] resultArray = new int[result.size()];
        int i = 0;
        for (int num : result) {
            resultArray[i++] = num;
        }
        return resultArray;
    }
}