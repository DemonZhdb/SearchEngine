package app.util.stemmer;

import java.lang.reflect.Method;

public class Among {
    public Among(String s, int substringI, int result) {
        this.s = s.toCharArray();
        this.substringI = substringI;
        this.result = result;
        this.method = null;
    }

    public Among(String s, int substringI, int result, String methodname,
                 Class<? extends SnowballProgram> programclass) {
        this.s = s.toCharArray();
        this.substringI = substringI;
        this.result = result;
        try {
            this.method = programclass.getDeclaredMethod(methodname);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public final char[] s; /* search string */
    public final int substringI; /* index to longest matching substring */
    public final int result; /* result of the lookup */
    public final Method method; /* method to use if substring matches */
};
