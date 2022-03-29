
public class Utils {

    /**
     * Function that returns the nth number of Fibonacci series.
     * @param n - number of element to compute
     * @return - nth element of Fibonacci series
     */
    static int fibo(int n) {
        int aux[] = new int[n + 2];

        aux[0] = 0;
        aux[1] = 1;
        for (int i = 2; i <= n; i++) {
            int newValue = aux[i - 2] + aux[i - 1];
            aux[i] = newValue;
        }

        return aux[n];
    }

    /**
     * Function that checks if a character is special or not
     * @param ch - character on which we perform the check
     * @return - true or false whether the character is special or not.
     */
    static boolean isSpecialCharacter(char ch) {
        return !(Character.isDigit(ch) || Character.isLetter(ch));
    }
}
