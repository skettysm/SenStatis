/****
 * 
 * This file deals with the continuous and dynamic statistical calculation of the sample sensor datas within the user input interval
 * 
 */


package app.android.senstatis;
public class MovingAverage
{
  private float circularBuffer[];
  private float avg;
  private int circularIndex;
  private int beginindex;
  private int endindex;
  private float sum;
  private int count;
  private static int counter;
  public MovingAverage (int k)
  {
	  
	  circularBuffer= new float[k];
	  count=0;
	  circularIndex=0;
	  beginindex=0;
	  endindex=0;
	  avg=0;
	  sum=0.0f;
	  counter=0;
	  
  }
  
  public void setBeginindex(int begin)
  { beginindex=begin;}
  
  
  public void setEndindex(int end)
  { endindex=end;}
  
  public void getStatus()
  {
	//  System.out.println("beginindex="+beginindex+"endindex="+endindex);
	  }
  
  
  
  
  
  
  public float getValue()
  {
	  if(endindex>=beginindex)
		  count=endindex-beginindex;
	  else count=circularBuffer.length-(beginindex-endindex);
	  
	  avg=sum/count;
	  
	  return avg;
	  }
  
  public float getDevi()
  { float sumintermediate=0;
    
	  for(int i=beginindex;i<endindex;i++)
		  
	    sumintermediate+=(circularBuffer[i]-avg)*(circularBuffer[i]-avg);
	  
	  
	//System.out.println("avg="+avg+"beginindex="+beginindex+"endindex="+endindex+"counter="+count); 
	return (float) Math.sqrt(sumintermediate/count);
  }
  
  public void afterwards()
  {
	  endindex=beginindex;
	  sum=0;
	//  counter=0;
	  
  }
  
  
  
  
  public void pushValue(float x)
  {
	 // System.out.println("haha input into the buffer");
	  if(endindex+1>circularBuffer.length)
		  endindex=0;
	  circularBuffer[endindex]=x;
	 // System.out.println("value="+circularBuffer[endindex]);
	 endindex++;
	 //System.out.println("endindex="+endindex);
	 sum=sum+x;
	 counter++;
	 
	  
	  
  }
  
  public int getCount()
  {System.out.println("test");
	  
	  return count;}
  
  
  private void primeBuffer(float val)
  {
	  
	  for (int i=0; i<circularBuffer.length; ++i)
	  {
		  circularBuffer[i]=val;}
		  avg =val;
		  
	  }
	  
	  private int nextIndex(int curIndex)
	  {
		  if (curIndex +1 >= circularBuffer.length)
		  {return 0;
		  }
		  
		  return curIndex+1;
		  
		  
	  }
  }
  
