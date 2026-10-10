class Solution {
    public void maxCal(int arr[],int maxLeft[],int maxRight[]){
        maxLeft[0]=arr[0];
        maxRight[arr.length-1]=arr[arr.length-1];
        for(int i=1;i<arr.length;i++){
            maxLeft[i]=Math.max(maxLeft[i-1],arr[i]);
        }
        for(int i=arr.length-2;i>=0;i--){
            maxRight[i]=Math.max(maxRight[i+1],arr[i]);
        }
    }
    public int trap(int[] height) {
        int flag1=0,flag2=0;
        if(height.length>2){
            for(int i=0;i<height.length-1;i++){
                if(height[i]<=height[i+1])
                    continue;
                else
                    flag1+=1;
            }
            for(int i=height.length-1;i>0;i--){
                if(height[i-1]>=height[i])
                    continue;
                else
                    flag2+=1;
            }
            if(flag1==0 || flag2==0)
                return 0;

            // Taking help of Auxilary/Helper Arrays
            int maxLeft[]=new int[height.length];
            int maxRight[]=new int[height.length];
            maxCal(height,maxLeft,maxRight);
            int trapped=0;
            for(int i=0;i<height.length;i++){
                trapped+=Math.min(maxLeft[i],maxRight[i])-height[i];
            }
            return trapped;
        }
        else
            return 0;
    }
}