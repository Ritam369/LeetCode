class Solution {
    public boolean arrayStringsAreEqual(String[] word1, String[] word2) {
        int r1=0, c1=0, r2=0, c2=0;
        while(r1<word1.length && r2<word2.length){
            if(word1[r1].charAt(c1) != word2[r2].charAt(c2))
                return false;
            c1++;
            c2++;

            if(c1==word1[r1].length()){
                r1++;
                c1=0;
            }
            if(c2==word2[r2].length()){
                r2++;
                c2=0;
            }
        }
        return r1==word1.length && r2==word2.length;
    }
}