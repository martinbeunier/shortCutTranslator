import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {



        HashMap<String, String> alphabetToSymbolsMap = new HashMap<>();
        alphabetToSymbolsMap =mapMap();

        HashMap<String, String> alphabetToOtherAlphabetMap  = new HashMap<>();
        alphabetToOtherAlphabetMap =mapMap2();

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
    public static HashMap<String, String> mapMap() {
        HashMap<String, String> alphabetToSymbolsMap = new HashMap<>();

        //logic operators
        alphabetToSymbolsMap.put("AAA", "∧");
        alphabetToSymbolsMap.put("OOO", "∨");
        alphabetToSymbolsMap.put("NNN", "¬");
        alphabetToSymbolsMap.put("III", "⊃");
        alphabetToSymbolsMap.put("QQQ", "≡");
        alphabetToSymbolsMap.put("XXX", "⊕");
        alphabetToSymbolsMap.put("A??", "∀");
        alphabetToSymbolsMap.put("E??", "∃");

        //množiny
        alphabetToSymbolsMap.put("?A?", "∩");
        alphabetToSymbolsMap.put("?O?", "∪");
        alphabetToSymbolsMap.put("?V?", "⊆"); //vlastní podmnožina
        alphabetToSymbolsMap.put("?V!", "⊇");
        alphabetToSymbolsMap.put("?P?", "⊂"); // podmnožina
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

//verze pro maly text

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
        alphabetToOtherAlphabetMap.put("+OM-", "ß");

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
        alphabetToOtherAlphabetMap.put("**i-", "ᶦ");
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

        //mocniny malá abeceda
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
        alphabetToOtherAlphabetMap.put("**T-", "ᵗ");
        alphabetToOtherAlphabetMap.put("**U-", "ᵘ");
        alphabetToOtherAlphabetMap.put("**V-", "ᵛ");
        alphabetToOtherAlphabetMap.put("**W-", "ʷ");
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


}