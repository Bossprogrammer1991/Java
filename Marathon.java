/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gravitycalculator;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package marathon;

/**
 *
 * @author Eric
 */
class Marathon {
    public static int fastestTime(int[] times)
    {
        int minIndex = 0;
        for(int i = 0; i < times.length; i++)
        {
            if(times[i] < times[minIndex])
            {
                minIndex = i;
            }
        }
        return minIndex;
    }
    public static void main (String[] arguments) {
        String[] names = {
            "Elena", "Thomas", "Hamilton", "Suzie", "Phil", "Matt", "Alex",
            "Emma", "John", "James", "Jane", "Emily", "Daniel", "Neda",
            "Aaron", "Kate"
        };

        int[] times = {
            341, 273, 278, 329, 445, 402, 388, 275, 243, 334, 412, 393, 299,
            343, 317, 265
        };
        int minIndex;
        minIndex = fastestTime(times);
        System.out.println(names[minIndex] + ": " + times[minIndex]);
    }
}