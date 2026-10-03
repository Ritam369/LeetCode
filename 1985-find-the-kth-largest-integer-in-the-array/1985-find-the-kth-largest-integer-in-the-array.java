class Solution {
    public String kthLargestNumber(String[] nums, int k) {
        //We are going to use min-heap where the smallest elements gets added on the top over the larger elements
        //Top-Bottom = smallest-largest
        //the size of the min-heap will be of size k (given)
        //When the size of the heap exceeds the topmost element will be deleted
        //At the end, the answer will be the topmost element of the heap
        PriorityQueue<String> minHeap = new PriorityQueue<>(
            (a, b) -> {
                if (a.length() != b.length()) {
                    return a.length() - b.length();
                }

                return a.compareTo(b); //jodi same length na hoyy tahole lexicographically comparison
            }
        );
        // As a PriorityQueue<String> compares strings lexicographically (dictionary order), not by their numerical value.
        // Comparator Rule for a.length() - b.length()
        // negative → a comes before b
        // 0 → a and b are considered equal
        // positive → b comes before a
        for(String num : nums){
            minHeap.add(num);
            if(minHeap.size() > k){
                minHeap.poll(); //deleteing the topmost element
            }
        } 
        return minHeap.peek();//peek() - topmost element
    }
}