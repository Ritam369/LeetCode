class Solution {
    public boolean checkIfPangram(String sentence) {
        boolean array[] = new boolean[26];
        int count = 0;
        for(char ch: sentence.toCharArray()){
            int index = ch - 'a';
            if(!array[index]){
                array[index]=true;
                count++;
                if(count==26) return true;
            }
        }
        return count==26;
    }
}