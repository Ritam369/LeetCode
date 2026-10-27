class Solution {
    public boolean isPalindrome(int x) {
        int num = x;
        int reverse = 0;
        while(num>0){
            int digit = num % 10;
            reverse = reverse*10 + digit;
            num/=10;
        }
        if(x>=0 && x==reverse)
            return true;
        else
            return false;
    }
}