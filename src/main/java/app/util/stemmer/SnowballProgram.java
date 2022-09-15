package app.util.stemmer;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

public class SnowballProgram implements Serializable {
    protected SnowballProgram() {
        current = new StringBuilder();
        init();
    }

    static final long serialVersionUID = 2016072500L;

    private void init() {
        cursor = 0;
        limit = current.length();
        limitBackward = 0;
        bra = cursor;
        ket = limit;
    }

    /**
     * Set the current string.
     */
    public void setCurrent(String value) {
        // Make a new StringBuilder.  If we reuse the old one, and a user of
        // the library keeps a reference to the buffer returned (for example,
        // by converting it to a String in a way which doesn't force a copy),
        // the buffer size will not decrease, and we will risk wasting a large
        // amount of memory.
        // Thanks to Wolfram Esser for spotting this problem.
        current = new StringBuilder(value);
        init();
    }

    /**
     * Get the current string.
     */
    public String getCurrent() {
        return current.toString();
    }

    // current string
    protected StringBuilder current;

    protected int cursor;
    protected int limit;
    protected int limitBackward;
    protected int bra;
    protected int ket;

    public SnowballProgram(SnowballProgram other) {
        current = other.current;
        cursor = other.cursor;
        limit = other.limit;
        limitBackward = other.limitBackward;
        bra = other.bra;
        ket = other.ket;
    }

    protected void copy_from(SnowballProgram other) {
        current = other.current;
        cursor = other.cursor;
        limit = other.limit;
        limitBackward = other.limitBackward;
        bra = other.bra;
        ket = other.ket;
    }

    protected boolean in_grouping(char[] s, int min, int max) {
        if (cursor >= limit) return false;
        char ch = current.charAt(cursor);
        if (ch > max || ch < min) return false;
        ch -= min;
        if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) return false;
        cursor++;
        return true;
    }

    protected boolean in_grouping_b(char[] s, int min, int max) {
        if (cursor <= limitBackward) return false;
        char ch = current.charAt(cursor - 1);
        if (ch > max || ch < min) return false;
        ch -= min;
        if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) return false;
        cursor--;
        return true;
    }

    protected boolean out_grouping(char[] s, int min, int max) {
        if (cursor >= limit) return false;
        char ch = current.charAt(cursor);
        if (ch > max || ch < min) {
            cursor++;
            return true;
        }
        ch -= min;
        if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) {
            cursor++;
            return true;
        }
        return false;
    }

    protected boolean out_grouping_b(char[] s, int min, int max) {
        if (cursor <= limitBackward) return false;
        char ch = current.charAt(cursor - 1);
        if (ch > max || ch < min) {
            cursor--;
            return true;
        }
        ch -= min;
        if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) {
            cursor--;
            return true;
        }
        return false;
    }

    protected boolean eq_s(CharSequence s) {
        if (limit - cursor < s.length()) return false;
        int i;
        for (i = 0; i != s.length(); i++) {
            if (current.charAt(cursor + i) != s.charAt(i)) return false;
        }
        cursor += s.length();
        return true;
    }

    protected boolean eq_s_b(CharSequence s) {
        if (cursor - limitBackward < s.length()) return false;
        int i;
        for (i = 0; i != s.length(); i++) {
            if (current.charAt(cursor - s.length() + i) != s.charAt(i)) return false;
        }
        cursor -= s.length();
        return true;
    }

    protected int find_among(Among v[]) {
        int i = 0;
        int j = v.length;

        int c = cursor;
        int l = limit;

        int commonI = 0;
        int commonJ = 0;

        boolean firstKeyInspected = false;

        while (true) {
            int k = i + ((j - i) >> 1);
            int diff = 0;
            int common = commonI < commonJ ? commonI : commonJ; // smaller
            Among w = v[k];
            int i2;
            for (i2 = common; i2 < w.s.length; i2++) {
                if (c + common == l) {
                    diff = -1;
                    break;
                }
                diff = current.charAt(c + common) - w.s[i2];
                if (diff != 0) break;
                common++;
            }
            if (diff < 0) {
                j = k;
                commonJ = common;
            } else {
                i = k;
                commonI = common;
            }
            if (j - i <= 1) {
                if (i > 0) break; // v->s has been inspected
                if (j == i) break; // only one item in v

                // - but now we need to go round once more to get
                // v->s inspected. This looks messy, but is actually
                // the optimal approach.

                if (firstKeyInspected) break;
                firstKeyInspected = true;
            }
        }
        while (true) {
            Among w = v[i];
            if (commonI >= w.s.length) {
                cursor = c + w.s.length;
                if (w.method == null) return w.result;
                boolean res;
                try {
                    Object resobj = w.method.invoke(this);
                    res = resobj.toString().equals("true");
                } catch (InvocationTargetException e) {
                    res = false;
                    // FIXME - debug message
                } catch (IllegalAccessException e) {
                    res = false;

                }
                cursor = c + w.s.length;
                if (res) return w.result;
            }
            i = w.substringI;
            if (i < 0) return 0;
        }
    }

    // find_among_b is for backwards processing. Same comments apply
    protected int find_among_b(Among v[]) {
        int i = 0;
        int j = v.length;

        int c = cursor;
        int lb = limitBackward;

        int commonI = 0;
        int commonJ = 0;

        boolean firstKeyInspected = false;

        while (true) {
            int k = i + ((j - i) >> 1);
            int diff = 0;
            int common = commonI < commonJ ? commonI : commonJ;
            Among w = v[k];
            int i2;
            for (i2 = w.s.length - 1 - common; i2 >= 0; i2--) {
                if (c - common == lb) {
                    diff = -1;
                    break;
                }
                diff = current.charAt(c - 1 - common) - w.s[i2];
                if (diff != 0) break;
                common++;
            }
            if (diff < 0) {
                j = k;
                commonJ = common;
            } else {
                i = k;
                commonI = common;
            }
            if (j - i <= 1) {
                if (i > 0) break;
                if (j == i) break;
                if (firstKeyInspected) break;
                firstKeyInspected = true;
            }
        }
        while (true) {
            Among w = v[i];
            if (commonI >= w.s.length) {
                cursor = c - w.s.length;
                if (w.method == null) return w.result;

                boolean res;
                try {
                    Object resobj = w.method.invoke(this);
                    res = resobj.toString().equals("true");
                } catch (InvocationTargetException e) {
                    res = false;
                    // FIXME - debug message
                } catch (IllegalAccessException e) {
                    res = false;
                    // FIXME - debug message
                }
                cursor = c - w.s.length;
                if (res) return w.result;
            }
            i = w.substringI;
            if (i < 0) return 0;
        }
    }

    /* to replace chars between cBra and cKet in current by the
     * chars in s.
     */
    protected int replace_s(int cBra, int cKet, String s) {
        int adjustment = s.length() - (cKet - cBra);
        current.replace(cBra, cKet, s);
        limit += adjustment;
        if (cursor >= cKet) cursor += adjustment;
        else if (cursor > cBra) cursor = cBra;
        return adjustment;
    }

    protected void slice_check() {
        if (bra < 0 ||
                bra > ket ||
                ket > limit ||
                limit > current.length())   // this line could be removed
        {
            System.err.println("faulty slice operation");
            // FIXME: report error somehow.
	/*
	    fprintf(stderr, "faulty slice operation:\n");
	    debug(z, -1, 0);
	    exit(1);
	    */
        }
    }

    protected void slice_from(String s) {
        slice_check();
        replace_s(bra, ket, s);
    }

    protected void slice_from(CharSequence s) {
        slice_from(s.toString());
    }

    protected void slice_del() {
        slice_from("");
    }

    protected void insert(int cBra, int cKet, String s) {
        int adjustment = replace_s(cBra, cKet, s);
        if (cBra <= bra) bra += adjustment;
        if (cBra <= ket) ket += adjustment;
    }

    protected void insert(int cBra, int cKet, CharSequence s) {
        insert(cBra, cKet, s.toString());
    }

    /* Copy the slice into the supplied StringBuilder */
    protected void slice_to(StringBuilder s) {
        slice_check();
        s.replace(0, s.length(), current.substring(bra, ket));
    }

    protected void assign_to(StringBuilder s) {
        s.replace(0, s.length(), current.substring(0, limit));
    }



};
