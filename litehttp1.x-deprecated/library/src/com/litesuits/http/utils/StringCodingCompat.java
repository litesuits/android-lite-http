//package com.litesuits.http.utils;
//
//import java.io.UnsupportedEncodingException;
//import java.lang.ref.SoftReference;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.charset.*;
//
///**
// * Utility class for string encoding and decoding.
// */
//public class StringCodingCompat {
//
//    private StringCodingCompat() { }
//
//    /* The cached coders for each thread
//     */
//    private static ThreadLocal decoder = new ThreadLocal();
//    private static ThreadLocal encoder = new ThreadLocal();
//
//    private static boolean warnUnsupportedCharset = true;
//
//    private static Object deref(ThreadLocal tl) {
//        SoftReference sr = (SoftReference)tl.get();
//        if (sr == null)
//            return null;
//        return sr.get();
//    }
//
//    private static void set(ThreadLocal tl, Object ob) {
//        tl.set(new SoftReference(ob));
//    }
//
//    // Trim the given byte array to the given length
//    //
//    public static byte[] safeTrim(byte[] ba, int len, Charset cs) {
//        if (len == ba.length
//                && (System.getSecurityManager() == null
//                || cs.getClass().getClassLoader() == null))
//            return ba;
//        else
//            return copyOf(ba, len);
//    }
//
//    // Trim the given char array to the given length
//    //
//    public static char[] safeTrim(char[] ca, int len, Charset cs) {
//        if (len == ca.length
//                && (System.getSecurityManager() == null
//                || cs.getClass().getClassLoader() == null))
//            return ca;
//        else
//            return copyOf(ca, len);
//    }
//
//    public static int scale(int len, float expansionFactor) {
//        // We need to perform double, not float, arithmetic; otherwise
//        // we lose low order bits when len is larger than 2**24.
//        return (int)(len * (double)expansionFactor);
//    }
//
//    public static Charset lookupCharset(String csn) {
//        if (Charset.isSupported(csn)) {
//            try {
//                return Charset.forName(csn);
//            } catch (UnsupportedCharsetException x) {
//                throw new Error(x);
//            }
//        }
//        return null;
//    }
//
//    public static void warnUnsupportedCharset(String csn) {
//        if (warnUnsupportedCharset) {
//            // Use sun.misc.MessageUtils rather than the Logging API or
//            // System.err since this method may be called during VM
//            // initialization before either is available.
//            //MessageUtils.err("WARNING: Default charset " + csn +
//            //        " not supported, using ISO-8859-1 instead");
//            warnUnsupportedCharset = false;
//        }
//    }
//
//
//    // -- Decoding --
//    private static class StringDecoder {
//        private final String requestedCharsetName;
//        private final Charset cs;
//        private final CharsetDecoder cd;
//
//        private StringDecoder(Charset cs, String rcn) {
//            this.requestedCharsetName = rcn;
//            this.cs = cs;
//            this.cd = cs.newDecoder()
//                    .onMalformedInput(CodingErrorAction.REPLACE)
//                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
//        }
//
//        String charsetName() {
//            //if (cs instanceof HistoricallyNamedCharset)
//            //    return ((HistoricallyNamedCharset)cs).historicalName();
//            return cs.name();
//        }
//
//        final String requestedCharsetName() {
//            return requestedCharsetName;
//        }
//
//        char[] decode(byte[] ba, int off, int len) {
//            int en = scale(len, cd.maxCharsPerByte());
//            char[] ca = new char[en];
//            if (len == 0)
//                return ca;
//            cd.reset();
//            ByteBuffer bb = ByteBuffer.wrap(ba, off, len);
//            CharBuffer cb = CharBuffer.wrap(ca);
//            try {
//                CoderResult cr = cd.decode(bb, cb, true);
//                if (!cr.isUnderflow())
//                    cr.throwException();
//                cr = cd.flush(cb);
//                if (!cr.isUnderflow())
//                    cr.throwException();
//            } catch (CharacterCodingException x) {
//                // Substitution is always enabled,
//                // so this shouldn't happen
//                throw new Error(x);
//            }
//            return safeTrim(ca, cb.position(), cs);
//        }
//
//    }
//
//    public static char[] decode(String charsetName, byte[] ba, int off, int len)
//            throws UnsupportedEncodingException
//    {
//        StringDecoder sd = (StringDecoder)deref(decoder);
//        String csn = (charsetName == null) ? "ISO-8859-1" : charsetName;
//        if ((sd == null) || !(csn.equals(sd.requestedCharsetName())
//                || csn.equals(sd.charsetName()))) {
//            sd = null;
//            try {
//                Charset cs = lookupCharset(csn);
//                if (cs != null)
//                    sd = new StringDecoder(cs, csn);
//            } catch (IllegalCharsetNameException x) {}
//            if (sd == null)
//                throw new UnsupportedEncodingException(csn);
//            set(decoder, sd);
//        }
//        return sd.decode(ba, off, len);
//    }
//
//    public static char[] decode(Charset cs, byte[] ba, int off, int len) {
//        StringDecoder sd = new StringDecoder(cs, cs.name());
//        byte[] b = copyOf(ba, ba.length);
//        return sd.decode(b, off, len);
//    }
//
//    public static char[] decode(byte[] ba, int off, int len) {
//        String csn = Charset.defaultCharset().name();
//        try {
//            return decode(csn, ba, off, len);
//        } catch (UnsupportedEncodingException x) {
//            warnUnsupportedCharset(csn);
//        }
//        try {
//            return decode("ISO-8859-1", ba, off, len);
//        } catch (UnsupportedEncodingException x) {
//            // If this code is hit during VM initialization, MessageUtils is
//            // the only way we will be able to get any kind of error message.
//            //MessageUtils.err("ISO-8859-1 charset not available: "
//            //        + x.toString());
//            // If we can not find ISO-8859-1 (a required encoding) then things
//            // are seriously wrong with the installation.
//            System.exit(1);
//            return null;
//        }
//    }
//
//
//
//
//    // -- Encoding --
//    private static class StringEncoder {
//        private Charset cs;
//        private CharsetEncoder ce;
//        private final String requestedCharsetName;
//
//        private StringEncoder(Charset cs, String rcn) {
//            this.requestedCharsetName = rcn;
//            this.cs = cs;
//            this.ce = cs.newEncoder()
//                    .onMalformedInput(CodingErrorAction.REPLACE)
//                    .onUnmappableCharacter(CodingErrorAction.REPLACE);
//        }
//
//        String charsetName() {
//            //if (cs instanceof HistoricallyNamedCharset)
//            //    return ((HistoricallyNamedCharset)cs).historicalName();
//            return cs.name();
//        }
//
//        final String requestedCharsetName() {
//            return requestedCharsetName;
//        }
//
//        byte[] encode(char[] ca, int off, int len) {
//            int en = scale(len, ce.maxBytesPerChar());
//            byte[] ba = new byte[en];
//            if (len == 0)
//                return ba;
//
//            ce.reset();
//            ByteBuffer bb = ByteBuffer.wrap(ba);
//            CharBuffer cb = CharBuffer.wrap(ca, off, len);
//            try {
//                CoderResult cr = ce.encode(cb, bb, true);
//                if (!cr.isUnderflow())
//                    cr.throwException();
//                cr = ce.flush(bb);
//                if (!cr.isUnderflow())
//                    cr.throwException();
//            } catch (CharacterCodingException x) {
//                // Substitution is always enabled,
//                // so this shouldn't happen
//                throw new Error(x);
//            }
//            return safeTrim(ba, bb.position(), cs);
//        }
//    }
//
//    public static byte[] encode(String charsetName, char[] ca, int off, int len)
//            throws UnsupportedEncodingException
//    {
//        StringEncoder se = (StringEncoder)deref(encoder);
//        String csn = (charsetName == null) ? "ISO-8859-1" : charsetName;
//        if ((se == null) || !(csn.equals(se.requestedCharsetName())
//                || csn.equals(se.charsetName()))) {
//            se = null;
//            try {
//                Charset cs = lookupCharset(csn);
//                if (cs != null)
//                    se = new StringEncoder(cs, csn);
//            } catch (IllegalCharsetNameException x) {}
//            if (se == null)
//                throw new UnsupportedEncodingException (csn);
//            set(encoder, se);
//        }
//        return se.encode(ca, off, len);
//    }
//
//    public static byte[] encode(Charset cs, char[] ca, int off, int len) {
//        StringEncoder se = new StringEncoder(cs, cs.name());
//        char[] c = copyOf(ca, ca.length);
//        return se.encode(c, off, len);
//    }
//
//    public static byte[] encode(char[] ca, int off, int len) {
//        String csn = Charset.defaultCharset().name();
//        try {
//            return encode(csn, ca, off, len);
//        } catch (UnsupportedEncodingException x) {
//            warnUnsupportedCharset(csn);
//        }
//        try {
//            return encode("ISO-8859-1", ca, off, len);
//        } catch (UnsupportedEncodingException x) {
//            // If this code is hit during VM initialization, MessageUtils is
//            // the only way we will be able to get any kind of error message.
//
//            //MessageUtils.err("ISO-8859-1 charset not available: "
//            //        + x.toString());
//
//            // If we can not find ISO-8859-1 (a required encoding) then things
//            // are seriously wrong with the installation.
//            System.exit(1);
//            return null;
//        }
//    }
//
//    public static char[] copyOf(char[] original, int newLength) {
//        if (newLength < 0) {
//            throw new NegativeArraySizeException(Integer.toString(newLength));
//        }
//        return copyOfRange(original, 0, newLength);
//    }
//
//    public static byte[] copyOf(byte[] original, int newLength) {
//        if (newLength < 0) {
//            throw new NegativeArraySizeException(Integer.toString(newLength));
//        }
//        return copyOfRange(original, 0, newLength);
//    }
//
//    public static byte[] copyOfRange(byte[] original, int start, int end) {
//        if (start > end) {
//            throw new IllegalArgumentException();
//        }
//        int originalLength = original.length;
//        if (start < 0 || start > originalLength) {
//            throw new ArrayIndexOutOfBoundsException();
//        }
//        int resultLength = end - start;
//        int copyLength = Math.min(resultLength, originalLength - start);
//        byte[] result = new byte[resultLength];
//        System.arraycopy(original, start, result, 0, copyLength);
//        return result;
//    }
//
//    public static char[] copyOfRange(char[] original, int start, int end) {
//        if (start > end) {
//            throw new IllegalArgumentException();
//        }
//        int originalLength = original.length;
//        if (start < 0 || start > originalLength) {
//            throw new ArrayIndexOutOfBoundsException();
//        }
//        int resultLength = end - start;
//        int copyLength = Math.min(resultLength, originalLength - start);
//        char[] result = new char[resultLength];
//        System.arraycopy(original, start, result, 0, copyLength);
//        return result;
//    }
//}
