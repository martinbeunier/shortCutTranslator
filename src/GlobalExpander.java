import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
        import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class GlobalExpander implements NativeKeyListener {

    private static final int MAX = 6;
    private final Map<String, String> spaced  = mapMap();   // 3znakové kódy – spustí se po mezeře
    private final Map<String, String> instant = mapMap2();  // 4znakové kódy – spustí se hned
    private final StringBuilder buf = new StringBuilder();
    private volatile boolean busy = false;
    private final Robot robot;
    private final boolean mac = System.getProperty("os.name").toLowerCase().contains("mac");

    public GlobalExpander() throws Exception {
        robot = new Robot();
        robot.setAutoDelay(5);
    }

    public static void main(String[] args) throws Exception {

        int mode = 0;

        switch(mode){
            case 0 :
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalExpander());
            break;
            case 1 :
                HashMap<String, String> alphabetToSymbolsMap = new HashMap<>();
                alphabetToSymbolsMap = GlobalExpander.mapMap();

                HashMap<String, String> alphabetToOtherAlphabetMap  = new HashMap<>();
                alphabetToOtherAlphabetMap = GlobalExpander.mapMap2();

                //String text = "( nnn a aaa a ) qqq 0";

                //String text = "*mm-*rr-*gg-*ff-";
                //String text = "lim x-> +++ , n**2- / n = +++ ";
                //String text = "NNN ( NNN A∧ NNN B)∨ NNN NNN ¬C";

                //String text = "5 + 9 === 6 ";
                //String text = "+++ === 6 ";
                //String text = "f(x) >>> 5";

                //String text = "123";
                //String text = "1234";

                //String text = "*jo-*gg-*uu-*rr-*tt-ffsa";
                //String text = "*jo-*gg-*uu-*rr-*tt-";

                //String text = "*zz-*dd-*rr-*aa-*ss-*tt-*vv-*uu-*jj- Petře +++";
                //String text = "+OM-";

                //String text = "Y0 = {(p,q) : ?E? R ,q = 0}";
                //String text = "A === 0 , B === 0 , C === 0";

                //String text = "SSS i=1 n ";
                //String text = "n**2-+5n**3-";


//String text = ("Vyšlo mi <f1,f2> = 2 ,  <f2,f3> = 2 ,  <f3,f4> = 2 ,    <f1,f4> = 2 ,  ||f1|| = SQR 4 ,  ||f2|| = SQR 2,  ||f3|| = SQR 3,  ||f4|| = SQR 2   ")  ;

                // String text = ("F1 = (2/ SQR 2 ,2/ SQR 2 ,0 ,0 )  "        )  ;

                String text = ("+ll-");

                text = translateAlphabetToSymbols(text,alphabetToSymbolsMap );
                //System.out.println(text);
                text = translateAlphabetToOtherAlphabet(text,alphabetToOtherAlphabetMap); //bug - maže počateční znak

                //  text = spaceCanceler(text);

                System.out.println(text);
                break;

        }


      //  System.out.println("Běží. Ukončení: Ctrl+C v konzoli.");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
       // System.out.println("pressed: code=" + e.getKeyCode() + " text=" + NativeKeyEvent.getKeyText(e.getKeyCode()));
        if (busy) return;
        switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_BACKSPACE -> { if (buf.length() > 0) buf.setLength(buf.length() - 1); }
            case NativeKeyEvent.VC_ENTER, NativeKeyEvent.VC_TAB, NativeKeyEvent.VC_ESCAPE,
                 NativeKeyEvent.VC_LEFT, NativeKeyEvent.VC_RIGHT,
                 NativeKeyEvent.VC_UP, NativeKeyEvent.VC_DOWN -> buf.setLength(0);
        }
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
        if (busy) return;
        char c = e.getKeyChar();
        System.out.println("typed: [" + c + "] buf=" + buf);   // až PO řádku s char c
        if (c == NativeKeyEvent.CHAR_UNDEFINED || Character.isISOControl(c)) return;
        if (c == ' ') {
            int idx = buf.lastIndexOf(" ");
            String token = buf.substring(idx + 1);
            boolean truncated = idx < 0 && buf.length() >= MAX;
            buf.setLength(0);
            if (!truncated && spaced.containsKey(token)) {
                replace(token.length() + 1, spaced.get(token), true);
                return;
            }
            buf.append(' ');
            return;
        }

        buf.append(c);
        if (buf.length() > MAX) buf.delete(0, buf.length() - MAX);

        if (buf.length() >= 4) {
            String last4 = buf.substring(buf.length() - 4);
            if (instant.containsKey(last4)) {
                buf.setLength(0);
                replace(4, instant.get(last4), false);
            }
        }
    }

    @Override public void nativeKeyReleased(NativeKeyEvent e) {}

    /** Smaže napsaný kód, vloží symbol přes schránku (Robot neumí psát unicode) a případně vrátí mezeru. */
    private void replace(int backspaces, String symbol, boolean retypeSpace) {
        busy = true;
        new Thread(() -> {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable old = null;
            try {
                old = cb.getContents(null);
                Thread.sleep(30);
                for (int i = 0; i < backspaces; i++) tap(KeyEvent.VK_BACK_SPACE);

                cb.setContents(new StringSelection(symbol), null);
                int mod = mac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL;
                robot.keyPress(mod); robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V); robot.keyRelease(mod);
                Thread.sleep(80);

                if (retypeSpace) tap(KeyEvent.VK_SPACE);
                Thread.sleep(50);
                if (old != null) cb.setContents(old, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                buf.setLength(0);
                busy = false;
            }
        }).start();
    }

    private void tap(int key) { robot.keyPress(key); robot.keyRelease(key); }


    public static HashMap<String, String> mapMap() {
        HashMap<String, String> alphabetToSymbolsMap = new HashMap<>();

        //capslock
        //logic operators
        alphabetToSymbolsMap.put("AAA", "∧");
        alphabetToSymbolsMap.put("OOO", "∨");
        alphabetToSymbolsMap.put("NNN", "¬");
        alphabetToSymbolsMap.put("III", "⇒");
        alphabetToSymbolsMap.put("QQQ", "≡");
        alphabetToSymbolsMap.put("XXX", "⊕");
        alphabetToSymbolsMap.put("A??", "∀");
        alphabetToSymbolsMap.put("E??", "∃");

        //množiny
        alphabetToSymbolsMap.put("?A?", "∩");
        alphabetToSymbolsMap.put("?O?", "∪");
        alphabetToSymbolsMap.put("?V?", "⊆");  // podmnožina
        alphabetToSymbolsMap.put("?V!", "⊇");
        alphabetToSymbolsMap.put("?P?", "⊂");  //vlastní podmnožina
        alphabetToSymbolsMap.put("?P!", "⊃");
        alphabetToSymbolsMap.put("?D?", "∁"); //doplněk
        alphabetToSymbolsMap.put("?E?", "∈");
        alphabetToSymbolsMap.put("?N?", "∉");


        //hodnoty a relační operátory + znaky podobné =
        alphabetToSymbolsMap.put("+++", "∞");
        alphabetToSymbolsMap.put("---", "∅");
        alphabetToSymbolsMap.put("===", "≠");
        alphabetToSymbolsMap.put(">>>", "≥");
        alphabetToSymbolsMap.put("<<<", "≤");

        alphabetToSymbolsMap.put("==-", "≈");
        alphabetToSymbolsMap.put("=--", "∼");
        // alphabetToSymbolsMap.put("<<<", "≤");
        // alphabetToSymbolsMap.put("<<<", "≤");

        //matematické znaky
        alphabetToSymbolsMap.put("?0?", "°");
        alphabetToSymbolsMap.put("+O-", "±");
        alphabetToSymbolsMap.put("SQR", "√");
        alphabetToSymbolsMap.put("ITG", "∫"); //YYY
        alphabetToSymbolsMap.put("SSS", "∑");
        alphabetToSymbolsMap.put("PPP", "∏");

        alphabetToSymbolsMap.put("DDD", "Δ"); //možná duplicita s řeckou abecedou
        alphabetToSymbolsMap.put("ddd", "δ"); //možná duplicita s řeckou abecedou

        //číselné množiny
        alphabetToSymbolsMap.put("+NN", "ℕ");
        alphabetToSymbolsMap.put("+ZZ", "ℤ");
        alphabetToSymbolsMap.put("+QQ", "ℚ");
        alphabetToSymbolsMap.put("+RR", "ℝ");
        alphabetToSymbolsMap.put("+CC", "ℂ");

        //šipky
        alphabetToSymbolsMap.put("-WW", "↑");
        alphabetToSymbolsMap.put("-WD", "↗");
        alphabetToSymbolsMap.put("-DD", "→");
        alphabetToSymbolsMap.put("-SD", "↘");
        alphabetToSymbolsMap.put("-SS", "↓");
        alphabetToSymbolsMap.put("-SA", "↙");
        alphabetToSymbolsMap.put("-AA", "←");
        alphabetToSymbolsMap.put("-WA", "↖");
        alphabetToSymbolsMap.put("-AD", "⇄");

//non caps lock

        //logic operators
        alphabetToSymbolsMap.put("aaa", "∧");
        alphabetToSymbolsMap.put("ooo", "∨");
        alphabetToSymbolsMap.put("nnn", "¬");
        alphabetToSymbolsMap.put("iii", "⇒");
        alphabetToSymbolsMap.put("qqq", "≡");
        alphabetToSymbolsMap.put("xxx", "⊕");
        alphabetToSymbolsMap.put("a??", "∀");
        alphabetToSymbolsMap.put("e??", "∃");

//množiny
        alphabetToSymbolsMap.put("?a?", "∩");
        alphabetToSymbolsMap.put("?o?", "∪");
        alphabetToSymbolsMap.put("?v?", "⊆");  // podmnožina
        alphabetToSymbolsMap.put("?v!", "⊇");
        alphabetToSymbolsMap.put("?p?", "⊂");  // vlastní podmnožina
        alphabetToSymbolsMap.put("?p!", "⊃");
        alphabetToSymbolsMap.put("?d?", "∁");  // doplněk
        alphabetToSymbolsMap.put("?e?", "∈");
        alphabetToSymbolsMap.put("?n?", "∉");

//hodnoty a relační operátory + znaky podobné =
        alphabetToSymbolsMap.put("+++", "∞");
        alphabetToSymbolsMap.put("---", "∅");
        alphabetToSymbolsMap.put("===", "≠");
        alphabetToSymbolsMap.put(">>>", "≥");
        alphabetToSymbolsMap.put("<<<", "≤");

        alphabetToSymbolsMap.put("==-", "≈");
        alphabetToSymbolsMap.put("=--", "∼");

//matematické znaky
        alphabetToSymbolsMap.put("?0?", "°");
        alphabetToSymbolsMap.put("+o-", "±");
        alphabetToSymbolsMap.put("sqr", "√");
        alphabetToSymbolsMap.put("itg", "∫");
        alphabetToSymbolsMap.put("sss", "∑");
        alphabetToSymbolsMap.put("ppp", "∏");

        alphabetToSymbolsMap.put("dlt", "Δ");  // změněno, "ddd" by kolidovalo s δ
        alphabetToSymbolsMap.put("ddd", "δ");

//číselné množiny
        alphabetToSymbolsMap.put("+nn", "ℕ");
        alphabetToSymbolsMap.put("+zz", "ℤ");
        alphabetToSymbolsMap.put("+qq", "ℚ");
        alphabetToSymbolsMap.put("+rr", "ℝ");
        alphabetToSymbolsMap.put("+cc", "ℂ");

//šipky
        alphabetToSymbolsMap.put("-ww", "↑");
        alphabetToSymbolsMap.put("-wd", "↗");
        alphabetToSymbolsMap.put("-dd", "→");
        alphabetToSymbolsMap.put("-sd", "↘");
        alphabetToSymbolsMap.put("-ss", "↓");
        alphabetToSymbolsMap.put("-sa", "↙");
        alphabetToSymbolsMap.put("-aa", "←");
        alphabetToSymbolsMap.put("-wa", "↖");
        alphabetToSymbolsMap.put("-ad", "⇄");

        return alphabetToSymbolsMap;
    }

    public static HashMap<String, String> mapMap2() {
        HashMap<String, String> alphabetToOtherAlphabetMap = new HashMap<>();

        //azbuka
        //velka verze
        alphabetToOtherAlphabetMap.put("*AA-", "А");
        alphabetToOtherAlphabetMap.put("*BB-", "Б");
        alphabetToOtherAlphabetMap.put("*VV-", "В");
        alphabetToOtherAlphabetMap.put("*GG-", "Г");
        alphabetToOtherAlphabetMap.put("*DD-", "Д");
        alphabetToOtherAlphabetMap.put("*JE-", "Е");
        alphabetToOtherAlphabetMap.put("*JO-", "Ё");
        alphabetToOtherAlphabetMap.put("*ŽŽ-", "Ж");
        alphabetToOtherAlphabetMap.put("*ZZ-", "З");

        alphabetToOtherAlphabetMap.put("*II-", "И");
        alphabetToOtherAlphabetMap.put("*JJ-", "Й");
        alphabetToOtherAlphabetMap.put("*KK-", "К");
        alphabetToOtherAlphabetMap.put("*LL-", "Л");
        alphabetToOtherAlphabetMap.put("*MM-", "М");
        alphabetToOtherAlphabetMap.put("*NN-", "Н");
        alphabetToOtherAlphabetMap.put("*OO-", "О");
        alphabetToOtherAlphabetMap.put("*PP-", "П");

        alphabetToOtherAlphabetMap.put("*RR-", "Р");
        alphabetToOtherAlphabetMap.put("*SS-", "С");
        alphabetToOtherAlphabetMap.put("*TT-", "Т");
        alphabetToOtherAlphabetMap.put("*UU-", "У");
        alphabetToOtherAlphabetMap.put("*FF-", "Ф");
        alphabetToOtherAlphabetMap.put("*CH-", "Х");
        alphabetToOtherAlphabetMap.put("*CC-", "Ц");
        alphabetToOtherAlphabetMap.put("*ČČ-", "Ч");

        alphabetToOtherAlphabetMap.put("*ŠŠ-", "Ш");
        alphabetToOtherAlphabetMap.put("*ŠČ-", "Щ");
        alphabetToOtherAlphabetMap.put("*TZ-", "Ъ");
        alphabetToOtherAlphabetMap.put("*YY-", "Ы");
        alphabetToOtherAlphabetMap.put("*MZ-", "Ь");
        alphabetToOtherAlphabetMap.put("*EE-", "Э");
        alphabetToOtherAlphabetMap.put("*JU-", "Ю");
        alphabetToOtherAlphabetMap.put("*JA-", "Я");

// mala verze (opravená)
        alphabetToOtherAlphabetMap.put("*aa-", "а");
        alphabetToOtherAlphabetMap.put("*bb-", "б");
        alphabetToOtherAlphabetMap.put("*vv-", "в");
        alphabetToOtherAlphabetMap.put("*gg-", "г");
        alphabetToOtherAlphabetMap.put("*dd-", "д");
        alphabetToOtherAlphabetMap.put("*je-", "е");
        alphabetToOtherAlphabetMap.put("*jo-", "ё");
        alphabetToOtherAlphabetMap.put("*žž-", "ж");
        alphabetToOtherAlphabetMap.put("*zz-", "з");

        alphabetToOtherAlphabetMap.put("*ii-", "и");
        alphabetToOtherAlphabetMap.put("*jj-", "й");
        alphabetToOtherAlphabetMap.put("*kk-", "к");
        alphabetToOtherAlphabetMap.put("*ll-", "л");
        alphabetToOtherAlphabetMap.put("*mm-", "м");
        alphabetToOtherAlphabetMap.put("*nn-", "н");
        alphabetToOtherAlphabetMap.put("*oo-", "о");
        alphabetToOtherAlphabetMap.put("*pp-", "п");

        alphabetToOtherAlphabetMap.put("*rr-", "р");
        alphabetToOtherAlphabetMap.put("*ss-", "с");
        alphabetToOtherAlphabetMap.put("*tt-", "т");
        alphabetToOtherAlphabetMap.put("*uu-", "у");
        alphabetToOtherAlphabetMap.put("*ff-", "ф");
        alphabetToOtherAlphabetMap.put("*ch-", "х");
        alphabetToOtherAlphabetMap.put("*cc-", "ц");
        alphabetToOtherAlphabetMap.put("*čč-", "ч");

        alphabetToOtherAlphabetMap.put("*šš-", "ш");
        alphabetToOtherAlphabetMap.put("*šč-", "щ");
        alphabetToOtherAlphabetMap.put("*tz-", "ъ");
        alphabetToOtherAlphabetMap.put("*yy-", "ы");
        alphabetToOtherAlphabetMap.put("*mz-", "ь");
        alphabetToOtherAlphabetMap.put("*ee-", "э");
        alphabetToOtherAlphabetMap.put("*ju-", "ю");
        alphabetToOtherAlphabetMap.put("*ja-", "я");

// řecká abeceda (malá)
        alphabetToOtherAlphabetMap.put("+aa-", "α");
        alphabetToOtherAlphabetMap.put("+bb-", "β");
        alphabetToOtherAlphabetMap.put("+gg-", "γ");
        alphabetToOtherAlphabetMap.put("+dd-", "δ");
        alphabetToOtherAlphabetMap.put("+ee-", "ε");
        alphabetToOtherAlphabetMap.put("+zz-", "ζ");
        alphabetToOtherAlphabetMap.put("+et-", "η");
        alphabetToOtherAlphabetMap.put("+th-", "θ");
        alphabetToOtherAlphabetMap.put("+io-", "ι");
        alphabetToOtherAlphabetMap.put("+kk-", "κ");

        alphabetToOtherAlphabetMap.put("+ll-", "λ");
        alphabetToOtherAlphabetMap.put("+mm-", "μ");
        alphabetToOtherAlphabetMap.put("+nn-", "ν");
        alphabetToOtherAlphabetMap.put("+xx-", "ξ");
        alphabetToOtherAlphabetMap.put("+oo-", "ο");
        alphabetToOtherAlphabetMap.put("+pp-", "π");
        alphabetToOtherAlphabetMap.put("+rr-", "ρ");
        alphabetToOtherAlphabetMap.put("+ss-", "σ");
        alphabetToOtherAlphabetMap.put("+sf-", "ς");   // koncové sigma (final)
        alphabetToOtherAlphabetMap.put("+tt-", "τ");
        alphabetToOtherAlphabetMap.put("+uu-", "υ");

        alphabetToOtherAlphabetMap.put("+ph-", "φ");
        alphabetToOtherAlphabetMap.put("+ch-", "χ");
        alphabetToOtherAlphabetMap.put("+ps-", "ψ");
        alphabetToOtherAlphabetMap.put("+om-", "ω");

        // řecká abeceda (VELKÁ)
        alphabetToOtherAlphabetMap.put("+AA-", "Α");
        alphabetToOtherAlphabetMap.put("+BB-", "Β");
        alphabetToOtherAlphabetMap.put("+GG-", "Γ");
        alphabetToOtherAlphabetMap.put("+DD-", "Δ");
        alphabetToOtherAlphabetMap.put("+EE-", "Ε");
        alphabetToOtherAlphabetMap.put("+ZZ-", "Ζ");
        alphabetToOtherAlphabetMap.put("+ET-", "Η");
        alphabetToOtherAlphabetMap.put("+TH-", "Θ");
        alphabetToOtherAlphabetMap.put("+IO-", "Ι");
        alphabetToOtherAlphabetMap.put("+KK-", "Κ");

        alphabetToOtherAlphabetMap.put("+LL-", "Λ");
        alphabetToOtherAlphabetMap.put("+MM-", "Μ");
        alphabetToOtherAlphabetMap.put("+NN-", "Ν");
        alphabetToOtherAlphabetMap.put("+XX-", "Ξ");
        alphabetToOtherAlphabetMap.put("+OO-", "Ο");
        alphabetToOtherAlphabetMap.put("+PP-", "Π");
        alphabetToOtherAlphabetMap.put("+RR-", "Ρ");
        alphabetToOtherAlphabetMap.put("+SS-", "Σ");
        alphabetToOtherAlphabetMap.put("+TT-", "Τ");
        alphabetToOtherAlphabetMap.put("+UU-", "Υ");

        alphabetToOtherAlphabetMap.put("+PH-", "Φ");
        alphabetToOtherAlphabetMap.put("+CH-", "Χ");
        alphabetToOtherAlphabetMap.put("+PS-", "Ψ");
        alphabetToOtherAlphabetMap.put("+OM-", "Ω");

        //německé znaky malé + další
        alphabetToOtherAlphabetMap.put("/aa-", "ä");
        alphabetToOtherAlphabetMap.put("/oo-", "ö");
        alphabetToOtherAlphabetMap.put("/uu-", "ü");
        alphabetToOtherAlphabetMap.put("/bb-", "ß");
        alphabetToOtherAlphabetMap.put("/ae-", "æ");
        alphabetToOtherAlphabetMap.put("/oe-", "œ");

        //německé znaky velké + další
        alphabetToOtherAlphabetMap.put("/AA-", "Ä");
        alphabetToOtherAlphabetMap.put("/OO-", "Ö");
        alphabetToOtherAlphabetMap.put("/UU-", "Ü");
        alphabetToOtherAlphabetMap.put("/BB-", "ẞ");
        alphabetToOtherAlphabetMap.put("/AE-", "Æ");
        alphabetToOtherAlphabetMap.put("/OE-", "Œ");

        //mocniny čísla
        alphabetToOtherAlphabetMap.put("**0-", "⁰");
        alphabetToOtherAlphabetMap.put("**1-", "¹");
        alphabetToOtherAlphabetMap.put("**2-", "²");
        alphabetToOtherAlphabetMap.put("**3-", "³");
        alphabetToOtherAlphabetMap.put("**4-", "⁴");
        alphabetToOtherAlphabetMap.put("**5-", "⁵");
        alphabetToOtherAlphabetMap.put("**6-", "⁶");
        alphabetToOtherAlphabetMap.put("**7-", "⁷");
        alphabetToOtherAlphabetMap.put("**8-", "⁸");
        alphabetToOtherAlphabetMap.put("**9-", "⁹");

        //mocniny malá abeceda
        alphabetToOtherAlphabetMap.put("**a-", "ᵃ");
        alphabetToOtherAlphabetMap.put("**b-", "ᵇ");
        alphabetToOtherAlphabetMap.put("**c-", "ᶜ");
        alphabetToOtherAlphabetMap.put("**d-", "ᵈ");
        alphabetToOtherAlphabetMap.put("**e-", "ᵉ");
        alphabetToOtherAlphabetMap.put("**f-", "ᶠ");
        alphabetToOtherAlphabetMap.put("**g-", "ᵍ");
        alphabetToOtherAlphabetMap.put("**h-", "ʰ");
        alphabetToOtherAlphabetMap.put("**i-", "ⁱ");
        alphabetToOtherAlphabetMap.put("**j-", "ʲ");
        alphabetToOtherAlphabetMap.put("**k-", "ᵏ");
        alphabetToOtherAlphabetMap.put("**l-", "ˡ");
        alphabetToOtherAlphabetMap.put("**m-", "ᵐ");
        alphabetToOtherAlphabetMap.put("**n-", "ⁿ");
        alphabetToOtherAlphabetMap.put("**o-", "ᵒ");
        alphabetToOtherAlphabetMap.put("**p-", "ᵖ");
        alphabetToOtherAlphabetMap.put("**q-", "q");
        alphabetToOtherAlphabetMap.put("**r-", "ʳ");
        alphabetToOtherAlphabetMap.put("**s-", "ˢ");
        alphabetToOtherAlphabetMap.put("**t-", "ᵗ");
        alphabetToOtherAlphabetMap.put("**u-", "ᵘ");
        alphabetToOtherAlphabetMap.put("**v-", "ᵛ");
        alphabetToOtherAlphabetMap.put("**w-", "ʷ");
        alphabetToOtherAlphabetMap.put("**x-", "ˣ");
        alphabetToOtherAlphabetMap.put("**y-", "ʸ");
        alphabetToOtherAlphabetMap.put("**z-", "ᶻ");

        //mocniny velká abeceda
        alphabetToOtherAlphabetMap.put("**A-", "ᴬ");
        alphabetToOtherAlphabetMap.put("**B-", "ᴮ");
        alphabetToOtherAlphabetMap.put("**C-", "ᶜ");
        alphabetToOtherAlphabetMap.put("**D-", "ᴰ");
        alphabetToOtherAlphabetMap.put("**E-", "ᴱ");
        alphabetToOtherAlphabetMap.put("**F-", "ᶠ");
        alphabetToOtherAlphabetMap.put("**G-", "ᴳ");
        alphabetToOtherAlphabetMap.put("**H-", "ᴴ");
        alphabetToOtherAlphabetMap.put("**I-", "ᴵ");
        alphabetToOtherAlphabetMap.put("**J-", "ᴶ");
        alphabetToOtherAlphabetMap.put("**K-", "ᴷ");
        alphabetToOtherAlphabetMap.put("**L-", "ᴸ");
        alphabetToOtherAlphabetMap.put("**M-", "ᴹ");
        alphabetToOtherAlphabetMap.put("**N-", "ᴺ");
        alphabetToOtherAlphabetMap.put("**O-", "ᴼ");
        alphabetToOtherAlphabetMap.put("**P-", "ᴾ");
        alphabetToOtherAlphabetMap.put("**Q-", "Q");
        alphabetToOtherAlphabetMap.put("**R-", "ᴿ");
        alphabetToOtherAlphabetMap.put("**S-", "ˢ");
        alphabetToOtherAlphabetMap.put("**T-", "ᵀ");
        alphabetToOtherAlphabetMap.put("**U-", "ᵁ");
        alphabetToOtherAlphabetMap.put("**V-", "ⱽ");
        alphabetToOtherAlphabetMap.put("**W-", "ᵂ");
        alphabetToOtherAlphabetMap.put("**X-", "ˣ");
        alphabetToOtherAlphabetMap.put("**Y-", "ʸ");
        alphabetToOtherAlphabetMap.put("**Z-", "ᶻ");





        //přidat do mocnin +-*/

        //možná
        //indexy
        // tabulka pro pleby - @ {}[]  & # $ | \
        // custom operace + v kroužku atd.
        // možná přidat : ∘ • ⇒ ⊗ ⊕


        return alphabetToOtherAlphabetMap;
    }
    public static String spaceCanceler(String text){
        return text.replace(" ","");
    }

    public static String translateAlphabetToSymbols(String input, HashMap<String, String> alphabetToSymbolsMap) {
        String output = "";

        String[] parts = input.split(" ");
        for (String part : parts)
        {
            // System.out.println(part);

            if (alphabetToSymbolsMap.containsKey(part))
            {
                output = output + alphabetToSymbolsMap.get(part);
            }else
            {
                output = output + part;
            }

            output = output + " ";
        }

        output = output.substring(0,output.length()-1);
        return output;
    }

    public static String translateAlphabetToOtherAlphabet(String input, HashMap<String, String> alphabetToSymbolsMap) {
        String output = "";
        int counter = 0;

        ArrayList<String> parts = new ArrayList<>();
        if (input.length() > 3) { //osetri delku vstupu

            for (int i = 0; i < input.length() - 3; i++)  //rozdeli text na casti dlouhe 4 znaky
            {
                parts.add(input.substring(i, i + 4));
                //System.out.println(parts.get(i));
            }

            for (String part : parts) //prevadi casti na symboly
            {
                // System.out.println(part);

                if (alphabetToSymbolsMap.containsKey(part)) {
                    output = output + alphabetToSymbolsMap.get(part);
                    counter = 3;
                } else {
                    if (counter < 0) //kdyz neni cast symbol ,pripise cast bez symbolu .
                    {
                        output = output + part.substring(0, 1);
                        counter = 0;
                    }

                }
                counter--;
            }

            if (!alphabetToSymbolsMap.containsKey(parts.get(0))) { //zajisti aby na zacatku nechybel znak
                // System.out.println(parts.get(0));
                // System.out.println(input.substring(0,1));
                output = input.substring(0, 1) + output;

            }

            if (!alphabetToSymbolsMap.containsKey(parts.getLast())) { // zajisti aby na konci nechybel znak
                //System.out.println(counter );
                output = output + parts.getLast().substring(2 + counter, 4);
                //System.out.println(parts.getLast());
            }

            return output;

        } else
        {
            return input;
        }
    }
}


